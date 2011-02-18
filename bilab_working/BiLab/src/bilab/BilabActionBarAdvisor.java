/**
* This document is a part of the source code and related artifacts for BiLab, an open source interactive workbench for 
* computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
* Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
* details.
*
* You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to 
* the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* The license is also available at: http://www.gnu.org/copyleft/lgpl.html
*/

// REG: This is the new ActionBarAdvisor implementation that
// is supposed to set up all of the menus and MagicBar controls
package bilab;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.NewWizardMenu;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.ide.IIDEActionConstants;

public class BilabActionBarAdvisor extends ActionBarAdvisor {
	
	// generic actions
    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction closeAllSavedAction;
    private IWorkbenchAction saveAction;
    private IWorkbenchAction saveAllAction;
    private IWorkbenchAction helpContentsAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction openPreferencesAction;
    private IWorkbenchAction saveAsAction;
    private IWorkbenchAction hideShowEditorAction;
    private IWorkbenchAction savePerspectiveAction;
    private IWorkbenchAction resetPerspectiveAction;
    private IWorkbenchAction editActionSetAction;
    private IWorkbenchAction closePerspAction;
    private IWorkbenchAction lockToolBarAction;
    private IWorkbenchAction closeAllPerspsAction;
    private IWorkbenchAction showViewMenuAction;
    private IWorkbenchAction showPartPaneMenuAction;
    private IWorkbenchAction nextPartAction;
    private IWorkbenchAction prevPartAction;
    private IWorkbenchAction nextEditorAction;
    private IWorkbenchAction prevEditorAction;
    private IWorkbenchAction nextPerspectiveAction;
    private IWorkbenchAction prevPerspectiveAction;
    private IWorkbenchAction activateEditorAction;
    private IWorkbenchAction maximizePartAction;
    private IWorkbenchAction minimizePartAction;
    private IWorkbenchAction workbenchEditorsAction;
    private IWorkbenchAction workbookEditorsAction;
    private IWorkbenchAction backwardHistoryAction;
    private IWorkbenchAction forwardHistoryAction;

    // generic retarget actions
    private IWorkbenchAction undoAction;
    private IWorkbenchAction redoAction;
    private IWorkbenchAction cutAction;
    private IWorkbenchAction copyAction;
    private IWorkbenchAction pasteAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction selectAllAction;
    private IWorkbenchAction findAction;
    private IWorkbenchAction printAction;
    private IWorkbenchAction revertAction;
    private IWorkbenchAction refreshAction;
    private IWorkbenchAction propertiesAction;
    private IWorkbenchAction quitAction;
    private IWorkbenchAction moveAction;
    private IWorkbenchAction renameAction;
    private IWorkbenchAction goIntoAction;
    private IWorkbenchAction backAction;
    private IWorkbenchAction forwardAction;
    private IWorkbenchAction upAction;
    private IWorkbenchAction nextAction;
    private IWorkbenchAction previousAction;

    // Bilab specific actions
    private IWorkbenchAction importResourcesAction;
    private IWorkbenchAction exportResourcesAction;
    private IWorkbenchAction newWizardAction;
    private IWorkbenchAction quickStartAction;
    private IWorkbenchAction tipsAndTricksAction;
    private IWorkbenchAction introAction;

    // Bilab-specific re-target actions
    private IWorkbenchAction addBookmarkAction;
    private IWorkbenchAction addTaskAction;
    
    // contribution items
    // @issue should obtain from ContributionItemFactory
    private NewWizardMenu newWizardMenu;
    private IContributionItem pinEditorContributionItem;

    public BilabActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}
	
	protected void makeActions(IWorkbenchWindow window){
		
        newWizardAction = ActionFactory.NEW.create(window);
        register(newWizardAction);

        importResourcesAction = ActionFactory.IMPORT.create(window);
        register(importResourcesAction);

        exportResourcesAction = ActionFactory.EXPORT.create(window);
        register(exportResourcesAction);

        saveAction = ActionFactory.SAVE.create(window);
        register(saveAction);

        saveAsAction = ActionFactory.SAVE_AS.create(window);
        register(saveAsAction);

        saveAllAction = ActionFactory.SAVE_ALL.create(window);
        register(saveAllAction);

        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);

        redoAction = ActionFactory.REDO.create(window);
        register(redoAction);

        cutAction = ActionFactory.CUT.create(window);
        register(cutAction);

        copyAction = ActionFactory.COPY.create(window);
        register(copyAction);

        pasteAction = ActionFactory.PASTE.create(window);
        register(pasteAction);

        printAction = ActionFactory.PRINT.create(window);
        register(printAction);

        selectAllAction = ActionFactory.SELECT_ALL.create(window);
        register(selectAllAction);

        findAction = ActionFactory.FIND.create(window);
        register(findAction);

        closeAction = ActionFactory.CLOSE.create(window);
        register(closeAction);

        closeAllAction = ActionFactory.CLOSE_ALL.create(window);
        register(closeAllAction);

        closeAllSavedAction = ActionFactory.CLOSE_ALL_SAVED.create(window);
        register(closeAllSavedAction);

        helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);
        register(helpContentsAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        openPreferencesAction = ActionFactory.PREFERENCES.create(window);
        register(openPreferencesAction);

        addBookmarkAction = IDEActionFactory.BOOKMARK.create(window);
        register(addBookmarkAction);

        addTaskAction = IDEActionFactory.ADD_TASK.create(window);
        register(addTaskAction);

        deleteAction = ActionFactory.DELETE.create(window);
        register(deleteAction);

        // Actions for invisible accelerators
        showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(window);
        register(showViewMenuAction);

        showPartPaneMenuAction = ActionFactory.SHOW_PART_PANE_MENU.create(window);
        register(showPartPaneMenuAction);

        nextEditorAction = ActionFactory.NEXT_EDITOR.create(window);
        prevEditorAction = ActionFactory.PREVIOUS_EDITOR.create(window);
        ActionFactory.linkCycleActionPair(nextEditorAction, prevEditorAction);
        register(nextEditorAction);
        register(prevEditorAction);

        nextPartAction = ActionFactory.NEXT_PART.create(window);
        prevPartAction = ActionFactory.PREVIOUS_PART.create(window);
        ActionFactory.linkCycleActionPair(nextPartAction, prevPartAction);
        register(nextPartAction);
        register(prevPartAction);

        nextPerspectiveAction = ActionFactory.NEXT_PERSPECTIVE.create(window);
        prevPerspectiveAction = ActionFactory.PREVIOUS_PERSPECTIVE.create(window);
        ActionFactory.linkCycleActionPair(nextPerspectiveAction,
                prevPerspectiveAction);
        register(nextPerspectiveAction);
        register(prevPerspectiveAction);

        activateEditorAction = ActionFactory.ACTIVATE_EDITOR.create(window);
        register(activateEditorAction);

        maximizePartAction = ActionFactory.MAXIMIZE.create(window);
        register(maximizePartAction);

		minimizePartAction = ActionFactory.MINIMIZE.create(window);
		register(minimizePartAction);
        
        workbenchEditorsAction = ActionFactory.SHOW_OPEN_EDITORS.create(window);
        register(workbenchEditorsAction);

        workbookEditorsAction = ActionFactory.SHOW_WORKBOOK_EDITORS.create(window);
        register(workbookEditorsAction);

        hideShowEditorAction = ActionFactory.SHOW_EDITOR.create(window);
        register(hideShowEditorAction);
        savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE.create(window);
        register(savePerspectiveAction);
        editActionSetAction = ActionFactory.EDIT_ACTION_SETS.create(window);
        register(editActionSetAction);
        lockToolBarAction = ActionFactory.LOCK_TOOL_BAR.create(window);
        register(lockToolBarAction);
        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        register(resetPerspectiveAction);
        closePerspAction = ActionFactory.CLOSE_PERSPECTIVE.create(window);
        register(closePerspAction);
        closeAllPerspsAction = ActionFactory.CLOSE_ALL_PERSPECTIVES.create(window);
        register(closeAllPerspsAction);

        forwardHistoryAction = ActionFactory.FORWARD_HISTORY.create(window);
        register(forwardHistoryAction);

        backwardHistoryAction = ActionFactory.BACKWARD_HISTORY.create(window);
        register(backwardHistoryAction);

        revertAction = ActionFactory.REVERT.create(window);
        register(revertAction);

        refreshAction = ActionFactory.REFRESH.create(window);
        register(refreshAction);

        propertiesAction = ActionFactory.PROPERTIES.create(window);
        register(propertiesAction);

        quitAction = ActionFactory.QUIT.create(window);
        register(quitAction);

        moveAction = ActionFactory.MOVE.create(window);
        register(moveAction);

        renameAction = ActionFactory.RENAME.create(window);
        register(renameAction);

        goIntoAction = ActionFactory.GO_INTO.create(window);
        register(goIntoAction);

        backAction = ActionFactory.BACK.create(window);
        register(backAction);

        forwardAction = ActionFactory.FORWARD.create(window);
        register(forwardAction);

        upAction = ActionFactory.UP.create(window);
        register(upAction);

        nextAction = ActionFactory.NEXT.create(window);
        register(nextAction);

        previousAction = ActionFactory.PREVIOUS.create(window);
        register(previousAction);

        if (window.getWorkbench().getIntroManager().hasIntro()) {
            introAction = ActionFactory.INTRO.create(window);
            register(introAction);
        }

        pinEditorContributionItem = ContributionItemFactory.PIN_EDITOR.create(window);
	}
	
	protected void fillMenuBar(IMenuManager menuBar) {
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(createWindowMenu());
		menuBar.add(createHelpMenu());
	}
	
	private MenuManager createFileMenu() {
		MenuManager menu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
       
		// setup the pop-out sub-menu for New --> Project/Other
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		{
			// create the new sub-menu
			String newText = "New";
			String newId = ActionFactory.NEW.getId();
			MenuManager newMenu = new MenuManager(newText, newId);
			
			// some stuff was here in the old model
			
			newMenu.add(new Separator(newId));
			this.newWizardMenu = new NewWizardMenu(getWindow());
			newMenu.add(this.newWizardMenu);
			newMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			menu.add(newMenu);
		}
		
		// open file menu
		menu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
		menu.add(new Separator());
		
		// Close | Close All
        menu.add(closeAction);
        menu.add(closeAllAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.CLOSE_EXT));
        menu.add(new Separator());

        // Save | Save As | Save All | Revert
        menu.add(saveAction);
        menu.add(saveAsAction);
        menu.add(saveAllAction);
        menu.add(revertAction);
        menu.add(new Separator());

        // Move | Rename | Refresh
        menu.add(moveAction);
        menu.add(renameAction);
        menu.add(refreshAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
        menu.add(new Separator());

        // Print
        menu.add(printAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
        menu.add(new Separator());
        
        menu.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
        menu.add(new Separator());
        
        // Import | Export
        menu.add(importResourcesAction);
        menu.add(exportResourcesAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.IMPORT_EXT));
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new Separator());

        // Properties | Most Recently Used...
        menu.add(propertiesAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.MRU));
        menu.add(new Separator());
        
        // Quit
        menu.add(quitAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
        
        return menu;
	}
	
	private MenuManager createEditMenu() {
		MenuManager menu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));

        menu.add(undoAction);
        menu.add(redoAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.UNDO_EXT));
        menu.add(new Separator());

        menu.add(cutAction);
        menu.add(copyAction);
        menu.add(pasteAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.CUT_EXT));
        menu.add(new Separator());

        menu.add(deleteAction);
        menu.add(selectAllAction);
        menu.add(new Separator());

        menu.add(findAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));
        menu.add(new Separator());

        menu.add(addBookmarkAction);
        menu.add(addTaskAction);
        menu.add(new GroupMarker(IWorkbenchActionConstants.ADD_EXT));

        menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        
        return menu;
	}
	
	private MenuManager createWindowMenu() {
		MenuManager menu = new MenuManager("Window", IWorkbenchActionConstants.M_WINDOW); 

        IWorkbenchAction action = ActionFactory.OPEN_NEW_WINDOW.create(getWindow());
        action.setText("New Window");
        menu.add(action);
        menu.add(new Separator());
        
        addPerspectiveActions(menu);
        menu.add(new Separator());

        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "end"));
        menu.add(openPreferencesAction);

        menu.add(ContributionItemFactory.OPEN_WINDOWS.create(getWindow()));
        return menu;
	}
	
	private MenuManager createHelpMenu() {
		MenuManager menu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
		addSeparatorOrGroupMarker(menu, "group.intro");
		
		// See if a welcome or intro page is specified
		if (introAction != null)
			menu.add(introAction);
		else if (quickStartAction != null)
			menu.add(quickStartAction);
		menu.add(new GroupMarker("group.intro.ext"));
		addSeparatorOrGroupMarker(menu, "group.main");
		menu.add(helpContentsAction);

		// See if a tips and tricks page is specified
		if (tipsAndTricksAction != null)
			menu.add(tipsAndTricksAction);
		
		// HELP_START should really be the first item, but it was after
		// quickStartAction and tipsAndTricksAction in 2.1.
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
		menu.add(new GroupMarker("group.main.ext"));
		addSeparatorOrGroupMarker(menu, "group.tutorials");
		addSeparatorOrGroupMarker(menu, "group.tools");
		addSeparatorOrGroupMarker(menu, "group.updates");
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
		addSeparatorOrGroupMarker(menu, IWorkbenchActionConstants.MB_ADDITIONS);
		
		// about should always be at the bottom
		menu.add(new Separator("group.about"));
		menu.add(aboutAction);
		menu.add(new GroupMarker("group.about.ext"));

        return menu;
	}
	
	private IWorkbenchWindow getWindow() {
        return this.getActionBarConfigurer().getWindowConfigurer().getWindow();
    }
	
	private void addPerspectiveActions(MenuManager menu) {
        String openText = "OpenPerspective";
        MenuManager changePerspMenuMgr = new MenuManager(openText,
                "openPerspective"); //$NON-NLS-1$
        IContributionItem changePerspMenuItem = ContributionItemFactory.PERSPECTIVES_SHORTLIST
                .create(getWindow());
        changePerspMenuMgr.add(changePerspMenuItem);
        menu.add(changePerspMenuMgr);

        MenuManager showViewMenuMgr = new MenuManager("Show View", "showView");
        IContributionItem showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST
                .create(getWindow());
        showViewMenuMgr.add(showViewMenu);
        menu.add(showViewMenuMgr);
        
        menu.add(new Separator());
        menu.add(editActionSetAction);
        menu.add(savePerspectiveAction);
        menu.add(resetPerspectiveAction);
        menu.add(closePerspAction);
        menu.add(closeAllPerspsAction);
    }	
	
    /**
	 * Adds a <code>GroupMarker</code> or <code>Separator</code> to a menu.
	 * The test for whether a separator should be added is done by checking for
	 * the existence of a preference matching the string
	 * useSeparator.MENUID.GROUPID that is set to <code>true</code>.
	 * 
	 * @param menu
	 *            the menu to add to
	 * @param string
	 *            the group id for the added separator or group marker
	 */
	private void addSeparatorOrGroupMarker(MenuManager menu, String groupId) {
		String prefId = "useSeparator." + menu.getId() + "." + groupId; //$NON-NLS-1$ //$NON-NLS-2$
		boolean addExtraSeparators = BilabPlugin.getDefault()
				.getPreferenceStore().getBoolean(prefId);
		if (addExtraSeparators) {
			menu.add(new Separator(groupId));
		} else {
			menu.add(new GroupMarker(groupId));
		}
	}
	
	protected void fillCoolBar(ICoolBarManager coolBar) {
//        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
//        coolBar.add(new ToolBarContributionItem(toolbar, "main"));  
//        toolbar.add(openViewAction);
//        toolbar.add(messagePopupAction);
        
        // Set up the context Menu
        IMenuManager popUpMenu = new MenuManager();
        popUpMenu.add(new ActionContributionItem(lockToolBarAction));
        popUpMenu.add(new ActionContributionItem(editActionSetAction));
        coolBar.setContextMenuManager(popUpMenu);
        
        coolBar.add(new GroupMarker(IIDEActionConstants.GROUP_FILE));
        { // File Group
            IToolBarManager fileToolBar = new ToolBarManager(coolBar
                    .getStyle());
            fileToolBar.add(new Separator(IWorkbenchActionConstants.NEW_GROUP));
            fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
            fileToolBar.add(new GroupMarker(
                    IWorkbenchActionConstants.SAVE_GROUP));
            fileToolBar.add(saveAction);
            fileToolBar
                    .add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
            fileToolBar.add(printAction);
            fileToolBar
                    .add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));

            fileToolBar
                    .add(new Separator(IWorkbenchActionConstants.BUILD_GROUP));
            fileToolBar
                    .add(new GroupMarker(IWorkbenchActionConstants.BUILD_EXT));
            fileToolBar.add(new Separator(
                    IWorkbenchActionConstants.MB_ADDITIONS));

            // Add to the cool bar manager
            coolBar.add(new ToolBarContributionItem(fileToolBar,
                    IWorkbenchActionConstants.TOOLBAR_FILE));
        }

        coolBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        coolBar.add(new GroupMarker(IIDEActionConstants.GROUP_NAV));
        { // Navigate group
            IToolBarManager navToolBar = new ToolBarManager(coolBar
                    .getStyle());
            navToolBar.add(new Separator(
                    IWorkbenchActionConstants.HISTORY_GROUP));
            navToolBar
                    .add(new GroupMarker(IWorkbenchActionConstants.GROUP_APP));
            navToolBar.add(backwardHistoryAction);
            navToolBar.add(forwardHistoryAction);
            navToolBar.add(new Separator(IWorkbenchActionConstants.PIN_GROUP));
            navToolBar.add(pinEditorContributionItem);

            // Add to the cool bar manager
            coolBar.add(new ToolBarContributionItem(navToolBar,
                    IWorkbenchActionConstants.TOOLBAR_NAVIGATE));
        }

        coolBar.add(new GroupMarker(IWorkbenchActionConstants.GROUP_EDITOR));
    }
}