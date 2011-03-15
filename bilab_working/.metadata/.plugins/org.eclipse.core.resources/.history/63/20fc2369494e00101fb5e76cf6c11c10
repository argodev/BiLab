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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

// simple string viewer (for completeness)
public class StringViewer extends ViewerBase {
    String input;

    Label label;

    public StringViewer(final Composite parent) {
        final Display display = Display.getCurrent();
        input = "";
        label = new Label(parent, SWT.NONE);
        label.setText("");
        label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
    }

    @Override
    public void dispose() {
    }

    @Override
    public String get_description() {
        return "";
    }

    @Override
    public String get_title() {
        return "";
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
        return new Point(SWT.MAX, SWT.MAX);
    }

    @Override
    public Point preferedSize() {
        return label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    @Override
    public void refresh() {
        if (input != null) {
            label.setText(input);
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
            if (input instanceof String) {
                this.input = (String) input;
            } else {
                this.input = input.toString();
            }
        }
        refresh();
    }

}
