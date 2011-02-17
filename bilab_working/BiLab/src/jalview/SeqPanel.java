/* Jalview - a java multiple alignment editor
 * Copyright (C) 1998  Michele Clamp
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jalview;
import java.awt.*;
import MCview.*;

public class SeqPanel extends Panel {
  protected AlignmentPanel alignPanel;
  public SeqCanvas seqCanvas;
  protected Scrollbar hscroll;
  protected Scrollbar vscroll;
  public DrawableAlignment align;

  protected int offx;
  protected int offy;

  protected int maxoffx = 0;
  protected int maxoffy = 0;

  protected int startres;
  protected int lastres;
  protected int endres;

  protected long lasttime;

  protected int startseq;
  protected int padseq;
  public boolean fastDraw;
  public boolean editFlag;

  public SeqPanel(AlignmentPanel alignPanel, DrawableSequence[] sequences) {
    this.alignPanel = alignPanel;
    this.align = new DrawableAlignment(sequences);
    System.out.println ("Maximum sequence length = " + align.maxLength);
    System.out.println("No sequences  = " + align.size);
    setFastDraw(true);
    componentInit();
  }
  public void setFastDraw(boolean fd) {
    this.fastDraw = fd;
    int i=0;
    while (i < align.ds.length && align.ds[i] != null) {
      align.ds[i].fastDraw = fd;
      i++;
    }
  }
  private void componentInit() {
    seqCanvas = new SeqCanvas(this);

    hscroll = new Scrollbar(Scrollbar.HORIZONTAL);
    vscroll = new Scrollbar(Scrollbar.VERTICAL);

    setLayout(new BorderLayout());
    
    add("Center",seqCanvas);
    add("East",vscroll);
    add("South",hscroll);

    setScrollValues(0,0);
    System.out.println("SeqPanel initialized");
  }

  public void setScrollValues(int offx, int offy) {
    int width;
    int height;

    String version = System.getProperty("java.version");
    //Brings up error in netscape 
    if (seqCanvas.size().width > 0) {
      width = seqCanvas.size().width;
      height = seqCanvas.size().height;
    } else {
      width = seqCanvas.preferredSize().width;
      height = seqCanvas.preferredSize().height;
    }

    //Make sure the maxima are right
    if (maxoffx != (align.maxLength+1)) {
      maxoffx = (align.maxLength+1);
    } 
    if (maxoffy != (align.size+1)) {
      maxoffy = (align.size+1);
    } 
    
    
    //The extra 1 is to make sure all the last character gets printed

    hscroll.setValues(offx,width/seqCanvas.charWidth,0,maxoffx);
    
    vscroll.setValues(offy,height/seqCanvas.charHeight,0,maxoffy);
    hscroll.setLineIncrement(1);
    vscroll.setLineIncrement(1);
    
    if (seqCanvas.endx > 0) {
      hscroll.setPageIncrement((seqCanvas.endx-seqCanvas.startx)/2);
      if (seqCanvas.endy-seqCanvas.starty > 2) {
	vscroll.setPageIncrement((seqCanvas.endy-seqCanvas.starty)/2);
      }
    } 
    
  }

  public boolean handleEvent(Event evt) {
    switch(evt.id) {
    case Event.SCROLL_LINE_UP:
    case Event.SCROLL_LINE_DOWN:
    case Event.SCROLL_ABSOLUTE:
    case Event.SCROLL_PAGE_UP:
    case Event.SCROLL_PAGE_DOWN:
    if (evt.target == hscroll) {
	    offx = hscroll.getValue();
	    //offy = vscroll.getValue();
	    alignPanel.scalePanel.scaleCanvas.paintFlag = true;
	    seqCanvas.repaint();
      alignPanel.scalePanel.scaleCanvas.repaint();

	    return true;
    }
    if (evt.target == vscroll) {
	    //offx = hscroll.getValue();
	    offy = vscroll.getValue();

	    alignPanel.idPanel.idCanvas.paintFlag = true;
	seqCanvas.repaint();
	alignPanel.idPanel.idCanvas.repaint();
	return true;
      }
    }
    return super.handleEvent(evt);
  }

  public boolean mouseUp(Event evt, int x, int y) {
    // This is for the autoconsensus stuff.
    if (editFlag) {
      if (align.autoConsensus == true) {
	System.out.println("Auto consensus");
	align.percentIdentity();

	BigPanel bp = ((AlignFrame)alignPanel.parent).bp;

	if (bp.scorePanel != null) {
	  bp.scorePanel.seqPanel.align.ds[0] = align.qualityScore;
	}
	
	boolean redraw = false;

	for (int j=0; j < align.groups.size(); j++) {
	  SequenceGroup sg =  ((SequenceGroup)align.groups.elementAt(j));
	  if (sg.colourScheme instanceof ClustalxColourScheme) {
	    redraw = true;
	    ((ClustalxColourScheme)sg.colourScheme).cons2 = align.cons2;
	    sg.colourScheme.setColours(sg);
	    
	  } else if (sg.colourScheme instanceof PIDColourScheme) {
	    sg.colourScheme = new PIDColourScheme(align.cons);
	    sg.colourScheme.setColours(sg);
	    redraw = true;
	  } else if (sg.colourScheme instanceof Blosum62ColourScheme ) {
	    ((Blosum62ColourScheme)sg.colourScheme).cons = align.cons;
	    sg.colourScheme.setColours(sg);
	    redraw = true;
	  } else if (sg.colourScheme instanceof ResidueColourScheme) {
	    ((ResidueColourScheme)sg.colourScheme).cons = align.cons;
	    redraw = true;
	    if (((ResidueColourScheme)sg.colourScheme).colourThreshold > 0) {
	      sg.colourScheme.setColours(sg);
	    } 
	  }
	}

	if (bp.scorePanel != null) {
	  bp.scorePanel.seqPanel.seqCanvas.paintFlag = true;
	  bp.scorePanel.seqPanel.seqCanvas.repaint();
	}

	if (redraw) {
	  seqCanvas.paintFlag = true;
	  seqCanvas.repaint();

	}


	
      }
    }

    try {
      int res = (x)/seqCanvas.charWidth + seqCanvas.startx;
      //      int seq = (y)/seqCanvas.charHeight + seqCanvas.starty;
      int seq = seqCanvas.getIndex(y);
            System.out.println("Y = " + y + " " + seq + " " + align.ds[seq].name);
      char resstr = align.ds[seq].charAt(res);

      endres = res;
      
      int pos = align.ds[seq].findPosition(res);
      // Find features (if any) this should be moved
      if (alignPanel.parent instanceof AlignFrame && editFlag == false) {
	AlignFrame af = (AlignFrame)alignPanel.parent;
	
	if (align.ds[seq].features != null && align.ds[seq].features.size() > 0) {	  
	  String tmp = "";
	  tmp = tmp + "No of features for " + align.ds[seq].name + " = " + align.ds[seq].features.size() + "\n\n";	
	  tmp = tmp + "------------------------------------------------\n";
	  
	  tmp = tmp + "Selected features for residue " + pos + " (" + res + " in alignment) : \n\n";
	  if (af.ff == null) {
	    af.ff = new FeatureFrame(af,"Sequence feature console",15,72,"");
	    af.ff.setTextFont(new Font("Courier",Font.PLAIN,12));
	    af.ff.resize(500,400);
	    af.ff.show();
	  }
	  for (int i = 0;i < align.ds[seq].features.size();i++) {
	    SequenceFeature sf = (SequenceFeature)align.ds[seq].features.elementAt(i);
	    if (pos >= sf.start && pos <= sf.end) {
	      tmp = tmp + sf.print() + "\n";
	    }
	  }
	  tmp = tmp + "\n------------------------------------------------\n";
	  tmp = tmp + "All features : \n\n";
	  for (int i = 0;i < align.ds[seq].features.size();i++) {
	    SequenceFeature sf = (SequenceFeature)align.ds[seq].features.elementAt(i);
	    tmp = tmp + sf.print() + "\n";
	    
	    
	    af.ff.setText(tmp);
	  }
	} 
      }
      
      // This is to detect edits - we're at the end of an edit if mouse is up
      editFlag = false;
      startseq = -1;
      startres = -1;
      lastres = -1;
    } catch (Exception e) {
    }
    return true;
  }

    public boolean mouseDown(Event evt, int x, int y) {
      int seq;
      int res;
      int pos;

      res = (x)/seqCanvas.charWidth + seqCanvas.startx;
      seq = (y)/seqCanvas.charHeight + seqCanvas.starty;
      
      if (seq < align.size() && res < align.maxLength && res >= 0) {
	char resstr = align.ds[seq].charAt(res);
	
	// Find the residue's position in the sequence (res is the position
	// in the alignment
	
	pos = align.ds[seq].findPosition(res);
	startseq = seq;
	
	if (startseq == (align.size()-1)) {
	  padseq = 1;
	} else {
	  padseq = 1;
	}
	startres = res;
	lastres = res;
	
	// Do we have a pdb code?
	// Need to have some way of isVisible here - maybe in AlignFrame?
	if (align.ds[seq].pdb != null) {
	  PDBChain tmp = (PDBChain)align.ds[seq].pdb.chains.elementAt(align.ds[seq].maxchain);	
	  // I sort of understand what's going on here :)
	  
	  int pdbpos = pos - align.ds[seq].seqstart + align.ds[seq].pdbstart + tmp.offset -1;
	  //Now select the right atom in the chain
	  
	  for (int i=0; i < tmp.bonds.size(); i++) {
	    if (((Bond)tmp.bonds.elementAt(i)).at1.resNumber == pdbpos) {
	      ((Bond)tmp.bonds.elementAt(i)).at1.isSelected = true;
	    }
	    if (((Bond)tmp.bonds.elementAt(i)).at2.resNumber == pdbpos) {
	      ((Bond)tmp.bonds.elementAt(i)).at2.isSelected = true;
	    }
	  }
	}
	if (alignPanel.parent instanceof AlignFrame) {
	  //System.out.println(align.ds[seq].start + " " + align.ds[seq].end);
	  ((AlignFrame)alignPanel.parent).status.setText("Sequence ID : " +  
							 align.ds[seq].getName() +    " (" + seq + ") Residue = "     +  resstr + " (" + pos + ") ");						    
	} 
      } else {
	startseq = -1;
	startres = -1;
	lastres = -1;
      }
      return false;
    }

  public void drawChars(int seqstart, int seqend, int start, int end) {
    //    for (int i=seqstart; i <seqend; i++) {
    //  alignPanel.setSequenceColor(align.ds[i],start,end);
    // }
    //    System.out.println(seqstart  + " " + seqend + " " + start + " " + end);
    seqCanvas.drawPanel(seqCanvas.getGraphics(),start,end,seqstart,seqend);
    seqCanvas.drawPanel(seqCanvas.gg,           start,end,seqstart,seqend);
    
    alignPanel.scalePanel.scaleCanvas.repaint();
  }

  public void insertChar(int j, int seq) {
    //    System.out.println(j + " " + align.gapCharacter.charAt(0));
    align.sequences[seq].insertCharAt(j,align.gapCharacter.charAt(0));
    align.maxLength();
    
    int end = align.ds[seq].sequence.length()-1;
    
    setScrollValues(offx,offy);      
    
  }

  public void deleteChar(int j, int res, int sno) {

      if (align.ds[sno].sequence.substring(j,j+1).equals(".") ||
	  align.ds[sno].sequence.substring(j,j+1).equals("-") ||
	  align.ds[sno].sequence.substring(j,j+1).equals(" ") ) {
	
	align.ds[sno].deleteCharAt(j);
	
      }
      align.maxLength();
      setScrollValues(offx,offy);

  }
  public boolean mouseDrag(Event evt, int x, int y) {
    // If we're dragging we're editing
    editFlag = true;

    int res = (x)/seqCanvas.charWidth + seqCanvas.startx;
    if (res < 0) {res = 0;}
    
    if (res  != lastres) {
      if (startseq != -1) {
	char resstr = align.ds[startseq].charAt(res);
	
	// Group editing
	if (alignPanel.groupEdit == true) {
	  int start = lastres;
	  SequenceGroup sg = align.findGroup(startseq);
	  
	  if (res < align.maxLength && res < lastres) { 
	    boolean flag = false;
	    for (int i= 0 ; i < sg.sequences.size(); i++) {
	      DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(i);
	      for (int j=lastres-1; j >= res; j--) {
		if (!flag) {
		  if (!s.sequence.substring(j,j+1).equals("-") && !s.sequence.substring(j,j+1).equals(".") &&
		      !s.sequence.substring(j,j+1).equals(" ")) {
		    res = j+1;
		    System.out.print("\07");
		    System.out.flush();
		    flag = true;
		  }
		}
	      }
	    }
	  }
	  
	  for (int i= 0 ; i < sg.sequences.size(); i++) {
	    DrawableSequence s = (DrawableSequence)sg.sequences.elementAt(i);
	    boolean found = false;
	    int sno = -1;
	    for (int k = 0; k < align.ds.length; k++) {
	      if (align.ds[k] == s) {
		found = true;
		sno = k;
		break;
	      }
	    }
	    if (found && sno != -1) {
	      if (res < align.maxLength && res > lastres) {
		for (int j = lastres; j < res; j++) {
		  insertChar(j,sno);
		}

		//		drawChars(i,i+1,lastres,align.maxLength);
		int index = align.findIndex(s);
		if (index != -1) {
		  drawChars(index,index+1,lastres,align.maxLength);
		}
		
	      } else if (res < align.maxLength && res < lastres) { 
		for (int j = res; j < lastres; j++) { 
		  deleteChar(j,res,sno);
		  startres = res;
		}
		int index = align.findIndex(s);
		if (index != -1) {
		  drawChars(index,index+1,res,align.maxLength);
		}

		//		drawChars(i,i+1,res,align.maxLength);
	      }
	    }
	    
	  }
	  lastres = res;
	} else {
	  //  System.out.println("res " + res + " " + lastres);
	  if (res < align.maxLength && res > lastres) {
	    //  System.out.println("icky");
	    for (int j = lastres; j < res; j++) {
	      insertChar(j,startseq);
	    }
	    drawChars(startseq,startseq+1,lastres,align.maxLength);
	    
	  } else if (res < align.maxLength && res < lastres) { 
	    for (int j = res; j < lastres; j++) { 
	      deleteChar(j,res,startseq);
	      startres = res;
	    }
	    drawChars(startseq,startseq+1,res,align.maxLength);
	  }
	}
      }

    }
    lastres = res;
    return false;
    }

  public void reshape(int x, int y, int width, int height) {
    super.reshape(x,y,width,height);
    setScrollValues(offx,offy);  
  }

  public Dimension minimumSize() {
    return new Dimension(700,500);
  }
  public Dimension preferredSize() {
    return minimumSize();
  }

  public static void main(String[] args) {
    Frame f = new Frame("SeqPanel");
    MSFfile msf;
    DrawableSequence[] s = null;

    try {
      msf = new MSFfile("pog.msf","File");
      for (int i=0;i < msf.seqs.size();i++) {
	s[i] = new DrawableSequence((Sequence)msf.seqs.elementAt(i));
      }
    } catch (java.io.IOException e) {
      System.out.println("Exception : " + e);
    }
    if (s != null) {
      SeqPanel sp = new SeqPanel(null,s);
      f.add(sp);
      f.resize(700,500);
      f.show();
    }
  }
}
    
