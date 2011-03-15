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

import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.components.*;
import uk.ac.sanger.artemis.io.*;
import uk.ac.sanger.artemis.util.*;
import uk.ac.sanger.artemis.sequence.Bases;
import uk.ac.sanger.artemis.sequence.NoSequenceException;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.*;

import scigol.Debug;

public class ArtemisViewer extends ViewerBase
{
  public ArtemisViewer(Composite parent) 
  {
    fixed = new Composite(parent, SWT.BORDER);
    fixed.setLayout(new FillLayout());
    fixed.setSize(300,300);
    
    embed = new Composite(fixed, SWT.EMBEDDED);
    compositeFrame = SWT_AWT.new_Frame(embed);
    
    // create Artemis panel 
    artemisPanel = new ArtemisPanel();
    compositeFrame.add(artemisPanel);
    
    sequence = null;
  }
  
  protected ArtemisPanel artemisPanel;
  
  public static class ArtemisPanel extends JPanel
  {
    public ArtemisPanel()
    {
    }
  }
  
  public Point preferedSize()
  {
    return new Point(SWT.DEFAULT,182);
  }
  
  public Point maximumSize()
  {
    return new Point(SWT.MAX,SWT.MAX);
  }
  
  public String get_title()
  {
    if (sequence == null) return "molecule [JMol]";
    return sequence.get_name()+ " [JMol]";
  }

  public String get_description()
  {
    if (sequence == null) return "sequence: none";
    return "molecule: "+sequence.get_DetailText();
  }
  
  public final static String inlinePrefix = "sequence://";
 
  Composite embed, fixed;
  Frame compositeFrame;
  
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
    return sequence;
  }

  public void refresh()
  {
    fixed.redraw();
  }

  public void setInput(Object input)
  {
    if (input == null) 
      sequence = null;
    else {
      Debug.Assert(input instanceof molecule);
      sequence = (seq)input;
    }

    // create Artemis Entry from BioJava Sequence
    BioJavaEntry bjentry = null;
    if (sequence instanceof DNA)
      bjentry = new BioJavaEntry(((DNA)sequence).seq);
    else if (sequence instanceof RNA)
      bjentry = new BioJavaEntry(((RNA)sequence).seq);
    else if (sequence instanceof protein)
      bjentry = new BioJavaEntry(((protein)sequence).seq);
    else
      sequence = null;

    if (sequence != null) {

      try {
        uk.ac.sanger.artemis.Entry entry = new uk.ac.sanger.artemis.Entry(bjentry);
      
        // create an EntryEdit for the sequence
        final Bases bases = entry.getBases();
        final EntryGroup entry_group = new SimpleEntryGroup(bases);
        entry_group.add(entry);
        
        // try to create a FeatureDisplay only
        GotoEventSource goto_event_source = new SimpleGotoEventSource(entry_group);
        Selection selection = new Selection(null);
        
        entry_group.addFeatureChangeListener(selection);
        entry_group.addEntryChangeListener(selection);
        
        String name = entry_group.getDefaultEntry().getName();
        
        Box vbox_panel = Box.createVerticalBox();

        Box hbox_panel = Box.createHorizontalBox();
        hbox_panel.add(vbox_panel);
        
        Box topvbox_panel = Box.createVerticalBox();
        topvbox_panel.add(new JLabel(name+":"));
        topvbox_panel.add(hbox_panel);
        artemisPanel.setLayout(new BorderLayout());
        artemisPanel.add(topvbox_panel, "North");
        
        BasePlotGroup base_plot_group = new BasePlotGroup(entry_group, artemisPanel, selection, goto_event_source);
        vbox_panel.add(base_plot_group);
        base_plot_group.setVisible(true);
        
        FeatureDisplay base_display = new FeatureDisplay(entry_group, selection, goto_event_source, base_plot_group);
        base_display.setShowLabels(false);
        base_display.setScaleFactor(0);
        vbox_panel.add(base_display);
        base_display.setVisible(true);
        
        int feature_count = entry_group.getAllFeaturesCount();
        if (feature_count > 0) {
          FeatureList feature_list = new FeatureList(entry_group, selection, goto_event_source, base_plot_group);
          hbox_panel.add(feature_list);
          feature_list.setVisible(true);
        }
        
      } catch (OutOfRangeException e) {
        sequence=null;
      } catch (NoSequenceException e) {
        sequence=null;
      }
    }
    
    if (sequence == null) {
      Notify.devWarning(this,"ArtemisViewer.setInput() - seq is null");
      ; // set viewer to empty here!!!
    }
  }
  
  public void selectionChanged(IWorkbenchPart part, ISelection selection) 
  {
  }
  
  seq sequence;
}