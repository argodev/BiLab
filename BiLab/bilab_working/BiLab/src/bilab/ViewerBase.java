package bilab;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISelectionListener;

import scigol.accessor;

@Sophistication(Sophistication.Developer)
public abstract class ViewerBase extends Viewer implements ISelectionListener {
    // all subclasses are required to have this constructor (which we'll look
    // for via reflection)
    /* public abstract ViewerBase ViewerBase(Composite parent); */

    public abstract void dispose();

    @accessor
    public abstract String get_description();

    @accessor
    @Summary("is this viewer in-line or does is use an external viewer component?")
    public boolean get_IsExternal() {
        return false;
    }

    @accessor
    public abstract String get_title();

    @Override
    public ISelection getSelection() {
        return StructuredSelection.EMPTY;
    }

    public abstract Point maximumSize();

    // provide some default implementations for convenience

    public abstract Point preferedSize();

    @Override
    public void setSelection(final ISelection selection, final boolean reveal) {
    }
}