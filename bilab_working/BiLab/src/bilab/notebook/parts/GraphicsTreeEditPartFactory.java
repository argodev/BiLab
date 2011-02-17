/*******************************************************************************
 * Copyright (c) 2004 Elias Volanakis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *******************************************************************************/
package bilab.notebook.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;


/**
 * Factory that maps model elements to TreeEditParts.
 * TreeEditParts are used in the outline view of the ShapesEditor.
 * @author Elias Volanakis
 */
public class GraphicsTreeEditPartFactory implements EditPartFactory {

/* (non-Javadoc)
 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
 */
public EditPart createEditPart(EditPart context, Object model) {
	if (model instanceof Graphic) {
		return new GraphicTreeEditPart((Graphic) model);
	}
	if (model instanceof GraphicsDiagram) {
		return new DiagramTreeEditPart((GraphicsDiagram) model);
	}
	return null; // will not show an entry for the corresponding model instance
}

}
