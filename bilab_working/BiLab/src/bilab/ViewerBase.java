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

// import org.eclipse.swt.widgets.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISelectionListener;

import scigol.accessor;

@Sophistication(Sophistication.Developer)
public abstract class ViewerBase extends Viewer implements ISelectionListener {
    // all subclasses are required to have this constructor (which we'll look
    // for via reflection)
    /* public abstract ViewerBase ViewerBase(Composite parent); */

    public abstract void dispose();

    @accessor
    public abstract String get_description();

    @accessor
    @Summary("is this viewer in-line or does is use an external viewer component?")
    public boolean get_IsExternal() {
        return false;
    }

    @accessor
    public abstract String get_title();

    @Override
    public ISelection getSelection() {
        return StructuredSelection.EMPTY;
    }

    public abstract Point maximumSize();

    // provide some default implementations for convenience

    public abstract Point preferedSize();

    @Override
    public void setSelection(final ISelection selection, final boolean reveal) {
    }
}
