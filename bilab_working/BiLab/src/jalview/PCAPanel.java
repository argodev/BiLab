package jalview;

import java.awt.*;
import java.util.*;

public class PCAPanel extends Panel {
  PCA pca;
  Object parent;
  int top;
  RotatableCanvas rc;
  Choice cx;
  Choice cy;
  Choice cz;
  Button b;

  public PCAPanel(Object parent, PCA pca) {
    this(parent,pca,null);
  }
  
  public PCAPanel(Object parent, PCA pca, Sequence[] s) {
  

    this.parent = parent;
    this.pca = pca;

    Panel p1  = new Panel();

    Panel p2  = new Panel();
    Panel p3  = new Panel();

    Panel p4 = new Panel();

    Label l1 = new Label("x = ");
    Label l2 = new Label("y = ");
    Label l3 = new Label("z = ");
    
    b = new Button("Close");

    cx = new Choice();
    cy = new Choice();
    cz = new Choice();

    addItems(cx);
    addItems(cy);
    addItems(cz);

    cx.select(1);
    cy.select(2);
    cz.select(3);

    top = pca.m.rows-1;

    Vector points = new Vector();
    float[][] scores = pca.getComponents(top-1,top-2,top-3,100);

    for (int i =0; i < pca.m.rows; i++ ) {
      SequencePoint sp = new SequencePoint(s[i],scores[i]);
      points.addElement(sp);
    }
    //    rc = new RotatableCanvas(parent,pca.getComponents(top-1,top-2,top-3,100),pca.m.rows,s);
    rc = new RotatableCanvas(parent,points,pca.m.rows);
    
    rc.printPoints();

    p1.setLayout(new BorderLayout());
    p1.add("Center",rc);

    p2.setLayout(new FlowLayout());
    p2.add(l1);
    p2.add(cx);

    p2.add(l2);
    p2.add(cy);

    p2.add(l3);
    p2.add(cz);
    
    p3.add(b);

    p4.setLayout(new GridLayout(2,1));
    p4.add(p2);
    p4.add(p3);

    setLayout(new BorderLayout());

    add("Center",p1);
    add("South",p4);
  }

  public boolean action(Event evt, Object arg) {
    boolean newdim = false;

    if (evt.target.equals(cx)) {
      newdim = true;
    } else if (evt.target.equals(cy)) {
      newdim=true;
    } else if (evt.target.equals(cz)) {
      newdim = true;
    } else if (evt.target.equals(b)) {
      this.hide();
      if (this.getParent() instanceof Frame) {
	((Frame)this.getParent()).dispose();
      }
    }

    if (newdim == true) {
      int dim1 = top - ((Choice)cx).getSelectedIndex();
      int dim2 = top - ((Choice)cy).getSelectedIndex();
      int dim3 = top -((Choice)cz).getSelectedIndex();

      float[][] scores  = pca.getComponents(dim1,dim2,dim3,100);
      for (int i=0; i < pca.m.rows; i++) {
	((SequencePoint)rc.points.elementAt(i)).coord = scores[i];
      }

      rc.img = null;
      rc.rotmat.setIdentity();
      rc.initAxes();
      rc.paint(rc.getGraphics());
      return true;
    } else {
      return false;
    }

  }
  public boolean keyDown(Event evt, int key) {
      if (key == Event.UP || key == Event.DOWN) {
	 return  rc.keyDown(evt,key);
       } else if (key == 's') {
	 return rc.keyDown(evt,key);
      } else {
     return super.keyDown(evt,key);
     }
  }
    
  public void addItems(Choice c) {
    c.addItem("dim 1");
    c.addItem("dim 2");
    c.addItem("dim 3");
    c.addItem("dim 4");
    c.addItem("dim 5");
    c.addItem("dim 6");
    c.addItem("dim 7");
  }
}
