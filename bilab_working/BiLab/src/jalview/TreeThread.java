package jalview;

import java.awt.*;
import java.util.*;

public class TreeThread extends Thread {
  String title;
  Object parent;
  Sequence[] s;
  String type;
  String pwtype;
  TreeFrame tf;

  public TreeThread(String title,Object parent,Vector vect, String type, String pwtype) {

    s = new Sequence[vect.size()];

    for (int i=0; i < vect.size(); i++) {
      s[i] = (Sequence)vect.elementAt(i);
    }
    
    this.title = title;

    this.parent = parent;
    this.type = type;
    this.pwtype = pwtype;
  }

  public TreeThread(String title,Object parent,Sequence[] s, String type, String pwtype) {
    this.title = title;
    this.s = s;
    this.parent = parent;
    this.type = type;
    this.pwtype = pwtype;
  }
    
  public void run() {
    tf = new TreeFrame(title,parent,s,type,pwtype);
    tf.resize(600,600);
    tf.show();
  }
}

