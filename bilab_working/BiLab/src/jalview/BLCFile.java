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



public class BLCFile extends FileParse {
  int maxLength = 0;

  Vector seqs;  //Vector of Sequences
  Vector headers;
  Vector titles;

  public BLCFile(String inStr) {
    seqs = new Vector();
    headers = new Vector();
    titles = new Vector();

    readLines(inStr);
    System.out.println(noLines);
    System.out.println(lineArray.size());
    parse();
  }

  public BLCFile(String inFile, String type) throws IOException {
    //Read in the file first
    super(inFile,type);
    
    seqs = new Vector();
    
    headers = new Vector();
    titles = new Vector();
    //Read lines from file
    System.out.print("Reading file....");
    readLines();
    System.out.println("done");
  

    System.out.println("Parsing file....");
    parse();
  }

  public void parse() {
    boolean foundids = false;
    Vector seqstrings = new Vector();
    Vector starts = new Vector();
    Vector ends = new Vector();

    for (int i=0; i < lineArray.size(); i++) {
      String line = (String)lineArray.elementAt(i);
      if (line.indexOf(">") >= 0) {
	// Extract an id
	String tmp = line.substring(line.indexOf(">")+1);
	StringTokenizer st = new StringTokenizer(tmp);
	String id = st.nextToken();
	
	if (id.indexOf("/") > 0 ) { 
	  StringTokenizer st2 = new StringTokenizer(id,"/");
	  if (st2.countTokens() == 2) {
	    id = st2.nextToken();
	    String tmp2 = st2.nextToken();
	    st2 = new StringTokenizer(tmp2,"-");
	    if (st2.countTokens() == 2) {
	      starts.addElement(new Integer(st2.nextToken()));
	      starts.addElement(new Integer(st2.nextToken()));
	    } else {
	      starts.addElement(new Integer(-1));
	      ends.addElement(new Integer(-1));
	    }
	  }
	} else {
	  starts.addElement(new Integer(-1));
	  ends.addElement(new Integer(-1));
	}
	// Extract the title
	String title = "";
	while (st.hasMoreTokens()) {
	  title = title + " " + st.nextToken();
	}
	System.out.println(id);
	headers.addElement(id);
	titles.addElement(title);
	seqstrings.addElement("");
	foundids = true;
      }

      if (line.indexOf("*") >= 0) {
	int startcol = line.indexOf("*");
	i++;
	line = (String)lineArray.elementAt(i);
	while (i < lineArray.size() && line.indexOf("*") < 0) {
	  System.out.println(":"+line+":");
	  for (int j = startcol; j < headers.size(); j++) {
	    String s = (String)seqstrings.elementAt(j);
	    //  System.out.println(j + " " + line.length());
	    if (line.length() > j) {
	      seqstrings.setElementAt(s + line.substring(j,j+1),j);
	    } else {
	      seqstrings.setElementAt(s + "-",j);
	    }
	  }
	  i++;
	  line = (String)lineArray.elementAt(i);
	}
	for (int j = startcol; j < headers.size(); j++) {
	  if (((Integer)starts.elementAt(j)).intValue() >= 0 && ((Integer)ends.elementAt(j)).intValue() >= 0) {
	  seqs.addElement(new Sequence((String)headers.elementAt(j-startcol),
				       (String)seqstrings.elementAt(j),
				       ((Integer)starts.elementAt(j)).intValue(),
				       ((Integer)ends.elementAt(j)).intValue()));

	  } else {
	    seqs.addElement(new Sequence((String)headers.elementAt(j-startcol),
				       (String)seqstrings.elementAt(j),
				       1,
				       ((String)seqstrings.elementAt(j)).length()));
	  }
	}

      }
    }
  }
  public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer();

    int i=0;
    int max = -1;
    while (i < s.length && s[i] != null) {
      out.append(">" + s[i].getName() + "/" + s[i].start + "-" + s[i].end + "\n");
      if (s[i].getSequence().length() > max) { max = s[i].getSequence().length();}
      i++;
      
      out.append("* iteration\n");
      for (int j = 0; j < max; j++) {
	i=0;
	while (i < s.length && s[i] != null) {
	  out.append(s[i].getSequence().substring(j,j+1));
	  i++;
	}
	out.append("\n");
      }
    }
    out.append("*\n");
    return out.toString();
  }
  
  public static void main(String[] args) {
    try {
      BLCFile blc = new BLCFile(args[0],"File");
      DrawableSequence[] s = new DrawableSequence[blc.seqs.size()];
      for (int i=0; i < blc.seqs.size(); i++) {
	s[i] = new DrawableSequence((Sequence)blc.seqs.elementAt(i));
      }
      String out = BLCFile.print(s);

      AlignFrame af = new AlignFrame(null,s);
      af.resize(700,500);
      af.show();
      System.out.println(out);
    } catch (java.io.IOException e) {
      System.out.println ("Exception " + e);
    }
  }
}
      
      
