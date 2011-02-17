package jalview;



import java.awt.*;


public class Pog extends Panel {

  // These are variables and can be used
  // throughout the object
  PogCanvas pogCanvas;
  Color color;


  // Constructor - run when the object is created
  public Pog(Color c) {
    super();   // does default stuff in the Panel class

    this.color = c;  // Set the color variable

    // Create the graphics canvas
    pogCanvas = new PogCanvas(color);

    // Graphics things
    setLayout(new BorderLayout());
    add("Center",pogCanvas);

  }


  // These two methods give the jvm an idea of what size
  // they'd like to be
  public Dimension preferredSize() {
    return new Dimension(100,100);
  }


  public Dimension minimumSize() {
    return new Dimension(25,25);
  }

  // This is the method that is run when the program starts
  public static void main(String[] args) {
    // First create a frame to put the window in
    Frame myFrame = new Frame("Pog");

    // Now create a panel to put in the frame
    Panel myPanel = new Panel();

    // Now create the Pog object to put in the panel
    Color c = new Color(100,50,255);
    Pog myPog = new Pog(c);

    // do the graphics layout type things
    myFrame.setLayout(new BorderLayout());
    myPanel.setLayout(new BorderLayout());

    // add the Pog object to the panel
    myPanel.add("Center",myPog);

    // add the Panel to the Frame
    myFrame.add("Center",myPanel);

    // Resize the frame
    myFrame.resize(500,500);
    
    // And finally display
    myFrame.show();
  }





// Class canvas needs to be subclassed otherwise the paint method
// doens't seem to be called when the scrollvar events occur


class PogCanvas extends Canvas {

  Color color;

  public PogCanvas(Color c) {
    super();
    this.color = c;
  }

  public void paint(Graphics g) {

    if (g != null) {
      g.setColor(color);

      // Draw the background colour
      g.fill3DRect(10,10,size().width-20,size().height-20,true);

      // Now draw some lines
      // size() is a method inherited from Canvas

      int width = size().width;
      int height = size().height;
      
      g.setColor(Color.BLACK);

      g.drawLine(20,20,width-40,height-40);
      g.drawLine(width-40,20,20,height-40);

    }

  }

  // This is so there is no flickering
  public void update(Graphics g) {
    paint(g);
  }

  public Dimension preferredSize() {
    return new Dimension(200,200);
  }

  public Dimension minimumSize() {
    return new Dimension(25,25);
  }

} //End of class myCanvas

} //End of class ColorPanel

