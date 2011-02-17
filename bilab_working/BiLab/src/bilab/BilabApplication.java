// REG: This is document #1 in the app.. it sets up the RCP and 
// allows things to work in the Eclipse environment.
// This has been updated from the archive and is using the new
// IApplication interface
package bilab;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class BilabApplication implements IApplication {

	/** {@inheritDoc} */
	public final Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();

		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new BilabWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			} else {
				return IApplication.EXIT_OK;
			}

		} finally {
			display.dispose();
		}
	}

	/** {@inheritDoc} */
	public final void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return;
		}
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed()) {
					workbench.close();
				}
			}
		});
	}
}