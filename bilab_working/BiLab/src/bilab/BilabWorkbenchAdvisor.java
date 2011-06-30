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
}