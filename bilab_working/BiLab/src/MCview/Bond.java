package MCview;

import java.awt.*;

public class Bond {

  float start[];
  float end[];

  Color startCol;
  Color endCol;

  public myAtom at1;
  public myAtom at2;

  public Bond(float[] start, float[] end, myAtom at1, myAtom at2) {
     this.start = start;
     this.end = end;
     this.startCol = at1.color;
     this.endCol = at2.color;
     this.at1 = at1;
     this.at2 = at2;
  }
  public Bond(Bond bond) {
     this.start = new float[3];
     this.start[0] = bond.start[0];
     this.start[1] = bond.start[1];
     this.start[2] = bond.start[2];
     this.end = new float[3];
     this.end[0] = bond.end[0];
     this.end[1] = bond.end[1];
     this.end[2] = bond.end[2];
     this.startCol = bond.startCol;
     this.endCol = bond.endCol;
  }

  public void print() {
     System.out.println("Start " + start[0] + " "+ start[1] + " " + start[2]);
     System.out.println("End   " + end[0] + " "+ end[1] + " " + end[2]);
  }
  
  public float length() {
     float len = (end[0] - start[0])*(end[0] - start[0]) +
                  (end[1] - start[1])*(end[1] - start[1]) +
                  (end[2] - start[2])*(end[2] - start[2]);
     len = (float)(Math.sqrt(len));
     return len;
  }
  public void translate(float x, float y, float z) {
     start[0] = (start[0] + x);
     end[0] = (end[0] + x);
     start[1] =(start[1] + y);
     end[1] = (end[1] + y);
     start[2] = (start[2] + z);
     end[2] = (end[2] + z);
  }
}
