package bilab;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.browser.*;

import java.net.URL;

// View HTML content
public class HTMLViewer extends ViewerBase
{
  public HTMLViewer(Composite parent)
  {
    top = new Composite(parent, SWT.NONE);
    top.setLayout(new FillLayout());
    
    browser = new Browser(top, SWT.BORDER);
    browser.setText("<html><title>Welcome to Bilab</title><body><h1>Welcome to Bilab.</h1></body></html>");
    input = "";
  }
  
  Composite top;
  Browser browser;
  
  String input; // url
  
  public void dispose()
  {
    // TODO Auto-generated method stub
  }

  public Point preferedSize()
  {
    return new Point(SWT.DEFAULT,400);
  }

  public Point maximumSize()
  {
    return new Point(SWT.MAX, SWT.MAX);
  }

  public String get_title()
  {
    return "Web";
  }

  public String get_description()
  {
    return "Web Browser";
  }

  public Control getControl()
  {
    return top;
  }

  public Object getInput()
  {
    return input;
  }

  public void refresh()
  {
//    browser.refresh();
  }

  public void setInput(Object input)
  {
    if (input instanceof String)
      this.input = (String)input;
    else if (input instanceof URL)
      this.input = ((URL)input).toString();
    else
      this.input = "";
    browser.setUrl(this.input);
    Notify.devInfo(this,"HTMLViewer set URL "+this.input);
  }

  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    // TODO Auto-generated method stub
  }
}