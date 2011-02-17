package MCview;

import java.awt.*;
import java.applet.*;
import java.io.*;

public class rotFrame extends Frame {
  String PDBstr;
  String type;
  public rotCanvas rc;

  public rotFrame(PDBfile pdb) throws IOException{
    Panel p = new Panel();
    rotCanvas rc = new rotCanvas(pdb,new Dimension(500,500));

    setLayout(new BorderLayout());
    add("Center",p);

    p.setLayout(new BorderLayout());
    p.add("Center",rc);
  }

  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {
      this.hide();
      this.dispose();
      //System.exit(0);
    }
    else super.handleEvent(evt);
    return true;
  }

  public static void main(String[] args) throws IOException {
    PDBfile pdb;
    try {
      pdb = new PDBfile(args[0],args[1]);
      rotFrame f = new rotFrame(pdb);
      f.resize(500,500);
      f.show();
    } catch(IOException e) {
      System.out.println(e);
      System.exit(0);
      
    }
  }
}

