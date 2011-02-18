/**
* This document is a part of the source code and related artifacts for BiLab, an open source interactive workbench for 
* computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
* Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
* details.
*
* You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to 
* the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* The license is also available at: http://www.gnu.org/copyleft/lgpl.html
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
 * <p>This edit part server as the main diagram container, the white area where
 * everything else is in. Also responsible for the container's layout (the
 * way the container rearanges is contents) and the container's capabilities
 * (edit policies).
 * </p>
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
class DiagramEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener  
{
  
  /**
   * Upon activation, attach to the model element as a property change listener.
   */
  public void activate() {
    if (!isActive()) {
      super.activate();
      ((ModelElement) getModel()).addPropertyChangeListener(this);
    }
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
   */
  protected void createEditPolicies() {
    // disallows the removal of this edit part from its parent
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
    // handles constraint changes (e.g. moving and/or resizing) of model elements
    // and creation of new model elements
    XYLayout layout = (XYLayout) getContentPane().getLayoutManager();
    installEditPolicy(EditPolicy.LAYOUT_ROLE,  new ShapesXYLayoutEditPolicy(layout));
    // disable selection feedback for this edit part
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
   */
  protected IFigure createFigure() {
    Figure f = new Figure();
    f.setLayoutManager(new FreeformLayout());
    return f;
  }
  
  /**
   * Upon deactivation, detach from the model element as a property change listener.
   */
  public void deactivate() {
    if (isActive()) {
      super.deactivate();
      ((ModelElement) getModel()).removePropertyChangeListener(this);
    }
  }
  
  private GraphicsDiagram getCastedModel() {
    return (GraphicsDiagram) getModel();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
   */
  protected List getModelChildren() {
    return getCastedModel().getChildren(); // return a list of shapes
  }
  
  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    String prop = evt.getPropertyName();
    // these properties are fired when Shapes are added into or removed from 
    // the ShapeDiagram instance and must cause a call of refreshChildren()
    // to update the diagram's contents.
    if (GraphicsDiagram.CHILD_ADDED_PROP.equals(prop)
        || GraphicsDiagram.CHILD_REMOVED_PROP.equals(prop)) {
      refreshChildren();
    }
  }
  
  /**
   * EditPolicy for the Figure used by this edit part.
   * Children of XYLayoutEditPolicy can be used in Figures with XYLayout.
   * @author Elias Volanakis
   */
  private class ShapesXYLayoutEditPolicy extends XYLayoutEditPolicy {
    
    /** 
     * Create a new instance of this edit policy.
     * @param layout a non-null XYLayout instance. This should be the layout of the editpart's 
     *              figure where this instance is installed.
     * @throws IllegalArgumentException if layout is null 
     * @see DiagramEditPart#createEditPolicies()
     */
    ShapesXYLayoutEditPolicy(XYLayout layout) {
      if (layout == null) {
        throw new IllegalArgumentException();
      }
      setXyLayout(layout);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
     */
    protected Command createAddCommand(EditPart child, Object constraint) {
      Notify.unimplemented(this);
      // not used in this example
      return null;
    }
    
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.requests.ChangeBoundsRequest, org.eclipse.gef.EditPart, java.lang.Object)
     */
    protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
        EditPart child, Object constraint) {
      if (child instanceof GraphicEditPart && constraint instanceof Rectangle) {
        // return a command that can move and/or resize a Shape
        return new GraphicSetConstraintCommand(
            (Graphic) child.getModel(), request, (Rectangle) constraint);
      }
      return super.createChangeConstraintCommand(request, child, constraint);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
     */
    protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
      Notify.unimplemented(this);
      // not used in this example
      return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
     */
    protected Command getCreateCommand(CreateRequest request) {
      Object childClass = request.getNewObjectType();
      if (childClass == EllipticalGraphic.class || childClass == RectangularGraphic.class) {
        // return a command that can add a Shape to a ShapesDiagram 
        return new GraphicCreateCommand(DiagramEditPart.this.getCastedModel(), request);
      }
      return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
     */
    protected Command getDeleteDependantCommand(Request request) {
      // not used in this example
      return null;
    }
  }
  
}