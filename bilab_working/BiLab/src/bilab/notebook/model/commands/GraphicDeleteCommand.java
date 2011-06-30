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
 * accompanying materials �* are made available under the terms of the Common
 * Public License v1.0 �* which accompanies this distribution, and is available
 * at �* http://www.eclipse.org/legal/cpl-v10.html �* �* Contributors:
 * �*����Elias Volanakis - initial API and implementation �
 *******************************************************************************/
package bilab.notebook.model.commands;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import bilab.notebook.model.Connection;
import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;

/**
 * A command to remove a shape from its parent. The command can be undone or
 * redone.
 * 
 * @author Elias Volanakis
 */
public class GraphicDeleteCommand extends Command {
    /** Shape to remove. */
    private final Graphic child;

    /** ShapeDiagram to remove from. */
    private final GraphicsDiagram parent;
    /** Holds a copy of the outgoing connections of child. */
    private List sourceConnections;
    /** Holds a copy of the incoming connections of child. */
    private List targetConnections;
    /** True, if child was removed from its parent. */
    private boolean wasRemoved;

    /**
     * Create a command that will remove the shape from its parent.
     * 
     * @param parent
     *            the ShapesDiagram containing the child
     * @param child
     *            the Shape to remove
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public GraphicDeleteCommand(final GraphicsDiagram parent,
            final Graphic child) {
        if (parent == null || child == null) {
            throw new IllegalArgumentException();
        }
        setLabel("shape deletion");
        this.parent = parent;
        this.child = child;
    }

    /**
     * Reconnects a List of Connections with their previous endpoints.
     * 
     * @param connections
     *            a non-null List of connections
     */
    private void addConnections(final List connections) {
        for (final Iterator iter = connections.iterator(); iter.hasNext();) {
            final Connection conn = (Connection) iter.next();
            conn.reconnect();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#canUndo()
     */
    @Override
    public boolean canUndo() {
        return wasRemoved;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#execute()
     */
    @Override
    public void execute() {
        // store a copy of incoming & outgoing connections before proceeding
        sourceConnections = child.getSourceConnections();
        targetConnections = child.getTargetConnections();
        redo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#redo()
     */
    @Override
    public void redo() {
        // remove the child and disconnect its connections
        wasRemoved = parent.removeChild(child);
        if (wasRemoved) {
            removeConnections(sourceConnections);
            removeConnections(targetConnections);
        }
    }

    /**
     * Disconnects a List of Connections from their endpoints.
     * 
     * @param connections
     *            a non-null List of connections
     */
    private void removeConnections(final List connections) {
        for (final Iterator iter = connections.iterator(); iter.hasNext();) {
            final Connection conn = (Connection) iter.next();
            conn.disconnect();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        // add the child and reconnect its connections
        if (parent.addChild(child)) {
            addConnections(sourceConnections);
            addConnections(targetConnections);
        }
    }
}