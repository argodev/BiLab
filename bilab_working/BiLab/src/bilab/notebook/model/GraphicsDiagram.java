/*******************************************************************************
 * Copyright (c) 2004 Elias Volanakis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *����Elias Volanakis - initial API and implementation
 *******************************************************************************/
package bilab.notebook.model;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * A container for multiple shapes.
 * This is the "root" of the model data structure.
 * @author Elias Volanakis
 */
public class GraphicsDiagram extends ModelElement {

/** Property ID to use when a child is added to this diagram. */
public static final String CHILD_ADDED_PROP = "ShapesDiagram.ChildAdded";
/** Property ID to use when a child is removed from this diagram. */
public static final String CHILD_REMOVED_PROP = "ShapesDiagram.ChildRemoved";
private static final long serialVersionUID = 1;
private Collection shapes = new Vector();

/** 
 * Add a shape to this diagram.
 * @param s a non-null shape instance
 * @return true, if the shape was added, false otherwise
 */
public boolean addChild(Graphic s) {
	if (s != null && shapes.add(s)) {
		firePropertyChange(CHILD_ADDED_PROP, null, s);
		return true;
	}
	return false;
}

/** Return a List of Shapes in this diagram. */
public List getChildren() {
	return new Vector(shapes);
}

/**
 * Remove a shape from this diagram.
 * @param s a non-null shape instance;
 * @return true, if the shape was removed, false otherwise
 */
public boolean removeChild(Graphic s) {
	if (s != null && shapes.remove(s)) {
		firePropertyChange(CHILD_REMOVED_PROP, null, s);
		return true;
	}
	return false;
}
}