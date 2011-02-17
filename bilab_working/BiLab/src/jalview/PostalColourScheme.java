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

public class PostalColourScheme extends ResidueColourScheme {
  Vector colours = ResidueProperties.scaleColours;

  public PostalColourScheme() {
    super();
  }

  public void setColours(DrawableSequence seq, int j) {
    Color c = Color.white;
    String s = seq.getSequence().substring(j,j+1);
    c = findColour(seq,s,j);
    seq.setResidueBoxColour(j,c);	   
  }

  public Color findColour(DrawableSequence seq,String s,int j) {
    int i = ((Double)seq.score[0].elementAt(j)).intValue();
    if ( i >0 && i < colours.size()) {
      return (Color)colours.elementAt(i);
    } else {
      return Color.white;
    }
  }
}



