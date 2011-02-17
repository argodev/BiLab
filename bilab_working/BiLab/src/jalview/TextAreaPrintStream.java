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
import java.io.*;

public class TextAreaPrintStream extends PrintStream {
  TextArea ta;
  OutputStream os;

  public TextAreaPrintStream(OutputStream os, TextArea ta) {
    super(os);
    this.os = os;
    this.ta = ta;
  }
  public void print(String s) {
    ta.setText(ta.getText() +  s);
    // System.out.println(s);
    //    ta.append(s);
	//ta.setCaretPosition(ta.getText().length());
  }
  
  public void println() {
    print("\n");
  }
  
  public void println(String s) {
    print(s); println();
  }
  
  public void println(float f) {
    print(f); println();
  }
  
  
  public static void main(String[] args) {
    TextArea ta = new TextArea(70,20);
    Frame f = new Frame();
    
    f.setLayout(new BorderLayout());
    f.add("Center",ta);
    f.resize(700,500);
    f.show();
    
    TextAreaPrintStream taps = new TextAreaPrintStream(System.out,ta);
    taps.println("hello");
    while (true) {
      try {
	Thread.sleep(500);
      } catch (InterruptedException ie) {}
      taps.println("pog " + Math.random());
    }
  }
}


  
