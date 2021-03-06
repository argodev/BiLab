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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.AbstractList;
import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import scigol.Any;
import scigol.ClassInfo;
import scigol.ClassScope;
import scigol.Entry;
import scigol.LocalScope;
import scigol.NamespaceScope;
import scigol.Num;
import scigol.Scope;
import scigol.TypeSpec;
import scigol.Value;

public class EnvNavigatorView extends ViewPart {
    class NameSorter extends ViewerSorter {
    }

    class ViewContentProvider implements IStructuredContentProvider,
            ITreeContentProvider {
        private Object invisibleRoot;

        NamespaceScope globalScope;

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getChildren(final Object parent) {
            try {
                if ((parent instanceof String) && parent.equals("namespaces")) {
                    final LinkedList<NamespaceScope> namespaces = new LinkedList<NamespaceScope>();
                    namespaces.add(globalScope);

                    // get namespace list
                    final AbstractList<String> namespaceNames = NamespaceScope
                            .getNamespaceNames();

                    for (final String namespaceName : namespaceNames) {
                        final NamespaceScope nsScope = globalScope
                                .getNamespaceScope(namespaceName);
                        if (nsScope != null) {
                            namespaces.add(nsScope);
                            // else
                            // Debug.Warning("a scope for namespace '"+namespaceName+"' could not be created");
                        }
                    }

                    namespaces.add(globalScope.getNamespaceScope("bilab.lib")); // !!!
                                                                                // this
                                                                                // is
                                                                                // a
                                                                                // scigol
                                                                                // define
                                                                                // only
                                                                                // namespace

                    return namespaces.toArray();
                } else if (parent instanceof Scope) {
                    if (parent instanceof NamespaceScope) {
                        return ((NamespaceScope) parent).getDeclaredEntries(
                                null, null);
                    } else if (parent instanceof ClassScope) {
                        // !!! in the case of ClassScope we should filter out
                        // accessors here
                        return ((ClassScope) parent).getClassType()
                                .getClassInfo().getDeclaredEntries(null);
                    }
                    return ((Scope) parent).getEntries(null, null);
                } else if (parent instanceof Entry) {
                    final Entry e = (Entry) parent;
                    if (e.type.isType() && (e.getStaticValue() != null)) {
                        final TypeSpec type = (TypeSpec) e.getStaticValue();
                        if (type.isClassOrBuiltinClassOrInterface()) {
                            final ClassInfo cinfo = type.getClassInfo();
                            return cinfo.getAllEntries(null);
                        }
                    }
                }
            } catch (final Throwable t) {
                Notify.devWarning(this, "Exception creating children for "
                        + parent + " - " + t);
                t.printStackTrace();
            }

            return new Object[0];
        }

        @Override
        public Object[] getElements(final Object parent) {
            if (parent.equals(getViewSite())) {
                if (invisibleRoot == null) {
                    initialize();
                }
                return getChildren(invisibleRoot);
            }
            return getChildren(parent);
        }

        @Override
        public Object getParent(final Object child) {

            if (child instanceof Scope) {
                return ((Scope) child).getOuter();
            }

            return null;
        }

        @Override
        public boolean hasChildren(final Object parent) {
            return (getChildren(parent).length > 0);
        }

        private void initialize() {
            globalScope = BilabPlugin.getDefault().getGlobalScope();

            invisibleRoot = "namespaces";
        }

        @Override
        public void inputChanged(final Viewer v, final Object oldInput,
                final Object newInput) {
        }
    }

    class ViewLabelProvider extends LabelProvider {

        protected ViewContentProvider viewContentProvider;

        public ViewLabelProvider(final ViewContentProvider cp) {
            viewContentProvider = cp;
        }

        @Override
        public Image getImage(final Object obj) {
            String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
            if ((obj instanceof NamespaceScope)
                    || viewContentProvider.hasChildren(obj)) {
                imageKey = ISharedImages.IMG_OBJ_FOLDER;
            }
            return PlatformUI.getWorkbench().getSharedImages()
                    .getImage(imageKey);
        }

        @Override
        public String getText(final Object obj) {

            try {
                if (obj instanceof Scope) {
                    if (obj instanceof NamespaceScope) {
                        final String name = ((NamespaceScope) obj)
                                .fullNamespaceName();
                        if (!name.equals("")) {
                            return name;
                        } else {
                            return "<global>";
                        }
                    } else if (obj instanceof LocalScope) {
                        return "<local>";
                    } else if (obj instanceof ClassScope) {
                        return ((ClassScope) obj).getClassType().typeName();
                    }
                } else if (obj instanceof Entry) {
                    final Entry e = (Entry) obj;
                    String s = e.name;

                    TypeSpec type = e.type;
                    // !!! if property then getStaticValue will be an EntryPair
                    if (!e.isProperty()) {
                        if (type.isAny()) {
                            type = TypeSpec
                                    .typeOf(((Any) e.getStaticValue()).value);
                        }
                        if (type.isNum()) {
                            type = TypeSpec
                                    .typeOf(((Num) e.getStaticValue()).value);
                        }
                    }

                    if (e.type.isAny() || e.type.isNum()) {
                        s = s + " :" + e.type + "(" + type + ")";
                    } else {
                        s = s + " :" + type;
                    }

                    if (type.isBuiltin() && !type.isAny() && !type.isMap()
                            && !type.isList() && !type.isMatrix()
                            && !type.isVector()) {
                        final Object v = e.getStaticValue();
                        if (v != null) {
                            s = s + " = " + e.getStaticValue().toString();
                        } else {
                            s = s + " = null";
                        }
                    }
                    final Object v2 = e.getStaticValue();
                    if ((v2 != null) && (v2 instanceof seq)) {
                        final seq sq = (seq) v2;
                        if ((sq.get_name().length() > 0)
                                && (!(sq.get_name().equals("untitled")))) {
                            s = s + " = " + sq.get_name();
                        }
                    }

                    // display Summary annotation, if any
                    final scigol.TypeSpec summaryAnnotationType = new scigol.TypeSpec(
                            Summary.class);

                    final Annotation summaryAnnot = e
                            .getAnnotation(summaryAnnotationType);
                    if (summaryAnnot != null) {
                        String summary = null;
                        if (summaryAnnot instanceof scigol.ScigolAnnotation) {
                            final scigol.Value v = (scigol.Value) ((scigol.ScigolAnnotation) summaryAnnot)
                                    .getMembers().get(0);
                            summary = (String) v.getValue();
                        } else {
                            summary = ((Summary) summaryAnnot).value();
                        }

                        s = s + " - " + summary;
                    }

                    return s;
                }

                return obj.toString();
            } catch (final Throwable t) {
                return "<error>";
            }
        }
    }

    private TreeViewer viewer;
    private DrillDownAdapter drillDownAdapter;
    private Action action1;

    private Action action2;

    private Action doubleClickAction;

    private Action selectionChangedAction;

    /**
     * The constructor.
     */
    public EnvNavigatorView() {
    }

    private void contributeToActionBars() {
        final IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    @Override
    public void createPartControl(final Composite parent) {
        viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        drillDownAdapter = new DrillDownAdapter(viewer);
        final ViewContentProvider contentProvider = new ViewContentProvider();
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new ViewLabelProvider(contentProvider));
        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        hookPostSelectionSelectionChangedAction();
        contributeToActionBars();
    }

    private void fillContextMenu(final IMenuManager manager) {
        manager.add(action1);
        manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalPullDown(final IMenuManager manager) {
        // manager.add(action1);
        // manager.add(new Separator());
        // manager.add(action2);
    }

    private void fillLocalToolBar(final IToolBarManager manager) {
        // manager.add(action1);
        // manager.add(action2);
        manager.add(new Separator());
        drillDownAdapter.addNavigationActions(manager);
    }

    private void hookContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                EnvNavigatorView.this.fillContextMenu(manager);
            }
        });
        final Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(final DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    private void hookPostSelectionSelectionChangedAction() {
        viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent e) {
                selectionChangedAction.run();
            }
        });
    }

    private void makeActions() {
        action1 = new Action() {
            @Override
            public void run() {
                showMessage("Action 1 executed");
            }
        };
        action1.setText("Action 1");
        action1.setToolTipText("Action 1 tooltip");
        action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        action2 = new Action() {
            @Override
            public void run() {
                showMessage("Action 2 executed");
            }
        };
        action2.setText("Action 2");
        action2.setToolTipText("Action 2 tooltip");
        action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

        selectionChangedAction = new Action() {
            @Override
            public void run() {

                // !!! hack alert. Until we adapt the two different selection
                // mechanisms of JFace & the workbench UI,
                // we'll just assume there is a single ValueView & directly set
                // its input via a static method
                final ValueView valueView = ValueView.getInstance();

                try {
                    final ISelection selection = viewer.getSelection();
                    final Object obj = ((IStructuredSelection) selection)
                            .getFirstElement();

                    if (valueView != null) {

                        // not all selection types trigger a Value view change

                        if (obj instanceof Entry) {
                            final Entry e = (Entry) obj;
                            Object v = e.getStaticValue();

                            TypeSpec type = e.type;
                            if (type.isAny()) {
                                type = TypeSpec.typeOf(((Any) v).value);
                            }
                            if (type.isNum()) {
                                type = TypeSpec.typeOf(((Num) v).value);
                            }

                            if (type.equals(new TypeSpec(
                                    scigol.NamespaceScope.class))) {
                                // display
                                // namespaces
                                return;
                            }

                            if (type.isFunc()) {
                                final StringBuilder sb = new StringBuilder();
                                sb.append(e.name + " : "
                                        + ((v != null) ? v.toString() : "null")
                                        + "\n");

                                // if full help is available, display that,
                                // otherwise just display the signature &
                                // summary help (if any)

                                final scigol.NamespaceScope globalScope = BilabPlugin
                                        .getDefault().getGlobalScope();

                                final scigol.TypeSpec summaryAnnotationType = new scigol.TypeSpec(
                                        Summary.class);
                                final scigol.TypeSpec docAnnotationType = new scigol.TypeSpec(
                                        Doc.class);

                                final Annotation docAnnot = e
                                        .getAnnotation(docAnnotationType);
                                if (docAnnot != null) {

                                    // found doc
                                    String doc = null;
                                    if (docAnnot instanceof scigol.ScigolAnnotation) {
                                        final scigol.Value val = (scigol.Value) ((scigol.ScigolAnnotation) docAnnot)
                                                .getMembers().get(0);
                                        doc = (String) val.getValue();
                                    } else {
                                        doc = ((Doc) docAnnot).value();
                                    }

                                    // if doc is a URL, display the page,
                                    // otherwise use doc as the text
                                    if (!doc.startsWith("file:")
                                            && !doc.startsWith("http:")) {
                                        sb.append(e.name + ":\n" + doc);
                                    } else {
                                        // display a URL (will be displayed by
                                        // the registered HTML viewer)

                                        final String URLString = doc;
                                        try {
                                            URL url = null;
                                            if (URLString.startsWith("file:")) { // treat
                                                                                 // is
                                                                                 // as
                                                                                 // a
                                                                                 // resource
                                                url = BilabPlugin
                                                        .findResource(URLString
                                                                .substring(5));
                                            } else {
                                                url = new URL(URLString);
                                            }

                                            v = url;
                                            valueView.setInput(new Value(v));
                                            return;

                                        } catch (final java.net.MalformedURLException ex) {
                                            sb.append(doc);
                                        } catch (final IOException ex) {
                                            throw new BilabException(
                                                    "error reading help resource "
                                                            + URLString + " - "
                                                            + ex);
                                        }
                                    }

                                } else { // no doc found, at least look for
                                         // summary

                                    final Annotation summaryAnnot = e
                                            .getAnnotation(summaryAnnotationType);
                                    if (summaryAnnot != null) {
                                        String summary = null;
                                        if (summaryAnnot instanceof scigol.ScigolAnnotation) {
                                            final scigol.Value val = (scigol.Value) ((scigol.ScigolAnnotation) summaryAnnot)
                                                    .getMembers().get(0);
                                            summary = (String) val.getValue();
                                        } else {
                                            summary = ((Summary) summaryAnnot)
                                                    .value();
                                        }

                                        sb.append(summary);
                                    }

                                }

                                v = sb.toString();
                            }

                            if (!(v instanceof NamespaceScope)) {
                                valueView.setInput(new Value(v));
                            }

                        }
                    }
                } catch (final Throwable t) {
                    if (valueView != null) {
                        valueView.setInput(new Value("<error obtaining value: "
                                + t + ">"));
                    }
                }

            }
        };

        doubleClickAction = selectionChangedAction; /*
                                                     * = new Action() { public
                                                     * void run() { ISelection
                                                     * selection =
                                                     * viewer.getSelection();
                                                     * Object obj =
                                                     * ((IStructuredSelection
                                                     * )selection
                                                     * ).getFirstElement();
                                                     * showMessage
                                                     * ("Double-click detected on "
                                                     * +obj.toString()); } };
                                                     */

    }

    /**
     * Passing the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    private void showMessage(final String message) {
        MessageDialog.openInformation(viewer.getControl().getShell(),
                "Environment Navigator", message);
    }

}