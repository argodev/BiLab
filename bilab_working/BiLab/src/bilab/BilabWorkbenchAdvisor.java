// REG: This is the second file in the chain, and sets up the other advisors.
// This version is heavily modified from the archive in that much of the
// functionality that had been implemented here is now moved into the
// new BilabWorkbenchWindowAdvisior as well as the new
// BilabActionBarAdvisor (newer interfaces than what had been implemented.
package bilab;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class BilabWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "bilab.consolePerspective";
	
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new BilabWorkbenchWindowAdvisor(configurer);
	}
	
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	// WorkbenchWindowAdvisor.preWindowOpen should be used???
  
 // public void preWindowOpen(IWorkbenchWindowConfigurer configurer) {
 //   super.preWindowOpen(configurer);
    //configurer.setInitialSize(new Point(1600-48, 1200-48));
    //configurer.setShowCoolBar(true);
    //configurer.setShowStatusLine(true);
    //configurer.setShowMenuBar(true);
    //configurer.setShowPerspectiveBar(true);
    //configurer.setShowProgressIndicator(true);
    //configurer.setTitle("BiLab - v0.1.3 Prototype (reg)");
 // }

 

//  public void eventLoopException(Throwable t)
//  {
	// try to get the message from the exception
//    String m = t.getMessage();
    
    // if we didn't get it, let's try to get it another way...
//    if (m==null) {
//      t = t.getCause();
//      m = t.getMessage();
//    }
    
    // if we still didn't get it, let's return the class at least
//    if (m==null) m = t.getClass().toString();

    // display the error
//    Notify.userWarning(this,"an exception occured within the Bilab interface: " + m);
    
    // dump the stack trace
//    t.printStackTrace(); ///!!! appropriate?
//  }  
}