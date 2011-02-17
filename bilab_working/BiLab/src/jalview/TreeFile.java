package jalview;

import java.io.*;
import java.awt.*;
import java.util.*;

public class TreeFile extends FileParse implements OutputGenerator {
  Font f;
  int fontSize = 8;
  SequenceNode top;

  int line = 0;
  int ch = 0;

  float ycount = 0;
  float maxheight;

  SequenceNode maxdist = null;
  boolean showDistances = true;

 // Output properties
  MailProperties mp;
  FileProperties fp;
  PostscriptProperties pp;

  int offx = 20;
  int offy = 20;

  Object found = null;

  Vector selected = null;
  Vector groups = new Vector();

  BufferedWriter bw;
  PrintStream ps;
  boolean makeString = false;
  StringBuffer out;

  public TreeFile(SequenceNode sn, Vector selected) {
    this(sn);
    this.selected = selected;
  }
  public TreeFile(SequenceNode sn) {
    top = sn;
    propertiesInit();

  }
  public TreeFile(String inStr) {
    readLines(inStr);
    parse();
    propertiesInit();
  }

  public TreeFile(String inFile, String type)  throws IOException {
    
      super(inFile,type);
      System.out.print("Reading file....");
      long tstart = System.currentTimeMillis();
      readLines();
      long tend = System.currentTimeMillis();
      System.out.println("done");
      System.out.println("Total time taken = " + (tend-tstart) + "ms");
      
      System.out.println("Parsing file....");
      parse();
      System.out.println("Parsing file....");
  }

  public void propertiesInit() {
    this.mp = new MailProperties();
    mp.server = "circinus.ebi.ac.uk";
    this.pp = new PostscriptProperties();
    this.fp = new FileProperties();
    
  }

  public void parse() {
    if (!((String)lineArray.elementAt(0)).equals("(")) {
      System.out.println("Invalid tree file: must start with (");
      top = null;
    } else {
      top = new SequenceNode();
      SequenceNode tmp = top;
      createTree(tmp,getChar());
      System.out.println("Done creating tree " + top);
    }
  }

  public void setFontSize(int size) {
      this.fontSize = size;
      if (f != null) {
	this.f = new Font(f.getName(),f.getStyle(),size);
      } else {
	this.f = new Font("Helvetica",Font.PLAIN,size);
      }
  }

  public void setFont(Font f) {
    this.f = f;
  }

  public String getChar() {
    if (ch >= ((String)lineArray.elementAt(line)).length()) {
      line++;
      ch = 0;
    }

    String str = ((String)lineArray.elementAt(line)).substring(ch,ch+1);
    ch++;
    return str;    
  }

  public String createTree(BinaryNode node,String str) {
    String name = "";
    String dist = "";

    if (str.equals("(")) {
      // New node
      System.out.println("New left node on " + node);
      SequenceNode sn = new SequenceNode();
      sn.parent = node;
      node.left = sn;
      str = createTree(sn,getChar());
      
      if (str.equals(",")) {
	// Now do the right side     
	System.out.println("New right node on " + node);
	SequenceNode sn2 = new SequenceNode();
	sn2.parent = node;
	node.right = sn2;
	str = createTree(sn2,getChar());

	// This means an unrooted tree - adding a parent to the top node
	if (str.equals(",")) {
	 System.out.println("Here " + node.parent);
	 System.out.println(((SequenceNode)node).dist + " " + ((SequenceNode)node.right).dist);
	 System.out.println(((SequenceNode)sn2).dist);
	 //New node for top
	 SequenceNode sn3 = new SequenceNode();
	 top = sn3;
	 sn3.left = node;
	 node.parent = sn3;
	 
	 // dummy distance?
	 ((SequenceNode)node).dist = (float).001;
	 SequenceNode sn4 = new SequenceNode();
	 sn4.parent = sn3;
	 sn3.right = sn4;
	 createTree(sn4,getChar());
	}

	// Tot up the height
	SequenceNode l = (SequenceNode)node.left;
	SequenceNode r = (SequenceNode)node.right;

	((SequenceNode)node).count = l.count + r.count;
	((SequenceNode)node).ycount = (l.ycount + r.ycount)/2;
	str = getChar();
      }
    } else {
      // Leaf node
      while (!(str.equals(":") || str.equals(",") ||str.equals(")"))) {
	name = name + str;
	str = getChar();
      }
      //      System.out.println("Name = " + name);
      node.setElement(new Sequence(name,"",0,0));
      ((SequenceNode)node).count = 1;
      ((SequenceNode)node).ycount = ycount++;
    }
    
    // Read distance
    if (!str.equals(";")) {
    while (!str.equals(":")) {
      str = getChar();
    }
    str=getChar();
      while (!(str.equals(":") || str.equals(",") ||str.equals(")"))) {
	dist = dist + str;
	str = getChar();
      }
      
      if (node instanceof SequenceNode) {
	((SequenceNode)node).dist  = Float.valueOf(dist).floatValue();
	System.out.println("Distance = " + ((SequenceNode)node).dist);
	
	
	if ( maxdist == null || Float.valueOf(dist).floatValue() > maxdist.dist) {
	  maxdist = (SequenceNode)node;
      }
	
	}
    }
    return str;
  }
  public float max(float l, float r) {
    if (l > r) {return l;} else {return r;}
  }
  public void printNode(SequenceNode node) {

    if (node == null) {return;}
    if (node.left == null && node.right == null) {
      System.out.println("Leaf = " + ((Sequence)node.element()).name);
      System.out.println("Dist " + ((SequenceNode)node).dist);
    } else {
      System.out.println("Dist " + ((SequenceNode)node).dist);
      printNode((SequenceNode)node.left);
      printNode((SequenceNode)node.right);
    }
  
  }
  public void  groupNodes(SequenceNode node, float threshold) {
    if (node == null) {return;}

    if (node.height/maxheight > threshold) {
      groups.addElement(node);
    } else {
      groupNodes((SequenceNode)node.left,threshold);
      groupNodes((SequenceNode)node.right,threshold);
    }
  }

  public Object findElement(Canvas c,int x, int y) {

    int count = (int)(   (float)(y-offy + 5)/(float)(c.size().height - 2*offy) * (float)((SequenceNode)top).count   );
    if (x > c.size().width*0.8) {
      found = null;
      Object ob =  findLeaf(top,count);
      //      System.out.println("Found " + ob);
      return ob;
    } else {
      return null;
    }

  }

  public Vector findLeaves(SequenceNode node, Vector leaves) {
    if (node == null) {return leaves;}

    if (node.left == null && node.right == null) {
      leaves.addElement(node);
      return leaves;
    } else {
      findLeaves((SequenceNode)node.left,leaves);
      findLeaves((SequenceNode)node.right,leaves);
    }
    return leaves;
  }
      
  public Object findLeaf(SequenceNode node,int count) {
    if (node == null) {return found;}
    //    System.out.println(node.ycount);
    if (node.ycount == count) {
      // System.out.println("Returning count ");
      found = node.element;
      return found;
    } else {
      findLeaf((SequenceNode)node.left,count);
      findLeaf((SequenceNode)node.right,count);
    }
    return found;
  }

  public void setColor(SequenceNode node, Color c) {
    if (node == null) {return;}

    if (node.left == null && node.right == null) {
      node.color = c;
      if (node instanceof SequenceNode) {
	((DrawableSequence)node.element).setColor(c);
      }
    } else {
      node.color = c;
      setColor((SequenceNode)node.left,c);
      setColor((SequenceNode)node.right,c);
    }
  }
  public float findHeight(SequenceNode node) {

    if (node == null) {return maxheight;}

    if (node.left == null && node.right == null) {
      node.height = ((SequenceNode)node.parent).height + node.dist;
      if (node.height > maxheight) {
	return node.height;
      } else {
	return maxheight;
      }
    } else {
      if (node.parent != null) {
	node.height = ((SequenceNode)node.parent).height + node.dist;
      } else {
	maxheight = 0;
	node.height = (float)0.0;
      }
      //     System.out.println("Height = " + node.height);
      maxheight = findHeight((SequenceNode)(node.left));
      maxheight = findHeight((SequenceNode)(node.right));
    }
    return maxheight;
  }


  public void draw(Graphics g, int width, int height) {
    if (f != null) {
      g.setFont(f);
    } else {
      g.setFont(new Font("Helvetica",Font.PLAIN,fontSize));
    }
    

    float wscale = (float)(width*.8-offx*2)/maxheight;
    if (((SequenceNode)top).count == 0) {
      ((SequenceNode)top).count = ((SequenceNode)top.left).count + ((SequenceNode)top.right).count ;
    }
    float chunk = (float)(height-offy*2)/((SequenceNode)top).count;
    
    drawNode(g,top,chunk,wscale,width,offx,offy);
  }

  public void drawPostscript(PostscriptProperties pp) {
    try {
      int width = 0;
      int height = 0;

      printout("%!\n");
      
      printout("/" + pp.font + " findfont\n");
      printout(pp.fsize + " scalefont setfont\n");
      
      int offx = pp.xoffset;
      int offy = pp.yoffset;

      if (pp.orientation == PostscriptProperties.PORTRAIT) {
	width = PostscriptProperties.SHORTSIDE;
	height = PostscriptProperties.LONGSIDE;
      } else {
	height = PostscriptProperties.SHORTSIDE;
	width = PostscriptProperties.LONGSIDE;
	printout(height + " 0 translate\n90 rotate\n");
      }
      float wscale = (float)(width*.8-offx*2)/maxheight;
      if (((SequenceNode)top).count == 0) {
	((SequenceNode)top).count = ((SequenceNode)top.left).count + ((SequenceNode)top.right).count ;
      }

      float chunk = (float)(height-offy*2)/(((SequenceNode)top).count+1);
      
      drawPostscriptNode(top,chunk,wscale,width,offx,offy);

      printout("showpage\n");
    } catch (java.io.IOException e) {
      System.out.println("Exception " + e);
    }
  }

  public void changeDirection(SequenceNode node, SequenceNode dir) {
    if (node == null) {return;}
    if (node.parent != top) {
      changeDirection((SequenceNode)node.parent, node);

      SequenceNode tmp = (SequenceNode)node.parent;
    
      if (dir == node.left) {
	node.parent = dir;
	node.left = tmp;
      } else if (dir == node.right) {
	node.parent = dir;
	node.right = tmp;
      }

    } else {
      if (dir == node.left) {
	node.parent = node.left;

	if (top.left == node) {
	  node.right = top.right;
	} else {
	  node.right = top.left;
	}
      } else {
	node.parent = node.right;

	if (top.left == node) {
	  node.left = top.right;
	} else {
	  node.left = top.left;
	}
      }
    }
  }


  public SequenceNode reRoot() {
    
    if (maxdist != null) {
      ycount = 0;
      float tmpdist = maxdist.dist;
      //      System.out.println("Max distance = " + maxdist.dist + " " + maxdist.element());

      // New top
      SequenceNode sn = new SequenceNode();  
      sn.parent = null;

      // New right hand of top
      SequenceNode snr = (SequenceNode)maxdist.parent;
      changeDirection(snr,maxdist);
      System.out.println("Printing reversed tree");
      printN(snr);
      snr.dist = tmpdist/2;
      maxdist.dist = tmpdist/2;

      snr.parent = sn;
      maxdist.parent = sn;

      sn.right = snr;
      sn.left = maxdist;

      top = sn;

      ycount = 0;
      reCount(top);
      findHeight(top);
      
    }

    return top;
  }

  public void drawPostscriptNode(SequenceNode node, float chunk, float scale, int width, int offx, int offy) throws java.io.IOException{
    if (node == null) {return;}

    if (node.left == null && node.right == null) {
      // Drawing leaf node

      float height = node.height;
      float dist = node.dist;

      int xstart = (int)((height-dist)*scale) + offx;
      int xend =   (int)(height*scale) + offx;

      int ypos = (int)(node.ycount * chunk) + offy;

      // g.setColor(Color.black);
      printout("\n" + new Format("%5.3f").form((double)node.color.getRed()/255) + " " +
	       new Format("%5.3f").form((double)node.color.getGreen()/255) + " " +
	       new Format("%5.3f").form((double)node.color.getBlue()/255) + " setrgbcolor\n");

      // Draw horizontal line
      //  g.drawLine(xstart,ypos,xend,ypos);
      printout(xstart + " " + ypos + " moveto " + xend + " " + ypos + " lineto stroke \n");

      if (showDistances && node.dist > 0) {
	//	g.drawString(new Format("%5.2f").form(node.dist),xstart,ypos - 5);
	printout("(" + new Format("%5.2f").form(node.dist) + ") " + xstart + " " + (ypos+5) + " moveto show\n");
      }
      //g.drawString((String)node.element(),xend+20,ypos);
	printout("(" + (((Sequence)node.element()).name) + ") " + (xend+20) + " " + (ypos) + " moveto show\n");
    } else {
      drawPostscriptNode((SequenceNode)node.left,chunk,scale,width,offx,offy);
      drawPostscriptNode((SequenceNode)node.right,chunk,scale,width,offx,offy);
      

      float height = node.height;
      float dist = node.dist;
      
      int xstart = (int)((height-dist)*scale) + offx;
      int xend =   (int)(height*scale) + offx;
      int ypos = (int)(node.ycount * chunk) + offy;

      printout("\n" + new Format("%5.3f").form((double)node.color.getRed()/255) + " " +
	       new Format("%5.3f").form((double)node.color.getGreen()/255) + " " +
	       new Format("%5.3f").form((double)node.color.getBlue()/255) + " setrgbcolor\n");
      //      g.setColor(Color.black);
      //      bw.append("\nblack setrgbcolor\n");
      // Draw horizontal line
      //      g.drawLine(xstart,ypos,xend,ypos);
      printout(xstart + " " + ypos + " moveto " + xend + " " + ypos + " lineto stroke\n");
      int ystart = (int)(((SequenceNode)node.left).ycount * chunk) + offy;
      int yend = (int)(((SequenceNode)node.right).ycount * chunk) + offy;

      //      g.drawLine((int)(height*scale) + offx, ystart,
      //		 (int)(height*scale) + offx, yend);
      printout
(((int)(height*scale) + offx) + " " + ystart + " moveto " +  ((int)(height*scale) + offx) + " " +
	       yend + " lineto stroke\n");
      if (showDistances && node.dist > 0) {
	//	g.drawString(new Format("%5.2f").form(node.dist),xstart,ypos - 5);
	printout("(" +new Format("%5.2f").form(node.dist) + ") " + (xstart) + " " + (ypos+5) + " moveto show\n");
      }
    }
  }
  public void printout(String s) throws IOException {
    if (bw != null) {
      bw.write(s);
    } 
    if (ps != null) {
      ps.print(s);
    } 
    if (makeString == true) {
      out.append(s);
    }
      
    
  }
  public void drawNode(Graphics g,SequenceNode node, float chunk, float scale, int width,int offx, int offy) {
    if (node == null) {return;}

    if (node.left == null && node.right == null) {
      // Drawing leaf node

      float height = node.height;
      float dist = node.dist;

      int xstart = (int)((height-dist)*scale) + offx;
      int xend =   (int)(height*scale) + offx;

      int ypos = (int)(node.ycount * chunk) + offy;

      g.setColor(((SequenceNode)node).color);

      // Draw horizontal line
      g.drawLine(xstart,ypos,xend,ypos);

      if (showDistances && node.dist > 0) {
	g.drawString(new Format("%5.2f").form(node.dist),xstart,ypos - 5);
      }

      // Colour selected leaves differently
      if (selected != null && selected.contains((Sequence)node.element())) {
	g.setColor(Color.gray);
	FontMetrics fm = g.getFontMetrics(f);
	int charWidth = fm.stringWidth(((Sequence)node.element()).name) + 3;
	int charHeight = fm.getHeight();
	
	g.fillRect(xend + 20, ypos - charHeight + 3,charWidth,charHeight);
	g.setColor(Color.white);
      }
      g.drawString(((Sequence)node.element()).name,xend+20,ypos);
      g.setColor(Color.black);
    } else {
      drawNode(g,(SequenceNode)node.left,chunk,scale,width,offx,offy);
      drawNode(g,(SequenceNode)node.right,chunk,scale,width,offx,offy);
      

      float height = node.height;
      float dist = node.dist;
      
      int xstart = (int)((height-dist)*scale) + offx;
      int xend =   (int)(height*scale) + offx;
      int ypos = (int)(node.ycount * chunk) + offy;

      g.setColor(((SequenceNode)node).color);

      // Draw horizontal line
      g.drawLine(xstart,ypos,xend,ypos);

      int ystart = (int)(((SequenceNode)node.left).ycount * chunk) + offy;
      int yend = (int)(((SequenceNode)node.right).ycount * chunk) + offy;

      g.drawLine((int)(height*scale) + offx, ystart,
		 (int)(height*scale) + offx, yend);
      if (showDistances && node.dist > 0) {
	g.drawString(new Format("%5.2f").form(node.dist),xstart,ypos - 5);
      }
    }
  }
    
   
  public static void printN(SequenceNode node) {
    if (node == null) {return;}

    if (node.left != null && node.right != null) {
      printN((SequenceNode)node.left);
      printN((SequenceNode)node.right);
    } else {
      System.out.println(" name = " + ((Sequence)node.element()).name);
    }
    System.out.println(" dist = " + ((SequenceNode)node).dist + " " + ((SequenceNode)node).count + " " + ((SequenceNode)node).height);
  }


  public void reCount(SequenceNode node) {
    if (node == null) {return;}

    if (node.left != null && node.right != null) {
      reCount((SequenceNode)node.left);
      reCount((SequenceNode)node.right);

      SequenceNode l = (SequenceNode)node.left;
      SequenceNode r = (SequenceNode)node.right;

      ((SequenceNode)node).count = l.count + r.count;
      ((SequenceNode)node).ycount = (l.ycount + r.ycount)/2;
   
    } else {
      ((SequenceNode)node).count = 1;
      ((SequenceNode)node).ycount = ycount++;
      //      System.out.println(node.element() + " " + ycount);
    }
  }

  public static void main(String[] args) {
    try {

      

      TreeFile tf = new TreeFile(args[0],"File");
      tf.top.height = (float)0.0;
      tf.maxheight = -1;
      tf.findHeight(tf.top);
      //      System.out.println("MAx height " + tf.maxheight);
      System.out.println("*************Done***********");
      //tf.printNode(tf.top);
      //System.out.println("*************Done***********");
      //tf.printN(tf.top);

      Frame f = new Frame();
      f.setLayout(new BorderLayout());
      Panel p = new Panel();
      p.setLayout(new BorderLayout());
      TreeCanvas mc = new TreeCanvas(tf);
      p.add("Center",mc);

      f.resize(600,600);
      f.add("Center",p);
   
      f.show();

      //      tf.draw(p.getGraphics(),500,500);
      //   tf.drawPostscript(sw,new PostscriptProperties());
      //      System.out.println(sw.toString());
      //      tf.groupNodes(tf.top,Float.valueOf(args[1]).floatValue());
    } catch (java.io.IOException e) {
      System.out.println("Exception " + e);

    }
  }
  public MailProperties getMailProperties() {return mp;}
  public PostscriptProperties getPostscriptProperties(){return pp;}
  public FileProperties getFileProperties(){return fp;}

  public void setMailProperties(MailProperties mp){this.mp = mp;}
  public void setPostscriptProperties(PostscriptProperties pp){this.pp = pp;}
  public void setFileProperties(FileProperties fp){this.fp = fp;}

  public String getText(String format) {
    return null;
  }
  public void getPostscript(BufferedWriter bw) {
    this.bw = bw;
    drawPostscript(pp);

  }
  public void getPostscript(PrintStream bw) {
    this.ps = bw;
    drawPostscript(pp);
    bw.flush();
  }
  
  public StringBuffer getPostscript() {
    makeString = true;
    out = new StringBuffer();
    drawPostscript(pp);
    return out;
  }

}
class TreeCanvas extends Canvas {
  TreeFile tf;

  public TreeCanvas(TreeFile tf) {
    super();
    this.tf = tf;
  }


  public void paint(Graphics g) {
    if (size() != null) {
      tf.draw(g,size().width,size().height);
    } else {
      tf.draw(g,500,500);
    }
  }

}
  

      
  









