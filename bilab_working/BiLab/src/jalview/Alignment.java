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
import java.util.*;

public class Alignment {
  public Sequence[] sequences;
  public int[][] scores;
  public int maxLength;
  public int size;
  public Vector groups = new Vector();
  public Hashtable[] cons;
  public int[][] cons2;
  String gapCharacter = "-";
  public Vector quality;
  
  public Alignment(Sequence[] sequences) {
    this.sequences = sequences;
    maxLength = maxLength();
    size = size();
    
    groups.addElement(new SequenceGroup());
    ((SequenceGroup)groups.elementAt(0)).colourScheme = new PIDColourScheme();
    int i = 0;
    while (i < sequences.length && sequences[i] != null) {
      addToGroup((SequenceGroup)groups.elementAt(0),sequences[i]);
      i++;
    }
  }
  
  public void addSequence(Sequence snew) {
    Sequence[] s = new Sequence[sequences.length+1];
    int i=0;
    while (i < sequences.length && sequences[i] != null) {
      s[i] = sequences[i];
      i++;
    }
    s[i] = snew;
    size = size();
    maxLength = maxLength();
  }
  
  public void sortGroups() {
    float[] arr = new float[groups.size()];
    Object[] s = new Object[groups.size()];
    
    for (int i=0; i < groups.size(); i++) {
      arr[i] = ((SequenceGroup)groups.elementAt(i)).sequences.size();
      s[i] = groups.elementAt(i);
    }
    
    QuickSort.sort(arr,s);
    
    Vector newg = new Vector(groups.size());
    
    for (int i=groups.size()-1; i >= 0; i--) {
      newg.addElement(s[i]);
    }
    
    groups = newg;
  }
  
  public void removeGaps() {
    for (int i=0; i < maxLength();i++) {
      boolean flag = true;
      
      for (int j=0; j < size(); j++) {
        if (sequences[j].sequence.length() > i) {
          if (!(sequences[j].sequence.substring(i,i+1).equals("-")) && 
              !(sequences[j].sequence.substring(i,i+1).equals(".")) && 
              !(sequences[j].sequence.substring(i,i+1).equals(" "))) {
            flag = false;
          }
        }
      }
      if (flag == true) {
        System.out.println("Deleting column " + i);
        deleteColumns(i+1,i+1);
      }
    }
  } 
  
  public Sequence[] getColumns(int start, int end) {
    return getColumns(0,size()-1,start,end);
  }
  public void deleteColumns(int start, int end) {
    if (end > quality.size()) { end = quality.size();}
    for (int i=start; i <= end; i++) {
      quality.removeElementAt(start);
    }
    deleteColumns(0,size()-1,start,end);
  }
  
  public Hashtable[] removeArrayElement(Hashtable[] cons, int i) {
    if (cons != null && cons.length > i) {
      Hashtable[] newhash = new Hashtable[cons.length-1];
      for (int j = 0; j < i; j++) {
        newhash[j] = cons[j];
      } 
      
      for (int j=i+1;j < newhash.length; j++) {
        newhash[j] = cons[j+1];
      }
      return newhash;
    }
    return null;
  }
  
  public void removeIntArrayColumn(int[][] cons2, int start) {
    int length = maxLength();
    int cons2leng = cons2.length;
    System.out.println("cons2 length " + cons2leng + " " + length);
    for (int k=start; k < (cons2leng-1); k++) {
      cons2[k] = cons2[k+1];
    }
    
  }
  
  public void deleteColumns(int seq1, int seq2, int start, int end) {
    System.out.println("Deleting cols : " + start + " " + end);
    for (int i=0; i <= (end-start); i++) {
      System.out.println("Removing cons for element " + (start+i));
      cons = removeArrayElement(cons,start);
      removeIntArrayColumn(cons2,start);
      
      for (int j=seq1; j <= seq2; j++) {
        sequences[j].deleteCharAt(start);
      }
    }
    maxLength = maxLength();
  }
  
  public void insertColumns(Sequence[] seqs, int pos) {
    if (seqs.length == size()) {
      for (int i=0; i < size();i++) {
        String tmp = new String(sequences[i].getSequence());
        sequences[i].sequence = tmp.substring(0,pos) + seqs[i].getSequence() + tmp.substring(pos);     }
    }
    maxLength = maxLength();
  }
  
  public Sequence[] getColumns(int seq1, int seq2, int start, int end) {
    Sequence[] seqs = new Sequence[(seq2-seq1)+1];
    for (int i=seq1; i<= seq2; i++ ) {       
      seqs[i] = new Sequence(sequences[i].name,sequences[i].getSequence().substring(start,end),sequences[i].findPosition(start),sequences[i].findPosition(end));
    }
    return seqs;
  }
  public void trimLeft(int i) {
    for (int j = 0;j< size;j++) {
      Sequence s = sequences[j];
      int newstart = s.findPosition(i);
      s.start = newstart;
      s.sequence = s.sequence.substring(i);
      s.num = Sequence.setNums(s.sequence);
    }
    if (cons != null) {
      int length = cons.length;
      for (int k=0; k < (length-i); k++) {
        cons[k] = cons[i+k];
      }
    }
    if (cons2 != null) {
      int length = maxLength();
      for (int k=0; k < (length-i); k++) {
        cons2[k] = cons2[i+k];
      }
    }
    
    maxLength = maxLength();
    size = size();
  }
  public void  trimRight(int i) {
    for (int j = 0;j< size;j++) {
      Sequence s = sequences[j];
      int newend = s.findPosition(i);
      s.end = newend;
      s.sequence = s.sequence.substring(0,i+1);
    }    if (cons != null) {
      int length = cons.length;
      Hashtable[] tmp = new Hashtable[i+1];
      for (int k=0; k <= i; k++) {
        tmp[k] = cons[i];
      }
    }
    maxLength = maxLength();
    size = size();
  }
  
  public void deleteSequence(Sequence s) {
    for (int i=0; i < size; i++) {
      if (sequences[i] == s) {
        deleteSequence(i);
      }
    }
  }
  public void  deleteSequence(int i) {
    Sequence[] tmp  = new Sequence[size-1];
    for (int j = 0; j < i; j++) {
      tmp[j] = sequences[j];
    }
    for (int j = i+1; j < size; j++ ) {
      tmp[j-1] = sequences[j];
    }
    sequences = tmp;
    size = size();
  }
  public Vector removeRedundancy(float threshold, Vector sel) {
    // These are the sequences to be deleted
    Vector del = new Vector();
    // Loop over the selected sequences
    for (int i=1; i < sel.size(); i++) {
      for (int j = 0; j < i; j++) {
        // Only do the comparison if either have not been deleted
        if (!del.contains((Sequence)sel.elementAt(i)) || !del.contains((Sequence)sel.elementAt(j))) {
          float pid = compare((Sequence)sel.elementAt(j),(Sequence)sel.elementAt(i));
          //	  System.out.println(pid);
          if (pid >= threshold) {
            // Delete the shortest one
            if (((Sequence)sel.elementAt(j)).getSequence().length() > 
                ((Sequence)sel.elementAt(i)).getSequence().length()) {
              del.addElement(sel.elementAt(i));
              System.out.println("Deleting sequence " + ((Sequence)sel.elementAt(i)).getName());
            } else {
              del.addElement(sel.elementAt(i));
              System.out.println("Deleting sequence " + ((Sequence)sel.elementAt(i)).getName());
            }
          }
        }
      }
    }
    
    // Now delete the sequences
    for (int i=0; i < del.size(); i++) {
      System.out.println("Deleting sequence " + ((Sequence)del.elementAt(i)).getName());
      deleteSequence((Sequence)del.elementAt(i));
    }
    
    return del;
  }
  
  public static float compare(Sequence ii, Sequence jj) {
    String si = ii.getSequence();
    String sj = jj.getSequence();
    
    int ilen = si.length();
    int jlen = sj.length();
    
    
    if ( si.substring(ilen).equals("-") ||   si.substring(ilen).equals(".")  ||  si.substring(ilen).equals(" ")) {
      ilen--;
      while (si.substring(ilen,ilen+1).equals("-")  ||   si.substring(ilen,ilen+1).equals(".")  ||  si.substring(ilen,ilen+1).equals(" ")) {
        ilen--;
      }
    }
    
    if ( sj.substring(jlen).equals("-") ||   sj.substring(jlen).equals(".")  ||  sj.substring(jlen).equals(" ")) {
      jlen--;
      while (sj.substring(jlen,jlen+1).equals("-")  ||   sj.substring(jlen,jlen+1).equals(".")  ||  sj.substring(jlen,jlen+1).equals(" ")) {
        jlen--;
      }
    }
    
    int count = 0;
    int match = 0;
    float pid = -1;
    if (ilen > jlen) {
      for (int j = 0; j < jlen; j++) {
        if (si.substring(j,j+1).equals(sj.substring(j,j+1))) {
          match++;
        }
        count++;
      }
      pid = (float)match/(float)ilen * 100;
    } else {
      for (int j = 0; j < jlen; j++) {
        if (si.substring(j,j+1).equals(sj.substring(j,j+1))) {
          match++;
        }
        count++;
      }
      pid = (float)match/(float)jlen * 100;
    }
    
    return pid;
  }
  
  public static float PID(Sequence s1 , Sequence s2) {
    int res = 0;
    int len;
    
    if (s1.getSequence().length() > s2.getSequence().length()) {
      len = s1.getSequence().length();
    } else {
      len = s2.getSequence().length();
    }
    int bad = 0;
    for (int i=0; i < len; i++) {
      String str1 = "";
      String str2 = "";
      if (i < s1.getSequence().length()) {
        str1 = s1.getSequence().substring(i,i+1);
      } else {
        str1 = ".";
      }
      if (i < s2.getSequence().length()) {
        str2 = s2.getSequence().substring(i,i+1);
      } else {
        str2 = ".";
      }
      
      if (!(str1.equals(".") || str1.equals("-") || str1.equals(" ")) && !(str2.equals(".") || str2.equals("-") || str2.equals(" "))) {
        if (!str1.equals(str2)) {
          bad++;
        }
      }
    }
    return (float)100*(len-bad)/len;
  }
  
  public void sortByPID(Sequence s) {
    
    float scores[] = new float[size()];
    
    for (int i = 0; i < size(); i++) {
      scores[i] = compare(sequences[i],s);
    }
    QuickSort.sort(scores,0,scores.length-1,sequences);
    
    int len = 0;
    if (size()%2 == 0) {
      len = size()/2;
    } else {
      len = (size()+1)/2;
    }
    
    for (int i = 0; i < len; i++) {
      Sequence tmp = sequences[i];
      sequences[i] = sequences[size()-i-1];
      sequences[size()-i-1] = tmp;
    }
  }
  
  public void sortByID() {
    String ids[] = new String[size()];
    
    for (int i = 0; i < size(); i++) {
      ids[i] = sequences[i].name;
    }
    
    QuickSort.sort(ids,sequences);
    
    int len = 0;
    if (size()%2 == 0) {
      len = size()/2;
    } else {
      len = (size()+1)/2;
    }
    for (int i = 0; i < len; i++) {
      Sequence tmp = sequences[i];
      sequences[i] = sequences[size()-i-1];
      sequences[size()-i-1] = tmp;
    }
  }
  
  public SequenceGroup findGroup(int i) {
    return findGroup(sequences[i]);
  }
  
  public SequenceGroup findGroup(Sequence s) {
    for (int i = 0; i < this.groups.size();i++) {
      SequenceGroup sg = (SequenceGroup)groups.elementAt(i);
      if (sg.sequences.contains(s)) {
        return sg;
      } 
    }
    return null;
    
  }
  public void addToGroup(SequenceGroup g, Sequence s) {
    if (!(g.sequences.contains(s))) {
      g.sequences.addElement(s);
    }
  }
  public void removeFromGroup(SequenceGroup g,Sequence s) {
    if (g != null && g.sequences != null) {
      if (g.sequences.contains(s)) {
        g.sequences.removeElement(s);
        if (g.sequences.size() == 0) {
          groups.removeElement(g);
        }
      }
    }
  }
  
  public void addGroup(SequenceGroup sg) {
    groups.addElement(sg);
  }
  public SequenceGroup addGroup() {
    SequenceGroup sg = new SequenceGroup();
    groups.addElement(sg);
    return sg;
  }
  
  public void deleteGroup(SequenceGroup g) {
    if (groups.contains(g)) {
      groups.removeElement(g);
    }
  }
  
  public Sequence findName(String name) {
    int i = 0;
    while (i < sequences.length && sequences[i] != null) {
      Sequence s = sequences[i];
      if (s.name.equals(name)) {
        return s;
      }
      i++;
    }
    return null;
  }
  
  public int findIndex(Sequence s) {
    int i=0;
    while (i < sequences.length && sequences[i] != null) {
      if (s == sequences[i]) {
        return i;
      }
      i++;
    }
    return -1;
  }
  public int size() {
    int i = 0;
    while (i < sequences.length && sequences[i] != null) {
      i++;
    }
    return i;
  }
  
  
  public int maxLength() {
    maxLength = -1;
    for (int i = 0; i < sequences.length; i++) {
      if ((sequences[i] != null) && (sequences[i].length() > maxLength) ) {
        maxLength = sequences[i].sequence.length();
      }
    }
    this.maxLength = maxLength;
    for (int i = 0; i < sequences.length; i++) {
      if (sequences[i] != null ) {
        for (int j = sequences[i].sequence.length(); j < maxLength; j++) {
          sequences[i].insertCharAt(j,gapCharacter.charAt(0));
        }
        
      }
    }
    
    return maxLength;
  }
  
  public int maxIdLength() {
    int max = 0;
    int i = 0;
    while (i < sequences.length && sequences[i] != null) {
      String tmp = sequences[i].getName() + "/" + sequences[i].start + "-" + sequences[i].end;
      if (tmp.length() > max) { 
        max = tmp.length();
      }
      i++;
    }
    return max;
  }
  
  public void  percentIdentity(Vector sel) {
    cons = new Hashtable[maxLength()];
    cons2 = new int[maxLength()][24];
    System.out.println("Maxlength " + maxLength());
    System.out.println(System.currentTimeMillis());
    percentIdentity(0,maxLength,sel);
    System.out.println(System.currentTimeMillis());
    //    percentIdentity2(0,maxLength,sel);
    System.out.println(System.currentTimeMillis());
  }
  
  
  public void percentIdentity2(int start, int end, Vector sel) {
    // Initialize the array
    for (int j=0;j<24;j++) {
      for (int i=start; i < end;i++) {
        cons2[i][j] = 0;
      }
    }
    
    
    for (int i = start; i < end; i++) {
      int j = 0;
      while(j < sequences.length && sequences[j] != null) {
        if (sel.contains(sequences[j])) {
          cons2[i][sequences[j].num[i]]++;
          //	  System.out.println(j + " " + i + " " + cons2[sequences[j].num[i]] + " " +  cons2[sequences[j].num[i]][i]); 
        }
        j++;
      }
    }
    for (int i=start; i < end; i++) {
      int max = -1000;
      int maxi = -1;
      int maxj = -1;
      for (int j=0;j<24;j++) {
        if (cons2[i][j] > max) {
          max = cons2[i][j];
          maxi = i;
          maxj = j;
        }
        
      }
      //      System.out.println("Max = " + max + " " + maxi + " " + maxj + " " + ResidueProperties.aa[maxj]);
    }
  }
  public void percentIdentity2() {
    percentIdentity2(0,maxLength()-1);
  }
  public void percentIdentity2(int start, int end) {
    cons2 = new int[maxLength()][24];
    // Initialize the array
    for (int j=0;j<24;j++) {
      for (int i=0; i < maxLength();i++) {
        cons2[i][j] = 0;
      }
    }
    
    for (int i = start; i <= end; i++) {
      int j = 0;
      while(j < sequences.length && sequences[j] != null) {
        cons2[i][sequences[j].num[i]]++;
        //System.out.println(j + " " + i + " " + cons2[sequences[j].num[i]] + " " +  cons2[sequences[j].num[i]][i]); 
        j++;
      }
    }
    for (int i=start; i <= end; i++) {
      int max = -1000;
      int maxi = -1;
      int maxj = -1;
      for (int j=0;j<24;j++) {
        //System.out.println(i + " " + j + " " + cons2[j][i]);
        if (cons2[i][j] > max) {
          max = cons2[i][j];
          maxi = i;
          maxj = j;
        }
        
      }
      //      System.out.println("Max = " + max + " " + maxi + " " + maxj + " " + ResidueProperties.aa[maxj]);
    }
  }
  public void percentIdentity(int start, int end, Vector sel) {
    //loop over the columns
    for (int i = start; i < end; i++) {
      int noRes = 0;
      if ( i < cons.length) {
        
        cons[i] = new Hashtable();
        
        int j = 0;
        while(j < sequences.length && sequences[j] != null) {
          if (sel.contains(sequences[j])) {
            String s = sequences[j].getSequence();
            String res = "";
            noRes++;
            
            if (s.length() > i+1) {
              res = s.substring(i,i+1);
            } else if (s.length() == i+1) {
              res = s.substring(i);
            }
            
            if (cons[i].get(res) != null) {
              int count = ((Integer)cons[i].get(res)).intValue() +1;
              cons[i].put(res,new Integer(count));
            } else {
              cons[i].put(res,new Integer(1));
            }
          }
          j++;
          
        }
        
        Enumeration e = cons[i].keys();
        
        String max = "";
        double maxno = 0.0;
        
        while(e.hasMoreElements()) {
          String key = (String)e.nextElement();
          double tmp = ((Integer)cons[i].get(key)).floatValue();
          
          if (tmp > maxno) {maxno = tmp; max = key;}
          
          double percent = tmp*100.0/(double)noRes;
          cons[i].put(key,new Double(percent));
          //	System.out.println("Key = " + key + " percent " + percent + " nores " + noRes);
        }
        
        cons[i].put("max",max);
      }
    }
    
  }
  public void  percentIdentity() {
    percentIdentity2();
    findQuality();
    cons = new Hashtable[maxLength()];
    
    //loop over the columns
    for (int i = 0; i < maxLength; i++) {
      
      int noRes = 0;
      cons[i] = new Hashtable();
      
      int j = 0;
      while(j < sequences.length && sequences[j] != null) {
        String s = sequences[j].getSequence();
        String res = "";
        
        noRes++;
        
        if (s.length() > i+1) {
          res = s.substring(i,i+1);
        } else if (s.length() == i+1) {
          res = s.substring(i);
        }
        
        if (cons[i].get(res) != null) {
          int count = ((Integer)cons[i].get(res)).intValue() +1;
          cons[i].put(res,new Integer(count));
        } else {
          cons[i].put(res,new Integer(1));
        }
        j++;
      }
      
      Enumeration e = cons[i].keys();
      
      String max = "";
      double maxno = 0.0;
      
      while(e.hasMoreElements()) {
        String key = (String)e.nextElement();
        double tmp = ((Integer)cons[i].get(key)).floatValue();
        
        if (tmp > maxno) {maxno = tmp; max = key;}
        
        double percent = tmp*100.0/(double)noRes;
        cons[i].put(key,new Double(percent));
        //	System.out.println("Key = " + key + " percent " + percent + " nores " + noRes);
      }
      
      cons[i].put("max",max);
      
    }
  }
  public void findQuality() {
    findQuality(0,maxLength-1);
  }
  
  public void findQuality(int start, int end) {
    quality = new Vector();
    double max = -10000;
    String s = "";
    //Loop over columns
    //    long ts = System.currentTimeMillis();
    //long te = System.currentTimeMillis();
    size = size();
    for (int j=start; j <= end; j++) {
      double bigtot = 0;
      
      // First Xr = depends on column only
      double x[] = new double[24];
      
      for (int ii=0; ii < 24; ii++) {
        x[ii] = 0;
        try {
          for (int i2=0; i2 < 24; i2++) {
            x[ii]  += (double)cons2[j][i2] * ResidueProperties.BLOSUM62[ii][i2]+4;
          }
        } catch (Exception e) {
          System.out.println("Exception : "  + e);
        }
        //System.out.println("X " + ii + " " + x[ii]);
        x[ii] /= (size);
        //System.out.println("X " + ii + " " + x[ii]);
      }
      
      // Now calculate D for each position and sum
      for (int k=0; k < size; k++) {
        double tot = 0;
        double[] xx = new double[24];
        // This is a loop over r
        for (int i=0; i < 23; i++) {
          double sr = 0;
          try {
            sr = (double)ResidueProperties.BLOSUM62[i][sequences[k].num[j]]+4;
          } catch (Exception e) {
            System.out.println("Exception in sr " + e);
          }
          //Calculate X with another loop over residues
          
          //  System.out.println("Xi " + i + " " + x[i] + " " + sr);	
          xx[i] = x[i] - sr;
          
          tot += xx[i]*xx[i];
        }
        bigtot += Math.sqrt(tot);
      }
      // This is the quality for one column
      if (max < bigtot) {max = bigtot;}
      //      bigtot  = bigtot * (size-cons2[j][23])/size;
      
      quality.addElement(new Double(bigtot));
      
      s += "-";
      
      // Need to normalize by gaps
    }
    for (int j=start; j <= end; j++) {
      double tmp =  ((Double)quality.elementAt(j)).doubleValue();
      tmp = (max - tmp)*(size-cons2[j][23])/size;
      //     System.out.println(tmp+ " " + j);
      quality.setElementAt(new Double(tmp),j);
    }
    //    System.out.println("Quality " + s);
  }
  
}



