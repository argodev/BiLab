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

public class PostalCGI extends CGI {
  Sequence[] sequence;

  public PostalCGI(String server, int port,String location,Sequence[] sequence, PrintStream statout) {
    super(server,port,location,statout);
    this.sequence = sequence;
    this.variables = makeVariables();
  }

  public Hashtable makeVariables() {
    String seq = PfamFile.print(sequence);
    Hashtable h = new Hashtable();
    h.put("sequence",seq);
    return h;

  }

  public void readInput(DataInputStream in) {

      String aln = "";
      boolean start = false;
      String outstr = "";
      try {
	while ((aln=in.readLine()) != null) {
	  outstr = outstr + aln + "\n";
	  statout.println(aln);
	}
	
	statout.print(outstr);
	DrawableSequence[] ds = FormatAdapter.toDrawableSequence(FormatAdapter.read("POSTAL",outstr));	  
	AlignFrame af = new AlignFrame("Null",ds);

	af.scores.setState(true);
	af.ap.seqPanel.seqCanvas.showScores = true;
	af.ap.idPanel.idCanvas.showScores = true;
	af.ap.seqPanel.align.percentIdentity();
	af.cons = af.ap.seqPanel.align.cons;
	af.resize(700,500);
	//	PostalFile.setColours(af.ap);
	af.ap.setSequenceColor(new PIDColourScheme(af.cons));
	af.show();
	af.updateFont();
	af.updateFont();
      } catch (IOException ioex) {
	System.out.println("Exception " + ioex);
      }
  }

  public static void main(String[] args) {

    Sequence[] seqs = FormatAdapter.toDrawableSequence(FormatAdapter.read(args[0],"File",args[1]));
     
      PostalCGI cwcgi = new PostalCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runpostal",seqs,System.out);
      cwcgi.run();
  }
}












