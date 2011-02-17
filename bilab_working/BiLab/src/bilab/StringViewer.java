package bilab;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

// simple string viewer (for completeness) 
public class StringViewer extends ViewerBase
{
  public StringViewer(Composite parent) 
  {
    Display display = Display.getCurrent();
    input = "";
    label = new Label(parent, SWT.NONE);
    label.setText("");
    label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
  }

  String input;
  Label label;
  
  
  

  public Point preferedSize()
  {
    return label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
  }

  public Point maximumSize()
  {
    return new Point(SWT.MAX, SWT.MAX);
  }


  public String get_title()
  {
    return "";
  }

  public String get_description()
  {
    return "";
  }

  
  public void dispose()
  {
  }
  
  
  public Control getControl()
  {
    return label;
  }

  public Object getInput()
  {
    return input;
  }

  public void refresh()
  {
    if (input!=null)
      label.setText(input);
    else 
      label.setText("");
  }

  public void setInput(Object input)
  {
    if (input == null) {
      this.input = null;
    }
    else {
      if (input instanceof String)
        this.input = (String)input;
      else
        this.input = input.toString();
    }
    refresh();
  }

  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
  }

}
