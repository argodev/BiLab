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



public class MSFfile extends FileParse {
  int noSeqs;
  int maxLength = 0;

  Hashtable myHash;  //hashtable containing the sequences
  Vector seqs;  //Vector of Sequences
  Vector headers;

  Vector words = new Vector();  //Stores the words in a line after splitting

  long start;
  long end;

  public MSFfile(String inStr) {
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();

    readLines(inStr);
    System.out.println(noLines);
    System.out.println(lineArray.size());
    parse();
  }

  public MSFfile(String inFile, String type) throws IOException {
    //Read in the file first
    super(inFile,type);
    
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();

    //Read lines from file
    System.out.print("Reading file....");
    readLines();
    System.out.println("done");
    System.out.println("Total time taken = " + (end-start) + "ms");

    System.out.println("Parsing file....");
    parse();
  }
  public void parse() {
    //Parse lines
    int i = 0;  //line counter
      int seqFlag = 0; //signifies that the sequences have started
      String key = new String();  //
      
      //Loop over lines in file
      for (i = 0; i < noLines; i++) {
	
	StringTokenizer str = new StringTokenizer(lineArray.elementAt(i).toString());
	
	while (str.hasMoreTokens()) {
	String inStr = str.nextToken();
	
	//If line has header information add to the headers vector
	if (inStr.indexOf("Name:") != -1) {
	  key = str.nextToken();
	  headers.addElement(key);
	  System.out.println(key);
	}
	
	//if line has // set SeqFlag to 1 so we know sequences are coming
	if (inStr.indexOf("//") != -1) {
	  seqFlag = 1;
	}

	//Process lines as sequence lines if seqFlag is set
	if (( inStr.indexOf("//") == -1) && (seqFlag == 1)) {
	  //seqeunce id is the first field
	  key = inStr;
	  StringBuffer tempseq = new StringBuffer();
	  //Get sequence from hash if it exists
	  if (myHash.containsKey(key)) {
	    tempseq = new StringBuffer(myHash.get(key).toString());
	  } 
	  //loop through the rest of the words
	  while (str.hasMoreTokens()) {	  
	    //append the word to the sequence
	    tempseq.append(str.nextToken());
	  }
	  //put the sequence back in the hash
	  myHash.put(key,tempseq.toString());
	} 
      }
      
    } 

    this.noSeqs = headers.size();

    //Add sequences to the hash
    for (i = 0; i < headers.size(); i++ ){

      if ( myHash.get(headers.elementAt(i)) != null) {
	String head =  headers.elementAt(i).toString();
	String seq =  myHash.get(head).toString();
	int start = 1;
	int end = seq.length();
	if (maxLength <  head.length() ) {
	  maxLength =  head.length();
	}
	
	if (head.indexOf("/") > 0 ) { 
	  StringTokenizer st = new StringTokenizer(head,"/");
	  if (st.countTokens() == 2) {
	    head = st.nextToken();
	    String tmp = st.nextToken();
	    st = new StringTokenizer(tmp,"-");
	    if (st.countTokens() == 2) {
	      start = Integer.valueOf(st.nextToken()).intValue();
	      end = Integer.valueOf(st.nextToken()).intValue();
	    }
	  }
	}

	Sequence newSeq = new Sequence(head,seq,start,end);
	seqs.addElement(newSeq);
      } else {
	System.out.println("Can't find sequence for " + headers.elementAt(i));
      }
    }

    end = System.currentTimeMillis();
    System.out.println("done");

  }

  public static int checkSum(String seq) {
    //String chars =  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.*~&@";
    int check = 0;

    String index =  "--------------------------------------&---*---.-----------------@ABCDEFGHIJKLMNOPQRSTUVWXYZ------ABCDEFGHIJKLMNOPQRSTUVWXYZ----@";
    index += "--------------------------------------------------------------------------------------------------------------------------------";
    
    for(int i = 0; i < seq.length(); i++) {
      try {
	if (i <seq.length()) {
	  int pos = index.indexOf(seq.substring(i,i+1));
	  if (!index.substring(pos,pos+1).equals("_")) {
	    check += ((i % 57) + 1) * pos;
	  }
	}
      } catch (Exception e) {
	System.err.println("Exception " + e);
      }
    }
    return check % 10000;    
  }

  public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer("PileUp\n\n");

    int max = 0;
    int maxid = 0;
    
    int i = 0;
    String big = "";
    while (i < s.length && s[i] != null) {
      big += s[i].getSequence();
      i++;
    }
    i = 0;
    int bigcheck = checkSum(big);

    out.append("   MSF: " + s[0].getSequence().length() + "   Type: P    Check:  " + bigcheck + "   ..\n\n\n");

    while (i < s.length && s[i] != null) {
      String seq = s[i].getSequence();
      String name =  s[i].getName()+ "/" + s[i].start + "-" + s[i].end;
      int check = checkSum(s[i].getSequence());
      out.append(" Name: " + name + " oo  Len:  " + s[i].getSequence().length() + "  Check:  " + check + "  Weight:  1.00\n");
      if (seq.length() > max) {
	max = seq.length();
      }
      if (name.length() > maxid) {
	maxid = name.length();
      }
      i++;
    }

    if (maxid < 10) { maxid = 10; }
    maxid++;
    out.append( "\n\n//\n\n");

    int len = 50;

    int nochunks =  max / len + 1;
    if (max%len == 0) {
      nochunks--;
    }
    for (i = 0; i < nochunks; i++) {
      int j = 0;
      while (j < s.length && s[j] != null) {
	   String name =  s[j].getName();
	out.append( new Format("%-" + maxid + "s").form(name + "/" + s[j].start + "-" + s[j].end) + " ");
	for (int k = 0; k < 5; k++) {
	  
	  int start = i*50 + k*10;
	  int end = start + 10;
	  
	  if (end < s[j].getSequence().length() && start < s[j].getSequence().length() ) {
	    out.append(s[j].getSequence().substring(start,end));
	    if (k < 4) { out.append(" ");}else{ out.append("\n");}
	  } else {
	    if (start < s[j].getSequence().length()) {
	      out.append(s[j].getSequence().substring(start));
	      out.append("\n");
	    } else {
	      if (k == 0) {
		out.append("\n");
	      }
	    }
	  }
	}
	j++;
      }
      out.append("\n");
	
    }
    return out.toString();
  }
} 







