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

import java.awt.Frame;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.openscience.jmol.viewer.JmolViewer;

import scigol.Debug;

public class JMolViewer extends ViewerBase {
    public final static String inlinePrefix = "molecule://";

    Composite embed, fixed;

    Frame compositeFrame;

    JmolPanel jmolPanel;

    molecule mol;

    public JMolViewer(final Composite parent) {
        fixed = new Composite(parent, SWT.BORDER);
        fixed.setLayout(new FillLayout());
        fixed.setSize(300, 300);

        embed = new Composite(fixed, SWT.EMBEDDED);
        compositeFrame = SWT_AWT.new_Frame(embed);

        jmolPanel = new JmolPanel();
        compositeFrame.add(jmolPanel);

        mol = null;
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public String get_description() {
        if (mol == null) {
            return "molecule: none";
        }
        return "molecule: " + mol.get_DetailText();
    }

    @Override
    public String get_title() {
        if (mol == null) {
            return "molecule [JMol]";
        }
        return mol.get_name() + " [JMol]";
    }

    @Override
    public Control getControl() {
        return fixed;
    }

    @Override
    public Object getInput() {
        return mol;
    }

    @Override
    public Point maximumSize() {
        return new Point(SWT.MAX, SWT.MAX);
    }

    @Override
    public Point preferedSize() {
        return new Point(200, 200);
    }

    @Override
    public void refresh() {
        // setInput(mol);
        fixed.redraw();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {
        Debug.WL("JMolViewer.selectionChanged() unimplemented");
    }

    @Override
    public void setInput(final Object input) {
        if (input == null) {
            mol = null;
        } else {
            Debug.Assert(input instanceof molecule);
            mol = (molecule) input;
        }

        final JmolViewer jmolViewer = jmolPanel.getViewer();
        jmolViewer.setJmolDefaults();
        // jmolViewer.setRasmolDefaults();

        if (mol != null) {
            String res = mol.get_AssociatedResource();

            if (res != null) { // we have the resource, just use that
                if (res.startsWith(inlinePrefix)) { // inline
                    res = res.substring(inlinePrefix.length());
                    jmolViewer.openStringInline(res);
                } else { // URL
                    if (res.startsWith("file://")) {
                        res = res.substring(7);
                    }

                    jmolViewer.openFile(res); // URL actually
                    final String strError = jmolViewer.getOpenFileError(); // Warning:
                                                                           // JMol
                                                                           // doesn't
                                                                           // seem
                                                                           // to
                                                                           // display
                                                                           // the
                                                                           // molecule
                                                                           // until
                                                                           // this
                                                                           // is
                                                                           // called
                                                                           // (!)
                    if (strError != null) {
                        Notify.userWarning(this,
                                "JMol error reading molecule resource '" + res
                                        + "' - " + strError);
                    }

                    jmolViewer
                            .evalString("ribbons ON; color ribbons structure");
                    // jmolViewer.evalString("spacefill off; rockets ON; color rockets structure");

                    jmolViewer.scaleFitToScreen();
                    jmolViewer.setZoomEnabled(true);
                }
            } else {
                // get the structure from the molecule
                Debug.Unimplemented();
            }
            jmolViewer.scaleFitToScreen();

        } else {
            Notify.devWarning(this,
                    "JMolViewer.setInput() - molecule model is null");
            ; // set viewer to empty here!!!
        }
    }
}