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

public class ScoreColourScheme extends ResidueColourScheme {
  public double min;
  public double max;
  public double[] scores;

  public ScoreColourScheme(double[] scores, double min, double max) {
    super();
    this.scores = scores;
    this.min = min;
    this.max = max;
  }

  public void setColours(DrawableSequence seq, int j) {
    Color c = Color.white;
    String s = seq.getSequence().substring(j,j+1);
    try {
      if (colourThreshold > 0 && cons != null) {
	if (aboveThreshold(seq,j,colourThreshold)) {
	  c = findColour(seq,s,j);
	} 
      } else if ( !s.equals("-")  && !s.equals(".") && !s.equals(" ")) {
	c = findColour(seq,s,j);
      } else {
	c = Color.white;
      }
    } catch (Exception e) {
      c = Color.white;
    }
    seq.setResidueBoxColour(j,c);	   
  }

  public Color findColour(DrawableSequence seq,String s,int j) {
    float red = (float)(scores[((Integer)ResidueProperties.aaHash.get(s)).intValue()]
			- (float)min)/(float)(max - min);
			if (red > (float)1.0) { red = (float)1.0;}
			if (red < (float)0.0) { red = (float)0.0;}
			
   return makeColour(red);
  }
  public Color makeColour(float c) {
    return new Color(c,(float)0.0,(float)1.0-c);
  }
  
}
