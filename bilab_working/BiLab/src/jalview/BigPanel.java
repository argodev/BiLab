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

public class BigPanel extends AlignmentPanel {
  Object parent;
  public ScorePanel scorePanel;
  
  Scrollbar hscroll;
  int maxoffx;
  int maxoffy;
  
  int offx;
  int offy;
  int width;
  int height;

  Panel p;

  public BigPanel(Component p, DrawableSequence[] s1) {
    super(p,s1);
    //    ScoreSequence[] s2= findScores(s1);
    // try {
    //  if ( s2.length > 0) {
    //	System.out.println("Creating scorePanel");
    //	scorePanel = new ScorePanel(p,s2);
    //  } else {
	scorePanel = null;
	//  }
	//  } catch (Exception e) {
	//System.out.println("Exception creating scorePanel : " + e);
	//scorePanel = null;
	// }
    componentInit();
  }



  public BigPanel(Component p ,DrawableSequence[] s1,ScoreSequence[] s2) {
    super(p,s1);
    scorePanel = new ScorePanel(p,s2);
    scorePanel = null;
    componentInit();
    System.out.println("Loaded BigPanel");
  }

  public void componentInit() {
    super.componentInit();
    hscroll = new Scrollbar(Scrollbar.HORIZONTAL);
    add(hscroll);
    setScrollValues(0,0);

    if (scorePanel != null) {
      add(scorePanel);
      scorePanel.seqPanel.remove(scorePanel.seqPanel.hscroll);
    }

    seqPanel.remove(seqPanel.hscroll);
    
    System.out.println("Finished BigPanel.componentInit");
  }
  
  public ScoreSequence[] findScores(Sequence[] s) {
    Vector score = new Vector();
    int i=0;
    while (i < s.length && s[i] != null) {
      int l=0;
      while (l < s[i].score.length && s[i].score[l] != null) {
        if (s[i].score[l].size() > 0) {
	  score.addElement(new ScoreSequence(s[i],l));
        }
        l++;
      }
      i++;
    }
    ScoreSequence[] out = new ScoreSequence[score.size()];
    for (i=0; i < score.size();i++) {
      out[i] = (ScoreSequence)score.elementAt(i);
    }
    return out;
  }

  public void setScorePanel(ScorePanel sp) {
    this.scorePanel = sp;
    sp.seqPanel.remove(sp.seqPanel.hscroll);
    add(scorePanel);
  }

   public void reshape(int x, int y, int width, int height) {
     // This is completely overidden to get the seqPanel size right
     // This is necessary otherwise flicker occurs
     super.oldreshape(x,y,width,height);

     int scrh = 20;
     int idw = idPanel.idCanvas.idWidth;
     int h = height-scrh;

     if (scorePanel != null) {
       // What is the scorePanel height?
       scorePanel.idPanel.idCanvas.idWidth = idw;
       //       int sph = scorePanel.seqPanel.align.size() * scorePanel.seqPanel.seqCanvas.charHeight;

       int sph = scorePanel.seqPanel.align.getHeight(0,scorePanel.seqPanel.align.size()-1)
	 + scorePanel.seqPanel.align.ds[scorePanel.seqPanel.align.size()-1].charHeight + 20;
	 // scorePanel.seqPanel.remove(scorePanel.seqPanel.vscroll);
       
       int sqh = (seqPanel.align.size()+1) * seqPanel.seqCanvas.charHeight;
       //int sqh = seqPanel.align.getHeight(0,seqPanel.align.size()-1);
       if (sph < (height-scrh)/2) { 
	 h = height-scrh-sph;
       } else {
	 h = (height-scrh)*sqh/(sph+sqh);
       }

       if (scaleheight + (seqPanel.align.size()+1) * seqPanel.seqCanvas.charHeight < h) {
	 //if (scaleheight + seqPanel.align.getHeight(0,seqPanel.align.size()-1) < h) {
	 h = (seqPanel.align.size()+1) * seqPanel.seqCanvas.charHeight + scaleheight;
	 //h =  seqPanel.align.getHeight(0,seqPanel.align.size()-1) + scaleheight;

	}

       scorePanel.reshape(0,h,width,height-h-scrh);
     } 
       
     scalePanel.reshape(0,0,width,scaleheight);
     idPanel.reshape(0,scaleheight,idw,h-scaleheight);
     seqPanel.reshape(idw,scaleheight,width-idw,h-scaleheight);

     hscroll.reshape(0,height-scrh,width,scrh);
     setScrollValues(offx,0);
   }

  public void setScrollValues(int offx, int offy) {
    seqPanel.invalidate();
    invalidate();
    validate();
    
    if (seqPanel.seqCanvas.size().width > 0) {
      width = seqPanel.seqCanvas.size().width;
      height = seqPanel.seqCanvas.size().height;
    } else {
      width = 700;
      height = 500;
    }
    
    //Make sure the maxima are right
    if (maxoffx != (seqPanel.align.maxLength()+1)) {
      maxoffx = (seqPanel.align.maxLength()+1);
    } 
 
    //The extra 1 is to make sure all the last character gets printed
    hscroll.setValues(offx, width/seqPanel.seqCanvas.charWidth,0,maxoffx);
    hscroll.setLineIncrement(1);

    if (seqPanel.seqCanvas.endx > 0) {
      hscroll.setPageIncrement((seqPanel.seqCanvas.endx-seqPanel.seqCanvas.startx)/2);
    } 
   }
  
  public boolean handleEvent(Event evt) {
     switch(evt.id) {
     case Event.SCROLL_LINE_UP:
     case Event.SCROLL_LINE_DOWN:
     case Event.SCROLL_ABSOLUTE:
     case Event.SCROLL_PAGE_UP:
     case Event.SCROLL_PAGE_DOWN:
       if (evt.target == hscroll) {
       	offx = hscroll.getValue();
	seqPanel.offx = offx;
 	scalePanel.scaleCanvas.paintFlag = true;
 	seqPanel.seqCanvas.repaint();
 	scalePanel.scaleCanvas.repaint();

	if (scorePanel != null) {
	  scorePanel.seqPanel.offx = offx;
	  scorePanel.scalePanel.scaleCanvas.paintFlag = true;
	  scorePanel.seqPanel.seqCanvas.repaint();
	  scorePanel.scalePanel.scaleCanvas.repaint();
	  
	}
	return true;
       }
     }
     return super.handleEvent(evt);
     }
}





