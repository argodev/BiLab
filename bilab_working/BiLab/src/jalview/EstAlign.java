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

import java.io.*;



public class EstAlign extends AlignSeq {



  int[] donor;

  int[] acceptor;

  int[][] G;

  int intronExon = 500;

  int splice = 100;



  public EstAlign(Sequence s1, Sequence s2,String type){

    super(s1,s2,type);

    donor = findDonors();

    acceptor = findAcceptors();

  }



  public int[] findDonors() {

    int[] donor = new int[s1str.length()];



    for (int i = 0; i < (s1str.length()-1); i++) {

      if (s1str.substring(i,i+2).equals("GT")) {

	donor[i] = 0;

      } else {

	donor[i] = 1;

      }

    }

    donor[seq1.length-1] = 1;



    return donor;

  }

  

  public int[] findAcceptors() {

    int[] acceptor = new int[s1str.length()];

    acceptor[0] = 1;



    for (int i = 1; i < s1str.length(); i++) {

      if (s1str.substring(i-1,i+1).equals("AG")) {

	acceptor[i] = 0;

      } else {

	acceptor[i] = 1;

      }

    }

    return acceptor;

  }

  public void calcScoreMatrix() {

    int n = seq1.length;

    int m = seq2.length;



    G = new int[n][m];



    // top left hand element

    score[0][0] =  lookup[seq1[0]][seq2[0]] * 10;



    E[0][0] = 0;

    F[0][0] = 0;

    G[0][0] = 0;



    // Calculate the top row first

    for (int j = 1; j < m; j++) {

      // What should these values be? 0 maybe

      E[0][j] = max(score[0][j-1] - gapOpen,E[0][j-1] - gapExtend);

      F[0][j] = 0;

      G[0][j] = 0;



      score[0][j] = max(lookup[seq1[0]][seq2[j]] * 10 ,-gapOpen,-gapExtend);

    }



    

    // Now do the left hand column

    for (int i = 1; i < n; i++) { 

      E[i][0] =  0;

      F[i][0] =  max(score[i-1][0]-gapOpen,F[i-1][0]-gapExtend);

      G[i][0] =  max(score[i-1][0]-donor[i]*intronExon - splice  ,

		     G[i-1][0], 

		     G[i-1][0]-acceptor[i]*intronExon  - splice  + lookup[seq1[i]][seq2[0]]*10);

    

      score[i][0] = max( lookup[seq1[i]][seq2[0]] * 10 ,E[i][0],F[i][0], G[i][0]);



    }

    

    // Now do all the other rows

    for (int i = 1; i < n; i++) {

      for (int j = 1; j < m; j++) {

	E[i][j] = max(score[i][j-1] - gapOpen,  E[i][j-1] - gapExtend);

	F[i][j] = max(score[i-1][j] - gapOpen,  F[i-1][j] - gapExtend);



	G[i][j] = max(score[i-1][j] - donor[i]*intronExon - splice  , 

		      G[i-1][j], 

		      G[i-1][j] - acceptor[i]*intronExon - splice   + lookup[seq1[i]][seq2[j]]*10);



	score[i][j] = max(score[i-1][j-1] + lookup[seq1[i]][seq2[j]]*10,

			  E[i][j],

			  F[i][j],

			  G[i][j]);

      }

    }



  }



  public void traceAlignment() {



    // Find the maximum score along the rhs or bottom row

    int max = -9999;

    for (int i = 0; i < seq1.length; i++) {

      if (score[i][seq2.length - 1] > max ) {

	max = score[i][seq2.length - 1];

	maxi = i;

	maxj = seq2.length-1;

      }

    }

    for (int j = 0; j < seq2.length; j++) {

      if (score[seq1.length - 1][j] > max ) {

	max = score[seq1.length - 1][j];

	maxi = seq1.length-1;

	maxj = j;

      }

    }



    //  System.out.println(maxi + " " + maxj + " " + score[maxi][maxj]);



    int i = maxi;

    int j = maxj;

    int trace;

    maxscore = score[i][j] / 10;



    seq1end = maxi+1;

    seq2end = maxj+1;



    aseq1 = new int[seq1.length + seq2.length];

    aseq2 = new int[seq1.length + seq2.length];

    

    count = seq1.length + seq2.length - 1;



    while (i>0 && j >0) {



      if (aseq1[count] != defInt && i >=0) {

	aseq1[count] = seq1[i];

	astr1 = intToStr[seq1[i]] + astr1;

      }



      if (aseq2[count] != defInt && j > 0) {

	aseq2[count] = seq2[j];

	astr2 = intToStr[seq2[j]] + astr2;

      }

      trace = findTrace(i,j);



      if (trace == 0) { 

	i--;

	j--;



      } else  if (trace == 1) { 

	j--;

	aseq1[count] = defInt;

	astr1 = "*" + astr1.substring(1);

      } else  if (trace == -1 || trace == -2) { 

	i--;

	aseq2[count] = defInt;

	if (trace == -1) {

	    astr2 = "." + astr2.substring(1);

	} else {

	    astr2 = "^" + astr2.substring(1);

	}

      }

      count--;

    }



    seq1start = i+1;

    seq2start = j+1;



    if (aseq1[count] != defInt) {

      aseq1[count] = seq1[i];

       astr1 = intToStr[seq1[i]] + astr1;

    }

    

    if (aseq2[count] != defInt) {

      aseq2[count] = seq2[j];

       astr2 = intToStr[seq2[j]] + astr2;

    }

  }

  

  public int findTrace(int i,int j) {

    int t = 0;

    int max = score[i-1][j-1] + lookup[seq1[i]][seq2[j]] * 10;



    if (G[i][j] > max) {

      max = G[i][j];

      t = -2;

      prev = -2;

    } else if (G[i][j] == max) {

      if (prev == -2) {

	max = G[i][j];

	t = -2;

	prev = -2;

      }

    }

    if (F[i][j] > max) {

      max = F[i][j];

      t = -1;

      prev = -1;

    } else if (F[i][j] == max) {

      if (prev == -1) {

	max = F[i][j];

	t = -1;

	prev = -1;

      }

    }

    if (E[i][j] >= max) {

      max = E[i][j];

      t = 1;

      prev = 1;

    } else if (E[i][j] == max) {

      if (prev == 1) {

	max = E[i][j];

	t = 1;

	prev = 1;

      }

    }



    return t;

  }



  public void printAlignment() {

    // Find the biggest id length for formatting purposes

    int maxid = s1.getName().length();



    if (s2.getName().length() > maxid) {

      maxid = s2.getName().length();

    }



    int len = 72 - maxid - 1;    

    int nochunks = ((aseq1.length - count) / len) + 1;

    pid = 0;

    int overlap = 0;



    output = output + ("Score = " + score[maxi][maxj] + "\n");

    output = output + ("Length of alignment = " + (aseq1.length-count) + "\n");

    output = output + ("Sequence ");

    output = output + (new Format("%" + maxid + "s").form(s1.getName()));

    output = output + (" :  " + seq1start + " - " + seq1end + " (Sequence length = " + s1str.length() + ")\n");

    output = output + ("Sequence ");

    output = output + (new Format("%" + maxid + "s").form(s2.getName()));

    output = output + (" :  " + seq2start + " - " + seq2end + " (Sequence length = " + s2str.length() + ")\n\n");

      

      for (int j = 0; j < nochunks; j++) {

	// Print the first aligned sequence

	output = output + (new Format("%" + (maxid) + "s").form(s1.getName()) + " ");

	for (int i = 0; i < len ; i++) {

	  

	  if ((count + i + j*len) < aseq1.length) {

	    output = output + (new Format("%s").form(intToStr[aseq1[count + i + j*len]]));

	  }

	}

	

	output = output + ("\n");

	output = output + (new Format("%" + (maxid) + "s").form(" ") + " ");

	// Print out the matching chars

	for (int i = 0; i < len ; i++) {

	  

	  if ((count + i + j*len) < aseq1.length) {

	    if ( intToStr[aseq1[count+i+j*len]].equals(intToStr[aseq2[count+i+j*len]]) && !intToStr[aseq1[count+i+j*len]].equals("-")) {

	      pid++;

	      output = output + ("|");

	    } else if (type.equals("pep")) {

	      if (ResidueProperties.getPAM250(intToStr[aseq1[count+i+j*len]],intToStr[aseq2[count+i+j*len]]) > 0) {

		output  = output + (".");

	      } else {

		output = output + (" ");

	      }

	    } else {

	      output = output + (" ");

	    }

	    

	  }

	}

	// Now print the second aligned sequence

	output = output + ("\n");

	output = output + (new Format("%" + (maxid) + "s").form(s2.getName()) + " " );

	for (int i = 0; i < len ; i++) {

	  if ((count + i + j*len) < aseq1.length) {

	    //    output = output + (new Format("%s").form(intToStr[aseq2[count + i + j*len]]));



	  }

	}



	if ((j+1)*len < astr2.length()) {

	  output = output + astr2.substring(j*len,(j+1)*len);

	} else {

	  output = output + astr2.substring(j*len);

	}

	output = output + ("\n\n");

      }

      pid = pid/(float)(aseq1.length-count)*100;

      output = output + (new Format("Percentage ID = %2.2f\n\n").form(pid));

      

    }

   



  public int  max(int i1, int i2, int i3, int i4) {

    int max = i1;

    

    if (i2 > i1) {max = i2;}



    if (i3 > max) {max = i3;}

    

    if (i4 > max) {return i4;} else {return max;}

  }

  public static void main(String[] args) {

    

    try {

      if (args.length != 2) {

	System.out.println("args: <dnafile1> <dnafile2> ");

	System.exit(0);

      }



      Sequence[] s = new Sequence[2];

      //      MSFfile msf = new MSFfile(args[0],args[1]);

      FastaFile fa = new FastaFile(args[0],"File");

      s[0] = (Sequence)fa.seqs.elementAt(0);

     

      FastaFile fa2 = new FastaFile(args[1],"File");

      s[1] = (Sequence)fa2.seqs.elementAt(0);

     

      EstAlign as = new EstAlign(s[0],s[1],"dna");

      //  System.out.println(as.s1str + " " + as.s2str);



      as.gapExtend = 50;

      as.gapOpen  = 100;

      as.calcScoreMatrix();

      as.traceAlignment();

      as.printAlignment();

      s[0] = new Sequence(as.s1.getName(),as.astr1,0,0);

      s[1] = new Sequence(as.s2.getName(),as.astr2,0,0);



      // AlignFrame af = new AlignFrame(null,s);

      //af.resize(700,200);

      //af.show();

      System.out.println(as.output + "\nScore = " + as.maxscore);

      

      // System.out.println(as.astr1);

      //System.out.println(as.astr2);

    } catch (Exception e) {

      System.out.println("Exception : " + e);

    }

  }

}

















