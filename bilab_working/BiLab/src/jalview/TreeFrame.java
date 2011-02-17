package jalview;



import java.awt.*;





public class TreeFrame extends Frame {

  Object parent;

  TreePanel p;



  public TreeFrame(String title,Object parent,Sequence[] s) {

    this(title,parent,s,"AV","PID");

  }



  public TreeFrame(String title,Object parent, Sequence[] s, String type, String pwtype) {

    super(title);

    this.parent = parent;

    p = new TreePanel(parent,s,type,pwtype);

    setLayout(new BorderLayout());

    add("Center",p);

    

  }

  public boolean handleEvent(Event evt) {

    if (evt.id == Event.WINDOW_DESTROY) {

      if (parent != null) {

	this.hide();

	this.dispose();

      } else if (parent == null) {

	System.exit(0);

      }

    }

    else super.handleEvent(evt);

    return false;

    }

  public static void main(String[] args) {

    try {

      

      MSFfile msf = new MSFfile(args[0],"File");

      

      Sequence[] s = new Sequence[msf.seqs.size()];

      

      for (int i=0; i < msf.seqs.size();i++) {

	s[i] = (Sequence)msf.seqs.elementAt(i);

      }

      TreeFrame tf = new TreeFrame("Tree for " + args[0],null,s,"AV","PID");

      tf.resize(500,500);

      tf.show();

      StringBuffer sw = new StringBuffer();

      tf.p.njt.tf.showDistances = true;

      PostscriptProperties pp = new PostscriptProperties();

      pp.orientation = PostscriptProperties.LANDSCAPE;

      //  tf.p.njt.tf.drawPostscript(sw,pp);

      System.out.println(sw.toString());

    } catch (java.io.IOException e) {

      System.out.println("Exception : " + e);

    }

  }

}



