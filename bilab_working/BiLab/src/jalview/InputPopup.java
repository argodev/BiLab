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

public class InputPopup extends Popup {
  TextArea ta;
  Label format;
  Choice f;

  public InputPopup(Frame parent,String title) {
    super(parent,title);

    ta = new TextArea(25,80);
    ta.setFont(new Font("Courier",Font.PLAIN,12));
    format = new Label("Alignment format");
    f = new Choice();

    for (int i = 0; i < FormatProperties.formats.size(); i++) {
      f.addItem((String)FormatProperties.formats.elementAt(i));
    }

    gbc.fill = GridBagConstraints.NONE; 

    gbc.insets = new Insets(20,20,20,20);

    add(ta,gb,gbc,0,0,4,4);
    add(format,gb,gbc,0,4,1,1);
    add(f,gb,gbc,1,4,1,1);

    add(status,gb,gbc,0,5,1,1);
    add(apply,gb,gbc,0,6,1,1);
    add(close,gb,gbc,1,6,1,1);
    pack();
    show();
  }
  public boolean handleEvent (Event e) {
    if (e.target == apply && e.id == 1001) {
      DrawableSequence[] s = null;
      String inStr = ta.getText();
      if (FormatProperties.contains(f.getSelectedItem())) {
	s = FormatAdapter.toDrawableSequence(FormatAdapter.read(f.getSelectedItem(),inStr));
      } else {
	System.out.println("Format not supported");
      }
      if (s != null) {
	AlignFrame af = new AlignFrame(parent.getParent(),s);
	System.out.println("Size = " + af.ap.seqPanel.align.size());
	if (af.ap.seqPanel.align.size() > 0) {
	  af.resize(700,500);
	  af.show();
	  ConsThread ct = new ConsThread(af);
	  ct.start();
	  this.hide();
	  this.dispose();
	} else {
	  af.dispose();
	  status.setText("Can't read input - check format");
	}
      } else {
	status.setText("No sequences found");
      }
      return true;
    } else {
      return super.handleEvent(e);
    }
  }
}
