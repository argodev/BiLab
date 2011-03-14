﻿/**
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import scigol.TypeSpec;
import scigol.Value;

// displays a scigol Value
public class ValueViewer extends Viewer implements ISelectionListener {

    // we register this with the viewer as a selection change listener, and
    // re-fire those events
    // via our Viewer.fileSelectionChanged()
    private final ISelectionChangedListener viewerListener = new ISelectionChangedListener() {
        @Override
        public void selectionChanged(final SelectionChangedEvent event) {
            fireSelectionChanged(event);
        }
    };

    // private Composite parent;
    private final Composite top;

    private Composite child;

    private Label label;

    private ViewerBase viewer; // viewer or null

    private Value input = null;

    public ValueViewer(final Composite parent) {
        // this.parent = parent;

        top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        child = new Composite(top, SWT.NONE);
        child.setLayout(new FillLayout());

        label = new Label(child, SWT.NONE);
        viewer = null;
        input = null;
        refresh();

    }

    public String get_description() {
        if (viewer != null) {
            return viewer.get_description();
        }
        return "";
    }

    public String get_title() {
        if (viewer != null) {
            return viewer.get_title();
        }
        return "";
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
    public ISelection getSelection() {
        if (viewer != null) {
            return viewer.getSelection();
        }
        return StructuredSelection.EMPTY;
    }

    public Point maximumSize() {
        if (viewer != null) {
            return viewer.maximumSize();
        }
        return top.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    public Point preferedSize() {
        if (viewer != null) {
            return viewer.preferedSize();
        }
        return top.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    @Override
    public void refresh() {
        if (viewer == null) {
            // update label
            if (input != null) {
                final Object v = TypeSpec.unwrapAnyOrNum(input);
                if (v instanceof IUserText) {
                    label.setText(((IUserText) v).get_DetailText());
                } else {
                    label.setText(v.toString());
                }
            } else {
                label.setText("<no value>");
            }
        } else { // update viewer
            viewer.setInput(TypeSpec.unwrapAnyOrNum(input));
            viewer.refresh();
        }
        top.layout(true);
        top.redraw();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part,
            final ISelection selection) {

        if (selection instanceof ValueSelection) {
            final ValueSelection vs = (ValueSelection) selection;
            if (!vs.isEmpty()) {
                // Notify.DevInfo(this,"got value:"+vs.value+
                // " instanceof Value?"+(vs.value instanceof Value));

                // change the value being viewed, if there is a viewer for it
                final Object model = TypeSpec.unwrapAnyOrNum(vs.value);
                if (BilabPlugin.existsViewer(TypeSpec.typeOf(model))) {
                    try {
                        Notify.devInfo(
                                this,
                                "setting viewer for type "
                                        + TypeSpec.typeOf(model) + " to input "
                                        + model.toString());
                        setInput(vs.value);
                    } catch (final Exception e) {
                        Notify.devWarning(this, "exception setting input:" + e);
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }

        // pass on selection
        if (viewer != null) {
            viewer.selectionChanged(part, selection);
        }

    }

    @Override
    public void setInput(final Object input) {
        if (input == this.input) {
            return; // didn't change
        }

        if (input instanceof Value) {
            this.input = (Value) input;
        } else {
            return;// !!!
                   // this.input = null;
                   // Notify.DevWarning(this,"non-Value input passed to ValueViewer.setInput()");
        }

        // first, dispose of the current viewer, if any
        if (!child.isDisposed()) {
            child.dispose();
            label = null;
        }
        if (viewer != null) {
            viewer.removeSelectionChangedListener(viewerListener);
            viewer.dispose();
            viewer = null;
        }

        child = new Composite(top, SWT.NONE);
        child.setLayout(new FillLayout());

        // instantiate the approproate type of viewer for the input type
        // (if any, otherwise, just use a Label to display the string
        // representation)
        if (input != null) {
            final Object model = TypeSpec.unwrapAnyOrNum(input);
            viewer = BilabPlugin.instantiateViewer(TypeSpec.typeOf(model),
                    child);
        }

        if (viewer == null) {
            label = new Label(child, SWT.NONE);
        } else {
            viewer.addSelectionChangedListener(viewerListener);
        }

        refresh();
    }

    @Override
    public void setSelection(final ISelection selection, final boolean reveal) {
        if (viewer != null) {
            viewer.setSelection(selection, reveal);
        }
    }
}
