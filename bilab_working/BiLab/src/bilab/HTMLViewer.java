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

import java.net.URL;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;

// View HTML content
public class HTMLViewer extends ViewerBase {
    Composite top;

    Browser browser;
    String input; // url

    public HTMLViewer(final Composite parent) {
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        browser = new Browser(top, SWT.BORDER);
        browser.setText("<html><title>Welcome to Bilab</title><body><h1>Welcome to Bilab.</h1></body></html>");
        input = "";
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public String get_description() {
        return "Web Browser";
    }

    @Override
    public String get_title() {
        return "Web";
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
        return new Point(SWT.MAX, SWT.MAX);
    }

    @Override
    public Point preferedSize() {
        return new Point(SWT.DEFAULT, 400);
    }

    @Override
    public void refresh() {
        // browser.refresh();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setInput(final Object input) {
        if (input instanceof String) {
            this.input = (String) input;
        } else if (input instanceof URL) {
            this.input = ((URL) input).toString();
        } else {
            this.input = "";
        }
        browser.setUrl(this.input);
        Notify.devInfo(this, "HTMLViewer set URL " + this.input);
    }
}