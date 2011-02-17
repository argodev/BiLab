package bilab;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;

public class UnimplementedAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	public UnimplementedAction() {
	}

	public void run(IAction action) {
		MessageDialog.openInformation(
			window.getShell(),
			"BiLab Plug-in",
			"No action has been implemented");
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}