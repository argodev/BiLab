package jalview;

import java.awt.*;

public class PCAThread extends Thread {
    DrawableSequence[] s;
    Object parent;
    PCA pca;
    PCAPanel p;
    
    boolean calculated = false;
    
    public PCAThread(Object parent, DrawableSequence[] s) {
      this.s = s;
      this.parent = parent;
    }
    
    public void run() {
      pca = new PCA(s);
      pca.run();
      calculated = true;
      
      if (parent instanceof AlignFrame) {
	((AlignFrame)parent).status.setText("Finished PCA calculation");
	((AlignFrame)parent).status.validate();
      }
      // Now find the component coordinates   
      int ii=0;
      while (ii < s.length && s[ii] != null) {
	ii++;
      }

      double[][] comps = new double[ii][ii];

      for (int i=0; i < ii; i++ ) {
	if (pca.eigenvector.d[i] > 1e-4) {
	  comps[i]  = pca.component(i);
	} 
	  
      }

      PCAFrame f = new PCAFrame("PCA results",parent);
      f.setLayout(new BorderLayout());
      p  = new PCAPanel(parent,pca,s);
      f.add("Center",p);
      f.resize(400,400);
      
      f.show();
    }
  }
