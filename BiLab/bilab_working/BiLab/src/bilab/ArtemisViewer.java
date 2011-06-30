/**
* This document is a part of the source code and related artifacts for BiLab,
* an open source interactive workbench for computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
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
//                base_display.setShowLabels(false);
//                base_display.setScaleFactor(0);
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
