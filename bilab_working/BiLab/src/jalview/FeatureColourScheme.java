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
import java.util.*;
import java.awt.*;

public class FeatureColourScheme extends ResidueColourScheme {
 
  public void setColours(DrawableSequence seq) {
    for (int i=0; i < seq.sequence.length(); i++) {
      seq.setResidueBoxColour(i,Color.white);
    }

    if (seq.features != null && seq.features.size() != 0) {
      
      for (int i=0; i < seq.features.size(); i++) {
	//System.out.println("Feature numver = " + i + " " + seq.features.size());
	SequenceFeature sf = (SequenceFeature)seq.features.elementAt(i);
	//System.out.println("Feature = " + sf);
	if (sf.start <= seq.end && sf.end >= seq.start) {
	  int startx = seq.findIndex(sf.start)-1;
	  // System.out.println("picky");
	  int endx = seq.findIndex(sf.end)-1;
	  //System.out.println("icky");
	  if (!sf.type.equals("DISULFID")) {
	    if (endx >= 0 && startx < seq.sequence.length()-1) {
	      //  System.out.println("wicky");
	      if (startx < 0) { startx = 0;}
	      //System.out.println("sicky");
	      if (endx > seq.sequence.length()) { endx = seq.sequence.length()-1;}
	      
	      //System.out.println("Feature " + sf.start + " " + sf.end + " " + startx + " " + endx);
	      for  (int ii=startx; ii <= endx; ii++) {
		//	System.out.println("Setting " + seq.name + " " + ii + " " + sf.color + " " + sf.type);
		seq.setResidueBoxColour(ii,sf.color);
	      }
	    }
	  } else {
	    try {
	      if (startx >= 0 && startx < seq.sequence.length() -1) {
		seq.setResidueBoxColour(startx,sf.color);
		//	System.out.println("Setting " + seq.name + " " + startx + " " + sf.color + " " + sf.type);
	      }
	      if (endx >= 0 && endx < seq.sequence.length() -1) {
		seq.setResidueBoxColour(endx,sf.color);
		//System.out.println("Setting " + seq.name + " " + endx + " " + sf.color + " " + sf.type);
	      }
	    } catch (Exception e) {
	      System.out.println(e);
	    }
	   
	  }
	  //System.out.println("Done setting feature");
	}
      }
    }
  }

  public void setColours(SequenceGroup sg) {
    for (int j = 0; j < sg.sequences.size(); j++) {
      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(j);
      setColours(s);
    }
  }
  public Color findColour(DrawableSequence seq, String s, int j) {
    if (seq.features != null && seq.features.size() != 0) {
      
      for (int i=0; i < seq.features.size(); i++) {
	SequenceFeature sf = (SequenceFeature)seq.features.elementAt(i);

	int startx = seq.findIndex(sf.start)-1;
	int endx = seq.findIndex(sf.end)-1;


	if (startx < 0) { startx = 0;}
	if (endx > seq.end) { endx = seq.end;}


	if (startx <= j && endx >= j) {
	  //System.out.println("Setting colour for " + sf.type + " to " + sf.color);
	  //System.out.println("Setting " + seq.name + " " + j + " " + sf.color);
	  return sf.color;
	}
      }
    }
    return Color.white;
  }
}
