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

/*******************************************************************************
 * Copyright (c) 2004 Elias Volanakis. �* All rights reserved. This program and
 * the accompanying materials �* are made available under the terms of the
 * Common Public License v1.0 �* which accompanies this distribution, and is
 * available at �* http://www.eclipse.org/legal/cpl-v10.html �* �* Contributors:
 * �*����Elias Volanakis - initial API and implementation �
 *******************************************************************************/
package bilab.notebook.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;

/**
 * Factory that maps model elements to TreeEditParts. TreeEditParts are used in
 * the outline view of the ShapesEditor.
 * 
 * @author Elias Volanakis
 */
public class GraphicsTreeEditPartFactory implements EditPartFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
     * java.lang.Object)
     */
    @Override
    public EditPart createEditPart(final EditPart context, final Object model) {
        if (model instanceof Graphic) {
            return new GraphicTreeEditPart((Graphic) model);
        }
        if (model instanceof GraphicsDiagram) {
            return new DiagramTreeEditPart((GraphicsDiagram) model);
        }
        return null; // will not show an entry for the corresponding model
                     // instance
    }

}
