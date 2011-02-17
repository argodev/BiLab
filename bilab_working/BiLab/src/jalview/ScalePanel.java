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



public class ScalePanel extends Panel {

  protected AlignmentPanel alignPanel;

  protected ScaleCanvas scaleCanvas;



  protected int offy;

  public int width;

  

  public ScalePanel(AlignmentPanel alignPanel) {

    this.alignPanel = alignPanel;

    componentInit();

    System.out.println("Loaded ScalePanel");

  }



  private void componentInit() {

    scaleCanvas = new ScaleCanvas(this);

    setLayout(new BorderLayout());

    add("Center",scaleCanvas);

  }



  public Dimension minimumSize() {

    return new Dimension(500,40);

  }

  public Dimension preferredSize() {

    return minimumSize();

  }

  

  public boolean mouseDown(Event evt, int x, int y) {

    int res = (x-alignPanel.idPanel.size().width)/alignPanel.seqPanel.seqCanvas.charWidth + alignPanel.seqPanel.seqCanvas.startx;

    boolean found = false;

    

    System.out.println("Selected column = " + res);

   

    Enumeration e = alignPanel.selectedColumns.elements();

    while (e.hasMoreElements()) {

      Integer i = (Integer)e.nextElement();

      if (res == i.intValue()) {

	alignPanel.selectedColumns.removeElement(i);

	found = true;

      }

    }

    if (found == false) {

      alignPanel.selectedColumns.addElement(new Integer(res));

    }

    scaleCanvas.paintFlag = true;

    scaleCanvas.repaint();

    return false;

  }

    

} 

    

    

