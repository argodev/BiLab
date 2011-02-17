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
import java.util.Vector;
import java.awt.*;

public class SequenceGroup {

  public Color[] color;
  public ColourScheme colourScheme;
  boolean isSelected;
  public boolean displayBoxes; // -DJ made public
  boolean displayText;
  boolean colourText;
  boolean display;
  Conservation conserve;
  
  public Vector sequences = new Vector();

  public SequenceGroup() {
    this.colourScheme = new ZappoColourScheme();
    this.isSelected = false;
    this.displayBoxes = true;
    this.displayText = true;
    this.colourText = false;
    this.display = true;
  }

  public SequenceGroup( ColourScheme scheme, boolean isSelected,
			boolean displayBoxes, boolean displayText,
			boolean colourText,
			boolean display) {
    this.colourScheme = scheme;
    this.isSelected = isSelected;
    this.displayBoxes = displayBoxes;
    this.displayText = displayText;
    this.colourText = colourText;
    this.display = display;
  }

  
  public void addSequence(Sequence s) {
    sequences.addElement(s);
  }

  public void deleteSequence(Sequence s) {
	sequences.removeElement(s);
  }
}
  




















