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