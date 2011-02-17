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
 
 public class PercentIdentityPopup extends Popup {
    ThresholdPanel tp;

   public PercentIdentityPopup(Frame parent, String title, String label, int low, int high, int value) {
     super(parent,title);
     
     tp = new ThresholdPanel(parent,label,low,high, value);
     status.setText("Enter the threshold above which to colour sequences");
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
	try {
	  threshold = Integer.valueOf(tp.tf.getText()).intValue();
	} catch (Exception ex) {
	  threshold = tp.sb.getValue();
	  tp.tf.setText(new Integer(threshold).toString());
	}
	if (parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)parent;
	  af.threshold = threshold;
	  af.ap.setSequenceColor(threshold,ColourAdapter.get(af.getScheme()));
	  af.updateFont();
	} 
	return true;
    } else if (e.target == close && e.id == 1001) {
	this.hide();
	this.dispose();
	return true;
    } else return super.handleEvent(e);
   }
 
   public static void main(String[] args) {
   Frame f = new Frame();
     PercentIdentityPopup pip = new PercentIdentityPopup(f,"title","Percent identity",0,100,80);
     pip.show();
    }
}


