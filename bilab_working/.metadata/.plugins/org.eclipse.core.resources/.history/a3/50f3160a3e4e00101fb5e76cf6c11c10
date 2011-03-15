/**
* This document is a part of the source code and related artifacts for BiLab, an open source interactive workbench for 
* computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
* Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
* details.
*
* You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to 
* the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* The license is also available at: http://www.gnu.org/copyleft/lgpl.html
*/

package bilab;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;


import scigol.*;


public class EnvNavigatorView extends ViewPart 
{
  private TreeViewer viewer;
  private DrillDownAdapter drillDownAdapter;
  private Action action1;
  private Action action2;
  private Action doubleClickAction;
  private Action selectionChangedAction;
  
  
  
  class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
    private Object invisibleRoot;
    
    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }
    
    public void dispose() {
    }
    
    public Object[] getElements(Object parent) {
      if (parent.equals(getViewSite())) {
        if (invisibleRoot==null) initialize();
        return getChildren(invisibleRoot);
      }
      return getChildren(parent);
    }
    
    public Object getParent(Object child) {
      
      if (child instanceof Scope) {
        return ((Scope)child).getOuter();
      }
      
      return null;
    }
    
    
    public Object [] getChildren(Object parent) {
      try {
        if ((parent instanceof String) && parent.equals("namespaces")) {
          LinkedList<NamespaceScope> namespaces = new LinkedList<NamespaceScope>();
          namespaces.add(globalScope);
          
          // get namespace list
          AbstractList<String> namespaceNames = NamespaceScope.getNamespaceNames();
          
          for(String namespaceName : namespaceNames) {
            NamespaceScope nsScope = globalScope.getNamespaceScope(namespaceName);
            if (nsScope != null)
              namespaces.add(nsScope);
            //else
            //  Debug.Warning("a scope for namespace '"+namespaceName+"' could not be created");
          }
          
          namespaces.add(globalScope.getNamespaceScope("bilab.lib")); //!!! this is a scigol define only namespace
  
          return namespaces.toArray();
        }
        else if (parent instanceof Scope) {
          if (parent instanceof NamespaceScope)
            return ((NamespaceScope)parent).getDeclaredEntries(null,null);
          else if (parent instanceof ClassScope)
            //!!! in the case of ClassScope we should filter out accessors here
            return ((ClassScope)parent).getClassType().getClassInfo().getDeclaredEntries(null);
          return ((Scope)parent).getEntries(null,null);
        }
        else if (parent instanceof Entry) {
          Entry e = (Entry)parent;
          if (e.type.isType() && (e.getStaticValue() != null)) {
            TypeSpec type = (TypeSpec)e.getStaticValue();
            if (type.isClassOrBuiltinClassOrInterface()) {
              ClassInfo cinfo = type.getClassInfo();
              return cinfo.getAllEntries(null);
            }
          }
        }
      } catch (Throwable t) {
        Notify.devWarning(this,"Exception creating children for "+parent+" - "+t);
        t.printStackTrace();
      }
      
      return new Object[0];
    }
    
    
    public boolean hasChildren(Object parent) {
      return (getChildren(parent).length > 0);
    }
    
    
    private void initialize() {
      globalScope = BilabPlugin.getDefault().getGlobalScope();
      
      invisibleRoot = "namespaces";
    }
    
    
    NamespaceScope globalScope;
  }
  
  
  class ViewLabelProvider extends LabelProvider {
    
    public ViewLabelProvider(ViewContentProvider cp)
    {
      viewContentProvider = cp;
    }
    
    public String getText(Object obj) {
      
      try {
        if (obj instanceof Scope) {
          if (obj instanceof NamespaceScope) {
            String name = ((NamespaceScope)obj).fullNamespaceName();
            if (!name.equals(""))
              return name;
            else
              return "<global>";
          }
          else if (obj instanceof LocalScope)
            return "<local>";
          else if (obj instanceof ClassScope)
            return ((ClassScope)obj).getClassType().typeName();
        }
        else if (obj instanceof Entry) {
          Entry e = (Entry)obj;
          String s = e.name;
          
          TypeSpec type = e.type;
          //!!! if property then getStaticValue will be an EntryPair
          if (!e.isProperty()) {
            if (type.isAny()) type = TypeSpec.typeOf(((Any)e.getStaticValue()).value);
            if (type.isNum()) type = TypeSpec.typeOf(((Num)e.getStaticValue()).value);
          }
          
          if (e.type.isAny() || e.type.isNum())
            s = s + " :" +e.type + "(" + type + ")";
          else
            s = s + " :" +type;
          
          if (type.isBuiltin() && !type.isAny() && !type.isMap() && !type.isList() && !type.isMatrix() && !type.isVector()) {
            Object v = e.getStaticValue();
            if (v != null)
              s = s + " = " + e.getStaticValue().toString();
            else
              s = s + " = null"; 
          }
          Object v2 = e.getStaticValue();
          if ((v2 != null) && (v2 instanceof seq)) {
            seq sq = (seq)v2;
            if ((sq.get_name().length() > 0) &&  (!(sq.get_name().equals("untitled"))))
              s = s + " = " + sq.get_name();
          }
          
          // display Summary annotation, if any
          final scigol.TypeSpec summaryAnnotationType = new scigol.TypeSpec(Summary.class);
          
          Annotation summaryAnnot = e.getAnnotation(summaryAnnotationType);
          if (summaryAnnot != null) {
            String summary = null;
            if (summaryAnnot instanceof scigol.ScigolAnnotation) {
              scigol.Value v = (scigol.Value)((scigol.ScigolAnnotation)summaryAnnot).getMembers().get(0);
              summary = (String)v.getValue();
            }
            else
              summary = ((Summary)summaryAnnot).value();
            
            s = s + " - "+summary;
          }
          
          
          return s;
        }
        
        return obj.toString();
      } catch (Throwable t) {
        return "<error>";
      }
    }
    
    public Image getImage(Object obj) {
      String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
      if ((obj instanceof NamespaceScope) || viewContentProvider.hasChildren(obj))
        imageKey = ISharedImages.IMG_OBJ_FOLDER;
      return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
    }
    
    protected ViewContentProvider viewContentProvider;
  }
  
  
  class NameSorter extends ViewerSorter {
  }
  
  /**
   * The constructor.
   */
  public EnvNavigatorView() {
  }
  
  /**
   * This is a callback that will allow us
   * to create the viewer and initialize it.
   */
  public void createPartControl(Composite parent) {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    drillDownAdapter = new DrillDownAdapter(viewer);
    ViewContentProvider contentProvider = new ViewContentProvider();
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
  
  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager manager) {
        EnvNavigatorView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, viewer);
  }
  
  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }
  
  private void fillLocalPullDown(IMenuManager manager) {
    //manager.add(action1);
    //manager.add(new Separator());
    //manager.add(action2);
  }
  
  private void fillContextMenu(IMenuManager manager) {
    manager.add(action1);
    manager.add(action2);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }
  
  private void fillLocalToolBar(IToolBarManager manager) {
    //manager.add(action1);
    //manager.add(action2);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
  }
  
  private void makeActions() {
    action1 = new Action() {
      public void run() {
        showMessage("Action 1 executed");
      }
    };
    action1.setText("Action 1");
    action1.setToolTipText("Action 1 tooltip");
    action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
        getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    
    action2 = new Action() {
      public void run() {
        showMessage("Action 2 executed");
      }
    };
    action2.setText("Action 2");
    action2.setToolTipText("Action 2 tooltip");
    action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
        getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    
    
    selectionChangedAction = new Action() {
      public void run() {
        
        //!!! hack alert.  Until we adapt the two different selection mechanisms of JFace & the workbench UI,
        //  we'll just assume there is a single ValueView & directly set its input via a static method
        ValueView valueView = ValueView.getInstance();

        try {
          ISelection selection = viewer.getSelection();
          Object obj = ((IStructuredSelection)selection).getFirstElement();
          
          if (valueView != null) {
            
            // not all selection types trigger a Value view change
            
            if (obj instanceof Entry) {
              Entry e = (Entry)obj;
              Object v = e.getStaticValue();
              
              TypeSpec type = e.type;
              if (type.isAny()) type = TypeSpec.typeOf(((Any)v).value);
              if (type.isNum()) type = TypeSpec.typeOf(((Num)v).value);
              
              if (type.equals(new TypeSpec(scigol.NamespaceScope.class))) // don't display namespaces
                return;
              
              if (type.isFunc()) {
                StringBuilder sb = new StringBuilder();
                sb.append( e.name +" : "+((v!=null)?v.toString():"null")+"\n");
                
                // if full help is available, display that, otherwise just display the signature & summary help (if any)
                
                scigol.NamespaceScope globalScope = BilabPlugin.getDefault().getGlobalScope();
                
                scigol.TypeSpec summaryAnnotationType = new scigol.TypeSpec(Summary.class);
                scigol.TypeSpec docAnnotationType = new scigol.TypeSpec(Doc.class);
                
                Annotation docAnnot = e.getAnnotation(docAnnotationType);
                if (docAnnot != null) {
                  
                  // found doc
                  String doc = null;
                  if (docAnnot instanceof scigol.ScigolAnnotation) {
                    scigol.Value val = (scigol.Value)((scigol.ScigolAnnotation)docAnnot).getMembers().get(0);
                    doc = (String)val.getValue();
                  }
                  else
                    doc = ((Doc)docAnnot).value();
                  
                  // if doc is a URL, display the page, otherwise use doc as the text
                  if (!doc.startsWith("file:") && !doc.startsWith("http:"))
                    sb.append(e.name+":\n"+doc);
                  else {
                    // display a URL (will be displayed by the registered HTML viewer)
                    
                    String URLString = doc;
                    try {
                      URL url=null;
                      if (URLString.startsWith("file:")) { // treat is as a resource
                        url = BilabPlugin.findResource(URLString.substring(5));
                      }
                      else
                        url = new URL(URLString);
                      
                      v = url;
                      valueView.setInput(new Value(v));
                      return;
                      
                    } catch (java.net.MalformedURLException ex) {
                      sb.append(doc);
                    } catch (IOException ex) {
                      throw new BilabException("error reading help resource "+URLString+" - "+ex);
                    }
                  }
                  
                }
                else { // no doc found, at least look for summary
                  
                  Annotation summaryAnnot = e.getAnnotation(summaryAnnotationType);
                  if (summaryAnnot != null) {
                    String summary = null;
                    if (summaryAnnot instanceof scigol.ScigolAnnotation) {
                      scigol.Value val = (scigol.Value)((scigol.ScigolAnnotation)summaryAnnot).getMembers().get(0);
                      summary = (String)val.getValue();
                    }
                    else
                      summary = ((Summary)summaryAnnot).value();
                    
                    
                    sb.append(summary);
                  }
                  
                  
                }
                
                v = sb.toString();
              }
              
              if (!(v instanceof NamespaceScope))
                valueView.setInput(new Value(v));
              
            }
          }  
        } catch (Throwable t) {
          if (valueView != null)
          valueView.setInput(new Value("<error obtaining value: "+t+">"));
        }

      }
    };
    

    doubleClickAction = selectionChangedAction; /*= new Action() {
      public void run() {
        ISelection selection = viewer.getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        showMessage("Double-click detected on "+obj.toString());
      }
    };*/

  }
  
  private void hookDoubleClickAction() {
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      public void doubleClick(DoubleClickEvent event) {
        doubleClickAction.run();
      }
    });
  }
  
  
  private void hookPostSelectionSelectionChangedAction() {
    viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent e) {
        selectionChangedAction.run();
      }
    });
  }
  
  
  private void showMessage(String message) {
    MessageDialog.openInformation(
        viewer.getControl().getShell(),
        "Environment Navigator",
        message);
  }
  
  /**
   * Passing the focus request to the viewer's control.
   */
  public void setFocus() {
    viewer.getControl().setFocus();
  }
  
  
  
  
}