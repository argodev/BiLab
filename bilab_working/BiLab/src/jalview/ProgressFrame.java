package jalview;

import java.awt.*;


public class ProgressFrame extends Frame {
  Object parent;
  ProgressPanel pp;
  TextArea ta;
  TextAreaPrintStream taps;

  public ProgressFrame(String title,Object parent,Thread th) {
    super(title);
    this.parent = parent;
    pp = new ProgressPanel(this,th);
    setLayout(new GridLayout(2,1));
    ta = new TextArea(50,20);
    taps = new TextAreaPrintStream(System.out,ta);
    resize(400,500);
    add(pp);
    add(ta);
  }

  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {

      if (pp.ct instanceof CommandThread) {
	pp.status.setText("Destroying process");
	((CommandThread)pp.ct).p.destroy();
      }
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
