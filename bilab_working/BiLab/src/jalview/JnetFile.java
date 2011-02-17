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



public class JnetFile extends FileParse {
  int maxLength = 0;

  Vector seqs;  //Vector of Sequences
  Vector headers;
  Vector titles;
  Vector conf;
  Vector hscores; 
  Vector escores; 
  Vector cscores; 

  public JnetFile(String inStr) {
    seqs = new Vector();
    headers = new Vector();
    titles = new Vector();
    hscores = new Vector();
    escores = new Vector();
    cscores = new Vector();
    conf = new Vector();
    readLines(inStr);
    //System.out.println(noLines);
    //System.out.println(lineArray.size());
    parse();
  }


  public JnetFile(String inFile, String type) throws IOException {
    //Read in the file first
    super(inFile,type);
    
    seqs = new Vector();
    
    headers = new Vector();
    titles = new Vector();

    hscores = new Vector();
    escores = new Vector();
    cscores = new Vector();
    conf = new Vector();
    //Read lines from file
    System.out.print("Reading file....");
    readLines();
    System.out.println("done");
  

    System.out.println("Parsing file....");
    parse();

  }

  public void parse() {
    String seq = "";
    String pred = "";

    for (int i=0; i < lineArray.size(); i++) {
      String line = (String)lineArray.elementAt(i);
      if (line.indexOf("START PRED") >= 0) {
	i++;

	while (i < lineArray.size() && line.indexOf("END PRED") < 0) {

	  line = (String)lineArray.elementAt(i);
	  //	  System.out.println(line);
	  StringTokenizer str = new StringTokenizer(line);

	  if (str.countTokens() != 6) {
	    System.out.println("Wrong number of columns " + str.countTokens());
	  } else {
	    seq = seq + str.nextToken();
	    pred = pred + str.nextToken();
	    // System.out.println(seq);
	    

	    
	    conf.addElement(new Double(str.nextToken()));
	    hscores.addElement(new Double(str.nextToken()));
	    escores.addElement(new Double(str.nextToken()));
	    cscores.addElement(new Double(str.nextToken()));
	  }
	  i++;
	}
      }
    }

    System.out.println(conf.size() + " " + hscores.size() + " " + escores.size() + " " + cscores.size());

    // Now put the sequence and predicition into Objects
    seqs.addElement(new Sequence("Query",seq,1,seq.length()));
    Sequence tmp = new Sequence("Predicition",pred,1,pred.length());
    tmp.score[0] = conf;
    tmp.score[1] = hscores;
    tmp.score[2] = escores;
    tmp.score[3] = cscores;
    seqs.addElement(tmp);
    System.out.println("Sequence is " + ((Sequence)seqs.elementAt(0)).sequence);
    System.out.println("size = " + seqs.size());
    // What do we do with the h e and c scores?
    
  }
  public static String print(Sequence[] s) {
    StringBuffer out = new StringBuffer();

    out.append("START PRED\n");
    for (int i=0; i < s[0].sequence.length(); i++) {
      out.append(s[0].sequence.substring(i,i+1) + " ");
      out.append(s[1].sequence.substring(i,i+1) + " ");
      out.append(s[1].score[0].elementAt(i) + " ");
      out.append(s[1].score[1].elementAt(i) + " ");
      out.append(s[1].score[2].elementAt(i) + " ");
      out.append(s[1].score[3].elementAt(i) + " ");

      out.append("\n");
    }
    out.append("END PRED\n");
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
      
      
