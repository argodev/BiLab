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
import java.io.*;

public class HelpFile extends FileParse {
  String text = "";

  public HelpFile(String inStr) {
    readLines(inStr);
    parse();
  }

  public HelpFile(String inFile, String type) throws IOException {
    super(inFile,type);
    readLines();
    parse();
  }

  public void parse() {
    for (int i=0; i < lineArray.size(); i++) {
      text += SimpleBrowser.parse((String)lineArray.elementAt(i)) + "\n";
    }
  }

  public String print() {
    return text;
  }

  public static void main(String[] args) {
    try {
      HelpFile hf = new HelpFile("http://circinus.ebi.ac.uk:6543/jalview/contents.html",
				 "URL");
      TextFrame tf = new TextFrame("Jalview help",10,72,hf.print());
      tf.ta.setFont(new Font("Helvetica",Font.PLAIN,12));
      tf.resize(500,700);
      tf.show();
      //      hf.print();
    }catch (IOException e) {
      System.out.println("Exception " + e);
    }
  }
}
