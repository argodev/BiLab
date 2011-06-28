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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

// a view for a scigol.Value
public class ValueView extends ViewPart implements ISelectionListener {

    // !!! hack for EnvNavigatorView to set our input (see explanation there)
    public static ValueView getInstance() {
        return instance;
    }

    private ValueViewer valueViewer;

    private static ValueView instance = null;

    public ValueView() {
        // !!! tmp hack (see below)
        instance = this;
    }

    @Override
    public void createPartControl(final Composite parent) {
        valueViewer = new ValueViewer(parent);
        //setInput(new scigol.Value(Util.readResource("molecules/1GP2.pdb", ""))); // !!!
        setInput(new scigol.Value(Util.readResource("molecules/dna.pdb", ""))); // !!!

        // the ValueView may want to provide selection events & also listen for
        // them
        // So, we need to adapt between the ISelectionChangeListenr interface of
        // jface & the platform ui ISelectionListener method

        // register our listener to listen to the value viewer and pass on the
        // events as an ISelection

        getSite().setSelectionProvider(valueViewer);
        getSite().getPage().addSelectionListener(this);

    }

    @Override
    public void dispose() {
        getSite().getPage().removeSelectionListener(valueViewer);
        super.dispose();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart source,
            final ISelection selection) {
        if (selection instanceof ValueSelection) {
            setInput(((ValueSelection) selection).value);
        }

        valueViewer.selectionChanged(source, selection); // !!! why was this
                                                         // commented out??
    }

    @Override
    public void setFocus() {
        valueViewer.getControl().setFocus();
    }

    public void setInput(final scigol.Value v) {
        valueViewer.setInput(v);
        final String title = valueViewer.get_title();
        if (title.length() > 0) {
            setPartName(title);
            setContentDescription(valueViewer.get_description());
        } else {
            setPartName("Value");
            setContentDescription("");
        }
    }

}
