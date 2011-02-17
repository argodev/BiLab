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
import java.awt.*;

public class AlignSeq {

  int[][] score;
  int[][] E;
  int[][] F;
  int[][] traceback;

  int[] seq1;
  int[] seq2;
  
  Sequence s1;
  Sequence s2;

  String s1str;
  String s2str;

  int maxi;
  int maxj;

  int[] aseq1;
  int[] aseq2;

  String astr1 = "";
  String astr2 = "";


  int seq1start;
  int seq1end;
  int seq2start;
  int seq2end;

  int count;

  int maxscore;
  float pid;
  int prev = 0;

  public static java.util.Hashtable dnaHash = new java.util.Hashtable();

  static  {
    dnaHash.put("C", new Integer(0));
    dnaHash.put("T", new Integer(1));
    dnaHash.put("A", new Integer(2));
    dnaHash.put("G", new Integer(3));
    dnaHash.put("-", new Integer(4));
  }
  
  static String dna[] = {"C","T","A","G","-"};
  static String pep[] = {"A","R","N","D","C","Q","E","G","H","I","L","K","M","F","P","S","T","W","Y","V","B","Z","X","-"};

  int gapOpen = 120;
  int gapExtend = 20;

  int lookup[][] = ResidueProperties.BLOSUM62;
  String intToStr[] = pep;
  int defInt = 23;

  String output = "";

  String type;
  Runtime rt;
  public AlignSeq(){}

  public AlignSeq(Sequence s1, Sequence s2,String type){
    rt  = Runtime.getRuntime();
    SeqInit(s1,s2,type);
  }

  public void SeqInit(Sequence s1, Sequence s2,String type){
    System.out.println(s1.getSequence());
    System.out.println(s2.getSequence());
    s1str = extractGaps(".",s1.getSequence());
    s2str = extractGaps(".",s2.getSequence());
    s1str = extractGaps("-",s1str);
    s2str = extractGaps("-",s2str);
    s1str = extractGaps(" ",s1str);
    s2str = extractGaps(" ",s2str);

    this.s1 = s1;
    this.s2 = s2;

    this.type = type;

    if (type.equals("pep")) {
      lookup = ResidueProperties.BLOSUM62;
      intToStr = pep;
      defInt = 23;
    } else if (type.equals("dna")) {
      lookup = ResidueProperties.DNA;
      intToStr = dna;
      defInt = 4;
    } else {
      output = output + ("Wrong type = dna or pep only");
      System.exit(0);
    }
 

    //System.out.println("lookuip " + rt.freeMemory() + " "+  rt.totalMemory());
    seq1 = new int[s1str.length()];
    //System.out.println("seq1 " + rt.freeMemory() +" "  + rt.totalMemory());
    seq2 = new int[s2str.length()];
    //System.out.println("seq2 " + rt.freeMemory() + " " + rt.totalMemory());
    score = new int[s1str.length()][s2str.length()];
    //System.out.println("score " + rt.freeMemory() + " " + rt.totalMemory());
    E = new int[s1str.length()][s2str.length()];
    //System.out.println("E " + rt.freeMemory() + " " + rt.totalMemory());
    F = new int[s1str.length()][s2str.length()];
    traceback = new int[s1str.length()][s2str.length()];
    //System.out.println("F " + rt.freeMemory() + " " + rt.totalMemory());
    seq1 = stringToInt(s1str,type);
    //System.out.println("seq1 " + rt.freeMemory() + " " + rt.totalMemory());
    seq2 = stringToInt(s2str,type);
    //System.out.println("Seq2 " + rt.freeMemory() + " " + rt.totalMemory());

    //   long tstart = System.currentTimeMillis();
    //    calcScoreMatrix();
    //long tend = System.currentTimeMillis();

    //System.out.println("Time take to calculate score matrix = " + (tend-tstart) + " ms");


    //   printScoreMatrix(score);
    //System.out.println();

    //printScoreMatrix(traceback);
    //System.out.println();
    
    //  printScoreMatrix(E);
    //System.out.println();

    ///printScoreMatrix(F);
      //System.out.println();
      // tstart = System.currentTimeMillis();
      //traceAlignment();
      //tend = System.currentTimeMillis();
      //System.out.println("Time take to traceback alignment = " + (tend-tstart) + " ms");
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
	astr1 = "-" + astr1.substring(1);
      } else  if (trace == -1) { 
	i--;
	aseq2[count] = defInt;
	astr2 = "-" + astr2.substring(1);
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
	    output = output + (new Format("%s").form(intToStr[aseq2[count + i + j*len]]));
	  }
	}
	output = output + ("\n\n");
      }
      pid = pid/(float)(aseq1.length-count)*100;
      output = output + (new Format("Percentage ID = %2.2f\n\n").form(pid));
      
    }
  
  public void printScoreMatrix(int[][] mat) {
    int n = seq1.length;
    int m = seq2.length;

    for (int i = 0; i < n;i++) {
	// Print the top sequence
	if (i == 0) {
	  Format.print(System.out,"%8s",s2str.substring(0,1));
	  for (int jj = 1;jj < m; jj++) {
	    Format.print(System.out,"%5s",s2str.substring(jj,jj+1));
	  }
	  System.out.println();
	}

      for (int j = 0;j < m; j++) {
	if (j == 0) {
	  Format.print(System.out,"%3s",s1str.substring(i,i+1));
	}
	Format.print(System.out,"%3d ",mat[i][j]/10);
      }
      System.out.println();
    }
  }

  public int findTrace(int i,int j) {
    int t = 0;
    int max = score[i-1][j-1] + lookup[seq1[i]][seq2[j]] * 10;

    if (F[i][j] > max) {
      max = F[i][j];
      t = -1;
    } else if (F[i][j] == max) {
      if (prev == -1) {
	max = F[i][j];
	t = -1;
      }
    }
    if (E[i][j] >= max) {
      max = E[i][j];
      t = 1;
    } else if (E[i][j] == max) {
      if (prev == 1) {
	max = E[i][j];
	t = 1;
      }
    }
    prev = t;
    return t;
  }

  public void calcScoreMatrix() {


    int n = seq1.length;
    int m = seq2.length;


    // top left hand element
    score[0][0] =  lookup[seq1[0]][seq2[0]] * 10;
    E[0][0] = -gapExtend;
    F[0][0] = 0;

    // Calculate the top row first
    for (int j=1; j < m; j++) {
      // What should these values be? 0 maybe
      E[0][j] = max(score[0][j-1] - gapOpen,E[0][j-1] - gapExtend);
      F[0][j] = -gapExtend;

      score[0][j] = max( lookup[seq1[0]][seq2[j]] * 10 ,-gapOpen,-gapExtend);

      traceback[0][j] = 1;
    }

    // Now do the left hand column
    for (int i=1; i < n; i++) {
      E[i][0] =  -gapOpen;
      F[i][0] =  max(score[i-1][0]-gapOpen,F[i-1][0]-gapExtend);

      score[i][0] = max( lookup[seq1[i]][seq2[0]] * 10 ,E[i][0],F[i][0]);
      traceback[i][0] = -1;
    }
    
    // Now do all the other rows
    for (int i = 1; i < n; i++) {
      for (int j = 1; j < m; j++) {

	E[i][j] = max(score[i][j-1] - gapOpen,  E[i][j-1] - gapExtend);
	F[i][j] = max(score[i-1][j] - gapOpen,  F[i-1][j] - gapExtend);

	score[i][j] = max(score[i-1][j-1] + lookup[seq1[i]][seq2[j]]*10,
			  E[i][j],
			  F[i][j]);
	traceback[i][j] = findTrace(i,j);
      }
    }
    
  }
  public static String extractChars(String chars, String seq) {
    String out = seq;
    for (int i=0; i < chars.length(); i++) {
      String gap = chars.substring(i,i+1);
      out = extractGaps(gap,out);
    }
    return out;
  }
  public static String extractGaps(String gapChar, String seq) {
    StringTokenizer str = new StringTokenizer(seq,gapChar);
    String newString = "";

    while (str.hasMoreTokens()) {
      newString  = newString + str.nextToken();
    }
    return newString;
  }  
	

  public int max(int i1, int i2, int i3) {
    int max = i1;
    if (i2 > i1)  {max = i2;}
    if (i3 > max) {max = i3;}
    return max;
  }

  public int max(int i1, int i2) {
    int max = i1;
    if (i2 > i1)  {max = i2;}
    return max;
  }

  public int[] stringToInt(String s,String type) {
    int[] seq1 = new int[s.length()];

    for (int i = 0;i < s.length(); i++) {
      String ss = s.substring(i,i+1).toUpperCase();
      try {
	if (type.equals("pep")) {
	  seq1[i] = ((Integer)ResidueProperties.aaHash.get(ss)).intValue();
	} else if (type.equals("dna")) {
	  seq1[i] = ((Integer)dnaHash.get(ss)).intValue();
	}
	if (seq1[i] > 23) {seq1[i] = 23;}
      } catch (Exception e) {
	if (type.equals("dna")) {
	  seq1[i] = 4;
	} else {
	  seq1[i] = 23;
	}
      }
    }
    return seq1;
  }

  public static void displayMatrix(Graphics g, int[][] mat, int n, int m,int psize) {

    int max = -1000;
    int min = 1000;

    for (int i=0; i < n; i++) {
      for (int j=0; j < m; j++) {
	if (mat[i][j] >= max) { max = mat[i][j];}
	if (mat[i][j] <= min) { min = mat[i][j];}
      }
    }
    System.out.println(max + " " + min);
    for (int i=0; i < n; i++) {
      for (int j=0; j < m; j++) {
	int x = psize*i;
	int y = psize*j;
	//	System.out.println(mat[i][j]);
	float score = (float)(mat[i][j] - min)/(float)(max-min);
	g.setColor(new Color(score,0,0));
	g.fillRect(x,y,psize,psize);
	//	System.out.println(x + " " + y + " " + score);
      }
    }
  }
      
  public static void main(String[] args) {
    
    try {
      if (args.length != 3) {
	System.out.println("args: <msffile> <File|URL> <pep|dna>");
	System.exit(0);
      }
      MSFfile msf = new MSFfile(args[0],args[1]);
      Sequence[] s = new Sequence[msf.seqs.size()];
      int scores[][] = new int[msf.seqs.size()][msf.seqs.size()];
      int totscore = 0;
      for (int i=0;i < msf.seqs.size();i++) {
	s[i] = (Sequence)msf.seqs.elementAt(i);
      }
      
      for (int i = 1; i < 2; i++) {
	for (int j = 0; j < i; j++) {
	  AlignSeq as = new AlignSeq(s[i],s[j],args[2]);
	  as.calcScoreMatrix();
	  as.traceAlignment();
	   as.printAlignment();

	    //as.printScoreMatrix(as.score);
	    //as.printScoreMatrix(as.E);
	    //as.printScoreMatrix(as.F);
	    //as.printScoreMatrix(as.traceback);
	  scores[i][j] = as.maxscore;
	  totscore = totscore + as.maxscore;
	  System.out.println(as.output);
	  Frame f = new Frame("Score matrix");
	  Panel p = new Panel();
	  p.setLayout(new BorderLayout());
	  ick ic = new ick(as);
	  p.add("Center",ic);
	  f.setLayout(new BorderLayout());
	  f.add("Center",p);
	  f.resize(500,500);
	  f.show();
	  
	}
      }
      System.out.println();
      System.out.print("      ");
      for (int i = 1; i < msf.seqs.size(); i++) {
	Format.print(System.out,"%6d ",i);
      }
      System.out.println();
      for (int i = 1; i < msf.seqs.size(); i++) {
	Format.print(System.out,"%6d",i+1);
	for (int j = 0; j < i; j++) {
	  Format.print(System.out,"%7.3f",(float)scores[i][j]/totscore);
	}
	System.out.println();
      }
      //Sequence[] s2 = new Sequence[3];
      //s2[0] = new Sequence("SEQ1",as.astr1,1,120);
      //s2[1] = new Sequence("SEQ2",as.astr2,1,120);
      //AlignFrame af = new AlignFrame(null,s2);
      //af.resize(700,500);
      //af.show();
      //af.cons = af.ap.seqPanel.align.percentIdentity();
      
      //      System.exit(0);

    } catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    //   AlignSeq as = new AlignSeq("GTGASAAATGGNNTGATTCTGTACCTTGTGGAGACTGGCGTGATGTGCAGCAACTATTCGANNGTGATCCAGTGGTTTTGTCGTTGAATCTGTCTTCGATGGTTCTCGGGTAAGCTATCACCAAGCATAGGTGGATTGGTTCATCTGAAGCAGCTGGATCTGTCATATAATGGGTTGTCAGNGGAAAATTCCTAAGGAAATTGGCAACTGTTCAAGCTTGGAGATTCTGAAACTAAACAATAACCAGTTTGATGGTGAGATACCTGTGGAAATAGGTAAGCTTGTGTCTTTGGAGAATCTGATCATATCAACAACAGAATCTCAGGGTCTCTCCCTGTGGAGATTGG","AAATGATTCTGTACCTTGTGGATGGACTGGCGTGATGTGCAGCAACTATTCGAGTGATCCAGAGGTTTTGTCCTTGAATCTGTCTTCGATGGTTCTCTCGGGTAAGATCCACCAAGCATA");
  }
}



class ick extends Canvas {
  AlignSeq fs;
  public ick(AlignSeq fs) {
    super();
    this.fs = fs;
  }

  public void paint(Graphics g) {
    fs.displayMatrix(g,fs.score,fs.seq1.length,fs.seq2.length,(int)500/fs.seq1.length);
  }

}


