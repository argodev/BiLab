/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
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