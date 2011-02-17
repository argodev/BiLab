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

import java.awt.*;
public class BinarySequence extends Sequence {
  int[] binary;
  double[] dbinary;

  public BinarySequence(Sequence s) {
    super(s);
  }

  public BinarySequence(String name, String sequence, int start, int end) {
    super(name,sequence,start,end);
  }

  public void encode() {

    
    // Set all matrix to 0
    dbinary = new double[getSequence().length() * 21];
    int nores = 21;
    for (int i = 0; i < dbinary.length; i++) {
      dbinary[i] = 0.0; 
    }
    
    for (int i=0; i < getSequence().length(); i++ ) {
      int aanum = 20;
      try {
	aanum = ((Integer)ResidueProperties.aaHash.get(getSequence().substring(i,i+1))).intValue();
      } catch (NullPointerException e) {
	aanum = 20;
      } 
      if (aanum > 20) {aanum = 20;}

      dbinary[i* nores + aanum] = 1.0;
      
    }
  }
  public void blosumEncode() {
    
    // Set all matrix to 0
    dbinary = new double[getSequence().length() * 21];
    int nores = 21;
    //for (int i = 0; i < dbinary.length; i++) {
    //  dbinary[i] = 0.0; 
    //}
    
    for (int i=0; i < getSequence().length(); i++ ) {
      int aanum = 20;
      try {
	aanum = ((Integer)ResidueProperties.aaHash.get(getSequence().substring(i,i+1))).intValue();
      } catch (NullPointerException e) {
	aanum = 20;
      } 
      if (aanum > 20) {aanum = 20;}

      // Do the blosum thing
      for (int j = 0;j < 20;j++) {
	dbinary[i * nores + j] = ResidueProperties.BLOSUM62[aanum][j];
      }
      
    }
  }
  public String toBinaryString() {
    String out = "";
    for (int i=0; i < binary.length;i++) {
      out += (new Integer(binary[i])).toString();
      if (i < binary.length-1) {
	out += " ";
      }
    }
    return out;
  }
  public static void printMemory(Runtime rt) {
    System.out.println("Free memory = " + rt.freeMemory());
  }


  public static void main(String[] args) {
    long tstart = System.currentTimeMillis();
    Runtime rt = Runtime.getRuntime();
    printMemory(rt);
    try {
      // Read the sequence file
      long tend = System.currentTimeMillis();
      System.out.println("Reading file " + (tend-tstart) + "ms");
      tstart = System.currentTimeMillis();
      MSFfile msf = new MSFfile(args[0],"File");
      tend = System.currentTimeMillis();
      System.out.println("done " + (tend-tstart) + "ms");
      System.out.println("Creating sequences");
      tstart = System.currentTimeMillis();
      DrawableSequence[] s = new DrawableSequence[msf.seqs.size()];
      for (int i=0;i < msf.seqs.size();i++) {
	s[i] = new DrawableSequence((Sequence)msf.seqs.elementAt(i));
      }
      tend = System.currentTimeMillis();
      System.out.println("done " + (tend-tstart) + "ms");
      System.out.println("Diagonalizing matrix");
      tstart = System.currentTimeMillis();
      PCA pca = new PCA(s);
      pca.run();
      tend = System.currentTimeMillis();
      System.out.println("done " + (tend-tstart) + "ms");
      System.out.println("Finding component coords");
      tstart = System.currentTimeMillis();

      // Now find the component coordinates      
      double[][] comps = new double[msf.seqs.size()][msf.seqs.size()];

      for (int i=0; i < msf.seqs.size(); i++ ) {
	if (pca.eigenvector.d[i] > 1e-4) {
	  comps[i]  = pca.component(i);
	} 
	  
      }
      tend = System.currentTimeMillis();
      System.out.println("done " + (tend-tstart) + "ms");
      System.out.println("Creating frame");
      tstart = System.currentTimeMillis();
      
      // for (int j = 0; j < s.length; j++ ){
      //	Format.print(System.out,"%20s",s[j].name + " ");
      //	int i = s.length-1;
      //	while (i >= 0 ) {
      //	  Format.print(System.out,"%13.4e",comps[i][j]);
      //	  i--;
      //	}
      //	System.out.println();
      //}
      //System.out.println();

 
      
          Frame f = new Frame();
      f.setLayout(new BorderLayout());
      PCAPanel p  = new PCAPanel(f,pca,s);
      f.add("Center",p);
      f.resize(400,400);
      
          AlignFrame af = new AlignFrame(p,s);//
       af.resize(700,300);
      	   af.show();
      f.show();
      //System.exit(0);
    } catch (java.io.IOException e) {
     System.out.println("IOException : " + e);
    }
  }
    
}
    
















