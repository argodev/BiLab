package bilab;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class NotebookPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout page) {
		
		page.setEditorAreaVisible(true);
		
		String editorArea = page.getEditorArea();
		
		
	}
}
