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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;


// A composite with a fixed size
public class SizedComposite extends Composite
{
  public SizedComposite(Composite parent, int style)
  {
    super(parent, style);
    setLayout(new FillLayout());
    
    minWidth = maxWidth = prefWidth = 200;
    minHeight = maxHeight = prefHeight = 200;
  }

  
  
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    return computeSize(wHint, hHint);
  }
  
  
  public Point computeSize(int wHint, int hHint)
  {
    int width = prefWidth;
    int height = prefHeight;
    
    if (wHint != SWT.DEFAULT) {
      width = wHint;
      if (width < minWidth) width = minWidth;
      if (width > maxWidth) width = maxWidth;
    }
    
    if (hHint != SWT.DEFAULT) {
      height = hHint;
      if (height < minHeight) height = minHeight;
      if (height > maxHeight) height = maxHeight;
    }
    
    return new Point(width, height);
  }
  

  public void setMinimumSize(int width, int height)
  {
    minWidth = width;
    if (minWidth > maxWidth) maxWidth = minWidth;
    if (minWidth > prefWidth) prefWidth = minWidth;
    
    minHeight = height;
    if (minHeight > maxHeight) maxHeight = minHeight;
    if (minHeight > prefHeight) prefHeight = minHeight;
  }

  public void setMinimumSize(Point p) { setMinimumSize(p.x,p.y); }
  
  
  
  public void setMaximumSize(int width, int height)
  {
    maxWidth = width;
    if (maxWidth < prefWidth) prefWidth = maxWidth;
    if (maxWidth < minWidth) minWidth = maxWidth;
    
    maxHeight = height;
    if (maxHeight < prefHeight) prefHeight = maxHeight;
    if (maxHeight < minHeight) minHeight = maxHeight;
  }

  public void setMaximumSize(Point p) { setMaximumSize(p.x,p.y); }
  
  
  public void setPreferedSize(int width, int height)
  {    
    prefWidth = width;
    if (prefWidth > maxWidth) maxWidth = prefWidth;
    if (prefWidth < minWidth) minWidth = prefWidth;
    
    prefHeight = height;
    if (prefHeight > maxHeight) maxHeight = prefHeight;
    if (prefHeight < minHeight) minHeight = prefHeight;
  }

  public void setPreferedSize(Point p) { setPreferedSize(p.x,p.y); }
  
  

  int minWidth, minHeight;
  int maxWidth, maxHeight;
  int prefWidth, prefHeight;
}
