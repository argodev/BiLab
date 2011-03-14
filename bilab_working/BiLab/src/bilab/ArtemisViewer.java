/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
 */
package bilab;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;

import scigol.Debug;
import uk.ac.sanger.artemis.EntryGroup;
import uk.ac.sanger.artemis.GotoEventSource;
import uk.ac.sanger.artemis.Selection;
import uk.ac.sanger.artemis.SimpleEntryGroup;
import uk.ac.sanger.artemis.SimpleGotoEventSource;
import uk.ac.sanger.artemis.components.BasePlotGroup;
import uk.ac.sanger.artemis.components.FeatureDisplay;
import uk.ac.sanger.artemis.components.FeatureList;
import uk.ac.sanger.artemis.io.BioJavaEntry;
import uk.ac.sanger.artemis.sequence.Bases;
import uk.ac.sanger.artemis.sequence.NoSequenceException;
import uk.ac.sanger.artemis.util.OutOfRangeException;

public class ArtemisViewer extends ViewerBase {
    public static class ArtemisPanel extends JPanel {
        public ArtemisPanel() {
        }
    }

    protected ArtemisPanel artemisPanel;

    public final static String inlinePrefix = "sequence://";

    Composite embed, fixed;

    Frame compositeFrame;

    seq sequence;

    public ArtemisViewer(final Composite parent) {
        fixed = new Composite(parent, SWT.BORDER);
        fixed.setLayout(new FillLayout());
        fixed.setSize(300, 300);

        embed = new Composite(fixed, SWT.EMBEDDED);
        compositeFrame = SWT_AWT.new_Frame(embed);

        // create Artemis panel
        artemisPanel = new ArtemisPanel();
        compositeFrame.add(artemisPanel);

        sequence = null;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public String get_description() {
        if (sequence == null) {
            return "sequence: none";
        }
        return "molecule: " + sequence.get_DetailText();
    }

    @Override
    public String get_title() {
        if (sequence == null) {
            return "molecule [JMol]";
        }
        return sequence.get_name() + " [JMol]";
    }

    @Override
    public Control getControl() {
        return fixed;
    }

    @Override
    public Object getInput() {
        return sequence;
    }

    @Override
    public Point maximumSize() {
        return new Point(SWT.MAX, SWT.MAX);
    }

    @Override
    public Point preferedSize() {
        return new Point(SWT.DEFAULT, 182);
    }

    @Override
    public void refresh() {
        fixed.redraw();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {
    }

    @Override
    public void setInput(final Object input) {
        if (input == null) {
            sequence = null;
        } else {
            Debug.Assert(input instanceof molecule);
            sequence = (seq) input;
        }

        // create Artemis Entry from BioJava Sequence
        BioJavaEntry bjentry = null;
        if (sequence instanceof DNA) {
            bjentry = new BioJavaEntry(((DNA) sequence).seq);
        } else if (sequence instanceof RNA) {
            bjentry = new BioJavaEntry(((RNA) sequence).seq);
        } else if (sequence instanceof protein) {
            bjentry = new BioJavaEntry(((protein) sequence).seq);
        } else {
            sequence = null;
        }

        if (sequence != null) {

            try {
                final uk.ac.sanger.artemis.Entry entry = new uk.ac.sanger.artemis.Entry(
                        bjentry);

                // create an EntryEdit for the sequence
                final Bases bases = entry.getBases();
                final EntryGroup entry_group = new SimpleEntryGroup(bases);
                entry_group.add(entry);

                // try to create a FeatureDisplay only
                final GotoEventSource goto_event_source = new SimpleGotoEventSource(
                        entry_group);
                final Selection selection = new Selection(null);

                entry_group.addFeatureChangeListener(selection);
                entry_group.addEntryChangeListener(selection);

                final String name = entry_group.getDefaultEntry().getName();

                final Box vbox_panel = Box.createVerticalBox();

                final Box hbox_panel = Box.createHorizontalBox();
                hbox_panel.add(vbox_panel);

                final Box topvbox_panel = Box.createVerticalBox();
                topvbox_panel.add(new JLabel(name + ":"));
                topvbox_panel.add(hbox_panel);
                artemisPanel.setLayout(new BorderLayout());
                artemisPanel.add(topvbox_panel, "North");

                final BasePlotGroup base_plot_group = new BasePlotGroup(
                        entry_group, artemisPanel, selection, goto_event_source);
                vbox_panel.add(base_plot_group);
                base_plot_group.setVisible(true);

                final FeatureDisplay base_display = new FeatureDisplay(
                        entry_group, selection, goto_event_source,
                        base_plot_group);
                base_display.setShowLabels(false);
                base_display.setScaleFactor(0);
                vbox_panel.add(base_display);
                base_display.setVisible(true);

                final int feature_count = entry_group.getAllFeaturesCount();
                if (feature_count > 0) {
                    final FeatureList feature_list = new FeatureList(
                            entry_group, selection, goto_event_source,
                            base_plot_group);
                    hbox_panel.add(feature_list);
                    feature_list.setVisible(true);
                }

            } catch (final OutOfRangeException e) {
                sequence = null;
            } catch (final NoSequenceException e) {
                sequence = null;
            }
        }

        if (sequence == null) {
            Notify.devWarning(this, "ArtemisViewer.setInput() - seq is null");
            ; // set viewer to empty here!!!
        }
    }
}