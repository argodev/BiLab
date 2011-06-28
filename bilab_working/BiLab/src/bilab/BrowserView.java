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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

public class BrowserView extends ViewPart {

    protected class InputListener implements KeyListener {
        BrowserView view;

        public InputListener(final BrowserView v) {
            view = v;
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            view.handleInputKeyEvent(e);
        }

        @Override
        public void keyReleased(final KeyEvent e) {
        }
    }

    Composite top, area, addressBar;

    Text address;
    HTMLViewer viewer;
    ToolItem back, forward, stop, reload, home;

    public BrowserView() {
    }

    @Override
    public void createPartControl(final Composite parent) {
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        area = new Composite(top, SWT.BORDER);
        final FormLayout vlayout = new FormLayout();
        area.setLayout(vlayout);

        addressBar = new Composite(area, SWT.NONE);

        final FormData addressBarData = new FormData();
        addressBarData.left = new FormAttachment(0, 0);
        addressBarData.top = new FormAttachment(0, 0);
        addressBarData.right = new FormAttachment(100, 0);
        addressBar.setLayoutData(addressBarData);

        final RowLayout hlayout = new RowLayout();
        hlayout.type = SWT.HORIZONTAL;
        hlayout.fill = false;
        hlayout.justify = false;
        hlayout.wrap = true;
        addressBar.setLayout(hlayout);

        final ToolBar toolBar = new ToolBar(addressBar, SWT.FLAT);

        back = new ToolItem(toolBar, SWT.NONE);
        setImage(back, "icons/left_small", "Back");

        forward = new ToolItem(toolBar, SWT.FLAT);
        setImage(forward, "icons/right_small", "Forward");

        stop = new ToolItem(toolBar, SWT.FLAT);
        setImage(stop, "icons/stop_small", "Stop");

        reload = new ToolItem(toolBar, SWT.FLAT);
        setImage(reload, "icons/reload_small", "Reload");

        home = new ToolItem(toolBar, SWT.FLAT);
        setImage(home, "icons/home_small", "Home");

        address = new Text(addressBar, SWT.BORDER);
        // address.setSize(400,24);
        address.addKeyListener(new InputListener(this));

        viewer = new HTMLViewer(area);

        final FormData viewerData = new FormData();
        viewerData.top = new FormAttachment(addressBar);
        viewerData.bottom = new FormAttachment(100, 0);
        viewerData.left = new FormAttachment(0, 0);
        viewerData.right = new FormAttachment(100, 0);
        viewer.getControl().setLayoutData(viewerData);

        // setUrl("http://robgillen.me");
        setUrl("http://www.ornl.gov/ornlhome/high_performance_computing.shtml");
    }

    protected void handleInputKeyEvent(final KeyEvent e) {
        if (e.stateMask == SWT.NONE) {
            if (e.keyCode == SWT.CR) {
                setUrl(address.getText());
                viewer.getControl().setFocus(); // take focus off address
            }
        }
    }

    @Override
    public void setFocus() {
        address.setFocus();
        address.selectAll();
    }

    protected void setImage(final ToolItem b, final String resourceNamePrefix,
            final String toolTip) {
        try {
            b.setImage(new Image(Display.getCurrent(), BilabPlugin
                    .findResourceStream(resourceNamePrefix + ".gif")));
            b.setDisabledImage(new Image(Display.getCurrent(), BilabPlugin
                    .findResourceStream(resourceNamePrefix + "_disabled.png")));
            b.setToolTipText(toolTip);
        } catch (final java.io.IOException e) {
            b.setText(toolTip);
        }
    }

    public void setUrl(final String url) {
        address.setText(url);
        viewer.setInput(url);
    }
}
