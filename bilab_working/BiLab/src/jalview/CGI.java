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

public class CGI implements Runnable {
  String server;
  Hashtable variables;
  PrintStream statout;
  int port;
  String location;
  URLConnection connection;
  PrintStream out;
  DataInputStream in;

  public CGI() {}

  public CGI(String server,int port,String location,Hashtable variables,PrintStream statout) {
    this.server = server;
    this.port = port;
    this.location = location;
    this.variables = variables;
    this.statout = statout;

  }
  public CGI(String server, int port, String location,PrintStream statout) {
    this.server = server;
    this.port = port;
    this.location = location;
    this.statout = statout;
  }
  public static boolean test(String server, int port) {
    try {
      Socket socket = new Socket(server,port);
      //Set the server connect timeout to 5 seconds
      socket.setSoTimeout(5000);
    } catch(UnknownHostException e){
       System.out.println(e);
       return false;
    } catch (SocketException e) {
       System.out.println("Socket Exception " + e);
       return false;
    } catch (InterruptedIOException e) {
       System.out.println("Read to server timed out " + e);
       return false;
    }  catch(IOException e){
       System.out.println("IOException " + e);
       return false;
    }
    return true;
  }

  public void run() {

    try {
      if (test(server,port)) {
	URL cgiServer = new URL("http://" + server + ":" + port + "/" + location);
	connection = cgiServer.openConnection();
	
	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	statout.println("Connection is " + connection);
	connection.setDoOutput(true);
	statout.println("OS = " + System.getProperty("os.name"));
	
	out = new PrintStream(connection.getOutputStream());
	
	out.println("FORM=" + URLEncoder.encode("SERVERSELECT"));
	System.out.println("Printstream = " + out);
	
	// Can only do one variable !!! Shurely not!!
	Enumeration en = variables.keys();
	while (en.hasMoreElements()) {
	  String name = (String)en.nextElement();
	  String value = (String)variables.get(name);
	  System.out.println(name);
	  System.out.println(value);
	  out.println("&" + name + "=" + URLEncoder.encode(value) + "\n");
	}
	
	out.close();
	statout.println("Transferred data to server");
	statout.println("Waiting for output data...");
	in = new DataInputStream(connection.getInputStream());
	readInput(in);
      }
    } catch (MalformedURLException ex) {
      System.out.println("Exception " + ex);
    } catch (IOException ioex) {
      System.out.println("Exception " + ioex);
    }

  }
  public void readInput(DataInputStream in) {
    
    String aln = "";
    try {
      while ((aln=in.readLine()) != null) {
	statout.println(aln);
      }
    } catch (IOException ioex) {
      System.out.println("Exception " + ioex);
    }
  }

}












