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
import java.net.*;

public class Mail {
  BufferedReader in;
  BufferedWriter out;
  Socket s ;

  public static void main(String s[]) {

  Mail t = new Mail();
  t.send(s[0], s[1]);
  t.finish();
  }
  
  public void send(String mailServer, String recipient, String author ,String subject, String text) {
    try {   
      s = new Socket(mailServer, 25);

      in = new BufferedReader
	(new InputStreamReader(s.getInputStream(), "8859_1"));
      out = new BufferedWriter
	(new OutputStreamWriter(s.getOutputStream(), "8859_1"),16384);
      
      send(in, out, "HELO theWorld");
      send(in, out, "MAIL FROM: " + author);
      send(in, out, "RCPT TO: " + recipient);
      send(in, out, "DATA");
      send(out, "Subject: " + subject);
      send(out, "From: " + author);
      send (out, "\n");      
      // message body
      send(out, text);
      //send(out, "\n.\n");
      //send(in, out, "QUIT");
      //s.close();
      }
   catch (Exception e) {
      e.printStackTrace();
      }
   }
   
  public void finish() {
    try {
      send(out,"\n.\n");
      send(in,out, "QUIT");
      s.close();
    } catch (Exception e) {
      System.out.println("Exception : " +  e);
    }
  }

public void send(String mailServer, String recipient) {
   try {   
      Socket s = new Socket(mailServer, 25);
      BufferedReader in = new BufferedReader
          (new InputStreamReader(s.getInputStream(), "8859_1"));
      BufferedWriter out = new BufferedWriter
          (new OutputStreamWriter(s.getOutputStream(), "8859_1"));

      send(in, out, "HELO theWorld");
      send(in, out, "MAIL FROM: <Elvis.Presley@jailhouse.rock>");
      send(in, out, "RCPT TO: " + recipient);
      send(in, out, "DATA");
      send(out, "Subject: In the ghetto");
      send(out, "From: Elvis Presley <Elvis.Presley@jailhouse.rock>");
      send (out, "\n");      
      // message body
      send(out, "I'm alive. Help me!");
      send(out, "\n.\n");
      send(in, out, "QUIT");
      s.close();
      }
   catch (Exception e) {
      e.printStackTrace();
      }
   }
   
 public void send(BufferedReader in, BufferedWriter out, String s) {
   try {
      out.write(s + "\n");
      out.flush();
      s = in.readLine();
      }
   catch (Exception e) {
      e.printStackTrace();
      }
   }

 public void send(BufferedWriter out, String s) {
   try {
      out.write(s + "\n");
      out.flush();
      }
   catch (Exception e) {
      e.printStackTrace();
      }
   }
} 
