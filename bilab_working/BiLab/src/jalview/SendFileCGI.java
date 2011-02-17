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
import java.io.*;
import java.util.*;


public class SendFileCGI extends CGI {
  String outstr;
  String file;
 
  public SendFileCGI(String server, int port, String location,String file, PrintStream statout, String outstr) {
     super(server,port,location,statout);
     this.outstr = outstr;
     this.file = file;
  }

  public void run() {

    try {
      if (test(server,port)) {
	byte bytestr[] = outstr.getBytes();
	
	URL cgiServer = new URL("http://" + server + ":" + port + "/" + location + "?" + file);
	connection = cgiServer.openConnection();
	connection.setDoOutput(true);
	connection.setDoInput(true);

	//	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	connection.setRequestProperty("Content-Type", "application/octet-stream");
	connection.setRequestProperty("Content-length","" + bytestr.length);
	statout.println("Connection is " + connection);

	statout.println("OS = " + System.getProperty("os.name"));
	
	out = new PrintStream(connection.getOutputStream());
	
	//	out.println("FORM=" + URLEncoder.encode("SERVERSELECT"));
	out.write(bytestr);
	System.out.println("Printstream = " + out);
	
	// Can only do one variable !!! Shurely not!!
// 	Enumeration en = variables.keys();
// 	while (en.hasMoreElements()) {
// 	  String name = (String)en.nextElement();
// 	  String value = (String)variables.get(name);
// 	  System.out.println(name);
// 	  System.out.println(value);
// 	  out.println("&" + name + "=" + URLEncoder.encode(value) + "\n");
// 	}
	
	out.close();
	statout.println("Transferred data to server");
	statout.println("Waiting for output data...");
	in = new DataInputStream(connection.getInputStream());
	String line = in.readLine();
	System.out.println(line);
	in.close();
        try
        {
            int result = Integer.valueOf(line).intValue();
            System.out.println(result);
        }
        catch (Exception e)
        {
	  //            return -1;
        }
	//	readInput(in);
	
      }
    } catch (MalformedURLException ex) {
      System.out.println("Exception " + ex);
    } catch (IOException ioex) {
      System.out.println("Exception " + ioex);
    }

  }
  public static void main(String[] args) {
    String out = "public class PostPutFile\n    public static int put(URL url, String filename, byte bytes[])\n        throws IOException, MalformedURLException";
    SendFileCGI sf= new SendFileCGI("circinus.ebi.ac.uk",6543,"cgi-bin/sendfile","out",System.out,out);
    sf.run();
  }

}
