package MCview;
import java.util.*;
import java.awt.*;

public class Atom {
  double x;
  double y;
  double z;
  int number;
  String name;
  String resName;
  int resNumber;
  int type;
  Color color;
  String chain;

  public Atom(StringTokenizer str) {
    this.number = (new Integer(str.nextToken())).intValue();
    this.name = str.nextToken();
    this.resName = str.nextToken();
    String tmpstr = new String();
    try {
      tmpstr = str.nextToken();
      this.resNumber = (new Integer(tmpstr).intValue());
      this.chain = "A";
      this.color = Color.green;
    } catch(NumberFormatException e) {
      this.chain = tmpstr;
      if (tmpstr.equals("A")) {
//        this.color = Color.green;
          this.color = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
      } else {
        this.color = Color.red;
      }
      this.resNumber = (new Integer(str.nextToken()).intValue());
    }
    this.x = (double)(new Double(str.nextToken()).floatValue());
    this.y = (double)(new Double(str.nextToken()).floatValue());
    this.z = (double)(new Double(str.nextToken()).floatValue());
  }

  public void setColor(Color col) {
    this.color = col;
  }
}
