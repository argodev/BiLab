package jalview;

import java.awt.*;
import java.util.*;

public class ColourChooserPanel extends Panel {
  Vector cp;
  ColourPanel none;
  Color[] color;
  ColorSelect cs;
  Hashtable hash;
  Frame f;
  AlignFrame af;

  public ColourChooserPanel(AlignFrame af,Color[] color) {
    this.af = af;
    this.color = color;
    this.cs = new ColorSelect();

    // Make a hash of the colours.
    hash = new Hashtable();
    makeHash(color);

    // Make the colourPanels
    cp = new Vector(hash.size());

    //    none = new ColourPanel(Color.gray,"");
    //none.tf.setEditable(false);
    Enumeration e = hash.keys();
    int count = 0;
    while (e.hasMoreElements()) {
      Color c = (Color)e.nextElement();
      String s = (String)hash.get(c);
      cp.addElement(new ColourPanel(c,s));
      count++;
    }

    Panel p = new Panel();
    p.setLayout(new GridLayout(cp.size()+1,1));
    for (int i=0; i < cp.size(); i++) {
      p.add((ColourPanel)cp.elementAt(i));
    }
    p.resize(250,(cp.size()+1)*50);
    p.invalidate();
    // add a no colour Panel

    // display a colour chooser
    setLayout(new BorderLayout());
    add("Center",p);

    // This is the colour selector
    Panel p2 = new Panel();
    p2.setLayout(new BorderLayout());
    p2.add("Center",cs);
    cs.resize(size().width,150);
    p2.resize(size().width,150);
    add("South",p2);

    
    
  }

  public void makeHash(Color[] color) {

    for (int i=0; i < color.length; i++) {
      if (hash.containsKey(color[i])) {
	String tmp = (String)hash.get(color[i]);
	String res = findResidueString(i);
	hash.put(color[i],tmp + res);
      } else {
	String res = findResidueString(i);
	hash.put(color[i],res);
      }
    }

  }
  public void updateHash() {
    for (int i=0; i < cp.size(); i++) {
      Color tmp = ((ColourPanel)cp.elementAt(i)).cc.c;
      ((ColourPanel)cp.elementAt(i)).setString((String)hash.get(tmp));
    }
  }
  public String findResidueString(int i) {
    Enumeration e = ResidueProperties.aaHash.keys();

    while (e.hasMoreElements()) {
      String key = (String)e.nextElement();
      int num = ((Integer)ResidueProperties.aaHash.get(key)).intValue();
      if (i == num) {
	return key;
      } 
    }
    return null;

  }

  public void makeArray() {
    Enumeration e = hash.keys();
    while (e.hasMoreElements()) {

      Color key = (Color)e.nextElement();
      String restr = (String)hash.get(key);
      String res;

      for (int i=0; i < restr.length(); i++) {
	res = restr.substring(i,i+1);
	int num = ResidueProperties.aaHash.size();
	try {
	  num = ((Integer)ResidueProperties.aaHash.get(res)).intValue();
	  color[num] = key;
	} catch (Exception ex) {
	  //	  num =  ResidueProperties.aaHash.size();
	  //	  color[num] = key;
	}

      }
    }
  }
	
  public void changeColour(ColourPanel cp) {
    String oldres = cp.s;
    String resstr = cp.getString();

    // If a residue in the old string isn't
    // in the new add to the white hash
    for (int i=0; i < oldres.length(); i++) {
      String tmp = oldres.substring(i,i+1);
      if (resstr.indexOf(tmp) < 0) {
	hash.put(Color.white,hash.get(Color.white) + tmp);
      }
    }
    //Loop over all the residues in the new string
    for (int i=0; i < resstr.length(); i++) {
      String tmp = resstr.substring(i,i+1);
      
      //Loop over the colours
      Enumeration e = hash.keys();
      while (e.hasMoreElements()) {
	Color key = (Color)e.nextElement();
	
	// If it is in another colour delete
	if (((String)hash.get(key)).indexOf(tmp) >= 0) {
	  String tmp2 = (String)hash.get(key);
	  tmp2 = tmp2.substring(0,tmp2.indexOf(tmp)) + tmp2.substring(tmp2.indexOf(tmp)+1);
	  hash.put(key,tmp2);
	}
      }
    }
    hash.put(cp.cc.c,resstr);
    updateHash();
  }
	    
  public boolean handleEvent(Event e) {
    if (e.target instanceof Button && e.id == 1001) {
      Button b = (Button)e.target;

      if (b.getParent() instanceof ColourPanel) {
	ColourPanel tmp = (ColourPanel)b.getParent();

	//	Color c = cs.getColor();
	//tmp.cc.c = c;
	//tmp.cc.repaint();
	changeColour(tmp);
	
	// Now update in the parent
	if (af != null) {
	  makeArray();

	  af.ap.color = color;
	  af.ap.setSequenceColor();
	  af.updateFont();
	}
	return true;
	
      }
    }
    return super.handleEvent(e);
  }

  public boolean action(Event e, Object arg) {
    if (e.target instanceof TextField) {
      return false;
    } else {
      return super.action(e,arg);
    }
  }

  public boolean mouseDown(Event e, int x, int y) {
    if (e.target instanceof ColourCanvas) {
      ColourCanvas cc = (ColourCanvas)e.target;
      if ((e.modifiers & Event.META_MASK) != 0) {
	cs.setColor(cc.c);
	cs.myColorPanel.pogCanvas.paint(cs.myColorPanel.pogCanvas.getGraphics());
      } else {
	Color c = cs.getColor();
	cc.c = cs.getColor();
	cc.repaint();
      }
      return true;
    } else {
      return false;
    }
  }
    
  public static void main(String[] args) {
    Frame f = new Frame();

    ColourChooserPanel cp = new ColourChooserPanel(null,ResidueProperties.color);
    f.setLayout(new BorderLayout());
    f.add("Center",cp);
    f.resize(cp.getPreferredSize().width,cp.getPreferredSize().height);
    f.show();
  }

  public Dimension getPreferredSize() {
    return new Dimension(((ColourPanel)cp.elementAt(0)).getPreferredSize().width, cp.size()*((ColourPanel)cp.elementAt(0)).getPreferredSize().height+150);
  }
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }
}
//
