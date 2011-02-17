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
import java.io.File;

public class FilePopup extends Popup {
  TextField tf;
  Label tfLabel;
  Button b;
  Label format;
  Choice f;

  public FilePopup(Frame parent,String title) {
    super(parent,title);

    tf = new TextField(40);
    tfLabel = new Label("Filename : ");

    format = new Label("Alignment format");
    f = new Choice();
    for (int i = 0; i < FormatProperties.formats.size(); i++) {
      f.addItem((String)FormatProperties.formats.elementAt(i));
    }

    b = new Button("Browse..");
    
    gbc.fill = GridBagConstraints.NONE; 

    gbc.insets = new Insets(20,20,20,20);

    add(tfLabel,gb,gbc,0,0,1,1);
    add(tf,gb,gbc,1,0,4,1);
    add(format,gb,gbc,0,1,1,1);
    add(f,gb,gbc,1,1,1,1);
    add(b,gb,gbc,5,0,1,1);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(status,gb,gbc,0,2,1,1);
    gbc.fill = GridBagConstraints.NONE; 
    add(apply,gb,gbc,0,3,1,1);
    add(close,gb,gbc,1,3,1,1);

    pack();
    show();
  }

  public boolean handleEvent (Event e) {
    if (e.target == apply && e.id == 1001) {
      status.setText("Reading file...");
      status.validate();
      String fileStr = tf.getText();
      File tmp = new File(fileStr);
      if (tmp.isFile()) {
	DrawableSequence[] s = null;
	s = FormatAdapter.toDrawableSequence(FormatAdapter.read(fileStr,"File",f.getSelectedItem()));
	
	if (s != null) {
	  status.setText("Creating new alignment window...");
	  status.validate();
	  AlignFrame af = new AlignFrame(parent.getParent(),s);
	  if (af.ap.seqPanel.align.size() > 0) {
	    af.resize(700,500);
	    af.show();
	    ConsThread ct = new ConsThread(af);
	    ct.start();
	  }
	  this.hide();
	  this.dispose(); 
	} else {
	  status.setText("ERROR: No sequences found. Check format.");
	}
      } else {
	  status.setText("ERROR: File not found or wrong format");
      }
      return true;
    } else if (e.target == b  && e.id == 1001) {
      FileDialog fd = new FileDialog(parent,"Open alignment file",FileDialog.LOAD);
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



