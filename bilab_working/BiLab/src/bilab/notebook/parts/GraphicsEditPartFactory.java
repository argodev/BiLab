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
 * Copyright (c) 2004 Elias Volanakis. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: ����Elias Volanakis - initial API and implementation
 *******************************************************************************/
package bilab.notebook.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bilab.notebook.model.Connection;
import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;

/**
 * Factory that maps model elements to edit parts.
 * 
 * @author Elias Volanakis
 */
public class GraphicsEditPartFactory implements EditPartFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
     * java.lang.Object)
     */
    @Override
    public EditPart createEditPart(final EditPart context,
            final Object modelElement) {
        // get EditPart for model element
        final EditPart part = getPartForElement(modelElement);
        // store model element in EditPart
        part.setModel(modelElement);
        return part;
    }

    /**
     * Maps an object to an EditPart.
     * 
     * @throws RuntimeException
     *             if no match was found (programming error)
     */
    private EditPart getPartForElement(final Object modelElement) {
        if (modelElement instanceof GraphicsDiagram) {
            return new DiagramEditPart();
        }
        if (modelElement instanceof Graphic) {
            return new GraphicEditPart();
        }
        if (modelElement instanceof Connection) {
            return new ConnectionEditPart();
        }
        throw new RuntimeException("Can't create part for model element: "
                + ((modelElement != null) ? modelElement.getClass().getName()
                        : "null"));
    }

}