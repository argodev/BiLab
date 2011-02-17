package bilab;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;

// simple viewer for sequence strings (breaks into columns) 
public class SeqStringViewer extends ViewerBase
{
  public SeqStringViewer(Composite parent) 
  {
    this.parent = parent;
    Display display = Display.getCurrent();
    input = null;
    label = new Label(parent, SWT.NONE);
    label.setText("");
    label.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
    label.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
    
    FontData fontData = new FontData("Courier New",10,SWT.BOLD);
    labelFont = new Font(display, fontData);
    label.setFont(labelFont);
    
    fontPixelWidth = 10; // hard code for now!!! (need to get from FontMetrics somehow)
  }

  seq input;
  Label label;
  Font labelFont;
  int fontPixelWidth;
  Composite parent;
  
  
  

  public Point preferedSize()
  {
    return label.computeSize( (60+6)*fontPixelWidth, SWT.DEFAULT);
  }

  public Point maximumSize()
  {
    return new Point((60+6)*fontPixelWidth, SWT.MAX);
  }


  public String get_title()
  {
    if (input != null)
      return input.get_name();
    else
      return "";
  }

  public String get_description()
  {
    if (input != null)
      return input.get_ShortText();
    else
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
      label.setText(input.get_sequence());
    else 
      label.setText("");
  }

  public void setInput(Object input)
  {
    if (input == null) {
      this.input = null;
    }
    else {
      if (input instanceof seq)
        this.input = (seq)input;
      else
        this.input = null;
    }
    refresh();
  }

  
  
  
  
  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
  }

}
