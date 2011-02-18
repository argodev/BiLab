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

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.browser.*;

import java.net.URL;

// View HTML content
public class HTMLViewer extends ViewerBase
{
  public HTMLViewer(Composite parent)
  {
    top = new Composite(parent, SWT.NONE);
    top.setLayout(new FillLayout());
    
    browser = new Browser(top, SWT.BORDER);
    browser.setText("<html><title>Welcome to Bilab</title><body><h1>Welcome to Bilab.</h1></body></html>");
    input = "";
  }
  
  Composite top;
  Browser browser;
  
  String input; // url
  
  public void dispose()
  {
    // TODO Auto-generated method stub
  }

  public Point preferedSize()
  {
    return new Point(SWT.DEFAULT,400);
  }

  public Point maximumSize()
  {
    return new Point(SWT.MAX, SWT.MAX);
  }

  public String get_title()
  {
    return "Web";
  }

  public String get_description()
  {
    return "Web Browser";
  }

  public Control getControl()
  {
    return top;
  }

  public Object getInput()
  {
    return input;
  }

  public void refresh()
  {
//    browser.refresh();
  }

  public void setInput(Object input)
  {
    if (input instanceof String)
      this.input = (String)input;
    else if (input instanceof URL)
      this.input = ((URL)input).toString();
    else
      this.input = "";
    browser.setUrl(this.input);
    Notify.devInfo(this,"HTMLViewer set URL "+this.input);
  }

  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    // TODO Auto-generated method stub
  }
}