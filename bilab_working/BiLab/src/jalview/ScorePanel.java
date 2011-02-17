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

import java.awt.*;
import java.util.*;

public class ScorePanel extends AlignmentPanel {
  
  public ScorePanel(Component p ,DrawableSequence[] s) {
    super(p,s);
    scaleheight = 0;
  }

  public void componentInit() {
    setLayout(null);
    add(idPanel);
    add(seqPanel);
    selectAll(false);
    System.out.println("Finished ScorePanel.componentInit");
  }

  public void setSequenceColor(ColourScheme c) {
  }
  public void setSequenceColor() {
  }

  public void setSequenceColor(int threshold,ColourScheme scheme) {
  }
    
  public static void main(String[] args) {
    Frame f = new Frame("SeqPanel");
    
    Sequence[] seq = FormatAdapter.read(args[0],"File","POSTAL");
    ScoreSequence[] s = new ScoreSequence[seq.length];

    for (int i=0;i < seq.length;i++) {
      s[i] = new ScoreSequence(seq[i]);
    }

    ScorePanel ap = new ScorePanel(null,s);
    f.setLayout(new BorderLayout());
    f.add("Center",ap);
    f.resize(700,500);
    f.show();
  
  }
}





