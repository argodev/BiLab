package jalview;

import java.awt.*;


public class PCAFrame extends Frame {
  Object parent;

  public PCAFrame(String title,Object parent) {
    super(title);
    this.parent = parent;
  }
  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {
      if (parent != null) {
	this.hide();
	this.dispose();
      } else if (parent == null) {
	System.exit(0);
      }
    }
    else super.handleEvent(evt);
    return false;
    }

}

