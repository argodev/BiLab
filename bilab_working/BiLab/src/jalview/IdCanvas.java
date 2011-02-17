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



public class IdCanvas extends Canvas {
  Image img;
  Graphics gg;

  int imgWidth;
  int imgHeight;

  Font f;
  
  int charWidth;
  int charHeight;
  
  int idWidth = 120;

  protected IdPanel idPanel;

  boolean paintFlag = false;
  public boolean showScores = true; // -DJ made public

  DrawableAlignment da;

  int starty;
  int endy;

  public IdCanvas(IdPanel idPanel) {
    //System.out.println("here");
    this.idPanel = idPanel;
    //System.out.println("here");
  }
  public boolean keyDown(Event evt, int key) {
    requestFocus();
    return false;
  }
  public void paint(Graphics g) {
    if (img == null || imgWidth != size().width || imgHeight != size().height || paintFlag == true) {
      da = idPanel.alignPanel.seqPanel.align;
      imgWidth = size().width;
      idWidth = size().width;
      
      FontMetrics fm = g.getFontMetrics(idPanel.alignPanel.seqPanel.seqCanvas.f);

      idWidth = idPanel.alignPanel.seqPanel.align.maxIdLength(fm)+2;
      imgHeight = size().height;

      if (imgWidth < 0 ) {imgWidth = 700;}
      if (imgHeight < 0 ) {imgHeight = 500;}

      img = createImage(imgWidth,imgHeight);

      gg = img.getGraphics();
      gg.setColor(Color.white);
      gg.fillRect(0,0,imgWidth,imgHeight);

      //Now the fonty bit
      f = idPanel.alignPanel.seqPanel.seqCanvas.f;

      gg.setFont(f);
      fm = gg.getFontMetrics(f);
      charWidth = fm.charWidth('W');
      charHeight = setCharHeight(fm,showScores);

      paintFlag = false;

    }
    //Fill in the background
    gg.setColor(Color.white);
    gg.fillRect(0,0,imgWidth,imgHeight);
    gg.setColor(Color.black);

    //Which ids are we printing
    starty = idPanel.alignPanel.seqPanel.offy;
    endy = getEndy();//starty + idPanel.alignPanel.seqPanel.seqCanvas.size().height/charHeight;

    //    if (endy > idPanel.alignPanel.seqPanel.align.size) {
    //  starty = starty - endy  +  idPanel.alignPanel.seqPanel.align.size;
    //  endy = idPanel.alignPanel.seqPanel.align.size;
    // }
     if (starty < 0) {
      starty = 0;
    }
    
    //Now draw the id strings
    for (int i = starty; i < endy; i++) {


      if (idPanel.alignPanel.sel.contains(idPanel.alignPanel.seqPanel.align.ds[i])) {
	gg.setColor(Color.gray);
	//	gg.fillRect(0,(i-starty)*charHeight,size().width,charHeight);
	gg.fillRect(0,da.getHeight(starty,i),size().width,da.ds[i].charHeight);
	gg.setColor(Color.white);

      } else {
	gg.setColor(idPanel.alignPanel.seqPanel.align.ds[i].getColor());
	//	gg.fillRect(0,(i-starty)*charHeight,size().width,charHeight);
	gg.fillRect(0,da.getHeight(starty,i),size().width,da.ds[i].charHeight);
	gg.setColor(Color.black);
      }
    
      String string = idPanel.alignPanel.seqPanel.align.ds[i].getName() + "/" + 
	 idPanel.alignPanel.seqPanel.align.ds[i].start + "-" + 
	 idPanel.alignPanel.seqPanel.align.ds[i].end;
      if (showScores) {
	//	gg.drawString(string,0,(i+1-starty)*charHeight-charHeight/2-2);
	gg.drawString(string,0,da.getHeight(starty,i)+da.ds[i].charHeight/2-2);
      } else {
	//	gg.drawString(string,0,(i+1-starty)*charHeight-2);
	gg.drawString(string,0,da.getHeight(starty,i)+da.ds[i].charHeight-2);
      }
    }

    g.drawImage(img,0,0,this);    
  }
  public void update(Graphics g) {
    paint(g);
  }

  public int setCharHeight(FontMetrics fm, boolean showScores) {
   int height = fm.getHeight();
    if (idPanel.alignPanel.seqPanel.align instanceof DrawableAlignment) {
      da = (DrawableAlignment)idPanel.alignPanel.seqPanel.align;
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
  public int getEndy() {
     int h=0;
     int i=starty;
     int tmp = starty;

     //     System.out.println("Starty = " + starty);
 //      while (h < idPanel.alignPanel.seqPanel.seqCanvas.size().height && i<da.ds.length && da.ds[i] != null) {
//         h += da.ds[i].charHeight;
//         i++;
//        }
//       //System.out.println("size " + i + " " + idPanel.alignPanel.seqPanel.align.size);
//       if (i >= idPanel.alignPanel.seqPanel.align.size) {
// 	i = idPanel.alignPanel.seqPanel.align.size-1;
// 	h = idPanel.alignPanel.seqPanel.seqCanvas.size().height;
// 	while (h > 0  && i >= 0) { 
// 	  h -= da.ds[i].charHeight;
// 	  i--;
// 	}
// 	//	i += 2;
// 	//tmp = starty - i +  seqPanel.align.size;
// 	tmp = i-1;
// 	i = idPanel.alignPanel.seqPanel.align.size;   
// 	//starty = i -  idPanel.alignPanel.seqPanel.align.size;
//         //i = idPanel.alignPanel.seqPanel.align.size;
//       } else {
//         i--;
//       }
//       starty = tmp;
//       //     System.out.println("Start " + i + " " + starty+  " " + (starty + idPanel.alignPanel.seqPanel.seqCanvas.size().height/charHeight));
//      return i;
     //    return (starty + idPanel.alignPanel.seqPanel.seqCanvas.size().height/charHeight);
     int out = idPanel.alignPanel.seqPanel.seqCanvas.getEndy();
     starty = idPanel.alignPanel.seqPanel.seqCanvas.starty;
     return out;
  }
  public Dimension minimumSize() {
    return new Dimension(100,100);
  }
  public Dimension preferredSize() {
    return minimumSize();
  }

  public int maxIdLength() {
    DrawableAlignment al = idPanel.alignPanel.seqPanel.align;
    int max = 0;
    int i = 0;
    while (i < al.ds.length && al.ds[i] != null) {
      if (al.ds[i].getName().length() > max) { 
	max = al.ds[i].getName().length();
      }
      i++;
    }
    return max;
  }
}















