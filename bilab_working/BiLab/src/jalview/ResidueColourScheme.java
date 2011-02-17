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
import MCview.*;

public class ResidueColourScheme implements ColourScheme {
  Color[] colors;
  int colourThreshold = 0;
  Hashtable[] cons;

  public ResidueColourScheme() {}

  public ResidueColourScheme(Color[] colors, int t) {
    this(colors,t,null);
  }
  public ResidueColourScheme(Color[] colors, int t, Hashtable[] cons) {
    this.colors = colors;
    this.colourThreshold = t;
    this.cons = cons;
  }
  public Color findColour(DrawableSequence seq,String s, int j) {
    return colors[((Integer)(ResidueProperties.aaHash.get(s))).intValue()];
  }
  public void setColours(DrawableSequence seq, int j) {
    Color c = Color.white;

    String s = seq.getSequence().substring(j,j+1);
    try {
      //System.out.println("Cons = " + cons + " threshold " + colourThreshold);
      if (colourThreshold > 0 && cons != null) {
	if (aboveThreshold(seq,j,colourThreshold)) {
	  c = findColour(seq,s,j);
	} 
      } else {
	c = findColour(seq,s,j);
      }
      seq.setResidueBoxColour(j,c);
    } catch (Exception e) {
      seq.setResidueBoxColour(j,Color.white);
    }
  }

  public void setColours(DrawableSequence s) {
    if (s.pdb != null) {
      ((PDBChain)s.pdb.chains.elementAt(s.maxchain)).colourBySequence();
    }
    for (int j = 0; j < s.sequence.length(); j++) {
      setColours(s,j);
    }
  }

  public void setColours(SequenceGroup sg) {
    for (int j = 0; j < sg.sequences.size(); j++) {
      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(j);
      
      for (int i = 0; i < s.getSequence().length();i++) {
	setColours(s,i);
      }
      if (s.pdb != null) {
	((PDBChain)s.pdb.chains.elementAt(s.maxchain)).colourBySequence();
      }
    }
  }
  public boolean aboveThreshold(DrawableSequence seq, int j, int threshold) {
    String s = seq.getSequence().substring(j,j+1);
    if (cons != null && j < cons.length) {
      String max = (String)cons[j].get("max");
      double sc = 0;
    
      if (cons[j].contains(s)) {
	sc = ((Double)cons[j].get(s)).floatValue();
	if  ( !s.equals("-")  && !s.equals(".") && !s.equals(" ")) {
	  if (sc >= (double)threshold) {
	    return true;
	  }
	}
      }
    }
    return false;
  }

}
