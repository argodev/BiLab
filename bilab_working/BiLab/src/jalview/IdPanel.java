/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jalview;
import java.applet.*;
import java.net.*;
import java.awt.*;
public class IdPanel extends Panel {

  protected AlignmentPanel alignPanel;
  public IdCanvas idCanvas; // -DJ made public

  protected int offy;
  public int width;

  public int lastid;

  public IdPanel(AlignmentPanel alignPanel) {
    this.alignPanel = alignPanel;
    componentInit();
  }

  private void componentInit() {
    idCanvas = new IdCanvas(this);
    setLayout(new BorderLayout());
    add("Center",idCanvas);
  }

  public Dimension minimumSize() {
    return new Dimension(100,500);
  }
  public Dimension preferredSize() {
    return minimumSize();
  }


  public boolean keyDown(Event evt, int key) {
    
    // This code moves the sequences up and down the alignment by pressing the
    // arrow keys.
    requestFocus();
    AlignFrame af = null;
    AlignmentPanel ap = alignPanel;
    if (ap.parent instanceof AlignFrame) {
      af = (AlignFrame)ap.parent;
    }
    if (ap.sel.size() > 0) {
      DrawableSequence[] tmps = new DrawableSequence[ap.sel.size()];
      float[] ind = new float[tmps.length];
      if (key == Event.UP) {
	
	// First sort the selected seuqences
	for (int j =0; j < tmps.length; j++) {
	  tmps[j] = (DrawableSequence)ap.sel.elementAt(j);
	  int i=0;
	  while (i< ap.seqPanel.align.ds.length && ap.seqPanel.align.ds[i] != null) {
	    if (ap.seqPanel.align.ds[i] == (DrawableSequence)ap.sel.elementAt(j)) {
	      ind[j] = (float)i;
	      break;
	    }
	    i++;
	  }
	}
      } else if (key == Event.DOWN) {
	for (int j =0; j < tmps.length; j++) {
	  tmps[j] = (DrawableSequence)ap.sel.elementAt(j);
	  int i=0;
	  while (i< ap.seqPanel.align.ds.length && ap.seqPanel.align.ds[i] != null) {
	    if (ap.seqPanel.align.ds[i] == (DrawableSequence)ap.sel.elementAt(j)) {
	      ind[j] = (float)(tmps.length-i);
	      break;
	    }
	    i++;
	  }
	}
      }
      
      QuickSort.sort(ind,tmps);	  
      
      // Now swap them about
      for (int ii=0; ii< tmps.length; ii++) {
	DrawableSequence ds = tmps[ii];
	int i=0 ;
	int fid = -1;
	boolean found = false;
	while (i< ap.seqPanel.align.ds.length && ap.seqPanel.align.ds[i] != null) {
	  if (ap.seqPanel.align.ds[i] == ds) {
	    found = true;
	    fid = i;
	  }
	  i++;
	}
	if (found) {
	  if (key == Event.UP && fid > 0) {
	    if (!ap.sel.contains(ap.seqPanel.align.ds[fid-1])) {
	      DrawableSequence tmp = ap.seqPanel.align.ds[fid-1];
	      ap.seqPanel.align.ds[fid-1] = ap.seqPanel.align.ds[fid];
	      ap.seqPanel.align.ds[fid] = tmp;
	      if (af != null) {
		af.status.setText("Moved " + ap.seqPanel.align.ds[fid-1].name);
		af.status.validate();
	      }
	    } 
	  } else if  (key == Event.DOWN && (fid+1) < ap.seqPanel.align.ds.length && ap.seqPanel.align.ds[fid+1] != null) {
	    if (!ap.sel.contains(ap.seqPanel.align.ds[fid+1])) {
	      DrawableSequence tmp = ap.seqPanel.align.ds[fid+1];
	      ap.seqPanel.align.ds[fid+1] = ap.seqPanel.align.ds[fid];
	      ap.seqPanel.align.ds[fid] = tmp;  
	      if (af != null) {
		af.status.setText("Moved " + af.ap.seqPanel.align.ds[fid+1].name);
		af.status.validate();
	      }
	    }
	  }
	} else {
	  System.out.println("ID not found");
	}
      }
    }  else {
	af.status.setText("ERROR: no sequence(s) selected");
    }
    if (af != null) {
      af.updateFont();
    } else {
	ap.idPanel.idCanvas.paintFlag = true;
	ap.idPanel.idCanvas.repaint();
	ap.seqPanel.seqCanvas.paintFlag = true;
	ap.seqPanel.seqCanvas.repaint();
    }
    return true;
  }

  public boolean mouseDrag(Event e, int x, int y) {
    try{
      int seq = (y)/alignPanel.seqPanel.seqCanvas.charHeight + alignPanel.seqPanel.seqCanvas.starty;
      if (seq != lastid) {
	if (alignPanel.parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)alignPanel.parent;
	  af.status.setText("Sequence ID : " + 
			    alignPanel.seqPanel.align.sequences[seq].getName() +  " (" + seq + ")");
	  if (af.ap.sel.contains( alignPanel.seqPanel.align.sequences[seq])) {
	    System.out.println("Selected " + seq);
	    af.ap.sel.removeElement(alignPanel.seqPanel.align.sequences[seq]);	    
	    if (af.pca != null) {
	      PCAPanel pca  = af.pca.p;
	      pca.rc.redrawneeded = true;
	      pca.rc.repaint();
	    }
	    if (af.tt != null) {
	      af.tt.tf.p.mc.repaint();
	    }
	    
	  } else {
	    //	    af.ap.selected[seq] = true;
	    af.ap.sel.addElement(alignPanel.seqPanel.align.sequences[seq]);
	    if (af.pca != null) {
	      PCAPanel pca = af.pca.p;
	      pca.rc.redrawneeded = true;
	      pca.rc.repaint();
	    }
	    if (af.tt != null) {
	      af.tt.tf.p.mc.repaint();
	    }
	    
	  }
	
	  af.ap.idPanel.idCanvas.paintFlag = true;
	  af.ap.idPanel.idCanvas.repaint();
	}
      }
      lastid = seq;
    } catch (Exception ex) {
    }
    return true;
  }
      
  public boolean mouseDown(Event e, int x, int y) {
    AlignFrame af = null;
    AlignmentPanel ap = alignPanel;
    if (ap.parent instanceof AlignFrame) {
      af = (AlignFrame)ap.parent;
    }
    //    int seq = (y)/ap.seqPanel.seqCanvas.charHeight + ap.seqPanel.seqCanvas.starty;
    int seq = alignPanel.seqPanel.seqCanvas.getIndex(y);
      System.out.println("Y = " + y + " " + seq + " " + ap.seqPanel.align.ds[seq].name);

    try {
      if ((e.modifiers & Event.META_MASK) != 0) {
      	try {	
	  String id =  ap.seqPanel.align.ds[seq].getName();
	  if ( id.indexOf("/") != -1) {
	    id = id.substring(0,id.indexOf("/"));
	  }
	  
	  URL u = new URL("http://" + af.srsServer + "wgetz?-e+[" + af.database + "-id:" + 
			  id + "]");
	  
      	  if (af != null && af.parent instanceof Applet) {
      	    Applet app = (Applet)af.parent;
      	    app.getAppletContext().showDocument(u,"entry");
      	  } else {
	    if (af != null && af.browser != null) {
	      if (af.browser.bf != null) {
		System.out.println("browser exists");
		af.browser.bf.tf.setText(u.toString());
		af.browser.pages.addElement(u.toString());
		af.browser.position = af.browser.pages.size()-1;
		if (af.browser.pages.size() > 1) {
		  af.browser.bf.back.enable();
		}
		af.browser.connect(af.browser.split(u.toString()));
	      } else {
		System.out.println("browser null");
		af.browser = new SimpleBrowser(u.toString());
	      }
	    } else if (af != null) {
	      af.browser = new SimpleBrowser(u.toString());
	    }
	  }
      	} catch (Exception ex) {
      	  System.out.println("Exception : " + ex);
      	}
      } else {
	lastid = seq;
	if (af != null) {af.status.setText("Sequence ID : " + 
					   alignPanel.seqPanel.align.ds[seq].getName() +  
					   " (" + seq + ")");
	}
	if (ap.sel.contains(alignPanel.seqPanel.align.ds[seq])) {
	  //	    af.ap.selected[seq] = false;
	  ap.sel.removeElement(alignPanel.seqPanel.align.ds[seq]);
	  if (af != null && af.pca != null) {
	    PCAPanel pca  = af.pca.p;
	    pca.rc.redrawneeded = true;
	    pca.rc.repaint();
	  }
	  if (af != null && af.tt != null) {
	    af.tt.tf.p.mc.repaint();
	  }
	  
	} else {
	  ap.sel.addElement(alignPanel.seqPanel.align.ds[seq]);
	  if (af != null && af.pca != null) {
	    PCAPanel pca = af.pca.p;
	    pca.rc.redrawneeded = true;
	    pca.rc.repaint();
	  }
	  if (af != null && af.tt != null) {
	    af.tt.tf.p.mc.repaint();
	  }
	  
	}
	ap.idPanel.idCanvas.paintFlag = true;
	ap.idPanel.idCanvas.repaint();
	
      }
    } catch (Exception ex) {
    }
    return true;
  }
  public boolean mouseUp(Event e,int x,int y) {
    if (alignPanel.seqPanel.seqCanvas.colourSelected) {
	alignPanel.seqPanel.seqCanvas.paintFlag = true;
	alignPanel.seqPanel.seqCanvas.repaint();
    }
    return true;
  }
}
    
    
