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



public class PfamFile extends FileParse {
  int noSeqs;
  int maxLength = 0;

  Hashtable myHash;  //hashtable containing the sequences
  Vector seqs;  //Vector of Sequences
  Vector headers;
  Vector ids;

  Vector words = new Vector();  //Stores the words in a line after splitting

  long start;
  long end;

  public PfamFile(String inStr) {
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();
    ids = new Vector();

    readLines(inStr);
    System.out.println(noLines);
    System.out.println(lineArray.size());
    parse();
  }

  public PfamFile(String inFile, String type) throws IOException {
    //Read in the file first
    super(inFile,type);
    
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();
    ids = new Vector();
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
    //Parse lines
    int i = 0;  //line counter
    
    //Loop over lines in file
    for (i = 0; i < noLines; i++) {
      if (lineArray.elementAt(i).toString().indexOf(" ") != 0) {
	if (lineArray.elementAt(i).toString().indexOf("#") != 0) {
	  StringTokenizer str = new StringTokenizer(lineArray.elementAt(i).toString()," ");
	  String id = "";
	  
	  if (str.hasMoreTokens()) {
	    id = str.nextToken();
	    
	    StringBuffer tempseq = new StringBuffer();
	    if (myHash.containsKey(id)) {
	      tempseq = new StringBuffer(myHash.get(id).toString());
	    }
	    if (!(headers.contains(id))) {
	      headers.addElement(id);
	    }
	    
	    //loop through the rest of the words
	    //  while (str.hasMoreTokens()) {	  
	    tempseq.append(str.nextToken());
	    // }
	    //put the sequence back in the hash
	    myHash.put(id,tempseq.toString());
	  }
	}
      }
    }
    
    this.noSeqs = headers.size();
      
    //Add sequences to the hash
    for (i = 0; i < headers.size(); i++ ){
	
      if ( myHash.get(headers.elementAt(i)) != null) {
	if (maxLength <  myHash.get(headers.elementAt(i)).toString().length() ) {
	  maxLength =  myHash.get(headers.elementAt(i)).toString().length();
	}
	String head =  headers.elementAt(i).toString();
	int start = 1;
	int end =  myHash.get(headers.elementAt(i)).toString().length();
	

	if (head.indexOf("/") > 0 ) { 
	  StringTokenizer st = new StringTokenizer(head,"/");
	  if (st.countTokens() == 2) {
	    ids.addElement(st.nextToken());
	    String tmp = st.nextToken();
	    st = new StringTokenizer(tmp,"-");
	    if (st.countTokens() == 2) {
	      start = Integer.valueOf(st.nextToken()).intValue();
	      end = Integer.valueOf(st.nextToken()).intValue();
	    } else {
	      start = -1;
	      end = -1;
	    }
	  } else {
	    ids.addElement(headers.elementAt(i));
	  }
	} else {
	  ids.addElement(headers.elementAt(i));
	}
	
	//	System.out.println(headers.elementAt(i));
	//System.out.println(ids.elementAt(i));
	//System.out.println( myHash.get(headers.elementAt(i).toString()).toString());
	if (start != -1 && end != -1) {
	  Sequence newSeq = new Sequence(ids.elementAt(i).toString(),
					 myHash.get(headers.elementAt(i).toString()).toString(),start,end);
	  seqs.addElement(newSeq);
	} else {
	  Sequence newSeq = new Sequence(ids.elementAt(i).toString(),
					 myHash.get(headers.elementAt(i).toString()).toString(),1,
					  myHash.get(headers.elementAt(i).toString()).toString().length());
	  seqs.addElement(newSeq);
	}
	
      } else {
	System.out.println("Can't find sequence for " + headers.elementAt(i));
      }
    }
    
    end = System.currentTimeMillis();
    System.out.println("done");
    System.out.println("Total time taken = " + (end-start) + "ms");
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

    if (maxid < 15) { maxid = 15; }
    
    int j = 0;
    while ( j < s.length && s[j] != null) {
      out.append( new Format("%-" + maxid + "s").form(s[j].getName() + "/" + s[j].start + "-" + s[j].end ) + " ");
	
      out.append(s[j].getSequence() + "\n");
      j++;
    }
    out.append("\n");
    
    return out.toString();
  }


  public static void main(String[] args) {
    String inStr = "CLUSTAL\n\nt1  GTGASAAATGGNNTGATTCTGTACCTTGTGGAGACTGGCGTGATGTGCAG\nt2  AAATGATTCTGTACCTTGTGGATGGACTGGCGTGATGTGCAGCAACTATT\n\nt1  CAACTATTCGANNGTGATCCAGTGGTTTTGTCGTTGAATCTGTCTTCGAT\nt2  CGAGTGATCCAGAGGTTTTGTCCTTGAATCTGTCTTCGATGGTTCTCTCG\n\nt1  GGTTCTCGGGTAAGCTATCACCAAGCATAGGTGGATTGGTTCATCTGAAG\nt2  GGTAAGATCCACCAAGCATATGCTAGCT\n ";
    ClustalFile msf = new ClustalFile(inStr);
    Sequence[] s = new Sequence[msf.seqs.size()+1];

    for (int i=0;i < msf.seqs.size();i++) {
      s[i] = (Sequence)msf.seqs.elementAt(i);
    }
    String outStr = msf.print(s);
    System.out.println(outStr);
  }

} 







