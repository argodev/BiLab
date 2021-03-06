/* FeatureEdit.java
 *
 * created: Tue Dec  1 1998
 *
 * This file is part of Artemis
 *
 * Copyright (C) 1998,1999,2000,2001,2002  Genome Research Limited
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
 *
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/FeatureEdit.java,v 1.3 2004/08/13 13:59:20 tjc Exp $
 **/

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.util.*;
import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.sequence.*;

import uk.ac.sanger.artemis.io.OutOfDateException;
import uk.ac.sanger.artemis.io.LocationParseException;
import uk.ac.sanger.artemis.io.InvalidRelationException;
import uk.ac.sanger.artemis.io.QualifierParseException;
import uk.ac.sanger.artemis.io.Range;
import uk.ac.sanger.artemis.io.RangeVector;
import uk.ac.sanger.artemis.io.Key;
import uk.ac.sanger.artemis.io.KeyVector;
import uk.ac.sanger.artemis.io.Location;
import uk.ac.sanger.artemis.io.Qualifier;
import uk.ac.sanger.artemis.io.QualifierVector;
import uk.ac.sanger.artemis.io.EntryInformation;
import uk.ac.sanger.artemis.io.EntryInformationException;
import uk.ac.sanger.artemis.io.StreamQualifier;
import uk.ac.sanger.artemis.io.QualifierInfo;
import uk.ac.sanger.artemis.io.EmblStreamFeature;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import javax.swing.*;

/**
 *  FeatureEdit class
 *
 *  @author Kim Rutherford
 *  @version $Id: FeatureEdit.java,v 1.3 2004/08/13 13:59:20 tjc Exp $
 **/

public class FeatureEdit extends JFrame
                         implements EntryChangeListener, FeatureChangeListener 
{

  /** Used to get current time/date in externalEdit(). */
  private static java.util.Calendar calendar =
                               java.util.Calendar.getInstance();

  /** The choice of feature keys - created in createComponents(). */
  private KeyChoice key_choice;

  /** The choice of qualifiers - created in createComponents(). */
  private QualifierChoice qualifier_choice = null;

  private final static int LOCATION_TEXT_WIDTH = 80;

  /** The location text - set by updateLocation(). */
  private JTextField location_text = new JTextField (LOCATION_TEXT_WIDTH);

  private JButton add_qualifier_button = new JButton ("Add qualifier");

  /** When pressed - apply changes and dispose of the component. */
  private JButton ok_button = new JButton ("OK");

  /** When pressed - discard changes and dispose of the component. */
  private JButton cancel_button = new JButton ("Cancel");

  /** When pressed - apply changes and keep the component open. */
  private JButton apply_button = new JButton ("Apply");

  /** Edit area for qualifiers - created by createComponents(). */
  private QualifierTextArea qualifier_text_area;

  /** The Feature this object is displaying. */
  private Feature edit_feature;

  /**
   *  The GotoEventSource that was passed to the constructor - used for the
   *  "Goto Feature" button.
   **/
  private GotoEventSource goto_event_source;

  /** Entry containing the Feature this object is displaying. */
  private Entry edit_entry;

  /** EntryGroup that contains this Feature (passed to the constructor). */
  private EntryGroup entry_group;

  /**
   *  The datestamp of the RWCorbaFeature when updateFromFeature () was last
   *  called or null if this is not a RWCorbaFeature.
   **/
  private Date datestamp = null;

  /** The Selection that was passed to the constructor. */
  private Selection selection;

  /**
   *  The contents of the QualifierTextArea before the user edits anything.
   *  This is used to work out if anything has changed since the creation of
   *  the FeatureEdit.
   **/
  final String orig_qualifier_text;

  /**
   *  Create a new FeatureEdit object from the given Feature.
   *  @param entry_group The EntryGroup that contains this Feature.
   *  @param selection The Selection operate on.  The operations are "Remove
   *    Range" and "Grab Range"
   *  @param goto_event_source The object the we will call gotoBase () on.
   **/
  public FeatureEdit (final Feature edit_feature,
                      final EntryGroup entry_group,
                      final Selection selection,
                      final GotoEventSource goto_event_source) 
  {
    super("Artemis Feature Edit: " + edit_feature.getIDString () +
          (edit_feature.isReadOnly () ?
           "  -  (read only)" :
           ""));

    this.edit_feature = edit_feature;
    this.edit_entry   = edit_feature.getEntry ();
    this.entry_group  = entry_group;
    this.selection    = selection;
    this.goto_event_source = goto_event_source;

    createComponents();

    updateFromFeature();

    orig_qualifier_text = qualifier_text_area.getText();
  
    edit_feature.getEntry().addEntryChangeListener(this);
    edit_feature.addFeatureChangeListener(this);

    addWindowListener(new WindowAdapter() 
    {
      public void windowClosing (WindowEvent event) 
      {
        stopListening ();
        dispose ();
      }
    });

    pack ();

    final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(new Point((screen.width - getSize().width)/2,
                          (screen.height - getSize().height)/2));

    qualifier_text_area.requestFocus();
  }

  /**
   *  Remove this object as a feature and entry change listener.
   **/
  public void stopListening() 
  {
    getEntry().removeEntryChangeListener (this);
    getFeature().removeFeatureChangeListener (this);
  }

  /**
   *  Implementation of the EntryChangeListener interface.  We listen to
   *  EntryChange events so we can notify the user if of this component if the
   *  feature gets deleted.
   **/
  public void entryChanged (EntryChangeEvent event) 
  {
    switch(event.getType())
    {
      case EntryChangeEvent.FEATURE_DELETED:
        if (event.getFeature () == edit_feature) 
        {
          stopListening ();
         dispose ();
        }
        break;
      default:
        // do nothing;
        break;
    }
  }

  /**
   *  Add an ActionListener to the Cancel JButton of this FeatureEdit.
   **/
  public void addCancelActionListener(final ActionListener l) 
  {
    cancel_button.addActionListener(l);
  }

  /**
   *  Remove an ActionListener from the Cancel JButton of this FeatureEdit.
   **/
  public void removeCancelActionListener(final ActionListener l) 
  {
    cancel_button.removeActionListener(l);
  }

  /**
   *  Add an ActionListener to the Apply JButton of this FeatureEdit.
   **/
  public void addApplyActionListener(final ActionListener l) 
  {
    apply_button.addActionListener (l);
  }

  /**
   *  Remove an ActionListener from the Apply JButton of this FeatureEdit.
   **/
  public void removeApplyActionListener(final ActionListener l) 
  {
    apply_button.removeActionListener(l);
  }

  /**
   *  Implementation of the FeatureChangeListener interface.  We need to
   *  listen to feature change events from the Features in this object so that
   *  we can update the display.
   *  @param event The change event.
   **/
  public void featureChanged(FeatureChangeEvent event) 
  {
    // re-read the information from the feature
    switch (event.getType ()) 
    {
    case FeatureChangeEvent.LOCATION_CHANGED:
      updateLocation ();
      break;
    case FeatureChangeEvent.KEY_CHANGED:
      updateKey ();
      break;
    case FeatureChangeEvent.QUALIFIER_CHANGED:
      if(qualifier_text_area.getText ().equals (orig_qualifier_text)) 
        updateFromFeature ();
      else
      {
        final String message =
          "warning: the qualifiers have changed outside the editor - " +
          "view now?";

        final YesNoDialog yes_no_dialog =
          new YesNoDialog(FeatureEdit.this, message);

        if(yes_no_dialog.getResult()) 
          new FeatureViewer(getFeature());
      }
      break;
    default:
      updateFromFeature ();
      break;
    }
  }


  /**
   *  Create all the components for this FeatureEdit component.
   **/
  private void createComponents()
  {
    qualifier_text_area = new QualifierTextArea ();
    qualifier_text_area.setWrapStyleWord (true);

    key_choice =
      new KeyChoice(getEntryInformation(),getFeature().getKey());

    final JPanel key_and_qualifier_panel = new JPanel();

    location_text.setBackground(Color.white);

    final JPanel key_panel = new JPanel();
    key_panel.add(new JLabel ("Key:"));
    key_panel.add(key_choice);

    key_and_qualifier_panel.setLayout(new BorderLayout());
    key_and_qualifier_panel.add(key_panel, "West");

    qualifier_choice = new QualifierChoice(getEntryInformation(),
                                           key_choice.getSelectedItem(),
                                           null);

    final JPanel qualifier_panel = new JPanel();
    final JButton qualifier_add_button = new JButton("Add Qualifier:");

    qualifier_panel.add(qualifier_add_button);
    qualifier_panel.add(qualifier_choice);

    key_and_qualifier_panel.add(qualifier_panel, "East");

    key_choice.addItemListener(new ItemListener() 
    {
      public void itemStateChanged(ItemEvent _) 
      {
        qualifier_choice.setKey (key_choice.getSelectedItem ());
      }
    });

    qualifier_add_button.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        final String qualifier_name =
          (String)qualifier_choice.getSelectedItem ();

        QualifierInfo qualifier_info =
          getEntryInformation().getQualifierInfo(qualifier_name);

        if(qualifier_info == null) 
        {
          qualifier_info = new QualifierInfo(qualifier_name,
                              QualifierInfo.OPTIONAL_QUOTED_TEXT,
                              null, null, false);
        }

        qualifier_text_area.append("/" + qualifier_name);

        switch (qualifier_info.getType ()) 
        {
          case QualifierInfo.QUOTED_TEXT:
            if(qualifier_name.equals("GO")) 
            {
              // special case for /GO
              final java.util.Calendar calendar =
                      java.util.Calendar.getInstance();
                
              final java.util.Date current_time =
                                    calendar.getTime();
            
              final java.text.SimpleDateFormat date_format =
                  new java.text.SimpleDateFormat("yyyyMMdd");
            
              final StringBuffer result_buffer = new StringBuffer();
            
              date_format.format(current_time, result_buffer,
                                 new java.text.FieldPosition(java.text.DateFormat.DATE_FIELD));
            
              final String go_string =
                "aspect=; term=; GOid=GO:; "+
                "evidence=ISS; db_xref=GOC:unpublished; " +
                "with=UNIPROT:; date=" + result_buffer;
              qualifier_text_area.append("=\"" + go_string + "\"");
            } 
            else 
              qualifier_text_area.append ("=\"\"");
            
            break;

          case QualifierInfo.NO_VALUE:
          case QualifierInfo.OPTIONAL_QUOTED_TEXT:
            break;

          default:
            qualifier_text_area.append ("=");
        }

        qualifier_text_area.append ("\n");
      }
    });

    final JPanel middle_panel = new JPanel();
    middle_panel.setLayout (new BorderLayout());

    final JPanel lower_panel = new JPanel();
    lower_panel.setLayout (new BorderLayout());

    final JPanel outer_location_button_panel = new JPanel();
    lower_panel.add(outer_location_button_panel, "North");
    outer_location_button_panel.setLayout(new BorderLayout());

    final JPanel location_button_panel = new JPanel();
    outer_location_button_panel.add(location_button_panel, "West");

    final JPanel location_panel = new JPanel();
    location_panel.setLayout(new BorderLayout());
    location_panel.add(new JLabel("location: "), "West");
    location_panel.add (location_text, "Center");

    final JButton complement_button = new JButton("Complement");
    location_button_panel.add(complement_button);
    complement_button.addActionListener(new ActionListener () 
    {
      public void actionPerformed(ActionEvent e) 
      {
        complementLocation();
      }
    });

    final JButton grab_button = new JButton("Grab Range");
    location_button_panel.add(grab_button);
    grab_button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        grabSelectedRange ();
      }
    });

    final JButton remove_button = new JButton("Remove Range");
    location_button_panel.add(remove_button);
    remove_button.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        removeSelectedRange();
      }
    });

    final JButton goto_button = new JButton("Goto Feature");
    location_button_panel.add(goto_button);
    goto_button.addActionListener(new ActionListener () 
    {
      public void actionPerformed(ActionEvent e)
      {
        goto_event_source.gotoBase(getFeature().getFirstBaseMarker());
      }
    });

    final JButton select_button = new JButton("Select Feature");
    location_button_panel.add(select_button);
    select_button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) 
      {
        getSelection().set(getFeature());
      }
    });

    final boolean sanger_options =
      Options.getOptions().getPropertyTruthValue("sanger_options");

    if(sanger_options) 
    {
      // a PSU only hack 
      final JButton tidy_button = new JButton("Tidy");
      location_button_panel.add(tidy_button);
      tidy_button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e) 
        {
          try 
          {
            tidy ();
          } 
          catch (QualifierParseException exception) 
          {
            final String error_string = exception.getMessage();
            new MessageDialog(FeatureEdit.this,
                              "Cannot tidy - qualifier error: " +
                              error_string);
          }
        }
      });
    }

    if(Options.getOptions().getProperty("external_editor") != null)
    {
      final JButton external_fasta_edit_button = new JButton("MESS/FASTA");
      location_button_panel.add(external_fasta_edit_button);
      external_fasta_edit_button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e) 
        {
          try 
          {
            if(getFeature().getQualifierByName("fasta_file") != null) 
            {
              final String DEFAULT_MAX_EUK_FASTA_HITS = "10";
              final String max_fasta_hits;

              final String max_fasta_hits_from_options =
                Options.getOptions().getProperty("mess_fasta_hits");

              if(Options.getOptions().isEukaryoticMode()) 
              {
                if (max_fasta_hits_from_options == null) 
                  max_fasta_hits = DEFAULT_MAX_EUK_FASTA_HITS;
                else 
                  max_fasta_hits = max_fasta_hits_from_options;

                externalEdit(new String[] 
                {
                  "-fasta",
                  "-maxhits",
                  max_fasta_hits,
                  "-euk"
                });
              } 
              else 
              {
                if(max_fasta_hits_from_options == null) 
                {
                  externalEdit(new String[] 
                  {
                    "-fasta"
                  });
                } 
                else 
                {
                  externalEdit(new String[] 
                  {
                    "-fasta",
                    "-maxhits",
                    max_fasta_hits_from_options
                  });

                }
              }
              return;
            }
          } catch (InvalidRelationException _) {}
          
          new MessageDialog(FeatureEdit.this,
                            "nothing to edit - no /fasta_file qualifier");
        }
      });

      final JButton external_blastp_edit_button = new JButton("MESS/BLASTP");
      location_button_panel.add(external_blastp_edit_button);
      external_blastp_edit_button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e) 
        {
          try 
          {
            if(getFeature().getQualifierByName("blastp_file") != null)
            {
              final String DEFAULT_MAX_BLASTP_HITS = "10";
              final String max_blastp_hits;

              final String max_blastp_hits_from_options =
                Options.getOptions().getProperty("mess_blastp_hits");

              if(max_blastp_hits_from_options == null) 
                max_blastp_hits = DEFAULT_MAX_BLASTP_HITS;
              else 
                max_blastp_hits = max_blastp_hits_from_options;

              if(Options.getOptions().isEukaryoticMode()) 
              {
                externalEdit(new String[] 
                {
                  "-blastp",
                  "-maxhits",
                  max_blastp_hits,
                  "-euk"
                });
              } 
              else
              {
                externalEdit(new String[] 
                {
                  "-blastp",
                  "-maxhits",
                  max_blastp_hits
                });
              }
              return;
            }
          } catch(InvalidRelationException _) {}
          
          new MessageDialog(FeatureEdit.this,
                            "nothing to edit - no /blastp_file qualifier");
        }
      });

      final JButton external_go_edit_button = new JButton("MESS/GO");
      location_button_panel.add(external_go_edit_button);
      external_go_edit_button.addActionListener(new ActionListener () 
      {
        public void actionPerformed(ActionEvent e) 
        {
          try
          {
            if(getFeature().getQualifierByName("blastp+go_file") != null) 
            {
              final String DEFAULT_MAX_GO_BLAST_HITS = "10";
              final String max_go_blast_hits;

              final String max_go_blast_hits_from_options =
                Options.getOptions ().getProperty ("mess_blast_go_hits");

              if (max_go_blast_hits_from_options == null) 
                max_go_blast_hits = DEFAULT_MAX_GO_BLAST_HITS;
              else 
                max_go_blast_hits = max_go_blast_hits_from_options;

              if(Options.getOptions().isEukaryoticMode()) 
              {
                externalEdit(new String[] 
                {
                  "-blastp+go",
                  "-maxhits",
                  max_go_blast_hits,
                  "-euk"
                });
              } 
              else
              {
                externalEdit(new String[] 
                {
                  "-blastp+go",
                  "-maxhits",
                  max_go_blast_hits
                });
              }
              return;
            }
          } catch (InvalidRelationException _) {}
          
          new MessageDialog(FeatureEdit.this,
                            "nothing to edit - no /blastp+go_file qualifier");
        }
      });
    }

    middle_panel.add(location_panel, "North");

    getContentPane().add(key_and_qualifier_panel, "North");

    cancel_button.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) 
      {
        stopListening();
        dispose();
      }
    });

    if(!getFeature().isReadOnly())
    {
      ok_button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if(setFeature()) 
          {
            stopListening();
            dispose();
          }
        }
      });

      apply_button.addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent e) 
        {
          setFeature();
        }
      });
    }

    final FlowLayout flow_layout =
                 new FlowLayout(FlowLayout.CENTER, 18, 5);

    final JPanel ok_cancel_update_panel = new JPanel(flow_layout);

    if(!getFeature().isReadOnly()) 
      ok_cancel_update_panel.add (ok_button);

    ok_cancel_update_panel.add (cancel_button);

    if(!getFeature().isReadOnly()) 
      ok_cancel_update_panel.add (apply_button);

    getContentPane().add(ok_cancel_update_panel, "South");

    middle_panel.add(lower_panel, "Center");
    lower_panel.add(new JScrollPane (qualifier_text_area), "Center");

    getContentPane().add(middle_panel, "Center");
  }

  /**
   *  Read the key, location and qualifier information from the feature and
   *  update the components.
   **/
  private void updateFromFeature() 
  {
    datestamp = getFeature().getDatestamp();

    updateKey();
    updateLocation();
    updateQualifiers();
  }

  /**
   *  Read the location from the feature and update the location field.
   **/
  private void updateLocation() 
  {
    location_text.setText(getFeature().getLocation().toStringShort());
  }

  /**
   *   Complement the current location_text.
   **/
  private void complementLocation() 
  {
    if(rationalizeLocation()) 
    {
      if(location_text.getText().startsWith("complement(")) 
      {
        final String new_text = location_text.getText().substring(11);
        if (new_text.endsWith (")")) 
        {
          final String new_text2 =
            new_text.substring (0, new_text.length () - 1);
          location_text.setText (new_text2);
        } 
        else 
          location_text.setText (new_text);
      }
      else 
      {
        final String new_text = location_text.getText ();
        location_text.setText ("complement(" + new_text + ")");
      }
    } 
    else 
      new MessageDialog (this, "complement failed - " +
                         "current location cannot be parsed");
  }

  /**
   *  Tidy the qualifiers by removing any indication that the annotation has
   *  been transferred from another gene.  Remove the "transferred_" part of
   *  all qualifier names and the "[[FROM sc_001234 abc1]]" bit of each
   *  corresponding qualifier value.
   **/
  private void tidy() throws QualifierParseException
  {
    final StringBuffer buffer = new StringBuffer();

    final QualifierVector qualifiers =
      qualifier_text_area.getParsedQualifiers(getEntryInformation());
    
    for(int qualifier_index = 0; qualifier_index < qualifiers.size();
         ++qualifier_index) 
    {
      final Qualifier this_qualifier = qualifiers.elementAt(qualifier_index);
      
      final QualifierInfo qualifier_info =
            getEntryInformation().getQualifierInfo(this_qualifier.getName());
      
      final StringVector qualifier_strings =
          StreamQualifier.toStringVector(qualifier_info, this_qualifier);
      
      for(int value_index = 0; value_index < qualifier_strings.size();
          ++value_index)
      {
        final String qualifier_string =
          qualifier_strings.elementAt(value_index);

        buffer.append(tidyHelper(qualifier_string) + "\n");
      }
    }

    qualifier_text_area.setText(buffer.toString());
  }

  /**
   *  Perform the action of tidy() on the String version of one qualifier.
   **/
  private String tidyHelper(final String qualifier_string)
  {
    final String temp_string;

    if (qualifier_string.startsWith("/transferred_")) 
      temp_string = "/" + qualifier_string.substring(13);
    else 
      temp_string = qualifier_string;

    final int start_index = temp_string.indexOf ("<<FROM ");
    final int end_index = temp_string.indexOf (">>");

    if(start_index != -1 && end_index != -1) 
    {
      if(temp_string.length() > end_index + 2 &&
         temp_string.charAt(end_index + 2) == ' ') 
        return temp_string.substring(0, start_index) +
               temp_string.substring(end_index + 3);
      else 
        return temp_string.substring(0, start_index) +
               temp_string.substring(end_index + 2);
    } 
    else 
      return temp_string;
  }

  /**
   *  Add the currently selected range to location_text.
   **/
  private void grabSelectedRange() 
  {
    if(!rationalizeLocation ()) 
    {
      new MessageDialog(this,
                        "grab failed - current location cannot be parsed");
      return;
    }

    final Range selected_range = getSelection().getSelectionRange();

    if(selected_range == null) 
    {
      new MessageDialog(this, "grab failed - nothing is selected");
      return;
    }

    // save it in case it gets mangled
    final String old_location_text = location_text.getText();

    if (old_location_text.endsWith ("))")) 
    {
      final String new_text =
        old_location_text.substring(0, old_location_text.length () - 2);

      location_text.setText(new_text + "," + selected_range.getStart () +
                            ".." + selected_range.getEnd () + "))");
    } 
    else
    {
      if(old_location_text.endsWith (")")) 
      {
        final String new_text =
          old_location_text.substring(0, old_location_text.length () - 1);

        location_text.setText(new_text + "," + selected_range.getStart () +
                              ".." + selected_range.getEnd () + ")");
      } 
      else
        location_text.setText(old_location_text + "," +
                              selected_range.getStart () +
                              ".." + selected_range.getEnd ());
    }

    if(!rationalizeLocation())
    {
      location_text.setText (old_location_text);
      new MessageDialog (this,
                         "grab failed - location cannot be parsed after " +
                         "grabbing");
    }
  }

  /**
   *  Remove the currently selected range of bases from location_text.
   **/
  private void removeSelectedRange()
  {
    if(!rationalizeLocation())
    {
      new MessageDialog(this,
                        "grab failed - current location cannot be parsed");
      return;
    }

    final MarkerRange selected_marker_range =
                                           getSelection().getMarkerRange();

    if(selected_marker_range == null) 
    {
      new MessageDialog(this, "remove range failed - no bases are selected");
      return;
    }

    final Range selected_range = selected_marker_range.getRawRange();

    if (selected_marker_range.getStrand() != getFeature().getStrand()) 
    {
      new MessageDialog(this, "remove range failed - you need to select " +
                         "some bases on the other strand");
      return;
    }

    final Location location;

    try 
    {
      location = new Location (location_text.getText ());
    }
    catch (LocationParseException e) 
    {
      // this shouldn't happen because we called rationalizeLocation ()
      throw new Error ("internal error - unexpected exception: " + e);
    }

    final Range location_total_range = location.getTotalRange();

    if(!selected_range.overlaps (location_total_range))
    {
      new MessageDialog(this, "remove range failed - the range you " +
                        "selected does not overlap the feature");
      return;
    }

    if(selected_range.contains(location_total_range)) 
    {
      new MessageDialog(this, "remove range failed - the range you " +
                        "selected overlaps the whole feature");
      return;
    }

    final RangeVector location_ranges = location.getRanges();
    final boolean location_is_complemented = location.isComplement();

    final RangeVector new_ranges = new RangeVector();

    // if the selected_range completely covers a range remove the
    // range. otherwise if the selected_range is completely within one of the
    // ranges two new ranges are created.  if the selected_range is not
    // completely contained then the appropriate end of the range is truncated
    for(int i = 0; i < location_ranges.size(); ++i) 
    {
      final Range this_range = location_ranges.elementAt(i);

      if(selected_range.overlaps (this_range)) 
      {
        try 
        {
          if(this_range.contains (selected_range) &&
             this_range.getStart () != selected_range.getStart () &&
             this_range.getEnd () != selected_range.getEnd ()) 
          {
            // chop a piece out of the middle and make two new ranges
            final Range new_start_range =
                    this_range.change(selected_range.getEnd () + 1,
                                      this_range.getEnd ());
            new_ranges.add(new_start_range);
            final Range new_end_range =
              this_range.change(this_range.getStart (),
                                selected_range.getStart () - 1);
            new_ranges.add(new_end_range);
          } 
          else
          {
            if(selected_range.contains (this_range)) {
              // delete (ie. don't copy) the range
            } 
            else
            {
              if(this_range.getStart() < selected_range.getStart()) 
              {
                // truncate the end of the range
                final Range new_start_range =
                  this_range.change(this_range.getStart(),
                                    selected_range.getStart() - 1);
                new_ranges.add(new_start_range);
              } 
              else
              {
                if(this_range.getEnd() > selected_range.getEnd())
                {
                  // truncate the start of the range
                  final Range new_end_range =
                    this_range.change(selected_range.getEnd() + 1,
                                      this_range.getEnd());
                  new_ranges.add(new_end_range);
                } 
                else
                  throw new Error ("internal error - can't remove range");
              }
            }
          }
        }
        catch(OutOfRangeException e)
        {
          throw new Error ("internal error - unexpected exception: " + e);
        }
      }
      else  
        new_ranges.add(this_range); // copy it unchanged
    }

    final Location new_location =
      new Location(new_ranges, location_is_complemented);

    location_text.setText(new_location.toStringShort ());
  }

  /**
   *  Attempt to parse the current location_text as a Location.  If it can be
   *  parsed it will be canonicalized (ie. the complement, if any, will be
   *  outermost).  Returns true if and only if the location_text could be
   *  parsed.
   **/
  private boolean rationalizeLocation()
  {
    try 
    {
      final Location location = new Location(location_text.getText ());
      location_text.setText(location.toStringShort ());
      return true;
    }
    catch(LocationParseException e) 
    {
      return false;
    }
  }


  /**
   *  Edit the qualifiers of this Feature in an external editor.  The
   *  qualifiers will be set when the editor finishes.  This method works by
   *  writing the qualifiers to a temporary file and the sequence of the
   *  feature to a different file.
   *  @param editor_extra_args Extra arguments to pass to the editor.  null
   *    means there are no extra args.
   **/
  private void externalEdit(final String[] editor_extra_args) 
  {
    try 
    {
      final String pre_edit_text = qualifier_text_area.getText();

      // write to a temporary file
      final java.util.Date current_time = calendar.getTime();

      final String temp_file_name =
               "/tmp/artemis_temp." + current_time.getTime();

      final File temp_file = new File(temp_file_name);

      final FileWriter out_writer    = new FileWriter(temp_file);
      final PrintWriter print_writer = new PrintWriter(out_writer);

      print_writer.write(qualifier_text_area.getText());
      print_writer.close();
      out_writer.close();

      final File sequence_temp_file = new File(temp_file_name + ".seq");
      final FileWriter sequence_out_writer =
                                     new FileWriter(sequence_temp_file);
      final PrintWriter sequence_print_writer =
                                   new PrintWriter(sequence_out_writer);

      getFeature().writeBasesOfFeature(sequence_print_writer);
      sequence_print_writer.close();
      sequence_out_writer.close();

      final String editor_path =
        Options.getOptions().getProperty("external_editor");

      final String[] process_args;

      if(editor_extra_args == null) 
      {
        process_args = new String[1];
        process_args[0] = temp_file.getCanonicalPath();
      } 
      else
      {
        process_args = new String[editor_extra_args.length + 1];
        System.arraycopy(editor_extra_args, 0, process_args, 0,
                         editor_extra_args.length);
        process_args[process_args.length - 1] = temp_file.getCanonicalPath();
      }

      final Process process =
        ExternalProgram.startProgram(editor_path, process_args);

      final ProcessWatcher process_watcher =
                                new ProcessWatcher(process, "editor", false);

      final Thread watcher_thread = new Thread(process_watcher);

      watcher_thread.start();

      final ProcessWatcherListener listener = new ProcessWatcherListener()
      {
        public void processFinished(final ProcessWatcherEvent event)
        {
          try 
          {
            final FileReader file_reader = new FileReader(temp_file);

            final BufferedReader buffered_reader = 
                                     new BufferedReader(file_reader);

            final StringBuffer buffer = new StringBuffer();

            String line;

            while((line = buffered_reader.readLine()) != null) 
              buffer.append(line + "\n");

            final String current_qualifier_text =
                                       qualifier_text_area.getText();

            if(!current_qualifier_text.equals(pre_edit_text))
            {
              final String message =
                  "the qualifiers have changed - apply changes from the " +
                  "external editor?";

              final YesNoDialog yes_no_dialog =
                  new YesNoDialog(FeatureEdit.this, message);

              if(!yes_no_dialog.getResult())
                return;
            }

            qualifier_text_area.setText(buffer.toString());
            temp_file.delete();
            sequence_temp_file.delete();

            return;
          }
          catch(IOException e) 
          {
            new MessageDialog(FeatureEdit.this, "an error occured while " +
                              "reading from the editor: " + e);
          }
        }
      };

      process_watcher.addProcessWatcherListener(listener);
    }
    catch(IOException e) 
    {
      new MessageDialog(this, "error while starting editor: " + e);
    } 
    catch(ExternalProgramException e) 
    {
      new MessageDialog(this, "error while starting editor: " + e);
    }
  }

  /**
   *  Read the qualifiers from the feature and update the qualifier JTextArea.
   **/
  private void updateQualifiers() 
  {
    qualifier_text_area.setText(getQualifierString());
  }

  /**
   *  Return a string containing one qualifier per line.  These are the
   *  original qualifiers, not the qualifiers from the qualifier_text_area.
   **/
  private String getQualifierString() 
  {
    final StringBuffer buffer = new StringBuffer();

    final QualifierVector qualifiers = getFeature().getQualifiers();

    for(int qualifier_index = 0; qualifier_index < qualifiers.size();
        ++qualifier_index) 
    {
      final Qualifier this_qualifier = qualifiers.elementAt(qualifier_index);

      final QualifierInfo qualifier_info =
                       getEntryInformation().getQualifierInfo(this_qualifier.getName());

      final StringVector qualifier_strings =
                       StreamQualifier.toStringVector(qualifier_info, this_qualifier);

      for(int value_index = 0; value_index < qualifier_strings.size();
          ++value_index)
      {
        final String qualifier_string = qualifier_strings.elementAt(value_index);
        buffer.append(qualifier_string + "\n");
      }
    }

    return buffer.toString();
  }

  /**
   *  Set the key, location and qualifiers of the feature to be the same as
   *  what values currently shown in the components.
   *  @return true if and only if action succeeds.  It may fail because of an
   *    illegal location or qualifier, in which case a message will be
   *    displayed before returning.
   **/
  private boolean setFeature() 
  {
    final Key key = key_choice.getSelectedItem();

    final KeyVector possible_keys = getEntryInformation().getValidKeys();

    if(possible_keys != null && !possible_keys.contains(key)) 
    {
      final YesNoDialog dialog =
        new YesNoDialog(this, "Add this new key: " + key + "?");

      if(dialog.getResult()) // yes
        getEntryInformation ().addKey (key);
      else 
        return false;
    }

    final Location location;

    try 
    {
      location = new Location(location_text.getText());
    }
    catch(LocationParseException exception) 
    {
      final String error_string = exception.getMessage ();
      System.out.println(error_string);
      new MessageDialog(this,
                        "Cannot apply changes because of location error: " +
                        error_string);

      return false;
    }


    final QualifierVector qualifiers;

    try 
    {
      qualifiers =
        qualifier_text_area.getParsedQualifiers(getEntryInformation ());
    }
    catch(QualifierParseException exception) 
    {
      final String error_string = exception.getMessage();
      System.out.println(error_string);
      new MessageDialog(this,
                        "Cannot apply changes because of a qualifier " +
                        "error: " + error_string);
      return false;
    }

    try 
    {
      entry_group.getActionController().startAction();

      try 
      {
        getFeature().set(datestamp, key, location, qualifiers);
      }
      catch(OutOfDateException e) 
      {
        final YesNoDialog dialog =
          new YesNoDialog(this,
                          "the feature has changed since the edit " +
                          "window was opened, continue?");

        if(dialog.getResult())  // yes - ignore the datestamp
          getFeature().set(key, location, qualifiers);
        else 
          return false;
      }
    } 
    catch(EntryInformationException e) 
    {
      final String error_string = e.getMessage();
      new MessageDialog(this, "Cannot apply changes: " + error_string);

      return false;
    } 
    catch(OutOfRangeException e) 
    {
      new MessageDialog(this,
                        "Cannot apply changes - the location is out of " +
                        "range for this sequence");
      return false;
    } 
    catch(ReadOnlyException e) 
    {
      new MessageDialog(this,
                        "Cannot apply changes - the feature is " +
                        "read only");
      return false;
    } 
    finally
    {
      entry_group.getActionController ().endAction ();
    }

    dribble();

    return true;
  }

  /**
   *  Return the Feature we are editing as passed to the constructor.
   **/
  public Feature getFeature() 
  {
    return edit_feature;
  }

  /**
   *  On Unix machines this method will append the text of the feature to a
   *  file in a current directory called .dribble + <the entry name>
   **/
  private void dribble()
  {
    if(!Options.isUnixHost ()) 
      return;

    final String dribble_file_name;

    if(getEntry().getName() != null) 
      dribble_file_name = ".dribble." + getEntry().getName();
    else 
      dribble_file_name = ".dribble.no_name";

    try  
    {
      final Writer writer = new FileWriter(dribble_file_name, true);
      getFeature().writeNative(writer);
      writer.flush();
      writer.close();
    } 
    catch(IOException e) 
    {
      System.err.println("IO exception while accessing " + dribble_file_name +
                         ": " + e.getMessage());
    }
  }

  /**
   *  Return the Entry that contains the Feature this object is displaying.
   **/
  private Entry getEntry()
  {
    return edit_entry;
  }

  /**
   *  Read the key from the feature and update the key chooser.
   **/
  private void updateKey() 
  {
    final Key feature_key = getFeature().getKey();
    key_choice.setKey(feature_key);
  }

  /**
   *  Return the EntryInformation object of the entry containing the feature.
   **/
  public EntryInformation getEntryInformation() 
  {
    return getEntry().getEntryInformation();
  }

  /**
   *  Return the Selection that was passed to the constructor.
   **/
  private Selection getSelection()
  {
    return selection;
  }

}
