package jalview;

import java.awt.*;
import java.util.*;

public class TreePanel extends Panel  {
  NJTree njt;
  Object parent;
  Panel p;

  Button close;
  Button output;

  Checkbox cb;
  TreeThreadCanvas mc;
  Choice f;
  
 // Output properties
  MailProperties mp;
  FileProperties fp;
  PostscriptProperties pp;

  Vector selected;

  public TreePanel(Object parent, NJTree njt) {
    this.njt = njt;
    this.parent = parent;
    treeInit();
  }
  public TreePanel(Object parent,Sequence[] s,String treetype, String pairdist) {
    this.njt = new NJTree(s,treetype,pairdist);
    if (parent instanceof AlignFrame ) {
      ((AlignFrame)parent).status.setText("Finished calculating tree");   
      ((AlignFrame)parent).status.validate();
    }
    this.parent = parent;
    treeInit();
  }
  public TreePanel(Object parent,Sequence[] s) {
    this(parent,s,"AV","PID");
  }
  public void treeInit() {
    setLayout(new BorderLayout());
    
    p = new Panel();

    Panel p2 = new Panel();
    
    p.setLayout(new BorderLayout());
    p2.setLayout(new FlowLayout());
    
    close = new Button("Close");
    output = new Button("Output");

    cb = new Checkbox("Show distances");
    cb.setState(false);

    Label l = new Label("Font size");

    f = new Choice();
    f.addItem("6");
    f.addItem("8");
    f.addItem("10");
    f.addItem("12");
    f.addItem("14");
    f.select("8");
    njt.tf.setFontSize(8);

    njt.tf.showDistances = false;

    njt.tf.reCount(njt.tf.top);
    njt.tf.findHeight(njt.tf.top);

    mc = new TreeThreadCanvas(njt.tf); 
    mc.setBackground(Color.white);
    if (parent instanceof OutputGenerator) {
      njt.tf.mp.server = ((OutputGenerator)parent).getMailProperties().server;
    }

    p.add("Center",mc);
    p2.add(l);
    p2.add(f);
    p2.add(cb);
    p2.add(close);
    p2.add(output);
    add("Center",p);
    add("South",p2);

    if (parent instanceof AlignFrame) {
      selected = ((AlignFrame)parent).ap.sel;
      njt.tf.selected =  ((AlignFrame)parent).ap.sel;
    } else {
      selected = new Vector();
    }
  }

  public boolean mouseDown(Event e, int x,int y) {
    if (e.target == mc) {
      Object ob = njt.tf.findElement(mc,x,y);
      
      if (ob instanceof Sequence) {
	Sequence s = (Sequence)ob;
	System.out.println(s.name);
	if (selected.contains(s)) {
	  selected.removeElement(s);
	} else {
	  selected.addElement(s);
	}
	if (parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)parent;
	  af.ap.idPanel.idCanvas.paintFlag = true;
	  af.ap.idPanel.idCanvas.repaint();
	  if (af.pca != null) {
	    af.pca.p.rc.redrawneeded = true;
	    af.pca.p.rc.repaint();
	  }
	}
	mc.repaint();
      } else {
	// Find threshold
	if (njt.tf.maxheight != 0) {
	  float t = (float)(x - njt.tf.offx)/(float)(size().width*0.8 - 2*njt.tf.offx);
	  System.out.println("New threshold = " + t);
	  mc.threshold = x;
	  mc.paint(mc.getGraphics());
	  if (parent instanceof AlignFrame) {
	    AlignFrame af = (AlignFrame)parent;
	    af.regroup(t);
	  }
	}
      }
    }
    return true;
  }
    
  public boolean action(Event evt, Object arg) {
    if (evt.target == f) {
      int size = Integer.parseInt(((Choice)f).getSelectedItem());
      njt.tf.setFontSize(size);
      mc.repaint();
    }
    return true;
  }
  public boolean handleEvent(Event evt) {

    if (evt.target == close && evt.id == 1001) {
      this.hide();
      if (getParent() instanceof Frame) {
	((Frame)getParent()).dispose();
      }
      return true;
    } else if (evt.target == output && evt.id == 1001) {
      if (parent instanceof Frame) {
	PostscriptPopup pp = new PostscriptPopup((Frame)parent,"Mail postscript",njt.tf);
      }
      return true;
    } else if (evt.target == cb) {
      njt.tf.showDistances = cb.getState();
      mc.repaint();

      if (parent instanceof Frame) {
	((Frame)parent).validate();
      }

      return true;
    } else {
      return super.handleEvent(evt);
    }
  }


  public void show() {
    super.show();
    njt.tf.draw(p.getGraphics(),500,500);
  }

  public static void main(String[] args) {
    try {
      
      MSFfile msf = new MSFfile(args[0],"File");
      
      Sequence[] s = new Sequence[msf.seqs.size()];
      
      for (int i=0; i < msf.seqs.size();i++) {
	s[i] = (Sequence)msf.seqs.elementAt(i);
      }
      Frame tf = new Frame("Average distance tree");
      TreePanel tp = new TreePanel(tf,s);
      tf.setLayout(new BorderLayout());
      tf.add("Center",tp);
      tf.resize(500,500);
      tf.show();



    } catch (java.io.IOException e) {
      System.out.println("Exception : " + e);
    }
  }
}

class TreeThreadCanvas extends Canvas {
  TreeFile tf;
  int threshold = 0;

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }
  public Dimension getMinimumSize() {
    return new Dimension(500,500);
  }
  public TreeThreadCanvas(TreeFile tf) {
    super();
    this.tf = tf;
  }
  
  public void paint(Graphics g) {
    if (size() != null) {
      tf.draw(g,size().width,size().height);
      if (threshold != 0) {
	g.setColor(Color.black);
	g.drawLine(threshold,0,threshold,size().height);
      }
    } else {
      tf.draw(g,500,500);
      if (threshold != 0) {
	g.setColor(Color.black);
	g.drawLine(threshold,0,threshold,size().height);
      }
      
    }
  }


}


