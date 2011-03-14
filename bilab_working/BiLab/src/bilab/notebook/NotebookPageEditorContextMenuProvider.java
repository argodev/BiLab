/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package bilab.notebook;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Provides context menu actions for the ShapesEditor.
 * 
 * @author Elias Volanakis
 */
class NotebookPageEditorContextMenuProvider extends ContextMenuProvider {

    /** The editor's action registry. */
    private final ActionRegistry actionRegistry;

    /**
     * Instantiate a new menu context provider for the specified EditPartViewer
     * and ActionRegistry.
     * 
     * @param viewer
     *            the editor's graphical viewer
     * @param registry
     *            the editor's action registry
     * @throws IllegalArgumentException
     *             if registry is <tt>null</tt>.
     */
    public NotebookPageEditorContextMenuProvider(final EditPartViewer viewer,
            final ActionRegistry registry) {
        super(viewer);
        if (registry == null) {
            throw new IllegalArgumentException();
        }
        actionRegistry = registry;
    }

    /**
     * Called when the context menu is about to show. Actions, whose state is
     * enabled, will appear in the context menu.
     * 
     * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void buildContextMenu(final IMenuManager menu) {
        // Add standard action groups to the menu
        GEFActionConstants.addStandardActionGroups(menu);

        // Add actions to the menu
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO, // target group id
                getAction(ActionFactory.UNDO.getId())); // action to add
        menu.appendToGroup(GEFActionConstants.GROUP_UNDO,
                getAction(ActionFactory.REDO.getId()));
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
                getAction(ActionFactory.DELETE.getId()));
    }

    private IAction getAction(final String actionId) {
        return actionRegistry.getAction(actionId);
    }

}
