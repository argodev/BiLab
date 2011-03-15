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

//import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.ISelectionListener;

import scigol.accessor;



@Sophistication(Sophistication.Developer)
public abstract class ViewerBase extends Viewer implements ISelectionListener
{
  // all subclasses are required to have this constructor (which we'll look for via reflection)
  /* public abstract ViewerBase ViewerBase(Composite parent); */
  
  @accessor
  @Summary("is this viewer in-line or does is use an external viewer component?")
  public boolean get_IsExternal() { return false; }
  
  
  public abstract void dispose();
  
  public abstract Point preferedSize();
  public abstract Point maximumSize();
  
  @accessor
  public abstract String get_title();

  @accessor
  public abstract String get_description();

  
  // provide some default implementations for convenience
  
  
  public void setSelection(ISelection selection, boolean reveal)
  {
  }
  
  
  public ISelection getSelection()
  {
    return StructuredSelection.EMPTY;
  }
}


