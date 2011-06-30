/* CanvasPanel.java
 *
 * created: Sat Jun 17 2000
 *
 * This file is part of Artemis
 *
 * Copyright(C) 2000  Genome Research Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or(at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/CanvasPanel.java,v 1.4 2004/12/14 11:38:11 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.Options;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  This is a JPanel that contains a JPanel containing a JComponent.  Both Panels
 *  have BorderLayout.  The JComponent is added at "Center".
 *
 *  @author Kim Rutherford <kmr@sanger.ac.uk>
 *  @version $Id: CanvasPanel.java,v 1.4 2004/12/14 11:38:11 tjc Exp $
 **/

abstract public class CanvasPanel extends JPanel 
{
  /** The height of the font used in this component. */
  private int font_ascent;

  /** maximum height of the font used in this component. */
  private int font_max_ascent;

  /** descent of the font used in this component. */
  private int font_descent;

  /** The(maximum) width of the font used in this component. */
  private int font_width;

  /** base line of the font used in this component. */
  private int font_base_line;


  /**
   *  Create a new JPanel(mid_panel) and a JComponent.
   **/
  public CanvasPanel() 
  {
    setLayout(new BorderLayout());
    setFontInfo();
  }

  /**
   *  Set font_width and font_ascent from the default font.
   **/
  private void setFontInfo() 
  {
    FontMetrics fm = getFontMetrics(getFont());

    // find the width of a wide character
    font_width = fm.charWidth('M');
    font_ascent = fm.getAscent();
    font_max_ascent = fm.getMaxAscent();
    font_descent = fm.getDescent();
  }

  /**
   *  Return the width of our font, as calculated by setFontInfo().
   **/
  public int getFontWidth() 
  {
    return font_width;
  }

  /**
   *  Return the ascent(height above the baseline) of our font, as calculated
   *  by setFontInfo().
   **/
  public int getFontAscent() 
  {
    return font_ascent;
  }

  /**
   *  Return the max ascent(height above the baseline) of our font, as
   *  calculated by setFontInfo().
   **/
  public int getFontMaxAscent() 
  {
    return font_ascent;
  }

  /**
   *  Return the descent of our font, as calculated by setFontInfo().
   **/
  public int getFontDescent() 
  {
    return font_descent;
  }

  /**
   *  The max ascent + descent of the default font.
   **/
  public int getFontHeight() 
  {
    return getFontMaxAscent() + getFontDescent();
  }

}
