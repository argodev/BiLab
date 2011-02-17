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
import java.applet.Applet;
import java.util.*;
import java.net.*;
import java.io.*;
import MCview.*;

public final class AlignFrame extends Frame implements OutputGenerator{
  MenuBar mb;
  Menu file;
  Menu edit;
  Menu font;
  Menu view;
  Menu colour;
  Menu calc;
  Menu help;
  Menu align;
  
  MenuItem gapCharacter;
  
  CheckboxMenuItem groupEdit;
  CheckboxMenuItem fastDraw;
  CheckboxMenuItem helvetica;
  CheckboxMenuItem courier;  
  CheckboxMenuItem times;
  
  CheckboxMenuItem zappo;
  CheckboxMenuItem clustalx;
  CheckboxMenuItem taylor;
  CheckboxMenuItem Hydrophobicity;
  CheckboxMenuItem helix;
  CheckboxMenuItem strand;
  CheckboxMenuItem turn;
  CheckboxMenuItem buried;
  
  
  CheckboxMenuItem conservation;
  CheckboxMenuItem autoconsensus;
  
  CheckboxMenuItem PID;
  CheckboxMenuItem BLOSUM62;
  MenuItem features;
  MenuItem structures;
  
  MenuItem PIDthreshold;
  MenuItem incitem;
  MenuItem userColours;
  
  CheckboxMenuItem colourText;
  CheckboxMenuItem blackText;
  CheckboxMenuItem boxes;
  CheckboxMenuItem text;
  CheckboxMenuItem scores;
  
  //public AlignmentPanel ap;
  public BigPanel bp;
  public AlignmentPanel ap;
  Object parent;
  
  Label status;
  Label redraw;
  Panel labelPanel;
  
  public Hashtable[] cons;
  public PCAThread pca;
  public TreeThread tt;
  public SimpleBrowser browser;
  public FeatureFrame ff;
  SequenceFeatureThread sft;
  
  String srsServer = "srs.ebi.ac.uk/srs5bin/cgi-bin/";
  String database = "swall";
  String tempdir = "temp";
  
  // Output properties
  public MailProperties mp;
  public FileProperties fp;
  public PostscriptProperties pp;
  
  
  //Save the current threshold 
  int threshold = 0;
  
  //Colours
  int increment = 30;
  
  public AlignFrame(Object parent) {
    bp = null;
    ap = null;
    this.parent = parent;
    frameInit();
    propertiesInit();
  } 
  public AlignFrame(Object parent, DrawableSequence[] s) {
    bp = new BigPanel(this,s);
    ap = bp;
    
    this.parent = parent;
    frameInit();
    propertiesInit();
    
  }
  public AlignFrame(DrawableSequence[] s) {
    bp = new BigPanel(this,s);
    ap = bp;
    frameInit();
    propertiesInit();
  }
  
  public AlignFrame(Object parent, String input, String type, String format) {
    this.parent = parent;
    DrawableSequence[] s = null;
    s = FormatAdapter.toDrawableSequence(FormatAdapter.read(input,type,format));
    
    bp = new BigPanel(this,s);
    System.out.println("Made BigPanel");
    ap = bp;
    frameInit();
    System.out.println("finished framInit");
    propertiesInit();
    System.out.println("finished propertiesInit");
  }
  
  private void setQuality() {
    ap.seqPanel.align.percentIdentity2();
    ap.seqPanel.align.findQuality();
    
    ScoreSequence[] sseq = new ScoreSequence[1];
    
    sseq[0] = ap.seqPanel.align.qualityScore;
    bp.setScorePanel(new ScorePanel(this,sseq));
  }
  
  public void propertiesInit() {
    this.mp = new MailProperties();
    mp.server = "circinus.ebi.ac.uk";
    this.pp = new PostscriptProperties();
    this.fp = new FileProperties();
  }
  
  public void frameInit() {
    System.out.println("Java version = " + System.getProperty("java.version"));
    
    fileMenu();
    editMenu();
    fontMenu();
    viewMenu();
    colourMenu();
    calcMenu();
    helpMenu();
    alignMenu();
    
    mb = new MenuBar();
    
    mb.add(file);
    mb.add(edit);
    mb.add(font);
    mb.add(view);
    mb.add(colour);
    mb.add(calc);
    mb.add(align);
    mb.add(help);
    
    setMenuBar(mb);
    
    // Must be borderlayout otherwise no show in netscape
    setLayout(new BorderLayout());
    
    setQuality();
    
    status = new Label("Status : ",Label.LEFT);
    redraw = new Label(" ",Label.RIGHT);
    
    labelPanel  = new Panel();
    labelPanel.setLayout(new GridLayout(1,2));
    add("South",labelPanel);
    labelPanel.add(status);
    labelPanel.add(redraw);
    
    add("Center",ap);
    resize(700,500);
    show();
    
    // This has to be called twice to make sure
    // the scale numbers coincide with the sequence
    updateFont();
    System.out.println("three");
    updateFont();
    System.out.println("four");
  }
  
  public void setStates(CheckboxMenuItem c,String label,boolean PIDthresh, boolean userThresh) {
    c.setState(true);
    if (c.getState()) {
      if (c != zappo) {zappo.setState(false);}
      if (c != taylor) {taylor.setState(false);}
      if (c != PID) {PID.setState(false);}
      if (c != BLOSUM62) {BLOSUM62.setState(false);}
      if (c != Hydrophobicity) {Hydrophobicity.setState(false);}
      if (c != helix) {helix.setState(false);}
      if (c != strand) {strand.setState(false);}
      if (c != turn) {turn.setState(false);}
      if (c != buried) {buried.setState(false);}
      if (c != clustalx) {clustalx.setState(false);}
      
      updateFont();
      
      if (PIDthresh == true) {PIDthreshold.enable();} else {PIDthreshold.disable();}
      if (c == zappo && userThresh == true) {userColours.enable();} else {userColours.disable();}
    } 
    status.setText(label);
    status.validate();
  }
  
  public boolean action(Event e,Object arg) {
    //Do the menu thingies
    if (e.target instanceof CheckboxMenuItem) {
      String label = ((CheckboxMenuItem)e.target).getLabel();
      
      if (label.equals("Boxes")) {
        ap.seqPanel.seqCanvas.boxFlag = !ap.seqPanel.seqCanvas.boxFlag;
        setBoxes(boxes.getState());
        updateFont();
        if (boxes.getState()) {
          status.setText("Residue boxes on");
        } else {
          status.setText("Residue boxes off");
        }
        status.validate();
      }else if (label.equals("Scores")) {
        ap.seqPanel.seqCanvas.showScores = scores.getState();
        ap.idPanel.idCanvas.showScores = scores.getState();
        updateFont();
        if (scores.getState()) {
          status.setText("Showing scores");
        } else {
          status.setText("Hiding scores");
        }
      } else if (label.equals("Group editing mode")) {
        ap.groupEdit = groupEdit.getState();
        if (ap.groupEdit) {
          status.setText("Group editing mode on");
        } else {
          status.setText("Group editing mode off");
        }
        status.validate();
      } else if (label.equals("Text")) {
        ap.seqPanel.seqCanvas.textFlag = !ap.seqPanel.seqCanvas.textFlag;
        setText(text.getState());
        updateFont();
        if (text.getState()) {
          status.setText("Residue text on");
        } else {
          status.setText("Residue text off");
        }
        status.validate();
      } else if (label.equals("Zappo Colourscheme")) {
        status.setText("Setting Zappo colours...");
        status.validate();
        
        ap.setSequenceColor(new ZappoColourScheme());
        ap.color = ResidueProperties.color;
        setStates(zappo,label,true,true);
        
      } else if (label.equals("Clustalx colours")) {
        status.setText("Setting clustalx colours...");
        status.validate();
        
        incitem.disable();
        conservation.disable();
        
        long start = System.currentTimeMillis();
        ap.seqPanel.align.percentIdentity2();
        long end = System.currentTimeMillis();
        System.out.println("Time for consensus " + (end-start));
        start = System.currentTimeMillis();
        
        ap.setSequenceColor(new ClustalxColourScheme(ap.seqPanel.align.cons2,ap.seqPanel.align.size()));
        end = System.currentTimeMillis();
        System.out.println("Time for colours " + (end-start));
        
        ap.color = ResidueProperties.color;
        setStates(clustalx,label,false,false);
        
      } else if (label.equals("Strand propensity")) {
        status.setText("Setting strand colours...");
        status.validate();
        
        ap.setSequenceColor(new StrandColourScheme());
        setStates(strand,"Strand propensity",true,true);
        
      } else if (label.equals("Helix propensity")) {
        status.setText("Setting helix colours...");
        status.validate();
        
        ap.setSequenceColor(new HelixColourScheme());
        setStates(helix,"Helix propensity",true,true);
        
      } else if (label.equals("Turn propensity")) {
        status.setText("Setting turn colours...");
        status.validate();
        
        ap.setSequenceColor(new TurnColourScheme());
        setStates(turn,"Turn propensity",true,true);
        
      } else if (label.equals("Buried index")) {
        status.setText("Setting buried colours...");
        status.validate();
        
        ap.setSequenceColor(new BuriedColourScheme());
        setStates(helix,"Buried index",true,true);
        
      } else if (label.equals("Taylor Colourscheme")) {
        status.setText("Setting Taylor colours...");
        status.validate();
        
        ap.setSequenceColor(new TaylorColourScheme());
        setStates(taylor,"Willie Taylor Colours",true,true);
        
      } else if (label.equals("By PID")) {
        status.setText("Setting PID colours...");
        status.validate();
        ap.seqPanel.align.percentIdentity();
        ap.setSequenceColor(new PIDColourScheme(ap.seqPanel.align.cons));
        setStates(PID,"Percentage identity colours",false,false);
        
      } else if (label.equals("By BLOSUM62 score")) {
        status.setText("Setting BLOSUM62 colours...");
        status.validate();
        
        ap.setSequenceColor(new Blosum62ColourScheme());
        setStates(BLOSUM62,"BLOSUM62 score colours",false,false);
        
      } else if (label.equals("By Hydrophobicity")) {
        status.setText("Setting hydrophobicity colours...");
        status.validate();
        
        ap.setSequenceColor(new HydrophobicColourScheme());
        setStates(Hydrophobicity,"colour by hydrophobicity",true,false);
        
      } else if (label.equals("By conservation")) {
        if (conservation.getState()) {
          status.setText("Colouring by conservation");
          status.validate();
          
          incitem.enable();
          ap.conservation = true;
          
          for (int j=0; j < ap.seqPanel.align.groups.size(); j++) {
            SequenceGroup sg =  ((SequenceGroup)ap.seqPanel.align.groups.elementAt(j));
            if (sg.conserve != null) {
              sg.colourScheme = new ConservationColourScheme(sg);
              sg.colourScheme.setColours(sg);
              updateFont();
            }
          }
        } else {
          status.setText("No conservation colouring");
          status.validate();
          incitem.disable();
          
          ap.conservation = false;
          
          if (!PID.getState() || !BLOSUM62.getState()) {
            PIDthreshold.enable();
          }
          
          for (int j=0; j < ap.seqPanel.align.groups.size(); j++) {
            SequenceGroup sg =  ((SequenceGroup)ap.seqPanel.align.groups.elementAt(j));
            if (sg.colourScheme instanceof ConservationColourScheme) {
              sg.colourScheme = ((ConservationColourScheme)sg.colourScheme).cs;
              sg.colourScheme.setColours(sg);
              updateFont();
            }
          }
        }
      } else if (label.equals("Colour text")) {
        
        ap.seqPanel.seqCanvas.colourText = colourText.getState();
        blackText.setState(!colourText.getState());
        setColourText(colourText.getState());
        
        if (colourText.getState()) {
          status.setText("Colouring text");      
          status.validate();
        } else {
          status.setText("Text colour black");
          status.validate();
        }
        
        updateFont();
        
      } else if (label.equals("Black text")) {
        ap.seqPanel.seqCanvas.colourText = !blackText.getState();
        colourText.setState(!blackText.getState());
        setColourText(colourText.getState());
        updateFont();
        if (colourText.getState()) {
          status.setText("Colouring text");      
          status.validate();
        } else {
          status.setText("Text colour black");
          status.validate();
        }
      } else if (label.equals("Fast Draw")) {
        ap.seqPanel.setFastDraw(fastDraw.getState());
        if (bp.scorePanel != null) {
          bp.scorePanel.seqPanel.setFastDraw(fastDraw.getState());
        }
        if (fastDraw.getState()) {
          status.setText("Fast draw mode on");
          status.validate();
          courier.setState(false);
          times.setState(false);
          helvetica.setState(false);
          times.disable();
          helvetica.disable();
          courier.disable();
          setFont("Courier");
          updateFont();
        } else {
          status.setText("Fast draw mode off");
          status.validate();
          courier.setState(true);
          times.setState(false);
          helvetica.setState(false);
          times.enable();
          helvetica.enable();
          courier.enable();
          setFont("Courier");
          updateFont(); 
        }
      } else if (label.equals("Helvetica")) {
        status.setText("Changing font...");
        status.validate();
        helvetica.setState(true);
        times.setState(false);
        courier.setState(false);
        if (!ap.seqPanel.seqCanvas.fontName.equals("Helvetica")) {
          setFont("Helvetica");
        }
      } else if (label.equals("Courier")) {
        status.setText("Changing font...");
        status.validate();
        
        helvetica.setState(false);
        times.setState(false);
        courier.setState(true);
        if (!ap.seqPanel.seqCanvas.fontName.equals("Courier")) {
          setFont("Courier");
        }
      } else if (label.equals("Times-Roman")) {
        status.setText("Changing font...");
        status.validate();
        
        helvetica.setState(false);
        times.setState(true);
        courier.setState(false);
        if (!ap.seqPanel.seqCanvas.fontName.equals("TimesRoman")) {
          setFont("TimesRoman");
        }
      } else if (label.equals("Autocalculate consensus")) {
        if (ap.seqPanel.align.autoConsensus) {
          status.setText("Automatic consensus calculation OFF");
        } else {
          status.setText("Automatic consensus calculation ON");
        }
        ap.seqPanel.align.autoConsensus = !ap.seqPanel.align.autoConsensus;
        autoconsensus.setState( ap.seqPanel.align.autoConsensus );
        
      }
    } else if (e.target instanceof MenuItem) {
      String label = (String) arg;
      // Need some sort of window counter in here
      if (label.equals("Quit")) {
        status.setText("Closing frame...");
        status.validate();
        this.hide();
        this.dispose();
        System.out.println(parent);
        
        if (!(this.parent instanceof Applet)) {
          status.setText("Exiting...");
          status.validate();
          
          System.exit(0);
        }      
      } else if (label.equals("Close")) {
        status.setText("Closing frame...");
        status.validate();
        
        this.hide();
        this.dispose();
      } else if (label.equals("Set gap character to .")) {
        status.setText("Changing gap character...");
        status.validate();
        
        ap.seqPanel.align.gapCharacter = ".";
        edit.remove(gapCharacter);
        gapCharacter = new MenuItem("Set gap character to -");
        edit.add(gapCharacter);
      } else if (label.equals("Set gap character to -")) {
        status.setText("Changing gap character...");
        status.validate();
        
        ap.seqPanel.align.gapCharacter = "-";
        edit.remove(gapCharacter);
        gapCharacter = new MenuItem("Set gap character to .");
        edit.add(gapCharacter);
      } else if (label.equals("Mail alignment...")) {
        
        status.setText("Creating mail window...");
        status.validate();
        
        MailTextPopup mp = new MailTextPopup(this,"Mail text alignment",this);
        
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Mail postscript...")) {
        status.setText("Creating mail window...");
        status.validate();
        
        PostscriptPopup mp = new PostscriptPopup(this,"Mail postscript",this);
        
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Groups...")) {
        status.setText("Creating group edit window...");
        status.validate();
        
        GroupPopup gp = new GroupPopup(this,"Group properties",ap.seqPanel.align);
        
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Remove gapped columns")) {
        status.setText("Removing gaps...");
        status.validate();
        
        ap.seqPanel.align.removeGappedColumns();
        updateFont();
        
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Delete selected sequences")) {
        status.setText("Deleting selected sequences...");
        status.validate();
        
        for (int i=0;i < ap.sel.size(); i++) {
          ap.seqPanel.align.deleteSequence((DrawableSequence)ap.sel.elementAt(i));
        }
        ap.sel.removeAllElements();
        if (ap instanceof BigPanel) { 
          BigPanel bp = (BigPanel)ap;
          for (int i=0;i < bp.scorePanel.sel.size(); i++) {
            bp.scorePanel.seqPanel.align.deleteSequence((DrawableSequence)bp.scorePanel.sel.elementAt(i));
          }
          bp.scorePanel.sel.removeAllElements();
        }
        updateFont();
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Remote alignment at EBI")) {
        status.setText("Aligning sequences remotely...");
        status.validate();
        Sequence[] newseq = new Sequence[ap.seqPanel.align.size()];
        for (int i=0; i < newseq.length; i++) {
          String newstr = AlignSeq.extractGaps(" ",ap.seqPanel.align.ds[i].getSequence());
          newstr =  AlignSeq.extractGaps("-",newstr);
          newstr =  AlignSeq.extractGaps(".",newstr);
          
          newseq[i]  = new Sequence(ap.seqPanel.align.ds[i].name,
              newstr,
              ap.seqPanel.align.ds[i].start,
              ap.seqPanel.align.ds[i].end);
        }
        
        status.setText("Creating progress frame");
        status.validate();
        ProgressFrame pf = new ProgressFrame("Clustalw progress...",this,null);
        TextAreaPrintStream taps = new TextAreaPrintStream(System.out,pf.ta);
        ClustalwCGI cwcgi = new ClustalwCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runclustal",newseq,taps);
        Thread th = new Thread(cwcgi);
        pf.pp.ct = th;
        
        pf.show();
        Thread t = new Thread(pf.pp);
        
        t.start();
        th.start();
        
        status.setText("Starting clustalw thread...");
        status.validate();
      } else if (label.equals("Remote postal analysis at EBI")) {
        status.setText("Remote postal analysis...");
        status.validate();
        Sequence[] newseq = new Sequence[ap.seqPanel.align.size()];
        
        status.setText("Creating progress frame");
        status.validate();
        ProgressFrame pf = new ProgressFrame("Postal progress...",this,null);
        TextAreaPrintStream taps = new TextAreaPrintStream(System.out,pf.ta);
        PostalCGI cwcgi = new PostalCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runpostal",ap.seqPanel.align.ds,taps);
        Thread th = new Thread(cwcgi);
        pf.pp.ct = th;
        
        pf.show();
        Thread t = new Thread(pf.pp);
        
        t.start();
        th.start();
        
        status.setText("Starting postal thread...");
        status.validate();
      } else if (label.equals("Remote Jnet analysis at EBI")) {
        status.setText("Running Jnet remotely...");
        status.validate();
        
        JnetCGI cwcgi;
        int i=0;
        if (ap.sel.size() == 0) {	
          while (i < ap.seqPanel.align.ds.length && ap.seqPanel.align.ds[i] != null) {
            ap.seqPanel.align.ds[i].sequence = ap.seqPanel.align.ds[i].sequence.toUpperCase();
            i++;
          }
          cwcgi = new JnetCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runjnet",ap.seqPanel.align.ds,System.out,this);
        } else {
          DrawableSequence[] tmp = new DrawableSequence[ap.sel.size()];
          for (int j=0; j < ap.sel.size(); j++) {
            tmp[j] = (DrawableSequence)ap.sel.elementAt(j);
          }
          cwcgi = new JnetCGI("circinus.ebi.ac.uk",6543,"/cgi-bin/runjnet",tmp,System.out,this);
        }
        //status.setText("Creating progress frame");
        //	ProgressFrame pf = new ProgressFrame("Jnet progress...",this,null);
        //TextAreaPrintStream taps = new TextAreaPrintStream(System.out,pf.ta);
        
        
        Thread th = new Thread(cwcgi);
        
        //pf.pp.ct = th;
        
        //pf.show();
        //Thread t = new Thread(pf.pp);
        
        //t.start();
        th.start();
        
        status.setText("Starting jnet thread...");
        status.validate();
      } else if (label.equals("Local alignment")) {
        if (!(parent instanceof Applet)) {
          status.setText("Starting local alignment...");
          status.validate();
          
          Sequence[] newseq = new Sequence[ap.seqPanel.align.size()];
          for (int i=0; i < newseq.length; i++) {
            String newstr = AlignSeq.extractGaps(" ",ap.seqPanel.align.ds[i].getSequence());
            newstr =  AlignSeq.extractGaps("-",newstr);
            newstr =  AlignSeq.extractGaps(".",newstr);
            System.out.println(newstr);
            newseq[i]  = new Sequence(ap.seqPanel.align.ds[i].name,
                newstr,
                ap.seqPanel.align.ds[i].start,
                ap.seqPanel.align.ds[i].end);
          }
          ClustalwThread ct = new ClustalwThread(newseq);
          ct.start();
          
        } else {
          error("ERROR: Can't run local process from Applet",true);
        }
      } else if (label.equals("Above PID threshold only...")) {
        if (ap.seqPanel.align.cons != null) {
          status.setText("Creating PID threshold chooser...");
          status.validate();
          
          PIDthreshold.disable();
          PercentIdentityPopup pip = new PercentIdentityPopup(this,"PID threshold selection","Percent identity",0,100,threshold);
          status.setText("Colouring above PID threshold...");
          status.validate();
          updateFont();
          status.setText("done");
          status.validate();
          PIDthreshold.enable();
        } else {
          error("ERROR: No consensus to compare with",true);
        }
      } else if (label.equals("Conservation colour increment...")) {
        status.setText("Creating increment chooser...");
        status.validate();
        IncrementPopup pip = new IncrementPopup(this,"Conservation colour increment selection","Colour increment",0,50);
        status.setText("Changing colour increment...");
        
        status.validate();
        for (int j=0; j < ap.seqPanel.align.groups.size(); j++) {
          SequenceGroup sg =  ((SequenceGroup)ap.seqPanel.align.groups.elementAt(j));
          if (sg.colourScheme instanceof ConservationColourScheme) {
            ((ConservationColourScheme)sg.colourScheme).inc = increment;
            sg.colourScheme.setColours(sg);
            status.setText("Changing group colour increment");
            status.validate();
          }
        }
        updateFont();
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Move selected sequences to new alignment")) {
        if (ap.sel.size() > 0) {
          status.setText("Moving selected sequences...");
          status.validate();
          
          DrawableSequence[] s = new DrawableSequence[ap.sel.size()];
          for (int i=0; i < ap.sel.size(); i++) {
            s[i] = new DrawableSequence((Sequence)ap.sel.elementAt(i));
            ap.seqPanel.align.deleteSequence((Sequence)ap.sel.elementAt(i));
          }
          ap.sel = new Vector();
          status.setText("Creating new alignment window...");
          status.validate();
          
          AlignFrame af = new AlignFrame(this,s);
          Font f = getFont();
          af.setAlignFont(f.getName(),f.getStyle(),f.getSize());
          af.resize(700,500);
          af.show();
          status.setText("done");
          status.validate();
          updateFont();
        } else {
          error("ERROR: No sequences selected...",true);
        }
      } else if (label.equals("Copy selected sequences to new alignment")) {
        if (ap.sel.size() > 0) {
          status.setText("Copying selected sequences...");
          status.validate();
          
          DrawableSequence[] s = new DrawableSequence[ap.sel.size()];
          for (int i=0; i < ap.sel.size(); i++) {
            s[i] = new DrawableSequence((Sequence)ap.sel.elementAt(i));
          }
          ap.sel = new Vector();
          status.setText("Creating new alignment window...");
          status.validate();
          
          AlignFrame af = new AlignFrame(this,s);
          Font f = getFont();
          af.setAlignFont(f.getName(),f.getStyle(),f.getSize());
          
          af.resize(700,500);
          af.show();
          
          updateFont();
          status.setText("done");
          status.validate();
          
        }
      } else if (label.equals("Sort by group")) {
        status.setText("Sorting sequences by group...");
        status.validate();
        
        ap.seqPanel.align.sortGroups();
        ap.seqPanel.align.sortByGroup();
        updateFont();
        status.setText("done");
        status.validate();
      } else if (label.equals("Sort by ID")) {
        status.setText("Sorting sequences by ID...");
        status.validate();
        
        ap.seqPanel.align.sortByID();
        updateFont();
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Sort by tree order")) {
        if (tt != null) {
          status.setText("Sorting by tree order...");
          status.validate();
          ap.seqPanel.align.sortByTree(tt.tf.p.njt.tf);
          status.setText("Updating display");
          status.validate();
          updateFont();
          status.setText("done");
          status.validate();
        } else {
          error("ERROR: No tree defined",true);
        }
      } else if (label.equals("Sort by pairwise identity")) {
        if (ap.seqPanel.align.cons == null) {
          status.setText("Calculating consensus...");
          status.validate();
          ap.seqPanel.align.percentIdentity();
          status.setText("done");
          status.validate();
        }
        String s = "";
        for (int i = 0; i < ap.seqPanel.align.maxLength(); i++) {
          s = s + ap.seqPanel.align.cons[i].get("max");
        }
        
        status.setText("Sorting by pairwise identity...");
        status.validate();
        
        ap.seqPanel.align.sortByPID(new DrawableSequence("Consensus",s,1,s.length()));
        updateFont();
        status.setText("done");
        status.validate();
      } else if (label.equals("Remove redundancy")) {
        status.setText("Creating redundancy chooser...");
        status.validate();
        
        RedundancyPopup rp = new RedundancyPopup(this,"Redundancy threshold selection","Percent identity",0,100,100);
        updateFont();
        status.setText("Redundant sequences removed");
        status.validate();
      } else if (label.equals("Remove sequence <- left of selected columns")) {
        if (ap.selectedColumns.size() > 0) {
          status.setText("Trimming sequences left...");
          status.validate();
          
          Enumeration en = ap.selectedColumns.elements();
          int min = ap.seqPanel.align.maxLength;
          while (en.hasMoreElements()) {
            Integer i = (Integer)en.nextElement();
            if (i.intValue() < min) {min = i.intValue();}
          }
          
          if (min < ap.seqPanel.align.maxLength) {
            
            ap.seqPanel.align.trimLeft(min);
            if (bp.scorePanel != null) {
              bp.scorePanel.seqPanel.align.trimLeft(min);
            }
            for (int i=0; i < ap.selectedColumns.size();i++) {
              int temp = ((Integer)ap.selectedColumns.elementAt(i)).intValue();
              ap.selectedColumns.setElementAt(new Integer(temp-min),i);
            }
            status.setText("Trimmed left of " + min);
            status.validate();
          } else {
            error("ERROR: No columns selected",true);
          }
        }
        updateFont();
        
      } else if (label.equals("Remove sequence -> right of selected columns")) {
        if (ap.selectedColumns.size() > 0) {
          status.setText("Trimming sequences right...");
          status.validate();
          
          Enumeration en = ap.selectedColumns.elements();
          int max = 0;
          while (en.hasMoreElements()) {
            Integer i = (Integer)en.nextElement();
            if (i.intValue() > max) {max = i.intValue();}
          }
          
          if (max > 0) {
            
            ap.seqPanel.align.trimRight(max);
            if (bp.scorePanel != null) {
              bp.scorePanel.seqPanel.align.trimRight(max);
            }
            
            for (int i=0; i < ap.selectedColumns.size();i++) {
              int temp = ((Integer)ap.selectedColumns.elementAt(i)).intValue();
              if (temp > max) {ap.selectedColumns.removeElementAt(i);}
            }
            status.setText("Trimmed right of " + max);
            status.validate();
          }
          updateFont();
        } else {
          error("ERROR: No columns selected",true);
        }
      } else if (label.equals("Fetch sequence features")) {
        
        status.setText("Fetching sequence features...");
        status.validate();
        
        sft = new SequenceFeatureThread(this);
        sft.start();
        
      } else if (label.equals("Fetch PDB structure")) {
        status.setText("Creating PDB popup...");
        status.validate();
        
        PDBPopup pdbp = new PDBPopup(this,"PDB selector",ap.seqPanel.align);
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Select all sequences")) {
        status.setText("Selecting all sequences");
        status.validate();
        
        int i=0;
        while (i < ap.seqPanel.align.sequences.length && ap.seqPanel.align.sequences[i] != null ) {
          ap.sel.addElement( ap.seqPanel.align.sequences[i]);
          i++;
        }
        updateFont();
        status.setText("Selected all sequences");
        status.validate();
        
      } else if (label.equals("Deselect all sequences")) {
        status.setText("Deselecting all sequences...");
        status.validate();
        
        ap.sel.removeAllElements();
        updateFont();
        status.setText("Deselected all sequences");
        status.validate();
        
      } else if (label.equals("Invert sequence selection")) {
        status.setText("Inverting sequence selection");
        status.validate();
        
        int i=0;
        while (i < ap.seqPanel.align.sequences.length &&  ap.seqPanel.align.sequences[i] != null) {
          if (ap.sel.contains( ap.seqPanel.align.sequences[i])) {
            ap.sel.removeElement( ap.seqPanel.align.sequences[i]);
          } else {
            ap.sel.addElement( ap.seqPanel.align.sequences[i]);
          }
          i++;
        }
        updateFont();
        status.setText("Inverted sequence selection");
        status.validate();
        
      } else if (label.equals("Deselect all columns")) {
        status.setText("Deselecting all columns...");
        status.validate();
        
        ap.selectedColumns.removeAllElements();
        ap.scalePanel.scaleCanvas.paintFlag = true;
        ap.scalePanel.scaleCanvas.repaint();
        status.setText("Deselected all columns");
        status.validate();
        
      } else if (label.equals("Bold")) {
        status.setText("Setting bold font");
        status.validate();
        
        if (ap.seqPanel.seqCanvas.fontStyle != Font.BOLD) {
          setFont(Font.BOLD,ap.seqPanel.seqCanvas.fontSize);
        }
        status.setText("Changed to bold font");
        status.validate();
        
      } else if (label.equals("Plain")) {
        status.setText("Setting plain font");
        status.validate();
        
        
        if (ap.seqPanel.seqCanvas.fontStyle != Font.PLAIN) {
          setFont(Font.PLAIN,ap.seqPanel.seqCanvas.fontSize);
        }
        status.setText("Changed to plain font");
        status.validate();
        
      } else  if (label.indexOf("Size =") == 0) {
        status.setText("Setting font size");
        status.validate();
        
        StringTokenizer str  = new StringTokenizer(label);
        String tmp = str.nextToken();
        tmp = str.nextToken();
        tmp = str.nextToken();
        int size = (Integer.valueOf(tmp)).intValue();
        
        setFont(ap.seqPanel.seqCanvas.fontStyle,size);
        
      } else if (label.equals("Average distance tree using PID")) {
        if (ap.sel != null && ap.sel.size() > 3) {
          status.setText("Calculating average distance tree...");
          status.validate();
          
          tt = new TreeThread("Average distance tree using PID",this,ap.sel,"AV","PID");
        } else {
          status.setText("Calculating average distance tree...");
          status.validate();
          
          tt = new TreeThread("Average distance tree using PID",this,ap.seqPanel.align.ds,"AV","PID");
        }
        
        tt.start();
      } else if (label.equals("Neighbour joining tree using PID")) {
        if (ap.sel != null && ap.sel.size() > 3) {
          tt = new TreeThread("Neighbour joining tree using PID",this,ap.sel,"NJ","PID");
          status.setText("Calculating NJ tree");
          status.validate();
        } else {
          tt = new TreeThread("Neighbour joining tree using PID",this,ap.seqPanel.align.ds,"NJ","PID");
          status.setText("Calculating NJ tree");
          status.validate();
        }
        tt.start();
      } else if (label.equals("Principal component analysis") ){
        status.setText("Starting PCA calculation...");
        status.validate();
        
        pca = new PCAThread(this,ap.seqPanel.align.ds);
        pca.start();
      } else if (label.equals("Consensus")) {
        status.setText("Calculating consensus");
        status.validate();
        int count = ap.countSelected();
        if (count == 0) {
          ap.seqPanel.align.percentIdentity();
          
          if (bp.scorePanel != null) {
            bp.scorePanel.seqPanel.align.ds[0] = ap.seqPanel.align.qualityScore;
            updateFont();
          }
          ap.seqPanel.align.percentIdentity();
          status.setText("Using all sequences for consensus");
          status.validate();
        } else {
          ap.seqPanel.align.percentIdentity(ap.sel);
        }
        
        cons = ap.seqPanel.align.cons;
        
        //       status.setText("Updating colours");
        //status.validate();
        
        //       ap.setSequenceColor(new PIDColourScheme(cons));
        //       ap.setSequenceColor(new ClustalxColourScheme(ap.seqPanel.align.cons2,ap.seqPanel.align.size()));
        
        //       updateFont();
        status.setText("done");
        status.validate();
      } else if (label.indexOf("User") == 0) {
        status.setText("Creating user colour chooser...");
        status.validate();
        
        ColourChooserFrame ccf = new ColourChooserFrame(this,ap.color);
        ccf.show();   
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Information")) {
        
        if (parent != null) {
          if (parent instanceof Applet) {
            status.setText("Fetching URL help file");
            status.validate();
            try {
              String urlStr = "http://circinus.ebi.ac.uk:6543/~michele/jalview/help.html";
              URL u = new URL(urlStr);
              ((Applet)parent).getAppletContext().showDocument(u,"right");
            } catch (MalformedURLException ex) {
              status.setText("Couldn't fetch help URL");
              status.validate();
            }
            status.setText("Help file displayed in browser");
            status.validate();
          }
          
        } else {
          status.setText("Fetching URL information file");
          //status.setText("Couldn't fetch help URL");
          status.validate();
          //IceBrowser ib = new IceBrowser("Jalview information file","http://circinus.ebi.ac.uk:6543/jalview/index.html");
          //ib.resize(600,800);
          // ib.show();
        }
      } else if (label.equals("Conservation")) {
        conservation.enable();
        incitem.enable();
        status.setText("Calculating conservation...");
        status.validate();
        
        for (int j=0; j < ap.seqPanel.align.groups.size(); j++) {
          SequenceGroup sg =  ((SequenceGroup)ap.seqPanel.align.groups.elementAt(j));
          Vector tmp = sg.sequences;
          sg.conserve = new Conservation("All",ap.seqPanel.align.cons,ResidueProperties.propHash,3,tmp,0,
              ap.seqPanel.align.sequences[0].getSequence().length()-1);
          sg.conserve.calculate();
          sg.conserve.verdict(false,20);
          
          System.out.println("Conservation = " + sg.conserve.consSequence.sequence);
          
          sg.colourScheme = new ConservationColourScheme(sg);
          sg.colourScheme.setColours(sg);
          
          updateFont();
        }
        status.setText("Calculated conservation");
        status.validate();
        
        conservation.setState(true);
        ap.conservation = true;
        PIDthreshold.disable();
        
      } else if (label.equals("Contents")) {
        if (parent != null) {
          if (parent instanceof Applet) {
            status.setText("Fetching URL help file");
            status.validate();
            try {
              String urlStr = "http://circinus.ebi.ac.uk:6543/~michele/jalview/contents.html";
              URL u = new URL(urlStr);
              ((Applet)parent).getAppletContext().showDocument(u,"right");
            } catch (MalformedURLException ex) {
              status.setText("Couldn't fetch help URL");
              
              status.validate();
            }
            status.setText("Help file displayed in browser");
            status.validate();
          }
          
        } else {
          status.setText("Fetching URL help file");
          status.validate();
          //IceBrowser ib = new IceBrowser("Jalview help file","http://circinus.ebi.ac.uk:6543/jalview/index.html");
          //ib.resize(600,800);
          //ib.show();
        }
      } else if (label.equals("Input alignment as URL")) {
        status.setText("Creating URL chooser...");
        status.validate();
        
        URLPopup up = new URLPopup(this,"Input alignment as URL");
      } else if (label.equals("Input alignment from local file")) {
        status.setText("Creating file chooser...");
        status.validate();
        FilePopup fp = new FilePopup(this,"Input alignment from local file");
        status.setText("done");
        status.validate();
      } else if (label.equals("Save alignment to local file")) {
        status.setText("Creating file chooser...");
        status.validate();
        
        if (parent instanceof Applet) {
          AppletFilePopup afp = new AppletFilePopup(this,"Save alignment to local file");
        } else {
          OutputFilePopup ofp = new OutputFilePopup(this,"Save alignment to local file");
        }
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Save postscript to local file")) {
        status.setText("Creating file chooser...");
        status.validate();
        if (parent instanceof Applet) {
          AppletPostscriptPopup app = new AppletPostscriptPopup(this,"Save postscript to local file",this);
        } else {
          PostscriptFilePopup pfp = new PostscriptFilePopup(this,"Save postscript to local file",this);
        }
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Input alignment via text box")) {
        status.setText("Creating input textbox...");
        status.validate();
        
        InputPopup ip = new InputPopup(this,"Input alignment via text box");
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Output alignment via text box")) {
        status.setText("Creating output textbox...");
        status.validate();
        
        OutputPopup ip = new OutputPopup(this,"Alignment " + this.getTitle());
        status.setText("done");
        status.validate();
        
      } else if (label.equals("Pairwise alignments")) {
        
        status.setText("Aligning selected sequences...");
        status.validate();
        
        PairAlignThread pat = new PairAlignThread(this);
        pat.start();
      }
      return true;
    } else {
      return false;
    }
    return false;
    
  }
  
  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) {
      if (parent instanceof Applet) {
        status.setText("Closing window...");
        status.validate();
        this.hide();
        this.dispose();
      } else if (parent == null) {
        status.setText("Quitting application");
        status.validate();
        
        System.exit(0);
      } else {
        status.setText("Closing window...");
        status.validate();
        
        this.hide();
        this.dispose();
      }
    }
    else super.handleEvent(evt);
    return false;
  }
  
  public int getScheme() {
    if (zappo.getState()) {return ColourProperties.ZAPPO;}
    else if (taylor.getState()) {return ColourProperties.TAYLOR;}
    else  if (PID.getState()) {return ColourProperties.PID;}
    else if (BLOSUM62.getState()) {return ColourProperties.BLOSUM62;}
    else if (Hydrophobicity.getState()) {return ColourProperties.HYDROPHOBIC;}
    else if (helix.getState()) {return ColourProperties.HELIX;}
    else if (strand.getState()) {return ColourProperties.STRAND;}
    else if (turn.getState()) {return ColourProperties.TURN;}
    else if (buried.getState()) {return ColourProperties.BURIED;}
    else return -1;
  }
  
  
  public void setBoxes(boolean state) {
    for (int i = 0; i < ap.seqPanel.align.groups.size();i++) {
      SequenceGroup sg = (SequenceGroup)ap.seqPanel.align.groups.elementAt(i);
      sg.displayBoxes = state;
      ap.seqPanel.align.displayBoxes(sg);
    }
  }
  public void setText(boolean state) {
    for (int i = 0; i < ap.seqPanel.align.groups.size();i++) {
      SequenceGroup sg = (SequenceGroup)ap.seqPanel.align.groups.elementAt(i);
      sg.displayText = state;
      ap.seqPanel.align.displayText(sg);
    }
  }
  public void setColourText(boolean state) {
    for (int i = 0; i < ap.seqPanel.align.groups.size();i++) {
      SequenceGroup sg = (SequenceGroup)ap.seqPanel.align.groups.elementAt(i);
      sg.colourText = state;
      ap.seqPanel.align.colourText(sg);
    }
  }
  
  
  
  // All the menus are added here
  public void fileMenu() {
    file = new Menu("File",true);
    
    if (!(parent instanceof Applet)) {
      file.add("Input alignment from local file");
      file.add("Input alignment as URL");
      file.addSeparator();
    }
    file.add("Save alignment to local file");
    file.add("Save postscript to local file");
    //    }
    file.addSeparator();
    file.add("Input alignment via text box");
    file.add("Output alignment via text box");
    file.addSeparator();
    file.add("Mail alignment...");
    file.add("Mail postscript...");
    
    file.addSeparator();
    file.add("Close");
    file.add("Quit");
  }
  
  public void editMenu() {
    
    edit = new Menu("Edit",true);
    edit.add("Groups...");
    groupEdit = new CheckboxMenuItem("Group editing mode");
    edit.add(groupEdit);
    groupEdit.setState(false);
    edit.addSeparator();
    edit.add("Select all sequences");
    edit.add("Deselect all sequences");
    edit.add("Invert sequence selection");
    edit.addSeparator();
    edit.add("Delete selected sequences");
    edit.add("Move selected sequences to new alignment");
    edit.add("Copy selected sequences to new alignment");
    edit.addSeparator();
    edit.add("Deselect all columns");
    edit.add("Remove sequence <- left of selected columns");
    edit.add("Remove sequence -> right of selected columns");
    edit.addSeparator();
    edit.add("Remove gapped columns");
    if (ap.seqPanel.align.gapCharacter.equals("-")) {
      gapCharacter = new MenuItem("Set gap character to .");
      edit.add(gapCharacter);
    } else if (ap.seqPanel.align.gapCharacter.equals(".")) {
      gapCharacter = new MenuItem("Set gap character to -");
      edit.add(gapCharacter);
    }
  }
  
  public void fontMenu() {
    font = new Menu("Font",true);
    
    fastDraw = new CheckboxMenuItem("Fast Draw");
    helvetica = new CheckboxMenuItem("Helvetica");
    courier = new CheckboxMenuItem("Courier");
    times  = new CheckboxMenuItem("Times-Roman");
    
    fastDraw.setState(true);
    helvetica.setState(false);
    times.setState(false);
    courier.setState(false);
    
    helvetica.disable();
    times.disable();
    courier.disable();
    
    font.add(fastDraw);
    font.addSeparator();
    
    font.add(helvetica);
    font.add(courier);
    font.add(times);
    
    
    font.addSeparator();
    font.add("Size = 1");
    font.add("Size = 2");
    font.add("Size = 4");
    font.add("Size = 6");
    font.add("Size = 8");
    font.add("Size = 10");
    font.add("Size = 12");
    font.add("Size = 14");
    font.add("Size = 16");
    font.add("Size = 20");
    font.add("Size = 24");
    font.addSeparator();
    font.add("Plain");
    font.add("Bold");
  }
  
  public void viewMenu() {
    view = new Menu("View",true);
    boxes = new CheckboxMenuItem("Boxes");
    boxes.setState(true);
    text = new CheckboxMenuItem("Text");
    text.setState(true);
    scores = new CheckboxMenuItem("Scores");
    scores.setState(false);
    ap.seqPanel.seqCanvas.showScores = false;
    ap.idPanel.idCanvas.showScores = false;
    
    view.add(boxes);
    view.add(text);
    view.add(scores);
    view.addSeparator();
    
    colourText = new CheckboxMenuItem("Colour text");
    colourText.setState(false);
    view.add(colourText);
    
    blackText = new CheckboxMenuItem("Black text");
    blackText.setState(true);
    
    view.add(blackText);
    
  }
  
  public void colourMenu() {
    colour = new Menu("Colour",true);
    
    clustalx = new CheckboxMenuItem("Clustalx colours");
    clustalx.setState(true);
    colour.add(clustalx);
    
    zappo = new CheckboxMenuItem("Zappo Colourscheme");
    zappo.setState(false);
    colour.add(zappo);
    
    taylor = new CheckboxMenuItem("Taylor Colourscheme");
    taylor.setState(false);
    colour.add(taylor);
    
    Hydrophobicity = new CheckboxMenuItem("By Hydrophobicity");
    Hydrophobicity.setState(false);
    colour.add(Hydrophobicity);
    
    helix = new CheckboxMenuItem("Helix propensity");
    helix.setState(false);
    colour.add(helix);
    
    strand = new CheckboxMenuItem("Strand propensity");
    strand.setState(false);
    colour.add(strand);
    
    turn = new CheckboxMenuItem("Turn propensity");
    turn.setState(false);
    colour.add(turn);
    
    buried = new CheckboxMenuItem("Buried index");
    buried.setState(false);
    colour.add(buried);
    
    colour.addSeparator();
    
    conservation = new CheckboxMenuItem("By conservation");
    conservation.disable();
    conservation.setState(false);
    ap.conservation = false;
    conservation.disable();
    colour.add(conservation);
    
    colour.addSeparator();
    
    PIDthreshold = new MenuItem("Above PID threshold only...");
    PIDthreshold.disable();
    incitem = new MenuItem("Conservation colour increment...");
    incitem.disable();
    
    colour.add(PIDthreshold);
    colour.add(incitem);
    userColours = new MenuItem("User defined colours...");
    userColours.disable();
    colour.add(userColours);
    
    colour.addSeparator();
    
    PID = new CheckboxMenuItem("By PID");
    PID.setState(false);
    
    SequenceGroup sg = (SequenceGroup)ap.seqPanel.align.groups.elementAt(0);
    sg.colourScheme = new PIDColourScheme();
    colour.add(PID);
    
    
    BLOSUM62 = new CheckboxMenuItem("By BLOSUM62 score");
    BLOSUM62.setState(false);
    colour.add(BLOSUM62);
    
    colour.addSeparator();
    features = new MenuItem("Fetch sequence features");
    colour.add(features);
    
    structures = new MenuItem("Fetch PDB structure");
    colour.add(structures);
    
  }
  
  public void calcMenu() {
    calc = new Menu("Calculate",true);
    calc.add("Consensus");
    autoconsensus = new CheckboxMenuItem("Autocalculate consensus");
    autoconsensus.setState(false);
    ap.seqPanel.align.autoConsensus = false;
    calc.add(autoconsensus);
    calc.addSeparator();
    calc.add("Sort by pairwise identity");
    calc.add("Sort by ID");
    calc.add("Sort by group");
    calc.add("Sort by tree order");
    calc.add("Remove redundancy");
    calc.addSeparator();
    calc.add("Pairwise alignments");
    calc.add("Principal component analysis");
    calc.addSeparator();
    calc.add("Average distance tree using PID");
    calc.add("Neighbour joining tree using PID");
    calc.addSeparator();
    calc.add("Conservation");
  }
  
  public void helpMenu() {
    help = new Menu("Help",true);
    help.add("Information");
    help.add("Contents");
  }
  
  public void alignMenu() {
    align = new Menu("Align",true);
    
    // We have no local alignment for an applet and a remote
    // alignment only if the applet server is the same as the clustalw
    // server.
    if (this.parent instanceof Applet) {
      Applet app = (Applet)this.parent;
      if (app.getCodeBase().getHost().equals("circinus.ebi.ac.uk")) {
        align.add("Remote alignment at EBI");	
        align.add("Remote postal analysis at EBI");
        align.add("Remote Jnet analysis at EBI");
      } else {
        align.disable();
      }
    } else {
      align.add("Local alignment");
      align.add("Remote alignment at EBI");	
      align.add("Remote postal analysis at EBI");
      align.add("Remote Jnet analysis at EBI");
    }
  }
  
  
  // Font stuff
  public void setAlignFont(String name, int style, int size) {
    ap.seqPanel.seqCanvas.fontName = name;
    ap.seqPanel.seqCanvas.fontStyle = style;
    ap.seqPanel.seqCanvas.fontSize = size;
    ap.seqPanel.seqCanvas.setFont(new Font(name,style,size));
    
    if (bp.scorePanel != null) {
      bp.scorePanel.seqPanel.seqCanvas.fontName = name;
      bp.scorePanel.seqPanel.seqCanvas.fontStyle = style;
      bp.scorePanel.seqPanel.seqCanvas.fontSize = size;
      bp.scorePanel.seqPanel.seqCanvas.setFont(new Font(name,style,size));
    }
  }
  public Font getAlignFont() {
    return ap.seqPanel.seqCanvas.f;
  }
  
  public void setFont(String fontName) {
    ap.seqPanel.seqCanvas.fontName = fontName;
    ap.seqPanel.seqCanvas.setFont(new Font(fontName, ap.seqPanel.seqCanvas.fontStyle, ap.seqPanel.seqCanvas.fontSize));
    
    if (bp.scorePanel != null) {
      bp.scorePanel.seqPanel.seqCanvas.fontName = fontName;
      bp.scorePanel.seqPanel.seqCanvas.setFont(new Font(fontName, ap.seqPanel.seqCanvas.fontStyle, ap.seqPanel.seqCanvas.fontSize));
    }
    updateFont();
    status.setText("Font changed to " + fontName);
  }
  
  public void setFont(int style,int size) {
    ap.seqPanel.seqCanvas.setFont(style,size);
    
    if (bp.scorePanel != null) {
      bp.scorePanel.seqPanel.seqCanvas.setFont(style,size);
    }
    
    updateFont();
    status.setText("Font changed to " + style + " " + size);
  }
  
  // This does a full redraw and resizes all the panels after a font change
  public void updateFont() {
    super.repaint();
    
    if (ap instanceof BigPanel) {
      BigPanel bp = (BigPanel)ap;
      bp.setScrollValues(ap.seqPanel.offx,0);
      
      if (bp.scorePanel != null) {
        bp.scorePanel.seqPanel.invalidate();
        
        bp.scorePanel.idPanel.idCanvas.invalidate();
        bp.scorePanel.idPanel.invalidate();
        
        
        bp.scorePanel.scalePanel.invalidate();
        bp.scorePanel.invalidate();
        bp.scorePanel.validate();
        
        bp.invalidate();
        bp.validate();
        
        bp.scorePanel.idPanel.idCanvas.paintFlag = true;
        bp.scorePanel.seqPanel.seqCanvas.paintFlag = true;
        bp.scorePanel.scalePanel.scaleCanvas.paintFlag = true;
        
        bp.scorePanel.scalePanel.scaleCanvas.repaint();  
        bp.scorePanel.idPanel.idCanvas.paint(bp.scorePanel.idPanel.idCanvas.getGraphics());
        bp.scorePanel.seqPanel.seqCanvas.paint(bp.scorePanel.seqPanel.seqCanvas.getGraphics());
        
        bp.scorePanel.invalidate();
        bp.scorePanel.validate();
      }
    }
    
    ap.seqPanel.setScrollValues(ap.seqPanel.offx,ap.seqPanel.offy);
    
    ap.idPanel.idCanvas.paintFlag = true;
    ap.seqPanel.seqCanvas.paintFlag = true;
    ap.scalePanel.scaleCanvas.paintFlag = true;
    
    ap.scalePanel.scaleCanvas.repaint();  
    ap.idPanel.idCanvas.paint(ap.idPanel.idCanvas.getGraphics());
    ap.seqPanel.seqCanvas.paint(ap.seqPanel.seqCanvas.getGraphics());
    
    ap.invalidate();
    ap.validate();
    this.validate();
    
    if (tt != null) {
      tt.tf.p.mc.repaint();
    }
    if (pca != null) {
      pca.p.rc.redrawneeded = true;
      pca.p.rc.repaint();
    }
  }
  
  // The properties stuff for the OutputGenerator interface
  public MailProperties getMailProperties() {return mp;}
  public PostscriptProperties getPostscriptProperties(){return pp;}
  public FileProperties getFileProperties(){return fp;}
  
  public void setMailProperties(MailProperties mp){this.mp = mp;}
  public void setPostscriptProperties(PostscriptProperties pp){this.pp = pp;}
  public void setFileProperties(FileProperties fp){this.fp = fp;}
  
  public String getText(String format) {
    if (FormatProperties.contains(format)) {
      return FormatAdapter.get(format,ap.seqPanel.align.sequences);
    } else {
      return null;
    }
  }
  
  
  public void getPostscript(BufferedWriter bw) {
    Postscript p  = new Postscript(this,bw);
    p.generate();
  }
  
  public StringBuffer getPostscript() {
    Postscript p = new Postscript(this,true);
    p.generate();
    return p.out;
  }
  public void getPostscript(PrintStream ps) {
    Postscript p = new Postscript(this,ps);
    
    p.generate();
    ps.flush();
    ps.close();
  }
  
  public static void fetchPDBStructure(DrawableSequence seq, String srsServer) throws UnknownHostException,IOException {
    if (seq.pdbcode.size() > 0) {
      System.out.println("code = " + seq.pdbcode.elementAt(0));
      PDBfile pdb = new PDBfile("http://" + srsServer + "wgetz?-e+[pdb-id:" + seq.pdbcode.elementAt(0) + "]","URL");
      seq.setPDBfile(pdb);
      ((PDBChain)pdb.chains.elementAt(seq.maxchain)).isVisible = true;
      ((PDBChain)pdb.chains.elementAt(seq.maxchain)).ds = seq;
      ((PDBChain)pdb.chains.elementAt(seq.maxchain)).colourBySequence();
      
      rotFrame f = new rotFrame(pdb);
      f.resize(500,500);
      f.show();
      
    } else {
      System.out.println("No pdb code found");
    }
  }
  
  public static AlignFrame parseArgs(String[] args) {
    AlignFrame af = null;
    int[] done = new int[args.length];
    
    for (int i=0; i < done.length; i++) {
      done[i] = 0;
    }
    
    try {
      if (args.length == 2) {
        af = new AlignFrame(null,args[0],args[1],"MSF");
        af.setTitle(args[0]);
        
      } else if (args.length >= 3) {
        af = new AlignFrame(null,args[0],args[1],args[2]);
        af.setTitle(args[0]);    
        
        for (int i=0; i < args.length; i++) {
          
          if (args[i].equals("-mail")) {
            af.mp.server = args[i+1];
            System.out.println("Mail server = " + af.mp.server);
          }
          if (args[i].equals("-srsserver")) {
            af.srsServer = args[i+1];
            if (!af.srsServer.substring(af.srsServer.length()-1).equals("/")) {
              af.srsServer = af.srsServer + "/";
            }
            System.out.println("Srs server = " + af.srsServer);
          }
          if (args[i].equals("-database")) {
            af.database = args[i+1];
            System.out.println("SRS database = " + af.database);
          }
        }
      } else {
        System.out.println("\nUsage: java jalview.AlignFrame <alignmentfile> <type> <format> [-mail <mailServer>]\n");
        System.out.println("              [-srsserver <srsServer>] [-database <srs database>]\n");
        System.out.println("  type  = File or URL  (case is important I'm afraid)");
        System.out.println("format  = MSF, CLUSTAL, FASTA, BLC, MSP  or PIR\n");
        System.out.println("srsServer is the cgi-bin directory containing wgetz on your srs server.");
        System.out.println("For example: srs.ebi.ac.uk:5000/srs5bin/cgi-bin");
        System.out.println("database is the srs database to be queried");
        System.exit(0);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return af;
  }
  public void regroup(float threshold) {
    if (tt != null) {
      status.setText("Regrouping sequences by tree...");
      status.validate();
      
      TreeFile tree = tt.tf.p.njt.tf;
      
      tree.groups.removeAllElements();
      tree.groupNodes(tree.top,threshold);
      tree.setColor((SequenceNode)tree.top,Color.black);
      
      for (int i=0; i < tree.groups.size(); i++) {
        ap.sel.removeAllElements();
        
        int tmp = i%(7);
        Color[] col = ResidueProperties.groupColors[tmp];
        
        tree.setColor((SequenceNode)tree.groups.elementAt(i),col[0].darker());
        
        // l is vector of Objects
        Vector l = tree.findLeaves((SequenceNode)tree.groups.elementAt(i),new Vector());
        for (int j= 0; j < l.size(); j++) {
          SequenceNode sn = (SequenceNode)l.elementAt(j);
          ap.sel.addElement((Sequence)sn.element);
        }
        
        SequenceGroup sg = ap.seqPanel.align.addGroup();
        SequenceGroup sg2 = ap.seqPanel.align.findGroup((Sequence)ap.sel.elementAt(0));
        sg.colourScheme = sg2.colourScheme;
        sg.conserve = null;
        sg.color = ap.color;
        
        for (int ii=0; ii < ap.sel.size(); ii++) {
          ap.seqPanel.align.removeFromGroup(ap.seqPanel.align.findGroup((Sequence)ap.sel.elementAt(ii)),(Sequence)ap.sel.elementAt(ii));
          ap.seqPanel.align.addToGroup(sg,(Sequence)ap.sel.elementAt(ii));
        }
        
        if (conservation.getState() &&sg.conserve != null) {
          status.setText("Setting conservation colour scheme...");
          status.validate();
          sg.colourScheme = new ConservationColourScheme(sg);
        }
        status.setText("Setting residue colours...");
        status.validate();
        
        sg.colourScheme.setColours(sg);
        ap.seqPanel.align.setColourScheme(sg);
        
        status.setText("Finished group");
        status.validate();
        
      }
      
      ap.sel.removeAllElements();
      updateFont();
      status.setText("Finished grouping nodes");
      status.validate();
      
    } else {
      error("ERROR: No tree defined yet",true);
    }
  }
  
  public static void showConsole() {
    Frame console = new Frame("Jalview console");
    console.setLayout(new BorderLayout());
    TextArea ta = new TextArea(20,80);
    console.add("Center",ta);
    console.resize(700,200);
    console.show();
    TextAreaPrintStream taps = new TextAreaPrintStream(System.out,ta);
    
    System.setOut(taps);
  }
  public void wait(int s) {
    try {
      Thread.sleep(s);
    } catch (Exception e) {
    }
  }
  public static void main(String[] args) {
    // AlignFrame.showConsole();
    
    AlignFrame af = parseArgs(args);   
    af.printVersion();
    if (af != null) {  
      //      ScoreSequence[] sseq = new ScoreSequence[1];
      
      //      af.bp.seqPanel.align.addSequence(sseq);
      //      af.ap.seqPanel.align.percentIdentity();
      //      af.cons = af.ap.seqPanel.align.cons;
      
      //      PIDColourScheme pidcs = new PIDColourScheme(af.ap.seqPanel.align.cons);
      
      af.ap.seqPanel.align.percentIdentity();
      
      ClustalxColourScheme cxcs = 
        new ClustalxColourScheme(af.ap.seqPanel.align.cons2,af.ap.seqPanel.align.size());
      af.ap.seqPanel.align.setColourScheme(cxcs);
      af.resize(700,500);
      af.show();
      af.status.setText("Updating font and colours");
      af.status.validate();
      //      af.updateFont();
      af.wait(500);
      af.updateFont();
      af.status.setText("done");
      af.status.validate();
      
    } else {
      System.out.println("ERROR in argument list");
      System.exit(0);
    }
    
  }
  public void update(Graphics g) {
    paint(g);
  }
  
  public void printVersion() {
    System.out.println("Jalview version : 1.6.4jnet2");
    System.out.println("Author: Michele Clamp (c) 1998");
    System.out.println("$Header: /homes/michele/cvs/java/jalview/AlignFrame.java,v 1.9.4.4 1999/01/06 11:57:51 michele Exp $");
    // $Author: michele $
    // $Date: 1999/01/06 11:57:51 $
    // $Log: AlignFrame.java,v $
    // Revision 1.9.4.4  1999/01/06 11:57:51  michele
    // Slight bug where the postscript was going to standard out rather than mail
    //
    // Revision 1.9.4.3  1999/01/06 11:42:36  michele
    // Changed CVS keywords
    //
    // Revision 1.9.4.2  1999/01/06 11:31:36  michele
    // Changed output options to have save to file in browser - via CGI
    // Needs tidying to have server/location variables.  Also there is
    // a problem with FilePopup and hiding buttons.
    // Increased buffer size in mail output to overcome strange explorer bug.
    // Added extra headers to overcome web server virtual hosting
    // Other stuff I can't remember
    //    
  }
  
  public void error(String text, boolean beep) {
    status.setText(text);
    status.validate();
    
    if (beep) {
      System.out.print("\07");
      System.out.flush();
    }
  }
}







