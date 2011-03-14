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

/*******************************************************************************
 * Copyright (c) 2004 Elias Volanakis. �* All rights reserved. This program and
 * the accompanying materials �* are made available under the terms of the
 * Common Public License v1.0 �* which accompanies this distribution, and is
 * available at �* http://www.eclipse.org/legal/cpl-v10.html �* �* Contributors:
 * �*����Elias Volanakis - initial API and implementation �
 *******************************************************************************/
package bilab.notebook.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.swt.graphics.Image;

import bilab.notebook.model.Graphic;
import bilab.notebook.model.ModelElement;

/**
 * TreeEditPart used for Shape instances (more specific for EllipticalShape and
 * RectangularShape instances). This is used in the Outline View of the
 * ShapesEditor.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
class GraphicTreeEditPart extends AbstractTreeEditPart implements
        PropertyChangeListener {

    /**
     * Create a new instance of this edit part using the given model element.
     * 
     * @param model
     *            a non-null Shapes instance
     */
    GraphicTreeEditPart(final Graphic model) {
        super(model);
    }

    /**
     * Upon activation, attach to the model element as a property change
     * listener.
     */
    @Override
    public void activate() {
        if (!isActive()) {
            super.activate();
            ((ModelElement) getModel()).addPropertyChangeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        // allow removal of the associated model element
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                new GraphicComponentEditPolicy());
    }

    /**
     * Upon deactivation, detach from the model element as a property change
     * listener.
     */
    @Override
    public void deactivate() {
        if (isActive()) {
            super.deactivate();
            ((ModelElement) getModel()).removePropertyChangeListener(this);
        }
    }

    private Graphic getCastedModel() {
        return (Graphic) getModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
     */
    @Override
    protected Image getImage() {
        return getCastedModel().getIcon();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
     */
    @Override
    protected String getText() {
        return getCastedModel().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        refreshVisuals(); // this will cause an invocation of getImage() and
                          // getText(), see below
    }
}