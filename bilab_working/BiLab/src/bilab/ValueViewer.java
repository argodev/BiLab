package bilab;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.jface.viewers.*;
import org.openscience.jmol.viewer.JmolViewer;

import scigol.*;



// displays a scigol Value
public class ValueViewer extends Viewer implements ISelectionListener
{
  
  public ValueViewer(Composite parent)
  {
    //this.parent = parent;
    
    top = new Composite(parent, SWT.NONE);
    top.setLayout(new FillLayout());
    
    child = new Composite(top, SWT.NONE);
    child.setLayout(new FillLayout());
    
    label = new Label(child, SWT.NONE);
    viewer = null;
    input = null;
    refresh();
  
  }
  
  
  public Control getControl()
  {
    return top;
  }
  
  
  public Point preferedSize()
  {
    if (viewer != null)
      return viewer.preferedSize();
    return top.computeSize(SWT.DEFAULT, SWT.DEFAULT);
  }
  
  public Point maximumSize()
  {
    if (viewer != null)
      return viewer.maximumSize();
    return top.computeSize(SWT.DEFAULT, SWT.DEFAULT);
  }
  

  
  public String get_title()
  {
    if (viewer != null)
      return viewer.get_title();
    return "";
  }
  
  public String get_description()
  {
    if (viewer != null)
      return viewer.get_description();
    return "";
  }

  
  
  public Object getInput()
  {
    return input;
  }
  
  
  public void setInput(Object input)
  {
    if (input == this.input) return; // didn't change
    
    if (input instanceof Value)
      this.input = (Value)input;
    else {
return;//!!!      
      //this.input = null;
      //Notify.DevWarning(this,"non-Value input passed to ValueViewer.setInput()");
    }
    
    // first, dispose of the current viewer, if any
    if (!child.isDisposed()) {
      child.dispose();
      label = null;
    }
    if (viewer != null) {
      viewer.removeSelectionChangedListener(viewerListener);
      viewer.dispose();
      viewer = null;
    }
    
    child = new Composite(top, SWT.NONE);
    child.setLayout(new FillLayout());
    
    
    // instantiate the approproate type of viewer for the input type
    //  (if any, otherwise, just use a Label to display the string representation)
    if (input != null) {
      Object model = TypeSpec.unwrapAnyOrNum(input);
      viewer = BilabPlugin.instantiateViewer(TypeSpec.typeOf(model), child);
    }
    
    if (viewer == null) // no viewer found, use Label
      label = new Label(child, SWT.NONE);
    else
      viewer.addSelectionChangedListener(viewerListener);

    refresh();
  }
  
  
  public ISelection getSelection()
  {
    if (viewer != null)
      return viewer.getSelection(); 
    return StructuredSelection.EMPTY;
  }
  
  public void setSelection(ISelection selection, boolean reveal)
  {
    if (viewer != null)
      viewer.setSelection(selection,reveal);
  }
  
  
  // we register this with the viewer as a selection change listener, and re-fire those events
  // via our Viewer.fileSelectionChanged()
  private ISelectionChangedListener viewerListener = new ISelectionChangedListener() {
    public void selectionChanged(SelectionChangedEvent event) {
      fireSelectionChanged(event);
    }
  };
  
  
  
  public void selectionChanged(IWorkbenchPart part, ISelection selection) 
  {

    if (selection instanceof ValueSelection) {
      ValueSelection vs = (ValueSelection)selection;
      if (!vs.isEmpty()) {
//Notify.DevInfo(this,"got value:"+vs.value+ " instanceof Value?"+(vs.value instanceof Value));
        
        // change the value being viewed, if there is a viewer for it
        Object model = TypeSpec.unwrapAnyOrNum(vs.value);
        if (BilabPlugin.existsViewer(TypeSpec.typeOf(model))) {
          try {
            Notify.devInfo(this,"setting viewer for type "+TypeSpec.typeOf(model)+" to input "+model.toString());
            setInput(vs.value);
          } catch (Exception e) {
            Notify.devWarning(this,"exception setting input:"+e);
            e.printStackTrace();
          }
          return;
        }
      }
    }

    // pass on selection
    if (viewer != null) 
      viewer.selectionChanged(part, selection);
    
  }
  

  
  public void refresh()
  {
    if (viewer == null) {
      // update label
      if (input != null) {
        Object v = TypeSpec.unwrapAnyOrNum(input);
        if (v instanceof IUserText) 
          label.setText(((IUserText)v).get_DetailText());
        else 
          label.setText(v.toString());
      }
      else
        label.setText("<no value>");
    } 
    else { // update viewer
      viewer.setInput(TypeSpec.unwrapAnyOrNum(input)); 
      viewer.refresh();
    }
    top.layout(true);
    top.redraw();
  }
  
  
  //private Composite parent;
  private Composite top;
  private Composite child;
  private Label label;
  private ViewerBase viewer; // viewer or null
  
  private Value input = null;
}



