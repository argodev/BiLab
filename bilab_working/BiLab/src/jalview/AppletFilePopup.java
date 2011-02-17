
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
import java.io.*;

public class AppletFilePopup extends FilePopup {

  public AppletFilePopup(Frame parent, String title) {
    super(parent,title);
    tf.hide();
    b.hide();
    b = null;
    tf = null;
    tfLabel.hide();
    tfLabel = null;
    validate();
  }

  public boolean handleEvent (Event e) {
    
    if (e.target == apply && e.id == 1001) {
      if (FormatProperties.contains(f.getSelectedItem())) {
	if (parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)parent;
	  String outStr = FormatAdapter.get(f.getSelectedItem().toUpperCase(),af.ap.seqPanel.align.sequences);
	  System.out.println(outStr);
	  status.setText("Saving file to server");
	  status.validate();

	  try {
	    Thread.sleep(500);
	  } catch (Exception ex2) {}

          status.setText("Sending file to browser...");
          status.validate();
	  SendFileCGI sf= new SendFileCGI("circinus.ebi.ac.uk",6543,"cgi-bin/sendfile","/ebi/barton/delly/michele/htdocs/temp/out.txt",System.out,outStr);
	  
	  sf.run();
	  try {
	    URL fileski = new URL("http://circinus.ebi.ac.uk:6543/temp/out.txt");
	    if (af.parent instanceof Applet) {
	      ((Applet)af.parent).getAppletContext().showDocument(fileski);
	    }
	  } catch (MalformedURLException ex1) {
	    status.setText("ERROR: Can't open file");
	    System.out.println("Exception : "+ ex1);
	    
	  }    
	  status.setText("done");
	  status.validate();
	  this.hide();
	  this.dispose();
	} else {
	  System.out.println("Error : parent isn't Alignment Frame");
	}
      } else {
	status.setText("Format not yet supported");
      }
      return true;
      
   } else {
     return super.handleEvent(e);
   }
 }
}


