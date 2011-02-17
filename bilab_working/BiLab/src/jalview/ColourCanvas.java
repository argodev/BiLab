package jalview;
import java.awt.*;


public class ColourCanvas extends Canvas {
  public Color c;
  
  public ColourCanvas(Color c) {
    this.c = c;
  }

  public void paint(Graphics g) {
    g.setColor(c);
    g.fillRect(0,0,size().width, size().height);
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }
  public Dimension getPreferredSize() {
    return new Dimension(40,40);
  }
} 
