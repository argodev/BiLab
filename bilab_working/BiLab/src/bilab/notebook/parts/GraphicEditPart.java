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
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
class GraphicEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart 
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
    // allow removal of the associated model element
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new GraphicComponentEditPolicy());
    // allow the creation of connections and 
    // and the reconnection of connections between Shape instances
    installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
      /* (non-Javadoc)
       * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
       */
      protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
        ConnectionCreateCommand cmd 
        = (ConnectionCreateCommand) request.getStartCommand();
        cmd.setTarget((Graphic) getHost().getModel());
        return cmd;
      }
      /* (non-Javadoc)
       * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
       */
      protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
        Graphic source = (Graphic) getHost().getModel();
        int style = ((Integer) request.getNewObjectType()).intValue();
        ConnectionCreateCommand cmd = new ConnectionCreateCommand(source, style);
        request.setStartCommand(cmd);
        return cmd;
      }
      /* (non-Javadoc)
       * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
       */
      protected Command getReconnectSourceCommand(ReconnectRequest request) {
        Connection conn = (Connection) request.getConnectionEditPart().getModel();
        Graphic newSource = (Graphic) getHost().getModel();
        ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
        cmd.setNewSource(newSource);
        return cmd;
      }
      /* (non-Javadoc)
       * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
       */
      protected Command getReconnectTargetCommand(ReconnectRequest request) {
        Connection conn = (Connection) request.getConnectionEditPart().getModel();
        Graphic newTarget = (Graphic) getHost().getModel();
        ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
        cmd.setNewTarget(newTarget);
        return cmd;
      }
    });
  }
  
  /*(non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
   */
  protected IFigure createFigure() {
    IFigure f = createFigureForModel();
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
    } 
    else if (getModel() instanceof RectangularGraphic) {
      return new RectangleFigure();
    } 
    else {
      // if Shapes gets extended the conditions above must be updated
      throw new IllegalArgumentException();
    }
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
  
  private Graphic getCastedModel() {
    return (Graphic) getModel();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
   */
  protected List getModelSourceConnections() {
    return getCastedModel().getSourceConnections();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
   */
  protected List getModelTargetConnections() {
    return getCastedModel().getTargetConnections();
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
   */
  public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
    return new ChopboxAnchor(getFigure());
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
   */
  public ConnectionAnchor getSourceConnectionAnchor(Request request) {
    return new ChopboxAnchor(getFigure());
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
   */
  public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
    return new ChopboxAnchor(getFigure());
  }
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
   */
  public ConnectionAnchor getTargetConnectionAnchor(Request request) {
    return new ChopboxAnchor(getFigure());
  }
  
  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    String prop = evt.getPropertyName();
    if (Graphic.SIZE_PROP.equals(prop) || Graphic.LOCATION_PROP.equals(prop)) {
      refreshVisuals();
    }
    if (Graphic.SOURCE_CONNECTIONS_PROP.equals(prop)) {
      refreshSourceConnections();
    }
    if (Graphic.TARGET_CONNECTIONS_PROP.equals(prop)) {
      refreshTargetConnections();
    }
  }
  
  protected void refreshVisuals() {
    // transfer the size and location from the model instance to the corresponding figure
    Rectangle bounds = new Rectangle(getCastedModel().getLocation(),
        getCastedModel().getSize());
    figure.setBounds(bounds);
    // notify parent container of changed position & location
    // if this line is removed, the XYLayoutManager used by the parent container 
    // (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
    // and will not draw it correctly.
    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure, bounds);
  }
}