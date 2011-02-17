// REG: This file is triggered/referenced by the BilabWorkbenchAdvisor
// and contains the data for setting up the window properties as well
// as calling the ActionBarAdvisor for setting up the menu bars
package bilab;

import org.eclipse.ui.application.*;

public class BilabWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public BilabWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}
	
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new BilabActionBarAdvisor(configurer);
	}
	
	public void preWindowOpen() {
		super.preWindowOpen();
		
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		
		// always open the window maximized
		//configurer.getWindow().getShell().setMaximized(true);
		
	    configurer.setShowCoolBar(true); // not sure what this controls...
	    configurer.setShowStatusLine(true);
	    configurer.setShowMenuBar(true); // unneeded
	    
	    // enable the perspective dialog at the right side of the button bar
	    configurer.setShowPerspectiveBar(true);
	    	    
	    configurer.setShowProgressIndicator(true);
	    
	    // set the application title (shown in the title bar)
	    configurer.setTitle("BiLab - v0.1.3 Prototype (reg)");
	}	
}