/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * $Log: JnetCGI.java,v $
 * Revision 1.1.4.1  1998/11/25 11:03:02  michele
 * Big changes to incorporate a score panel to display quality/confidence levels
 * Clustalx colour scheme and consensus calculations have been changed
 * to use arrays instead of Hashtables - much faster.  A
 * AlignFrame has a BigPanel instead of an AlignmentPanel
 * which consists of two AlignmentPanels together - one for sequencs
 * and another for scores.
 * The horizontal scrollbar goes all the way along the bottom
 * The height of the sequences can be changed.
 * SimpleBrowser has a Host: line in the header for virtual hosting
 *
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
import java.awt.*;

public class JnetCGI extends CGI {
  Sequence[] sequence;
  Object parent;

  public JnetCGI(String server, int port,String location,Sequence[] sequence, PrintStream statout,Object parent) {
    super(server,port,location,statout);
    this.sequence = sequence;
    this.variables = makeVariables();
    this.parent = parent;
  }

  public Hashtable makeVariables() {
    String seq = MSFfile.print(sequence);
    Hashtable h = new Hashtable();
    h.put("sequence",seq);
    return h;

  }
  public void insertGaps(Sequence s1, Sequence s2) {
    for (int i=0; i < s1.sequence.length(); i++) {
      String s = s1.sequence.substring(i,i+1);
      if (s.equals("-") || s.equals(".") || s.equals(" ")) {
	System.out.println(i + " " + s2.sequence.length());
	s2.insertCharAt(i,'-',false);
      }
    }
  }
  public DrawableSequence copySequence(DrawableSequence ds) {
    DrawableSequence ds2 =  new DrawableSequence(ds.name,ds.sequence,ds.start,ds.end);
    int i=0;
    while (i < ds.score.length && ds.score[i] != null) {
      ds2.score[i] = (Vector)ds.score[i].clone();
      i++;
    }
    return ds2;
  }
  public void readInput(DataInputStream in) {

      String aln = "";
      boolean start = false;
      String outstr = "";
      try {
	while ((aln=in.readLine()) != null) {
	  outstr = outstr + aln + "\n";
	}
	
	statout.print("Prediction finished. Displaying output...");
	DrawableSequence[] ds = FormatAdapter.toDrawableSequence(FormatAdapter.read("JNET",outstr));	 
	insertGaps(sequence[0],ds[0]);
	insertGaps(sequence[0],ds[1]);
	System.out.println("ds0 " + ds[0].sequence);
	System.out.println("ds1 " + ds[1].sequence);
	String out = JnetFile.print(ds);
	System.out.println(out);

	ds[0].name = sequence[0].name;
	ds[0].start = sequence[0].start;
	ds[0].end = sequence[0].end;


	ds[1].name = sequence[0].name;
	ds[1].start = sequence[0].start;
	ds[1].end = sequence[0].end;

	// Make the score sequences
	DrawableSequence[] s = new DrawableSequence[5];
	s[0] = ds[1];
	SecondaryColourScheme sc = new SecondaryColourScheme();
	sc.setColours(s[0]);
	s[1] = new ScoreSequence(copySequence(ds[1]),0,50,50,255);
	s[1].name = s[1].name + ".conf";
	s[2] = new ScoreSequence(copySequence(ds[1]),1,255,50,255);
	s[2].name = s[2].name + ".helix";
	s[3] = new ScoreSequence(copySequence(ds[1]),2,255,255,50);
	s[3].name = s[3].name + ".sheet";
	s[4] = new ScoreSequence(copySequence(ds[1]),3,50,255,255);
	s[4].name = s[4].name + ".turn";
	s[0].name = s[0].name + ".pred";	
	
	AlignFrame af = null;

	if (parent instanceof AlignFrame) {
	  af = (AlignFrame)parent;
	} else {	
	  af = new AlignFrame("Null",ds);
	}
	if (af != null) {
	  if (af.bp.scorePanel != null) {
	    af.bp.scorePanel.seqPanel.align.addSequence(s);
	    af.updateFont();
	  }
	}
      } catch (IOException ioex) {
	if (parent instanceof AlignFrame) {
	  AlignFrame af = (AlignFrame)parent;
	  af.error("ERROR: IOException when contacting Jnet server",true);
	} else {	
	  System.out.println("Exception " + ioex);
	}
      }
  }

  public static void main(String[] args) {

    Sequence[] seqs = FormatAdapter.toDrawableSequence(FormatAdapter.read(args[0],"File",args[1]));
     
      JnetCGI cwcgi = new JnetCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runjnet",seqs,System.out,null);
      cwcgi.run();
  }
}












