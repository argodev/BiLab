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

import java.net.*;
import java.io.*;
import java.util.*;

public class ClustalwCGI extends CGI {
  Sequence[] sequence;

  public ClustalwCGI(String server, int port,String location,Sequence[] sequence, PrintStream statout) {
    super(server,port,location,statout);
    this.sequence = sequence;
    this.variables = makeVariables();
  }

  public Hashtable makeVariables() {
    String seq = FastaFile.print(sequence,72);
    Hashtable h = new Hashtable();
    h.put("sequence",seq);
    h.put("newseq","poggy");
    return h;

  }

  public void readInput(DataInputStream in) {

      String aln = "";
      boolean start = false;
      String outstr = "";
      try {
	while ((aln=in.readLine()) != null) {
	  if (aln.indexOf("CLUSTAL") == 0) {
	    start = true;
	  }
	  if (start == true) {
	    outstr = outstr + aln + "\n";
	  } else {
	    statout.println(aln);
	  }
	}
	statout.print(outstr);

	DrawableSequence[] ds = FormatAdapter.toDrawableSequence(FormatAdapter.read("CLUSTAL",outstr));
	AlignFrame af = new AlignFrame("Null",ds);
	
	af.ap.seqPanel.align.percentIdentity();
	af.cons = af.ap.seqPanel.align.cons;
	af.ap.seqPanel.align.setColourScheme(ColourAdapter.get(ColourProperties.PID));
	
	af.resize(700,500);
	af.show();

      } catch (IOException ioex) {
	System.out.println("Exception " + ioex);
      }
  }

  public static void main(String[] args) {

    try {
      FastaFile ff = new FastaFile("hth2.fa","File");
      Sequence[] seqs = new Sequence[ff.seqs.size()];
      
      for (int i = 0; i < ff.seqs.size(); i++) {
	seqs[i] = (Sequence)ff.seqs.elementAt(i);
      }
      
      ClustalwCGI cwcgi = new ClustalwCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runclustal",seqs,System.out);
      cwcgi.run();
    } catch (IOException ie) {
      System.out.println("Exception " + ie);
    }
  }
}












