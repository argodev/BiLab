package bilab.notebook.model.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;

import bilab.notebook.model.EllipticalGraphic;
import bilab.notebook.model.Graphic;
import bilab.notebook.model.GraphicsDiagram;


/**
 * A command to add a Shape to a ShapeDiagram.
 * The command can be undone or redone.
 * @author Elias Volanakis
 */
public class GraphicCreateCommand extends Command 
{
  /** The new shape. */ 
  private Graphic newShape;
  
  /** ShapeDiagram to add to. */
  private final GraphicsDiagram parent;
  /** A request to create a new Shape. */
  private final CreateRequest request;
  /** True, if newShape was added to parent. */
  private boolean shapeAdded;
  
  /**
   * Create a command that will add a new Shape to a ShapesDiagram.
   * @param parent the ShapesDiagram that will hold the new element
   * @param req     a request to create a new Shape
   * @throws IllegalArgumentException if any parameter is null, or the request
   * 						  does not provide a new Shape instance
   */
  public GraphicCreateCommand(GraphicsDiagram parent, CreateRequest req) {
    if (parent == null || req == null)
      throw new IllegalArgumentException("neither parent nor req may be null");
    
    Object newObj = null;
    try {
      newObj = req.getNewObject();
    } catch (java.lang.ExceptionInInitializerError e) {
      Throwable t = e;
      if (t.getCause() != null) t = t.getCause(); 
      throw new IllegalArgumentException("unable to instantiate requested class:"+req.getNewObjectType(),t);
      
    } catch (Throwable t) {
      throw new IllegalArgumentException("unable to instantiate requested class:"+req.getNewObjectType(),t);
    }
    
    if (!(newObj instanceof Graphic)) 
      throw new IllegalArgumentException("object must be a Graphic (not "+newObj.getClass()+")");
    
    this.parent = parent;
    this.request = req;
    setLabel("shape creation");
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.commands.Command#canUndo()
   */
  public boolean canUndo() {
    return shapeAdded;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.commands.Command#execute()
   */
  public void execute() {
    // Obtain the new Shape instance from the request.
    // This causes the factory stored in the request to create a new instance.
    // The factory is supplied in the palette-tool-entry, see
    // ShapesEditorPaletteFactory#createComponentsGroup()
    newShape = (Graphic) request.getNewObject();
    // Get desired location and size from the request
    newShape.setSize(request.getSize()); // might be null!
    newShape.setLocation(request.getLocation());
    redo();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.commands.Command#redo()
   */
  public void redo() {
    shapeAdded = parent.addChild(newShape);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.commands.Command#undo()
   */
  public void undo() {
    parent.removeChild(newShape);
  }
  
}
