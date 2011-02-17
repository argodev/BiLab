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

public class MSPFile extends FileParse {
  int noSeqs;
  int maxLength = 0;

  Hashtable myHash;  //hashtable containing the sequences
  Vector seqs;  //Vector of Sequences
  Vector headers;

  Vector words = new Vector();  //Stores the words in a line after splitting

  long start;
  long end;

  public MSPFile(String inStr) {
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();
    readLines(inStr);
    parse();
  }

  public MSPFile(String inFile, String type) throws IOException {
    //Read in the file first
    super(inFile,type);
    
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();

    //Read lines from file
    System.out.print("Reading file....");
    start = System.currentTimeMillis();
    readLines();
    end = System.currentTimeMillis();
    System.out.println("done");
    System.out.println("Total time taken = " + (end-start) + "ms");

    System.out.println("Parsing file....");
    start = System.currentTimeMillis();
    parse();
  }

  public void parse() {
    
    // The first two lines are descriptive

    for (int i = 0; i < lineArray.size(); i++) {
      String line = (String)lineArray.elementAt(i);


      // Check for comment lines
      if (line.indexOf("#") == -1 ) {
	//Split into fields

	StringTokenizer st = new StringTokenizer(line);
	if (st.countTokens() == 8) {
	  try {
	    String s = st.nextToken();
	    int score = Integer.parseInt(s);
	    String frame = st.nextToken();
	    int  qstart = Integer.parseInt(st.nextToken());
	    int  qend   = Integer.parseInt(st.nextToken());
	    int hstart = Integer.parseInt(st.nextToken());
	    int hend = Integer.parseInt(st.nextToken());
	    String id = st.nextToken();
	    String seq = st.nextToken();
	    String database = "";

	    if (id.indexOf("|") != -1) {
	      StringTokenizer st2 = new StringTokenizer(id,"|");
	      while (st2.hasMoreTokens()) {
		String tmp = st2.nextToken();
		if (!tmp.equals("")) {
		  id = tmp;
		}
	      }
	    } else if (id.indexOf(":") != -1) {
	      database = id.substring(0,id.indexOf(":"));
	      id = id.substring(id.indexOf(":")+1);
	    }
	    seqs.addElement(new MSPSequence(id,seq,hstart,hend,qstart,qend,score,frame,database));
	  } catch (NumberFormatException nfe) {
	    System.out.println("NumberFormatException " + nfe);
	  }
	}
      }
    }
    noSeqs = seqs.size();
  }

  public static String print(Sequence[] s) {
    return print(s,true);
  }
	
  public static String print(Sequence[] s,boolean gaps) {
    StringBuffer out = new StringBuffer();
    int i = 0;

    while (i < s.length && s[i] != null) {

      if (s[i] instanceof MSPSequence) {
      MSPSequence tmp = (MSPSequence)s[i];	
	out.append(tmp.score + " (" + tmp.frame + ")  " + tmp.qstart + " " + tmp.qend + " " + 
		   tmp.start + " " + tmp.end + " " + tmp.name + " " + 
		   tmp.sequence.substring(tmp.qstart-1) + "\n");
      }
      i++;
    }
    return out.toString();
  }

  public void extractSeqs(int sgapthresh, int qgapthresh, boolean fullseq) {
    MSPSequence[] seq = new MSPSequence[seqs.size()];
    Vector tmp = new Vector();
    Vector seq2 = new Vector();

    for (int i=0; i < seqs.size(); i++) {
      seq[i] = (MSPSequence)seqs.elementAt(i);
    }

    Alignment al = new Alignment(seq);
    al.sortByID();
    for (int i=0; i < al.sequences.length; i++) {
      System.out.println(i + " " + al.sequences[i].name);
    }
    int i=0;
    
    while (i < al.sequences.length) {
      String name = al.sequences[i].name;
      Vector tmp2 = new Vector();
      tmp2.addElement(al.sequences[i]);
      
	while (++i < al.sequences.length && al.sequences[i].name.equals(name) == true) {
	  System.out.println(name + " " + i);
	  tmp2.addElement(al.sequences[i]);
	  name = al.sequences[i].name;
	}
	
	System.out.println("ID  " + i + " "  + al.sequences[i-1].name + " has " + tmp2.size() + " elements ");

	((Sequence)tmp2.elementAt(0)).getFeatures("srs.ebi.ac.uk/srs5bin/cgi-bin/","swall");
	Sequence sp = ((Sequence)tmp2.elementAt(0)).sp.sequence;
      if (sp != null) {
	if (!fullseq) {  
	    if (tmp2.size() > 1) {
	    
	    MSPSequence[] tmp3 = new MSPSequence[tmp2.size()];
	    float[] starts = new float[tmp2.size()];
	    for (int j=0; j < tmp3.length; j++) {
	      tmp3[j] = (MSPSequence)tmp2.elementAt(j);
	      starts[j] = tmp3[j].start;
	    }
	    QuickSort.sort(starts,tmp3);
	    
	    // Now try and join the sequences
	    
	    int start = tmp3[0].start;
	    int end = tmp3[0].end;
	    int qend = tmp3[0].qend;
	    
	    Vector jstart = new Vector();
	    Vector jend = new Vector();
	    
	    jstart.addElement(new Integer(start));
	    
	    int k = 1;
	    while (k < tmp3.length) {
	      int sgap = tmp3[k].start - end;
	      int qgap = tmp3[k].qstart - qend;
	      
	      if (tmp3[k].start < end && tmp3[k].end > end) {
		end = tmp3[k].end;
		qend = tmp3[k].qend;
	      } else if  (sgap == qgap && sgap < sgapthresh) {
		end = tmp3[k].end;
	      } else if ((qgap-sgap)/qgap < qgapthresh) {
		end = tmp3[k].end;
	      } else {
		jend.addElement(new Integer(end));
		jstart.addElement(new Integer(tmp3[k].start));
		start = tmp3[k].start;
		end = tmp3[k].end;
	      }
	      k++;
	    }
	    jend.addElement(new Integer(end));
	    
	    // We should now have joined start-end points in jstart and jend
	    for (int kk=0; kk < jstart.size(); kk++) {
	      int lstart = ((Integer)jstart.elementAt(kk)).intValue();
	      int lend =  ((Integer)jend.elementAt(kk)).intValue();
	      seq2.addElement(new DrawableSequence(sp.name,sp.sequence.substring(lstart-1,lend-1),lstart,lend));
	    }
	  } else {
	    Sequence t2 = (Sequence)tmp2.elementAt(0);
	    String useq = unPad(t2.sequence,true);
	    useq = unPad(useq,false);
	    seq2.addElement(new DrawableSequence(t2.name,useq,t2.start,t2.end));
	  }
	} else {
	  System.out.println("Adding " + sp.name);
	  seq2.addElement(new DrawableSequence(sp.name,sp.sequence,sp.start,sp.end));
	}
      } else {
        System.out.println("ERROR: Couldn't fetch SRS sequence");
      }

    }

    DrawableSequence[] outseq = new DrawableSequence[seq2.size()];
    for (int l=0;l<seq2.size();l++) {
      DrawableSequence t = (DrawableSequence)seq2.elementAt(l);
      outseq[l] = t;
      System.out.println(t.name + " " + t.start + " " + t.end + " " + t.sequence);
    }
    AlignFrame aa = new AlignFrame(outseq);
    aa.resize(700,500);
    aa.show();
  }

	
  public String unPad(String in, boolean end) {
    if (end == true) {
      while (in.length() > 0&& 
	     (in.substring(0,1).equals(" ") || in.substring(0,1).equals("-") || in.substring(0,1).equals("."))) {
	in = in.substring(1);
      }
      return in;
    } else if (end == false) {
      while (in.length() > 0 &&
	     (in.substring(in.length()-1).equals(" ") || in.substring(in.length()-1).equals("-") || in.substring(in.length()-1).equals("."))) {
	in = in.substring(0,in.length()-1);
      }
      return in;
    }
    return null;
  }
      
    
  
  public static void main(String[] args) {
    try {
      MSPFile msp = new MSPFile(args[0],"File");
      MSPSequence[] s = new MSPSequence[msp.seqs.size()];

      msp.extractSeqs(5,20,true);
       for (int i=0; i < msp.seqs.size(); i++) {
 	s[i] = (MSPSequence)msp.seqs.elementAt(i);
       }
       String out = msp.print(s);
       AlignFrame af = new AlignFrame(s);
       af.resize(700,500);
       af.show();
       ConsThread ct = new ConsThread(af);
       ct.start();
       System.out.println(out);
      System.out.println("done");
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}















