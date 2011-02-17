package bilab.notebook;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;

import bilab.BilabPlugin;
import bilab.notebook.model.Connection;
import bilab.notebook.model.EllipticalGraphic;
import bilab.notebook.model.RectangularGraphic;

/**
 * Utility class that can create a GEF Palette.
 * @see #createPalette() 
 * @author Elias Volanakis
 */
final class NotebookPageEditorPaletteFactory {
  /** Default palette size. */
  private static final int DEFAULT_PALETTE_SIZE = 125;
  
  /** Preference ID used to persist the palette location. */
  private static final String PALETTE_DOCK_LOCATION = "ShapesEditorPaletteFactory.Location";
  /** Preference ID used to persist the palette size. */
  private static final String PALETTE_SIZE = "ShapesEditorPaletteFactory.Size";
  /** Preference ID used to persist the flyout palette's state. */
  private static final String PALETTE_STATE = "ShapesEditorPaletteFactory.State";
  
  /** Create the "Shapes" drawer. */
  private static PaletteContainer createShapesDrawer() {
    PaletteDrawer componentsDrawer = new PaletteDrawer("Graphics");
    
    CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry(
        "Ellipse", 
        "Create an elliptical shape", 
        EllipticalGraphic.class,
        new SimpleFactory(EllipticalGraphic.class), 
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/ellipse16.gif"), 
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/ellipse24.gif"));
    componentsDrawer.add(component);
    
    component = new CombinedTemplateCreationEntry(
        "Rectangle",
        "Create a rectangular shape", 
        RectangularGraphic.class,
        new SimpleFactory(RectangularGraphic.class), 
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/rectangle16.gif"), 
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/rectangle24.gif"));
    componentsDrawer.add(component);
    
    return componentsDrawer;
  }
  
  /**
   * Creates the PaletteRoot and adds all palette elements.
   * Use this factory method to create a new palette for your graphical editor.
   * @return a new PaletteRoot
   */
  static PaletteRoot createPalette() {
    PaletteRoot palette = new PaletteRoot();
    palette.add(createToolsGroup(palette));
    palette.add(createShapesDrawer());
    return palette;
  }
  
  /**
   * Return a FlyoutPreferences instance used to save/load the preferences of a flyout palette.
   */
  static FlyoutPreferences createPalettePreferences() {
    // set default flyout palette preference values, in case the preference store
    // does not hold stored values for the given preferences
    getPreferenceStore().setDefault(PALETTE_DOCK_LOCATION, -1);
    getPreferenceStore().setDefault(PALETTE_STATE, -1);
    getPreferenceStore().setDefault(PALETTE_SIZE, DEFAULT_PALETTE_SIZE);
    
    return new FlyoutPreferences() {
      public int getDockLocation() {
        return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
      }
      public int getPaletteState() {
        return getPreferenceStore().getInt(PALETTE_STATE);
      }
      public int getPaletteWidth() {
        return getPreferenceStore().getInt(PALETTE_SIZE);
      }
      public void setDockLocation(int location) {
        getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
      }
      public void setPaletteState(int state) {
        getPreferenceStore().setValue(PALETTE_STATE, state);
      }
      public void setPaletteWidth(int width) {
        getPreferenceStore().setValue(PALETTE_SIZE, width);
      }
    };
  }
  
  /** Create the "Tools" group. */
  private static PaletteContainer createToolsGroup(PaletteRoot palette) {
    PaletteGroup toolGroup = new PaletteGroup("Tools");
    
    // Add a selection tool to the group
    ToolEntry tool = new SelectionToolEntry();
    toolGroup.add(tool);
    palette.setDefaultEntry(tool);
    
    // Add a marquee tool to the group
    toolGroup.add(new MarqueeToolEntry());
    
    // Add a (unnamed) separator to the group
    toolGroup.add(new PaletteSeparator());
    
    // Add (solid-line) connection tool 
    tool = new ConnectionCreationToolEntry(
        "Solid connection",
        "Create a solid-line connection",
        new CreationFactory() {
          public Object getNewObject() { return null; }
          // see ShapeEditPart#createEditPolicies() 
          // this is abused to transmit the desired line style 
          public Object getObjectType() { return Connection.SOLID_CONNECTION; }
        },
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/connection_s16.gif"),
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/connection_s24.gif"));
    toolGroup.add(tool);
    
    // Add (dashed-line) connection tool
    tool = new ConnectionCreationToolEntry(
        "Dashed connection",
        "Create a dashed-line connection",
        new CreationFactory() {
          public Object getNewObject() { return null; }
          // see ShapeEditPart#createEditPolicies()
          // this is abused to transmit the desired line style 
          public Object getObjectType() { return Connection.DASHED_CONNECTION; }
        },
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/connection_d16.gif"),
        ImageDescriptor.createFromFile(BilabPlugin.class, "notebook/icons/connection_d24.gif"));
    toolGroup.add(tool);
    
    return toolGroup;
  }
  
  /** 
   * Returns the preference store for the ShapesPlugin.
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore() 
   */
  private static IPreferenceStore getPreferenceStore() {
    return BilabPlugin.getDefault().getPreferenceStore();
  }
  
  /** Utility class. */
  private NotebookPageEditorPaletteFactory() {
    // Utility class
  }
  
}