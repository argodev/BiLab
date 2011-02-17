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

public class SeqCanvas extends Canvas {
  DrawableAlignment da;
  // For the offscreen drawing
  Image img;
  Graphics gg;
  int imgWidth;
  int imgHeight;

  // The text font
  int fontStyle = Font.PLAIN;
  int fontSize = 10;
  String fontName = "Courier";
  public Font f = new Font(fontName,fontStyle,fontSize);
  int charWidth = 8;
  int charHeight = 8;
  public boolean showScores = true; // -DJ made public

  // The offset of the text from the residue boxes
  int pady = 2;

  // Scrolling values
  int oldstartx;
  int oldstarty;
  int oldendx;
  int oldendy;
  
  // Do we redraw the whole thing?
  boolean paintFlag = false;
  // Do we draw residue boxes?
  boolean boxFlag = true;
  // Do we draw text?
  boolean textFlag = true;
  // Do we draw coloured text?
  boolean colourText = false;

  // Coordinates of the viewable part of the alignment
  int startx;
  int starty;
  int endx;
  int endy;

  // Miscellaneous colours for the percentage identity
  // colour scheme
  Color lightBlue = new Color(175,175,255);
  Color midBlue = new Color(110,110,255);

  // Which colour scheme
  String colourFlag = "PID";
  boolean badResidues = false;

  // Colour the selected sequences only?
  boolean colourSelected = false;

  // The parent panel
  protected SeqPanel seqPanel;

  // Constructor
  public SeqCanvas(SeqPanel seqPanel) {
    this.seqPanel = seqPanel;
    this.da = (DrawableAlignment)seqPanel.align;
  }

  public void setFont(Font f) {
    this.f = f;
    if (gg != null) {
      FontMetrics fm = gg.getFontMetrics(f);
      if (seqPanel.fastDraw) {
        this.f = new Font("Courier",f.getStyle(),f.getSize());
        charWidth = fm.charWidth('W');
        charHeight = setCharHeight(fm,showScores);
      } else {
	charWidth = fm.charWidth('W') + 2;
	charHeight = setCharHeight(fm,showScores);
      }
    }
  }
    
  public void paint(Graphics g) {
    // Create the image at the beginning or after a resize
    // or if instructed to by paintFlag (change of font/colourscheme
    if (img == null || imgWidth != size().width || imgHeight != size().height || paintFlag == true) {
      // The size of the viewable region

      imgWidth = size().width;
      imgHeight = size().height;

      img = createImage(imgWidth,imgHeight);

      // The offscreen graphics
      gg = img.getGraphics();

      //Now the fonty bit
      gg.setFont(f);
      // Set the fonts in the other panels
      seqPanel.alignPanel.idPanel.idCanvas.f = f;
      seqPanel.alignPanel.scalePanel.scaleCanvas.f = f;

      // Find the size of the residue boxes
      FontMetrics fm = gg.getFontMetrics(f);
      if (seqPanel.fastDraw) {
      charWidth = fm.charWidth('W');
      } else {
      charWidth = fm.charWidth('W')+2;
      }
      charHeight = setCharHeight(fm,showScores);

      //Set the scrollbar values
      seqPanel.setScrollValues(seqPanel.offx,seqPanel.offy);

      // No more repainting
      paintFlag = false;

      // Initialize the scrollbar difference values
      oldstartx = -1;
      oldendx = -1;
      oldstarty = -1;
      oldendy = -1;

    }

    // What are the starting and ending residues/sequences in the alignment
    startx = seqPanel.offx;
    starty = seqPanel.offy;

    endx = getEndx();//startx + size().width/charWidth;
    endy = getEndy();//starty + size().height/charHeight;

    // Don't view greater than the size of the alignment
    if (endx > seqPanel.align.maxLength) { 
      startx = startx - endx + seqPanel.align.maxLength;
      endx =  seqPanel.align.maxLength;
    }

    //This is when the alignment width/heigth is less
    //than the canvas size
    if (starty < 0) {
      starty = 0;
    }
    if (startx < 0) {
      startx = 0;
    }

    //Timings are calculated for the redraw
    long tstart = System.currentTimeMillis();

    // Draw everything if just created the image
    if (oldendx == -1) {

      //Fill in the background
      fillBackground(gg,Color.white,0,0,imgWidth,imgHeight);

      // Draw the viewable parts of the sequences
      drawPanel(gg,startx,endx,starty,endy);

      // Set the scrollbar difference variables
      oldstartx = startx;
      oldendx = endx;
      oldstarty = starty;
      oldendy = endy;




    }  else if (oldstartx < startx) {
      // Scroll the image right
      int delx = (startx - oldstartx) * charWidth;
      int delx2 = (oldendx - startx) * charWidth;
      
      // Copy the common area to the left 
      //      gg.copyArea(delx,0,delx2,(endy-starty)*charHeight,-delx,0);
      // System.out.println("toleft " + (endy-starty)*charHeight + " " + da.getHeight(starty,endy));
      gg.copyArea(delx,0,delx2,da.getHeight(starty,endy),-delx,0);
      
      // Draw the remaining part that has come into view
      // on the right
      drawPanel(gg,oldendx,endx,starty,endy);

      oldstartx = startx;
      oldendx = endx;



      
    } else if (oldstartx > startx) {
      // Scroll the image left
      int delx = (oldstartx - startx) * charWidth;
      int delx2 = (endx - oldstartx) * charWidth;

      // Copy the common area to the right
      //      gg.copyArea(0,0,delx2,(endy-starty)*charHeight,delx,0);
      //System.out.println("toright " + (endy-starty)*charHeight + " " + da.getHeight(starty,endy));
      gg.copyArea(0,0,delx2,da.getHeight(starty,endy),delx,0);

      // Draw the remaining part
      drawPanel(gg,startx,oldstartx,starty,endy);

      oldstartx = startx;
      oldendx = endx;




    }  else if (oldstarty < starty) {
      // Scroll the image down
      //      int dely = (starty - oldstarty) * charHeight;
      //System.out.println("down   " + (starty-oldstarty)*charHeight + " " + da.getHeight(oldstarty,starty));
      int dely = da.getHeight(oldstarty,starty);
      //int dely2 = (oldendy - starty) * charHeight;
      //System.out.println("down   " + (oldendy-starty)*charHeight + " " + da.getHeight(starty,oldendy));
      int dely2 = da.getHeight(starty,oldendy);

      // Copy the common area up
      gg.copyArea(0,dely,(endx-startx)*charWidth,dely2,0,-dely);
      drawPanel(gg,startx,endx,oldendy,endy);
 
      oldstarty = starty;
      oldendy = endy;



    } else if (oldstarty > starty) {
    // Scroll the image up
      //      int dely = (oldendy - endy) * charHeight;
      // System.out.println("up     " + (oldendy-endy)*charHeight + " " + da.getHeight(endy,oldendy));
      int dely = da.getHeight(endy,oldendy);
      //      int dely2 = (endy - oldstarty) * charHeight;
      //System.out.println("up     " + (endy-oldstarty)*charHeight + " " + da.getHeight(oldstarty,endy));
      int dely2 = da.getHeight(oldstarty,endy);

      gg.copyArea(0,0,(endx-startx)*charWidth,dely2,0,dely);
      drawPanel(gg,startx,endx,starty,oldstarty);

      oldstarty = starty;
      oldendy = endy;
    }

    // Print out the time taken for a redraw
    long tend = System.currentTimeMillis();
    if (seqPanel.alignPanel.parent instanceof AlignFrame) {
      //      System.out.println("Frame rate = " + (float)1000/(float)(tend-tstart));
      ((AlignFrame)seqPanel.alignPanel.parent).redraw.setText("Redraw time = " + (tend-tstart) + " ms");
    }

    // Copy the offscreen image to the screen
    g.drawImage(img,0,0,this);    

  }


  // Draws a region of the alignment - needs startx etc
  public void drawPanel(Graphics g,int x1,int x2, int y1, int y2) {
    g.setFont(f);
    // Fill the background white
    // fillBackground(g,Color.white,(x1-startx)*charWidth,(y1-starty)*charHeight,(x2-x1)*charWidth,(y2-y1)*charHeight);


    fillBackground(g,Color.white,(x1-startx)*charWidth,da.getHeight(starty,y1),(x2-x1)*charWidth,da.getHeight(y1,y2));

    //System.out.println("Back " + (y1-starty)*charHeight + " " + da.getHeight(starty,y1));
    //System.out.println("Back " + (y2-y1)*charHeight + " " + da.getHeight(y1,y2));
    // Draw the sequence text if required
    for (int i = y1 ; i < y2 ;i++) {
   //   SequenceGroup sg = (SequenceGroup)seqPanel.align.findGroup(i);

   //   if (sg != null && sg.display) {
      //      	((DrawableSequence)(seqPanel.align.sequences[i])).drawSequence(g,x1,x2,(x1-startx)*charWidth,
      //								       (i-starty)*charHeight,charWidth,charHeight,showScores);
      //   System.out.println("Draw " + (i-starty)*charHeight + " " + da.getHeight(starty,i) + " " + charHeight + " " + da.ds[i].charHeight);
      	((DrawableSequence)(seqPanel.align.sequences[i])).drawSequence(g,x1,x2,(x1-startx)*charWidth,
								       da.getHeight(starty,i),charWidth,
								       da.ds[i].charHeight,showScores);
   //   }
    }
  }
  
  public boolean keyDown(Event evt, int key) {
    return seqPanel.alignPanel.idPanel.keyDown(evt,key);
  }

  public int getEndy() {
    int h=0;
    int i=starty;
    int tmp = starty;

    // Tot up to the last sequence
    while (h < size().height && i < da.ds.length  && da.ds[i] != null) {
      h += da.ds[i].charHeight;
      i++;
    }

    //    System.out.println("New " + starty + " " + (i-1) + " " + seqPanel.align.size());
    int j = i;
    int tmp2 = 0;
    
    // If we haven't reached the canvas size reduce starty if poss
    if (h < size().height ) {
      tmp = da.size;
      h = size().height;
      
      while (h > 0 && tmp > 0) {
	h -= da.ds[tmp-1].charHeight;
	tmp--;
      }
      if (h <= 0) {
	tmp++;
      }
    } else {
      i--;
    }

 
    //    System.out.println("end " + tmp + " " + i);

     endy = starty + size().height/charHeight;
     if (endy >= seqPanel.align.size) {
       starty = starty - endy  +  seqPanel.align.size;
       endy = seqPanel.align.size;
     }
     if (starty < 0) {
       starty = 0;
     }
     //     System.out.println("old " + starty + " " + endy);	     
     //     return endy;
     starty = tmp;
     if (starty < 0) {
       starty = 0;
     }
     return i;
  }
  public int getEndx() {
    return startx + size().width/charWidth;
  }

  public int setCharHeight(FontMetrics fm, boolean showScores) {
    int height = fm.getHeight();
    if (seqPanel.align instanceof DrawableAlignment) {
      DrawableAlignment da = (DrawableAlignment)seqPanel.align;
      int i=0;
      while (i < da.ds.length && da.ds[i] != null) {
	if (da.ds[i] instanceof ScoreSequence) {
	  da.ds[i].charHeight = height*3;
	} else {
	  if (showScores) {
	    da.ds[i].charHeight = height*2;
	  } else {
	    da.ds[i].charHeight = height;
	  }
	}
	i++;
      }
    }
    if (showScores) {
      return fm.getHeight() * 2;
    } else {
      return fm.getHeight();
    }
  }

  // Fill the background within certain coordinates
  public void fillBackground(Graphics g,Color c, int x1,int y1,int width,int height) {
    g.setColor(c);
    g.fillRect(x1,y1,width,height);
  }

  // Reset so no flicker
  public void update(Graphics g) {
    paint(g);
  }
    

  // Needed for initialization
  public Dimension minimumSize() {
    return new Dimension(100,100);
  }
  public Dimension preferredSize() {
    return minimumSize();
  }
  public void setFont(int style, int size) {
     this.fontStyle = style;
     this.fontSize = size;
     f  = new Font(fontName,fontStyle,fontSize);
     setFont(f);
  }
  
  public int getIndex(int y) {

    int y1 = 0;
    //    System.out.println("get " + y + " " + starty + " " + endy + " " + y1);

    for (int i = starty; i <= endy; i++) {
      if (i < da.ds.length && da.ds[i] != null) {
	int y2 = y1 + da.ds[i].charHeight;
	//      System.out.println("YY " + y + " " + y1 + " " + y2);
	if (y>=y1 && y <=y2) {
	  return i;
	}
	y1  = y2;
      } else {
	return -1;
      }
    }
    return -1;
  }
}






