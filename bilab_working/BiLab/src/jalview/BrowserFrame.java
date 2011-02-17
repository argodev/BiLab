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


public class BrowserFrame extends TextFrame {

  Panel p4;
  Label l;
  TextField tf;
  String url;
  Button back;
  Button forward;
  SimpleBrowser sb;

  public BrowserFrame(SimpleBrowser sb,String title,int nrows, int ncols, String text,String url) {
    super(title,nrows,ncols,text);
    this.url = url;
    this.sb = sb;

    p4 = new Panel();
    l = new Label("URL : ");
    tf = new TextField(url,60);
    setTextFont(new Font("Courier",Font.PLAIN,12));
    back = new Button("Back");
    forward = new Button("Forward");

    p4.setLayout(new FlowLayout());
    p4.add(l);
    p4.add(tf);
    p4.add(back);
    p4.add(forward);

    add("North",p4);
  }

  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {
	this.hide();
	this.dispose();
	return sb.handleEvent(evt);
    }  else return super.handleEvent(evt);
  }
  
  public boolean action(Event e, Object args) {
    return sb.action(e,args);
  }
}

