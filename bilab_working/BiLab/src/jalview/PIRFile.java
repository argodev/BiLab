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

public class PIRFile extends FileParse {
  int noSeqs;
  int maxLength = 0;

  Hashtable myHash;  //hashtable containing the sequences
  Vector seqs;  //Vector of Sequences
  Vector headers;

  Vector words = new Vector();  //Stores the words in a line after splitting

  long start;
  long end;

  public PIRFile(String inStr) {
    myHash = new Hashtable();
    seqs = new Vector();
    headers = new Vector();
    readLines(inStr);
    parse();
  }

  public PIRFile(String inFile, String type) throws IOException {
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
    
    String id = "";
    String seq = "";
    String desc = "";
    int count = 0;
    boolean flag = false;
    int sstart =-1;
    int send =-1;

    for (int i = 0; i < lineArray.size(); i++) {

      //Do we have an id line?
      if (((String)lineArray.elementAt(i)).length() > 0) {
	if (((String)lineArray.elementAt(i)).indexOf(">P1;") == 0) {
	  flag = true;
	  // If this isn't the first sequence add the previous sequence to the array
	  if (count != 0) {
	    if (seq.indexOf("*") > 0) {
	      seq = seq.substring(0,seq.indexOf("*"));
	    }
	    seqs.addElement(new Sequence(id,seq,1,seq.length()));
	  }
	  count++;
	  StringTokenizer str = new StringTokenizer((String)lineArray.elementAt(i)," ");
	  // Extract the id and take off the >
	  id = str.nextToken();
	  id = id.substring(4);
	  if (id.indexOf("/") > 0 ) { 
	    StringTokenizer st = new StringTokenizer(id,"/");
	    if (st.countTokens() == 2) {
	      id = st.nextToken();
	      String tmp = st.nextToken();
	      st = new StringTokenizer(tmp,"-");
	      if (st.countTokens() == 2) {
		sstart = Integer.valueOf(st.nextToken()).intValue();
		send = Integer.valueOf(st.nextToken()).intValue();
	      } else {
		sstart = -1;
		send = -1;
	      }
	    } else {
	      sstart = -1;
	      send = -1;
	    }
	  }
	  i++;
	  desc = (String)lineArray.elementAt(i);
	  seq = "";
	  
	} else {
	  // We have sequence
	  StringTokenizer st = new StringTokenizer((String)lineArray.elementAt(i)," ");
	  while (st.hasMoreTokens()) {
	    seq = seq + st.nextToken();
	  }
	}
      }
    }
    if (flag == true) {
      if (seq.indexOf("*") > 0) {
	seq = seq.substring(0,seq.indexOf("*"));
      }
      if (sstart >=0 && send >=0) {
	seqs.addElement(new Sequence(id,seq,sstart,send));
      } else {
	seqs.addElement(new Sequence(id,seq,1,seq.length()));
      }
    }
  }
  
  public static String print(Sequence[] s, int len) {
    return print(s,len,true);
  }
  public static String print(Sequence[] s, int len,boolean gaps) {
    StringBuffer out = new StringBuffer();
    int i = 0;

    while (i < s.length && s[i] != null) {
      String seq = "";
      if (gaps) {
	seq = s[i].getSequence() + "*";
      } else {
	seq = AlignSeq.extractGaps(s[i].getSequence(),"-");
	seq = AlignSeq.extractGaps(seq,".");
	seq = AlignSeq.extractGaps(seq," ");
	seq = seq + "*";
      }

      out.append(">P1;" + s[i].getName() + "/" + s[i].start + "-" + s[i].end + "\n");
      out.append(" Dummy title\n");
      int nochunks = seq.length() / len + 1;

      for (int j = 0; j < nochunks; j++) {
	int start = j*len;
	int end = start + len;
	
	if (end < seq.length()) {
	  out.append(seq.substring(start,end) + "\n");
	} else if (start < seq.length()) {
	  out.append(seq.substring(start) + "\n");
	}
      }
      i++;
    }
    return out.toString();
  }

  public static void main(String[] args) {
    String inStr = ">P1;LCAT_MOUSE_90.35\nMGLPGSPWQRVLLLLGLLLPPATPFWLLNVLFPPHTTPKAELSNHTRPVILVPGCLGNRLEAKLDKPDVVNW\nMCYRKTEDFFTIWLDFNLFLPLGVDCWIDNTRIVYNHSSGRVSNAPGVQIRVPGFGKTESVEYVDDNKLAGY\n\n>LCAT_PAPAN_95.78\nMGPPGSPWQWVPLLLGLLLPPAAPFWLLNVLFPPHTTPKAELSNHTRPVILVPGCLGNQLEAKLDKPDVVNW\nMCYRKTEDFFTIWLDLNMFLPLGVDCWIDNTRVVYNRSSGLVSNAPGVQIRVPGFGKTYSVEYLDSSKLAGY\nLHTLVQNLVNNGYVRDETVRAAPYDWRLEPGQQEEYYHKLAGLVEEMHAAYGKPVFLIGHSLGCLHLLYFLL\n";
    PIRFile fa = new PIRFile(inStr);
  }
}

	 

