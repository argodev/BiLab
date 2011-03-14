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