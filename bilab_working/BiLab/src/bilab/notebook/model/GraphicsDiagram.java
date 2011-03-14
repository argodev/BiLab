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