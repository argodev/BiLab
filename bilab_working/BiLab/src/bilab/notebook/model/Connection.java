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

import org.eclipse.draw2d.Graphics;

/**
 * A connection between two distinct shapes.
 * 
 * @author Elias Volanakis
 */
public class Connection extends ModelElement {
    /**
     * Used for indicating that a Connection with dashed line style should be
     * created.
     * 
     * @see org.eclipse.gef.examples.shapes.parts.GraphicEditPart#createEditPolicies()
     */
    public static final Integer DASHED_CONNECTION = new Integer(
            Graphics.LINE_DASH);
    /** Property ID to use when the line style of this connection is modified. */
    public static final String LINESTYLE_PROP = "Connection.LineStyle";

    private static final long serialVersionUID = 1;
    /**
     * Used for indicating that a Connection with solid line style should be
     * created.
     * 
     * @see org.eclipse.gef.examples.shapes.parts.GraphicEditPart#createEditPolicies()
     */
    public static final Integer SOLID_CONNECTION = new Integer(
            Graphics.LINE_SOLID);
    /** True, if the connection is attached to its endpoints. */
    private boolean isConnected;
    /** Line drawing style for this connection. */
    private int lineStyle = Graphics.LINE_SOLID;
    /** Connection's source endpoint. */
    private Graphic source;
    /** Connection's target endpoint. */
    private Graphic target;

    /**
     * Create a (solid) connection between two distinct shapes.
     * 
     * @param source
     *            a source endpoint for this connection (non null)
     * @param target
     *            a target endpoint for this connection (non null)
     * @throws IllegalArgumentException
     *             if any of the parameters are null or source == target
     * @see #setLineStyle(int)
     */
    public Connection(final Graphic source, final Graphic target) {
        reconnect(source, target);
    }

    /**
     * Disconnect this connection from the shapes it is attached to.
     */
    public void disconnect() {
        if (isConnected) {
            source.removeConnection(this);
            target.removeConnection(this);
            isConnected = false;
        }
    }

    /**
     * Returns the line drawing style of this connection.
     * 
     * @return an int value (Graphics.LINE_DASH or Graphics.LINE_SOLID)
     */
    public int getLineStyle() {
        return lineStyle;
    }

    /**
     * Returns the source endpoint of this connection.
     * 
     * @return a non-null Shape instance
     */
    public Graphic getSource() {
        return source;
    }

    /**
     * Returns the target endpoint of this connection.
     * 
     * @return a non-null Shape instance
     */
    public Graphic getTarget() {
        return target;
    }

    /**
     * Reconnect this connection. The connection will reconnect with the shapes
     * it was previously attached to.
     */
    public void reconnect() {
        if (!isConnected) {
            source.addConnection(this);
            target.addConnection(this);
            isConnected = true;
        }
    }

    /**
     * Reconnect to a different source and/or target shape. The connection will
     * disconnect from its current attachments and reconnect to the new source
     * and target.
     * 
     * @param newSource
     *            a new source endpoint for this connection (non null)
     * @param newTarget
     *            a new target endpoint for this connection (non null)
     * @throws IllegalArgumentException
     *             if any of the paramers are null or newSource == newTarget
     */
    public void reconnect(final Graphic newSource, final Graphic newTarget) {
        if (newSource == null || newTarget == null || newSource == newTarget) {
            throw new IllegalArgumentException();
        }
        disconnect();
        this.source = newSource;
        this.target = newTarget;
        reconnect();
    }

    /**
     * Set the line drawing style of this connection.
     * 
     * @param lineStyle
     *            one of following values: Graphics.LINE_DASH or
     *            Graphics.LINE_SOLID
     * @see Graphics#LINE_DASH
     * @see Graphics#LINE_SOLID
     * @throws IllegalArgumentException
     *             if lineStyle does not have one of the above values
     */
    public void setLineStyle(final int lineStyle) {
        if (lineStyle != Graphics.LINE_DASH && lineStyle != Graphics.LINE_SOLID) {
            throw new IllegalArgumentException();
        }
        this.lineStyle = lineStyle;
        firePropertyChange(LINESTYLE_PROP, null, new Integer(this.lineStyle));
    }
}