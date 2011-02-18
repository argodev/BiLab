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


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.openscience.jmol.viewer.JmolViewer;


// a view for a scigol.Value
public class ValueView extends ViewPart implements ISelectionListener 
{

  public ValueView() {
    //!!! tmp hack (see below)
    instance = this;
	}

	public void createPartControl(Composite parent) {
	  valueViewer = new ValueViewer(parent);
	  setInput(new scigol.Value(Util.readResource("molecules/1GP2.pdb",""))); //!!!
	  
	  
	  // the ValueView may want to provide selection events & also listen for them
	  //  So, we need to adapt between the ISelectionChangeListenr interface of jface & the platform ui ISelectionListener method
	  
	  // register our listener to listen to the value viewer and pass on the events as an ISelection
	  
	  
      getSite().setSelectionProvider(valueViewer);
      getSite().getPage().addSelectionListener(this);
      
	}

	
	public void setInput(scigol.Value v)
	{
	  valueViewer.setInput(v);
	  String title = valueViewer.get_title();
	  if (title.length()>0) {
	    setPartName(title);
	    setContentDescription(valueViewer.get_description());
	  }
	  else {
	    setPartName("Value");
	    setContentDescription("");
	  }
	}

	
	public void selectionChanged(IWorkbenchPart source, ISelection selection)
	{
	  if (selection instanceof ValueSelection) {
	    setInput( ((ValueSelection)selection).value );
	  }
	  
    valueViewer.selectionChanged(source, selection); //!!! why was this commented out?? 
	}
	
	
	public void dispose()
	{
	  getSite().getPage().removeSelectionListener(valueViewer);
	  super.dispose();
	}
	
	
	public void setFocus() {
	  valueViewer.getControl().setFocus();
	}
	
	
	private ValueViewer valueViewer;
  
  //!!! hack for EnvNavigatorView to set our input (see explanation there)
  public static ValueView getInstance() { return instance; }
  private static ValueView instance=null;
	  
}