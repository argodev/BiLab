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
 * Copyright (c) 2004 Elias Volanakis. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: ����Elias Volanakis - initial API and implementation
 *******************************************************************************/
package bilab.notebook.model;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * A container for multiple shapes. This is the "root" of the model data
 * structure.
 * 
 * @author Elias Volanakis
 */
public class GraphicsDiagram extends ModelElement {

    /** Property ID to use when a child is added to this diagram. */
    public static final String CHILD_ADDED_PROP = "ShapesDiagram.ChildAdded";
    /** Property ID to use when a child is removed from this diagram. */
    public static final String CHILD_REMOVED_PROP = "ShapesDiagram.ChildRemoved";
    private static final long serialVersionUID = 1;
    private final Collection shapes = new Vector();

    /**
     * Add a shape to this diagram.
     * 
     * @param s
     *            a non-null shape instance
     * @return true, if the shape was added, false otherwise
     */
    public boolean addChild(final Graphic s) {
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
     * 
     * @param s
     *            a non-null shape instance;
     * @return true, if the shape was removed, false otherwise
     */
    public boolean removeChild(final Graphic s) {
        if (s != null && shapes.remove(s)) {
            firePropertyChange(CHILD_REMOVED_PROP, null, s);
            return true;
        }
        return false;
    }
}