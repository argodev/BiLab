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


public class SecondaryColourScheme extends ResidueColourScheme {

  
  public void setColours(DrawableSequence seq, int j) {
    Color c = Color.white;
    String s = seq.getSequence().substring(j,j+1);
    if (colourThreshold > 0 && cons != null) {
      if (aboveThreshold(seq,j,colourThreshold)) {
	if (ResidueProperties.ssHash.containsKey(s)) {
	  c = (Color)ResidueProperties.ssHash.get(s);
	}
      }
    } else if  ( !s.equals("-")  && !s.equals(".") && !s.equals(" ")) {
      if (ResidueProperties.ssHash.containsKey(s)) {
	c = (Color)ResidueProperties.ssHash.get(s);
      } else {
	c = Color.white;
      }
    }
    seq.setResidueBoxColour(j,c);	   
  }
}
