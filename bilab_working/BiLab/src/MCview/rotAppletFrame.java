package MCview;

import java.awt.*;
import java.applet.*;
import java.io.*;
import java.util.*;

public class rotAppletFrame extends Applet {
String pdbid;
String infile;
String type;
Panel p;
Vector structures = new Vector();
TextField tf;

  public void init() {
    tf = new TextField(6);
    add(tf);
  }

  public boolean action(Event e, Object arg) {
    if (e.target == tf) {
      String tmp = tf.getText();
      pdbid = "http://srs.ebi.ac.uk/srs5bin/cgi-bin/wgetz?-e+[pdb-id:" + tmp + "]";
      drawStruct(pdbid,"URL");
      return true;
    } else {
      return false;
    }
  }

  public void drawStruct(String pdbid,String type) {
    try {
      PDBfile pdb = new PDBfile(pdbid,type);
      for (int i=0; i < pdb.chains.size(); i++) {
	((PDBChain)pdb.chains.elementAt(i)).isVisible = true;
      }
      rotFrame rf = new rotFrame(pdb);
      rf.resize(500,500);
      rf.show();
    } catch (IOException e) {
      System.out.println(e);
     }
  }
}
