/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
 */

// package bilab;
//
// import org.eclipse.jface.action.ActionContributionItem;
// import org.eclipse.jface.action.GroupMarker;
// import org.eclipse.jface.action.IAction;
// import org.eclipse.jface.action.IContributionItem;
// import org.eclipse.jface.action.ICoolBarManager;
// import org.eclipse.jface.action.IMenuManager;
// import org.eclipse.jface.action.IToolBarManager;
// import org.eclipse.jface.action.MenuManager;
// import org.eclipse.jface.action.Separator;
// import org.eclipse.jface.action.ToolBarContributionItem;
// import org.eclipse.jface.action.ToolBarManager;
// import org.eclipse.ui.IWorkbenchActionConstants;
// import org.eclipse.ui.IWorkbenchWindow;
// import org.eclipse.ui.actions.ActionFactory;
// import org.eclipse.ui.actions.ContributionItemFactory;
// import org.eclipse.ui.actions.NewWizardMenu;
// import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
// import org.eclipse.ui.application.IActionBarConfigurer;
// import org.eclipse.ui.application.IWorkbenchConfigurer;
// import org.eclipse.ui.ide.IDEActionFactory;
// import org.eclipse.ui.ide.IIDEActionConstants;
//
// /**
// * Adds actions to a workbench window.
// */
// public final class BilabActionBuilder {
// private IWorkbenchWindow window;
//
// /**
// * A convenience variable and method so that the actionConfigurer doesn't need
// to
// * get passed into registerGlobalAction every time it's called.
// */
// private IActionBarConfigurer actionBarConfigurer;
//
// // generic actions
// private IWorkbenchAction closeAction;
//
// private IWorkbenchAction closeAllAction;
//
// private IWorkbenchAction closeAllSavedAction;
//
// private IWorkbenchAction saveAction;
//
// private IWorkbenchAction saveAllAction;
//
// private IWorkbenchAction helpContentsAction;
//
// private IWorkbenchAction aboutAction;
//
// private IWorkbenchAction openPreferencesAction;
//
// private IWorkbenchAction saveAsAction;
//
// private IWorkbenchAction hideShowEditorAction;
//
// private IWorkbenchAction savePerspectiveAction;
//
// private IWorkbenchAction resetPerspectiveAction;
//
// private IWorkbenchAction editActionSetAction;
//
// private IWorkbenchAction closePerspAction;
//
// private IWorkbenchAction lockToolBarAction;
//
// private IWorkbenchAction closeAllPerspsAction;
//
// private IWorkbenchAction showViewMenuAction;
//
// private IWorkbenchAction showPartPaneMenuAction;
//
// private IWorkbenchAction nextPartAction;
//
// private IWorkbenchAction prevPartAction;
//
// private IWorkbenchAction nextEditorAction;
//
// private IWorkbenchAction prevEditorAction;
//
// private IWorkbenchAction nextPerspectiveAction;
//
// private IWorkbenchAction prevPerspectiveAction;
//
// private IWorkbenchAction activateEditorAction;
//
// private IWorkbenchAction maximizePartAction;
//
// private IWorkbenchAction minimizePartAction;
//
// private IWorkbenchAction workbenchEditorsAction;
//
// private IWorkbenchAction workbookEditorsAction;
//
// private IWorkbenchAction backwardHistoryAction;
//
// private IWorkbenchAction forwardHistoryAction;
//
// // generic retarget actions
// private IWorkbenchAction undoAction;
//
// private IWorkbenchAction redoAction;
//
// private IWorkbenchAction cutAction;
//
// private IWorkbenchAction copyAction;
//
// private IWorkbenchAction pasteAction;
//
// private IWorkbenchAction deleteAction;
//
// private IWorkbenchAction selectAllAction;
//
// private IWorkbenchAction findAction;
//
// private IWorkbenchAction printAction;
//
// private IWorkbenchAction revertAction;
//
// private IWorkbenchAction refreshAction;
//
// private IWorkbenchAction propertiesAction;
//
// private IWorkbenchAction quitAction;
//
// private IWorkbenchAction moveAction;
//
// private IWorkbenchAction renameAction;
//
// private IWorkbenchAction goIntoAction;
//
// private IWorkbenchAction backAction;
//
// private IWorkbenchAction forwardAction;
//
// private IWorkbenchAction upAction;
//
// private IWorkbenchAction nextAction;
//
// private IWorkbenchAction previousAction;
//
// // Bilab specific actions
// private IWorkbenchAction importResourcesAction;
//
// private IWorkbenchAction exportResourcesAction;
//
// private IWorkbenchAction newWizardAction;
//
// // private IWorkbenchAction newWizardDropDownAction;
//
// private IWorkbenchAction quickStartAction;
//
// private IWorkbenchAction tipsAndTricksAction;
//
// private IWorkbenchAction introAction;
//
//
// // Bilab-specific retarget actions
// private IWorkbenchAction addBookmarkAction;
//
// private IWorkbenchAction addTaskAction;
//
//
//
// // contribution items
// // @issue should obtain from ContributionItemFactory
// private NewWizardMenu newWizardMenu;
//
// private IContributionItem pinEditorContributionItem;
//
//
//
//
//
// /**
// * Constructs a new action builder which contributes actions
// * to the given window.
// *
// * @param window the window
// */
// public BilabActionBuilder(IWorkbenchWindow window) {
// this.window = window;
// }
//
// /**
// * Returns the window to which this action builder is contributing.
// */
// private IWorkbenchWindow getWindow() {
// return window;
// }
//
//
//
// /**
// * Builds the actions and contributes them to the given window.
// */
// public void makeAndPopulateActions(IWorkbenchConfigurer windowConfigurer,
// IActionBarConfigurer actionBarConfigurer) {
// makeActions(windowConfigurer, actionBarConfigurer);
// populateMenuBar(actionBarConfigurer);
// populateCoolBar(actionBarConfigurer);
// // populateStatusLine(actionBarConfigurer);
// }
//
// /**
// * Fills the coolbar with the workbench actions.
// */
// public void populateCoolBar(IActionBarConfigurer configurer) {
// ICoolBarManager cbManager = configurer.getCoolBarManager();
//
// { // Set up the context Menu
// IMenuManager popUpMenu = new MenuManager();
// popUpMenu.add(new ActionContributionItem(lockToolBarAction));
// popUpMenu.add(new ActionContributionItem(editActionSetAction));
// cbManager.setContextMenuManager(popUpMenu);
// }
// cbManager.add(new GroupMarker(IIDEActionConstants.GROUP_FILE));
// { // File Group
// IToolBarManager fileToolBar = new ToolBarManager(cbManager
// .getStyle());
// fileToolBar.add(new Separator(IWorkbenchActionConstants.NEW_GROUP));
// fileToolBar.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
// fileToolBar.add(new GroupMarker(
// IWorkbenchActionConstants.SAVE_GROUP));
// fileToolBar.add(saveAction);
// fileToolBar
// .add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
// fileToolBar.add(printAction);
// fileToolBar
// .add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
//
// fileToolBar
// .add(new Separator(IWorkbenchActionConstants.BUILD_GROUP));
// fileToolBar
// .add(new GroupMarker(IWorkbenchActionConstants.BUILD_EXT));
// fileToolBar.add(new Separator(
// IWorkbenchActionConstants.MB_ADDITIONS));
//
// // Add to the cool bar manager
// cbManager.add(new ToolBarContributionItem(fileToolBar,
// IWorkbenchActionConstants.TOOLBAR_FILE));
// }
//
// cbManager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
//
// cbManager.add(new GroupMarker(IIDEActionConstants.GROUP_NAV));
// { // Navigate group
// IToolBarManager navToolBar = new ToolBarManager(cbManager
// .getStyle());
// navToolBar.add(new Separator(
// IWorkbenchActionConstants.HISTORY_GROUP));
// navToolBar
// .add(new GroupMarker(IWorkbenchActionConstants.GROUP_APP));
// navToolBar.add(backwardHistoryAction);
// navToolBar.add(forwardHistoryAction);
// navToolBar.add(new Separator(IWorkbenchActionConstants.PIN_GROUP));
// navToolBar.add(pinEditorContributionItem);
//
// // Add to the cool bar manager
// cbManager.add(new ToolBarContributionItem(navToolBar,
// IWorkbenchActionConstants.TOOLBAR_NAVIGATE));
// }
//
// cbManager.add(new GroupMarker(IWorkbenchActionConstants.GROUP_EDITOR));
//
// }
//
// /**
// * Fills the menu bar with the workbench actions.
// */
// public void populateMenuBar(IActionBarConfigurer configurer) {
// IMenuManager menubar = configurer.getMenuManager();
// menubar.add(createFileMenu());
// menubar.add(createEditMenu());
// menubar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
// menubar.add(createWindowMenu());
// menubar.add(createHelpMenu());
// }
//
// /**
// * Creates and returns the File menu.
// */
// private MenuManager createFileMenu() {
// MenuManager menu = new MenuManager("&File",
// IWorkbenchActionConstants.M_FILE);
// menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
// {
// // create the New submenu, using the same id for it as the New action
// String newText = "New";
// String newId = ActionFactory.NEW.getId();
// MenuManager newMenu = new MenuManager(newText, newId);
// // {
// // public String getMenuText() {
// // String result = super.getMenuText();
// // if (newQuickMenu == null)
// // return result;
// // String shortCut = newQuickMenu.getShortCutString();
// // if (shortCut == null)
// // return result;
////                    return result + "\t" + shortCut; //$NON-NLS-1$
// // }
// // };
// newMenu.add(new Separator(newId));
// this.newWizardMenu = new NewWizardMenu(getWindow());
// newMenu.add(this.newWizardMenu);
// newMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
// menu.add(newMenu);
// }
//
// menu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
// menu.add(new Separator());
//
// menu.add(closeAction);
// menu.add(closeAllAction);
// // menu.add(closeAllSavedAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.CLOSE_EXT));
// menu.add(new Separator());
// menu.add(saveAction);
// menu.add(saveAsAction);
// menu.add(saveAllAction);
//
// menu.add(revertAction);
// menu.add(new Separator());
// menu.add(moveAction);
// menu.add(renameAction);
// menu.add(refreshAction);
//
// menu.add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
// menu.add(new Separator());
// menu.add(printAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
// menu.add(new Separator());
// menu.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
// menu.add(new Separator());
// menu.add(importResourcesAction);
// menu.add(exportResourcesAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.IMPORT_EXT));
// menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//
// menu.add(new Separator());
// menu.add(propertiesAction);
//
// menu.add(ContributionItemFactory.REOPEN_EDITORS.create(getWindow()));
// menu.add(new GroupMarker(IWorkbenchActionConstants.MRU));
// menu.add(new Separator());
// menu.add(quitAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
// return menu;
// }
//
// /**
// * Creates and returns the Edit menu.
// */
// private MenuManager createEditMenu() {
//        MenuManager menu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT); //$NON-NLS-1$
// menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));
//
// menu.add(undoAction);
// menu.add(redoAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.UNDO_EXT));
// menu.add(new Separator());
//
// menu.add(cutAction);
// menu.add(copyAction);
// menu.add(pasteAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.CUT_EXT));
// menu.add(new Separator());
//
// menu.add(deleteAction);
// menu.add(selectAllAction);
// menu.add(new Separator());
//
// menu.add(findAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));
// menu.add(new Separator());
//
// menu.add(addBookmarkAction);
// menu.add(addTaskAction);
// menu.add(new GroupMarker(IWorkbenchActionConstants.ADD_EXT));
//
// menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
// menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
// return menu;
// }
//
//
// /**
// * Creates and returns the Window menu.
// */
// private MenuManager createWindowMenu() {
// MenuManager menu = new MenuManager("Window",
// IWorkbenchActionConstants.M_WINDOW);
//
// IWorkbenchAction action = ActionFactory.OPEN_NEW_WINDOW
// .create(getWindow());
// action.setText("New Window");
// menu.add(action);
// menu.add(new Separator());
// addPerspectiveActions(menu);
// menu.add(new Separator());
// // addKeyboardShortcuts(menu);
// menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "end")); //$NON-NLS-1$
// menu.add(openPreferencesAction);
//
// menu.add(ContributionItemFactory.OPEN_WINDOWS.create(getWindow()));
// return menu;
// }
//
// /**
// * Adds the perspective actions to the specified menu.
// */
// private void addPerspectiveActions(MenuManager menu) {
// {
// String openText = "OpenPerspective";
// MenuManager changePerspMenuMgr = new MenuManager(openText,
//                    "openPerspective"); //$NON-NLS-1$
// IContributionItem changePerspMenuItem =
// ContributionItemFactory.PERSPECTIVES_SHORTLIST
// .create(getWindow());
// changePerspMenuMgr.add(changePerspMenuItem);
// menu.add(changePerspMenuMgr);
// }
// {
//            MenuManager showViewMenuMgr = new MenuManager("Show View", "showView"); //$NON-NLS-1$ //$NON-NLS-2$
// IContributionItem showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST
// .create(getWindow());
// showViewMenuMgr.add(showViewMenu);
// menu.add(showViewMenuMgr);
// }
// menu.add(new Separator());
// menu.add(editActionSetAction);
// menu.add(savePerspectiveAction);
// menu.add(resetPerspectiveAction);
// menu.add(closePerspAction);
// menu.add(closeAllPerspsAction);
// }
//
//
// /**
// * Creates and returns the Help menu.
// */
// private MenuManager createHelpMenu() {
// MenuManager menu = new MenuManager("&Help",
// IWorkbenchActionConstants.M_HELP);
//		addSeparatorOrGroupMarker(menu, "group.intro"); //$NON-NLS-1$
// // See if a welcome or intro page is specified
// if (introAction != null)
// menu.add(introAction);
// else if (quickStartAction != null)
// menu.add(quickStartAction);
//		menu.add(new GroupMarker("group.intro.ext")); //$NON-NLS-1$
//		addSeparatorOrGroupMarker(menu, "group.main"); //$NON-NLS-1$
// menu.add(helpContentsAction);
//
// // See if a tips and tricks page is specified
// if (tipsAndTricksAction != null)
// menu.add(tipsAndTricksAction);
// // HELP_START should really be the first item, but it was after
// // quickStartAction and tipsAndTricksAction in 2.1.
// menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
//		menu.add(new GroupMarker("group.main.ext")); //$NON-NLS-1$
//		addSeparatorOrGroupMarker(menu, "group.tutorials"); //$NON-NLS-1$
//		addSeparatorOrGroupMarker(menu, "group.tools"); //$NON-NLS-1$
//		addSeparatorOrGroupMarker(menu, "group.updates"); //$NON-NLS-1$
// menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
// addSeparatorOrGroupMarker(menu, IWorkbenchActionConstants.MB_ADDITIONS);
// // about should always be at the bottom
//		menu.add(new Separator("group.about")); //$NON-NLS-1$
// menu.add(aboutAction);
//		menu.add(new GroupMarker("group.about.ext")); //$NON-NLS-1$
//
// /*
// final IMutableContextActivationService contextActivationServiceA =
// ContextActivationServiceFactory.getMutableContextActivationService();
// contextActivationServiceA.setActiveContextIds(new
// HashSet(Collections.singletonList("A")));
//
// final IMutableContextActivationService contextActivationServiceB =
// ContextActivationServiceFactory.getMutableContextActivationService();
// contextActivationServiceB.setActiveContextIds(new
// HashSet(Collections.singletonList("B")));
//
// menu.add(new Separator());
//
// menu.add(new Action("Add context A to the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchContextSupport workbenchContextSupport = (IWorkbenchContextSupport)
// workbench.getContextSupport();
// workbenchContextSupport.getCompoundContextActivationService().addContextActivationService(contextActivationServiceA);
// }
// });
//
// menu.add(new Action("Remove context A from the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchContextSupport workbenchContextSupport = (IWorkbenchContextSupport)
// workbench.getContextSupport();
// workbenchContextSupport.getCompoundContextActivationService().removeContextActivationService(contextActivationServiceA);
// }
// });
//
// menu.add(new Action("Add context B to the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchContextSupport workbenchContextSupport = (IWorkbenchContextSupport)
// workbench.getContextSupport();
// workbenchContextSupport.getCompoundContextActivationService().addContextActivationService(contextActivationServiceB);
// }
// });
//
// menu.add(new Action("Remove context B from the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchContextSupport workbenchContextSupport = (IWorkbenchContextSupport)
// workbench.getContextSupport();
// workbenchContextSupport.getCompoundContextActivationService().removeContextActivationService(contextActivationServiceB);
// }
// });
//
// menu.add(new Separator());
//
// menu.add(new Action("Add context A to the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageContextSupport workbenchPageContextSupport =
// (IWorkbenchPageContextSupport) workbenchPage.getContextSupport();
// workbenchPageContextSupport.getCompoundContextActivationService().addContextActivationService(contextActivationServiceA);
// }
// }
// }
// });
//
// menu.add(new Action("Remove context A from the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageContextSupport workbenchPageContextSupport =
// (IWorkbenchPageContextSupport) workbenchPage.getContextSupport();
// workbenchPageContextSupport.getCompoundContextActivationService().removeContextActivationService(contextActivationServiceA);
// }
// }
// }
// });
//
// menu.add(new Action("Add context B to the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageContextSupport workbenchPageContextSupport =
// (IWorkbenchPageContextSupport) workbenchPage.getContextSupport();
// workbenchPageContextSupport.getCompoundContextActivationService().addContextActivationService(contextActivationServiceB);
// }
// }
// }
// });
//
// menu.add(new Action("Remove context B from the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageContextSupport workbenchPageContextSupport =
// (IWorkbenchPageContextSupport) workbenchPage.getContextSupport();
// workbenchPageContextSupport.getCompoundContextActivationService().removeContextActivationService(contextActivationServiceB);
// }
// }
// }
// });
//
// IHandler handlerA = new IHandler() {
// public void execute() {
// }
//
// public void execute(Event event) {
// }
//
// public boolean isEnabled() {
// return false;
// }
// };
//
// IHandler handlerB = new IHandler() {
// public void execute() {
// }
//
// public void execute(Event event) {
// }
//
// public boolean isEnabled() {
// return false;
// }
// };
//
// final IMutableCommandHandlerService commandHandlerServiceA =
// CommandHandlerServiceFactory.getMutableCommandHandlerService();
// commandHandlerServiceA.setHandlersByCommandId(new
// HashMap(Collections.singletonMap("command", handlerA)));
//
// final IMutableCommandHandlerService commandHandlerServiceB =
// CommandHandlerServiceFactory.getMutableCommandHandlerService();
// commandHandlerServiceB.setHandlersByCommandId(new
// HashMap(Collections.singletonMap("command", handlerB)));
//
// menu.add(new Separator());
//
// menu.add(new Action("Add handler A to the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchCommandSupport workbenchCommandSupport = (IWorkbenchCommandSupport)
// workbench.getCommandSupport();
// workbenchCommandSupport.getCompoundCommandHandlerService().addCommandHandlerService(commandHandlerServiceA);
// }
// });
//
// menu.add(new Action("Remove handler A from the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchCommandSupport workbenchCommandSupport = (IWorkbenchCommandSupport)
// workbench.getCommandSupport();
// workbenchCommandSupport.getCompoundCommandHandlerService().removeCommandHandlerService(commandHandlerServiceA);
// }
// });
//
// menu.add(new Action("Add handler B to the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchCommandSupport workbenchCommandSupport = (IWorkbenchCommandSupport)
// workbench.getCommandSupport();
// workbenchCommandSupport.getCompoundCommandHandlerService().addCommandHandlerService(commandHandlerServiceB);
// }
// });
//
// menu.add(new Action("Remove handler B from the workbench") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchCommandSupport workbenchCommandSupport = (IWorkbenchCommandSupport)
// workbench.getCommandSupport();
// workbenchCommandSupport.getCompoundCommandHandlerService().removeCommandHandlerService(commandHandlerServiceB);
// }
// });
//
// menu.add(new Separator());
//
// menu.add(new Action("Add handler A to the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageCommandSupport workbenchPageCommandSupport =
// (IWorkbenchPageCommandSupport) workbenchPage.getCommandSupport();
// workbenchPageCommandSupport.getCompoundCommandHandlerService().addCommandHandlerService(commandHandlerServiceA);
// }
// }
// }
// });
//
// menu.add(new Action("Remove handler A from the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageCommandSupport workbenchPageCommandSupport =
// (IWorkbenchPageCommandSupport) workbenchPage.getCommandSupport();
// workbenchPageCommandSupport.getCompoundCommandHandlerService().removeCommandHandlerService(commandHandlerServiceA);
// }
// }
// }
// });
//
// menu.add(new Action("Add handler B to the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageCommandSupport workbenchPageCommandSupport =
// (IWorkbenchPageCommandSupport) workbenchPage.getCommandSupport();
// workbenchPageCommandSupport.getCompoundCommandHandlerService().addCommandHandlerService(commandHandlerServiceB);
// }
// }
// }
// });
//
// menu.add(new Action("Remove handler B from the workbench page") {
// public void run() {
// IWorkbench workbench = PlatformUI.getWorkbench();
// IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
//
// if (workbenchWindow != null) {
// IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
//
// if (workbenchPage != null) {
// IWorkbenchPageCommandSupport workbenchPageCommandSupport =
// (IWorkbenchPageCommandSupport) workbenchPage.getCommandSupport();
// workbenchPageCommandSupport.getCompoundCommandHandlerService().removeCommandHandlerService(commandHandlerServiceB);
// }
// }
// }
// });
// */
//
// return menu;
// }
//
// /**
// * Adds a <code>GroupMarker</code> or <code>Separator</code> to a menu.
// * The test for whether a separator should be added is done by checking for
// * the existence of a preference matching the string
// * useSeparator.MENUID.GROUPID that is set to <code>true</code>.
// *
// * @param menu
// * the menu to add to
// * @param string
// * the group id for the added separator or group marker
// */
// private void addSeparatorOrGroupMarker(MenuManager menu, String groupId) {
//		String prefId = "useSeparator." + menu.getId() + "." + groupId; //$NON-NLS-1$ //$NON-NLS-2$
// boolean addExtraSeparators = BilabPlugin.getDefault()
// .getPreferenceStore().getBoolean(prefId);
// if (addExtraSeparators) {
// menu.add(new Separator(groupId));
// } else {
// menu.add(new GroupMarker(groupId));
// }
// }
//
// /**
// * Disposes any resources and unhooks any listeners that are no longer needed.
// * Called when the window is closed.
// */
// public void dispose() {
// closeAction.dispose();
// closeAllAction.dispose();
// closeAllSavedAction.dispose();
// saveAction.dispose();
// saveAllAction.dispose();
// aboutAction.dispose();
// openPreferencesAction.dispose();
// saveAsAction.dispose();
// hideShowEditorAction.dispose();
// savePerspectiveAction.dispose();
// resetPerspectiveAction.dispose();
// editActionSetAction.dispose();
// closePerspAction.dispose();
// lockToolBarAction.dispose();
// closeAllPerspsAction.dispose();
// showViewMenuAction.dispose();
// showPartPaneMenuAction.dispose();
// nextPartAction.dispose();
// prevPartAction.dispose();
// nextEditorAction.dispose();
// prevEditorAction.dispose();
// nextPerspectiveAction.dispose();
// prevPerspectiveAction.dispose();
// activateEditorAction.dispose();
// maximizePartAction.dispose();
// minimizePartAction.dispose();
// workbenchEditorsAction.dispose();
// workbookEditorsAction.dispose();
// backwardHistoryAction.dispose();
// forwardHistoryAction.dispose();
// undoAction.dispose();
// redoAction.dispose();
// cutAction.dispose();
// copyAction.dispose();
// pasteAction.dispose();
// deleteAction.dispose();
// selectAllAction.dispose();
// findAction.dispose();
// printAction.dispose();
// revertAction.dispose();
// refreshAction.dispose();
// propertiesAction.dispose();
// quitAction.dispose();
// moveAction.dispose();
// renameAction.dispose();
// goIntoAction.dispose();
// backAction.dispose();
// forwardAction.dispose();
// upAction.dispose();
// nextAction.dispose();
// previousAction.dispose();
//
// // editorsDropDownAction is not currently an IWorkbenchAction
// // editorsDropDownAction.dispose();
// newWizardAction.dispose();
// importResourcesAction.dispose();
// exportResourcesAction.dispose();
// if (quickStartAction != null) {
// quickStartAction.dispose();
// }
// if (tipsAndTricksAction != null) {
// tipsAndTricksAction.dispose();
// }
// addBookmarkAction.dispose();
// addTaskAction.dispose();
// pinEditorContributionItem.dispose();
// if (introAction != null) {
// introAction.dispose();
// }
//
//
// // null out actions to make leak debugging easier
// closeAction = null;
// closeAllAction = null;
// closeAllSavedAction = null;
// saveAction = null;
// saveAllAction = null;
// helpContentsAction = null;
// aboutAction = null;
// openPreferencesAction = null;
// saveAsAction = null;
// hideShowEditorAction = null;
// savePerspectiveAction = null;
// resetPerspectiveAction = null;
// editActionSetAction = null;
// closePerspAction = null;
// lockToolBarAction = null;
// closeAllPerspsAction = null;
// showViewMenuAction = null;
// showPartPaneMenuAction = null;
// nextPartAction = null;
// prevPartAction = null;
// nextEditorAction = null;
// prevEditorAction = null;
// nextPerspectiveAction = null;
// prevPerspectiveAction = null;
// activateEditorAction = null;
// maximizePartAction = null;
// minimizePartAction = null;
// workbenchEditorsAction = null;
// workbookEditorsAction = null;
// backwardHistoryAction = null;
// forwardHistoryAction = null;
// undoAction = null;
// redoAction = null;
// cutAction = null;
// copyAction = null;
// pasteAction = null;
// deleteAction = null;
// selectAllAction = null;
// findAction = null;
// printAction = null;
// revertAction = null;
// refreshAction = null;
// propertiesAction = null;
// quitAction = null;
// moveAction = null;
// renameAction = null;
// goIntoAction = null;
// backAction = null;
// forwardAction = null;
// upAction = null;
// nextAction = null;
// previousAction = null;
// newWizardAction = null;
// importResourcesAction = null;
// exportResourcesAction = null;
// quickStartAction = null;
// tipsAndTricksAction = null;
// addBookmarkAction = null;
// addTaskAction = null;
// newWizardMenu = null;
// pinEditorContributionItem = null;
// introAction = null;
// }
//
//
// /**
// * Returns true if the menu with the given ID should
// * be considered as an OLE container menu. Container menus
// * are preserved in OLE menu merging.
// */
// public boolean isContainerMenu(String menuId) {
// if (menuId.equals(IWorkbenchActionConstants.M_FILE))
// return true;
// if (menuId.equals(IWorkbenchActionConstants.M_WINDOW))
// return true;
// return false;
// }
//
// /**
// * Return whether or not given id matches the id of the coolitems that
// * the workbench creates.
// */
// public boolean isWorkbenchCoolItemId(String id) {
// if (IWorkbenchActionConstants.TOOLBAR_FILE.equalsIgnoreCase(id))
// return true;
// if (IWorkbenchActionConstants.TOOLBAR_NAVIGATE.equalsIgnoreCase(id))
// return true;
// return false;
// }
//
//
// /**
// * Creates actions (and contribution items) for the menu bar, toolbar and
// status line.
// */
// private void makeActions(IWorkbenchConfigurer workbenchConfigurer,
// IActionBarConfigurer actionBarConfigurer) {
//
// // The actions in jface do not have menu vs. enable, vs. disable vs. color
// // There are actions in here being passed the workbench - problem
// setCurrentActionBarConfigurer(actionBarConfigurer);
//
// newWizardAction = ActionFactory.NEW.create(getWindow());
// registerGlobalAction(newWizardAction);
//
// // newWizardDropDownAction = IDEActionFactory.NEW_WIZARD_DROP_DOWN
// // .create(getWindow());
//
// importResourcesAction = ActionFactory.IMPORT.create(getWindow());
// registerGlobalAction(importResourcesAction);
//
// exportResourcesAction = ActionFactory.EXPORT.create(getWindow());
// registerGlobalAction(exportResourcesAction);
//
// saveAction = ActionFactory.SAVE.create(getWindow());
// registerGlobalAction(saveAction);
//
// saveAsAction = ActionFactory.SAVE_AS.create(getWindow());
// registerGlobalAction(saveAsAction);
//
// saveAllAction = ActionFactory.SAVE_ALL.create(getWindow());
// registerGlobalAction(saveAllAction);
//
// undoAction = ActionFactory.UNDO.create(getWindow());
// registerGlobalAction(undoAction);
//
// redoAction = ActionFactory.REDO.create(getWindow());
// registerGlobalAction(redoAction);
//
// cutAction = ActionFactory.CUT.create(getWindow());
// registerGlobalAction(cutAction);
//
// copyAction = ActionFactory.COPY.create(getWindow());
// registerGlobalAction(copyAction);
//
// pasteAction = ActionFactory.PASTE.create(getWindow());
// registerGlobalAction(pasteAction);
//
// printAction = ActionFactory.PRINT.create(getWindow());
// registerGlobalAction(printAction);
//
// selectAllAction = ActionFactory.SELECT_ALL.create(getWindow());
// registerGlobalAction(selectAllAction);
//
// findAction = ActionFactory.FIND.create(getWindow());
// registerGlobalAction(findAction);
//
// closeAction = ActionFactory.CLOSE.create(getWindow());
// registerGlobalAction(closeAction);
//
// closeAllAction = ActionFactory.CLOSE_ALL.create(getWindow());
// registerGlobalAction(closeAllAction);
//
// closeAllSavedAction = ActionFactory.CLOSE_ALL_SAVED.create(getWindow());
// registerGlobalAction(closeAllSavedAction);
//
// helpContentsAction = ActionFactory.HELP_CONTENTS.create(getWindow());
// registerGlobalAction(helpContentsAction);
//
// aboutAction = ActionFactory.ABOUT.create(getWindow());
// // aboutAction
// // .setImageDescriptor(IDEInternalWorkbenchImages
// // .getImageDescriptor(IDEInternalWorkbenchImages.IMG_OBJS_DEFAULT_PROD));
// registerGlobalAction(aboutAction);
//
// openPreferencesAction = ActionFactory.PREFERENCES.create(getWindow());
// registerGlobalAction(openPreferencesAction);
//
// addBookmarkAction = IDEActionFactory.BOOKMARK.create(getWindow());
// registerGlobalAction(addBookmarkAction);
//
// addTaskAction = IDEActionFactory.ADD_TASK.create(getWindow());
// registerGlobalAction(addTaskAction);
//
// deleteAction = ActionFactory.DELETE.create(getWindow());
// registerGlobalAction(deleteAction);
// /*
// AboutInfo[] infos = BilabPlugin.getDefault().getFeatureInfos();
// // See if a welcome page is specified
// for (int i = 0; i < infos.length; i++) {
// if (infos[i].getWelcomePageURL() != null) {
// quickStartAction = IDEActionFactory.QUICK_START
// .create(getWindow());
// registerGlobalAction(quickStartAction);
// break;
// }
// }
//
// // See if a tips and tricks page is specified
// for (int i = 0; i < infos.length; i++) {
// if (infos[i].getTipsAndTricksHref() != null) {
// tipsAndTricksAction = IDEActionFactory.TIPS_AND_TRICKS
// .create(getWindow());
// registerGlobalAction(tipsAndTricksAction);
// break;
// }
// }
// */
// // Actions for invisible accelerators
// showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(getWindow());
// registerGlobalAction(showViewMenuAction);
//
// showPartPaneMenuAction = ActionFactory.SHOW_PART_PANE_MENU
// .create(getWindow());
// registerGlobalAction(showPartPaneMenuAction);
//
// nextEditorAction = ActionFactory.NEXT_EDITOR.create(getWindow());
// prevEditorAction = ActionFactory.PREVIOUS_EDITOR.create(getWindow());
// ActionFactory.linkCycleActionPair(nextEditorAction, prevEditorAction);
// registerGlobalAction(nextEditorAction);
// registerGlobalAction(prevEditorAction);
//
// nextPartAction = ActionFactory.NEXT_PART.create(getWindow());
// prevPartAction = ActionFactory.PREVIOUS_PART.create(getWindow());
// ActionFactory.linkCycleActionPair(nextPartAction, prevPartAction);
// registerGlobalAction(nextPartAction);
// registerGlobalAction(prevPartAction);
//
// nextPerspectiveAction = ActionFactory.NEXT_PERSPECTIVE
// .create(getWindow());
// prevPerspectiveAction = ActionFactory.PREVIOUS_PERSPECTIVE
// .create(getWindow());
// ActionFactory.linkCycleActionPair(nextPerspectiveAction,
// prevPerspectiveAction);
// registerGlobalAction(nextPerspectiveAction);
// registerGlobalAction(prevPerspectiveAction);
//
// activateEditorAction = ActionFactory.ACTIVATE_EDITOR
// .create(getWindow());
// registerGlobalAction(activateEditorAction);
//
// maximizePartAction = ActionFactory.MAXIMIZE.create(getWindow());
// registerGlobalAction(maximizePartAction);
//
// minimizePartAction = ActionFactory.MINIMIZE.create(getWindow());
// registerGlobalAction(minimizePartAction);
//
// workbenchEditorsAction = ActionFactory.SHOW_OPEN_EDITORS
// .create(getWindow());
// registerGlobalAction(workbenchEditorsAction);
//
// workbookEditorsAction = ActionFactory.SHOW_WORKBOOK_EDITORS
// .create(getWindow());
// registerGlobalAction(workbookEditorsAction);
//
// hideShowEditorAction = ActionFactory.SHOW_EDITOR.create(getWindow());
// registerGlobalAction(hideShowEditorAction);
// savePerspectiveAction = ActionFactory.SAVE_PERSPECTIVE
// .create(getWindow());
// registerGlobalAction(savePerspectiveAction);
// editActionSetAction = ActionFactory.EDIT_ACTION_SETS
// .create(getWindow());
// registerGlobalAction(editActionSetAction);
// lockToolBarAction = ActionFactory.LOCK_TOOL_BAR.create(getWindow());
// registerGlobalAction(lockToolBarAction);
// resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE
// .create(getWindow());
// registerGlobalAction(resetPerspectiveAction);
// closePerspAction = ActionFactory.CLOSE_PERSPECTIVE.create(getWindow());
// registerGlobalAction(closePerspAction);
// closeAllPerspsAction = ActionFactory.CLOSE_ALL_PERSPECTIVES
// .create(getWindow());
// registerGlobalAction(closeAllPerspsAction);
//
// forwardHistoryAction = ActionFactory.FORWARD_HISTORY
// .create(getWindow());
// registerGlobalAction(forwardHistoryAction);
//
// backwardHistoryAction = ActionFactory.BACKWARD_HISTORY
// .create(getWindow());
// registerGlobalAction(backwardHistoryAction);
//
// revertAction = ActionFactory.REVERT.create(getWindow());
// registerGlobalAction(revertAction);
//
// refreshAction = ActionFactory.REFRESH.create(getWindow());
// registerGlobalAction(refreshAction);
//
// propertiesAction = ActionFactory.PROPERTIES.create(getWindow());
// registerGlobalAction(propertiesAction);
//
// quitAction = ActionFactory.QUIT.create(getWindow());
// registerGlobalAction(quitAction);
//
// moveAction = ActionFactory.MOVE.create(getWindow());
// registerGlobalAction(moveAction);
//
// renameAction = ActionFactory.RENAME.create(getWindow());
// registerGlobalAction(renameAction);
//
// goIntoAction = ActionFactory.GO_INTO.create(getWindow());
// registerGlobalAction(goIntoAction);
//
// backAction = ActionFactory.BACK.create(getWindow());
// registerGlobalAction(backAction);
//
// forwardAction = ActionFactory.FORWARD.create(getWindow());
// registerGlobalAction(forwardAction);
//
// upAction = ActionFactory.UP.create(getWindow());
// registerGlobalAction(upAction);
//
// nextAction = ActionFactory.NEXT.create(getWindow());
// // nextAction
// // .setImageDescriptor(IDEInternalWorkbenchImages
// // .getImageDescriptor(IDEInternalWorkbenchImages.IMG_ETOOL_NEXT_NAV));
// registerGlobalAction(nextAction);
//
// previousAction = ActionFactory.PREVIOUS.create(getWindow());
// // previousAction
// // .setImageDescriptor(IDEInternalWorkbenchImages
// // .getImageDescriptor(IDEInternalWorkbenchImages.IMG_ETOOL_PREVIOUS_NAV));
// registerGlobalAction(previousAction);
//
//
// if (getWindow().getWorkbench().getIntroManager().hasIntro()) {
// introAction = ActionFactory.INTRO.create(window);
// registerGlobalAction(introAction);
// }
//
//
// pinEditorContributionItem = ContributionItemFactory.PIN_EDITOR
// .create(getWindow());
// }
//
// private void setCurrentActionBarConfigurer(
// IActionBarConfigurer actionBarConfigurer) {
// this.actionBarConfigurer = actionBarConfigurer;
// }
//
// private void registerGlobalAction(IAction action) {
// actionBarConfigurer.registerGlobalAction(action);
// }
//
//
// /**
// * Update the pin action's tool bar
// */
// void updatePinActionToolbar() {
//
// ICoolBarManager coolBarManager = actionBarConfigurer
// .getCoolBarManager();
// IContributionItem cbItem = coolBarManager
// .find(IWorkbenchActionConstants.TOOLBAR_NAVIGATE);
// if (!(cbItem instanceof ToolBarContributionItem)) {
// // This should not happen
//            Notify.devError(this,"Navigation toolbar contribution item is missing"); //$NON-NLS-1$
// return;
// }
// ToolBarContributionItem toolBarItem = (ToolBarContributionItem) cbItem;
// IToolBarManager toolBarManager = toolBarItem.getToolBarManager();
// if (toolBarManager == null) {
// // error if this happens, navigation toolbar assumed to always exist
//          Notify.devError(this,"Navigate toolbar is missing"); //$NON-NLS-1$
// return;
// }
//
// toolBarManager.update(false);
// toolBarItem.update(ICoolBarManager.SIZE);
// }
// }