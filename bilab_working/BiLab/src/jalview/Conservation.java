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
import java.applet.Applet;
import java.util.*;
import java.net.*;
import java.io.*;

public class Conservation {
  Vector sequences;
  int start;
  int end;
  Vector total = new Vector();
  String consString = "";
  DrawableSequence consSequence;
  Hashtable propHash;
  int threshold;
  Hashtable[] freqs;
  String name = "";

  public Conservation(String name,Hashtable[] freqs,Hashtable propHash, int threshold, Vector sequences, int start, int end) {
    this.name = name;
    this.freqs = freqs;
    this.propHash = propHash;
    this.threshold = threshold;
    this.sequences = sequences;
    this.start = start;
    this.end = end;
  }

  
  public void  calculate() {

    for (int i = start;i <= end; i++) {
      Hashtable resultHash = null;
      resultHash = new Hashtable();

      Hashtable residueHash = null;
      residueHash = new Hashtable();

      for (int j=0; j < sequences.size(); j++) {
	if (sequences.elementAt(j) instanceof Sequence) {
	  Sequence s = (Sequence)sequences.elementAt(j);
	  if (s.getSequence().length() > i) {
	    String res = s.getSequence().substring(i,i+1);
	    
	    if (residueHash.containsKey(res)) {
	      int count = ((Integer)residueHash.get(res)).intValue() ;
	      count++;
	      residueHash.put(res,new Integer(count));
	    } else {
	      residueHash.put(res,new Integer(1));
	    }
	  } else {
	    if (residueHash.containsKey("-")) {
	      int count = ((Integer)residueHash.get("-")).intValue() ;
	      count++;
	      residueHash.put("-",new Integer(count));
	    } else {
	      residueHash.put("-",new Integer(1));
	    }
	  }
	}
      }

      //What is the count threshold to count the residues in residueHash()
      int thresh = threshold*(sequences.size())/100;
    
      //loop over all the found residues
      Enumeration e = residueHash.keys();
      while (e.hasMoreElements()) {
	String res = (String)e.nextElement();
	if (((Integer)residueHash.get(res)).intValue() > thresh) {
	  //	  System.out.println(res + " " + residueHash.get(res));
	  //Now loop over the properties
	  Enumeration e2 = propHash.keys();
	  while (e2.hasMoreElements()) {
	    String type = (String)e2.nextElement();
	    Hashtable ht = (Hashtable)propHash.get(type);
	    
	    //Have we ticked this before?
	    if (! resultHash.containsKey(type)) {
	      if (ht.containsKey(res)) {
		resultHash.put(type,ht.get(res));
	      } else {
		//		System.out.println("No properties for residue " + res + ". Assuming gap type");
		resultHash.put(type,ht.get("-"));
	      }
	    } else if ( ((Integer)resultHash.get(type)).equals((Integer)ht.get(res)) == false) {
	      resultHash.put(type,new Integer(-1));
	    }
	  }
	}
      }
      total.addElement(resultHash);
    }
  }
  public int countGaps(int j) {
    int count = 0;
    for (int i = 0; i < sequences.size();i++) {
      String tmp = ((Sequence)sequences.elementAt(i)).sequence.substring(j,j+1);
      if (tmp.equals(" ") || tmp.equals(".") || tmp.equals("-")) {
	count++;
      }
    }
    return count;
  }
  public  void  verdict(boolean consflag, float percentageGaps) {
    String consString = "";

    for (int i=start; i <= end; i++) {
      int totGaps = countGaps(i);
      float pgaps = (float)totGaps*100/(float)sequences.size();
      
      if (percentageGaps > pgaps) {
	Hashtable resultHash = (Hashtable)total.elementAt(i);
	//Now find the verdict
	int count = 0;
	Enumeration e3 = resultHash.keys();
	
	while (e3.hasMoreElements()) {
	  String type = (String)e3.nextElement();
	  Integer result = (Integer)resultHash.get(type);
	  //	  System.out.println(type + " " + result);
	  //Do we want to count +ve conservation or +ve and -ve cons.?
	  if (consflag) {
	    if (result.intValue() == 1) {
	      count++;
	    }
	  } else {
	    if (result.intValue() != -1) {
	      count++;
	    }
	  }
	}
	//	System.out.println("Final conservation = " + count);
	if (count < 10) {
	  consString = consString + String.valueOf(count);
	} else {
	  consString = consString + "*";
	}
      } else {
	consString = consString + "-";
      }
    }
    consSequence = new DrawableSequence(name,consString,start,end);

  }
  
  public static void main(String[] args) {
    DrawableSequence[] s = null;
    s = FormatAdapter.toDrawableSequence(FormatAdapter.read(args[0],"File",args[1]));
    AlignFrame af = new AlignFrame(null,s);
    af.resize(700,500);
    af.show();

    Vector tmp = new Vector();
    for(int i=0; i < s.length; i++) {
      tmp.addElement(s[i]);
    }
    Alignment al = new Alignment(s);
    al.percentIdentity();

    DrawableSequence[] c = new DrawableSequence[20];
    int count=0;
    Enumeration en = ResidueProperties.propHash.keys();
    while (en.hasMoreElements()) {
      String key = (String)en.nextElement();
      Hashtable hyd = new Hashtable();
      hyd.put(key,(Hashtable)ResidueProperties.propHash.get(key));
      Conservation  cons = new Conservation(key,al.cons,hyd,3,tmp,0,s[0].getSequence().length()-1);
      cons.calculate();
      cons.verdict(false,20);
      c[count++] = cons.consSequence;
    }
    Conservation  cons2 = new Conservation("All",al.cons,ResidueProperties.propHash,3,tmp,0,s[0].getSequence().length()-1);
    cons2.calculate();
    cons2.verdict(false,20);
    c[count++] = cons2.consSequence;
    af.ap.seqPanel.align.addSequence(c);
    for (int i=0; i < count; i++) {
      System.out.println(">" + c[i].getName() + "\n" + c[i].getSequence());
    }
  
    System.exit(0);
  }

}















