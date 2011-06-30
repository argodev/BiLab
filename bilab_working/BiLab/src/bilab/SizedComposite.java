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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

// A composite with a fixed size
public class SizedComposite extends Composite {
    int minWidth, minHeight;

    int maxWidth, maxHeight;

    int prefWidth, prefHeight;

    public SizedComposite(final Composite parent, final int style) {
        super(parent, style);
        setLayout(new FillLayout());

        minWidth = maxWidth = prefWidth = 200;
        minHeight = maxHeight = prefHeight = 200;
    }

    @Override
    public Point computeSize(final int wHint, final int hHint) {
        int width = prefWidth;
        int height = prefHeight;

        if (wHint != SWT.DEFAULT) {
            width = wHint;
            if (width < minWidth) {
                width = minWidth;
            }
            if (width > maxWidth) {
                width = maxWidth;
            }
        }

        if (hHint != SWT.DEFAULT) {
            height = hHint;
            if (height < minHeight) {
                height = minHeight;
            }
            if (height > maxHeight) {
                height = maxHeight;
            }
        }

        return new Point(width, height);
    }

    @Override
    public Point computeSize(final int wHint, final int hHint,
            final boolean changed) {
        return computeSize(wHint, hHint);
    }

    public void setMaximumSize(final int width, final int height) {
        maxWidth = width;
        if (maxWidth < prefWidth) {
            prefWidth = maxWidth;
        }
        if (maxWidth < minWidth) {
            minWidth = maxWidth;
        }

        maxHeight = height;
        if (maxHeight < prefHeight) {
            prefHeight = maxHeight;
        }
        if (maxHeight < minHeight) {
            minHeight = maxHeight;
        }
    }

    public void setMaximumSize(final Point p) {
        setMaximumSize(p.x, p.y);
    }

    public void setMinimumSize(final int width, final int height) {
        minWidth = width;
        if (minWidth > maxWidth) {
            maxWidth = minWidth;
        }
        if (minWidth > prefWidth) {
            prefWidth = minWidth;
        }

        minHeight = height;
        if (minHeight > maxHeight) {
            maxHeight = minHeight;
        }
        if (minHeight > prefHeight) {
            prefHeight = minHeight;
        }
    }

    public void setMinimumSize(final Point p) {
        setMinimumSize(p.x, p.y);
    }

    public void setPreferedSize(final int width, final int height) {
        prefWidth = width;
        if (prefWidth > maxWidth) {
            maxWidth = prefWidth;
        }
        if (prefWidth < minWidth) {
            minWidth = prefWidth;
        }

        prefHeight = height;
        if (prefHeight > maxHeight) {
            maxHeight = prefHeight;
        }
        if (prefHeight < minHeight) {
            minHeight = prefHeight;
        }
    }

    public void setPreferedSize(final Point p) {
        setPreferedSize(p.x, p.y);
    }
}
