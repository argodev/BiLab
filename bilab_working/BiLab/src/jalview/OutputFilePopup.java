
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

public class OutputFilePopup extends FilePopup {

  public OutputFilePopup(Frame parent, String title) {
    super(parent,title);
  }

  public boolean handleEvent (Event e) {

   if (e.target == apply && e.id == 1001) {

     String fileStr = tf.getText();
     if (FormatProperties.contains(f.getSelectedItem())) {
       if (parent instanceof AlignFrame) {
	 AlignFrame af = (AlignFrame)parent;
	 String outStr = FormatAdapter.get(f.getSelectedItem().toUpperCase(),af.ap.seqPanel.align.sequences);
	 System.out.println(outStr + " " + fileStr);
	 try {
	   PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileStr)));
	   status.setText("Saving file");
	   status.validate();
	   try {
	     Thread.sleep(500);
	   } catch (Exception ex2) {}
	   ps.print(outStr);
	   ps.close();
	   status.setText("done");
	   status.validate();
	   this.hide();
	   this.dispose();
	 } catch (IOException ex) {
	   status.setText("ERROR: Can't open file");
	   System.out.println("Exception : "+ ex);
	 }
       } else {
	 System.out.println("Error : parent isn't Alignment Frame");
       }
     } else {
       status.setText("Format not yet supported");
     }
     return true;
     
   } else if (e.target == b  && e.id == 1001) {
     FileDialog fd = new FileDialog(parent,"Save alignment file",FileDialog.LOAD);
     fd.show();
     String dir = "";
     String file = "";
     if (fd.getDirectory() != null) { dir = fd.getDirectory();}
     if (fd.getFile() != null) { file = fd.getFile();}
     tf.setText(dir + file);
     return true;
   } else {
     return super.handleEvent(e);
   }
 }
}


