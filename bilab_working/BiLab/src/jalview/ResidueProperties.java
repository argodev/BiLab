
 /* Copyright (C) 1998  Michele Clamp
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
 * Foundation, Inc, 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jalview;

import java.awt.Color;
import java.util.*;

public class ResidueProperties {
  
  //Stores residue codes/names and colours and other things
  public static Hashtable aaHash = new Hashtable();  // stores the number value of the aa
    public static Hashtable aa3Hash = new Hashtable(); 

   static {
    aaHash.put("A", new Integer(0));
    aaHash.put("R", new Integer(1));
    aaHash.put("N", new Integer(2));
    aaHash.put("D", new Integer(3));
    aaHash.put("C", new Integer(4));
    aaHash.put("Q", new Integer(5));
    aaHash.put("E", new Integer(6));
    aaHash.put("G", new Integer(7));
    aaHash.put("H", new Integer(8));
    aaHash.put("I", new Integer(9));
    aaHash.put("L", new Integer(10));
    aaHash.put("K", new Integer(11));
    aaHash.put("M", new Integer(12));
    aaHash.put("F", new Integer(13));
    aaHash.put("P", new Integer(14));
    aaHash.put("S", new Integer(15));
    aaHash.put("T", new Integer(16));
    aaHash.put("W", new Integer(17));
    aaHash.put("Y", new Integer(18));
    aaHash.put("V", new Integer(19));
    aaHash.put("B", new Integer(20));
    aaHash.put("Z", new Integer(21));
    aaHash.put("X", new Integer(22));
    aaHash.put("-", new Integer(23));
    aaHash.put("*", new Integer(23));
    aaHash.put(".", new Integer(23));
    aaHash.put(" ", new Integer(23));
   }
   static {
    aa3Hash.put("ALA", new Integer(0));
    aa3Hash.put("ARG", new Integer(1));
    aa3Hash.put("ASN", new Integer(2));
    aa3Hash.put("ASP", new Integer(3)); //D
    aa3Hash.put("CYS", new Integer(4));
    aa3Hash.put("GLN", new Integer(5)); //Q
    aa3Hash.put("GLU", new Integer(6));   // E
    aa3Hash.put("GLY", new Integer(7));
    aa3Hash.put("HIS", new Integer(8));
    aa3Hash.put("ILE", new Integer(9));
    aa3Hash.put("LEU", new Integer(10));
    aa3Hash.put("LYS", new Integer(11));
    aa3Hash.put("MET", new Integer(12));
    aa3Hash.put("PHE", new Integer(13));
    aa3Hash.put("PRO", new Integer(14));
    aa3Hash.put("SER", new Integer(15));
    aa3Hash.put("THR", new Integer(16));
    aa3Hash.put("TRP", new Integer(17));
    aa3Hash.put("TYR", new Integer(18));
    aa3Hash.put("VAL", new Integer(19));
    aa3Hash.put("B", new Integer(20));
    aa3Hash.put("Z", new Integer(21));
    aa3Hash.put("X", new Integer(22));
    aa3Hash.put("-", new Integer(23));
    aa3Hash.put("*", new Integer(23));
    aa3Hash.put(".", new Integer(23));
    aa3Hash.put(" ", new Integer(23));
   }

  public static String aa[] = {
    "A","R","N","D","C","Q","E","G","H","I","L","K","M","F","P","S","T","W","Y","V","B","Z","X","_","*","."," "
  };
  public static Color midBlue = new Color(100,100,255);
  public static Vector scaleColours = new Vector();
  static {
    scaleColours.addElement(new Color(114,0,147));
    scaleColours.addElement(new Color(156,0,98));
    scaleColours.addElement(new Color(190,0,0));
    scaleColours.addElement(Color.red);
    scaleColours.addElement(new Color(255,125,0));
    scaleColours.addElement(Color.orange);
    scaleColours.addElement(new Color(255,194,85));
    scaleColours.addElement(Color.yellow);
    scaleColours.addElement(new Color(255,255,181));
    scaleColours.addElement(Color.white);
  }
    
  public static Color[] taylor = {
      new Color(204,255,0),          // A  Greenish-yellowy-yellow
      new Color(0,0,255),            // R  Blueish-bluey-blue
      new Color(204,0,255),          // N  Blueish-reddy-blue
      new Color(255,0,0),            // D  Reddish-reddy-red
      new Color(255,255,0),          // C  Yellowish-yellowy-yellow
      new Color(255,0,204),          // Q  Reddish-bluey-red
      new Color(255,0,102),          // E  Blueish-reddy-red
      new Color(255,153,0),          // G  Yellowy-reddy-yellow
      new Color(0,102,255),          // H  Greenish-bluey-blue
      new Color(102,255,0),          // I  Greenish-yellowy-green
      new Color(51,255,0),           // L  Yellowish-greeny-green
      new Color(102,0,255),          // K  Reddish-bluey-blue
      new Color(0,255,0),            // M  Greenish-greeny-green
      new Color(0,255,102),          // F  Blueish-greeny-green
      new Color(255,204,0),          // P  Reddish-yellowy-yellow
      new Color(255,51,0),           // S  Yellowish-reddy-red
      new Color(255,102,0),          // T  Reddish-yellowy-red
      new Color(0,204,255),          // W  Blueish-greeny-green
      new Color(0,255,204),          // Y  Greenish-bluey-green
      new Color(153,255,0),          // V  Yellowish-greeny-yellow
      Color.white,         // B
      Color.white,         // Z
      Color.white,         // X
      Color.white,         // -
      Color.white,         // *
      Color.white          // .
      };
   
    public static Color[] color = {
      Color.pink,          // A
      midBlue,             // R
      Color.green,         // N
      Color.red,           // D
      Color.yellow,        // C 
      Color.green,         // Q
      Color.red,           // E
      Color.magenta,       // G
      Color.red,           // H
      Color.pink,          // I
      Color.pink,          // L
      midBlue,             // K
      Color.pink,          // M
      Color.orange,        // F
      Color.magenta,       // P
      Color.green,         // S
      Color.green,         // T
      Color.orange,        // W
      Color.orange,        // Y
      Color.pink,          // V
      Color.white,         // B
      Color.white,         // Z
      Color.white,         // X
      Color.white,         // -
      Color.white,         // *
      Color.white,          // .
      Color.white          // ' '
      };
    
  // Dunno where I got these numbers from
    public static double[] hyd2 = {
      0.62,  //A
      0.29,  //R
     -0.90,  //N
     -0.74,  //D
      1.19,  //C
      0.48,  //Q
      -0.40, //E
      1.38,  //G
      -1.50, //H
      1.06,  //I
      0.64,  //L
      -0.78, //K
      0.12,  //M
      -0.85, //F
      -2.53, //P
      -0.18, //S
      -0.05, //T
      1.08,  //W
      0.81,  //Y
      0.0,   //V
      0.26,  //B
      0.0,   //Z
      0.0   //X
    };
 public static double[] helix = { 
   1.42,
   0.98,
   0.67,
   1.01,
   0.70,
   1.11,
   1.51,
   0.57,
   1.00,
   1.08,
   1.21,
   1.16,
   1.45,
   1.13,
   0.57,
   0.77,
   0.83,
   1.08,
   0.69,
   1.06,
   0.84,
   1.31,
   1.00
 };
  public static double helixmin = 0.57;
  public static double helixmax = 1.51;

  public static double[] strand = {
    0.83,0.93,0.89,0.54,1.19,1.10,0.37,
    0.75,0.87,1.60,1.30,0.74,1.05,1.38,0.55,
    0.75,1.19,1.37,1.47,1.70,0.72,0.74,1.0};

  public static double strandmin = 0.37;
  public static double strandmax = 1.7;

  public static double[] turn = {
    0.66,0.95,1.56,1.46,1.19,0.98,0.74,
    1.56,0.95,0.47,0.59,1.01,0.60,0.60,1.52,
    1.43,0.96,0.96,1.14,0.50,1.51,0.86,1.00};

  public static double turnmin = 0.47;
  public static double turnmax = 1.56;

  public static double[] buried = {
    1.7,
    0.1,
    0.4,
    0.4,
    4.6,
    0.3,
    0.3,
    1.8,
    0.8,
    3.1,
    2.4,
    0.05,
    1.9,
    2.2,
    0.6,
    0.8,0.7,1.6,0.5,2.9,0.4,0.3,
    1.358
  };

  public static double buriedmin = 0.05;
  public static double buriedmax = 4.6;

  // This is hydropathy index 
  // Kyte, J., and Doolittle, R.F., J. Mol. Biol.
  // 1157, 105-132, 1982

  public static double hyd[] = {
    1.8,
    -4.5,
    -3.5,
    -3.5,
    2.5,
    -3.5,
    -3.5,
    -0.4,
    -3.2,
    4.5,
    3.8,
    -3.9,
    1.9,
    2.8,
    -1.6,
    -0.8,
    -0.7,
    -0.9,
    -1.3,
    4.2,
    -3.5,
    -3.5,
    -0.49
  };

  public static final double hydmax = 4.5;
  public static final double hydmin = -3.9;

  //public static final double hydmax = 1.38;
  //public static final double hydmin = -2.53;

  static final int[][] BLOSUM62 = {
    //A   R   N   D   C   Q   E   G   H   I   L   K   M   F   P   S   T   W   Y   V   B   Z   X  -
    { 4 ,-1, -2 ,-2 , 0 ,-1 ,-1 , 0 ,-2 ,-1 ,-1 ,-1 ,-1 ,-2 ,-1 , 1 , 0 ,-3 ,-2 , 0 ,-2, -1 , 0 ,-4}, 
    {-1 , 5 , 0 ,-2 ,-3 , 1 , 0 ,-2 , 0 ,-3 ,-2 , 2 ,-1 ,-3 ,-2 ,-1 ,-1 ,-3 ,-2 ,-3 ,-1 , 0 ,-1 ,-4 },
    {-2 , 0 , 6 , 1 ,-3 , 0 , 0 , 0 , 1 ,-3 ,-3 , 0 ,-2 ,-3 ,-2 , 1 , 0 ,-4 ,-2 ,-3 , 3 , 0 ,-1 ,-4 },
    {-2 ,-2 , 1 , 6 ,-3 , 0 , 2 ,-1 ,-1 ,-3 ,-4 ,-1 ,-3 ,-3 ,-1 , 0 ,-1 ,-4 ,-3 ,-3 , 4 , 1 ,-1 ,-4 },
    { 0 , 3 ,-3 ,-3 , 9 ,-3 ,-4 ,-3 ,-3 ,-1 ,-1 ,-3 ,-1 ,-2 ,-3 ,-1 ,-1 ,-2 ,-2 ,-1 ,-3 ,-3 ,-2 ,-4 },
    {-1 , 1 , 0 , 0 ,-3 , 5 , 2 ,-2 , 0 ,-3 ,-2 , 1 , 0 ,-3 ,-1 , 0 ,-1 ,-2 ,-1 ,-2 , 0 , 3 ,-1 ,-4 },
    {-1 , 0 , 0 , 2 ,-4 , 2 , 5 ,-2 , 0 ,-3 ,-3 , 1 ,-2 ,-3 ,-1 , 0 ,-1 ,-3 ,-2 ,-2 , 1 , 4 ,-1 ,-4 },
    { 0 ,-2 , 0 ,-1 ,-3 ,-2 ,-2 , 6 ,-2 ,-4 ,-4 ,-2 ,-3 ,-3 ,-2 , 0 ,-2 ,-2 ,-3 ,-3 ,-1 ,-2 ,-1 ,-4 },
    {-2 , 0 , 1 ,-1 ,-3 , 0 , 0 ,-2 , 8 ,-3 ,-3 ,-1 ,-2 ,-1 ,-2 ,-1 ,-2 ,-2 , 2 ,-3 , 0 , 0 ,-1 ,-4 },
    {-1 ,-3 ,-3 ,-3 ,-1 ,-3 ,-3 ,-4 ,-3 , 4 , 2 ,-3 , 1 , 0 ,-3 ,-2 ,-1 ,-3 ,-1 , 3 ,-3 ,-3 ,-1 ,-4 },
    {-1 ,-2 ,-3 ,-4 ,-1 ,-2 ,-3 ,-4 ,-3 , 2 , 4 ,-2 , 2 , 0 ,-3 ,-2 ,-1 ,-2 ,-1 , 1 ,-4 ,-3 ,-1 ,-4 },
    {-1 , 2 , 0 ,-1 ,-3 , 1 , 1 ,-2 ,-1 ,-3 ,-2 , 5 ,-1 ,-3 ,-1 , 0 ,-1 ,-3 ,-2 ,-2 , 0 , 1 ,-1 ,-4 },
    {-1 ,-1 ,-2 ,-3 ,-1 , 0 ,-2 ,-3 ,-2 , 1 , 2 ,-1 , 5 , 0 ,-2 ,-1 ,-1 ,-1 ,-1 , 1 ,-3 ,-1 ,-1 ,-4 },
    {-2 ,-3 ,-3 ,-3 ,-2 ,-3 ,-3 ,-3 ,-1 , 0 , 0 ,-3 , 0 , 6 ,-4 ,-2 ,-2 , 1 , 3 ,-1 ,-3 ,-3 ,-1 ,-4 },
    {-1 ,-2 ,-2 ,-1 ,-3 ,-1 ,-1 ,-2 ,-2 ,-3 ,-3 ,-1 ,-2 ,-4 , 7 ,-1 ,-1 ,-4 ,-3 ,-2 ,-2 ,-1 ,-2 ,-4 },
    { 1 ,-1 , 1 , 0 ,-1 , 0 , 0 , 0 ,-1 ,-2 ,-2 , 0 ,-1 ,-2 ,-1 , 4 , 1 ,-3 ,-2 ,-2 , 0 , 0 , 0 ,-4 },
    { 0 ,-1 , 0 ,-1 ,-1 ,-1 ,-1 ,-2 ,-2 ,-1 ,-1 ,-1 ,-1 ,-2 ,-1 , 1 , 5 ,-2 ,-2 , 0 ,-1 ,-1 , 0 ,-4 },
    {-3 ,-3 ,-4 ,-4 ,-2 ,-2 ,-3 ,-2 ,-2 ,-3 ,-2 ,-3 ,-1 , 1 ,-4 ,-3 ,-2 ,11 , 2 ,-3 ,-4 ,-3 ,-2 ,-4 },
    {-2 ,-2 ,-2 ,-3 ,-2 ,-1 ,-2 ,-3 , 2 ,-1 ,-1 ,-2 ,-1 , 3 ,-3 ,-2 ,-2 , 2 , 7 ,-1 ,-3 ,-2 ,-1 ,-4 },
    { 0 ,-3 ,-3 ,-3 ,-1 ,-2 ,-2 ,-3 ,-3 , 3 , 1 ,-2 , 1 ,-1 ,-2 ,-2 , 0 ,-3 ,-1 , 4 ,-3 ,-2 ,-1 ,-4 },
    {-2 ,-1 , 3 , 4 ,-3 , 0 , 1 ,-1 , 0 ,-3 ,-4 , 0 ,-3 ,-3 ,-2 , 0 ,-1 ,-4 ,-3 ,-3 , 4 , 1 ,-1 ,-4 },
    {-1 , 0 , 0 , 1 ,-3 , 3 , 4 ,-2 , 0 ,-3 ,-3 , 1 ,-1 ,-3 ,-1 , 0 ,-1 ,-3 ,-2 ,-2 , 1 , 4 ,-1 ,-4 },
    { 0 ,-1 ,-1 ,-1 ,-2 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-1 ,-2 , 0 , 0 ,-2 ,-1 ,-1 ,-1 ,-1 ,-1 ,-4 },
    {-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 ,-4 , 1 },
  };
  static final int[][] PAM250 = {
    {  2,-2 ,0 ,0,-2 ,0 ,0 ,1,-1,-1,-2,-1,-1,-3 ,1 ,1 ,1,-6,-3 ,0 ,0 ,0 ,0,-8},
    { -2 ,6 ,0,-1,-4 ,1,-1,-3 ,2,-2,-3 ,3 ,0,-4 ,0 ,0,-1 ,2,-4,-2,-1 ,0,-1,-8},
    {  0 ,0 ,2 ,2,-4 ,1 ,1 ,0 ,2,-2,-3 ,1,-2,-3 ,0 ,1 ,0,-4,-2,-2 ,2 ,1 ,0,-8},
    {  0,-1 ,2 ,4,-5 ,2 ,3 ,1 ,1,-2,-4 ,0,-3,-6,-1 ,0 ,0,-7,-4,-2 ,3 ,3,-1,-8},
    { -2,-4,-4,-5,12,-5,-5,-3,-3,-2,-6,-5,-5,-4,-3 ,0,-2,-8 ,0,-2,-4,-5,-3,-8},
    {  0 ,1 ,1 ,2,-5 ,4 ,2,-1 ,3,-2,-2 ,1,-1,-5 ,0,-1,-1,-5,-4,-2 ,1 ,3,-1,-8},
    {  0,-1 ,1 ,3,-5 ,2 ,4 ,0 ,1,-2,-3 ,0,-2,-5,-1 ,0 ,0,-7,-4,-2 ,3 ,3,-1,-8},
    {  1,-3 ,0 ,1,-3,-1 ,0 ,5,-2,-3,-4,-2,-3,-5 ,0 ,1 ,0,-7,-5,-1 ,0 ,0,-1,-8},
    { -1 ,2 ,2 ,1,-3 ,3 ,1,-2 ,6,-2,-2 ,0,-2,-2 ,0,-1,-1,-3 ,0,-2 ,1 ,2,-1,-8},
    { -1,-2,-2,-2,-2,-2,-2,-3,-2 ,5 ,2,-2 ,2 ,1,-2,-1 ,0,-5,-1 ,4,-2,-2,-1,-8},
    { -2,-3,-3,-4,-6,-2,-3,-4,-2 ,2 ,6,-3 ,4 ,2,-3,-3,-2,-2,-1 ,2,-3,-3,-1,-8},
    { -1 ,3 ,1 ,0,-5 ,1 ,0,-2 ,0,-2,-3 ,5 ,0,-5,-1 ,0 ,0,-3,-4,-2 ,1 ,0,-1,-8},
    { -1 ,0,-2,-3,-5,-1,-2,-3,-2 ,2 ,4 ,0 ,6 ,0,-2,-2,-1,-4,-2 ,2,-2,-2,-1,-8},
    { -3,-4,-3,-6,-4,-5,-5,-5,-2 ,1 ,2,-5 ,0 ,9,-5,-3,-3 ,0 ,7,-1,-4,-5,-2,-8},
    {  1 ,0 ,0,-1,-3 ,0,-1 ,0 ,0,-2,-3,-1,-2,-5 ,6 ,1 ,0,-6,-5,-1,-1 ,0,-1,-8},
    {  1 ,0 ,1 ,0 ,0,-1 ,0 ,1,-1,-1,-3 ,0,-2,-3 ,1 ,2 ,1,-2,-3,-1 ,0 ,0 ,0,-8},
    {  1,-1 ,0 ,0,-2,-1 ,0 ,0,-1 ,0,-2 ,0,-1,-3 ,0 ,1 ,3,-5,-3 ,0 ,0,-1 ,0,-8},
    { -6 ,2,-4,-7,-8,-5,-7,-7,-3,-5,-2,-3,-4 ,0,-6,-2,-5,17 ,0,-6,-5,-6,-4,-8},
    { -3,-4,-2,-4 ,0,-4,-4,-5 ,0,-1,-1,-4,-2 ,7,-5,-3,-3 ,0,10,-2,-3,-4,-2,-8},
    {  0,-2,-2,-2,-2,-2,-2,-1,-2 ,4 ,2,-2 ,2,-1,-1,-1 ,0,-6,-2 ,4,-2,-2,-1,-8},
    {  0,-1 ,2 ,3,-4 ,1 ,3 ,0 ,1,-2,-3 ,1,-2,-4,-1 ,0 ,0,-5,-3,-2 ,3 ,2,-1,-8},
    {  0 ,0 ,1 ,3,-5 ,3 ,3 ,0 ,2,-2,-3 ,0,-2,-5 ,0 ,0,-1,-6,-4,-2 ,2 ,3,-1,-8},
    {  0,-1 ,0,-1,-3,-1,-1,-1,-1,-1,-1,-1,-1,-2,-1 ,0 ,0,-4,-2,-1,-1,-1,-1,-8},
    { -8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8 ,1},
  };

  public static Hashtable ssHash = new Hashtable();  // stores the number value of the aa
  
   static {
    ssHash.put("H", Color.magenta);
    ssHash.put("E", Color.yellow);
    ssHash.put("-", Color.white);
    ssHash.put(".", Color.white);
    ssHash.put("S", Color.cyan);
    ssHash.put("T", Color.blue);
    ssHash.put("G", Color.pink);
    ssHash.put("I", Color.pink);
    ssHash.put("B", Color.yellow);
   }  
static final int[][] DNA = {
  //  C  T  A  G  -
    { 5,-4,-4,-4, 1},// C
    {-4, 5,-4,-4, 1},// T
    {-4,-4, 5,-4, 1},// A
    {-4,-4,-4, 5, 1},// G
    { 1, 1, 1, 1, 1},// -
  };
  public static int getPAM250(String A1, String A2) {
    Integer pog1  = (Integer)aaHash.get(A1);
    Integer pog2  = (Integer)aaHash.get(A2);
    int pog = ResidueProperties.PAM250[pog1.intValue()][pog2.intValue()];
    return pog ;
  }
    public static int getBLOSUM62(String A1, String A2) {
      int pog = 0;
      try {
	Integer pog1  = (Integer)aaHash.get(A1);
	Integer pog2  = (Integer)aaHash.get(A2);
	pog = ResidueProperties.BLOSUM62[pog1.intValue()][pog2.intValue()];
      } catch (Exception e) {
	//System.out.println("Unknown residue in " + A1 + " " + A2);
      }
    return pog ;
  }
  
  public static Color[] pidColours = {    
    midBlue,
    new Color(153,153,255),
    //    Color.lightGray,
    new Color(204,204,255),
  };
  public static float[] pidThresholds = { 
    80,
    60,
    40,
  } ; 
  private ResidueProperties() {
  }

  public static Hashtable codonHash = new Hashtable();

  public static Vector Lys = new Vector();
  public static Vector Asn = new Vector();
  public static Vector Gln = new Vector();
  public static Vector His = new Vector();
  public static Vector Glu = new Vector();
  public static Vector Asp = new Vector();
  public static Vector Tyr = new Vector();
  public static Vector Thr = new Vector();
  public static Vector Pro = new Vector();
  public static Vector Ala = new Vector();
  public static Vector Ser = new Vector();
  public static Vector Arg = new Vector();
  public static Vector Gly = new Vector();
  public static Vector Trp = new Vector();
  public static Vector Cys = new Vector();
  public static Vector Ile = new Vector();
  public static Vector Met = new Vector();
  public static Vector Leu = new Vector();
  public static Vector Val = new Vector();
  public static Vector Phe = new Vector();
  public static Vector STOP = new Vector();

  static {
    codonHash.put("K",Lys);
    codonHash.put("N",Asn);
    codonHash.put("Q",Gln);
    codonHash.put("H",His);
    codonHash.put("E",Glu);
    codonHash.put("D",Asp);
    codonHash.put("Y",Tyr);
    codonHash.put("T",Thr);
    codonHash.put("P",Pro);
    codonHash.put("A",Ala);
    codonHash.put("S",Ser);
    codonHash.put("R",Arg);
    codonHash.put("G",Gly);
    codonHash.put("W",Trp);
    codonHash.put("C",Cys);
    codonHash.put("I",Ile);
    codonHash.put("M",Met);
    codonHash.put("L",Leu);
    codonHash.put("V",Val);
    codonHash.put("F",Phe);
    codonHash.put("STOP",STOP);
  }

  public static Vector getCodons(String res) {
    if (codonHash.containsKey(res)) return (Vector)codonHash.get(res);
    return null;
  }

  public static String codonTranslate(String codon) {
    Enumeration e = codonHash.keys();
    while (e.hasMoreElements()) {
      String key = (String)e.nextElement();
      Vector tmp = (Vector)codonHash.get(key);
      if (tmp.contains(codon)) {
	return key;
      }
    }
    return null;
  }
  static {
  Lys.addElement("AAA");
  Lys.addElement("AAG");
  Asn.addElement("AAC");
  Asn.addElement("AAT");

  Gln.addElement("CAA");
  Gln.addElement("CAG");
  His.addElement("CAC");
  His.addElement("CAT");

  Glu.addElement("GAA");
  Glu.addElement("GAG");
  Asp.addElement("GAC");
  Asp.addElement("GAT");

  Tyr.addElement("TAC");
  Tyr.addElement("TAT");

  Thr.addElement("ACA");
  Thr.addElement("ACG");
  Thr.addElement("ACC");
  Thr.addElement("ACT");

  Pro.addElement("CCA");
  Pro.addElement("CCG");
  Pro.addElement("CCC");
  Pro.addElement("CCG");

  Ala.addElement("GCA");
  Ala.addElement("GCG");
  Ala.addElement("GCC");
  Ala.addElement("GCT");

  Ser.addElement("TCA");
  Ser.addElement("TCG");
  Ser.addElement("TCC");
  Ser.addElement("TCT");
  Ser.addElement("AGC");
  Ser.addElement("AGT");

  Arg.addElement("AGA");
  Arg.addElement("AGG");
  Arg.addElement("CGA");
  Arg.addElement("CGG");
  Arg.addElement("CGC");
  Arg.addElement("CGT");

  Gly.addElement("GGA");
  Gly.addElement("GGG");
  Gly.addElement("GGC");
  Gly.addElement("GGT");

  STOP.addElement("TGA");
  STOP.addElement("TAA");
  STOP.addElement("TAG");

  Trp.addElement("TGG");

  Cys.addElement("TGC");
  Cys.addElement("TGT");

  Ile.addElement("ATA");
  Ile.addElement("ATC");
  Ile.addElement("ATT");

  Met.addElement("ATG");
  
  Leu.addElement("CTA");
  Leu.addElement("CTG");
  Leu.addElement("CTC");
  Leu.addElement("CTT");
  Leu.addElement("TTA");
  Leu.addElement("TTG");

  Val.addElement("GTA");
  Val.addElement("GTG");
  Val.addElement("GTC");
  Val.addElement("GTT");

  Phe.addElement("TTC");
  Phe.addElement("TTT");
  }

  public static Color[][] groupColors = {
    {Color.red,Color.red.brighter(),Color.red.brighter().brighter()},
    {Color.orange,Color.orange.brighter(),Color.orange.brighter().brighter()},
    {Color.green,Color.green.brighter(),Color.green.brighter().brighter()},
    {Color.blue,Color.blue.brighter(),Color.blue.brighter().brighter()},
    {Color.magenta,Color.magenta.brighter(),Color.magenta.brighter().brighter()},
    {Color.cyan,Color.cyan.brighter(),Color.cyan.brighter().brighter()},
    {Color.pink,Color.pink.brighter(),Color.pink.brighter().brighter()},
  
  };


  
  //Stores residue codes/names and colours and other things
  public static Hashtable propHash = new Hashtable();
  public static Hashtable hydrophobic = new Hashtable();
  public static Hashtable polar = new Hashtable();
  public static Hashtable small = new Hashtable();
  public static Hashtable positive = new Hashtable();
  public static Hashtable negative = new Hashtable();
  public static Hashtable charged = new Hashtable();
  public static Hashtable aromatic = new Hashtable();
  public static Hashtable aliphatic = new Hashtable();
  public static Hashtable tiny = new Hashtable();
  public static Hashtable proline = new Hashtable();

  static {
    hydrophobic.put("I",new Integer(1));
    hydrophobic.put("L",new Integer(1));
    hydrophobic.put("V",new Integer(1));
    hydrophobic.put("C",new Integer(1));
    hydrophobic.put("A",new Integer(1));
    hydrophobic.put("G",new Integer(1));
    hydrophobic.put("M",new Integer(1));
    hydrophobic.put("F",new Integer(1));
    hydrophobic.put("Y",new Integer(1));
    hydrophobic.put("W",new Integer(1));
    hydrophobic.put("H",new Integer(1));
    hydrophobic.put("K",new Integer(1));
    hydrophobic.put("X",new Integer(1));
    hydrophobic.put("-",new Integer(1));
    hydrophobic.put("*",new Integer(1));
    hydrophobic.put("R",new Integer(0));
    hydrophobic.put("E",new Integer(0));
    hydrophobic.put("Q",new Integer(0));
    hydrophobic.put("D",new Integer(0));
    hydrophobic.put("N",new Integer(0));
    hydrophobic.put("S",new Integer(0));
    hydrophobic.put("T",new Integer(0));
    hydrophobic.put("P",new Integer(0));
  }
  static {
    polar.put("Y",new Integer(1));
    polar.put("W",new Integer(1));
    polar.put("H",new Integer(1));
    polar.put("K",new Integer(1));
    polar.put("R",new Integer(1));
    polar.put("E",new Integer(1));
    polar.put("Q",new Integer(1));
    polar.put("D",new Integer(1));
    polar.put("N",new Integer(1));
    polar.put("S",new Integer(1));
    polar.put("T",new Integer(1));
    polar.put("X",new Integer(1));
    polar.put("-",new Integer(1));
    polar.put("*",new Integer(1));
    polar.put("I",new Integer(0));
    polar.put("L",new Integer(0));
    polar.put("V",new Integer(0));
    polar.put("C",new Integer(0));
    polar.put("A",new Integer(0));
    polar.put("G",new Integer(0));
    polar.put("M",new Integer(0));
    polar.put("F",new Integer(0));
    polar.put("P",new Integer(0));

  }
  static {
    small.put("I",new Integer(0));
    small.put("L",new Integer(0));
    small.put("V",new Integer(1));
    small.put("C",new Integer(1));
    small.put("A",new Integer(1));
    small.put("G",new Integer(1));
    small.put("M",new Integer(0));
    small.put("F",new Integer(0));
    small.put("Y",new Integer(0));
    small.put("W",new Integer(0));
    small.put("H",new Integer(0));
    small.put("K",new Integer(0));
    small.put("R",new Integer(0));
    small.put("E",new Integer(0));
    small.put("Q",new Integer(0));
    small.put("D",new Integer(1));
    small.put("N",new Integer(1));
    small.put("S",new Integer(1));
    small.put("T",new Integer(1));
    small.put("P",new Integer(1));
    small.put("-",new Integer(1));
    small.put("*",new Integer(1));
  }
  static {
    positive.put("I",new Integer(0));
    positive.put("L",new Integer(0));
    positive.put("V",new Integer(0));
    positive.put("C",new Integer(0));
    positive.put("A",new Integer(0));
    positive.put("G",new Integer(0));
    positive.put("M",new Integer(0));
    positive.put("F",new Integer(0));
    positive.put("Y",new Integer(0));
    positive.put("W",new Integer(0));
    positive.put("H",new Integer(1));
    positive.put("K",new Integer(1));
    positive.put("R",new Integer(1));
    positive.put("E",new Integer(0));
    positive.put("Q",new Integer(0));
    positive.put("D",new Integer(0));
    positive.put("N",new Integer(0));
    positive.put("S",new Integer(0));
    positive.put("T",new Integer(0));
    positive.put("P",new Integer(0));
    positive.put("-",new Integer(1));
    positive.put("*",new Integer(1));
  }
  static {
    negative.put("I",new Integer(0));
    negative.put("L",new Integer(0));
    negative.put("V",new Integer(0));
    negative.put("C",new Integer(0));
    negative.put("A",new Integer(0));
    negative.put("G",new Integer(0));
    negative.put("M",new Integer(0));
    negative.put("F",new Integer(0));
    negative.put("Y",new Integer(0));
    negative.put("W",new Integer(0));
    negative.put("H",new Integer(0));
    negative.put("K",new Integer(0));
    negative.put("R",new Integer(0));
    negative.put("E",new Integer(1));
    negative.put("Q",new Integer(0));
    negative.put("D",new Integer(1));
    negative.put("N",new Integer(0));
    negative.put("S",new Integer(0));
    negative.put("T",new Integer(0));
    negative.put("P",new Integer(0));
    negative.put("-",new Integer(1));
    negative.put("*",new Integer(1));
  }
  static {
    charged.put("I",new Integer(0));
    charged.put("L",new Integer(0));
    charged.put("V",new Integer(0));
    charged.put("C",new Integer(0));
    charged.put("A",new Integer(0));
    charged.put("G",new Integer(0));
    charged.put("M",new Integer(0));
    charged.put("F",new Integer(0));
    charged.put("Y",new Integer(0));
    charged.put("W",new Integer(0));
    charged.put("H",new Integer(1));
    charged.put("K",new Integer(1));
    charged.put("R",new Integer(1));
    charged.put("E",new Integer(1));
    charged.put("Q",new Integer(0));
    charged.put("D",new Integer(1));
    charged.put("N",new Integer(1));
    charged.put("S",new Integer(0));
    charged.put("T",new Integer(0));
    charged.put("P",new Integer(0));
    charged.put("-",new Integer(1));
    charged.put("*",new Integer(1));
  }
  static {
    aromatic.put("I",new Integer(0));
    aromatic.put("L",new Integer(0));
    aromatic.put("V",new Integer(0));
    aromatic.put("C",new Integer(0));
    aromatic.put("A",new Integer(0));
    aromatic.put("G",new Integer(0));
    aromatic.put("M",new Integer(0));
    aromatic.put("F",new Integer(1));
    aromatic.put("Y",new Integer(1));
    aromatic.put("W",new Integer(1));
    aromatic.put("H",new Integer(1));
    aromatic.put("K",new Integer(0));
    aromatic.put("R",new Integer(0));
    aromatic.put("E",new Integer(0));
    aromatic.put("Q",new Integer(0));
    aromatic.put("D",new Integer(0));
    aromatic.put("N",new Integer(0));
    aromatic.put("S",new Integer(0));
    aromatic.put("T",new Integer(0));
    aromatic.put("P",new Integer(0));
    aromatic.put("-",new Integer(1));
    aromatic.put("*",new Integer(1));
  }
  static {
    aliphatic.put("I",new Integer(1));
    aliphatic.put("L",new Integer(1));
    aliphatic.put("V",new Integer(1));
    aliphatic.put("C",new Integer(0));
    aliphatic.put("A",new Integer(0));
    aliphatic.put("G",new Integer(0));
    aliphatic.put("M",new Integer(0));
    aliphatic.put("F",new Integer(0));
    aliphatic.put("Y",new Integer(0));
    aliphatic.put("W",new Integer(0));
    aliphatic.put("H",new Integer(0));
    aliphatic.put("K",new Integer(0));
    aliphatic.put("R",new Integer(0));
    aliphatic.put("E",new Integer(0));
    aliphatic.put("Q",new Integer(0));
    aliphatic.put("D",new Integer(0));
    aliphatic.put("N",new Integer(0));
    aliphatic.put("S",new Integer(0));
    aliphatic.put("T",new Integer(0));
    aliphatic.put("P",new Integer(0));
    aliphatic.put("-",new Integer(1));
    aliphatic.put("*",new Integer(1));
  }

  static {
    tiny.put("I",new Integer(0));
    tiny.put("L",new Integer(0));
    tiny.put("V",new Integer(0));
    tiny.put("C",new Integer(0));
    tiny.put("A",new Integer(1));
    tiny.put("G",new Integer(1));
    tiny.put("M",new Integer(0));
    tiny.put("F",new Integer(0));
    tiny.put("Y",new Integer(0));
    tiny.put("W",new Integer(0));
    tiny.put("H",new Integer(0));
    tiny.put("K",new Integer(0));
    tiny.put("R",new Integer(0));
    tiny.put("E",new Integer(0));
    tiny.put("Q",new Integer(0));
    tiny.put("D",new Integer(0));
    tiny.put("N",new Integer(0));
    tiny.put("S",new Integer(1));
    tiny.put("T",new Integer(0));
    tiny.put("P",new Integer(0));
    tiny.put("-",new Integer(1));
    tiny.put("*",new Integer(1));
  }

  static {
    proline.put("I",new Integer(0));
    proline.put("L",new Integer(0));
    proline.put("V",new Integer(0));
    proline.put("C",new Integer(0));
    proline.put("A",new Integer(0));
    proline.put("G",new Integer(0));
    proline.put("M",new Integer(0));
    proline.put("F",new Integer(0));
    proline.put("Y",new Integer(0));
    proline.put("W",new Integer(0));
    proline.put("H",new Integer(0));
    proline.put("K",new Integer(0));
    proline.put("R",new Integer(0));
    proline.put("E",new Integer(0));
    proline.put("Q",new Integer(0));
    proline.put("D",new Integer(0));
    proline.put("N",new Integer(0));
    proline.put("S",new Integer(0));
    proline.put("T",new Integer(0));
    proline.put("P",new Integer(1));
    proline.put("-",new Integer(1));
    proline.put("*",new Integer(1));
  }


  static { 
    propHash.put("hydrophobic",hydrophobic);
    propHash.put("small",small);
    propHash.put("positive",positive);
    propHash.put("negative",negative);
    propHash.put("charged",charged);
    propHash.put("aromatic",aromatic);
    propHash.put("aliphatic",aliphatic);
    propHash.put("tiny",tiny);
    propHash.put("proline",proline);
    propHash.put("polar",polar);
  }
  
  public static Hashtable chainColours = new Hashtable();
  static { 
    chainColours.put("A",Color.red);
    chainColours.put("B",Color.orange);
    chainColours.put("C",Color.yellow);
    chainColours.put("D",Color.green);
    chainColours.put("E",Color.cyan);
    chainColours.put("F",Color.blue);
    chainColours.put("G",Color.magenta);
    chainColours.put("H",Color.pink);
  }
}

  
  
  

  
  

