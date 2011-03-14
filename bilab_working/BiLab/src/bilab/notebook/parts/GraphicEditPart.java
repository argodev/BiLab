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

package bilab.notebook.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import bilab.notebook.model.Connection;
import bilab.notebook.model.EllipticalGraphic;
import bilab.notebook.model.Graphic;
import bilab.notebook.model.ModelElement;
import bilab.notebook.model.RectangularGraphic;
import bilab.notebook.model.commands.ConnectionCreateCommand;
import bilab.notebook.model.commands.ConnectionReconnectCommand;

/**
 * EditPart used for Shape instances (more specific for EllipticalShape and
 * RectangularShape instances).
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
class GraphicEditPart extends AbstractGraphicalEditPart implements
        PropertyChangeListener, NodeEditPart {

    /**
     * Upon activation, attach to the model element as a property change
     * listener.
     */
    @Override
    public void activate() {
        if (!isActive()) {
            super.activate();
            ((ModelElement) getModel()).addPropertyChangeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
     */
    @Override
    protected void createEditPolicies() {
        // allow removal of the associated model element
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                new GraphicComponentEditPolicy());
        // allow the creation of connections and
        // and the reconnection of connections between Shape instances
        installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
                new GraphicalNodeEditPolicy() {
                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
                     * getConnectionCompleteCommand
                     * (org.eclipse.gef.requests.CreateConnectionRequest)
                     */
                    @Override
                    protected Command getConnectionCompleteCommand(
                            final CreateConnectionRequest request) {
                        final ConnectionCreateCommand cmd = (ConnectionCreateCommand) request
                                .getStartCommand();
                        cmd.setTarget((Graphic) getHost().getModel());
                        return cmd;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
                     * getConnectionCreateCommand
                     * (org.eclipse.gef.requests.CreateConnectionRequest)
                     */
                    @Override
                    protected Command getConnectionCreateCommand(
                            final CreateConnectionRequest request) {
                        final Graphic source = (Graphic) getHost().getModel();
                        final int style = ((Integer) request.getNewObjectType())
                                .intValue();
                        final ConnectionCreateCommand cmd = new ConnectionCreateCommand(
                                source, style);
                        request.setStartCommand(cmd);
                        return cmd;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
                     * getReconnectSourceCommand
                     * (org.eclipse.gef.requests.ReconnectRequest)
                     */
                    @Override
                    protected Command getReconnectSourceCommand(
                            final ReconnectRequest request) {
                        final Connection conn = (Connection) request
                                .getConnectionEditPart().getModel();
                        final Graphic newSource = (Graphic) getHost()
                                .getModel();
                        final ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(
                                conn);
                        cmd.setNewSource(newSource);
                        return cmd;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
                     * getReconnectTargetCommand
                     * (org.eclipse.gef.requests.ReconnectRequest)
                     */
                    @Override
                    protected Command getReconnectTargetCommand(
                            final ReconnectRequest request) {
                        final Connection conn = (Connection) request
                                .getConnectionEditPart().getModel();
                        final Graphic newTarget = (Graphic) getHost()
                                .getModel();
                        final ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(
                                conn);
                        cmd.setNewTarget(newTarget);
                        return cmd;
                    }
                });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        final IFigure f = createFigureForModel();
        f.setOpaque(true); // non-transparent figure
        f.setBackgroundColor(ColorConstants.lightBlue);
        return f;
    }

    /**
     * Return a IFigure depending on the instance of the current model element.
     * This allows this EditPart to be used for both sublasses of Shape.
     */
    private IFigure createFigureForModel() {

        if (getModel() instanceof EllipticalGraphic) {
            return new Ellipse();
        } else if (getModel() instanceof RectangularGraphic) {
            return new RectangleFigure();
        } else {
            // if Shapes gets extended the conditions above must be updated
            throw new IllegalArgumentException();
        }
    }

    /**
     * Upon deactivation, detach from the model element as a property change
     * listener.
     */
    @Override
    public void deactivate() {
        if (isActive()) {
            super.deactivate();
            ((ModelElement) getModel()).removePropertyChangeListener(this);
        }
    }

    private Graphic getCastedModel() {
        return (Graphic) getModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections
     * ()
     */
    @Override
    protected List getModelSourceConnections() {
        return getCastedModel().getSourceConnections();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections
     * ()
     */
    @Override
    protected List getModelTargetConnections() {
        return getCastedModel().getTargetConnections();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
     * .ConnectionEditPart)
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(
            final ConnectionEditPart connection) {
        return new ChopboxAnchor(getFigure());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
     * .Request)
     */
    @Override
    public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
        return new ChopboxAnchor(getFigure());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
     * .ConnectionEditPart)
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(
            final ConnectionEditPart connection) {
        return new ChopboxAnchor(getFigure());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
     * .Request)
     */
    @Override
    public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
        return new ChopboxAnchor(getFigure());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String prop = evt.getPropertyName();
        if (Graphic.SIZE_PROP.equals(prop)
                || Graphic.LOCATION_PROP.equals(prop)) {
            refreshVisuals();
        }
        if (Graphic.SOURCE_CONNECTIONS_PROP.equals(prop)) {
            refreshSourceConnections();
        }
        if (Graphic.TARGET_CONNECTIONS_PROP.equals(prop)) {
            refreshTargetConnections();
        }
    }

    @Override
    protected void refreshVisuals() {
        // transfer the size and location from the model instance to the
        // corresponding figure
        final Rectangle bounds = new Rectangle(getCastedModel().getLocation(),
                getCastedModel().getSize());
        figure.setBounds(bounds);
        // notify parent container of changed position & location
        // if this line is removed, the XYLayoutManager used by the parent
        // container
        // (the Figure of the ShapesDiagramEditPart), will not know the bounds
        // of this figure
        // and will not draw it correctly.
        ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
                bounds);
    }
}