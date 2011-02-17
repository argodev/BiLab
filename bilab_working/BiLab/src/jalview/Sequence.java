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

import java.util.Vector;

public class Sequence {
  public String name;
  public String sequence;
  public int[] num;
  public int start;
  public int end;
  String desc;
  Vector features;
  Vector[] score;
  Vector pdbcode;
  SwissprotFile sp;
  
  public Sequence(Sequence s) {
    
    this.name = s.name;
    this.sequence = new String(s.getSequence());
    this.num = new int[sequence.length()];
    this.start = s.start;
    this.end = s.end;
    this.score = s.score;
    num = setNums(s.getSequence());
  }
  
  public Sequence(String name, String sequence, int start, int end) {
    this.name = name;
    this.sequence = sequence;
    this.num = new int[sequence.length()];
    this.start = start;
    this.end = end;
    this.score = new Vector[10];
    num = setNums(sequence);
  }
  
  public static int[] setNums(String s) {
    int[] out = new int[s.length()];
    int i=0;
    while (i < s.length()) {
      try {
        out[i] = ((Integer)ResidueProperties.aaHash.get(s.substring(i,i+1))).intValue();
      } catch (Exception e) {
        System.out.println("Exception in Sequence:setNums " + i + " " + s.substring(i,i+1));
        out[i] = 23;
      }
      i++;
    }
    return out;
  }
  public void setScore(int i, float s,int num) {
    if (num < 10 && score[num] == null) {
      score[num] = new Vector();
    } else {
      System.out.println("ERROR: maximum number of scores = 10 " + num);
    }
    if (score[num].size() <= i) {
      for(int j= score[num].size(); j <= i; j++) {
        score[num].addElement(null);
      }
    }
    score[num].setElementAt(new Double(s),i);
  }
  
  
  public void getFeatures(String server, String database) {
    System.out.println("ark");
    if (features == null) {
      try {
        String id = name;
        
        if (id.indexOf("/") > 0) {
          id = name.substring(0,id.indexOf("/"));
        }
        
        sp = new SwissprotFile("http://" + server + "wgetz?-e+[" + database + "-id:" + id + "]","URL");
        if (sp == null || sp.sequence == null) {
          sp = new SwissprotFile("http://" + server + "wgetz?-e+[" + database + "-acc:" + id + "]","URL");
        }
        if (sp != null && sp.sequence != null) {
          String ungap = AlignSeq.extractChars(". -",sequence);
          System.out.println(ungap);
          System.out.println(sp.sequence.sequence);
          System.out.println(start + " " + end);
          System.out.println(sp.sequence.sequence.indexOf(ungap));
          if (sp.sequence.sequence.indexOf(ungap) != (start-1)) {
            start = sp.sequence.sequence.indexOf(ungap)+1;
            end = start + sequence.length() - 1;
          }
          if (!(sp.id.equals(""))) {
            System.out.println("Fetched features for " + name);
            this.features = sp.features;
            this.pdbcode = sp.pdbcode;
          }
        }
      } catch (java.io.IOException e) {
        System.out.println("Exception in fetching features " + e);
      }
    }
  }
  
  public int length() {
    return this.sequence.length();
  }
  public String getName() {
    return this.name;
  }
  public String getSequence() {
    return this.sequence;
  }
  public int  getStart() {
    return this.start;
  }
  public int getEnd() {
    return this.end;
  }
  public char charAt(int i) {
    if (i < sequence.length()) {
      return sequence.charAt(i);
    } else {
      return ' ';
    }
  }
  public int findIndex(int pos) {
    // returns the alignment position for a residue
    int j = start;
    int i = 0;
    while (i< sequence.length() && j <= end && j <= pos) {
      //  System.out.println("Here we are at " + i + " " + j);
      String s = sequence.substring(i,i+1);
      //System.out.println("String = " + s);
      if (!(s.equals(".") || s.equals("-") || s.equals(" "))) {
        j++;
      }
      i++;
    }
    if (j == end && j < pos) {
      // System.out.println("past end");
      return end+1;
    } else {
      // System.out.println("Not past end");
      return i;
    }
  }
  
  public int findPosition(int i) {
    // Returns the sequence position for an alignment position
    int j = 0;
    int pos = start;
    while (j < i) {
      String s = sequence.substring(j,j+1);
      if (!(s.equals(".") || s.equals("-") || s.equals(" "))) {
        pos++;
      }
      j++;
    }
    return pos;
  }
  public void deleteCharAt(int i,int n) {
    
    
    if (score[n].size() > i) {
      score[n].removeElementAt(i);
    }
  }
  public void insertCharAt(int i,char c, int n) {
    if (score[n] != null) {
      for (int j=score[n].size(); j < i; j++) {
        score[n].addElement(new Double(0));
      }
      score[n].insertElementAt(new Double(0),i);
    }
  }
  public void deleteCharAt(int i) {
    //    System.out.println(i + " " + sequence.length());
    if ((i+1) < sequence.length()) {
      sequence = sequence.substring(0,i) + sequence.substring(i+1);
    } else {
      sequence = sequence.substring(0,i);
    }
    
    //    System.out.println("Deleting char at " + i);
    
    
    
    
    int[] tmp = new int[num.length];
    for (int j = 0; j < i; j++) {
      tmp[j] = num[j];
    }
    
    for (int j=i+1;j < num.length; j++) {
      tmp[j-1] = num[j];
    }
    num[num.length-1] = 23;
    num = tmp;
    
    int j = 0;
    while (j < score.length && score[j] != null) {
      deleteCharAt(i,j);
      j++;
    }
  }
  public void insertCharAt(int i, char c) {
    insertCharAt(i,c,true);
  }
  public void insertCharAt(int i,char c,boolean chop) {
    //    System.out.println("Inserting char at " + i);
    // Insert the char into the sequence string
    String tmp = new String(sequence);
    if (i < sequence.length()) {
      sequence = tmp.substring(0,i) + String.valueOf(c) + tmp.substring(i);
    } else {
      sequence = tmp + String.valueOf(c);
    }
    // If this sequence had a gap at end don't increase the length
    int len = num.length+1;
    if (chop == true) {
      if (sequence.substring(sequence.length()-1).equals("-") ||
          sequence.substring(sequence.length()-1).equals(".") ||
          sequence.substring(sequence.length()-1).equals(" ")) {
        if (i < sequence.length()-1) {
          len = num.length;
          sequence = sequence.substring(0,sequence.length()-1);
        }
      }
    }
    int[]  newnum = new  int[len];
    int j = 0;
    
    for (j=0;j<i;j++) {
      newnum[j] = num[j];
    }
    
    try {
      newnum[j] =  ((Integer)ResidueProperties.aaHash.get(String.valueOf(c))).intValue();
    } catch (Exception e) {
      System.out.println("Exception in insertChar " + c);
      newnum[j] = 23;
    }
    for (j = i+1; j < len;j++) {
      newnum[j] = num[j-1];
    }
    num = newnum;
    j = 0;
    while (j < score.length && score[j] != null) {
      insertCharAt(i,c,j);
      j++;
    }
  }
  public void setScore(int i,float s) {
    int j = 0;
    while (j < score.length && score[j] != null) {
      setScore(i,s,j);
      j++;
    }
  }
}











