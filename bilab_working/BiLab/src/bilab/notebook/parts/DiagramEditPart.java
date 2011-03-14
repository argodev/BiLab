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

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import bilab.Notify;
import bilab.notebook.model.EllipticalGraphic;
import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;
import bilab.notebook.model.ModelElement;
import bilab.notebook.model.RectangularGraphic;
import bilab.notebook.model.commands.GraphicCreateCommand;
import bilab.notebook.model.commands.GraphicSetConstraintCommand;

/**
 * EditPart for the a ShapesDiagram instance.
 * <p>
 * This edit part server as the main diagram container, the white area where
 * everything else is in. Also responsible for the container's layout (the way
 * the container rearanges is contents) and the container's capabilities (edit
 * policies).
 * </p>
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
class DiagramEditPart extends AbstractGraphicalEditPart implements
        PropertyChangeListener {

    /**
     * EditPolicy for the Figure used by this edit part. Children of
     * XYLayoutEditPolicy can be used in Figures with XYLayout.
     * 
     * @author Elias Volanakis
     */
    private class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {

        /**
         * Create a new instance of this edit policy.
         * 
         * @param layout
         *            a non-null XYLayout instance. This should be the layout of
         *            the editpart's figure where this instance is installed.
         * @throws IllegalArgumentException
         *             if layout is null
         * @see DiagramEditPart#createEditPolicies()
         */
        ShapesXYLayoutEditPolicy(final XYLayout layout) {
            if (layout == null) {
                throw new IllegalArgumentException();
            }
            setXyLayout(layout);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand
         * (org.eclipse.gef.EditPart, java.lang.Object)
         */
        @Override
        protected Command createAddCommand(final EditPart child,
                final Object constraint) {
            Notify.unimplemented(this);
            // not used in this example
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#
         * createChangeConstraintCommand
         * (org.eclipse.gef.requests.ChangeBoundsRequest,
         * org.eclipse.gef.EditPart, java.lang.Object)
         */
        @Override
        protected Command createChangeConstraintCommand(
                final ChangeBoundsRequest request, final EditPart child,
                final Object constraint) {
            if (child instanceof GraphicEditPart
                    && constraint instanceof Rectangle) {
                // return a command that can move and/or resize a Shape
                return new GraphicSetConstraintCommand(
                        (Graphic) child.getModel(), request,
                        (Rectangle) constraint);
            }
            return super.createChangeConstraintCommand(request, child,
                    constraint);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#
         * createChangeConstraintCommand(org.eclipse.gef.EditPart,
         * java.lang.Object)
         */
        @Override
        protected Command createChangeConstraintCommand(final EditPart child,
                final Object constraint) {
            Notify.unimplemented(this);
            // not used in this example
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org
         * .eclipse.gef.requests.CreateRequest)
         */
        @Override
        protected Command getCreateCommand(final CreateRequest request) {
            final Object childClass = request.getNewObjectType();
            if (childClass == EllipticalGraphic.class
                    || childClass == RectangularGraphic.class) {
                // return a command that can add a Shape to a ShapesDiagram
                return new GraphicCreateCommand(
                        DiagramEditPart.this.getCastedModel(), request);
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand
         * (org.eclipse.gef.Request)
         */
        @Override
        protected Command getDeleteDependantCommand(final Request request) {
            // not used in this example
            return null;
        }
    }

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
        // disallows the removal of this edit part from its parent
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                new RootComponentEditPolicy());
        // handles constraint changes (e.g. moving and/or resizing) of model
        // elements
        // and creation of new model elements
        final XYLayout layout = (XYLayout) getContentPane().getLayoutManager();
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new ShapesXYLayoutEditPolicy(
                layout));
        // disable selection feedback for this edit part
        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
     */
    @Override
    protected IFigure createFigure() {
        final Figure f = new Figure();
        f.setLayoutManager(new FreeformLayout());
        return f;
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

    private GraphicsDiagram getCastedModel() {
        return (GraphicsDiagram) getModel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
     */
    @Override
    protected List getModelChildren() {
        return getCastedModel().getChildren(); // return a list of shapes
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
        // these properties are fired when Shapes are added into or removed from
        // the ShapeDiagram instance and must cause a call of refreshChildren()
        // to update the diagram's contents.
        if (GraphicsDiagram.CHILD_ADDED_PROP.equals(prop)
                || GraphicsDiagram.CHILD_REMOVED_PROP.equals(prop)) {
            refreshChildren();
        }
    }

}