package MCview;

import java.util.*;
import java.awt.*;

public class myAtom {
  float x;
  float y;
  float z;
  public int number;
  public String name;
  public String resName;
  public int resNumber;
  public int type;
  public Color color;
  String chain;
  public boolean isSelected = false;

  public myAtom(StringTokenizer str) {
    this.number = (new Integer(str.nextToken())).intValue();
    this.name = str.nextToken();
    this.resName = str.nextToken();
    String tmpstr = new String();
    try {
      tmpstr = str.nextToken();
      this.resNumber = (new Integer(tmpstr).intValue());
      this.chain = "A";
      this.color = Color.lightGray;
    } catch(NumberFormatException e) {
      this.chain = tmpstr;
      if (tmpstr.equals("A")) {
       this.color = Color.lightGray;
//           this.color = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());

      } else {
        this.color = Color.red;
      }
      this.resNumber = (new Integer(str.nextToken()).intValue());
    }
    this.x = (float)(new Float(str.nextToken()).floatValue());
    this.y = (float)(new Float(str.nextToken()).floatValue());
    this.z = (float)(new Float(str.nextToken()).floatValue());
  }

  public void setColor(Color col) {
    this.color = col;
  }
}
