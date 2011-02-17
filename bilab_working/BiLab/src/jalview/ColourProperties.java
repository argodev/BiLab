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

public class ColourProperties {

  public static final int ZAPPO = 0;
  public static final int TAYLOR = 1;
  public static final int PID = 2;
  public static final int BLOSUM62 = 3;
  public static final int HYDROPHOBIC = 4;
  public static final int SECONDARY = 5;
  public static final int USER = 6;
  public static final int CONSERVATION = 7;
  public static final int HELIX = 8;
  public static final int STRAND = 9;
  public static final int TURN = 10;
  public static final int BURIED = 11;
  public static final int FEATURES = 12;
  public static final int CLUSTALX = 13;

  static Vector colourSchemes = new Vector();

  static {
    colourSchemes.addElement("Zappo");
    colourSchemes.addElement("Taylor");
    colourSchemes.addElement("PID");
    colourSchemes.addElement("BLOSUM62");
    colourSchemes.addElement("Hydrophobic");
    colourSchemes.addElement("Secondary structure");
    colourSchemes.addElement("User defined");
    colourSchemes.addElement("Conservation");
    colourSchemes.addElement("Helix");
    colourSchemes.addElement("Strand");
    colourSchemes.addElement("Turn");
    colourSchemes.addElement("Buried");
    colourSchemes.addElement("Features");
    colourSchemes.addElement("Clustalx");
  }
  static int indexOf(String scheme) {

    if (colourSchemes.contains(scheme)) {
      return colourSchemes.indexOf(scheme);
    } else {
      return -1;
    }
  }
    
  static boolean contains(String scheme) {

    if (colourSchemes.contains(scheme)) {
      return true;
    } else {
      return false;
    }
  }
    
}
