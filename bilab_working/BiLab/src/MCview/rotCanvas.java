package MCview;

import java.awt.*;

import java.util.*;

import java.io.*;

//This is a test comment
//This is new test comment
public class rotCanvas extends Canvas {
  Matrix  idmat = new Matrix(3,3);
  Matrix  objmat = new Matrix(3,3);
  
  boolean redrawneeded =true;
  
  int omx = 0; int mx = 0;
  int omy = 0; int my = 0;
  
  public PDBfile pdb;
  int bsize;

  Image img;
  Graphics ig;
  
  Dimension prefsize;
  
  float centre[] = new float[3];
  float width[] = new float[3];
  float maxwidth;
  float scale;
  
  String inStr;
  String inType;
  
  boolean depthcue = true;
  boolean wire = false;
  boolean bymolecule = false;
  boolean zbuffer = true;
  boolean dragging;

  int xstart;
  int xend;
  int ystart;
  int yend;
  int xmid;
  int ymid;

  Font font = new Font("Helvetica",Font.PLAIN,10);
  public rotCanvas(PDBfile pdb,Dimension d) throws IOException{
    this.pdb = pdb;
    prefsize = new Dimension(d);

    //Initialize the matrices to identity
    
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3 ; j++) {
	if (i != j) {
	  idmat.addElement(i,j,0);
	  objmat.addElement(i,j,0);
	} else {
	  idmat.addElement(i,j,1);
	  objmat.addElement(i,j,1);
	}
      }
    }
    addPDBfile();
  }

  public void addPDBfile() {
    findCentre();
    findWidth();
    scale = findScale();
    System.out.println("Scale factor = " + scale);
  }
  public void deleteBonds() {
    scale = 0;
    maxwidth  = 0;
    width[0] = 0;
    width[1] = 0;
    width[2] = 0;
    centre[0] = 0;
    centre[1] = 0;
    centre[2] = 0;
    for (int i=0; i < pdb.chains.size(); i++) {
      ((PDBChain)pdb.chains.elementAt(i)).bonds = null;
    }
  }

  public void findWidth() {
    float max[] = new float[3];
    float min[] = new float[3];
    
    max[0] = (float)-1e30;
    max[1] = (float)-1e30;
    max[2] = (float)-1e30;
    min[0] = (float)1e30;
    min[1] = (float)1e30;
    min[2] = (float)1e30;
    
    for (int ii=0; ii < pdb.chains.size(); ii++) {
      if (((PDBChain)pdb.chains.elementAt(ii)).isVisible) {
	Vector bonds = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
	for (int i = 0; i < bonds.size(); i++) {
	  Bond tmp = (Bond)bonds.elementAt(i);
	  
	  if (tmp.start[0] >= max[0]) max[0] = tmp.start[0];
	  if (tmp.start[1] >= max[1]) max[1] = tmp.start[1];
	  if (tmp.start[2] >= max[2]) max[2] = tmp.start[2];
	  if (tmp.start[0] <= min[0]) min[0] = tmp.start[0];
	  if (tmp.start[1] <= min[1]) min[1] = tmp.start[1];
	  if (tmp.start[2] <= min[2]) min[2] = tmp.start[2];
	  
	  if (tmp.end[0] >= max[0]) max[0] = tmp.end[0];
	  if (tmp.end[1] >= max[1]) max[1] = tmp.end[1];
	  if (tmp.end[2] >= max[2]) max[2] = tmp.end[2];
	  if (tmp.end[0] <= min[0]) min[0] = tmp.end[0];
	  if (tmp.end[1] <= min[1]) min[1] = tmp.end[1];
	  if (tmp.end[2] <= min[2]) min[2] = tmp.end[2];
	}
      }  
    }
    System.out.println("xmax " + max[0] + " min " + min[0]);
    System.out.println("ymax " + max[1] + " min " + min[1]);
    System.out.println("zmax " + max[2] + " min " + min[2]);
    
    width[0] = (float)Math.abs(max[0] - min[0]);
    width[1] = (float)Math.abs(max[1] - min[1]);
    width[2] = (float)Math.abs(max[2] - min[2]);
    
    maxwidth = width[0];
    
    if (width[1] > width[0]) maxwidth = width[1];
    if (width[2] > width[1]) maxwidth = width[2];
    
    System.out.println("Maxwidth = " + maxwidth);
  } 
  
  public float findScale() {
    int dim, width, height;
    if (size().width != 0) {
      width = size().width;
      height = size().height;
    } else {
      width = prefsize.width;
      height = prefsize.height;
    }
    
    if (width < height) {
      dim = width;
    } else {
      dim = height;
    } 
    
    return (float)(dim/(1.5d*maxwidth));
  }
  
  
  
  public void findCentre() {
    float xtot = 0;
    float ytot = 0;
    float ztot = 0;
    
    int bsize = 0;
    //Find centre coordinate

    for (int ii = 0; ii < pdb.chains.size() ; ii++) {
      if (((PDBChain)pdb.chains.elementAt(ii)).isVisible) {
	Vector bonds = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
	bsize += bonds.size();
	for (int i = 0; i < bonds.size(); i++) {
	  xtot = xtot + ((Bond)bonds.elementAt(i)).start[0] + 
	    ((Bond)bonds.elementAt(i)).end[0];
	  ytot = ytot + ((Bond)bonds.elementAt(i)).start[1] +
	    ((Bond)bonds.elementAt(i)).end[1];
        ztot = ztot + ((Bond)bonds.elementAt(i)).start[2] + 
	  ((Bond)bonds.elementAt(i)).end[2];
	}
      }
    }
    centre[0] = xtot / (2 * (float)bsize);
    centre[1] = ytot / (2 * (float)bsize);
    centre[2] = ztot / (2 * (float)bsize);
  }
  
  public void paint(Graphics g) {
    //Only create the image at the beginning - 
    //this saves much memory usage
    if ((img == null) || (prefsize.width != size().width) || (prefsize.height != size().height)){
      prefsize.width = size().width;
      prefsize.height = size().height;
      scale = findScale();
      img = createImage(prefsize.width,prefsize.height);
      ig = img.getGraphics();
      redrawneeded = true;
    }
    
    if (redrawneeded == true) {
      drawBackground(ig,Color.black);
      drawScene(ig); 
      redrawneeded = false;
    } else {
      ig = img.getGraphics();
    }
    
    g.drawImage(img,0,0,this);
  }
  
  public void drawBackground(Graphics g, Color col) {
    g.setColor(col);
    g.fillRect(0,0,prefsize.width,prefsize.height);
  }
  
  
  public void drawScene(Graphics g) {
    // Sort the bonds by z coord

    Vector bonds = new Vector();


      for (int ii = 0; ii < pdb.chains.size() ; ii++) {
	if (((PDBChain)pdb.chains.elementAt(ii)).isVisible) {
	  Vector tmp = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
	  for (int i=0; i < tmp.size(); i++) {
	    bonds.addElement(tmp.elementAt(i));
	  }
	}
      }
      if (zbuffer) {
	Zsort.Zsort(bonds);
      }
    //    for (int ii = 0; ii < pdb.chains.size() ; ii++) {
    //  Vector bonds = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
      for (int i = 0; i < bonds.size(); i++) {
        Bond tmpBond = (Bond)bonds.elementAt(i);
	
        xstart = (int)((tmpBond.start[0] - centre[0])*scale + size().width/2);
        ystart = (int)((tmpBond.start[1] - centre[1])*scale + size().height/2);
	
        xend = (int)((tmpBond.end[0] - centre[0])*scale + size().width/2);
        yend = (int)((tmpBond.end[1] - centre[1])*scale + size().height/2);
	
        xmid = (xend+xstart)/2;
        ymid = (yend+ystart)/2;
	
	//System.out.println(xstart + " " + ystart);
	
	if (depthcue && !bymolecule) {
	  if (tmpBond.start[2] < (centre[2] - maxwidth/6))  {
	    g.setColor(tmpBond.startCol.darker().darker());
            drawLine(g,xstart,ystart,xmid,ymid);
	    
            g.setColor(tmpBond.endCol.darker().darker());
            drawLine(g,xmid,ymid,xend,yend);
	  } else if (tmpBond.start[2] < (centre[2]+maxwidth/6)) {
	    g.setColor(tmpBond.startCol.darker());
	    drawLine(g,xstart,ystart,xmid,ymid);
	    
	    g.setColor(tmpBond.endCol.darker());
	    drawLine(g,xmid,ymid,xend,yend);
	  } else {
	    g.setColor(tmpBond.startCol);
	    drawLine(g,xstart,ystart,xmid,ymid);
	    
            g.setColor(tmpBond.endCol);
            drawLine(g,xmid,ymid,xend,yend);
	  }
	} else if (depthcue && bymolecule) {
	  if (tmpBond.start[2] < (centre[2] - maxwidth/6))  {
	    g.setColor(Color.green.darker().darker());
	    drawLine(g,xstart,ystart,xend,yend);
	  } else if (tmpBond.start[2] < (centre[2]+maxwidth/6)) {
	    g.setColor(Color.green.darker());
	    drawLine(g,xstart,ystart,xend,yend);
	  } else {
	    g.setColor(Color.green);
            drawLine(g,xstart,ystart,xend,yend);
	  }
	} else if (!depthcue && !bymolecule){
	  g.setColor(tmpBond.startCol);
	  drawLine(g,xstart,ystart,xmid,ymid);
	  g.setColor(tmpBond.endCol);
	  drawLine(g,xmid,ymid,xend,yend);
	} else {
	  drawLine(g,xstart,ystart,xend,yend);
	}
      }
    }
  //}
  
  public void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
    if (!wire) {
      if (((float)Math.abs(y2-y1)/(float)Math.abs(x2-x1)) < 0.5) {
	g.drawLine(x1,y1,x2,y2);
	g.drawLine(x1+1,y1+1,x2+1,y2+1);
	g.drawLine(x1,y1-1,x2,y2-1);
      } else {
	g.setColor(g.getColor().brighter());
	g.drawLine(x1,y1,x2,y2);
	g.drawLine(x1+1,y1,x2+1,y2);
       	g.drawLine(x1-1,y1,x2-1,y2);
      }
     } else {
       g.drawLine(x1,y1,x2,y2);
     }
  }
  
  public Dimension minimumsize() {
    return prefsize;
  }
  public Dimension preferredsize() {
    return prefsize;
  }
  public boolean keyDown(Event evt, int key) {
    if (key == Event.UP) {
      scale = (float)(scale * 1.1);
      redrawneeded = true;
      repaint();
    } else if (key == Event.DOWN) {
      scale = (float)(scale * 0.9);
      redrawneeded = true;
      repaint();
    } else if (key == 'w') {
      wire = !wire;
      System.out.println("wireframe " + wire);
      redrawneeded = true;
      repaint();
    } else if (key == 'd') {
      depthcue = !depthcue;
      System.out.println("Depth cueing is " + depthcue);
      redrawneeded = true;
      repaint();
    } else if (key == 'm') {
      bymolecule = !bymolecule;
      System.out.println("Bymolecule is " + bymolecule);
      redrawneeded = true;
      repaint();
    } else if (key == 'z') {
      zbuffer = !zbuffer;
      System.out.println("Z buffering is " + zbuffer);
      redrawneeded = true;
      repaint();
    } else if (key == 'c') {
      bymolecule = false;
      pdb.setChainColours();
      System.out.println("Colouring by chain");
      redrawneeded = true;
      repaint();
    } else if (key == 'h') {
      bymolecule = false;
      pdb.setHydrophobicityColours();
      System.out.println("Colouring by hydrophobicity");
      redrawneeded = true;
      repaint();
    } else if (key == 'q') {
      bymolecule = false;
      pdb.setChargeColours();
      System.out.println("Colouring charges and cysteines");
      redrawneeded = true;
      repaint();
    } else super.keyDown(evt,key);
    return true;
  }
  
  public boolean mouseDrag(Event e, int x, int y) {
    mx = x;
    my = y;
    

    Matrix objmat = new Matrix(3,3);
    objmat.setIdentity();
    
    if ((e.modifiers  & Event.META_MASK) != 0) {
      objmat.rotatez((float)((mx-omx)));
    } else {
      objmat.rotatex((float)((my-omy)));
      objmat.rotatey((float)((omx-mx)));
    }
    
    

      //Alter the bonds
    for (int ii = 0; ii < pdb.chains.size() ; ii++) {
      Vector bonds = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
      for (int i = 0; i < bonds.size(); i++) {
        Bond tmpBond = (Bond)bonds.elementAt(i);
	
        //Translate the bond so the centre is 0,0,0
        tmpBond.translate(-centre[0],-centre[1],-centre[2]);
	
        //Now apply the rotation matrix
        tmpBond.start = objmat.vectorMultiply(tmpBond.start);
        tmpBond.end = objmat.vectorMultiply(tmpBond.end);
	
        //Now translate back again
        tmpBond.translate(centre[0],centre[1],centre[2]);
      }
    }
    objmat = null;
    
    omx = mx;
    omy = my;
    
    redrawneeded = true;
    paint(this.getGraphics());
 
    dragging = true;
   return true;
  }

  public boolean mouseUp(Event evt, int x, int y) {
    if (!dragging) {
      myAtom tmp = findAtom(x,y);
    }
    drawLabels();
    return true;
  }
  public void drawLabels() {
    redrawneeded = true;
    paint(this.getGraphics());
    for (int ii = 0; ii < pdb.chains.size() ; ii++) {
      PDBChain chain = (PDBChain)pdb.chains.elementAt(ii);
      if (chain.isVisible) {
	Vector bonds = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
	for (int i = 0; i < bonds.size(); i++) {
	  Bond tmpBond = (Bond)bonds.elementAt(i);
	  if (tmpBond.at1.isSelected) {
	    labelAtom(img.getGraphics(),tmpBond,1);
	  }
	  if (tmpBond.at2.isSelected) {
	    labelAtom(img.getGraphics(),tmpBond,2);
	  }
	}
      }
    }
    this.getGraphics().drawImage(img,0,0,this);
    dragging = false;
  }
  public void labelAtom(Graphics g,Bond b, int n) {
    g.setFont(font);
    if (n ==1) {
      int xstart = (int)((b.start[0] - centre[0])*scale + size().width/2);
      int ystart = (int)((b.start[1] - centre[1])*scale + size().height/2);

      g.setColor(Color.red);
      g.drawString(b.at1.resName + "-" + b.at1.resNumber,xstart,ystart);
    }
    if (n ==2) {
      int xstart = (int)((b.end[0] - centre[0])*scale + size().width/2);
      int ystart = (int)((b.end[1] - centre[1])*scale + size().height/2);
      g.setColor(Color.red);
      g.drawString(b.at2.resName + "-" + b.at2.resNumber,xstart,ystart);
    }
  }

  public boolean mouseDown(Event evt, int x, int y) {
    mx = x;
    my = y;
    omx = mx;
    omy = my;
    
    dragging = false;

    return true;
  }
  public myAtom findAtom(int x, int y) {
    myAtom fatom = null;
    int foundchain = -1;
    for (int ii = 0; ii < pdb.chains.size() ; ii++) {
      PDBChain chain = (PDBChain)pdb.chains.elementAt(ii);
      if (chain.isVisible) {
	Vector bonds = ((PDBChain)pdb.chains.elementAt(ii)).bonds;
	for (int i = 0; i < bonds.size(); i++) {
	  Bond tmpBond = (Bond)bonds.elementAt(i);
	  int truex = (int)((tmpBond.start[0] - centre[0])*scale + size().width/2);
	  
	  if (Math.abs(truex - x) <= 2) {
	    int truey = (int)((tmpBond.start[1] - centre[1])*scale + size().height/2);
	    if (Math.abs(truey - y) <= 2) {
	      System.out.println("Found match");
	      System.out.println(x + " " + y);
	      System.out.println(truex + " " + truey);
	      System.out.println(tmpBond.start[0] + " " + tmpBond.start[1]);
	      System.out.println("Atom 1 = " + tmpBond.at1.resName + " " +
				 tmpBond.at1.resNumber + " " + tmpBond.at1.chain);
	      fatom = tmpBond.at1;
	      fatom.isSelected = !fatom.isSelected;
	      foundchain = ii;
	    }
	  }
	}
      }
      if (fatom != null && chain.ds != null) {
	chain = (PDBChain)pdb.chains.elementAt(foundchain);
	int tmp = chain.ds.seqstart + fatom.resNumber - chain.offset;
	int pos = chain.ds.findIndex(tmp);
	System.out.println("Found seq " + chain.ds.name + " "  + tmp + " " + pos);
      }
      
    }
    return null;
  }
  
  public void update(Graphics g) {
    paint(g);
  }
  
}
