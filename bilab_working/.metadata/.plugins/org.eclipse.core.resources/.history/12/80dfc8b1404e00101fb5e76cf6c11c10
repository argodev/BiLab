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

import org.eclipse.swt.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.awt.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.IWorkbenchPart;

import org.openscience.jmol.viewer.JmolViewer;

import java.awt.Frame;

import scigol.Debug;

public class JMolViewer extends ViewerBase
{
  public JMolViewer(Composite parent) 
  {
    fixed = new Composite(parent, SWT.BORDER);
    fixed.setLayout(new FillLayout());
    fixed.setSize(300,300);
    
    embed = new Composite(fixed, SWT.EMBEDDED);
    compositeFrame = SWT_AWT.new_Frame(embed);
    
    jmolPanel = new JmolPanel();
    compositeFrame.add(jmolPanel);
    
    mol = null;
  }
  
  public Point preferedSize()
  {
    return new Point(200,200);
  }
  
  public Point maximumSize()
  {
    return new Point(SWT.MAX,SWT.MAX);
  }
  
  public String get_title()
  {
    if (mol == null) return "molecule [JMol]";
    return mol.get_name()+ " [JMol]";
  }

  public String get_description()
  {
    if (mol == null) return "molecule: none";
    return "molecule: "+mol.get_DetailText();
  }
  
  public final static String inlinePrefix = "molecule://";
 
  Composite embed, fixed;
  Frame compositeFrame;
  JmolPanel jmolPanel;
  
  public void dispose()
  {
    // TODO Auto-generated method stub
  }

  public Control getControl()
  {
    return fixed;
  }

  public Object getInput()
  {
    return mol;
  }

  public void refresh()
  {
    //setInput(mol);
    fixed.redraw();
  }

  public void setInput(Object input)
  {
    if (input == null) 
      mol = null;
    else {
      Debug.Assert(input instanceof molecule);
      mol = (molecule)input;
    }

    JmolViewer jmolViewer = jmolPanel.getViewer();
    jmolViewer.setJmolDefaults();
    //jmolViewer.setRasmolDefaults();

    if (mol != null) {
      String res = mol.get_AssociatedResource();
    
      if (res != null) { // we have the resource, just use that
        if (res.startsWith(inlinePrefix)) { // inline
          res = res.substring(inlinePrefix.length());
          jmolViewer.openStringInline(res);
        }
        else { // URL
          if (res.startsWith("file://")) 
            res = res.substring(7);

          jmolViewer.openFile(res); // URL actually
          String strError = jmolViewer.getOpenFileError(); // Warning: JMol doesn't seem to display the molecule until this is called (!)
          if (strError != null) 
            Notify.userWarning(this,"JMol error reading molecule resource '"+res+"' - "+strError);
          
          jmolViewer.evalString("ribbons ON; color ribbons structure");
          //jmolViewer.evalString("spacefill off; rockets ON; color rockets structure");
          
          jmolViewer.scaleFitToScreen();
          jmolViewer.setZoomEnabled(true);
        }
      }
      else {
        // get the structure from the molecule
        Debug.Unimplemented();
      }
      jmolViewer.scaleFitToScreen();

    }
    else {
      Notify.devWarning(this,"JMolViewer.setInput() - molecule model is null");
      ; // set viewer to empty here!!!
    }
  }
  
  public void selectionChanged(IWorkbenchPart part, ISelection selection) 
  {
    Debug.WL("JMolViewer.selectionChanged() unimplemented");
  }
  
  molecule mol;
}