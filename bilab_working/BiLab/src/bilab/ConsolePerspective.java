package bilab;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ConsolePerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout page) {
		
		page.setEditorAreaVisible(false);
		
		String editorArea = page.getEditorArea();
		
		IFolderLayout folder = page.createFolder("bilab.MainView", IPageLayout.BOTTOM, 1.0f, editorArea);
		
		folder.addView("bilab.ConsoleView");
		IViewLayout consoleView = page.getViewLayout("bilab.ConsoleView");
		consoleView.setCloseable(false);
		
		folder.addView("bilab.BrowserView");
		
		//folder.addView("bilab.TextView");
		
		// then environment navigator view 
		page.addView("bilab.EnvNavigatorView", IPageLayout.RIGHT, 0.7f, "bilab.ConsoleView");
		IViewLayout navView = page.getViewLayout("bilab.EnvNavigatorView");
		navView.setCloseable(false);
		
		// the value viewer
		page.addView("bilab.ValueView", IPageLayout.BOTTOM, 0.5f, "bilab.EnvNavigatorView");
		IViewLayout valueView = page.getViewLayout("bilab.ValueView");
		valueView.setCloseable(false);
	}
}