package bilab;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;

public class BrowserView extends ViewPart {

	public BrowserView() {
	}

	public void createPartControl(Composite parent) {
	  top = new Composite(parent, SWT.NONE);
	  top.setLayout(new FillLayout());
	  
	  area = new Composite(top, SWT.BORDER);
	  FormLayout vlayout = new FormLayout();
	  area.setLayout(vlayout);

	  addressBar = new Composite(area, SWT.NONE);
	  
	  FormData addressBarData = new FormData();
	  addressBarData.left = new FormAttachment(0,0);
	  addressBarData.top = new FormAttachment(0,0);
	  addressBarData.right = new FormAttachment(100,0);
	  addressBar.setLayoutData(addressBarData);
	  
	  RowLayout hlayout = new RowLayout();
	  hlayout.type = SWT.HORIZONTAL;
	  hlayout.fill = false;
	  hlayout.justify = false;
	  hlayout.wrap = true;
	  addressBar.setLayout(hlayout);
	  
	  ToolBar toolBar = new ToolBar(addressBar,SWT.FLAT);
	  
	  back = new ToolItem(toolBar,SWT.NONE);
	  setImage(back, "icons/left_small", "Back");
	  
	  forward = new ToolItem(toolBar, SWT.FLAT);
      setImage(forward, "icons/right_small", "Forward");
	  
	  stop = new ToolItem(toolBar, SWT.FLAT);
      setImage(stop, "icons/stop_small", "Stop");
	  
	  reload = new ToolItem(toolBar, SWT.FLAT);
      setImage(reload, "icons/reload_small", "Reload");
	  
	  home = new ToolItem(toolBar, SWT.FLAT);
      setImage(home, "icons/home_small", "Home");
	  

	  address = new Text(addressBar, SWT.BORDER);
	  //address.setSize(400,24);
	  address.addKeyListener(new InputListener(this));
	  
	  viewer = new HTMLViewer(area);
	  
	  FormData viewerData = new FormData();
	  viewerData.top = new FormAttachment(addressBar);
	  viewerData.bottom = new FormAttachment(100,0);
	  viewerData.left = new FormAttachment(0,0);
	  viewerData.right = new FormAttachment(100,0);
	  viewer.getControl().setLayoutData(viewerData);
	  
	  //setUrl("http://robgillen.me");
	  setUrl("http://www.ornl.gov/ornlhome/high_performance_computing.shtml");
	}

	Composite top, area, addressBar;
	Text address;
	HTMLViewer viewer;
	ToolItem back, forward, stop, reload, home;
	
	

	protected void setImage(ToolItem b, String resourceNamePrefix, String toolTip)
	{
	  try {
	    b.setImage(new Image(Display.getCurrent(), BilabPlugin.findResourceStream(resourceNamePrefix+".gif")));
	    b.setDisabledImage(new Image(Display.getCurrent(), BilabPlugin.findResourceStream(resourceNamePrefix+"_disabled.png")));
	    b.setToolTipText(toolTip);
	  } catch (java.io.IOException e) {
	    b.setText(toolTip);
	  }
	}

	
	public void setFocus() {
		address.setFocus();
		address.selectAll();
	}
	
	
	public void setUrl(String url)
	{
	  address.setText(url);
	  viewer.setInput(url);
	}
	
	
	
	protected void handleInputKeyEvent(KeyEvent e)
	{
	  if (e.stateMask == SWT.NONE) {
	    if (e.keyCode == SWT.CR) {
	      setUrl( address.getText() );
	      viewer.getControl().setFocus(); // take focus off address
	    }
	  }
	}
	      
	
	
	
	protected class InputListener implements KeyListener
	  {
	    public InputListener(BrowserView v)
	    {
	      view = v;
	    }
	    
	    public void keyPressed(KeyEvent e)
	    {
	      view.handleInputKeyEvent(e);
	    }
	    
	    public void keyReleased(KeyEvent e)
	    {
	    }
	    
	    
	    BrowserView view;
	  }
}