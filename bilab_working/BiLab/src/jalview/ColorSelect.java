package jalview;

import java.awt.*;
import java.applet.*;



public class ColorSelect extends Panel {

  SliderPanel mySliderPanel;         // Contains the colour sliders
  public ColorPanel myColorPanel;    // A canvas displaying the colour selected

  // RGB values
  int Red;
  int Green;
  int Blue;


  // Constructors - these are run when the object is created
  public ColorSelect() {

    mySliderPanel = new SliderPanel();
    myColorPanel = new ColorPanel();

    setLayout(new GridLayout(1,2));

    setColor(Red,Green,Blue);


    add(mySliderPanel);
    add(myColorPanel);

    //This is needed for the canvas to be seen at all

    myColorPanel.pogCanvas.resize(size().width/2,size().height);

  }

  public ColorSelect(Color c) {

    mySliderPanel = new SliderPanel();
    myColorPanel = new ColorPanel();

    setLayout(new GridLayout(1,2));

    Red = c.getRed();
    Green = c.getGreen();
    Blue = c.getBlue(); 

    setColor(Red,Green,Blue);

    add(mySliderPanel);
    add(myColorPanel);

  } 

  public Color getColor() {
    return new Color(Red,Green,Blue);
  }


  // Two methods which set the colour on the panel
  // They update the stored values in the object (Red,Green,Blue)
  // and also update the screen (sliders and canvas)
  public void setColor(Color c) {
    setColor(c.getRed(),c.getGreen(),c.getBlue());
  }

  public void setColor(int red, int green, int blue) {

    Red = red;
    Green = green;
    Blue = blue;

    myColorPanel.pogCanvas.red = red;
    myColorPanel.pogCanvas.green = green;
    myColorPanel.pogCanvas.blue = blue;

    mySliderPanel.redSlider.setValue(red);
    mySliderPanel.greenSlider.setValue(green);
    mySliderPanel.blueSlider.setValue(blue);

  }

  

  //Handle the scrollbar events
  // This method is called automatically when a graphics event occurs

  public boolean handleEvent(Event e) {

    if (e.target == mySliderPanel.redSlider.slider) {
      switch(e.id) {
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_PAGE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_ABSOLUTE:

        Red = ((Integer)e.arg).intValue();
        break;

      }

      setColor(Red,Green,Blue);
      myColorPanel.pogCanvas.paint(myColorPanel.pogCanvas.getGraphics());
      return true;

    }


  if (e.target == mySliderPanel.greenSlider.slider) {
      switch(e.id) {
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_PAGE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_ABSOLUTE:

        Green = ((Integer)e.arg).intValue();
        break;

      }

      setColor(Red,Green,Blue);
      myColorPanel.pogCanvas.paint(myColorPanel.pogCanvas.getGraphics());
      return true;

    }

  if (e.target == mySliderPanel.blueSlider.slider) {
      switch(e.id) {
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_PAGE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_ABSOLUTE:

        Blue = ((Integer)e.arg).intValue();
        break;

      }

      setColor(Red,Green,Blue);
      myColorPanel.pogCanvas.paint(myColorPanel.pogCanvas.getGraphics());
      return true;

    }
    // If we haven't handled the event pass it to the container class
    return(super.handleEvent(e));

 }


 //Deal with textfields in here - updates only done after a return

 public boolean action(Event e, Object arg) {

   if (e.target == mySliderPanel.redSlider.tfield) {
     Red = Integer.valueOf(mySliderPanel.redSlider.tfield.getText()).intValue();

     mySliderPanel.redSlider.slider.setValue(Red);

     setColor(Red,Green,Blue);

     myColorPanel.pogCanvas.paint(myColorPanel.pogCanvas.getGraphics());

     return true;

   }

   if (e.target == mySliderPanel.greenSlider.tfield) {

     Green = Integer.valueOf(mySliderPanel.greenSlider.tfield.getText()).intValue();

     mySliderPanel.greenSlider.slider.setValue(Green);

     setColor(Red,Green,Blue);

     myColorPanel.pogCanvas.paint(myColorPanel.pogCanvas.getGraphics());

     return true;

   }

   if (e.target == mySliderPanel.blueSlider.tfield) {

     Blue = Integer.valueOf(mySliderPanel.blueSlider.tfield.getText()).intValue();

     mySliderPanel.blueSlider.slider.setValue(Blue);

     setColor(Red,Green,Blue);

     myColorPanel.pogCanvas.paint(myColorPanel.pogCanvas.getGraphics());

     return true;

   }



   return false;

 }



     
// this method is run first - a frame is created and then
// a ColorSelect object.  The ColorSelect object is put into
// the frame and displayed.

 public static void main(String[] args) {
   // Create frame
   Frame f = new Frame("Colour selector");

   // Create ColorSelect object
   ColorSelect cs = new ColorSelect(new Color(100,1,24));

   // How do we want the components laid out
   f.setLayout(new BorderLayout());

   // Put the color panel in the frame
   f.add("Center",cs);

   // resize the frame
   f.resize(300,200);

   // and finally display
   f.show();

  }
   

} //End of class ColorSelect


// This is the SliderPanel class

class SliderPanel extends Panel {

  public SPanel redSlider, greenSlider, blueSlider;
  int red,green,blue;



  public SliderPanel() {

    redSlider = new SPanel(red,0,255);
    greenSlider = new SPanel(green,0,255);
    blueSlider = new SPanel(blue,0,255);

    setLayout(new GridLayout(3,1));

    add(redSlider);
    add(greenSlider);
    add(blueSlider);

  }

} // End of class SliderPanel


class  SPanel extends Panel {

  Scrollbar slider;
  Canvas tmpCanvas;
  TextField tfield;
  int scrollvalue;

  public SPanel(int value, int min, int max) {

    super();
    scrollvalue = value;

    slider = new Scrollbar(Scrollbar.HORIZONTAL,0,5,min,max);

    tmpCanvas = new Canvas();

    tfield = new TextField(Integer.toString(scrollvalue));
      setLayout(new GridLayout(2,1));
      add(slider);
      add(tfield);

  }

  public void setValue(int value) {

    this.scrollvalue = value;
    slider.setValue(scrollvalue);
    tfield.setText(Integer.toString(value));

  }



}



class ColorPanel extends Panel {

  CSPogCanvas pogCanvas;
  int red,green,blue;

  public ColorPanel() {

    super();
    pogCanvas = new CSPogCanvas();
    setLayout(new BorderLayout());

    add("Center",pogCanvas);

  }

  public Dimension preferredSize() {

    return new Dimension(100,100);

  }


  public Dimension minimumSize() {

    return new Dimension(25,25);

  }




// Class canvas needs to be subclassed otherwise the paint method
// doens't seem to be called when the scrollvar events occur


class CSPogCanvas extends Canvas {

  int red,green,blue;

  public CSPogCanvas() {

    super();

  }



  public void paint(Graphics g) {

    if (g != null) {

      g.setColor(new Color(red,green,blue));
      g.fill3DRect(10,10,size().width-20,size().height-20,true);

    }

  }



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
