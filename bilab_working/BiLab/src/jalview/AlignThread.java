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



public class AlignThread extends Thread {

  AlignFrame af;
  String input;
  String type;
  String format;
 
  Sequence[] s = new Sequence[2000];

  Object parent;



  public AlignThread(Object parent,String input,String type, String format) {

    this.input = input;
    this.type = type;
    this.parent = parent;

  }


  public void run() {
    
    try {
      
      af = new AlignFrame(parent,input,type,format);
      af.resize(700,500);
      af.show();
    
      ConsThread ct = new ConsThread(af);
      
      ct.start();
      
    } catch (Exception e) {
      
      System.out.println("Exception : " + e);
      
    }
    
  }
  
}



