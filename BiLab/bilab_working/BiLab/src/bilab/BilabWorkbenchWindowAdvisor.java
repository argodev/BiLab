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

// REG: This file is triggered/referenced by the BilabWorkbenchAdvisor
// and contains the data for setting up the window properties as well
// as calling the ActionBarAdvisor for setting up the menu bars
package bilab;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class BilabWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public BilabWorkbenchWindowAdvisor(
            final IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(
            final IActionBarConfigurer configurer) {
        return new BilabActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        super.preWindowOpen();

        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

        // always open the window maximized
        // configurer.getWindow().getShell().setMaximized(true);

        configurer.setShowCoolBar(true); // not sure what this controls...
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true); // unneeded

        // enable the perspective dialog at the right side of the button bar
        configurer.setShowPerspectiveBar(true);

        configurer.setShowProgressIndicator(true);

        // set the application title (shown in the title bar)
        configurer.setTitle("BiLab - v0.1.3 Prototype (reg)");
    }
}