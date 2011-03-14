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

package bilab;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;

public class ConsolePerspective implements IPerspectiveFactory {

    @Override
    public void createInitialLayout(final IPageLayout page) {

        page.setEditorAreaVisible(false);

        final String editorArea = page.getEditorArea();

        final IFolderLayout folder = page.createFolder("bilab.MainView",
                IPageLayout.BOTTOM, 1.0f, editorArea);

        folder.addView("bilab.ConsoleView");
        final IViewLayout consoleView = page.getViewLayout("bilab.ConsoleView");
        consoleView.setCloseable(false);

        folder.addView("bilab.BrowserView");

        // folder.addView("bilab.TextView");

        // then environment navigator view
        page.addView("bilab.EnvNavigatorView", IPageLayout.RIGHT, 0.7f,
                "bilab.ConsoleView");
        final IViewLayout navView = page
                .getViewLayout("bilab.EnvNavigatorView");
        navView.setCloseable(false);

        // the value viewer
        page.addView("bilab.ValueView", IPageLayout.BOTTOM, 0.5f,
                "bilab.EnvNavigatorView");
        final IViewLayout valueView = page.getViewLayout("bilab.ValueView");
        valueView.setCloseable(false);
    }
}