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


public class FeatureFrame extends TextFrame {

  Panel p4;
  Label l;
  TextField tf;
  String url;
  Button back;
  Button forward;
  Object parent;

  public FeatureFrame(Object parent,String title,int nrows, int ncols, String text) {
    super(title,nrows,ncols,text);
    this.parent = parent;
  }

  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {
	this.hide();
	this.dispose();
	if (parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)parent;
	  af.ff = null;
	  if (af.sft.isAlive()) {
	    System.out.println("icky");
	    af.sft.stop();
	    af.sft = null;

	  }
	  af.status.setText("Closed sequence feature console");	  
	}

	return true;
    }  else return super.handleEvent(evt);
  }
  public boolean action(Event e, Object arg) {
    if (e.target == b) {
       System.out.println("Disposing of feature frame");
       this.hide();
       this.dispose();
       if (parent instanceof AlignFrame) {
	 AlignFrame af = (AlignFrame)parent;
	 af.ff = null;
	 if (af.sft.isAlive()) {
	   
	    af.sft.stop();
	    af.sft = null;

	  }
	 af.status.setText("Closed sequence feature console");	  
       }
       return true;
     } else {
       return false;
     }
   }
}

