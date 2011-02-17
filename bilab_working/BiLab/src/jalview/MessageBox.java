package jalview;



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

 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 */



import java.awt.*;

import java.awt.event.*;

import java.applet.*;

import java.util.*;



public class MessageBox extends Popup {

  String message;

  DrawCanvas can;



  public MessageBox(Frame parent, String title, String message) {

    super(parent,title);

    this.message = message;

    this.can = new DrawCanvas(message);



    gbc.fill = GridBagConstraints.NONE; 

    gbc.insets = new Insets(20,20,20,20);



    can.resize(200,200);

    add(can,gb,gbc,0,0,2,2);

      

    add(apply,gb,gbc,0,2,1,1);

    add(close,gb,gbc,1,2,1,1);



    this.pack();

    this.show();



  }



  public boolean handleEvent(Event evt) {

    if (evt.target == apply && evt.id == 1001) {

      System.out.println("Applied");

      return true;

    }

    return super.handleEvent(evt);

  }

  public static void main(String[] args) {

    Frame f = new Frame("titley");

    f.resize(100,100);

    f.show();

    MessageBox mb = new MessageBox(f,"Message box","Hello my name is\npog");

  }

}





class DrawCanvas extends Canvas {

  String s;

  public DrawCanvas(String message) {

    super();

    this.s = message;

  }



  public void paint(Graphics g) {

    StringTokenizer st = new StringTokenizer(s,"\n");

    int y = 20;

    while (st.hasMoreTokens()) {

      String tmp = st.nextToken();

      System.out.println(tmp);

      g.drawString(tmp,40,y);

      y = y + 20;

    }

  }

}





