package jalview;

import java.awt.*;

public class SequenceFeature {
  int start;
  int end;
  String type;
  String description;
  Color color;
  Sequence sequence;


  public SequenceFeature(Sequence sequence,String type, int start, int end, String description) {
    this.sequence = sequence;
    this.type = type;
    this.start = start;
    this.end = end;
    this.description = description;
    setColor();
  }

  public void setColor() {
    if (type.equals("CHAIN")) {
      color = Color.white;
    } else if (type.equals("DOMAIN")) {
      color = Color.white;
    } else if (type.equals("TRANSMEM")) {
      color = Color.red.darker();
    } else if (type.equals("SIGNAL")) {
      color = Color.cyan;
    } else if (type.equals("HELIX")) {
      color = Color.magenta;
    } else if (type.equals("TURN")) {
      color = Color.cyan;
    } else if (type.equals("SHEET")) {
      color = Color.yellow;
    } else if (type.equals("STRAND")) {
      color = Color.yellow;
    } else if (type.equals("CARBOHYD")) {
      color = Color.pink;
    } else if (type.equals("ACT_SITE")) {
      color = Color.red;
    } else if (type.equals("TRANSIT")) {
      color = Color.orange;
    } else if (type.equals("VARIANT")) {
      color = Color.orange.darker();
    } else if (type.equals("BINDING")) {
      color = Color.blue;
    } else if (type.equals("DISULFID")) {
      color = Color.yellow.darker();
    } else if (type.equals("NP_BIND")) {
      color = Color.red;
    } else if (type.indexOf("BIND") > 0) {
      color = Color.red;
    } else {
      color = Color.lightGray;
    }
  }
  public String print() {
    String tmp = new Format("%15s").form(type);
    tmp = tmp +  new Format("%6d").form(start);
    tmp = tmp +  new Format("%6d").form(end);
    tmp = tmp +  " " + description;
    return tmp;
  }
  public void draw(Graphics g, int fstart, int fend, int x1, int y1, int width, int height) {
    g.setColor(new Color((float)(Math.random()),(float)(Math.random()),(float)(Math.random())));

    //    int xstart = sequence.findIndex(start);
    //int xend = sequence.findIndex(end);
    int xstart = start;
    int xend = end;
    long tstart = System.currentTimeMillis();
    if (!(xend < fstart && xstart > fend)){

      if (xstart > fstart) {
	x1 = x1 + (xstart-fstart)*width;
	fstart = xstart;
      }

      if (xend < fend) {
	fend = xend;
      }

      for (int i = fstart; i <= fend; i++) {
	 String s = sequence.sequence.substring(i,i+1);
	 if (!(s.equals(".") || s.equals("-") || s.equals(" "))) {
	   g.fillRect(x1+(i-fstart)*width,y1,width,height);
	 } else {
	   g.drawString("-",x1+(i-fstart)*width,y1+height);
	 }
      }

    }
    long tend = System.currentTimeMillis();
    System.out.println("Time = " + (tend-tstart) + "ms");
    
  }

  public static int CHAIN = 0;
  public static int DOMAIN = 1;
  public static int TRANSMEM = 2;
  public static int SIGNAL = 3;
  public static int HELIX = 4;
  public static int TURN = 5;
  public static int SHEET = 6;
  public static int CARBOHYD = 7;
  public static int ACT_SITE = 8;
  public static int TRANSIT = 9;
  public static int VARIANT = 10;
  public static int BINDING = 11;

}

