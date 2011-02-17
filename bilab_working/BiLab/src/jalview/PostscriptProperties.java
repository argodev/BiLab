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

import java.util.*;



public class PostscriptProperties {



  static int PORTRAIT = 0;

  static int LANDSCAPE = 1;



  static int SHORTSIDE = 612;

  static int LONGSIDE = 792;



  static Vector fonts = new Vector();

  static {

    fonts.addElement("Helvetica");

    fonts.addElement("Times-Roman");

    fonts.addElement("Courier");

  }

  static Vector fontsizes = new Vector();

  static {

    fontsizes.addElement("6");

    fontsizes.addElement("8");

    fontsizes.addElement("10");

    fontsizes.addElement("12");

    fontsizes.addElement("14");

    fontsizes.addElement("16");

  }

  public int orientation = PORTRAIT;

  public int width = SHORTSIDE;

  public int height = LONGSIDE;
  

  public int xoffset = 30;

  public int yoffset = 30;

  public int fsize = 8;

  public String font = "Helvetica";

  

  public PostscriptProperties() {}

  public PostscriptProperties(int or, int w, int h, int xoff, int yoff, int fsize, String font) {

    this.orientation = or;

    this.width = w;

    this.height = h;

    this.xoffset = xoff;

    this.yoffset = yoff;

    this.fsize = fsize;

    this.font = font;

  }

}

  





