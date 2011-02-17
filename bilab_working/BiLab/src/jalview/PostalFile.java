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
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.Font;

public class PostalFile extends FileParse {
  Vector scores = new Vector();
  Vector seqs = new Vector();

  public PostalFile(String inStr) {
    readLines(inStr);
    System.out.println(noLines);
    System.out.println(lineArray.size());
    parse();
  }

  public PostalFile(String inFile, String type) throws IOException {
    //Read in the file first
    super(inFile,type);
 
    //Read lines from file
    System.out.print("Reading file....");
    readLines();
    System.out.println("done");

    System.out.println("Parsing file....");
    parse();
  }

  public void parse() {
    //Parse lines
    int i = 0;  //line counter
    
    //Loop over lines in file
    for (i = 0; i < noLines; i++) {
      String tmp = lineArray.elementAt(i).toString();

      if (tmp.indexOf(" ") != 0) {
	StringTokenizer str = new StringTokenizer(tmp.toString()," ");
	// If we have a line beginning with a hash we have scores
	if (tmp.indexOf("#") == 0) {
	  String id = "";
	  
	  if (str.countTokens() == 3) {
	    id = str.nextToken();
	    id = str.nextToken();

	    if (id.indexOf("_acc") >= 0 ) {
	      id = id.substring(0,id.indexOf("_acc"));
	    }
	    if (!id.equals("Consensus")) {
	      String sc = str.nextToken();
	      
	      char[] ch = sc.toCharArray();
	      Vector sco = new Vector();
	      
	      for (int j=0; j < ch.length; j++) {
		if (ch[j] == '.') {
		  sco.addElement(new Double(10));
		} else {
		  sco.addElement(new Double(Character.getNumericValue(ch[j])));
		}
	      }
	      scores.addElement(sco);
	    } 
	  }
	} else if (tmp.indexOf(" ") != 0){
	  String id = str.nextToken();
	  int start = -1;
	  int end = -1;
	  if (id.indexOf("/") > 0) {
	    StringTokenizer st = new StringTokenizer(id,"/");
	    if (st.countTokens() == 2) {
	      id = st.nextToken();
	      tmp = st.nextToken();
	      st = new StringTokenizer(tmp,"-");
	      if (st.countTokens() == 2) {
		start = Integer.valueOf(st.nextToken()).intValue();
		end = Integer.valueOf(st.nextToken()).intValue();
	      } else {
		start = -1;
		end = -1;
	      }
	    }
	  }
	  String seq = str.nextToken();
	  if (start != -1 && end != -1) {
	    Sequence tmp2 = new Sequence(id,seq,start,end);
	    seqs.addElement(tmp2);
	  } else {	 
	    Sequence tmp2 = new Sequence(id,seq,1,seq.length());
	    seqs.addElement(tmp2);
	  }

	}
      }
    }
    addScores();
  }
  public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer("");
    
    int max = 0;
    int maxid = 0;
    
    int i = 0;
    
    while (i < s.length && s[i] != null) {
      String tmp = s[i].getName() + "/" + s[i].start + "-" + s[i].end;

      if (s[i].getSequence().length() > max) {
	max = s[i].getSequence().length();
      }
      if (tmp.length() > maxid) {
	maxid = tmp.length();
      }
      i++;
    }
    // Increment a little for the score lines
    maxid = maxid + 5;
    if (maxid < 15) { maxid = 15; }
    
    int j = 0;
    while ( j < s.length && s[j] != null) {
      // First the sequences
      out.append( new Format("%-" + maxid + "s").form(s[j].getName() + "/" + s[j].start + "-" + s[j].end ) + " ");
      out.append(s[j].getSequence() + "\n");
      // Now the scores
      out.append( new Format("%-" + maxid + "s").form("#=GC " + s[j].getName() + "_acc") + " ") ;
      for (int ii=0; ii < s[j].score[0].size(); ii++) {
	if (((Double)s[j].score[0].elementAt(ii)).intValue() == 10) {
	  out.append(".");
	} else {
	  int t = ((Double)s[j].score[0].elementAt(ii)).intValue();
	  out.append(t);
	}
      }
      out.append("\n");
      j++;
    }

    
    return out.toString();
  }
  
  public void addScores() {
    System.out.println("Adding scores");
    for (int i=0 ; i < seqs.size(); i++) {
      Sequence ds = (Sequence)seqs.elementAt(i);
      //      System.out.println("Scores "  + scores.size() + " " + seqs.size());
      if (i < scores.size() && ds != null && scores.elementAt(i) != null) {
	System.out.println("Adding scores for " + ds.name + " " + ((Vector)scores.elementAt(i)).size());
	ds.score[0] = (Vector)scores.elementAt(i);
      }
    }
  }

  public static void setColours(AlignmentPanel ap) {
    PostalColourScheme pcs = new PostalColourScheme();
    ap.setSequenceColor(pcs);
  }

  public static void main(String[] args) {
    AlignFrame af = new AlignFrame(null,args[0],"File","POSTAL");

    af.resize(700,500);
    af.show();
    try {
      PostalFile pf = new PostalFile(args[0],"File");
      
      pf.setColours(af.ap);
      af.updateFont();
      af.updateFont();
    } catch (IOException e) {
    }
  }
} 








