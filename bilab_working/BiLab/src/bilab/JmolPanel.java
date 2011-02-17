package bilab;

import java.awt.*;
import javax.swing.*;

import org.jmol.api.ModelAdapter;
import org.jmol.adapter.smarter.SmarterModelAdapter;
import org.openscience.jmol.viewer.JmolViewer;

class JmolPanel extends JPanel 
{
  JmolViewer viewer;
  ModelAdapter adapter;
  
  JmolPanel() 
  {
    adapter = new SmarterModelAdapter(null);
    viewer = new JmolViewer(this, adapter);
  }

  public JmolViewer getViewer() {
    return viewer;
  }

  Dimension currentSize = new Dimension();

  public void paint(Graphics g) {
    //viewer.setScreenDimension(getSize(currentSize));
    getSize(currentSize);
    viewer.setScreenDimension(currentSize);
    Rectangle rectClip = new Rectangle();
    g.getClipBounds(rectClip);
    viewer.renderScreenImage(g, currentSize, rectClip);
  }
}