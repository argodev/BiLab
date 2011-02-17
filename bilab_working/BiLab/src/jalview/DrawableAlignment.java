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

public class DrawableAlignment extends Alignment {
  DrawableSequence[] ds;
  public ScoreSequence qualityScore; //-DJ made public
  boolean autoConsensus = false;
  
  public DrawableAlignment(DrawableSequence[] ds){
    super(ds);
    this.ds = ds;
  }
  
  public void getFeatures(String fids, TextArea ta, String server, String database) {
    try {
      ta.setText("Querying srs server " + server + " and database " + database);
      FileParse fp = new FileParse("http://" + server + "wgetz?-e+[" + database + "-id:" + fids + "]","URL");
      fp.readLines();
      int ii=0;
      // Split the line array into chunks
      while (ii < fp.lineArray.size()) {
        String tmp =  "";
        // Read one entry
        while (ii < fp.lineArray.size() && ((String)fp.lineArray.elementAt(ii)).indexOf("//") != 0) {
          //  System.out.println(fp.lineArray.elementAt(ii));
          tmp = tmp + (String)fp.lineArray.elementAt(ii) + "\n";
          ii++;
        }
        tmp = tmp + "//";
        ta.setText("Reading features");
        SwissprotFile sp = new SwissprotFile(tmp);
        
        // Attach it to the right sequence
        if (!(sp.id.equals(""))) {
          ta.setText("Read features for " + sp.id);
          int j = 0;
          while (j < ds.length && ds[j] != null) {
            if (ds[j].name.equals(sp.id)) {
              ta.setText("Attaching features to sequence " + ds[j].name);
              String ungap = AlignSeq.extractChars(". -",ds[j].sequence);
              System.out.println(ungap);
              System.out.println(sp.sequence.sequence);
              System.out.println(ds[j].start + " " + ds[j].end);
              System.out.println(sp.sequence.sequence.indexOf(ungap));
              if (sp.sequence.sequence.indexOf(ungap) == -1) {
                System.out.println("ERROR: id " + sp.id + "sequence doesn't match alignment sequence");
              } else if (sp.sequence.sequence.indexOf(ungap) != (ds[j].start-1)) {
                System.out.println("Adjusting start end positions for " + sp.id);
                ds[j].start = sp.sequence.sequence.indexOf(ungap)+1;
                ds[j].end = ds[j].start + ds[j].sequence.length() - 1;
              }
              ds[j].features = sp.features;
              ds[j].pdbcode = sp.pdbcode;
            }
            j++;
          }
        }
        ii++;
      }
    } catch (java.io.IOException e) {
      System.out.println("Exception in fetching features " + e);
      ta.setText("ERROR: Exception in fetching features from " + server + " : " + e);
    }
  }
  
  
  public Vector getPDBCodes() {
    int i=0;
    Vector codes = new Vector();
    while (i < ds.length && ds[i] != null) {
      if (ds[i].pdbcode != null && ds[i].pdbcode.size() != 0) {
        for (int j=0; j < ds[i].pdbcode.size(); j++) {
          codes.addElement(ds[i].pdbcode.elementAt(j));
        }
      }
      i++;
    }
    return codes;
  }
  public void getFeatures(TextArea ta , String server, String database) {
    int i=0;
    String fids = "";
    while (i < sequences.length && sequences[i] != null) {
      if (ds[i].features == null) {
        String id = ds[i].name;
        if (id.indexOf("/") > 0) {
          id = id.substring(0,id.indexOf("/"));
        } 
        if (i%20 == 0) {
          
          fids = fids + id;
          if (fids.substring(fids.length()-1).equals("|")) {
            fids = fids.substring(0,fids.length()-1);
          }
          getFeatures(fids,ta,server,database);
          fids = "";
        } else {
          fids = fids + id + "|";
        }
      }
      i++;
    }
    // Just a little finishing off
    if (!fids.equals("")) {
      if (fids.substring(fids.length()-1).equals("|")) {
        fids = fids.substring(0,fids.length()-1);
      }
      getFeatures(fids,ta,server,database);
    }
    ta.setText("done");
  }
  
  public void getFeatures(TextArea ta,Vector sel,String server,String database) {
    for (int i=0; i < sel.size(); i++) {
      if (sel.elementAt(i) instanceof Sequence) {
        if ( ((Sequence)sel.elementAt(i)).features == null) {
          ((Sequence)sel.elementAt(i)).getFeatures(server,database);
          ta.setText("Fetched features for " + ds[i].name +" (" + (i+1)+"/"+sel.size()+")");
        }
      }
    }
  }
  public void getFeatures(Vector sel,String server,String database) {
    for (int i=0; i < sel.size(); i++) {
      if (sel.elementAt(i) instanceof Sequence) {
        if ( ((Sequence)sel.elementAt(i)).features == null) {
          ((Sequence)sel.elementAt(i)).getFeatures(server,database);
        }
      }
    }
  }
  public void getFeatures(String server, String database) {
    int i=0;
    while (i < sequences.length && sequences[i] != null) {
      if (ds[i].features == null) {
        ds[i].getFeatures(server,database);
      }
      i++;
    }
  }
  public int getHeight() {
    int i=0;
    int h =0;
    
    while (i < ds.length && ds[i] != null) {
      h += ds[i].charHeight;
      i++;
    }
    return h;
  }
  public int getHeight(int i, int j) {
    int h=0;
    while (i < j) {
      h += ds[i].charHeight;
      i++;
    }
    return h;
  }
  
  public int getHeight(int i) {
    int j = 0;
    int h = 0;
    while (j < i && j < ds.length && ds[j] != null) {
      if (j > 0) {
        h += ds[j-1].charHeight;
      }
      j++;
    }
    return h;
  }
  public void findQuality() {
    super.findQuality();
    System.out.println("Done quality");
    if (quality != null) {
      //      System.out.println("ick " + sequences[0].sequence);
      String tmp2 = new String(sequences[0].sequence);
      DrawableSequence tmp = new DrawableSequence("Quality",tmp2,1,maxLength);
      tmp.score[0] = quality;
      //ScoreSequence tmp3 = new ScoreSequence(tmp,0,255,100,100);
      ScoreSequence tmp3 = new ScoreSequence(tmp,0,(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
      
      //      addSequence(tmp3);
      this.qualityScore = tmp3;
    }
  }
  
  public int getHeight(DrawableSequence s) {
    int i = 0;
    int h =0;
    while (i < ds.length && ds[i] != null) {
      if (ds[i] != s) {
        h+= ds[i].charHeight;
      }
      i++;
    }
    return h;
  }
  public void addSequence(DrawableSequence[] s) {
    int oldlen = ds.length;
    DrawableSequence[] newds = new DrawableSequence[ds.length +s.length];
    int i=0;
    while (i < oldlen && ds[i] != null) {
      newds[i] = ds[i];
      i++;
    }
    oldlen = i;
    System.out.println(oldlen + " " + i + " " + s.length);
    while (i < newds.length && (i-oldlen) < s.length && s[i-oldlen] != null) {
      System.out.println(oldlen + " " + i + " " + s.length);
      newds[i] = s[i-oldlen];
      i++;
    }
    
    ds = newds;
    sequences = newds;
    size = size();
  }
  
  public void addSequence(DrawableSequence news) {
    DrawableSequence[] newds = new DrawableSequence[ds.length +1];
    int i=0;
    while (i < ds.length && ds[i] != null) {
      newds[i] = ds[i];
      i++;
    }
    newds[i] = news;
    ds = newds;
    sequences = newds;
    size = size();
  }
  
  public void sortByTree(TreeFile tf) {
    DrawableSequence[] newds = new DrawableSequence[ds.length];
    Vector tmp = new Vector();
    tmp = _sortByTree(tf.top,tmp);
    
    for (int i=0; i < tmp.size(); i++) {
      newds[i] = (DrawableSequence)tmp.elementAt(i);
    }
    sequences = newds;
    ds = newds;
  }    
  
  public Vector _sortByTree(SequenceNode node, Vector tmp) {
    if (node == null) {return tmp;}
    
    if (node.left == null && node.right == null) {
      if (node.element instanceof DrawableSequence) {
        tmp.addElement((DrawableSequence)node.element);
        return tmp;
      }
    } else {
      _sortByTree((SequenceNode)node.left,tmp);
      _sortByTree((SequenceNode)node.right,tmp);
    }
    return tmp;
  }
  
  public void sortByGroup() {
    DrawableSequence[] newds = new DrawableSequence[ds.length];
    int count = 0;
    for (int i=0; i < groups.size(); i++) {
      SequenceGroup sg = (SequenceGroup)groups.elementAt(i);
      
      for (int j = 0; j < sg.sequences.size(); j++) {
        newds[count] = (DrawableSequence)sg.sequences.elementAt(j);
        count++;
      }
      
    }
    ds = newds;
    sequences = newds;
  }
  
  public void trimLeft(int i) {
    super.trimLeft(i);
    for (int j=0;j<size;j++) {
      
      for (int k=0;k<i;k++) {
        if (ds[j].textColour.size() > k) {
          ds[j].textColour.removeElementAt(0);
        }
        if (ds[j].boxColour.size() > k) {
          ds[j].boxColour.removeElementAt(0);
        }
        int l=0;
        while (l < ds[j].score.length && ds[j].score[l] != null) {
          if (ds[j].score[l].size() > k) {
            ds[j].score[l].removeElementAt(0);
          }
          l++;
        }
      }
    }
  }
  public void deleteSequence(Sequence s) {
    for (int i=0; i < size; i++) {
      if (ds[i] == s) {
        deleteSequence(i);
      }
    }
  }
  
  public void  deleteSequence(int i) {
    for (int k=0; k < groups.size(); k++) {
      if (((SequenceGroup)groups.elementAt(k)).sequences.contains(sequences[i])) {
        ((SequenceGroup)groups.elementAt(k)).sequences.removeElement(sequences[i]);
      }
    }
    for (int j = i+1; j < size(); j++ ) {
      sequences[j-1] = sequences[j];
      ds[j-1] = ds[j];
    }
    ds[size-1] = null;
    sequences[size-1] = null;
    size = size();
  }
  
  
  public void removeGappedColumns() {
    Vector v = new Vector(maxLength());
    for (int i=0; i < maxLength(); i++) {
      boolean gap = true;
      for (int j=0; j < ds.length; j++) {
        if (ds[j] != null) {
          String tmp = ds[j].sequence.substring(i,i+1);
          if (!(tmp.equals("-") || tmp.equals(".") ||tmp.equals(" "))) {
            gap = false;
            break;
          }
        }
      }
      if (gap) {
        v.addElement("0");
      } else {
        v.addElement("1");
      }
    }
    for (int j = 0; j < v.size(); j++) {
      if ((String)v.elementAt(j) == "0") {
        //System.out.println("Deleting column blah " + j);
        deleteColumns(j,j);
        v.removeElementAt(j);
        j--;
      }
    }
  }
  public void deleteColumns(int start, int end) {
    super.deleteColumns(start,end);
    int i=0;
    System.out.println("*********** in deletreColumns ***********");
    while (i<ds.length && ds[i] != null) {
      for (int j = start; j <= end; j++) {
        // 	System.out.println("j = " + j + " " + i + " " + ds.length);
        // 	      if (i < ds.length && ds[i] != null) {
        // 	        if (ds[i].boxColour.size() > start) {
        // 		  System.out.println(ds[i].boxColour.elementAt(start));
        // 	         ds[i].boxColour.removeElementAt(start);
        // 		 System.out.println("Removign box color at " + start);
        // 	        }
        //         }
        
        int l=0;
        while (ds[i].score != null && l < ds[i].score.length && ds[i].score[l] != null) {
          if (ds[i].score[l].size() > start) {
            ds[i].score[l].removeElementAt(start);
          }
          l++;
        }
      }
      
      i++;
    }
  }
  public void colourText(SequenceGroup sg) {
    for (int j = 0; j < sg.sequences.size(); j++) {
      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(j);
      s.colourText = sg.colourText;
    }
  }
  public void displayText(SequenceGroup sg) {
    for (int j = 0; j < sg.sequences.size(); j++) {
      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(j);
      s.displayText = sg.displayText;
    }
  }
  public void displayBoxes(SequenceGroup sg) {
    for (int j = 0; j < sg.sequences.size(); j++) {
      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(j);
      s.displayBoxes = sg.displayBoxes;
    }
  }
  
  public void setColourScheme(SequenceGroup sg) {
    sg.colourScheme.setColours(sg);
  }
  
  public void setColourScheme(ColourScheme colourScheme) {
    for (int i=0 ; i < groups.size(); i++) {
      SequenceGroup sg = (SequenceGroup)groups.elementAt(i);
      sg.colourScheme = colourScheme;
      System.out.println("Setting colour scheme for " + i);
      sg.colourScheme.setColours(sg);
    }
  }
  public int maxIdLength(FontMetrics fm) {
    int i=0;
    int max = 0;
    while(i < sequences.length && sequences[i] != null) {
      Sequence s = sequences[i];
      String tmp = s.getName() + "/" + s.start + "-" + s.end;
      if (fm.stringWidth(tmp ) > max) {
        max = fm.stringWidth(tmp);
      }
      i++;
    }
    return max;
  }
  
  
  
  public void trimRight(int i) {
    super.trimRight(i);
    for (int j=0;j<size;j++) {
      for (int k=i+1;k<ds[j].sequence.length();k++) {
        if (ds[j].textColour.size() > (i+1)) {
          ds[j].textColour.removeElementAt(i+1);
        }
        if (ds[j].boxColour.size() > (i+1)) {
          ds[j].boxColour.removeElementAt(i+1);
        }
        int l=0;
        while (l < ds[j].score.length && ds[j].score[l] != null) {
          if (ds[j].score[l].size() > (i+1)) {
            ds[j].score[l].removeElementAt(i+1);
          }
          l++;
        }
      }
    }
  }
  
  
  
}
















