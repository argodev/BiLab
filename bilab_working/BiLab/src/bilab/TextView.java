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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

// !!! probably depricated - replace with editor (not a view)
public class TextView extends ViewPart {
    Text text;
    Composite top, inner;
    ScrolledComposite scrolled;

    public TextView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        scrolled = new ScrolledComposite(top, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.BORDER);

        // inner = new Composite(scrolled, SWT.NONE);
        // inner.setLayout(new FillLayout());
        // scrolled.setContent(inner);

        // inner.setBounds(0,0,640,1000);
        // inner.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        // scrolled.setMinSize(inner.computeSize(SWT.DEFAULT, SWT.DEFAULT,
        // false));

        text = new Text(scrolled, SWT.MULTI);
        text.setBounds(0, 0, 640, 1000);

        final FontData fontData = new FontData("Courier New", 10, SWT.NORMAL);
        final Font consoleFont = new Font(Display.getCurrent(), fontData);
        text.setFont(consoleFont);

        scrolled.setContent(text);
        scrolled.setMinSize(text.computeSize(SWT.DEFAULT, SWT.DEFAULT, false));
    }

    @Override
    public void setFocus() {
        scrolled.setFocus();
    }
}