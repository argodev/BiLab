package jalview;

import java.awt.*;

public class ColourChooserFrame extends Frame {


  ColourChooserPanel ccp;

  public ColourChooserFrame(AlignFrame af, Color[] color) {
    super("User colours");
    ccp = new ColourChooserPanel(af,color);
    setLayout(new BorderLayout());
    add("Center",ccp);
    resize(ccp.getPreferredSize().width,ccp.getPreferredSize().height);
  }

  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {
	this.hide();
	this.dispose();
    }
    else super.handleEvent(evt);
    return false;
  }
}
