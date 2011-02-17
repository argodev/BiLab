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
import java.util.*;
import java.awt.*;

public class ClustalwThread extends CommandThread {
  String inFile;
  String outFile;
  PrintStream ps;

  public ClustalwThread(String inFile,String outFile) {
    super(null);

    this.inFile = inFile;
    this.outFile = outFile;

    // Test the file
    File in = new File(inFile);
    File out = new File(outFile);

    System.out.println("OS = " + System.getProperty("os.name"));
    
    if (System.getProperty("os.name").equals("Windows 95")) { 
      command = "clustalw /inFile=" + inFile + " /outFile=" + outFile + " /outorder=input";
    } else {
      command = "clustalw -inFile=" + inFile + " -outFile=" + outFile + " /outorder=input";
    }
  }
  public ClustalwThread(Sequence[] s) {
    
    try {
      long seed = 12;
      Random rnums = new Random(seed);
      
      File tmpout = null;
      File tmpnew = null;
      
      String prefix = "";
      
      if (System.getProperty("os.name").equals("Windows 95")) {
	prefix = "c:\\windows\\temp\\";
      }
      
      do {
	int r1 = rnums.nextInt();
	if (r1 < 0) { r1 = -r1;}
	
	String r1str = String.valueOf(r1);
	
	if (r1str.length() > 4) {
	  r1str = r1str.substring(0,4);
	}
	
	inFile = prefix + "pog" + r1str + ".msf";
	outFile = prefix + "pog" + r1str + ".aln";
	
	tmpout = new File(inFile);
	tmpnew = new File(outFile);
      } while (tmpout.exists() || tmpnew.exists());
      
      System.out.println(inFile);
      System.out.println(outFile);
      
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(inFile));
      String outStr = FormatAdapter.get("MSF",s);
      byte[] fileBytes = outStr.getBytes();
      for (int i = 0; i < fileBytes.length; i++) {
	bos.write(fileBytes[i]);
      }
      
      bos.flush();
      bos.close();

      System.out.println("OS = " + System.getProperty("os.name"));
      
      if (System.getProperty("os.name").equals("Windows 95")) { 
	command = "clustalw /inFile=" + inFile + " /outFile=" + outFile + " /outorder=input";
      } else {
	command = "clustalw -inFile=" + inFile + " -outFile=" + outFile + " -outorder=input";
      }

      System.out.println("Command = " + command);



      ProgressFrame pf = new ProgressFrame("Clustalw progress",this,null);
      TextAreaPrintStream taps = new TextAreaPrintStream(System.out,pf.ta);      
      this.ps = taps;
      pf.show();
      Thread t = new Thread(pf.pp);
      pf.pp.ct = this;
      t.start();


    } catch (Exception e) {
      System.out.println("Exception in clustalwThread " + e);
    }
  }

      
      
  // This is a clustalw specific hack because
  // WINDOWS 95 NEVER RETURNS FROM A PROCESS ARGGGGGHHHH!!!!!
  // AAAAAAAAAAAAAAAAAAAAAAARRRRRRRRRRRRRRRRRRRRRRRGGGGGGHHHHHHHHHHHH!!!
  public void run() {
    System.out.println("Running command: " +  command);
    try {
      p = Runtime.getRuntime().exec(command);
       
      BufferedInputStream is = new BufferedInputStream(p.getInputStream());
      int len = 0;
      byte buf[] = new byte[1000];

      while((len = is.read(buf)) != -1 ) {
	String str = new String(buf,0,0,len);

	// This is the string that stops the process
	if (str.indexOf("CLUSTAL-Alignment file created") >= 0) break;
        System.out.println(str);
	ps.print(str);
      }

      System.out.println("Command thread is done");

    } catch( java.io.EOFException eof ) { 
      System.out.println("Exception : " + eof);
    } catch (java.io.IOException e) {
      System.out.println("Exception : " + e);
    }
    done = true;
  }
}
	   
