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

import java.applet.Applet;
import java.awt.*;
import java.util.StringTokenizer;

public class AlignApplet extends Applet {
  //  Button b;
  String input;
  String type;
  AlignFrame af;
  String fontSize = "10";
  String format = "MSF";
  int noGroups;

  String consString = "*";
  String local="";
  ConsThread ct;
  String mailServer;
  String clustalServer;
  String srsServer;
  String database;

  public void init() {
    input = getParameter("input");
    type = getParameter("type");
    fontSize = getParameter("fontsize");

    mailServer = getParameter("mailServer");
    clustalServer = getParameter("clustalServer");
    srsServer = getParameter("srsServer");
    database = getParameter("database");

    local = getParameter("local");
    
    try {
      noGroups = (Integer.valueOf(getParameter("groups"))).intValue();
      System.out.println("Number of groups = " + noGroups);
    } catch (Exception e) {
      //      System.out.println("Exception in nogroups : " + e);
    }
    
    format = getParameter("format");
    if (format == null || format.equals("")) {
      format = "MSF";
    }

    format = format.toUpperCase();

    System.out.println("Format = " + format);
    consString =  getParameter("Consensus");

    if (consString == null) { 
      consString = "*";
    }

    componentInit();

  }
  public void componentInit() {
    makeFrame();
  }

  public void makeFrame() {
    try {
      String s = getParameter("numseqs");
      if (getParameter("numseqs") == null) {
	af = new AlignFrame(this,input,type,format);
      } else {
	int num = (Integer.valueOf(getParameter("numseqs"))).intValue();
	int i = 0; 
	int count = 0;
	DrawableSequence[] seqs = new DrawableSequence[num];
	while (i < num) {
	  String s2 = getParameter("seq" + (i+1));
	  if (s2 != null) {
	    String id = getParameter("id" + (i+1));
	    if (id == null) {
	      id = "Seq_" + i;
	    }
	    seqs[count] = new DrawableSequence(id,s2,1,s2.length());
	    count++;
	  } else {
	    System.out.println("Can't read sequence " + (i+1));
	  }
	  i++;
	}
	if (count > 0) {
	  af = new AlignFrame(this,seqs);
	} else {
	  System.out.println("No sequences found");
	  this.stop();
	}
      }
      
      af.setTitle("Jalview alignment editor");
      
      if (mailServer != null) {
	if (!(mailServer.equals(""))) {
	  af.mp.server = mailServer;
	} else {
	  af.mp.server = "";
	}
      } 
      
      if (srsServer != null) {
	if (!(srsServer.equals(""))) {

	  if (!srsServer.substring(srsServer.length()-1).equals("/")) {
	    srsServer = srsServer + "/";
	  }
	  af.srsServer = srsServer;
	  System.out.println("Srs server = " + af.srsServer);
	}
      } 
      if (database != null) {
	if (!(database.equals(""))) {
	  af.database = database;
	  System.out.println("Srs database = " + af.database);
	}
      } 
      
      try {
	int fs = (Integer.valueOf(fontSize)).intValue();
	//	    af.ap.seqPanel.seqCanvas.f = new Font("Courier",Font.PLAIN,fs);
	af.ap.seqPanel.seqCanvas.setFont(Font.PLAIN,fs);
      } catch (Exception ex) {
	System.out.println("Exception in font size : " + ex);
	//af.ap.seqPanel.seqCanvas.f = new Font("Courier",Font.PLAIN,10)
	af.ap.seqPanel.seqCanvas.setFont(Font.PLAIN,10);
      }
      System.out.println("Consensus string " + consString);
      if (consString.equals("*")) {  
	af.ap.selectAll(true);
      } else {
	int seqs[] = selectSeqs(consString);
	int i = 0;
	af.ap.selectAll(false);
	while (i < seqs.length && seqs[i] != -1) {
	  af.ap.sel.addElement(af.ap.seqPanel.align.sequences[seqs[i]]);
	  i++;
	}
      }

      af.status.setText("Calculating consensus...");
      af.ap.seqPanel.align.percentIdentity(af.ap.sel);
      af.ap.seqPanel.align.percentIdentity2();
      af.ap.seqPanel.align.findQuality();
      af.cons = af.ap.seqPanel.align.cons;
      af.status.setText("done");
      
      //ScoreSequence[] sseq = new ScoreSequence[1];
      
      //sseq[0] = af.ap.seqPanel.align.qualityScore;
      //af.bp.setScorePanel(new ScorePanel(af,sseq));
      
      if (noGroups > 0) {
	for (int i = 0; i < noGroups; i++) {
	  af.status.setText("Parsing group " + (i+1));
	  String gs = getParameter("group" + (i+1));
	  System.out.println("Group = " + gs);
	  
	  parseGroup(af.ap.seqPanel.align,gs);
	}
      } else {
	af.status.setText("Setting colour scheme...");
	
	
	
	//	PIDColourScheme pidcs = new PIDColourScheme(af.ap.seqPanel.align.cons);
	ClustalxColourScheme pidcs = new ClustalxColourScheme(af.ap.seqPanel.align.cons2,af.ap.seqPanel.align.size());
	af.ap.seqPanel.align.setColourScheme(pidcs);
      }
      af.status.setText("done");	
      af.updateFont();
      af.ap.selectAll(false);
      af.ap.idPanel.idCanvas.paintFlag = true;
      af.ap.idPanel.idCanvas.repaint();
      
      //af.updateFont();
    } catch (Exception ex) {
      System.out.println("Exception in applet : " + ex);
    }
  }

  public void parseGroup(Alignment al, String gs) {
    int col = 0;
    boolean boxes = true;
    boolean text = true;
    boolean colourText = false;

    StringTokenizer st = new StringTokenizer(gs,":");
    try {
      int seqs[] = selectSeqs(st.nextToken());

      String tmp = st.nextToken();
      col = 0;
      tmp.toUpperCase();
      System.out.println(tmp);

      if (tmp.equals("SECONDARY")) { tmp = "Secondary structure";}
      if (tmp.equals("ZAPPO")) { tmp = "Zappo";}
      if (tmp.equals("TAYLOR")) { tmp = "Taylor";}
      if (tmp.equals("HYDROPHOBIC")) { tmp = "Hydrophobic";}
      if (tmp.equals("USERDEFINED")) { tmp = "User defined";}
      if (tmp.equals("HELIX")) { tmp = "Helix";}
      if (tmp.equals("STRAND")) { tmp = "Strand";}
      if (tmp.equals("TURN")) { tmp = "Turn";}
      if (tmp.equals("CLUSTALX")) {tmp = "Clustalx";}

      ColourScheme cs = ColourAdapter.get(tmp);

      if (cs == null) {
	cs = new TaylorColourScheme();
      }

      System.out.println("Colour scheme = " + cs);
       tmp = st.nextToken();
       tmp = tmp.toUpperCase();

       if (tmp.equals("TRUE")) {
 	boxes = true;
       } else if (tmp.equals("FALSE")){
 	boxes = false;
       }

       tmp = st.nextToken();
       tmp = tmp.toUpperCase();

       if (tmp.equals("TRUE")) {
 	text = true;
       } else if (tmp.equals("FALSE")) {
 	text = false;
       }

       tmp = st.nextToken();
       tmp = tmp.toUpperCase();

       if (tmp.equals("TRUE")) {
 	colourText = true;
       } else if (tmp.equals("FALSE")){
 	colourText = false;
       }
       System.out.println("boxes = " + boxes);
       System.out.println("text = " + text);

       SequenceGroup sg = new SequenceGroup(cs,true,boxes,text,colourText,true);

	 
       System.out.println("Sequence group " + sg);
       af.ap.seqPanel.align.addGroup(sg);

       int i = 0;
       while(seqs[i] != -1) {
 	if (af.ap.seqPanel.align.sequences[seqs[i]] != null) {
 	  if (af.ap.seqPanel.align.findGroup(seqs[i]) != null) {
 	    af.ap.seqPanel.align.removeFromGroup(af.ap.seqPanel.align.findGroup(seqs[i]),
 						 af.ap.seqPanel.align.sequences[seqs[i]]);
 	  }
 	   sg.addSequence(af.ap.seqPanel.align.sequences[seqs[i]]);
 	 }
 	i++;
       }

       System.out.println("Colourscheme is " + cs);
       if ((af.cons != null) && (cs instanceof ResidueColourScheme)) {
	 System.out.println("Setting colour scheme " + cs);
	 ((ResidueColourScheme)sg.colourScheme).cons = af.cons;
       }
       if ((af.cons != null) && (cs instanceof ClustalxColourScheme)) {
	 System.out.println("Setting colour scheme " + cs);
	 ((ClustalxColourScheme)sg.colourScheme).cons = af.cons;
	 System.out.println("Consensus is" + af.cons);
       }

       af.ap.seqPanel.align.displayText(sg);
       af.ap.seqPanel.align.displayBoxes(sg);
       af.ap.seqPanel.align.colourText(sg);
       af.ap.seqPanel.align.setColourScheme(sg);

     } catch (Exception e) {
       System.out.println("Exception : " + e);
     }
  }
  
  public int[] selectSeqs(String s) {
    StringTokenizer st = new StringTokenizer(s,",");
    
    int[] seqs = new int[2000];
    int count = 0;

    while (st.hasMoreTokens()) {
      String tmp = st.nextToken();
      if (tmp.equals("*")) {
	for (int i=0; i < af.ap.seqPanel.align.sequences.length; i++) {
	  seqs[i] = i;
	}
      } else  if (tmp.indexOf("-") >= 0) {
	try {
	  StringTokenizer st2 = new StringTokenizer(tmp,"-");
	  
	  int start = (Integer.valueOf(st2.nextToken())).intValue();
	  int end = (Integer.valueOf(st2.nextToken())).intValue();
	  
	  if (end > start) {
	    for (int i = start; i <= end; i++) {
	      //	      System.out.println("Adding " + i + " to group");
	      seqs[count] = i-1;
	      count++;
	    }
	  }
	} catch (Exception e) {
	  System.out.println("Exception : " + e);
	}
      } else {
	try {
	  seqs[count] = (Integer.valueOf(tmp)).intValue()-1;
	  System.out.println("Adding " + seqs[count] + " to group");
	  count++;
	} catch (Exception e) {
	  System.out.println("Exception : " + e);
	}
      }
    }
    seqs[count] = -1;

    return seqs;
  }
}













