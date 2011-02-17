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

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;
import bilab.notebook.model.commands.GraphicDeleteCommand;

/**
 * This edit policy enables the removal of a Shapes instance from its container. 
 * @see GraphicEditPart#createEditPolicies()
 * @see GraphicTreeEditPart#createEditPolicies()
 * @author Elias Volanakis
 */
class GraphicComponentEditPolicy extends ComponentEditPolicy {

/* (non-Javadoc)
 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
 */
protected Command createDeleteCommand(GroupRequest deleteRequest) {
	Object parent = getHost().getParent().getModel();
	Object child = getHost().getModel();
	if (parent instanceof GraphicsDiagram && child instanceof Graphic) {
		return new GraphicDeleteCommand((GraphicsDiagram) parent, (Graphic) child);
	}
	return super.createDeleteCommand(deleteRequest);
}
}