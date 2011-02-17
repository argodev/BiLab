package bilab.notebook.model;

import org.eclipse.swt.graphics.Image;

/**
 * An elliptical shape.
 */
public class EllipticalGraphic extends Graphic {
  
  /** A 16x16 pictogram of an elliptical shape. */
  private static final Image ELLIPSE_ICON = createImage("notebook/icons/ellipse16.gif");
  
  private static final long serialVersionUID = 1;
  
  public Image getIcon() {
    return ELLIPSE_ICON;
  }
  
  public String toString() {
    return "Ellipse " + hashCode();
  }
}
