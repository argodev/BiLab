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


public class Blosum62ColourScheme extends ResidueColourScheme {
  
  public void setColours(DrawableSequence seq , int j) {
    Color c = Color.white;
    String s = seq.getSequence().substring(j,j+1);
    if (cons != null) {
      c = findColour(seq,s,j);
    } else {
      c = Color.white;
    }
    seq.setResidueBoxColour(j,c);
  }
  public Color findColour(DrawableSequence seq, String s, int j) {
    if  ( !s.equals("-")  && !s.equals(".") && !s.equals(" ")) {
	String max = (String)cons[j].get("max");
	if (s.equals(max)) {
	  return new Color(154,154,255);
	} else if (ResidueProperties.getBLOSUM62(max,s) > 0) {
	  return new Color(204,204,255);
	} else {
	  return Color.white;
	}
      } else {
	 return Color.white;
      }
  }
}
