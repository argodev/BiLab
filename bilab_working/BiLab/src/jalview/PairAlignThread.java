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
import java.util.*;

public final class PairAlignThread extends Thread {
  AlignFrame af;

  public PairAlignThread(AlignFrame af) {
    this.af = af;
  }

  public void run() {
    float scores[][] = new float[af.ap.seqPanel.align.size()][af.ap.seqPanel.align.size()];
    double totscore = 0;
    int count = af.ap.sel.size();
    
    if (count == 1) {
      af.status.setText("ERROR: only 1 sequence selected");
    } else if (count == 0) {
      af.status.setText("ERROR: no sequences selected");
    } else {
      PairAlignFrame tf = new PairAlignFrame("Pairwise alignments",25,73,"");
      tf.setTextFont(new Font("Courier",Font.PLAIN,12));
      tf.resize(550,550);
      tf.show();
      int acount = 0;
      for (int i = 1; i < count; i++) {
	for (int j = 0; j < i; j++) {
	  acount++;
	  AlignSeq as = new AlignSeq((Sequence)af.ap.sel.elementAt(i),(Sequence)af.ap.sel.elementAt(j),"pep");
	  tf.status.setText("Aligning " + as.s1.getName() + " and " + as.s2.getName() + " (" + acount + "/" + (count*(count-1)/2) + ")");
	  
	  as.calcScoreMatrix();
	  as.traceAlignment();
	  as.printAlignment();
	  scores[i][j] = (float)as.maxscore/(float)as.aseq1.length;
	  totscore = totscore + scores[i][j];
	  
	  tf.setText(tf.getText() + as.output);
	  tf.ta.setCaretPosition(tf.ta.getText().length());
	  tf.addSequence(new DrawableSequence(as.s1.getName(),as.astr1,0,0));
	  tf.addSequence(new DrawableSequence(as.s2.getName(),as.astr2,0,0));
	  
	}
      }
      af.status.setText("done");
      System.out.println();
      
      if (count > 2) {
	System.out.print("      ");
	//	for (int i = 0; i < count ; i++) {
	  //	 Format.print(System.out,"%6d ",i);
	// Format.print(System.out,"%6d ",sel[i]+1);
	//}
	//System.out.println();
	
	for (int i = 0; i < count;i++) {
	  //	  Format.print(System.out,"%6d",sel[i]+1);
	  for (int j = 0; j < i; j++) {
	    Format.print(System.out,"%7.3f",scores[i][j]/totscore);
	  }
	  System.out.println();
	}
      }
    }
  }
}
