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

package bilab.notebook.model.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import bilab.notebook.model.Graphic;

/**
 * A command to resize and/or move a shape. The command can be undone or redone.
 * 
 * @author Elias Volanakis
 */
public class GraphicSetConstraintCommand extends Command {
    /** Stores the new size and location. */
    private final Rectangle newBounds;
    /** Stores the old size and location. */
    private Rectangle oldBounds;
    /** A request to move/resize an edit part. */
    private final ChangeBoundsRequest request;

    /** Shape to manipulate. */
    private final Graphic shape;

    /**
     * Create a command that can resize and/or move a shape.
     * 
     * @param shape
     *            the shape to manipulate
     * @param req
     *            the move and resize request
     * @param newBounds
     *            the new size and location
     * @throws IllegalArgumentException
     *             if any of the parameters is null
     */
    public GraphicSetConstraintCommand(final Graphic shape,
            final ChangeBoundsRequest req, final Rectangle newBounds) {
        if (shape == null || req == null || newBounds == null) {
            throw new IllegalArgumentException();
        }
        this.shape = shape;
        this.request = req;
        this.newBounds = newBounds.getCopy();
        setLabel("move / resize");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#canExecute()
     */
    @Override
    public boolean canExecute() {
        final Object type = request.getType();
        // make sure the Request is of a type we support:
        return (RequestConstants.REQ_MOVE.equals(type)
                || RequestConstants.REQ_MOVE_CHILDREN.equals(type)
                || RequestConstants.REQ_RESIZE.equals(type) || RequestConstants.REQ_RESIZE_CHILDREN
                .equals(type));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#execute()
     */
    @Override
    public void execute() {
        oldBounds = new Rectangle(shape.getLocation(), shape.getSize());
        redo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#redo()
     */
    @Override
    public void redo() {
        shape.setSize(newBounds.getSize());
        shape.setLocation(newBounds.getLocation());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.commands.Command#undo()
     */
    @Override
    public void undo() {
        shape.setSize(oldBounds.getSize());
        shape.setLocation(oldBounds.getLocation());
    }
}
