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


public class TextFrame extends Frame {
  TextArea ta;
  Label status;
  Button b;
  Panel p;
  Panel p2;
  Panel p3;

  public TextFrame(String title,int nrows, int ncols, String text) {
    super(title);
    ta = new TextArea(nrows,ncols);
    setText(text);
    frameInit();
  }

  public TextFrame(String title, int nrows, int ncols) {
    super(title);
    ta = new TextArea(nrows,ncols);
    frameInit();
  }

  public void frameInit() {
    ta.setBackground(Color.white);
    p = new Panel();
    p2 = new Panel();
    p3 = new Panel();

    b = new Button("close");
    status = new Label("Status:",Label.LEFT);

    p2.setLayout(new GridLayout(2,1,5,5));
    p2.add(status);

    p.setLayout(new FlowLayout());
    p.add(b);

    p3.setLayout(new GridLayout(2,1,0,0));
    p3.add(p2);
    p3.add(p);

    setLayout(new BorderLayout());
    add("Center",ta);
    
    add("South",p3);

  }

  public void setTextFont(Font f) {
    ta.setFont(f);
  }
  public void setText(String text) {
    ta.setText(text);
  }

  public String getText() {
    return ta.getText();
  }

  public boolean action(Event evt, Object arg) {
    if (evt.target == b) {
      this.hide();
      this.dispose();
    }
    System.out.println("In action");
    return false;
  }
  //  public boolean handleEvent(Event evt) {
  //  if (evt.target == b && evt.id == 1001) {
  //    System.out.println("Closing frame");
  //    this.hide();
  //    this.dispose();
  //    return false;
  // } else {
  //   return super.handleEvent(evt);
  //  }
  //}

  public static void main(String[] args) {
    TextFrame tf = new TextFrame("Test",10,72,"poggy text");
    tf.resize(500,500);
    tf.show();
  }
}








