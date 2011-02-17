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



public class ScaleCanvas extends Canvas {

  Image img;

  Graphics gg;



  int imgWidth;

  int imgHeight;



  Font f;

  

  int charWidth;

  int charHeight;

  

  protected ScalePanel scalePanel;

  

  boolean paintFlag = false;



  public ScaleCanvas(ScalePanel scalePanel) {

    this.scalePanel = scalePanel;

  }



  public void paint(Graphics g) {

    if (img == null || imgWidth != size().width || imgHeight != size().height || paintFlag == true) {

      imgWidth = size().width;

      imgHeight = size().height;

      img = createImage(imgWidth,imgHeight);

      

      gg = img.getGraphics();

      gg.setColor(Color.white);

      gg.fillRect(0,0,imgWidth,imgHeight);

      

      //Now the fonty bit

      f = scalePanel.alignPanel.seqPanel.seqCanvas.f;

      gg.setFont(f);

      FontMetrics fm = gg.getFontMetrics(f);

      if (scalePanel.alignPanel.seqPanel.fastDraw) {

	this.f = new Font("Courier",f.getStyle(),f.getSize());

        charWidth = fm.charWidth('W');

        charHeight = fm.getHeight();

      } else {

	charWidth = fm.charWidth('W') + 2;

	charHeight = fm.getHeight();

      }

      paintFlag = false;

    }

    //Fill in the background

    gg.setColor(Color.white);

    gg.fillRect(0,0,imgWidth,imgHeight);



   

    //Set the text font

    gg.setColor(Color.black);



    int startx = scalePanel.alignPanel.seqPanel.offx;

    int endx = startx + scalePanel.alignPanel.seqPanel.seqCanvas.size().width/charWidth;



    if (endx > scalePanel.alignPanel.seqPanel.align.maxLength) { 

      startx = startx - endx + scalePanel.alignPanel.seqPanel.align.maxLength;

      endx =  scalePanel.alignPanel.seqPanel.align.maxLength;

    }



    if (startx < 0) {

      startx = 0;

    }

    

    int scalestartx = startx - startx%10 + 10;



    //Fill the selected columns

    Enumeration e = scalePanel.alignPanel.selectedColumns.elements();

    while (e.hasMoreElements()) {

      int sel  = ((Integer)e.nextElement()).intValue();

      //      System.out.println("Selection = " + sel);

      if ( sel >= startx  && sel <= endx) {

	gg.setColor(Color.red);

	gg.fillRect(scalePanel.alignPanel.idPanel.idCanvas.idWidth +(sel-startx)*charWidth,17-charHeight,charWidth,charHeight);

      }

    }



    // Draw the scale numbers

    gg.setColor(Color.black);

    for (int i=scalestartx;i < endx;i+= 10) {

      String string = String.valueOf(i);

      gg.drawString(string,scalePanel.alignPanel.idPanel.idCanvas.idWidth +(i-startx-1)*charWidth,15);

    }





    g.drawImage(img,0,0,this);    

  }

  public void update(Graphics g) {

    paint(g);

  }

    

  public Dimension minimumSize() {

    return new Dimension(500,40);

  }

  public Dimension preferredSize() {

    return minimumSize();

  }

}

