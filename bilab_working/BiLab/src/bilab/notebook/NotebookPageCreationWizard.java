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
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: Elias Volanakis - initial API and implementation
 *******************************************************************************/
package bilab.notebook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;

import bilab.notebook.model.GraphicsDiagram;

/**
 * Create new new .shape-file. Those files can be used with the ShapesEditor
 * (see plugin.xml).
 * 
 * @author Elias Volanakis
 */
public class NotebookPageCreationWizard extends Wizard implements INewWizard {

    /**
     * This WizardPage can create an empty .npg file for the NotebookPageEditor.
     */
    private class CreationPage extends WizardNewFileCreationPage {
        private static final String DEFAULT_EXTENSION = ".npg";
        private final IWorkbench workbench;

        /**
         * Create a new wizard page instance.
         * 
         * @param workbench
         *            the current workbench
         * @param selection
         *            the current object selection
         * @see GraphicsCreationWizard#init(IWorkbench, IStructuredSelection)
         */
        CreationPage(final IWorkbench workbench,
                final IStructuredSelection selection) {
            super("shapeCreationPage1", selection);
            this.workbench = workbench;
            setTitle("Create a new " + DEFAULT_EXTENSION + " file");
            setDescription("Create a new " + DEFAULT_EXTENSION + " file");
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.ui.dialogs.WizardNewFileCreationPage#createControl(org
         * .eclipse.swt.widgets.Composite)
         */
        @Override
        public void createControl(final Composite parent) {
            super.createControl(parent);
            setFileName("shapesExample" + fileCount + DEFAULT_EXTENSION);
            setPageComplete(validatePage());
        }

        /** Return a new ShapesDiagram instance. */
        private Object createDefaultContent() {
            return new GraphicsDiagram();
        }

        /**
         * This method will be invoked, when the "Finish" button is pressed.
         * 
         * @see GraphicsCreationWizard#performFinish()
         */
        boolean finish() {
            // create a new file, result != null if successful
            final IFile newFile = createNewFile();
            fileCount++;

            // open newly created file in the editor
            final IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
                    .getActivePage();
            if (newFile != null && page != null) {
                try {
                    IDE.openEditor(page, newFile, true);
                } catch (final PartInitException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
         */
        @Override
        protected InputStream getInitialContents() {
            ByteArrayInputStream bais = null;
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(createDefaultContent()); // argument must be
                                                         // Serializable
                oos.flush();
                oos.close();
                bais = new ByteArrayInputStream(baos.toByteArray());
            } catch (final IOException ioe) {
                ioe.printStackTrace();
            }
            return bais;
        }

        /**
         * Return true, if the file name entered in this page is valid.
         */
        private boolean validateFilename() {
            if (getFileName() != null
                    && getFileName().endsWith(DEFAULT_EXTENSION)) {
                return true;
            }
            setErrorMessage("The 'file' name must end with "
                    + DEFAULT_EXTENSION);
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
         */
        @Override
        protected boolean validatePage() {
            return super.validatePage() && validateFilename();
        }
    }

    private static int fileCount = 1;

    private CreationPage page1;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    @Override
    public void addPages() {
        // add pages to this wizard
        addPage(page1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    public void init(final IWorkbench workbench,
            final IStructuredSelection selection) {
        // create pages for this wizard
        page1 = new CreationPage(workbench, selection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        return page1.finish();
    }
}
