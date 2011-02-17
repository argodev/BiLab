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

public class SwissprotFile extends FileParse {
  String title;
  String id = "";
  String acc;
  Sequence sequence;
  Vector features;
  int length;
  Vector pdbcode = new Vector();

  public SwissprotFile(String inStr) {
    features = new Vector();
    readLines(inStr);
    parse();
  }
  public SwissprotFile(String inFile, String type) throws IOException {
    super(inFile,type);
    features = new Vector();
    readLines();
    parse();
  }

  public void parse() {
    int start = -1;
    int end = -1;

    for (int i=0; i < noLines;i++) {
      if (lineArray.elementAt(i).toString().indexOf(" ") != 0) {
	StringTokenizer str = new StringTokenizer(lineArray.elementAt(i).toString()," ");

	String type = str.nextToken();

	if (type.equals("<PRE>ID")) {
	  id = str.nextToken();
	} else if (type.equals("AC")) {
	  acc = str.nextToken();
	  if (acc.indexOf(";") != -1) {
	    acc = acc.substring(0,acc.indexOf(";"));
	  }
	} else if (type.equals("DE")) {
	  title = str.nextToken();
	  i++;
	  while (lineArray.elementAt(i).toString().indexOf(" ") == 0) {
	    title = title + lineArray.elementAt(i).toString();
	    i++;
	  }
	} else if (type.equals("SQ")) {
	  String tmp = str.nextToken();
	  tmp = str.nextToken();
	  
	  length = Integer.valueOf(tmp).intValue();
	  i++;
	  String seq = "";
	  while (lineArray.elementAt(i).toString().indexOf(" ") == 0) {
	    seq = seq + lineArray.elementAt(i).toString();
	    i++;
	  }
	  String seq2 = "";
	  StringTokenizer str2 = new  StringTokenizer(seq," ");
	  while (str2.hasMoreTokens()) {
	    seq2 = seq2 + str2.nextToken();
	  }
          if (start != -1) {
	    sequence = new Sequence(id,seq2,start,end);
          } else {
	    sequence = new Sequence(id,seq2,1,length);
          }
	  sequence.features = new Vector();
	  for (int j=0;j < features.size();j++) {
	    sequence.features.addElement(features.elementAt(j));
	    ((SequenceFeature)features.elementAt(j)).sequence = sequence;
	  }
	} else if (type.equals("FT") &&  !(lineArray.elementAt(i).toString().substring(6,7).equals(" "))) {
	  String ftype = str.nextToken();
	  try {
	    int fstart = Integer.valueOf(str.nextToken()).intValue();
	    int fend = Integer.valueOf(str.nextToken()).intValue();
	    
	    String def = "";
	    while (str.hasMoreTokens()) {
	      def = def + str.nextToken() + " ";
	    }
	    //	    System.out.println(ftype + " " + fstart + " " + fend + " " + def);
	    if (!(ftype.equals("REPEAT"))) {
	      features.addElement(new SequenceFeature(null,ftype,fstart,fend,def));
	    }
	  } catch (NumberFormatException e) {
	    System.out.println("Exception : " + e);
	  }
	  
	} else if (type.equals("DR")) {
	  //	  System.out.println("Found DR line");

	  // Parse the database refs;
	  String tmp = SimpleBrowser.parse(lineArray.elementAt(i).toString());
	  StringTokenizer str2 = new StringTokenizer(tmp);

	  String dtype = str2.nextToken();
	  dtype = str2.nextToken();

	  if (dtype.indexOf(";") == (dtype.length()-1)) {
	    dtype = dtype.substring(0,dtype.indexOf(";"));
	  }
	  if (dtype.equals("PDB")) {
	    String code = str2.nextToken();
	    if (code.indexOf(";") == (code.length()-1)) {
	      code = code.substring(0,code.indexOf(";"));
	    }
	    System.out.println("Found pdb code " + code);
	    pdbcode.addElement(code);
	  }
	}	  
      }
    }
  }
  public void print() {
    System.out.println("ID = " + id);
    System.out.println("ACC = " + acc);
    System.out.println("length = " + length);
    System.out.println("SEQ = " + sequence.getSequence());
    for (int j=0;j < features.size();j++) {
      SequenceFeature sf = (SequenceFeature)features.elementAt(j);
      System.out.println(sf.type + " " + sf.start + " " + sf.end + " " + sf.description);
    
    }
    
  }
    
  public static void main(String[] args) {
    try {
      SwissprotFile sp = new SwissprotFile("http://srs.ebi.ac.uk:5000/srs5bin/cgi-bin/wgetz?-e+[swall-id:" + args[0] + "]","URL");
      sp.print();
    } catch (IOException e) {
      System.out.println("Exception " + e);
    }
  }
}

















