package bilab;


import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;


//!!! probably depricated - replace with editor (not a view)
public class TextView extends ViewPart 
{
	Text text;
	Composite top, inner;
	ScrolledComposite scrolled;

	 
	public TextView() {
	}

	public void createPartControl(Composite parent) {
	  top = new Composite(parent, SWT.NONE);
	  top.setLayout(new FillLayout());
	  
	  scrolled = new ScrolledComposite(top, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	  
	  //inner = new Composite(scrolled, SWT.NONE);
	  //inner.setLayout(new FillLayout());
	  //scrolled.setContent(inner);
	  
	  //inner.setBounds(0,0,640,1000);
	  //inner.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	  //scrolled.setMinSize(inner.computeSize(SWT.DEFAULT, SWT.DEFAULT, false));

	  text = new Text(scrolled, SWT.MULTI);
	  text.setBounds(0,0,640,1000);
	  
      FontData fontData = new FontData("Courier New",10,SWT.NORMAL);
	  Font consoleFont = new Font(Display.getCurrent(), fontData);
	  text.setFont(consoleFont);

	  scrolled.setContent(text);
	  scrolled.setMinSize(text.computeSize(SWT.DEFAULT, SWT.DEFAULT, false));
	}



	public void setFocus() {
		scrolled.setFocus();
	}
}