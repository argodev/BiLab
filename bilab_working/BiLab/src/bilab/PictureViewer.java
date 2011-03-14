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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbenchPart;

// simple picture viewer
public class PictureViewer extends ViewerBase {
    public class PaintListener implements Listener {
        public picture input;

        public PaintListener() {
            input = null;
        }

        @Override
        public void handleEvent(final Event e) {

            if (input == null) {
                return; // paint default 'no image'?
            }

            if (this.input.get_PictureType() == picture.PictureType.Image) {
                final Image image = input.get_Image();
                final GC gc = e.gc;
                gc.drawImage(image, 0, 0);
                final Rectangle rect = image.getBounds();
                final Rectangle client = pictureComposite.getClientArea();
                final int marginWidth = client.width - rect.width;
                if (marginWidth > 0) {
                    gc.fillRectangle(rect.width, 0, marginWidth, client.height);
                }
                final int marginHeight = client.height - rect.height;
                if (marginHeight > 0) {
                    gc.fillRectangle(0, rect.height, client.width, marginHeight);
                }
            } else {
                Notify.unimplemented(this);
                final GC gc = e.gc;
                gc.fillRectangle(0, 0, 10, 10);
            }
        }
    }

    picture input;
    SizedComposite pictureComposite;
    PaintListener paintListener;

    public PictureViewer(final Composite parent) {
        final Display display = Display.getCurrent();
        input = null; // !!! set a default 'no image' icon of something here

        pictureComposite = new SizedComposite(parent, SWT.NO_BACKGROUND);
        pictureComposite.setPreferedSize(10, 10); // !! size of default icon
        pictureComposite.setMaximumSize(10, 10);
        pictureComposite.setMinimumSize(10, 10);
        pictureComposite.setSize(10, 10);

        pictureComposite.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

        paintListener = new PaintListener();
        paintListener.input = input;

        pictureComposite.addListener(SWT.Paint, paintListener);
    }

    @Override
    public void dispose() {
    }

    @Override
    public String get_description() {
        return get_title();
    }

    @Override
    public String get_title() {
        return (input != null) ? input.get_name() : "<blank picture>";
    }

    @Override
    public Control getControl() {
        return pictureComposite;
    }

    @Override
    public Object getInput() {
        return input;
    }

    @Override
    public Point maximumSize() {
        return pictureComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        // return new Point(SWT.MAX, SWT.MAX); // !!! later perhaps we can scale
        // images
    }

    @Override
    public Point preferedSize() {
        return pictureComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    @Override
    public void refresh() {
        pictureComposite.redraw();
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
            if (input instanceof picture) {
                this.input = (picture) input;
                if (this.input.get_PictureType() == picture.PictureType.Image) {
                    final Rectangle rect = this.input.get_Image().getBounds();
                    pictureComposite.setPreferedSize(rect.width, rect.height);
                    pictureComposite.setMaximumSize(rect.width, rect.height);
                    pictureComposite.setMinimumSize(10, 10);
                    pictureComposite.setSize(rect.width, rect.height);
                } else {
                    Notify.unimplemented(this);
                }
            } else {
                this.input = null;
            }
        }
        paintListener.input = this.input;
        refresh();
    }
}