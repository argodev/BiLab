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

/*******************************************************************************
 * Copyright (c) 2004 Elias Volanakis. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Common
 * Public License v1.0 which accompanies this distribution, and is available at
 * �* http://www.eclipse.org/legal/cpl-v10.html �* �* Contributors: �*����Elias
 * Volanakis - initial API and implementation �
 *******************************************************************************/
package bilab.notebook;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Contributes actions to a toolbar. This class is tied to the editor in the
 * definition of editor-extension (see plugin.xml).
 * 
 * @author Elias Volanakis
 */
public class NotebookPageEditorActionBarContributor extends
        ActionBarContributor {

    /**
     * Create actions managed by this contributor.
     * 
     * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
     */
    @Override
    protected void buildActions() {
        addRetargetAction(new UndoRetargetAction());
        addRetargetAction(new RedoRetargetAction());
    }

    /**
     * Add actions to the given toolbar.
     * 
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     */
    @Override
    public void contributeToToolBar(final IToolBarManager toolBarManager) {
        super.contributeToToolBar(toolBarManager);
        toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
        toolBarManager.add(getAction(ActionFactory.REDO.getId()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
     */
    @Override
    protected void declareGlobalActionKeys() {
        // currently none
    }

}