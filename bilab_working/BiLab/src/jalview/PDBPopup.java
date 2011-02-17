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
import java.util.Vector;
import java.io.*;
import java.net.*;

public class PDBPopup extends Popup {
  java.io.PrintStream o = System.out;

  List pdbList;
  Label title1;
  Label title1a;
  Label title2;
  Label sequenceLabel;

  TextField tf;
  Label tfLabel;
  Button fetch;

  DrawableAlignment da;
  Vector codes;
  DrawableSequence seq;

  public PDBPopup(Frame parent, String title, DrawableAlignment da) {
    super(parent,title);
    System.out.println("poppoy");
    this.da = da;

    title1 = new Label("Select a pdb code obtained from the database entry");
    title2 = new Label("Or enter a code yourself");

    sequenceLabel = new Label("No code selected");
    title1a = new Label();
    pdbList = new List(9,false);
    addCodes();

    tf = new TextField(20);

    tfLabel = new Label("Enter PDB code");
    fetch = new Button("Fetch structure");

    apply.setLabel("Fetch structure");

    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(5,5,5,5);

    add(title1,gb,gbc,0,0,2,1);         // Title for pdb codes already present
    add(pdbList,gb,gbc,0,1,1,3);        // list of pdb codes
    add(sequenceLabel,gb,gbc,1,1,1,1);  // Label for the pdb list - selected sequence
    add(title1a,gb,gbc,1,2,1,1);  // Label for the pdb list - selected sequence
    gbc.fill = GridBagConstraints.NONE;
    add(apply,gb,gbc,1,3,1,1);          // Fetch structure 

    gbc.fill = GridBagConstraints.BOTH;
    add(title2,gb,gbc,0,4,2,1);         // title 2 for text input
    //    add(tfLabel,gb,gbc,0,5,1,1);        // label for text input
    add(tf,gb,gbc,0,5,1,1);             // field for typing
    gbc.fill = GridBagConstraints.NONE;
    add(fetch,gb,gbc,1,5,1,1);          // button for fetching the structure



    gbc.fill = GridBagConstraints.BOTH;
    
    status = new Label("Status:",Label.LEFT);
    add(status,gb,gbc,0,6,2,1);
    gbc.fill = GridBagConstraints.NONE;
    add(close,gb,gbc,0,7,2,1);

    pack();
    show();

  }

  public void addCodes() {
    codes = da.getPDBCodes();
    if (codes.size() == 0) {
      sequenceLabel.setText("No PDB codes available");
    } else {
      for (int i=0; i < codes.size() ;i++) {
	pdbList.addItem((String)codes.elementAt(i));
      }
    }
  }

  
  public boolean handleEvent(Event e) {
    if (e.target == apply && e.id == 1001) {
      applyCommand();
    } else if (e.target == fetch && e.id == 1001 &! tf.getText().equals("")) {
      if (da.ds[0].pdbcode == null) {
	da.ds[0].pdbcode = new Vector();
      }
      da.ds[0].pdbcode.insertElementAt(tf.getText(),0);
      seq = da.ds[0];
      applyCommand();
    } else if (e.target == pdbList) {

      String code = (String)pdbList.getSelectedItem();

      int i=0;

      while( i < da.ds.length && da.ds[i] != null) {
	if (da.ds[i].pdbcode != null) {
	  for (int j = 0; j < da.ds[i].pdbcode.size(); j++) {
	    //   System.out.println("Code = " + code + " " + (String)da.ds[i].pdbcode.elementAt(j));
	    if (((String)da.ds[i].pdbcode.elementAt(j)).equals(code)) {
	      seq = da.ds[i];
	    }
	  }
	}
	i++;
      }
      sequenceLabel.setText("PDB structure is attached to :");
      title1a.setText(seq.name);
      return true;
    } else {
      super.handleEvent(e);
    }
    return super.handleEvent(e);
  }
  
  public void applyCommand() {
    if (seq != null) {
      if (parent instanceof AlignFrame) {
	try {
	  AlignFrame af = (AlignFrame)parent;
	  AlignFrame.fetchPDBStructure(seq,((AlignFrame)parent).srsServer);
	} catch (UnknownHostException e) {

	  System.out.print("\07");
	  System.out.flush();

	  status.setText("ERROR: host can't be contacted");
	  status.validate();
	  System.out.println(e);
	}  catch (IOException e) {
	  System.out.print("\07");
	  System.out.flush();

	  status.setText("ERROR: IO exception in fetching pdb entry");
	  status.validate();
	  System.out.println(e);
	}
      } else {
	try {
	  AlignFrame.fetchPDBStructure(seq,"srs.ebi.ac.uk/srs5bin/cgi-bin/");
	} catch (IOException e) {
	  System.out.println("Exception in fetching pdb structure " + e);
	}
      }

    } else {
      sequenceLabel.setText("No sequence found");
    }
  }

}





