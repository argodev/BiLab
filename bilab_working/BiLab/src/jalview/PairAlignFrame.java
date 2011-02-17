/* Jalview  - a java multiple alignment editor
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

public class PairAlignFrame extends TextFrame {

  DrawableSequence[] s = new DrawableSequence[2000];
  Button align;
  int noseqs = 0;

  public PairAlignFrame(String title,int nrows, int ncols, String text) {
    super(title,nrows,ncols,text);
    align = new Button("View in alignment editor");
    p.add(align);
  }

  public void addSequence(DrawableSequence s) {
    this.s[noseqs] = s;
    noseqs++;
  }

  public boolean handleEvent(Event evt) {
    if (evt.target == align && evt.id == 1001) {
      AlignFrame af  = new AlignFrame(this,s);
      af.resize(700,200);
      af.show();
      return true;
    } else if (evt.target == b && evt.id == 1001) {
      System.out.println("Closing frame");
      this.hide();
      this.dispose();
      return true;
    } else if (evt.id == Event.WINDOW_DESTROY) {
      this.hide();
      this.dispose();
      return true;
    } else {
      return super.handleEvent(evt);
    }
  }
}


    
