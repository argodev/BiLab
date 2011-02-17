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

public class GroupPopup extends Popup {
  java.io.PrintStream o = System.out;
  List groupList;
  Label groupLabel;

  List memberList;
  Label memberLabel;
  
  List ungroupedList;
  Label ungroupedLabel;

  Button add;
  Button del;
  Button addAll;
  Button delAll;

  Label groupProperties;
  
  Choice colourScheme;
  Label colourSchemeLabel;
  Checkbox boxes;
  Checkbox text;
  Checkbox colourText;
  Checkbox display;

  Button addGroup;
  Button delGroup;
  Button addSelected;
  
  DrawableAlignment da;

  public GroupPopup(Frame parent, String title, DrawableAlignment da) {
    super(parent,title);

    this.da = da;

    groupList = new List(9,false);
    groupLabel = new Label("Groups");
    memberList = new List(9,false);
    memberList.setMultipleSelections(true);
    memberLabel = new Label("Group members");
    ungroupedList = new List(9,false);
    ungroupedList.setMultipleSelections(true);
    ungroupedLabel = new Label("Ungrouped sequences");

    add = new Button("<- Add");
    del = new Button("Delete ->");
    addAll = new Button("<- Add all");
    delAll = new Button("Delete all ->");

    groupProperties = new Label("Group properties");
    
    colourScheme = new Choice();
    for (int i=0; i < ColourProperties.colourSchemes.size(); i++) {
      colourScheme.addItem((String)ColourProperties.colourSchemes.elementAt(i));
    }
    colourSchemeLabel = new Label("Colour scheme");

    boxes = new Checkbox("Display boxes");
    boxes.setState(true);
    text = new Checkbox("Display text");
    text.setState(true);
    colourText = new Checkbox("Colour text");
    colourText.setState(false);
    display = new Checkbox("Display group");
    display.setState(true);

    addGroup = new Button("Add new group");
    delGroup = new Button("Delete selected group");
    addSelected = new Button("Add selected IDs");

    Panel listp = new Panel();
    listp.setLayout(new GridLayout(1,4,10,5));
    Panel adddel = new Panel();
    adddel.setLayout(new GridLayout(4,1,5,5));
    Panel props = new Panel();
    props.setLayout(new GridLayout(6,1,5,5));
    Panel gprops = new Panel();
    gprops.setLayout(new GridLayout(3,1,5,5));
    

    gbc.fill = GridBagConstraints.NONE; 
    gbc.insets = new Insets(2,2,2,2);

    listp.add(groupList);
    listp.add(memberList);


    adddel.add(add);
    adddel.add(del);
    adddel.add(addAll);
    adddel.add(delAll);

    listp.add(adddel);
    listp.add(ungroupedList);

    
    props.add(groupProperties);

    props.add(colourScheme);
    props.add(boxes);
    props.add(text);
    props.add(colourText);

    gprops.add(addGroup);
    gprops.add(delGroup);
    gprops.add(addSelected);


    add(listp,gb,gbc,0,0,2,3);
    add(props,gb,gbc,0,3,1,1);
    add(gprops,gb,gbc,1,3,1,1);

    add(apply,gb,gbc,0,4,1,1);
    add(close,gb,gbc,1,4,1,1);

    pack();
    show();

    listGroups();
    showUngrouped();

  }

  public void listGroups() {
    if (groupList.countItems() > 0) {
      groupList.clear();
    }

    Vector g = da.groups;
      
    for (int i = 0;i < g.size(); i++) {
      SequenceGroup sg = (SequenceGroup)g.elementAt(i);
      groupList.addItem(i + " (" + sg.sequences.size() + " sequences)");
    }
    
  }

  public void showUngrouped() {
    if (ungroupedList.countItems() > 0) {
      ungroupedList.clear();
    }
    for (int i = 0; i < da.size;i++) {
	Sequence s = da.sequences[i];
	if (da.findGroup(s) == null) {
	  ungroupedList.addItem(s.name);
	}
    }
  }
  
  public void showGroup(SequenceGroup sg) {
    
    if (memberList.countItems() > 0) {
      memberList.clear();
    }
    for (int j = 0; j < sg.sequences.size(); j++) {
      Sequence s = (Sequence)sg.sequences.elementAt(j);
      memberList.addItem(s.name);
    }
    displayProperties(sg);
  }

  public void setProperties(SequenceGroup sg) {
    int schemeno = colourScheme.getSelectedIndex();
    sg.colourScheme = ColourAdapter.get(schemeno);
    
    if (sg.colourScheme instanceof ResidueColourScheme) {
      System.out.println("setting consensus to " + da.cons);
      ((ResidueColourScheme)sg.colourScheme).cons = da.cons;
    }

    da.setColourScheme(sg);

    sg.displayBoxes = boxes.getState();
    sg.displayText = text.getState();
    sg.colourText = colourText.getState();
    da.displayBoxes(sg);
    da.displayText(sg);
    da.colourText(sg);

  }
  public void displayProperties(SequenceGroup sg) {
    int num  = ColourAdapter.get(sg.colourScheme);
    String name = (String)ColourProperties.colourSchemes.elementAt(num);
    colourScheme.select(name);

    boxes.setState(sg.displayBoxes);
    text.setState(sg.displayText);
    colourText.setState(sg.colourText);
      
  }
  
  
  public boolean handleEvent(Event e) {
    if (e.target == groupList && e.id == 701) {
      System.out.println(e);
      Vector g = da.groups;
      if (groupList.getSelectedIndex() >= 0) {
	showGroup((SequenceGroup)g.elementAt(groupList.getSelectedIndex()));
      }
      return true;
    } else if (e.target == addGroup && e.id == 1001) {
      SequenceGroup sg = da.addGroup();
      setProperties(sg);
      da.setColourScheme(sg);
      listGroups();
      groupList.select(da.groups.size()-1);
      
      if (memberList.countItems() > 0) {
	memberList.clear();
      }
      return true;
    } else if (e.target == delGroup && e.id == 1001) {
      delGroup.requestFocus();
      int i = groupList.getSelectedIndex();
      if (i >= 0) {
	da.deleteGroup((SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex()));
	listGroups();
	if (memberList.countItems() > 0) {
	  memberList.clear();
	}
	showUngrouped();
      }
      return true;
    } else if (e.target == add && e.id == 1001) {
      add.requestFocus();
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	String[] selseqs = ungroupedList.getSelectedItems();
	for (int i = 0; i < selseqs.length; i++) {
	  da.addToGroup(sg,da.findName(selseqs[i]));
	}
	listGroups();
	groupList.select(sel);
	showGroup(sg);
	showUngrouped();
      }
      return true;
    } else if (e.target == addAll && e.id == 1001) {
      addAll.requestFocus();
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	     
	for (int i = 0; i < ungroupedList.countItems(); i++ ) {
	  da.addToGroup(sg,da.findName(ungroupedList.getItem(i)));
	}
	listGroups();
	groupList.select(sel);
	showGroup(sg);
	showUngrouped();
      }
      return true;
    } else if (e.target == delAll && e.id == 1001) {
      delAll.requestFocus();
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	
	for (int i = 0; i < memberList.countItems(); i++ ) {
	    da.removeFromGroup(sg,da.findName(memberList.getItem(i)));
	}
	listGroups();
	groupList.select(sel);
	showGroup(sg);
	showUngrouped();
      }
      return true;
    } else if (e.target == del && e.id == 1001) {
      del.requestFocus();
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());

	if (memberList.getSelectedItems() != null) {
	  String[] selseqs = memberList.getSelectedItems();
	  for (int i = 0; i < selseqs.length; i++) {
	    da.removeFromGroup(sg,da.findName(selseqs[i]));
	  }
	}
	listGroups();
	groupList.select(sel);
	showGroup(sg);
	showUngrouped();
      }
      return true;
    } else if (e.target == addSelected && e.id == 1001) {
      addSelected.requestFocus();
      if (parent instanceof AlignFrame) {
	int sel = groupList.getSelectedIndex();
	if (sel >= 0) {
	  SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	  Vector selseqs = ((AlignFrame)parent).ap.sel;
	  int i = 0;
	  while (i < selseqs.size()) {
	    SequenceGroup found = da.findGroup((Sequence)selseqs.elementAt(i));
	    da.removeFromGroup(found,(Sequence)selseqs.elementAt(i));
	    if (found.sequences.size() == 0) {
	      da.groups.removeElement(found);
	    }
	    
	    da.addToGroup(sg,(Sequence)selseqs.elementAt(i));
	    i++;
	  }
	  listGroups();
	  groupList.select(sel);
	  showGroup(sg);
	  showUngrouped();
	}
	
      }
      return true;

    } else if (e.target == boxes) {
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	  SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	  sg.displayBoxes = boxes.getState();
	  da.displayBoxes(sg);
      }
      return true;
    } else if (e.target == text) {
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	sg.displayText = text.getState();
	da.displayText(sg);
      }
      return true;
    } else if (e.target == colourText) {
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());
	sg.colourText = colourText.getState();
	da.colourText(sg);
      }
      return true;
    } else if (e.target == colourScheme) {
      int sel = groupList.getSelectedIndex();
      if (sel >= 0) {
	SequenceGroup sg = (SequenceGroup)da.groups.elementAt(groupList.getSelectedIndex());

	int schemeno = colourScheme.getSelectedIndex();
	sg.colourScheme = ColourAdapter.get(schemeno);

	if (sg.colourScheme instanceof ResidueColourScheme) {
	  System.out.println("setting consensus to " + da.cons);
	  ((ResidueColourScheme)sg.colourScheme).cons = da.cons;
	}
	
	da.setColourScheme(sg);
      }
      return true;
    } else if (e.target == apply && e.id == 1001) {
      applyCommand();
    } else {
      super.handleEvent(e);
    }
    return super.handleEvent(e);
  }
  
  public void applyCommand() {
    if (parent instanceof AlignFrame) { 
      AlignFrame af = (AlignFrame)parent;
      ((AlignFrame)parent).updateFont();
    }
  }

}





