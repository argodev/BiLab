package bilab;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.Label;
import java.awt.Frame;
import java.awt.Font;

import jalview.AlignmentPanel;
import jalview.ClustalxColourScheme;
import jalview.ColourProperties;
import jalview.DrawableSequence;
import jalview.ResidueProperties;
import jalview.ScorePanel;
import jalview.ScoreSequence;
import jalview.SequenceGroup;

import java.awt.Component;
import java.awt.Container;


// alignment viewer (using JalView)
public class JalViewAlignmentViewer extends ViewerBase
{
  public JalViewAlignmentViewer(Composite parent) 
  {
    fixed = new Composite(parent, SWT.NONE);
    fixed.setLayout(new FillLayout());
    fixed.setSize(300,300);
    
    top = new Composite(fixed, SWT.EMBEDDED);
    compositeFrame = SWT_AWT.new_Frame(top);
  
    child = new Panel();
    compositeFrame.add(child);
    
    label = new Label("<no alignment>");
    child.add(label);
    
    ap = null;
    input = null;
    refresh();
  }
  
  
  Composite fixed, top;
  Container child;
  Frame compositeFrame;
  Label label;
  AlignmentPanel ap;
  ScorePanel sp;
  
  alignment input;
  
  
  public void dispose()
  {
    // TODO Auto-generated method stub

  }

  public Point preferedSize()
  {
    return new Point(700,200);
  }
  
  public Point maximumSize()
  {
    return new Point(SWT.MAX, 200);
  }

  public String get_title()
  {
    return "alignment";
  }

  public String get_description()
  {
    return get_title();
  }

  public Control getControl()
  {
    return top;
  }

  public Object getInput()
  {
    return input;
  }

  public void refresh()
  {
    compositeFrame.invalidate();
    child.invalidate();
    compositeFrame.setVisible(true);
    compositeFrame.repaint();
  }

  
  public void setInput(Object input)
  {
    if (this.input == input) return; // nothing changed
    
    if ((input != null) && (input instanceof alignment)) {
      this.input = (alignment)input;
    }

    // first, remove the current viewer, if any
    child.removeAll();
    
    // make a new one
    if (this.input != null) {
      
      //!!! this stuff is flakey 
      
      DrawableSequence[] ds = new DrawableSequence[this.input.alignedSeqs.length];
      for (int i=0;i < this.input.alignedSeqs.length;i++) {
        ds[i] = new DrawableSequence(this.input.alignedSeqs[i]);
      }
      
      ap = new AlignmentPanel(child,ds);
      
      //if (this.input.seqScores.length == 0) {
        ap.seqPanel.align.percentIdentity2();
        ap.seqPanel.align.findQuality();
        this.input.seqScores = new ScoreSequence[1];
        this.input.seqScores[0] = ap.seqPanel.align.qualityScore;
//scigol.Debug.WL("* setting quality");
      //}

      //ap.setSequenceColor();
      //ap.setFont(new Font("Courier New", Font.PLAIN ,12));
      //ap.setSequenceColor(9999999, ColourProperties.CLUSTALX);
      //ap.seqPanel.seqCanvas.  seqPanel.seqCanvas.boxFlag = true;
//scigol.Debug.WL("* setting clustalx colors");
      
      ap.setSequenceColor(new ClustalxColourScheme(ap.seqPanel.align.cons2,ap.seqPanel.align.size()));
      ap.color = ResidueProperties.color;
      
      ap.groupEdit = false;
      
//scigol.Debug.WL("* setting boxes on");
      for (int i = 0; i < ap.seqPanel.align.groups.size();i++) {
        SequenceGroup sg = (SequenceGroup)ap.seqPanel.align.groups.elementAt(i);
        sg.displayBoxes = true;
        ap.seqPanel.align.displayBoxes(sg);
      }
      
      ScorePanel sp = new ScorePanel(child,this.input.seqScores);

      ap.seqPanel.seqCanvas.showScores = true;
      ap.idPanel.idCanvas.showScores = true;
      
//scigol.Debug.WL("* setting font");      
      ap.seqPanel.seqCanvas.setFont(new Font("Courier",Font.PLAIN,12));
      sp.seqPanel.seqCanvas.setFont(new Font("Courier",Font.PLAIN,12));
      
      
      //!!!
      
      child.setLayout(new BorderLayout());
      child.add("Center",ap);
      child.add("South",sp);
      compositeFrame.layout();

      refresh();
    }
    else {
      label = new Label("<no alignment>");
      child.add(label);
      ap = null;
      sp = null;
    }
    
  }

  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
  }

}
