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

// REG: This is the second file in the chain, and sets up the other advisors.
// This version is heavily modified from the archive in that much of the
// functionality that had been implemented here is now moved into the
// new BilabWorkbenchWindowAdvisior as well as the new
// BilabActionBarAdvisor (newer interfaces than what had been implemented.
package bilab;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class BilabWorkbenchAdvisor extends WorkbenchAdvisor {

    private static final String PERSPECTIVE_ID = "bilab.consolePerspective";

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            final IWorkbenchWindowConfigurer configurer) {
        return new BilabWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public String getInitialWindowPerspectiveId() {
        return PERSPECTIVE_ID;
    }

    // WorkbenchWindowAdvisor.preWindowOpen should be used???

    // public void preWindowOpen(IWorkbenchWindowConfigurer configurer) {
    // super.preWindowOpen(configurer);
    // configurer.setInitialSize(new Point(1600-48, 1200-48));
    // configurer.setShowCoolBar(true);
    // configurer.setShowStatusLine(true);
    // configurer.setShowMenuBar(true);
    // configurer.setShowPerspectiveBar(true);
    // configurer.setShowProgressIndicator(true);
    // configurer.setTitle("BiLab - v0.1.3 Prototype (reg)");
    // }

    // public void eventLoopException(Throwable t)
    // {
    // try to get the message from the exception
    // String m = t.getMessage();

    // if we didn't get it, let's try to get it another way...
    // if (m==null) {
    // t = t.getCause();
    // m = t.getMessage();
    // }

    // if we still didn't get it, let's return the class at least
    // if (m==null) m = t.getClass().toString();

    // display the error
    // Notify.userWarning(this,"an exception occured within the Bilab interface: "
    // + m);

    // dump the stack trace
    // t.printStackTrace(); ///!!! appropriate?
    // }
}