package jalview;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.applet.*;
import MCview.*;

public class SequenceFeatureThread extends Thread {
  Object parent;
  int find = -1;


  public SequenceFeatureThread(Object parent) {
    this.parent = parent;
    System.out.println("pog");
  }

  public void run() {
    System.out.println("wog");
    if (parent instanceof AlignFrame) {
      AlignFrame af = (AlignFrame)parent;
      try {
	// Test the server can be accessed
	String tmp = af.srsServer;
	String page;
	String server; 
	int port;
	page = "/";
	if (tmp.indexOf("/") != -1) {
	  page = tmp.substring(tmp.indexOf("/"));
	} 
	if (af.srsServer.indexOf(":") >= 0) {
          System.out.println("server = " + af.srsServer);
	  port = Integer.parseInt(tmp.substring(tmp.indexOf(":")+1, tmp.indexOf("/")));
	  server = tmp.substring(0,tmp.indexOf(":"));
	} else {
	  port = 80;
	  server = tmp.substring(0,tmp.indexOf("/"));
	}
	System.out.println("eek");
	// Check if applet and if the server is allowed
	if (af.parent instanceof Applet) {
	  Applet app = (Applet)af.parent;
	  if (! app.getCodeBase().getHost().equals(server)) {
	    af.status.setText("ERROR: SRS server must be the applet host");
	  } 
	}
	System.out.println("ook");
	if (CGI.test(server,port)) {
	  System.out.println("ork");
	  if (af.ap.sel.size() != 0) {
	    if (af.ff == null || af.ff.isVisible() == false) {
	      af.ff = new FeatureFrame(af,"Sequence feature console",15,72,"");
	      af.ff.setTextFont(new Font("Courier",Font.PLAIN,12));
	      af.ff.resize(500,400);
	      af.ff.show();
	      
	    }
	    find = 0;
	    af.ap.seqPanel.align.getFeatures(af.ff.ta,af.ap.sel,af.srsServer,af.database);
	    find = 1;
	  } else {
	    if (af.ff == null || af.ff.isVisible() == false) {
	      af.ff = new FeatureFrame(af,"Sequence feature console",15,72,"");
	      af.ff.setTextFont(new Font("Courier",Font.PLAIN,12));
	      af.ff.resize(500,400);
	      af.ff.show();
	    }
	    find = 0;
	    System.out.println("Fetching features");
	    af.ap.seqPanel.align.getFeatures(af.ff.ta,af.srsServer,af.database);
	    System.out.println("Fetched features");
	    find = 1;

	  }
	  af.status.setText("Setting feature colours");
	  af.status.validate();
	  FeatureColourScheme ftcs = new FeatureColourScheme();
	  System.out.println("Setting feature colour scheme");
	  af.ap.seqPanel.align.setColourScheme(ftcs);
	  System.out.println("Done coloour Scheme");

	  af.updateFont();
	  af.status.setText("done");
	  af.status.validate();

// 	  boolean found = false;
// 	  for (int i=0; i < af.ap.seqPanel.align.ds.length; i++) {
	    
// 	    DrawableSequence seq = (DrawableSequence)af.ap.seqPanel.align.ds[i];
// 	    if (seq.pdbcode.size() > 0) {
// 	      found = true;
// 	      break;
// 	    }
// 	  }
// 	  if (found) {
// 	    af.structures.enable();
// 	  }

	} else {
	  af.status.setText("ERROR: srs server can't be reached");
	  af.status.validate();
	}
      } catch (StringIndexOutOfBoundsException ex) {
	af.status.setText("ERROR: invalid SRS server name \"" + af.srsServer + "\"");
      }
    }
  }

  public void fetchPDBstructures() {
    if (parent instanceof AlignFrame) {
      AlignFrame af = (AlignFrame)parent;
      
      for (int i=0; i < af.ap.seqPanel.align.ds.length; i++) {
	
	try {
	  DrawableSequence seq = (DrawableSequence)af.ap.seqPanel.align.ds[i];
	  if (seq.pdbcode.size() > 0) {
	    PDBfile pdb = new PDBfile("http://srs.ebi.ac.uk/srs5bin/cgi-bin/wgetz?-e+[pdb-id:" + seq.pdbcode.elementAt(0) + "]","URL");
	    seq.setPDBfile(pdb);
	    ((PDBChain)pdb.chains.elementAt(seq.maxchain)).isVisible = true;
	    ((PDBChain)pdb.chains.elementAt(seq.maxchain)).ds = seq;
	    ((PDBChain)pdb.chains.elementAt(seq.maxchain)).colourBySequence();
	  
	    rotFrame f = new rotFrame(pdb);
	    f.resize(500,500);
	    f.show();

	  } else {
	    System.out.println("No pdb code found");
	  }
	} catch (Exception e) {
	  System.out.println(e);
	}
      }
    }
  }
}

