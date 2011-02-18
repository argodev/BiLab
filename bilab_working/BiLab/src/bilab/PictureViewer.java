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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

// simple picture viewer  
public class PictureViewer extends ViewerBase
{
  public PictureViewer(Composite parent) 
  {
    Display display = Display.getCurrent();
    input = null; // !!! set a default 'no image' icon of something here
    
    pictureComposite = new SizedComposite(parent, SWT.NO_BACKGROUND);
    pictureComposite.setPreferedSize(10,10); // !! size of default icon
    pictureComposite.setMaximumSize(10,10);
    pictureComposite.setMinimumSize(10,10);
    pictureComposite.setSize(10,10);
    
    pictureComposite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    
    paintListener = new PaintListener();
    paintListener.input = input;
    
    pictureComposite.addListener (SWT.Paint, paintListener);
  }

  picture input;
  SizedComposite pictureComposite;
  PaintListener paintListener;
  
  public class PaintListener implements Listener
  {
    public PaintListener() 
    {
      input = null;
    }
    
    public void handleEvent (Event e) {
      
      if (input == null) return; // paint default 'no image'?
      
      if (this.input.get_PictureType() == picture.PictureType.Image) {
        Image image = input.get_Image();
        GC gc = e.gc;
        gc.drawImage (image, 0, 0);
        Rectangle rect = image.getBounds ();
        Rectangle client = pictureComposite.getClientArea();
        int marginWidth = client.width - rect.width;
        if (marginWidth > 0) {
          gc.fillRectangle (rect.width, 0, marginWidth, client.height);
        }
        int marginHeight = client.height - rect.height;
        if (marginHeight > 0) {
          gc.fillRectangle (0, rect.height, client.width, marginHeight);
        }
      }
      else {
        Notify.unimplemented(this);
        GC gc = e.gc;
        gc.fillRectangle(0, 0, 10, 10);
      }
    }
    
    public picture input;
  }

  public Point preferedSize()
  {
    return pictureComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
  }

  public Point maximumSize()
  {
    return pictureComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    //return new Point(SWT.MAX, SWT.MAX); // !!! later perhaps we can scale images
  }

  public String get_title()
  {
    return (input!=null)?input.get_name():"<blank picture>";
  }

  public String get_description()
  {
    return get_title();
  }

  public void dispose()
  {
  }
  
  public Control getControl()
  {
    return pictureComposite;
  }

  public Object getInput()
  {
    return input;
  }

  public void refresh()
  {
    pictureComposite.redraw();
  }

  public void setInput(Object input)
  {
    if (input == null) {
      this.input = null;
    }
    else {
      if (input instanceof picture) {
        this.input = (picture)input;
        if (this.input.get_PictureType() == picture.PictureType.Image) {
          Rectangle rect = this.input.get_Image().getBounds();
          pictureComposite.setPreferedSize(rect.width, rect.height);
          pictureComposite.setMaximumSize(rect.width, rect.height);
          pictureComposite.setMinimumSize(10,10);
          pictureComposite.setSize(rect.width, rect.height);
        }
          else
            Notify.unimplemented(this);
      }
      else
        this.input = null;
    }
    paintListener.input = this.input;
    refresh();
  }

  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
  }
}