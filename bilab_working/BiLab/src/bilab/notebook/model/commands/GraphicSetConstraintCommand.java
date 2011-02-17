package bilab.notebook.model.commands;

import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

import bilab.notebook.model.Graphic;

/**
 * A command to resize and/or move a shape.
 * The command can be undone or redone.
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
 * @param shape	the shape to manipulate
 * @param req		the move and resize request
 * @param newBounds the new size and location
 * @throws IllegalArgumentException if any of the parameters is null
 */
public GraphicSetConstraintCommand(Graphic shape, ChangeBoundsRequest req, 
		Rectangle newBounds) {
	if (shape == null || req == null || newBounds == null) {
		throw new IllegalArgumentException();
	}
	this.shape = shape;
	this.request = req;
	this.newBounds = newBounds.getCopy();
	setLabel("move / resize");
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	Object type = request.getType();
	// make sure the Request is of a type we support:
	return (RequestConstants.REQ_MOVE.equals(type)
			|| RequestConstants.REQ_MOVE_CHILDREN.equals(type) 
			|| RequestConstants.REQ_RESIZE.equals(type)
			|| RequestConstants.REQ_RESIZE_CHILDREN.equals(type));
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	oldBounds = new Rectangle(shape.getLocation(), shape.getSize());
	redo();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	shape.setSize(newBounds.getSize());
	shape.setLocation(newBounds.getLocation());
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	shape.setSize(oldBounds.getSize());
	shape.setLocation(oldBounds.getLocation());
}
}
