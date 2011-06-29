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
package bilab.notebook.model.commands;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import bilab.notebook.model.Connection;
import bilab.notebook.model.Graphic;

/**
 * A command to create a connection between two shapes. The command can be
 * undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command properly, following steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <tt>getConnectionCreateCommand(...)</tt> method, to create a
 * new instance of this class and put it into the CreateConnectionRequest.</li>
 * <li>Override the <tt>getConnectionCompleteCommand(...)</tt> method, to obtain
 * the Command from the ConnectionRequest, call setTarget(...) to set the target
 * endpoint of the connection and return this command instance.</li>
 * </ol>
 * 
 * @see org.eclipse.gef.examples.shapes.parts.GraphicEditPart#createEditPolicies()
 *      for an example of the above procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 * @author Elias Volanakis
 */
public class ConnectionCreateCommand extends Command {
    /** The connection instance. */
    private Connection connection;
    /** The desired line style for the connection (dashed or solid). */
    private final int lineStyle;

    /** Start endpoint for the connection. */
    private final Graphic source;
    /** Target endpoint for the connection. */
    private Graphic target;

    /**
     * Instantiate a command that can create a connection between two shapes.
     * 
     * @param source
     *            the source endpoint (a non-null Shape instance)
     * @param lineStyle
     *            the desired line style. See Connection#setLineStyle(int) for
     *            details
     * @throws IllegalArgumentException
     *             if source is null
     * @see Connection#setLineStyle(int)
     */
    public ConnectionCreateCommand(final Graphic source, final int lineStyle) {
        if (source == null) {
            throw new IllegalArgumentException();
        }
        setLabel("connection creation");
        this.source = source;
        this.lineStyle = lineStyle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#canExecute()
     */
    @Override
    public boolean canExecute() {
        // disallow source -> source connections
        if (source.equals(target)) {
            return false;
        }
        // return false, if the source -> target connection exists already
        for (final Iterator iter = source.getSourceConnections().iterator(); iter
                .hasNext();) {
            final Connection conn = (Connection) iter.next();
            if (conn.getTarget().equals(target)) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#execute()
     */
    @Override
    public void execute() {
        // create a new connection between source and target
        connection = new Connection(source, target);
        // use the supplied line style
        connection.setLineStyle(lineStyle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#redo()
     */
    @Override
    public void redo() {
        connection.reconnect();
    }

    /**
     * Set the target endpoint for the connection.
     * 
     * @param target
     *            that target endpoint (a non-null Shape instance)
     * @throws IllegalArgumentException
     *             if target is null
     */
    public void setTarget(final Graphic target) {
        if (target == null) {
            throw new IllegalArgumentException();
        }
        this.target = target;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        connection.disconnect();
    }
}
