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
import MCview.*;
import java.awt.*;
import java.util.Vector;

public class DrawableSequence extends Sequence {
  
  int charHeight;
  
  boolean fastDraw;
  boolean displayBoxes;
  boolean displayText;
  boolean colourText;
  
  Vector textColour;
  public Vector boxColour;
  
  Color color;
  
  Font font;
  String fontName;
  int fontSize;
  int fontStyle;
  
  int padx = 0;
  int pady = 2;
  
  PDBfile pdb;
  public int maxchain = -1;
  
  public int pdbstart;
  public int pdbend;
  public int seqstart;
  public int seqend;
  
  public DrawableSequence(Sequence s) {
    super(s);
    textColour = new Vector();
    boxColour = new Vector(s.sequence.length());
    
    color = Color.white;
    
    displayBoxes = true;
    displayText = true;
    colourText = false;
    
    setFont("Dialog",Font.PLAIN,14);
    
    int i = 0;
    
  }
  
  
  
  public DrawableSequence(String name,String sequence, int start, int end) {
    super(name,sequence,start,end);
    textColour = new Vector();
    boxColour = new Vector(sequence.length());
    
    color = Color.white;
    
    displayBoxes = true;
    displayText = true;
    colourText = false;
    
    setFont("Dialog",Font.PLAIN,14);
  }
  
  public void setPDBfile(PDBfile pdb) {
    this.pdb = pdb;
    int max = -10;
    maxchain = -1;
    
    for (int i=0; i < pdb.chains.size(); i++) {
      
      System.out.println("PDB sequence = " + ((PDBChain)pdb.chains.elementAt(i)).sequence);
      // Now lets compare the sequences to get
      // the start and end points.
      
      String tmp = AlignSeq.extractGaps(".",sequence);
      tmp = AlignSeq.extractGaps("-",sequence);
      tmp = AlignSeq.extractGaps(" ",sequence);
      tmp = AlignSeq.extractGaps("~",sequence);
      
      // Align the sequence to the pdb
      AlignSeq as = new AlignSeq(this,((PDBChain)pdb.chains.elementAt(i)).sequence,"pep");
      as.calcScoreMatrix();
      as.traceAlignment();
      as.printAlignment();
      
      System.out.println("Score = " + as.maxscore);
      if (as.maxscore > max) {
        System.out.println("New max score");
        max = as.maxscore;
        maxchain = i;
        
        pdbstart = as.seq2start;
        pdbend = as.seq2end;
        seqstart = as.seq1start + start - 1 ;
        seqend = as.seq1end +  end -1;
      }
      System.out.println(as.output);	
      System.out.println("PDB start/end " + pdbstart + " " + pdbend);
      System.out.println("SEQ start/end " + seqstart + " " + seqend);
    }
  }
  public Color getColor() {
    return color;
  }
  
  public void setColor(Color c) {
    this.color = c;
  }
  public void setFont(String name,int style, int size) {
    this.font = new Font(name,style,size);
    fontName = name;
    fontStyle = style;
    fontSize = size;
  }
  
  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }
  public void setResidueTextColour(int i,Color c) {
    if (textColour.size() <= i) {
      for (int j = textColour.size(); j <= i; j++) {
        textColour.addElement(null);
      }
    }
    textColour.setElementAt(c,i);
  }
  
  
  public void setResidueBoxColour(int i, Color c) {
    if (boxColour.size() <= i) {
      for (int j = boxColour.size(); j <= i; j++) {
        boxColour.addElement(null);
      }
    }
    boxColour.setElementAt(c,i);
    
  }
  
  public void drawSequence(Graphics g,int start, int end, int x1, int y1, int width, int height,boolean showScores) {
    if (!font.getName().equals("Courier")) {
      padx = 1;
    } else {
      padx = 1;
    }
    if (showScores) {
      showScores(g,start,end,x1,y1+height/2+1,width,height/2-1,0);
      
      if (displayBoxes == true) {
        drawBoxes(g,start,end,x1,y1,width, height/2);
      }
      if (displayText == true) {
        if (colourText) {
          drawColourText(g,start, end, x1, y1, width,height/2);
        } else {
          drawText(g,start,end,x1,y1,width,height/2);
        }
      }
    } else {
      if (displayBoxes == true) {
        drawBoxes(g,start,end,x1,y1,width, height);
      }
      if (displayText == true) {
        if (colourText) {
          drawColourText(g,start, end, x1, y1, width,height);
        } else {
          drawText(g,start,end,x1,y1,width,height);
        }
      }
    }
  }
  
  public void showScores(Graphics g, int start, int end, int x1, int y1, int width, int height) {
    showScores(g,start,end,x1,y1,width,height,0);
  }
  public void showScores(Graphics g, int start, int end, int x1, int y1, int width, int height, int num) {
    int i = start;
    while (i < end && i < getSequence().length() && score[num] != null && i < score[num].size()) {
      if (score[num].size() > 0 && ((Double)score[num].elementAt(i)).intValue() < 10) {
        g.setColor((Color)ResidueProperties.scaleColours.elementAt(((Double)score[num].elementAt(i)).intValue()));
        g.fillRect(x1+width*(i-start),y1,width,height/2);
        
      }
      i++;
      
    }
  }
  public void drawBoxes(Graphics g,int start, int end, int x1, int y1, int width, int height) {
    int i = start;
    while (i < end && i < getSequence().length() && i < boxColour.size()) {
      if (boxColour.elementAt(i) != Color.white) {
        g.setColor((Color)boxColour.elementAt(i));
        
        g.fillRect(x1+width*(i-start),y1,width,height);
      }
      i++;
    }
  }
  public void drawText(Graphics g, int start, int end, int x1, int y1, int width, int height) {
    String s = "";
    if (start < getSequence().length()) {
      if (getSequence().length() > (end)) {
        s = getSequence().substring(start,end);
      } else {
        s = getSequence().substring(start);
      }
      g.setColor(Color.black);
      if (fastDraw) {
        g.drawString(s,x1,y1+height-pady);
      } else {
        for (int i=0; i < s.length(); i++) {
          g.drawString(s.substring(i,i+1),x1 + padx + i*width, y1 + height -pady);
        }
      }
    }
  }
  public void drawColourText(Graphics g, int start, int end, int x1, int y1, int width, int height) {
    if (start < getSequence().length()) {
      int i = start;
      while (i < getSequence().length() && i < (end) && i < boxColour.size()) {
        String s = getSequence().substring(i,i+1);
        g.setColor(((Color)boxColour.elementAt(i)).darker());
        if (fastDraw) {
          g.drawString(s,x1+width*(i-start),y1+height-pady);
        } else {
          g.drawString(s,x1+width*(i-start)+padx,y1+height-pady);
        }
        i++;
      }
    }
  }
  
  public synchronized void insertCharAt(int i,char c) {
    super.insertCharAt(i,c);
    for (int j=boxColour.size(); j < i; j++) {
      boxColour.addElement(Color.white);
    }
    boxColour.insertElementAt(Color.white,i);
  } 
  
  public void deleteCharAt(int i) {
    //    System.out.println("in drawable Sequence - deleteCharAt "  + i );
    super.deleteCharAt(i);
    if (boxColour.size() > i) {
      boxColour.removeElementAt(i);
    }
  }
  
  public static void main(String[] args) {
    try {
      
      DrawableSequence[] s = null;
      s = FormatAdapter.toDrawableSequence(FormatAdapter.read("http://srs.ebi.ac.uk/srs5bin/cgi-bin/wgetz?-e+-f+seq+-sf+fasta+[swissprot-id:" + args[0] + "]","URL","FASTA"));
      s[0].sequence = s[0].sequence.substring(0,s[0].sequence.indexOf("</PRE>"));
      DrawableSequence seq = s[0];
      seq.getFeatures("srs.ebi.ac.uk/srs5bin/cgi-bin/","swall");
      FeatureColourScheme ftcs = new FeatureColourScheme();
      ftcs.setColours(seq);
      
      if (seq.pdbcode.size() > 0) {
        PDBfile pdb = new PDBfile("http://srs.ebi.ac.uk/srs5bin/cgi-bin/wgetz?-e+[pdb-id:" + seq.pdbcode.elementAt(0) + "]","URL");
        seq.setPDBfile(pdb);
        System.out.println("Max chain " + seq.maxchain);
        ((PDBChain)pdb.chains.elementAt(seq.maxchain)).isVisible = true;
        ((PDBChain)pdb.chains.elementAt(seq.maxchain)).ds = seq;
        ((PDBChain)pdb.chains.elementAt(seq.maxchain)).colourBySequence();
        
        rotFrame f = new rotFrame(pdb);
        f.resize(500,500);
        f.show();
        DrawableSequence[] s1 = new DrawableSequence[2];
        s1[0] = seq;
        s1[1] = new DrawableSequence(((PDBChain)pdb.chains.elementAt(seq.maxchain)).sequence);
        AlignFrame af= new AlignFrame(null,s1);
        af.resize(700,300);
        af.show();
      } else {
        System.out.println("No pdb code found");
      }
    } catch (Exception e) {
      System.out.println(e);
      System.exit(0);
    }
  }
}



















