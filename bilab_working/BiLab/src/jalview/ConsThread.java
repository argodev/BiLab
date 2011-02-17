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

import java.net.*;
import java.awt.*;

public class ConsThread extends Thread {
  AlignFrame af;

  public ConsThread(AlignFrame af) {
    this.af = af;
  }

  public void run() {
    try {
      long tstart = System.currentTimeMillis();
      af.status.setText("Calculating consensus...");
      af.ap.seqPanel.align.percentIdentity(af.ap.sel);
      af.status.setText("done");
      af.cons = af.ap.seqPanel.align.cons;
      af.ap.seqPanel.align.setColourScheme(ColourAdapter.get(ColourProperties.PID));
     

      af.ap.selectAll(false);
      af.ap.idPanel.idCanvas.paintFlag = true;
      af.ap.idPanel.idCanvas.repaint();
      af.ap.idPanel.invalidate();
      af.ap.validate();
           long tend = System.currentTimeMillis();
      System.out.println("Time taken for consensus calculation  = " + (tend-tstart));

   } catch (Exception e) {
      System.out.println("Exception : " + e);
    }
  }
}
    
