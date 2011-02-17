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
import java.awt.Color;
import java.util.*;

public class Postscript {
  AlignFrame af;
  DrawableAlignment al;

  int noseqs;
  float boxsize;
  float yspace;
  int ygap;

  int maxseqlen;
  int maxidlen;
  
  StringBuffer out;
  BufferedWriter sw;
  PrintStream ps;

  Color lightBlue = new Color(175,175,255);
  Color midBlue = new Color(110,110,255);

  float xorig;
  float xtext;
  float ytext;
  float seqstart;
  
  int orient;
  int fontsize;
  int xmargin;
  int ymargin;
  int paperwidth;
  int paperheight;
  String font;
  boolean makeString;

  public Postscript(AlignFrame af,BufferedWriter sw)  {
    this.af = af;
    this.al = af.ap.seqPanel.align;
    this.orient = af.pp.orientation;

    font = af.pp.font;
    fontsize = af.pp.fsize;
    xmargin = af.pp.xoffset;
    ymargin = af.pp.yoffset;

    ygap = 30;
    this.sw = sw;
  }

  public Postscript(AlignFrame af,PrintStream ps)  {
    this.af = af;
    this.al = af.ap.seqPanel.align;
    this.orient = af.pp.orientation;

    out = new StringBuffer();

    font = af.pp.font;
    fontsize = af.pp.fsize;
    xmargin = af.pp.xoffset;
    ymargin = af.pp.yoffset;

    ygap = 30;
    this.ps = ps;
  }

  public Postscript(AlignFrame af,boolean buffer)  {
    if (buffer == true) {
      this.makeString = true;
      this.af = af;
      this.al = af.ap.seqPanel.align;
      this.orient = af.pp.orientation;
      
      this.out = new StringBuffer();
      
      font = af.pp.font;
      fontsize = af.pp.fsize;
      xmargin = af.pp.xoffset;
      ymargin = af.pp.yoffset;
      
      ygap = 30;
    }
  }

  public void print(String s) throws IOException {
    if (sw != null) {
      sw.write(s);
    } 
    if (ps != null) {
      ps.print(s);
    }
    if (makeString == true) {
      out.append(s);
    }
  }
  public void generate() {
    if (orient == af.pp.PORTRAIT) {
      paperwidth = af.pp.SHORTSIDE;
      paperheight = af.pp.LONGSIDE;
    } else {
      paperwidth = af.pp.LONGSIDE;
      paperheight = af.pp.SHORTSIDE;
    }

    noseqs = al.size();
    int seqlen = al.maxLength();
    maxidlen = al.maxIdLength();
    boxsize = (float)(fontsize * 1.1);
    if (af.ap.seqPanel.seqCanvas.showScores) {
      yspace = boxsize *2;
    } else {
      yspace = boxsize;
    }
    seqstart = (float)(boxsize * maxidlen * 0.7  + xmargin);
    // How many chars in x direction
    int nocharx = (int)((paperwidth - seqstart - 2 * xmargin)/boxsize);
    // How many chars in y direction
    int nochary = (int)((paperheight - 2*ymargin)/yspace);
    // How deep is the alignment in the y direction
    float aligny = (float)(2*boxsize + noseqs*yspace);
    // How many alignments can we fit on one page?
    int noalign = (int)((paperheight - ymargin)/(aligny + ymargin));

    printinit();

    int page = 1;
    float yoffset = 0;
    int aligncount = 0;

    try {
    for (int i = 0; i < seqlen; i++) {
      
      if (i%nocharx == 0) {
	if ((noalign > 1 ) && (aligncount < noalign) && (aligncount != 0)) {
	  yoffset = aligncount*(aligny + 30);
	  aligncount++;
	} else {
	  yoffset = 0;
	  aligncount = 1;
	  if (page > 1) {
	    print("\nshowpage\n");
	  }
	  print("\n%%Page: ? " + page + "\n");
	  
	  if (paperwidth > paperheight) {
	    print(af.pp.SHORTSIDE + " 0 translate\n90 rotate\n");
	  }
	  
	  print("gsave\n");
	  print("/Times-Roman findfont\n");
	  print("8 scalefont\n");
	  print(xmargin + " " + ymargin + " moveto\n");
	  print("(Jalview Michele Clamp 1998) show\n");
	  print("grestore\n");
	  page++;
	  if (sw != null) {
	    sw.flush();
	  }
	}
	
	print("black setrgbcolor\n");
	for (int k = 0;k < noseqs; k++) {
	  String id = al.sequences[k].getName();
	  // Position of the kth sequence id
	  float yorig = (float)(paperheight - ymargin - boxsize - k*yspace - yoffset);

	  if (af.ap.seqPanel.seqCanvas.showScores) {
	    ytext = (float)(yorig + yspace/2 + 0.20*boxsize);
	  } else {
	    ytext = (float)(yorig + 0.20*boxsize);
	  }
	  print("(" + id + ") " + xmargin + " " + ytext + " moveto show\n");
	}
      }
      int ii = i%nocharx;

      xorig = (float)(ii*boxsize + seqstart);
      xtext = (float)(xorig + 0.12*boxsize);

      // Now the number labels
      if (i%10 == 9 && i != 0) {
	float ylabel1 = (float)(paperheight -ymargin + 0.2*boxsize - yoffset);
	float ylabel2 = (float)(paperheight -ymargin + 1.2*boxsize - yoffset);
	float x1 = (float)(xorig + 0.4*boxsize);
	int num = i+1;
	print("black setrgbcolor\n");
	print("(|) " + x1 + " " + ylabel1 + " moveto show\n");
	print("(" + num + ") " + xtext + " " + ylabel2 + " moveto show\n");
      }
      
      // Now the residue letters
      for (int j = 0; j < noseqs; j++) {
	String id = al.sequences[j].getName();
	float yorig = (float)(paperheight - ymargin - boxsize - j*yspace - yoffset);

	// This is where the text is - depends on showScores
	if (af.ap.seqPanel.seqCanvas.showScores) {
	  ytext = (float)(yorig + yspace/2 + 0.20 *boxsize);
	} else {
	  ytext = (float)(yorig  + 0.20 *boxsize);
	}
	
	String s = " ";

	if (i < al.sequences[j].getSequence().length()) {
	  s = al.sequences[j].getSequence().substring(i,i+1);
	} else if (i == al.sequences[j].getSequence().length()) {
	  s = al.sequences[j].getSequence().substring(i);
	}

	int resint = 23;

	try {
	  resint = ((Integer)(ResidueProperties.aaHash.get(s))).intValue();
	} catch (Exception e) {
	  //	  System.out.println("Exception : defaulting to " + resint + " for " + s);
	}

	SequenceGroup sg = al.findGroup(j);
	

	// Find the box colour
	Color c = Color.white;
	if (sg.displayBoxes == true) {
	  c  = findBoxColor(i,j,s);
	}
	Format ff = new Format("%6.3f");
	String red = ff.form((float)(c.getRed()/256.0));
	String green = ff.form((float)(c.getGreen()/256.0));
	String blue = ff.form((float)(c.getBlue()/256.0));

	if (sg.displayText == false) {
	  s = " ";
	} 

	// find the text colour
	if (sg.colourText == true) {
	  Color tcolor = findTextColor(i,j,s);
	  String tred = ff.form((float)(tcolor.getRed()/256.0));
	  String tgreen = ff.form((float)(tcolor.getGreen()/256.0));
	  String tblue = ff.form((float)(tcolor.getBlue()/256.0));
	  if (af.ap.seqPanel.seqCanvas.showScores) {
	    print("(" + s + ") " + xtext + " " + ytext + " " 
		  + tred + " " + tgreen + " " + tblue + " "  
		  + red + " " + green + " " + blue + " " 
		  + xorig + " " + (yorig+boxsize) + " -" + boxsize + " " + boxsize + " boxtext\n"); 
	  } else {
	    print("(" + s + ") " + xtext + " " + ytext + " " + tred + " " 
		  + tgreen + " " + tblue + " "  + red + " " + green + " " 
		  + blue + " " + xorig + " " + yorig + " -" + boxsize + " " + boxsize + " boxtext\n"); 
	  }
	} else {
	  if (af.ap.seqPanel.seqCanvas.showScores) {
	    print("(" + s + ") " + xtext + " " + ytext + " black " 
		  + red + " " + green + " " + blue + " " + xorig + " "
		  + (yorig+boxsize) + " -" + boxsize + " " + boxsize + " boxtext\n"); 
	  } else {
	    print("(" + s + ") " + xtext + " " + ytext + " black " 
		  + red + " " + green + " " + blue + " " + xorig + " "
		  + yorig + " -" + boxsize + " " + boxsize + " boxtext\n"); 
	  }
	}

	// We want a bit in here for the score drawing
	if (af.ap.seqPanel.seqCanvas.showScores) {
	  if (al.sequences[j].score[0] != null && al.sequences[j].score[0].size() > i) {
	    int score = ((Double)al.sequences[j].score[0].elementAt(i)).intValue();
	    //   System.out.println(score);
	    if (score >=0 && score < 10) {
	      Color cc = (Color)ResidueProperties.scaleColours.elementAt(((Double)al.sequences[j].score[0].elementAt(i)).intValue());
	      String sred = ff.form((float)(cc.getRed()/256.0));
	      String sgreen = ff.form((float)(cc.getGreen()/256.0));
	      String sblue = ff.form((float)(cc.getBlue()/256.0));
	      
	      print("( ) " + xtext + " " + ytext + " black " + sred + " " + sgreen + " "
		    + sblue + " " + xorig + " " + (yorig+boxsize-yspace/4) + " -" + boxsize/2 + " " + boxsize/2 + " " + boxsize + " recttext\n"); 
	    }
	  }
	}

      }
    }
    print("showpage\n");
    if (sw != null) {
      sw.flush();
    }
    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }
  }
  
  public Color findBoxColor(int i, int j,String s) {
    return (Color)af.ap.seqPanel.align.ds[j].boxColour.elementAt(i);
  }
  public Color findTextColor(int i, int j,String s) {
    return ((Color)af.ap.seqPanel.align.ds[j].boxColour.elementAt(i)).darker();
  }

  public void printinit() {
    try {
    print("%!\n");
    print("/white {1.000000 1.000000 1.000000 } def\n");
    print("/black {0.000000 0.000000 0.000000 } def\n");
    print("/gray  {0.350000 0.350000 0.350000 } def\n");
    print("/red  {1.00000 0.350000 0.350000 } def\n");
    print("/green  {0.350000 1.000000 0.350000 } def\n");
    print("/blue  {0.350000 0.350000 1.00000 } def\n");

    print("/boxtext {\n");
  
    print("/boxsize exch def\n");
    print("/negsize exch def\n");

    print("newpath\nmoveto\n\n");
    print("0 boxsize rlineto\n");
    print("boxsize 0 rlineto\n");
    print("0 negsize  rlineto\n");
    print("closepath\n");

    print("setrgbcolor fill setrgbcolor\n");
    print("moveto show\n");
    print("} def\n");

    print("/recttext {\n");
  
    print("/boxx exch def\n");
    print("/boxy exch def\n");
    print("/negy exch def\n");

    print("newpath\nmoveto\n\n");
    print("0 boxy rlineto\n");
    print("boxx 0 rlineto\n");
    print("0 negy  rlineto\n");
    print("closepath\n");

    print("setrgbcolor fill setrgbcolor\n");
    print("moveto show\n");
    print("} def\n");

    print("/" + font + " findfont\n");
    print(fontsize + " scalefont\n");
    print("setfont\n");
    } catch (Exception e) {
      System.out.println("Exception " + e);
    }
  }
}

