 /* Copyright (C) 1998  Michele Clamp
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
 * Foundation, Inc, 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jalview;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;

public class SequenceProperties {

  public static String fullBackTranslate(String prot) {
    String s = "";
    for (int i=0; i < prot.length(); i++) {
      String tmp = prot.substring(i,i+1);

      Vector codons = ResidueProperties.getCodons(tmp);

      if (codons != null) {
	for (int j=0; j < codons.size(); j++) {
	  s = s + (String)codons.elementAt(j);

	}
      }
    }
    return s;
  }
  public static String backTranslate(String prot) {
    String s = "";
    for (int i=0; i < prot.length(); i++) {
      String tmp = prot.substring(i,i+1);
      Vector codons = ResidueProperties.getCodons(tmp);
      if (codons != null) {
	  s = s + (String)codons.elementAt(0);
      } else {
	s = s + "NNN";
      }
    }
    return s;
  }
  public static String translate(String dna) {
    int nocods = (int)dna.length()/3;
    String prot = "";
    for (int i=0; i < nocods; i++) {
      String cod = dna.substring(i*3,(i+1)*3);
      prot = prot + ResidueProperties.codonTranslate(cod);
    }
    return prot;
  }
}
