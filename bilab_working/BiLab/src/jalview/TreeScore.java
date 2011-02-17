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

import java.util.*;
import java.io.*;

public class TreeScore {

  Vector score;
  int start;
  int end;
  int step;
  int window;
  Vector selseq;
  Vector selcol;
  Sequence[] s;

  double[][] od1;
  double[][] od2;

  int oldstart1 = -1000;
  int oldstart2 = -1000;
  int oldend1 = -1000;
  int oldend2 = -1000;

  public TreeScore(Sequence[] s,int start, int end, int step, int window,Vector selseq, Vector selcol) {
    this.s = s;
    this.start = start;
    this.end = end;
    this.step = step;
    this.window = window;
    this.selseq = selseq;
    this.selcol = selcol;

    int nocols = countCols(selcol);
    int chunks = (nocols - window)/step;
    int endi = findEndi();

    score = new Vector();
    
    int i=0;
    while (i < endi) {
      System.out.println("Calculating score for position " + i);
      score.addElement(calcScore(i));
      i = i + step;   // For selcol this should be calculated
    }
  }
  public Vector calcScore(int start) {
    Vector out = new Vector();
    int split = start + window/2-1;
    
    //double[][] d1 = calcDistances(start,split);
    //double[][] d2 = calcDistances(split+1,start+window);

    double[][] d1 = calcDistances(start,split,od1,oldstart1,oldend1);
    double[][] d2 = calcDistances(split+1,start+window-1,od2,oldstart2,oldend2);
    
//    for (int i=0; i < s.length; i++) {
//       for (int j=0; j < s.length; j++) {
// 	System.out.print(d1[i][j] + " " );
//       }
//       System.out.println();
//     }
//     System.out.println();
//     for (int i=0; i < s.length; i++) {
//       for (int j=0; j < s.length; j++) {
// 	System.out.print(d2[i][j] + " " );
//       }
//     System.out.println();
//     }
//     System.out.println();
    for (int i=0; i < s.length; i++) {
      
      double tmp = dotProd(d1[i],d2[i],s.length);
      out.addElement(new Double(tmp));

    }

    od1 = d1;
    od2 = d2;

    oldstart1 = start;
    oldend1 = split;
    oldstart2 = split + 1;
    oldend2 = start + window - 1;
    //    System.out.println("olds " + oldstart1 + " " + oldend1 + " " + oldstart2 + " " + oldend2 + " " + split);
    return out;
  }

  public double[][] calcDistances(int start, int end) {
    double[][] out = new double[s.length][s.length];
    for (int i=0;i< s.length; i++) {
      for (int j=0;j < s.length; j++) {
	out[i][j] = 0.0;
	for (int k=start; k <= end; k++) {
	  out[i][j] += ResidueProperties.getBLOSUM62(s[i].sequence.substring(k,k+1),s[j].sequence.substring(k,k+1));
	}
      }
    }
    
    return out;
  }

  public double[][] calcDistances(int start, int end, double[][] dold,int oldstart,int oldend) {
    if (oldend > start) {
      for (int i=0;i< s.length; i++) {

	for (int j=0;j < s.length; j++) {

	  for (int k=oldstart; k < start; k++) {
	    dold[i][j] -= ResidueProperties.getBLOSUM62(s[i].sequence.substring(k,k+1),s[j].sequence.substring(k,k+1));
	  }
	  for (int k = oldend+1; k <= end; k++) {
	    dold[i][j] += ResidueProperties.getBLOSUM62(s[i].sequence.substring(k,k+1),s[j].sequence.substring(k,k+1));
	  }
	}
      }
    } else {
      dold = calcDistances(start,end);
    }
    return dold;
  }

  public double dotProd(double[] d1, double[] d2, int m) {
      double tmp = 0.0;

      double meanx = 0.0;
      double meany = 0.0;

      double sumxy = 0.0;

      double sumx2 = 0.0;
      double sumy2 = 0.0;

      for (int j=0; j < m; j++) {
	meanx += d1[j];
	meany += d2[j];
      }

      meanx /= (double)m;
      meany /= (double)m;
      
      double tmp2 = 0.0;

      // System.out.println("Means " + meanx + " " + meany);
      for (int j=0; j < m; j++) {
	sumxy += (d1[j]-meanx) * (d2[j] - meany);
	tmp2 += d1[j] * d2[j];
      }
      // System.out.println("sumxy " + sumxy);
      for (int j=0; j < m; j++) {
	sumx2 += (d1[j]-meanx)*(d1[j]-meanx);
      }

      sumx2 = Math.sqrt(sumx2);

      for (int j=0; j < m; j++) {
	sumy2 += (d2[j]-meany)*(d2[j]-meany);
      }

      sumy2 = Math.sqrt(sumy2);
      //System.out.println("sumx2y2 " + sumx2 + " " + sumy2);
      if (sumx2 == 0.0 || sumy2 == 0) {
	tmp = 1;
      } else {
	tmp = sumxy/(sumx2*sumy2);
      }

      //System.out.println("Tmp = " + tmp);
      if (tmp > 1) { tmp = 1;}
      return tmp*100;
  }
    
  public int findEndi() {
    if (selcol == null) {
      return s[0].sequence.length() - window;
    } else {
      // Needs changing
      return s[0].sequence.length() - window;
    }
  }
  public int countCols(Vector sel) {
    if (sel == null || sel.size() == 0) {
      return s[0].sequence.length();
    } else {
      //domething here
      return s[0].sequence.length();
    }
  }
  public static void main(String[] args) {
    Sequence[] s = FormatAdapter.read(args[0],"File",args[1]);
    TreeScore ts = new TreeScore(s,0,s[0].sequence.length()-1, Integer.parseInt(args[2]),Integer.parseInt(args[3]),null,null);
    try {
      PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(args[4])));


      if (ts.score != null && ts.score.size() > 0) {
	for (int i=0; i < ts.score.size(); i++) {
	  if (ts.score.elementAt(i) instanceof Vector) {
	    Vector tmp = (Vector)ts.score.elementAt(i);
	    for (int j = 0;j < tmp.size(); j++) {
	      int tmp2 = ((Double)tmp.elementAt(j)).intValue();
	      Format.print(ps,"%8d ",tmp2);
	    }
	    ps.println();
	  }
	}
      }
      ps.close();
    } catch (IOException ex) {
      System.out.println("Exception : "+ ex);
    }
  }
}


