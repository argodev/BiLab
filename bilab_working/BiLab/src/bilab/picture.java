package bilab;


import jalview.FormatAdapter;
import jalview.ScoreSequence;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

// A picture (e.g. a bitmap image or a vector drawing)
public class picture implements /*IAnnotated,*/ IResourceIOProvider
{
  // !!! NB: reading an image required the UI thread to be running - which isn't the case before the window opens (e.g. executing sample.sg)
  //  should allow some kind of delayed loading for pictures.  (perhaps ise Display.getCurrent() and if it is null, delay loading)
  
  
  public picture()
  {
    Display display = Display.getDefault();
    image = new Image(display,10,10); //!!! use a default image here (like a cross or question mark something)
    picType = PictureType.Image;
  }
  
  
  
  
  private static java.util.List<String> supportedResourceTypes;
  
  static {
    // list of supported resource name type (not extensions)
    supportedResourceTypes = new LinkedList<String>();
    supportedResourceTypes.add("PNG");
    supportedResourceTypes.add("GIF");
    supportedResourceTypes.add("JPG");
  }
  
  
  public static java.util.List<String> getSupportedResourceTypes()
  {
    return supportedResourceTypes;
  }
  
  
  
  
  
  
  public static Object importResource(String resourceName, String resourceType)
  {
    try {
      InputStream inputStream = BilabPlugin.findResourceStream(resourceName);
      if (inputStream == null)
        throw new BilabException("unable to open resource:"+resourceName);

      Display display = Display.getDefault();
      picture pic = new picture();
      
      pic.image = new Image(display, inputStream);
      pic.picType = picture.PictureType.Image;
      pic.name = resourceName+" [bitmap]";
      
      inputStream.close();

      if (pic.image == null)
        throw new BilabException("unsupported picture resource type:"+resourceType);
      
      return pic;
      
    } catch (BilabException e) {
      throw e;
    } catch (Exception e) {
      throw new BilabException("unable to locate/import resource as picture: "+resourceName+" - "+e);
    }
    
  }
  
  
  public String get_name() { return name; }
  
  
  @Sophistication(Sophistication.Developer)
  public PictureType get_PictureType() { return picType; }
  
  @Sophistication(Sophistication.Developer)
  public Image get_Image() { return image; }
  
  
  
  @Sophistication(Sophistication.Developer)
  protected enum PictureType { Image, Drawing }
  
  private PictureType picType;
  private Image image;
  
  private String name;
  
}
