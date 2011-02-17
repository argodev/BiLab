/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jalview;
import java.util.*;
import java.awt.*;

public class ClustalxColourScheme implements ColourScheme {
  Hashtable[] cons;
  int[][] cons2;
  ConsensusColour[] colours;
  ConsensusColour[] ResidueColour;
  int size;
  Hashtable colhash = new Hashtable();
  Consensus[] conses = new Consensus[32];
  
  Vector colourTable = new Vector();
  public ClustalxColourScheme() {
    this.cons2 = null;
    this.size = 0;
    makeColours();
  }
  public ClustalxColourScheme(int[][] cons2, int size) {
    this.cons2 = cons2;
    this.size = size;
    makeColours();
  }
  public void makeColours() {
    //    System.out.println("Making colours");
    
    
    colhash.put("RED",new Color((float)0.9,(float)0.2,(float)0.1));
    colhash.put("BLUE",new Color((float)0.5,(float)0.7,(float)0.9));
    colhash.put("GREEN",new Color((float)0.1,(float)0.8,(float)0.1));
    colhash.put("ORANGE",new Color((float)0.9,(float)0.6,(float)0.3));
    colhash.put("CYAN",new Color((float)0.1,(float)0.7,(float)0.7));
    colhash.put("PINK",new Color((float)0.9,(float)0.5,(float)0.5));
    colhash.put("MAGENTA",new Color((float)0.8,(float)0.3,(float)0.8));
    colhash.put("YELLOW",new Color((float)0.8,(float)0.8,(float)0.0));
    
    
    conses[0] = new Consensus("WLVIMAFCYHP",60);
    conses[1] = new Consensus("WLVIMAFCYHP",80);
    conses[2] = new Consensus("ED",50);
    conses[3] = new Consensus("KR",60);
    conses[4] = new Consensus("G",50);
    conses[5] = new Consensus("N",50);
    conses[6] = new Consensus("QE",50);
    conses[7] = new Consensus("P",50);
    conses[8] = new Consensus("TS",50);
    
    conses[26] = new Consensus("A",85);
    conses[27] = new Consensus("C",85);
    conses[10] = new Consensus("E",85);
    conses[11] = new Consensus("F",85);
    conses[12] = new Consensus("G",85);
    conses[13] = new Consensus("H",85);
    conses[14] = new Consensus("I",85);
    conses[15] = new Consensus("L",85);
    conses[16] = new Consensus("M",85);
    conses[17] = new Consensus("N",85);
    conses[18] = new Consensus("P",85);
    conses[19] = new Consensus("Q",85);
    conses[20] = new Consensus("R",85);
    conses[21] = new Consensus("S",85);
    conses[22] = new Consensus("T",85);
    conses[23] = new Consensus("V",85);
    conses[24] = new Consensus("W",85);
    conses[25] = new Consensus("Y",85);
    conses[28] = new Consensus("K",85);
    conses[29] = new Consensus("D",85);
    
    conses[30] = new Consensus("G",0);
    conses[31] = new Consensus("P",0);
    
    // We now construct the colours
    colours = new ConsensusColour[11];
    
    
    Consensus[] tmp8 = new Consensus[1];
    tmp8[0] = conses[30];//G
    colours[7] =new ConsensusColour((Color)colhash.get("ORANGE"),tmp8);
    
    Consensus[] tmp9 = new Consensus[1];
    tmp9[0] = conses[31];//P
    colours[8] =new ConsensusColour((Color)colhash.get("YELLOW"),tmp9);
    
    Consensus[] tmp10 = new Consensus[1];
    tmp10[0] = conses[27];//C
    colours[9] =new ConsensusColour((Color)colhash.get("PINK"),tmp8);
    
    Consensus[] tmp1 = new Consensus[14];
    tmp1[0] = conses[0]; //%
    tmp1[1] = conses[1]; //#
    tmp1[2] = conses[26]; //A
    tmp1[3] = conses[27]; //C
    tmp1[4] = conses[11]; //F
    tmp1[5] = conses[13]; //H
    tmp1[6] = conses[14]; //I
    tmp1[7] = conses[15]; //L
    tmp1[8] = conses[16]; //M
    tmp1[9] = conses[23]; //V
    tmp1[10] = conses[24]; //W
    tmp1[11] = conses[25]; //Y
    tmp1[12] = conses[18]; //P
    tmp1[13] = conses[19]; //p
    colours[0] = new ConsensusColour((Color)colhash.get("BLUE"),tmp1);
    
    colours[10] = new ConsensusColour((Color)colhash.get("CYAN"),tmp1);
    
    Consensus[] tmp2 = new Consensus[5];
    tmp2[0] = conses[8];  //t
    tmp2[1] = conses[21]; //S
    tmp2[2] = conses[22]; //T
    tmp2[3] = conses[0];  //%
    tmp2[4] = conses[1];  //#
    colours[1] = new ConsensusColour((Color)colhash.get("GREEN"),tmp2);
    
    Consensus[] tmp3 = new Consensus[3];
    
    tmp3[0] = conses[17]; //N
    tmp3[1] = conses[29]; //D
    tmp3[2] = conses[5];  //n
    colours[2] = new ConsensusColour((Color)colhash.get("GREEN"),tmp3);
    
    Consensus[] tmp4 = new Consensus[6];
    tmp4[0] = conses[6];  // q = QE
    tmp4[1] = conses[19]; //Q
    tmp4[2] = conses[22]; //E
    tmp4[3] = conses[3];  //+
    tmp4[4] = conses[28]; //K
    tmp4[5] = conses[20]; //R
    colours[3] = new ConsensusColour((Color)colhash.get("GREEN"),tmp4);
    Consensus[] tmp5 = new Consensus[4];
    tmp5[0] = conses[3];  //+
    tmp5[1] = conses[28]; //K
    tmp5[2] = conses[20]; //R
    tmp5[3] = conses[19]; //Q
    colours[4] = new ConsensusColour((Color)colhash.get("RED"),tmp5);
    Consensus[] tmp6 = new Consensus[5];
    tmp6[0] = conses[3];  //-
    tmp6[1] = conses[29]; //D
    tmp6[2] = conses[10]; //E
    tmp6[3] = conses[6];  //q
    tmp6[4] = conses[19]; //Q
    colours[5] = new ConsensusColour((Color)colhash.get("MAGENTA"),tmp6);
    
    Consensus[] tmp7 = new Consensus[5];
    tmp7[0] = conses[3];  //-
    tmp7[1] = conses[29]; //D
    tmp7[2] = conses[10]; //E
    tmp7[3] = conses[17];  //N
    tmp7[4] = conses[2]; //DE
    colours[6] = new ConsensusColour((Color)colhash.get("MAGENTA"),tmp7);
    
    // Now attach the ConsensusColours to the residue letters
    ResidueColour = new ConsensusColour[20];
    ResidueColour[0] = colours[0]; // A
    ResidueColour[1] = colours[4]; // R
    ResidueColour[2] = colours[2]; // N
    ResidueColour[3] = colours[6]; // D
    ResidueColour[4] = colours[0]; // C
    ResidueColour[5] = colours[3]; // Q
    ResidueColour[6] = colours[5]; // E
    ResidueColour[7] = colours[7]; // G
    ResidueColour[8] = colours[10]; // H
    ResidueColour[9] = colours[0]; // I
    ResidueColour[10] = colours[0]; // L
    ResidueColour[11] = colours[4]; // K
    ResidueColour[12] = colours[0]; // M
    ResidueColour[13] = colours[0]; // F
    ResidueColour[14] = colours[8]; // P
    ResidueColour[15] = colours[1]; // S
    ResidueColour[16] = colours[1]; // T
    ResidueColour[17] = colours[0]; // W
    ResidueColour[18] = colours[10]; // Y
    ResidueColour[19] = colours[0]; // V
  }
  
  public Color findColour(DrawableSequence seq, String s, int j) {
    // System.out.println("Finding colour for " + s + " " + j);
    int i = seq.num[j];
    Color c = Color.white;
    for (int k=0; k < ResidueColour[i].conses.length ; k++) {
      if (ResidueColour[i].conses[k].isConserved(cons2,j,seq.num[j],size)) {
        c =  ResidueColour[i].c;
      }
    }
    if (i ==4) {
      if (conses[27].isConserved(cons2,j,seq.num[j],size)) {
        c = (Color)colhash.get("PINK");
      }
    }
    return c;
  }
  
  public void setColours(DrawableSequence seq, int j) {
    Color c = Color.white;
    
    String s = seq.getSequence().substring(j,j+1);
    try {
      c = findColour(seq,s,j);
      seq.setResidueBoxColour(j,c);
    } catch (Exception e) {
//e.printStackTrace();//!!!-DJ      
      seq.setResidueBoxColour(j,Color.white);
    }
  }
  
  public void setColours(DrawableSequence s) {
    for (int j = 0; j < s.sequence.length(); j++) {
      setColours(s,j);
    }
  }
  
  public void setColours(SequenceGroup sg) {
    for (int j = 0; j < sg.sequences.size(); j++) {
      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(j);
      for (int i = 0; i < s.getSequence().length();i++) {
        setColours(s,i);
      }
    }
  }
  
  
}










