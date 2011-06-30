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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import bilab.notebook.model.GraphicsDiagram;
import bilab.notebook.parts.GraphicsEditPartFactory;
import bilab.notebook.parts.GraphicsTreeEditPartFactory;

/**
 * A graphical editor with flyout palette that can edit .bln files. The binding
 * between the .shapes file extension and this editor is done in plugin.xml
 * 
 * @author Elias Volanakis
 */
public class NotebookPageEditor extends GraphicalEditorWithFlyoutPalette {

    /**
     * Creates an outline pagebook for this editor.
     */
    public class NotebookEditorOutlinePage extends ContentOutlinePage {
        /**
         * Pointer to the editor instance associated with this outline page.
         * Used for calling back on editor methods.
         */
        private final NotebookPageEditor editor;
        /** A pagebook can containt multiple pages (just one in our case). */
        private PageBook pagebook;

        /**
         * Create a new outline page for the shapes editor.
         * 
         * @param editor
         *            the editor associated with this outline page (non-null)
         * @param viewer
         *            a viewer (TreeViewer instance) used for this outline page
         * @throws IllegalArgumentException
         *             if editor is null
         */
        public NotebookEditorOutlinePage(final NotebookPageEditor editor,
                final EditPartViewer viewer) {
            super(viewer);
            if (editor == null) {
                throw new IllegalArgumentException();
            }
            this.editor = editor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite
         * )
         */
        @Override
        public void createControl(final Composite parent) {
            pagebook = new PageBook(parent, SWT.NONE);

            // create outline viewer page
            final Control outline = getViewer().createControl(pagebook);
            // configure outline viewer
            getViewer().setEditDomain(editor.getEditDomain());
            getViewer().setEditPartFactory(new GraphicsTreeEditPartFactory());
            getViewer().setKeyHandler(editor.getCommonKeyHandler());
            // configure & add context menu to viewer
            final ContextMenuProvider cmProvider = new NotebookPageEditorContextMenuProvider(
                    getViewer(), editor.getActionRegistry());
            getViewer().setContextMenu(cmProvider);
            getSite().registerContextMenu(
                    "org.eclipse.gef.examples.shapes.outline.contextmenu",
                    cmProvider, getSite().getSelectionProvider());
            // hook outline viewer
            editor.getSelectionSynchronizer().addViewer(getViewer());
            // initialize outline viewer with model
            getViewer().setContents(editor.getModel());
            // show outline viewer
            pagebook.showPage(outline);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.part.IPage#dispose()
         */
        @Override
        public void dispose() {
            // unhook outline viewer
            editor.getSelectionSynchronizer().removeViewer(getViewer());
            // dispose
            super.dispose();
            editor.disposeOutlinePage();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.ui.part.IPage#getControl()
         */
        @Override
        public Control getControl() {
            return pagebook;
        }
    } // inner class

    /** This is the root of the editor's model. */
    private GraphicsDiagram diagram = new GraphicsDiagram();
    /** OutlinePage instance for this editor. */
    private IContentOutlinePage outlinePage;
    /** Palette component, holding the tools and shapes. */
    private PaletteRoot palette;
    /** Cache save-request status. */
    private boolean saveAlreadyRequested;

    /**
     * KeyHandler with common bindings for both the Outline View and the Editor.
     */
    private KeyHandler sharedKeyHandler;

    /** Create a new ShapesEditor instance. This is called by the Workspace. */
    public NotebookPageEditor() {
        setEditDomain(new DefaultEditDomain(this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util
     * .EventObject)
     */
    @Override
    public void commandStackChanged(final EventObject event) {
        super.commandStackChanged(event);
        if (isDirty() && !saveAlreadyRequested) {
            saveAlreadyRequested = true;
            firePropertyChange(IEditorPart.PROP_DIRTY);
        } else {
            saveAlreadyRequested = false;
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    /**
     * Configure the graphical viewer before it receives contents.
     * <p>
     * This is the place to choose an appropriate RootEditPart and
     * EditPartFactory for your editor. The RootEditPart determines the behavior
     * of the editor's "work-area". For example, GEF includes zoomable and
     * scrollable root edit parts. The EditPartFactory maps model elements to
     * edit parts (controllers).
     * </p>
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
     */
    @Override
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        final GraphicalViewer viewer = getGraphicalViewer();
        viewer.setRootEditPart(new ScalableRootEditPart());
        viewer.setEditPartFactory(new GraphicsEditPartFactory());
        viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer)
                .setParent(getCommonKeyHandler()));

        // configure the context menu provider
        final ContextMenuProvider cmProvider = new NotebookPageEditorContextMenuProvider(
                viewer, getActionRegistry());
        viewer.setContextMenu(cmProvider);
        getSite().registerContextMenu(cmProvider, viewer);
    }

    private void createOutputStream(final OutputStream os) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(getModel());
        oos.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#
     * createPaletteViewerProvider()
     */
    @Override
    protected PaletteViewerProvider createPaletteViewerProvider() {
        return new PaletteViewerProvider(getEditDomain()) {
            @Override
            protected void configurePaletteViewer(final PaletteViewer viewer) {
                super.configurePaletteViewer(viewer);
                // create a drag source listener for this palette viewer
                // together with an appropriate transfer drop target listener,
                // this will enable
                // model element creation by dragging a
                // CombinatedTemplateCreationEntries
                // from the palette into the editor
                // @see ShapesEditor#createTransferDropTargetListener()
                viewer.addDragSourceListener(new TemplateTransferDragSourceListener(
                        viewer));
            }
        };
    }

    /**
     * Create a transfer drop target listener. When using a
     * CombinedTemplateCreationEntry tool in the palette, this will enable model
     * element creation by dragging from the palette.
     * 
     * @see #createPaletteViewerProvider()
     */
    private TransferDropTargetListener createTransferDropTargetListener() {
        return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
            @Override
            protected CreationFactory getFactory(final Object template) {
                return new SimpleFactory((Class) template);
            }
        };
    }

    /**
     * Forget the outline page for this editor. Note that this is called from
     * ShapesEditorOutlinePage#dispose, so the outline page is already disposed.
     * 
     * @see ShapesEditorOutlinePage#dispose()
     */
    private void disposeOutlinePage() {
        outlinePage = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor
     * )
     */
    @Override
    public void doSave(final IProgressMonitor monitor) {

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            createOutputStream(out);
            final IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            file.setContents(new ByteArrayInputStream(out.toByteArray()), true, // keep
                                                                                // saving,
                                                                                // even
                                                                                // if
                                                                                // IFile
                                                                                // is
                                                                                // out
                                                                                // of
                                                                                // sync
                                                                                // with
                                                                                // the
                                                                                // Workspace
                    false, // dont keep history
                    monitor); // progress monitor
            getCommandStack().markSaveLocation();
        } catch (final CoreException ce) {
            ce.printStackTrace();
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        // Show a SaveAs dialog
        final Shell shell = getSite().getWorkbenchWindow().getShell();
        final SaveAsDialog dialog = new SaveAsDialog(shell);
        dialog.setOriginalFile(((IFileEditorInput) getEditorInput()).getFile());
        dialog.open();

        final IPath path = dialog.getResult();
        if (path != null) {
            // try to save the editor's contents under a different file name
            final IFile file = ResourcesPlugin.getWorkspace().getRoot()
                    .getFile(path);
            try {
                new ProgressMonitorDialog(shell).run(false, // don't fork
                        false, // not cancelable
                        new WorkspaceModifyOperation() { // run this operation
                            @Override
                            public void execute(final IProgressMonitor monitor) {
                                try {
                                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    createOutputStream(out);
                                    file.create(
                                            new ByteArrayInputStream(out
                                                    .toByteArray()), // contents
                                            true, // keep saving, even if IFile
                                                  // is out of sync with the
                                                  // Workspace
                                            monitor); // progress monitor
                                } catch (final CoreException ce) {
                                    ce.printStackTrace();
                                } catch (final IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            }
                        });
                // set input to the new file
                setInput(new FileEditorInput(file));
                getCommandStack().markSaveLocation();
            } catch (final InterruptedException ie) {
                // should not happen, since the monitor dialog is not cancelable
                ie.printStackTrace();
            } catch (final InvocationTargetException ite) {
                ite.printStackTrace();
            }
        } // if
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(
     * java.lang.Class)
     */
    @Override
    public Object getAdapter(final Class type) {
        // returns the content outline page for this editor
        if (type == IContentOutlinePage.class) {
            if (outlinePage == null) {
                outlinePage = new NotebookEditorOutlinePage(this,
                        new TreeViewer());
            }
            return outlinePage;
        }
        return super.getAdapter(type);
    }

    /**
     * Returns the KeyHandler with common bindings for both the Outline and
     * Graphical Views. For example, delete is a common action.
     */
    KeyHandler getCommonKeyHandler() {
        if (sharedKeyHandler == null) {
            sharedKeyHandler = new KeyHandler();

            // Add key and action pairs to sharedKeyHandler
            sharedKeyHandler
                    .put(KeyStroke.getPressed(SWT.DEL, 127, 0),
                            getActionRegistry().getAction(
                                    ActionFactory.DELETE.getId()));
        }
        return sharedKeyHandler;
    }

    private GraphicsDiagram getModel() {
        return diagram;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#
     * getPalettePreferences()
     */
    @Override
    protected FlyoutPreferences getPalettePreferences() {
        return NotebookPageEditorPaletteFactory.createPalettePreferences();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot
     * ()
     */
    @Override
    protected PaletteRoot getPaletteRoot() {
        if (palette == null) {
            palette = NotebookPageEditorPaletteFactory.createPalette();
        }
        return palette;
    }

    private void handleLoadException(final Exception e) {
        bilab.Notify
                .userError(
                        this,
                        "Unable to load the specified file as a Notebook.  Creating empty document instead.");
        diagram = new GraphicsDiagram();
    }

    /**
     * Set up the editor's inital content (after creation).
     * 
     * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
     */
    @Override
    protected void initializeGraphicalViewer() {
        super.initializeGraphicalViewer();
        final GraphicalViewer graphicalViewer = getGraphicalViewer();
        graphicalViewer.setContents(getModel()); // set the contents of this
                                                 // editor
        // listen for dropped parts
        graphicalViewer
                .addDropTargetListener(createTransferDropTargetListener());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     */
    @Override
    public boolean isDirty() {
        return getCommandStack().isDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    protected void setInput(final IEditorInput input) {
        super.setInput(input);
        try {
            final IFile file = ((IFileEditorInput) input).getFile();
            final ObjectInputStream in = new ObjectInputStream(
                    file.getContents());
            diagram = (GraphicsDiagram) in.readObject();
            in.close();
            setPartName(file.getName());
        } catch (final IOException e) {
            handleLoadException(e);
        } catch (final CoreException e) {
            handleLoadException(e);
        } catch (final ClassNotFoundException e) {
            handleLoadException(e);
        } catch (final Exception e) {
            handleLoadException(e);
        }
    }
}