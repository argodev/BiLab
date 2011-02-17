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

public class ColourAdapter {
 
  public static ColourScheme get(String scheme) {
    if (ColourProperties.contains(scheme)) {
      if (ColourProperties.indexOf(scheme) == ColourProperties.ZAPPO) {
	return new ZappoColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.TAYLOR) {
	return new TaylorColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.HELIX) {
	return new HelixColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.STRAND) {
	return new StrandColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.TURN) {
	return new TurnColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.BURIED) {
	return new BuriedColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.PID) {
	return new PIDColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.BLOSUM62) {
	return new Blosum62ColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.SECONDARY) {
	return new SecondaryColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.USER) {
	//return new UserColourScheme();
	return new ZappoColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.CONSERVATION) {
	//	return new ConservationColourScheme();
	return new ZappoColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.FEATURES) {
	return new FeatureColourScheme();
      } else  if (ColourProperties.indexOf(scheme) == ColourProperties.CLUSTALX) {
	return new ClustalxColourScheme();
      }
    } else {
      // Should throw exception here
      return null;
    }
    return null;
  }
  public static ColourScheme get(int scheme) {
    if (scheme == ColourProperties.ZAPPO) {
      return new ZappoColourScheme();
    } else if (scheme == ColourProperties.TAYLOR) {
      return new TaylorColourScheme();
    } else if (scheme == ColourProperties.HELIX) {
      return new HelixColourScheme();
    } else if (scheme == ColourProperties.STRAND) {
      return new StrandColourScheme();
    } else if (scheme == ColourProperties.TURN) {
      return new TurnColourScheme();
    } else if (scheme == ColourProperties.BURIED) {
      return new BuriedColourScheme();
    } else if (scheme == ColourProperties.PID) {
       return new PIDColourScheme();
    } else if (scheme == ColourProperties.BLOSUM62) {
      return new Blosum62ColourScheme();
    } else if (scheme == ColourProperties.HYDROPHOBIC) {
      return new HydrophobicColourScheme();
    } else if (scheme == ColourProperties.SECONDARY) {
      return new SecondaryColourScheme();
    } else if (scheme == ColourProperties.USER) {
      //return new UserColourScheme();
      return new ZappoColourScheme();
    } else if (scheme == ColourProperties.CONSERVATION) {
      //return new ConservationColourScheme();
      return new ZappoColourScheme();
    } else if (scheme == ColourProperties.FEATURES) {
      return new FeatureColourScheme();
    } else if (scheme == ColourProperties.CLUSTALX) {
      return new ClustalxColourScheme();
    } else {
      // Should throw exception here
      return null;
    }
  }
  public static int get(ColourScheme cs) {
    if (cs instanceof ZappoColourScheme) { return 0;}
    else if (cs instanceof PIDColourScheme) { return 1;}
    else if (cs instanceof TaylorColourScheme) { return 2;}
    else if (cs instanceof Blosum62ColourScheme) { return 3;}
    else if (cs instanceof HydrophobicColourScheme) { return 4;}
    else if (cs instanceof SecondaryColourScheme) { return 5;}
    else if (cs instanceof ConservationColourScheme) { return 6;}
    else if (cs instanceof UserColourScheme) { return 7;}
    else if (cs instanceof HelixColourScheme) { return 8;}
    else if (cs instanceof StrandColourScheme) { return 9;}
    else if (cs instanceof TurnColourScheme) { return 10;}
    else if (cs instanceof BuriedColourScheme) { return 11;}
    else if (cs instanceof FeatureColourScheme) { return 12;}
    else if (cs instanceof ClustalxColourScheme) { return 13;}

    else return -1;
  }
}


