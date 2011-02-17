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

import java.io.*;

public class CommandThread extends Thread {
  String command;
  Process p = null;
  StringBuffer sb;
  boolean done = false;

  public CommandThread() {
  }
  public  CommandThread (String command) {
    this.command = command;
  }

  public void run() {
    if (!command.equals("")) {
      System.out.println("Running command: " +  command);
      try {
	p = Runtime.getRuntime().exec(command);
	
	BufferedInputStream is = new BufferedInputStream(p.getInputStream());
	int len = 0;
	byte buf[] = new byte[100];
	
	while( p != null && is != null && (len = is.read(buf)) != -1 ) {
	  String str = new String(buf,0,0,len);
	  System.out.println(str);
	}
	
	System.out.println("Command thread is done");
	
      } catch( java.io.EOFException eof ) { 
	System.out.println("Exception : " + eof);
      } catch (java.io.IOException e) {
	System.out.println("Exception : " + e);
      }
      done = true;
    } else {
      System.out.println("Can't run process: null command");
    }
  }
}

