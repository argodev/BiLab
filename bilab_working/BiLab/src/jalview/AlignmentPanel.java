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
import java.util.*;

public class AlignmentPanel extends Panel {
  
//public ScorePanel scorePanel;
  public SeqPanel seqPanel;
  public IdPanel idPanel;
  public ScalePanel scalePanel;
  public Component parent;
  
  public Vector sel;
  public boolean[] selected;
  public Vector selectedColumns;
  public boolean groupEdit = false;
  
  public Hashtable[] cons;
  boolean conservation = false;
  public Color[] color = ResidueProperties.color; //-DJ made public
  public int scaleheight = 20;
  
  public AlignmentPanel(Component p ,DrawableSequence[] s) {
    this.parent = p;
    idPanel = new IdPanel(this);
    //    this.seqPanel = new SeqPanelLite(this,s);
    this.seqPanel = new SeqPanel(this,s);
    scalePanel = new ScalePanel(this);
    sel = new Vector();
    selected = new boolean[seqPanel.align.size];
    selectedColumns = new Vector();
    componentInit();
    System.out.println("Loaded AlignmentPanel");
  }
  public int countSelected() {
    int count = 0;
    int i = 0;
    while (i < selected.length) {
      if (selected[i]) {
        count++;
      }
      i++;
    }
    //    return count;
    return sel.size();
  }
  public void componentInit() {
    setLayout(null);
    add(idPanel);
    add(scalePanel);
    add(seqPanel);
    selectAll(false);
    System.out.println("Finished AlignmentPanel.componentInit");
  }
  
  public void selectAll(boolean flag) {
    int i = 0;
    if (flag) {
      while (i < seqPanel.align.size) {
        if (! sel.contains(seqPanel.align.sequences[i])) {
          sel.addElement(seqPanel.align.sequences[i]);
        }
        i++;
      }
    } else {
      sel = null;
      sel = new Vector();
    }
  }
  public void update(Graphics g) {
    paint(g);
  }
  public void oldreshape(int x, int y, int width, int height) {
    super.reshape(x,y,width,height);
  }
  public void reshape(int x, int y, int width, int height) {
    //Do the business stuff
    // System.out.println("Reshaping");
    super.reshape(x,y,width,height);
    
    int idw = idPanel.idCanvas.idWidth;
    
    scalePanel.reshape(0,0,width,scaleheight);
    idPanel.reshape(0,scaleheight,idw,height-scaleheight);
    seqPanel.reshape(idw,scaleheight,width-idw,height-scaleheight);
  }
  
  public void setSequenceColor(ColourScheme c) {
    int count = countSelected();
    if (count == 0) {
      if (cons == null) {
        System.out.println("Calculating consensus");
        seqPanel.align.percentIdentity();
        cons = seqPanel.align.cons;
      } else {
        cons = seqPanel.align.cons;
      }
      
      if (c instanceof ResidueColourScheme) {
        System.out.println("Setting consensus");
        ((ResidueColourScheme)c).cons = cons;
      }
      
      seqPanel.align.setColourScheme(c);
      
      for (int i = 0; i < seqPanel.align.groups.size();i++) {
        SequenceGroup sg = (SequenceGroup)seqPanel.align.groups.elementAt(i);
        
        sg.colourScheme = c;
        sg.color = color;
        
        if (conservation && sg.conserve != null) {
          ConservationColourScheme tmp = new ConservationColourScheme(sg);
          sg.colourScheme = tmp;
        }
        sg.colourScheme.setColours(sg);
      }
    } else {
      
      SequenceGroup sg = seqPanel.align.addGroup();
      sg.colourScheme = c;
      sg.color = color;
      
      seqPanel.align.setColourScheme(sg);
      
      for (int i=0; i < sel.size(); i++) {
        seqPanel.align.removeFromGroup(seqPanel.align.findGroup((Sequence)sel.elementAt(i)),(Sequence)sel.elementAt(i));
        seqPanel.align.addToGroup(sg,(Sequence)sel.elementAt(i));
      }
      
      if (conservation && sg.conserve != null) {
        sg.colourScheme = new ConservationColourScheme(sg);
      }
      
      sg.colourScheme.setColours(sg);
      
    }
    
  }
  
  public void setSequenceColor(int start, int end) {
    for (int i = 0; i < seqPanel.align.groups.size();i++) {
      SequenceGroup sg = (SequenceGroup)seqPanel.align.groups.elementAt(i);
      for (int k=0; k < sg.sequences.size(); k++) {
        DrawableSequence ds = (DrawableSequence)sg.sequences.elementAt(k);
        for (int j=start; j<= end; j++) {
          sg.colourScheme.setColours(ds,j);
        }
      }
    }
  }
  
  public void setSequenceColor(DrawableSequence seq,int start, int end) {
    SequenceGroup sg = seqPanel.align.findGroup(seq);
    for (int j=start; j<= end; j++) {
      sg.colourScheme.setColours(seq,j);
    }
  }
  
  public void setSequenceColor() {
    for (int i = 0; i < seqPanel.align.groups.size();i++) {
      SequenceGroup sg = (SequenceGroup)seqPanel.align.groups.elementAt(i);
      sg.colourScheme.setColours(sg);
    }
  }
  
  public void setSequenceColor(int threshold,ColourScheme scheme) {
    int count = countSelected();
    if (count == 0) {
      if (cons == null) {
        seqPanel.align.percentIdentity();
        cons = seqPanel.align.cons;
      }
      if (conservation) {
        for (int j=0; j < seqPanel.align.groups.size(); j++) {
          SequenceGroup sg =  ((SequenceGroup)seqPanel.align.groups.elementAt(j));
          if (sg.conserve != null) {
            sg.colourScheme = new ConservationColourScheme(sg);
            sg.colourScheme.setColours(sg);
          }
        }
      } else {
        for (int i = 0; i < seqPanel.align.groups.size();i++) {
          SequenceGroup sg = (SequenceGroup)seqPanel.align.groups.elementAt(i);
          if (sg.colourScheme instanceof ResidueColourScheme) {
            ((ResidueColourScheme)sg.colourScheme).colourThreshold = threshold;
            ((ResidueColourScheme)sg.colourScheme).cons = cons;
            sg.colourScheme.setColours(sg);
          }
        }
      }
      
    } else {
      SequenceGroup sg = seqPanel.align.addGroup();
      
      sg.colourScheme = scheme;
      
      for (int i=0; i < sel.size(); i++) {
        seqPanel.align.removeFromGroup(seqPanel.align.findGroup((Sequence)sel.elementAt(i)),(Sequence)sel.elementAt(i));
        seqPanel.align.addToGroup(sg,(Sequence)sel.elementAt(i));
      }
      
      if (conservation) {
        if (sg.colourScheme instanceof ResidueColourScheme) {
          ((ResidueColourScheme)sg.colourScheme).colourThreshold = threshold;
          ((ResidueColourScheme)sg.colourScheme).cons = cons;
          if  (sg.conserve != null) {
            sg.colourScheme = new ConservationColourScheme(sg);
            sg.colourScheme.setColours(sg);
          }
        }
      } else {
        if (sg.colourScheme instanceof ResidueColourScheme) {
          ((ResidueColourScheme)sg.colourScheme).colourThreshold = threshold;
          ((ResidueColourScheme)sg.colourScheme).cons = cons;
          sg.colourScheme.setColours(sg);
        }
      }
      
      
    }
    
    
  }
  
  
  public static void main(String[] args) {
    Frame f = new Frame("SeqPanel");
    
    Sequence[] seq = FormatAdapter.read(args[0],"File","POSTAL");
    ScoreSequence[] s = new ScoreSequence[seq.length];
    for (int i=0;i < seq.length;i++) {
      s[i] = new ScoreSequence(seq[i]);
    }
    DrawableSequence[] s1 = new DrawableSequence[seq.length];
    for (int i=0;i < seq.length;i++) {
      s1[i] = new DrawableSequence(seq[i]);
    }
    
    AlignmentPanel ap = new AlignmentPanel(null,s1);
    ScorePanel sp = new ScorePanel(null,s);
    //   ap.setScorePanel(sp);
    f.setLayout(new BorderLayout());
    f.add("Center",ap);
    f.resize(700,500);
    f.show();
  }
}





