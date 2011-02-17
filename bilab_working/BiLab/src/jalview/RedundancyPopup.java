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
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.util.*;

 public class RedundancyPopup extends Popup {
    ThresholdPanel tp;

   public RedundancyPopup(Frame parent, String title, String label, int low, int high, int value) {
     super(parent,title);
     
     tp = new ThresholdPanel(parent,label,low,high, value);
     status.setText("Enter the redundancy threshold");
     gbc.fill = GridBagConstraints.NONE; 

     gbc.insets = new Insets(20,20,20,20);
     add(tp,gb,gbc,0,0,2,1);
     add(status,gb,gbc,0,1,2,1);
     add(apply,gb,gbc,0,2,1,1);
     add(close,gb,gbc,1,2,1,1);
     pack();
     show();
    }

    public boolean handleEvent(Event e) {
      if (e.target == apply && e.id == 1001) {
	int threshold = 0;
	Vector del;
	try {
	  threshold = Integer.valueOf(tp.tf.getText()).intValue();
	} catch (Exception ex) {
	  threshold = tp.sb.getValue();
	  tp.tf.setText(new Integer(threshold).toString());
	}
	System.out.println("Threshold = " + threshold);
	System.out.println(parent);
	if (parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)parent;
	  af.status.setText("Removing redundancy...");	  

	  af.status.validate();
	  if (af.ap.sel != null && af.ap.sel.size() != 0) {
	    System.out.println("Sel = " + af.ap.sel);
	    if (af.ap.sel.size() == 1) {
	      af.status.setText("ERROR: Only one sequence selected");	  af.status.validate();
	    } else {
	      status.setText("Removing redundancy for " + af.ap.sel.size() + " sequences");	  
	      del = af.ap.seqPanel.align.removeRedundancy(threshold,af.ap.sel);
	      for (int i=0; i < del.size(); i++) {
		if (af.ap.sel.contains(del.elementAt(i))) {
		  af.ap.sel.removeElement(del.elementAt(i));
		}
	      }
	    }
	  } else {
	    System.out.println("Creating vector");
	    Vector s = new Vector();
	    int i=0;
	    while(i < af.ap.seqPanel.align.sequences.length &&  af.ap.seqPanel.align.sequences[i] != null) {
	      s.addElement( af.ap.seqPanel.align.sequences[i]);
	      i++;
	    }
	    System.out.println("Removing redundancy for " + s.size()  + " sequences");
	    del = af.ap.seqPanel.align.removeRedundancy(threshold,s);
	    for (int j=0; j < del.size(); j++) {
		af.status.setText("Removing sequence " + ((Sequence)del.elementAt(j)).getName());
		af.status.validate();
	      if (af.ap.sel.contains(del.elementAt(j))) {
		af.ap.sel.removeElement(del.elementAt(j));
	      }
	    }
	  }
	  af.status.setText("Done");
	  af.updateFont();
	} 
	return true;
    } else if (e.target == close && e.id == 1001) {
	this.hide();
	this.dispose();
	return true;
    } else return super.handleEvent(e);
   }
}




