/**
* This document is a part of the source code and related artifacts for BiLab, an open source interactive workbench for 
* computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
* Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
* details.
*
* You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to 
* the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* The license is also available at: http://www.gnu.org/copyleft/lgpl.html
*/

package bilab;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

// simple viewer for sequence strings (breaks into columns) 
public class SeqStringViewer extends ViewerBase
{
  public SeqStringViewer(Composite parent) 
  {
    this.parent = parent;
    Display display = Display.getCurrent();
    input = null;
    label = new Label(parent, SWT.NONE);
    label.setText("");
    label.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    label.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    
    FontData fontData = new FontData("Courier New",10,SWT.BOLD);
    labelFont = new Font(display, fontData);
    label.setFont(labelFont);
    
    fontPixelWidth = 10; // hard code for now!!! (need to get from FontMetrics somehow)
  }

  seq input;
  Label label;
  Font labelFont;
  int fontPixelWidth;
  Composite parent;
  
  
  

  public Point preferedSize()
  {
    return label.computeSize( (60+6)*fontPixelWidth, SWT.DEFAULT);
  }

  public Point maximumSize()
  {
    return new Point((60+6)*fontPixelWidth, SWT.MAX);
  }


  public String get_title()
  {
    if (input != null)
      return input.get_name();
    else
      return "";
  }

  public String get_description()
  {
    if (input != null)
      return input.get_ShortText();
    else
      return "";
  }

  
  public void dispose()
  {
  }
  
  
  public Control getControl()
  {
    return label;
  }

  public Object getInput()
  {
    return input;
  }

  public void refresh()
  {
    if (input!=null)
      label.setText(input.get_sequence());
    else 
      label.setText("");
  }

  public void setInput(Object input)
  {
    if (input == null) {
      this.input = null;
    }
    else {
      if (input instanceof seq)
        this.input = (seq)input;
      else
        this.input = null;
    }
    refresh();
  }

  
  
  
  
  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
  }

}
