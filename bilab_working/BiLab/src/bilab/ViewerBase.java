package bilab;

//import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.ISelectionListener;

import scigol.accessor;



@Sophistication(Sophistication.Developer)
public abstract class ViewerBase extends Viewer implements ISelectionListener
{
  // all subclasses are required to have this constructor (which we'll look for via reflection)
  /* public abstract ViewerBase ViewerBase(Composite parent); */
  
  @accessor
  @Summary("is this viewer in-line or does is use an external viewer component?")
  public boolean get_IsExternal() { return false; }
  
  
  public abstract void dispose();
  
  public abstract Point preferedSize();
  public abstract Point maximumSize();
  
  @accessor
  public abstract String get_title();

  @accessor
  public abstract String get_description();

  
  // provide some default implementations for convenience
  
  
  public void setSelection(ISelection selection, boolean reveal)
  {
  }
  
  
  public ISelection getSelection()
  {
    return StructuredSelection.EMPTY;
  }
}


