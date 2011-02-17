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


public class Consensus {
  //  String id;
  //  String maskstr;
  int[] mask;
  double threshold;
  
  public Consensus(String mask, double threshold) {
    // this.id = id;
    //    this.mask = mask;
    //    this.maskstr = mask;
    setMask(mask);
    this.threshold = threshold;
  }

  public void setMask(String s) {
    this.mask = Sequence.setNums(s);
    //   for (int i=0; i < mask.length; i++) {
    //  System.out.println(mask[i] + " " + ResidueProperties.aa[mask[i]]);
    // }
  }

  public boolean isConserved(int[][] cons2,int col, int res,int size) {
    int tot = 0;

    for (int i=0; i < mask.length; i++) {
      //      System.out.println("Mask " + cons2[mask[i]][col] + " " + ResidueProperties.aa[mask[i]] + " " + col);
      tot += cons2[col][mask[i]];
    }
    //    System.out.println("Total " + tot + " " + maskstr + " " + threshold*size/100);
    if ((double)tot > threshold*size/100) {
      return true;
    }
    return false;
  }
      
}
    
