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

package bilab.notebook;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;


/**
 * Provides context menu actions for the ShapesEditor.
 * @author Elias Volanakis
 */
class NotebookPageEditorContextMenuProvider extends ContextMenuProvider {

/** The editor's action registry. */
private ActionRegistry actionRegistry;
	
/**
 * Instantiate a new menu context provider for the specified EditPartViewer 
 * and ActionRegistry.
 * @param viewer	the editor's graphical viewer
 * @param registry	the editor's action registry
 * @throws IllegalArgumentException if registry is <tt>null</tt>. 
 */
public NotebookPageEditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
	super(viewer);
	if (registry == null) {
		throw new IllegalArgumentException();
	}
	actionRegistry = registry;
}

/**
 * Called when the context menu is about to show. Actions, 
 * whose state is enabled, will appear in the context menu.
 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
 */
public void buildContextMenu(IMenuManager menu) {
	// Add standard action groups to the menu
	GEFActionConstants.addStandardActionGroups(menu);
	
	// Add actions to the menu
	menu.appendToGroup(
			GEFActionConstants.GROUP_UNDO, // target group id
			getAction(ActionFactory.UNDO.getId())); // action to add
	menu.appendToGroup(
			GEFActionConstants.GROUP_UNDO, 
			getAction(ActionFactory.REDO.getId()));
	menu.appendToGroup(
			GEFActionConstants.GROUP_EDIT,
			getAction(ActionFactory.DELETE.getId()));
}

private IAction getAction(String actionId) {
	return actionRegistry.getAction(actionId);
}

}
