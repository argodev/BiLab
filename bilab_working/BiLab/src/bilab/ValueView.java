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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

// a view for a scigol.Value
public class ValueView extends ViewPart implements ISelectionListener {

    // !!! hack for EnvNavigatorView to set our input (see explanation there)
    public static ValueView getInstance() {
        return instance;
    }

    private ValueViewer valueViewer;

    private static ValueView instance = null;

    public ValueView() {
        // !!! tmp hack (see below)
        instance = this;
    }

    @Override
    public void createPartControl(final Composite parent) {
        valueViewer = new ValueViewer(parent);
        setInput(new scigol.Value(Util.readResource("molecules/1GP2.pdb", ""))); // !!!

        // the ValueView may want to provide selection events & also listen for
        // them
        // So, we need to adapt between the ISelectionChangeListenr interface of
        // jface & the platform ui ISelectionListener method

        // register our listener to listen to the value viewer and pass on the
        // events as an ISelection

        getSite().setSelectionProvider(valueViewer);
        getSite().getPage().addSelectionListener(this);

    }

    @Override
    public void dispose() {
        getSite().getPage().removeSelectionListener(valueViewer);
        super.dispose();
    }

    @Override
    public void selectionChanged(final IWorkbenchPart source,
            final ISelection selection) {
        if (selection instanceof ValueSelection) {
            setInput(((ValueSelection) selection).value);
        }

        valueViewer.selectionChanged(source, selection); // !!! why was this
                                                         // commented out??
    }

    @Override
    public void setFocus() {
        valueViewer.getControl().setFocus();
    }

    public void setInput(final scigol.Value v) {
        valueViewer.setInput(v);
        final String title = valueViewer.get_title();
        if (title.length() > 0) {
            setPartName(title);
            setContentDescription(valueViewer.get_description());
        } else {
            setPartName("Value");
            setContentDescription("");
        }
    }

}