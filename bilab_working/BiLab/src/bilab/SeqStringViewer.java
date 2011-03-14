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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

// simple viewer for sequence strings (breaks into columns)
public class SeqStringViewer extends ViewerBase {
    seq input;

    Label label;
    Font labelFont;
    int fontPixelWidth;
    Composite parent;

    public SeqStringViewer(final Composite parent) {
        this.parent = parent;
        final Display display = Display.getCurrent();
        input = null;
        label = new Label(parent, SWT.NONE);
        label.setText("");
        label.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
        label.setForeground(display.getSystemColor(SWT.COLOR_BLACK));

        final FontData fontData = new FontData("Courier New", 10, SWT.BOLD);
        labelFont = new Font(display, fontData);
        label.setFont(labelFont);

        fontPixelWidth = 10; // hard code for now!!! (need to get from
                             // FontMetrics somehow)
    }

    @Override
    public void dispose() {
    }

    @Override
    public String get_description() {
        if (input != null) {
            return input.get_ShortText();
        } else {
            return "";
        }
    }

    @Override
    public String get_title() {
        if (input != null) {
            return input.get_name();
        } else {
            return "";
        }
    }

    @Override
    public Control getControl() {
        return label;
    }

    @Override
    public Object getInput() {
        return input;
    }

    @Override
    public Point maximumSize() {
        return new Point((60 + 6) * fontPixelWidth, SWT.MAX);
    }

    @Override
    public Point preferedSize() {
        return label.computeSize((60 + 6) * fontPixelWidth, SWT.DEFAULT);
    }

    @Override
    public void refresh() {
        if (input != null) {
            label.setText(input.get_sequence());
        } else {
            label.setText("");
        }
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {
    }

    @Override
    public void setInput(final Object input) {
        if (input == null) {
            this.input = null;
        } else {
            if (input instanceof seq) {
                this.input = (seq) input;
            } else {
                this.input = null;
            }
        }
        refresh();
    }

}
