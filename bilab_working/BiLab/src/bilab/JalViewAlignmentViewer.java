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

import jalview.AlignmentPanel;
import jalview.ClustalxColourScheme;
import jalview.DrawableSequence;
import jalview.ScorePanel;
import jalview.ScoreSequence;
import jalview.SequenceGroup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;

// alignment viewer (using JalView)
public class JalViewAlignmentViewer extends ViewerBase {
    Composite fixed, top;

    Container child;
    Frame compositeFrame;
    Label label;
    AlignmentPanel ap;
    ScorePanel sp;
    bilab.alignment input;

    public JalViewAlignmentViewer(final Composite parent) {
        fixed = new Composite(parent, SWT.NONE);
        fixed.setLayout(new FillLayout());
        fixed.setSize(300, 300);

        top = new Composite(fixed, SWT.EMBEDDED);
        compositeFrame = SWT_AWT.new_Frame(top);

        child = new Panel();
        compositeFrame.add(child);

        label = new Label("<no alignment>");
        child.add(label);

        ap = null;
        input = null;
        refresh();
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public String get_description() {
        return get_title();
    }

    @Override
    public String get_title() {
        return "alignment";
    }

    @Override
    public Control getControl() {
        return top;
    }

    @Override
    public Object getInput() {
        return input;
    }

    @Override
    public Point maximumSize() {
        return new Point(SWT.MAX, 200);
    }

    @Override
    public Point preferedSize() {
        return new Point(700, 200);
    }

    @Override
    public void refresh() {
        compositeFrame.invalidate();
        child.invalidate();
        compositeFrame.setVisible(true);
        compositeFrame.repaint();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {
    }

    @Override
    public void setInput(final Object input) {
        if (this.input == input) {
            return; // nothing changed
        }

        if ((input != null) && (input instanceof alignment)) {
            this.input = (alignment) input;
        }

        // first, remove the current viewer, if any
        child.removeAll();

        // make a new one
        if (this.input != null) {

            // !!! this stuff is flakey

            final DrawableSequence[] ds = new DrawableSequence[this.input.alignedSeqs.length];
            for (int i = 0; i < this.input.alignedSeqs.length; i++) {
                ds[i] = new DrawableSequence(this.input.alignedSeqs[i]);
            }

            ap = new AlignmentPanel(child, ds);

            // if (this.input.seqScores.length == 0) {
            ap.seqPanel.align.percentIdentity2();
            ap.seqPanel.align.findQuality();
            this.input.seqScores = new ScoreSequence[1];

            //
            // REG: 110310 - testing
            //
            // this.input.seqScores[0] = ap.seqPanel.align.qualityScore;

            // scigol.Debug.WL("* setting quality");
            // }

            // ap.setSequenceColor();
            // ap.setFont(new Font("Courier New", Font.PLAIN ,12));
            // ap.setSequenceColor(9999999, ColourProperties.CLUSTALX);
            // ap.seqPanel.seqCanvas. seqPanel.seqCanvas.boxFlag = true;
            // scigol.Debug.WL("* setting clustalx colors");

            ap.setSequenceColor(new ClustalxColourScheme(
                    ap.seqPanel.align.cons2, ap.seqPanel.align.size()));

            //
            // REG: 110310 - testing
            //
            // ap.color = ResidueProperties.color;

            ap.groupEdit = false;

            // scigol.Debug.WL("* setting boxes on");
            for (int i = 0; i < ap.seqPanel.align.groups.size(); i++) {
                final SequenceGroup sg = (SequenceGroup) ap.seqPanel.align.groups
                        .elementAt(i);
                // REG: 110310 - testing
                // sg.displayBoxes = true;
                ap.seqPanel.align.displayBoxes(sg);
            }

            final ScorePanel sp = new ScorePanel(child, this.input.seqScores);

            //
            // REG: 110310 - testing
            //
            // ap.seqPanel.seqCanvas.showScores = true;

            //
            // REG: 110310 - testing
            //
            // ap.idPanel.idCanvas.showScores = true;

            // scigol.Debug.WL("* setting font");
            ap.seqPanel.seqCanvas.setFont(new Font("Courier", Font.PLAIN, 12));
            sp.seqPanel.seqCanvas.setFont(new Font("Courier", Font.PLAIN, 12));

            // !!!

            child.setLayout(new BorderLayout());
            child.add("Center", ap);
            child.add("South", sp);
            compositeFrame.layout();

            refresh();
        } else {
            label = new Label("<no alignment>");
            child.add(label);
            ap = null;
            sp = null;
        }

    }

}
