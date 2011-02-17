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



public class FormatProperties {



  static int FASTA = 0;

  static int MSF = 1;

  static int CLUSTAL = 2;

  static int BLC = 3;

  static int PIR = 4;

  static int MSP = 5;

  static int PFAM = 6;

  static int POSTAL = 7;

  static int JNET = 8;

  static Vector formats = new Vector();

  static {

    formats.addElement("FASTA");

    formats.addElement("MSF");

    formats.addElement("CLUSTAL");

    formats.addElement("BLC");

    formats.addElement("PIR");

    formats.addElement("MSP");

    formats.addElement("PFAM");

    formats.addElement("POSTAL");
    formats.addElement("JNET");
  }

  static int indexOf(String format) {

    format.toUpperCase();

    if (formats.contains(format)) {

      return formats.indexOf(format);

    } else {

      return -1;

    }

  }

    

  static boolean contains(String format) {

    format.toUpperCase();



    if (formats.contains(format)) {

      return true;

    } else {

      return false;

    }

  } 
}

  



  



