/* AlignmentViewer.java
 *
 * created: Mon Jul 12 1999
 *
 * This file is part of Artemis
 *
 * Copyright (C) 1999,2000,2001  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/AlignmentViewer.java,v 1.2 2004/07/01 11:26:49 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.sequence.*;

import uk.ac.sanger.artemis.util.OutOfRangeException;
import uk.ac.sanger.artemis.util.StringVector;
import uk.ac.sanger.artemis.io.Range;
import uk.ac.sanger.artemis.io.RangeVector;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 *  This component shows an alignment of two sequences using the data from a
 *  ComparisonData object.
 *
 *  @author Kim Rutherford
 *  @version $Id: AlignmentViewer.java,v 1.2 2004/07/01 11:26:49 tjc Exp $
 **/

public class AlignmentViewer extends CanvasPanel
    implements SequenceChangeListener 
{
  /**
   *  Create a new AlignmentViewer for the given entries.
   *  @param subject_feature_display The FeatureDisplay that is above this
   *    component.
   *  @param query_feature_display The FeatureDisplay that is below this
   *    component.
   *  @param comparison_data Provides the AlignMatch objects that will be
   *    displayed.
   **/
  public AlignmentViewer (final FeatureDisplay subject_feature_display,
                          final FeatureDisplay query_feature_display,
                          final ComparisonData comparison_data) 
  {
    this.subject_feature_display = subject_feature_display;
    this.query_feature_display   = query_feature_display;
    this.comparison_data         = comparison_data;
    this.all_matches             = getComparisonData ().getMatches ();

    subject_entry_group          = getSubjectDisplay ().getEntryGroup ();
    query_entry_group            = getQueryDisplay ().getEntryGroup ();

    final Bases subject_bases = getSubjectForwardStrand ().getBases ();
    final Bases query_bases   = getQueryForwardStrand ().getBases ();

    final Selection subject_selection = getSubjectDisplay ().getSelection ();
    final Selection query_selection = getQueryDisplay ().getSelection ();

    final SelectionChangeListener subject_listener =
      new SelectionChangeListener () {
        public void selectionChanged (SelectionChangeEvent event) {
          final RangeVector ranges = subject_selection.getSelectionRanges ();
          selectFromSubjectRanges (ranges);
        }
      };

    final SelectionChangeListener query_listener =
      new SelectionChangeListener () {
        public void selectionChanged (SelectionChangeEvent event) {
          final RangeVector ranges = query_selection.getSelectionRanges ();
          selectFromQueryRanges (ranges);
        }
      };

    makeColours ();

    subject_selection.addSelectionChangeListener (subject_listener);
    query_selection.addSelectionChangeListener (query_listener);

    subject_bases.addSequenceChangeListener (this, 0);
    query_bases.addSequenceChangeListener (this, 0);

    orig_subject_forward_strand = getSubjectForwardStrand ();
    orig_query_forward_strand = getQueryForwardStrand ();

    getCanvas().addMouseListener(new MouseAdapter() 
    {
      public void mousePressed(final MouseEvent event) 
      {
        // on windows we have to check isPopupTrigger in mouseReleased(),
        // but do it in mousePressed() on UNIX
        if(isMenuTrigger(event)) 
          popupMenu(event);
        else 
          handleCanvasMousePress(event);
      }
    });

    getCanvas().addMouseMotionListener(new MouseMotionAdapter() 
    {
      public void mouseDragged(final MouseEvent event) 
      {
        if(isMenuTrigger(event))
          return;

        if (!modifiersForLockToggle(event))
        {
          if (!event.isShiftDown()) 
          {
            selected_matches = null;
            toggleSelection (event.getPoint());
          }
          repaintCanvas();
        }
      }
    });

    scroll_bar = new JScrollBar (Scrollbar.VERTICAL);
    scroll_bar.setValues (1, 1, 1, 1000);
    scroll_bar.setBlockIncrement (10);

    scroll_bar.addAdjustmentListener(new AdjustmentListener() 
    {
      public void adjustmentValueChanged(AdjustmentEvent e) 
      {
        repaintCanvas();
      }
    });

    maximum_score = getComparisonData ().getMaximumScore ();

    add (scroll_bar, "East");
  }

  /**
   *  Returns true if and only if the given MouseEvent should toggle the lock
   *  displays toggle.
   **/
  private boolean modifiersForLockToggle (final MouseEvent event) {
    return (event.getModifiers () & InputEvent.BUTTON2_MASK) != 0 ||
      event.isAltDown ();
  }

  /**
   *  Select those matches that overlap the given range on the subject
   *  sequence.
   **/
  public void selectFromSubjectRanges (final RangeVector select_ranges) {
    if (disable_selection_from_ranges) {
      return;
    }

    selected_matches = null;

    for (int range_index = 0 ;
         range_index < select_ranges.size () ;
         ++range_index) {

      final Range select_range = select_ranges.elementAt (range_index);

      for (int match_index = 0 ;
           match_index < all_matches.length ;
           ++match_index) {
        final AlignMatch this_match = all_matches [match_index];

        if (!isVisible (this_match)) {
          // not visible
          continue;
        }

        final Strand current_subject_forward_strand =
          getSubjectForwardStrand ();

        final int subject_length =
          current_subject_forward_strand.getSequenceLength ();

        int subject_sequence_start =
          getRealSubjectSequenceStart (this_match,
                                       subject_length,
                                       (getOrigSubjectForwardStrand () !=
                                        current_subject_forward_strand));
        int subject_sequence_end =
          getRealSubjectSequenceEnd (this_match,
                                     subject_length,
                                     (getOrigSubjectForwardStrand () !=
                                      current_subject_forward_strand));

        if (subject_sequence_end < subject_sequence_start) {
          final int tmp = subject_sequence_start;
          subject_sequence_start = subject_sequence_end;
          subject_sequence_end = tmp;
        }

        if (select_range.getStart () < subject_sequence_start &&
            select_range.getEnd () < subject_sequence_start) {
          continue;
        }

        if (select_range.getStart () > subject_sequence_end &&
            select_range.getEnd () > subject_sequence_end) {
          continue;
        }

        if (selected_matches == null) {
          selected_matches = new AlignMatchVector ();
        }

        if (!selected_matches.contains (this_match)) {
          selected_matches.add (this_match);
        }
      }
    }

    selectionChanged ();
  }

  /**
   *  Select those matches that overlap the given range on the query sequence.
   **/
  public void selectFromQueryRanges (final RangeVector select_ranges) {
    if (disable_selection_from_ranges) {
      return;
    }

    selected_matches = null;

    for (int range_index = 0 ;
         range_index < select_ranges.size () ;
         ++range_index) {

      final Range select_range = select_ranges.elementAt (range_index);

      for (int match_index = 0 ;
           match_index < all_matches.length ;
           ++match_index) {
        final AlignMatch this_match = all_matches [match_index];

        if (!isVisible (this_match)) {
          // not visible
          continue;
        }

        final Strand current_query_forward_strand = getQueryForwardStrand ();

        final int query_length =
          current_query_forward_strand.getSequenceLength ();

        int query_sequence_start =
          getRealQuerySequenceStart (this_match,
                                     query_length,
                                     (getOrigQueryForwardStrand () !=
                                      current_query_forward_strand));
        int query_sequence_end =
          getRealQuerySequenceEnd (this_match,
                                   query_length,
                                   (getOrigQueryForwardStrand () !=
                                    current_query_forward_strand));

        if (query_sequence_end < query_sequence_start) {
          final int tmp = query_sequence_start;
          query_sequence_start = query_sequence_end;
          query_sequence_end = tmp;
        }

        if (select_range.getStart () < query_sequence_start &&
            select_range.getEnd () < query_sequence_start) {
          continue;
        }

        if (select_range.getStart () > query_sequence_end &&
            select_range.getEnd () > query_sequence_end) {
          continue;
        }

        if (selected_matches == null) {
          selected_matches = new AlignMatchVector ();
        }

        if (!selected_matches.contains (this_match)) {
          selected_matches.add (this_match);
        }
      }
    }

    selectionChanged ();
  }

  /**
   *  Select the given match and move it to the top of the display.
   **/
  public void setSelection (final AlignMatch match) {
    selected_matches = new AlignMatchVector ();

    selected_matches.add (match);

    selectionChanged ();
  }

  /**
   *  This method tells this AlignmentViewer component where the subject
   *  sequence is now.
   **/
  public void setSubjectSequencePosition (final DisplayAdjustmentEvent event) {
    last_subject_event = event;
    repaintCanvas ();
  }

  /**
   *  This method tells this AlignmentViewer component where the query
   *  sequence is now.
   **/
  public void setQuerySequencePosition (final DisplayAdjustmentEvent event) {
    last_query_event = event;
    repaintCanvas ();
  }

  /**
   *  Implementation of the SequenceChangeListener interface.  The display is
   *  redrawn if there is an event.
   **/
  public void sequenceChanged (final SequenceChangeEvent event) {
    repaintCanvas ();
  }

  /**
   *  Return true if and only if the given MouseEvent (a mouse press) should
   *  pop up a JPopupMenu.
   **/
  private boolean isMenuTrigger (final MouseEvent event) {
    if (event.isPopupTrigger () ||
        (event.getModifiers () & InputEvent.BUTTON3_MASK) != 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   *  Popup a menu.
   **/
  private void popupMenu (final MouseEvent event) {
    final JPopupMenu popup = new JPopupMenu ();

    final JMenuItem alignmatch_list_item =
      new JMenuItem ("View Selected Matches");

    popup.add (alignmatch_list_item);

    alignmatch_list_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        if (selected_matches == null) {
          new MessageFrame ("No matches selected").setVisible (true);
        } else {
          final AlignMatchVector matches =
            (AlignMatchVector) selected_matches.clone ();

          final AlignMatchViewer viewer =
            new AlignMatchViewer (AlignmentViewer.this, matches);

          viewer.setVisible (true);
        }
      }
    });

    final JMenuItem flip_subject_item =
      new JMenuItem ("Flip Subject Sequence");

    popup.add (flip_subject_item);

    flip_subject_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        if (getSubjectDisplay ().isRevCompDisplay ()) {
          getSubjectDisplay ().setRevCompDisplay (false);
        } else {
          getSubjectDisplay ().setRevCompDisplay (true);
        }
      }
    });

    final JMenuItem flip_query_item =
      new JMenuItem ("Flip Query Sequence");

    popup.add (flip_query_item);

    flip_query_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        if (getQueryDisplay ().isRevCompDisplay ()) {
          getQueryDisplay ().setRevCompDisplay (false);
        } else {
          getQueryDisplay ().setRevCompDisplay (true);
        }
      }
    });

    final JMenuItem lock_item = new JMenuItem ("Lock Sequences");

    popup.add (lock_item);

    lock_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        lockDisplays ();
      }
    });

    final JMenuItem unlock_item = new JMenuItem ("Unlock Sequences");

    popup.add (unlock_item);

    unlock_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        unlockDisplays ();
      }
    });

    final JMenuItem cutoffs_item = new JMenuItem ("Set Score Cutoffs ...");

    popup.add (cutoffs_item);

    cutoffs_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        final ScoreChangeListener minimum_listener =
          new ScoreChangeListener () {
            public void scoreChanged (final ScoreChangeEvent event) {
              minimum_score = event.getValue ();
              repaintCanvas ();
            }
          };

        final ScoreChangeListener maximum_listener =
          new ScoreChangeListener () {
            public void scoreChanged (final ScoreChangeEvent event) {
              maximum_score = event.getValue ();
              repaintCanvas ();
            }
          };

        final ScoreChanger score_changer =
          new ScoreChanger ("Score Cutoffs",
                            minimum_listener, maximum_listener,
                            getComparisonData ().getMinimumScore (),
                            getComparisonData ().getMaximumScore ());

        score_changer.setVisible (true);
      }
    });

    final JMenuItem percent_id_cutoffs_item =
      new JMenuItem ("Set Percent ID Cutoffs ...");

    popup.add (percent_id_cutoffs_item);

    percent_id_cutoffs_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        final ScoreChangeListener minimum_listener =
          new ScoreChangeListener () {
            public void scoreChanged (final ScoreChangeEvent event) {
              minimum_percent_id = event.getValue ();
              repaintCanvas ();
            }
          };

        final ScoreChangeListener maximum_listener =
          new ScoreChangeListener () {
            public void scoreChanged (final ScoreChangeEvent event) {
              maximum_percent_id = event.getValue ();
              repaintCanvas ();
            }
          };

        final ScoreChanger score_changer =
          new ScoreChanger ("Percent Identity Cutoffs",
                            minimum_listener, maximum_listener,
                            0, 100);

        score_changer.setVisible (true);
      }
    });

    popup.addSeparator ();

    final JCheckBoxMenuItem offer_to_flip_item =
      new JCheckBoxMenuItem ("Offer To RevComp", offer_to_flip_flag);

    offer_to_flip_item.addItemListener (new ItemListener () {
      public void itemStateChanged (ItemEvent e) {
        offer_to_flip_flag = !offer_to_flip_flag;
      }
    });

    popup.add (offer_to_flip_item);

    final JCheckBoxMenuItem ignore_self_match_item =
      new JCheckBoxMenuItem ("Ignore Self Matches");

    ignore_self_match_item.addItemListener (new ItemListener () {
      public void itemStateChanged (ItemEvent event) {
        ignore_self_match_flag = ignore_self_match_item.getState ();
        repaintCanvas ();
      }
    });

    ignore_self_match_item.setState (ignore_self_match_flag);

    popup.add (ignore_self_match_item);

    getCanvas ().add (popup);
    popup.show (getCanvas (), event.getX (), event.getY ());
  }


  /**
   *  Handle a mouse press event on the drawing canvas - select on click,
   *  select and broadcast it on double click.
   **/
  private void handleCanvasMousePress (final MouseEvent event) {
    if (event.getID() != MouseEvent.MOUSE_PRESSED) {
      return;
    }

    if (event.getClickCount () == 2) {
      handleCanvasDoubleClick (event);
    } else {
      handleCanvasSingleClick (event);
    }

    repaintCanvas ();
  }

  /**
   *  Handle a double click on the canvas.
   **/
  private void handleCanvasDoubleClick (final MouseEvent event) {
    if (selected_matches != null) {
      // there should be only one match in the array because
      // handleCanvasSingleClick () has been called
      alignAt (selected_matches.elementAt (0));
    }
  }

  /**
   *  Send an AlignmentEvent to all the AlignmentListeners.
   *  @param align_match The AlignMatch that we have just centred on.
   **/
  public void alignAt (final AlignMatch align_match) {
    final java.util.Vector targets;
    // copied from a book - synchronizing the whole method might cause a
    // deadlock
    synchronized (this) {
      targets = (java.util.Vector) alignment_event_listeners.clone ();
    }

    for (int i = 0 ; i < targets.size () ; ++i) {
      final AlignmentListener listener =
        (AlignmentListener) targets.elementAt (i);

      listener.alignMatchChosen (new AlignmentEvent (align_match));
    }
  }

  /**
   *  Handle a single click on the canvas.
   **/
  private void handleCanvasSingleClick (final MouseEvent event) {
    if (modifiersForLockToggle (event)) {
      toggleDisplayLock ();
    } else {
      if (!event.isShiftDown ()) {
        selected_matches = null;
      }
      toggleSelection (event.getPoint ());
    }
  }

  /**
   *  Add or remove the match at the given mouse position to the selection.
   **/
  private void toggleSelection (final Point point) {
    final AlignMatch clicked_align_match =
      getAlignMatchFromPosition (point);

    if (clicked_align_match != null) {
      if (selected_matches == null) {
        selected_matches = new AlignMatchVector ();
        selected_matches.add (clicked_align_match);
      } else {
        if (selected_matches.contains (clicked_align_match)) {
          selected_matches.remove (clicked_align_match);
          if (selected_matches.size () == 0) {
            selected_matches = null;
          }
        } else {
          selected_matches.add (clicked_align_match);
        }
      }

    }

    selectionChanged ();
  }

  /**
   *  Return the AlignMatch at the given Point on screen or null if there is
   *  no match at that point.  The alignment_data_array is searched in reverse
   *  order.
   **/
  private AlignMatch getAlignMatchFromPosition (final Point click_point) {
    final int canvas_height = getCanvas ().getSize ().height;
    final int canvas_width = getCanvas ().getSize ().width;

    for (int i = all_matches.length - 1 ; i >= 0  ; --i) {
      final AlignMatch this_match = all_matches [i];

      final int [] match_x_positions =
        getMatchCoords (canvas_width, this_match);

      if (match_x_positions == null) {
        continue;
      }

      if (!isVisible (this_match)) {
        continue;
      }

      final int subject_start_x = match_x_positions[0];
      final int subject_end_x = match_x_positions[1];
      final int query_start_x = match_x_positions[2];
      final int query_end_x = match_x_positions[3];

      // this is the x coordinate of the point where the line y = click_point
      // hits the left edge of the match box
      final double match_left_x =
        subject_start_x +
        (1.0 * (query_start_x - subject_start_x)) *
        (1.0 * click_point.y / canvas_height);

      // this is the x coordinate of the point where the line y = click_point
      // hits the right edge of the match box
      final double match_right_x =
        subject_end_x +
        (1.0 * (query_end_x - subject_end_x)) *
        (1.0 * click_point.y / canvas_height);

      if (click_point.x >= match_left_x - 1 &&
          click_point.x <= match_right_x + 1 ||
          click_point.x <= match_left_x + 1 &&
          click_point.x >= match_right_x - 1) {
        return this_match;
      }
    }

    return null;
  }

  /**
   *  This method is called by setSelection () and others whenever the list of
   *  selected/highlighted hits changes.  Calls alignmentSelectionChanged ()
   *  on all interested AlignmentSelectionChangeListener objects, moves the
   *  selected matches to the top of the display and then calls
   *  repaintCanvas ().
   **/
  private void selectionChanged () {
    for (int i = 0 ; i < selection_change_listeners.size () ; ++i) {
      final AlignMatchVector matches =
        (AlignMatchVector) selected_matches.clone ();

      final AlignmentSelectionChangeEvent ev =
        new AlignmentSelectionChangeEvent (this, matches);

      final AlignmentSelectionChangeListener listener =
        (AlignmentSelectionChangeListener) selection_change_listeners.elementAt (i);

      listener.alignmentSelectionChanged (ev);
    }

    if (selected_matches != null && selected_matches.size () > 0) {
      // a count of the number of selected matches seen so far
      int seen_and_selected_count = 0;

      for (int i = 0 ; i < all_matches.length ; ++i) {
        final AlignMatch this_match = all_matches[i];

        if (selected_matches.contains (this_match)) {
          ++seen_and_selected_count;
        } else {
          if (seen_and_selected_count > 0) {
            // move the matches down to fill the gap
            all_matches[i-seen_and_selected_count] = all_matches[i];
          }
        }
      }

      // put the selected_matches at the end of all_matches
      for (int i = 0 ; i < selected_matches.size () ; ++i) {
        all_matches[all_matches.length - selected_matches.size () + i] =
          selected_matches.elementAt (i);
      }
    }

    repaintCanvas ();
  }

  /**
   *  Add the AlignmentSelectionChangeListener to the list of objects
   *  listening for AlignmentSelectionChangeEvents.
   **/
  public void
    addAlignmentSelectionChangeListener (final AlignmentSelectionChangeListener l) {
    selection_change_listeners.addElement (l);
  }

  /**
   *  Remove the AlignmentSelectionChangeListener from the list of objects
   *  listening for AlignmentSelectionChangeEvents.
   **/
  public void
    removeAlignmentSelectionChangeListener (final AlignmentSelectionChangeListener l) {
    selection_change_listeners.removeElement (l);
  }

  /**
   *  Adds the specified AlignmentEvent listener to receive events from this
   *  object.
   *  @param l the listener.
   **/
  public void addAlignmentListener (AlignmentListener l) {
    alignment_event_listeners.addElement (l);
  }

  /**
   *  Removes the specified AlignmentEvent listener so that it no longer
   *  receives events from this object.
   *  @param l the listener.
   **/
  public void removeAlignmentListener (AlignmentListener l) {
    alignment_event_listeners.removeElement (l);
  }

  /**
   *  Returns true if and only if we should offer to flip the query
   *  sequence when the user double clicks on a flipped match.
   **/
  public boolean offerToFlip () {
    return offer_to_flip_flag;
  }

  /**
   *  The main paint function for the canvas.  An off screen image used for
   *  double buffering when drawing the canvas.
   *  @param g The Graphics object of the canvas.
   **/
  protected void paintCanvas (final Graphics g) {
    fillBackground (g);

    if (last_subject_event != null && last_query_event != null) {
      drawAlignments (g);
      drawLabels (g);
    }
  }

  /**
   *  Draw the background colour of the frames.
   **/
  private void fillBackground (final Graphics g) {
    g.setColor (Color.white);

    g.fillRect (0, 0, getCanvas ().getSize ().width,
                getCanvas ().getSize ().height);
  }

  /**
   *  Draw the labels into the given Graphics object.  There is a label at the
   *  top left showing info about the current AlignMatch object,
   *
   *  XXX
   *  a label at
   *  the bottom left showing whether or not the query sequence is reverse
   *  complemented
   *  XXX
   *
   *  and a label beside the Scrollbar showing the current score
   *  cutoff.
   **/
  private void drawLabels (final Graphics g) {
    final FontMetrics fm = g.getFontMetrics ();
    final int canvas_width = getCanvas ().getSize ().width;
    final int canvas_height = getCanvas ().getSize ().height;

    final String cutoff_label =
      Integer.toString (scroll_bar.getValue ());

    final int cutoff_label_width = fm.stringWidth (cutoff_label);

    int cutoff_label_position =
      (int)((scroll_bar.getValue () -
             scroll_bar.getMinimum ()) / (1.0 *
            (scroll_bar.getMaximum () -
             scroll_bar.getMinimum ())) * canvas_height);

    if (cutoff_label_position < getFontAscent ()) {
      cutoff_label_position = getFontAscent ();
    }


    final int [] cutoff_x_points = {
      canvas_width - cutoff_label_width,
      canvas_width - 2,
      canvas_width - 2,
      canvas_width - cutoff_label_width,
    };

    final int [] cutoff_y_points = {
      cutoff_label_position + 1,
      cutoff_label_position + 1,
      cutoff_label_position - getFontAscent (),
      cutoff_label_position - getFontAscent (),
    };

    g.setColor (Color.white);
    g.fillPolygon (cutoff_x_points, cutoff_y_points, 4);

    g.setColor (Color.black);

    g.drawString (cutoff_label, canvas_width - cutoff_label_width,
                  cutoff_label_position);

    final int font_height = getFontAscent () + getFontDescent ();

    if (selected_matches != null) {
      final String match_string_1;

      if (selected_matches.size () > 1) {
        match_string_1 = selected_matches.size () + " matches selected";
      } else {
        final AlignMatch selected_align_match = selected_matches.elementAt (0);

        match_string_1 =
          selected_align_match.getQuerySequenceStart () + ".." +
          selected_align_match.getQuerySequenceEnd () + " -> " +
          selected_align_match.getSubjectSequenceStart () + ".." +
          selected_align_match.getSubjectSequenceEnd ();
      }

      final int match_string_1_width = fm.stringWidth (match_string_1);

      final int [] match_1_x_points = {
        0, 0, match_string_1_width, match_string_1_width
      };

      final int [] match_1_y_points = {
        0, font_height, font_height, 0
      };

      g.setColor (Color.white);
      g.fillPolygon (match_1_x_points, match_1_y_points, 4);

      g.setColor (Color.black);
      g.drawString (match_string_1, 0, getFontAscent ());

      if (selected_matches.size () == 1) {
        final AlignMatch selected_align_match = selected_matches.elementAt (0);

        final String match_string_2 = "score: " +
          selected_align_match.getScore () + "  percent id: " +
          selected_align_match.getPercentID () + "%";
        
        final int match_string_2_width = fm.stringWidth (match_string_2);
        
        final int [] match_2_x_points = {
          0, 0, match_string_2_width, match_string_2_width
        };
        
        final int [] match_2_y_points = {
          font_height, font_height * 2, font_height * 2, font_height
        };
        
        g.setColor (Color.white);
        g.fillPolygon (match_2_x_points, match_2_y_points, 4);
        
        g.setColor (Color.black);
        g.drawString (match_string_2, 0, getFontAscent () + font_height);
      }
    }

    final StringVector status_strings = new StringVector ();

    if (displaysAreLocked ()) {
      status_strings.add ("LOCKED");
    }

    if (getSubjectDisplay ().isRevCompDisplay ()) {
      status_strings.add ("Subject: Flipped");
    }

    if (getQueryDisplay ().isRevCompDisplay ()) {
      status_strings.add ("Query: Flipped");
    }

    if (getOrigSubjectForwardStrand () != getSubjectForwardStrand ()) {
      status_strings.add ("Subject: Reverse Complemented");
    }

    if (getOrigQueryForwardStrand () != getQueryForwardStrand ()) {
      status_strings.add ("Query: Reverse Complemented");
    }

    g.setColor (Color.white);

    for (int i = 0 ; i < status_strings.size () ; ++i) {
      final String status_string = status_strings.elementAt (i);

      final int status_string_width = fm.stringWidth (status_string);

      final int [] x_points = {
        0, 0, status_string_width, status_string_width
      };

      final int string_offset = font_height * (status_strings.size () - i - 1);

      final int [] y_points = {
        canvas_height - string_offset,
        canvas_height - font_height - string_offset,
        canvas_height - font_height - string_offset,
        canvas_height - string_offset
      };

      g.fillPolygon (x_points, y_points, 4);
    }

    g.setColor (Color.black);

    for (int i = 0 ; i < status_strings.size () ; ++i) {
      final String status_string = status_strings.elementAt (i);

      final int string_offset = font_height * (status_strings.size () - i - 1);

      g.drawString (status_string, 0,
                    canvas_height - string_offset - getFontDescent ());
    }
  }

  /**
   *  Draw the alignments into the given Graphics object.
   **/
  private void drawAlignments (final Graphics g) {
    final int canvas_height = getCanvas ().getSize ().height;
    final int canvas_width = getCanvas ().getSize ().width;

    for (int i = 0 ; i < all_matches.length ; ++i) {
      final AlignMatch this_match = all_matches [i];

      final int [] match_x_positions =
        getMatchCoords (canvas_width, this_match);

      if (match_x_positions == null) {
        continue;
      }

      if (!isVisible (this_match)) {
        // not visible
        continue;
      }

      final int subject_start_x = match_x_positions[0];
      final int subject_end_x = match_x_positions[1];
      final int query_start_x = match_x_positions[2];
      final int query_end_x = match_x_positions[3];

      final int [] x_coords = new int [4];
      final int [] y_coords = new int [4];

      x_coords[0] = subject_start_x;
      y_coords[0] = 0;
      x_coords[1] = query_start_x;
      y_coords[1] = canvas_height;
      x_coords[2] = query_end_x;
      y_coords[2] = canvas_height;
      x_coords[3] = subject_end_x;
      y_coords[3] = 0;

      final boolean highlight_this_match;

      if (selected_matches != null &&
          selected_matches.contains (this_match)) {
        highlight_this_match = true;
      } else {
        highlight_this_match = false;
      }

      final int OFFSCREEN = 3000;

      final int percent_id = this_match.getPercentID ();

      if (highlight_this_match) {
        g.setColor (Color.yellow);
      } else {
        if (percent_id == -1) {
          if (this_match.isRevMatch ()) {
            g.setColor (Color.blue);
          } else {
            g.setColor (Color.red);
          }
        } else {
          int colour_index = red_percent_id_colours.length - 1;

          if (maximum_percent_id > minimum_percent_id) {
            colour_index =
              (int)(red_percent_id_colours.length * 0.999 *
                    (percent_id - minimum_percent_id) /
                    (maximum_percent_id - minimum_percent_id));
          }

          if (this_match.isRevMatch ()) {
            g.setColor (blue_percent_id_colours[colour_index]);
          } else {
            g.setColor (red_percent_id_colours[colour_index]);
          }
        }
      }

      g.fillPolygon (x_coords, y_coords, x_coords.length);

      if (subject_end_x - subject_start_x < 5 &&
          subject_end_x - subject_start_x > -5 ||
          subject_start_x < -OFFSCREEN ||
          subject_end_x > OFFSCREEN ||
          query_start_x < -OFFSCREEN ||
          query_end_x > OFFSCREEN) {

        // match is (probably) narrow so draw the border to the same colour as
        // the polygon
      } else {

        // draw a black outline the make the match stand out
        g.setColor (Color.black);
      }

      g.drawLine (subject_start_x, 0, query_start_x, canvas_height);
      g.drawLine (subject_end_x, 0, query_end_x, canvas_height);
    }
  }

  /**
   *  Return true if and only if the given match is currently visible.
   **/
  private boolean isVisible (final AlignMatch match) {
    if (ignore_self_match_flag && match.isSelfMatch ()) {
      return false;
    }

    final int score = match.getScore ();
    final int percent_id = match.getPercentID ();

    if (score > -1) {
      if (score < minimum_score || score > maximum_score) {
        return false;
      }
    }

    if (percent_id > -1) {
      if (percent_id < minimum_percent_id || percent_id > maximum_percent_id) {
        return false;
      }
    }

    final int match_length =
      Math.abs (match.getSubjectSequenceStart () -
                match.getSubjectSequenceEnd ());

    if (match_length < scroll_bar.getValue ()) {
      return false;
    }

    return true;
  }

  /**
   *  Return the start position in the subject sequence of the given
   *  AlignMatch, taking into account the current orientation of the
   *  sequences. If the reverse_position argument is true reverse the
   *  complemented match coordinates will be returned.
   **/
  static int getRealSubjectSequenceStart (final AlignMatch match,
                                          final int sequence_length,
                                          final boolean reverse_position) {
    if (reverse_position) {
      return sequence_length - match.getSubjectSequenceStart () + 1;
    } else {
      return match.getSubjectSequenceStart ();
    }
  }

  /**
   *  Return the end position in the subject sequence of the given AlignMatch,
   *  taking into account the current orientation of the sequences.  If the
   *  reverse_position argument is true reverse the complemented match
   *  coordinates will be returned.
   **/
  static int getRealSubjectSequenceEnd (final AlignMatch match,
                                        final int sequence_length,
                                        final boolean reverse_position) {
    if (reverse_position) {
      return sequence_length - match.getSubjectSequenceEnd () + 1;
    } else {
      return match.getSubjectSequenceEnd ();
    }
  }

  /**
   *  Return the start position in the query sequence of the given AlignMatch,
   *  taking into account the current orientation of the sequences.    If the
   *  reverse_position argument is true reverse the complemented match
   *  coordinates will be returned.
   **/
  static int getRealQuerySequenceStart (final AlignMatch match,
                                        final int sequence_length,
                                        final boolean reverse_position) {
    if (reverse_position) {
      return sequence_length - match.getQuerySequenceStart () + 1;
    } else {
      return match.getQuerySequenceStart ();
    }
  }

  /**
   *  Return the end position in the query sequence of the given AlignMatch,
   *  taking into account the current orientation of the sequences.  If the
   *  reverse_position argument is true reverse the complemented match
   *  coordinates will be returned.
   **/
  static int getRealQuerySequenceEnd (final AlignMatch match,
                                      final int sequence_length,
                                      final boolean reverse_position) {
    if (reverse_position) {
      return sequence_length - match.getQuerySequenceEnd () + 1;
    } else {
      return match.getQuerySequenceEnd ();
    }
  }

  /**
   *  Return the screen x positions of the corners of the given match.  The
   *  order is Top Left, Top Right, Bottom Left, Bottom Right, unless the
   *  match is an inversion, in which case it will be TL,TR,BR,BL.  Returns
   *  null if and only if the match is not currently visible.
   **/
  private int [] getMatchCoords (final int canvas_width,
                                 final AlignMatch this_match) {
    final int subject_length = getSubjectForwardStrand ().getSequenceLength ();
    final int query_length = getQueryForwardStrand ().getSequenceLength ();

    int subject_sequence_start =
      getRealSubjectSequenceStart (this_match,
                                   subject_length,
                                   subjectIsRevComp ());
    int subject_sequence_end =
      getRealSubjectSequenceEnd (this_match,
                                 subject_length,
                                 subjectIsRevComp ());
    int query_sequence_start =
      getRealQuerySequenceStart (this_match,
                                 query_length,
                                 queryIsRevComp ());
    int query_sequence_end =
      getRealQuerySequenceEnd (this_match,
                               query_length,
                               queryIsRevComp ());

    // add one base because we want to draw to the end of the base
    if (subjectIsRevComp ()) {
      subject_sequence_start += 1;
    } else {
      subject_sequence_end += 1;
    }

    if (this_match.isRevMatch () && !queryIsRevComp () ||
        !this_match.isRevMatch () && queryIsRevComp ()) {
      query_sequence_start += 1;
    } else {
      query_sequence_end += 1;
    }

    final int subject_start_x =
      getScreenPosition (canvas_width, last_subject_event,
                         subject_sequence_start);
    final int subject_end_x =
      getScreenPosition (canvas_width, last_subject_event,
                         subject_sequence_end);

    final int query_start_x =
      getScreenPosition (canvas_width, last_query_event,
                         query_sequence_start);
    final int query_end_x =
      getScreenPosition (canvas_width, last_query_event,
                         query_sequence_end);

    boolean subject_off_left = false;
    boolean subject_off_right = false;
    boolean query_off_left = false;
    boolean query_off_right = false;

    if (subject_start_x < 0 && subject_end_x < 0) {
      subject_off_left = true;
    }

    if (subject_start_x >= canvas_width && subject_end_x >= canvas_width) {
      subject_off_right = true;
    }

    if (query_start_x < 0 && query_end_x < 0) {
      query_off_left = true;
    }

    if (query_start_x >= canvas_width && query_end_x >= canvas_width) {
      query_off_right = true;
    }

    if ((subject_off_left ? 1 : 0) +
        (query_off_left ? 1 : 0) +
        (subject_off_right ? 1 : 0) +
        (query_off_right ? 1 : 0) == 2) {
      return null;
    } else {
      final int [] return_values = new int [4];

      return_values[0] = subject_start_x;
      return_values[1] = subject_end_x;
      return_values[2] = query_start_x;
      return_values[3] = query_end_x;

      return return_values;
    }
  }

  /**
   *  Return the current forward Strand of the subject EntryGroup.
   **/
  private Strand getSubjectForwardStrand () {
    return getSubjectEntryGroup ().getBases ().getForwardStrand ();
  }

  /**
   *  Return the current forward Strand of the query EntryGroup.
   **/
  private Strand getQueryForwardStrand () {
    return getQueryEntryGroup ().getBases ().getForwardStrand ();
  }

  /**
   *  Return the current reverse Strand of the subject EntryGroup.
   **/
  private Strand getSubjectReverseStrand () {
    return getSubjectEntryGroup ().getBases ().getReverseStrand ();
  }

  /**
   *  Return the current reverse Strand of the query EntryGroup.
   **/
  private Strand getQueryReverseStrand () {
    return getQueryEntryGroup ().getBases ().getReverseStrand ();
  }

  /**
   *  Return the subject EntryGroup that was passed to the constructor.
   **/
  private EntryGroup getSubjectEntryGroup () {
    return subject_entry_group;
  }

  /**
   *  Return the subject EntryGroup that was passed to the constructor.
   **/
  private EntryGroup getQueryEntryGroup () {
    return query_entry_group;
  }

  /**
   *  Return the reference of the subject FeatureDisplay.
   **/
  public FeatureDisplay getSubjectDisplay () {
    return subject_feature_display;
  }

  /**
   *  Return the reference of the query FeatureDisplay.
   **/
  public FeatureDisplay getQueryDisplay () {
    return query_feature_display;
  }

  /**
   *  Returns true if and only if the subject sequence has been flipped since
   *  this object was created.
   **/
  boolean subjectIsRevComp () {
    final Strand current_subject_forward_strand = getSubjectForwardStrand ();

    if (getOrigSubjectForwardStrand () == current_subject_forward_strand ^
        getSubjectDisplay ().isRevCompDisplay ()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   *  Returns true if and only if the query sequence has been flipped since
   *  this object was created.
   **/
  boolean queryIsRevComp () {
    final Strand current_query_forward_strand = getQueryForwardStrand ();

    if (getOrigQueryForwardStrand () == current_query_forward_strand ^
        getQueryDisplay ().isRevCompDisplay ()) {
      return false;
    } else {
      return true;
    }
  }

  /**
   *  Return the forward Strand of the subject EntryGroup from when the
   *  Comparator was created.
   **/
  public Strand getOrigSubjectForwardStrand () {
    return orig_subject_forward_strand;
  }

  /**
   *  Return the forward Strand of the query EntryGroup from when the
   *  Comparator was created.
   **/
  public Strand getOrigQueryForwardStrand () {
    return orig_query_forward_strand;
  }

  /**
   *  Return the reverse Strand of the subject EntryGroup from when the
   *  Comparator was created.
   **/
  public Strand getOrigSubjectReverseStrand () {
    return orig_subject_reverse_strand;
  }

  /**
   *  Return the reverse Strand of the query EntryGroup from when the
   *  Comparator was created.
   **/
  public Strand getOrigQueryReverseStrand () {
    return orig_query_reverse_strand;
  }

  /**
   *  Arrange for the two FeatureDisplay objects to scroll in parallel.
   **/
  public void lockDisplays () {
    displays_are_locked = true;
    repaintCanvas ();
  }

  /**
   *  Arrange for the two FeatureDisplay objects to scroll independently.
   **/
  public void unlockDisplays () {
    displays_are_locked = false;
    repaintCanvas ();
  }

  /**
   *  Return true if and only if the displays are currently locked.
   **/
  public boolean displaysAreLocked () {
    return displays_are_locked;
  }

  /**
   *  Toggle whether the two displays are locked.
   **/
  public void toggleDisplayLock () {
    displays_are_locked = !displays_are_locked;
    repaintCanvas ();
  }

  /**
   *  Calling this method will temporarily disable selectFromQueryRange() and
   *  selectFromSubjectRange() until enableSelection().  This is need to allow
   *  the selections of the top and bottom FeatureDisplays to be set without
   *  changing which AlignMatches are selected.
   **/
  public void disableSelection () {
    disable_selection_from_ranges = true;
  }

  /**
   *  Enable selectFromQueryRange() and selectFromSubjectRange().
   **/
  public void enableSelection () {
    disable_selection_from_ranges = false;
  }

  /**
   *  Convert a base position into a screen x coordinate.
   **/
  private int getScreenPosition (final int canvas_width,
                                 final DisplayAdjustmentEvent event,
                                 final int base_position) {
    // this is the base that is at the left of the screen
    final int screen_start_base = event.getStart ();

    final double base_pos_float =
      event.getBaseWidth () * (base_position - screen_start_base);

    if (base_pos_float > 30000) {
      return 30000;
    } else {
      if (base_pos_float < -30000) {
        return -30000;
      } else {
        return (int) base_pos_float;
      }
    }
  }

  /**
   *  Return an array of colours that will be used for colouring the matches
   *  (depending on score).
   **/
  private void makeColours () {
    red_percent_id_colours = new Color [NUMBER_OF_SHADES];
    blue_percent_id_colours = new Color [NUMBER_OF_SHADES];

    for (int i = 0 ; i < blue_percent_id_colours.length ; ++i) {
      final int shade_value = 255 - (int) (256 * 1.0 * i / NUMBER_OF_SHADES);
      red_percent_id_colours[i] = new Color (255, shade_value, shade_value);
      blue_percent_id_colours[i] = new Color (shade_value, shade_value, 255);
    }
  }

  /**
   *  Return the ComparisonData object that was passed to the constructor.
   **/
  private ComparisonData getComparisonData () {
    return comparison_data;
  }

  /**
   *  The comparison data that will be displayed in this component.
   **/
  final private ComparisonData comparison_data;

  /**
   *  All the AlignMatch objects from comparison_data (possibly in a
   *  different order.
   **/
  private AlignMatch [] all_matches = null;

  /**
   *  This is the last DisplayAdjustmentEvent reference that was passed to
   *  setSubjectSeqeuencePosition ().
   **/
  private DisplayAdjustmentEvent last_subject_event;

  /**
   *  This is the last DisplayAdjustmentEvent reference that was passed to
   *  setQuerySeqeuencePosition ().
   **/
  private DisplayAdjustmentEvent last_query_event;

  /**
   *  The FeatureDisplay that is above this component.  (From the constructor).
   **/
  private FeatureDisplay subject_feature_display;

  /**
   *  The FeatureDisplay that is below this component.  (From the constructor).
   **/
  private FeatureDisplay query_feature_display;

  /**
   *  Set by the constructor to be the original forward strand for the subject
   *  sequence.  This is use to determine whether to subject sequence has been
   *  reverse-complemented or not.
   **/
  private Strand orig_subject_forward_strand;

  /**
   *  Set by the constructor to be the original forward strand for the query
   *  sequence.  This is use to determine whether to query sequence has been
   *  reverse-complemented or not.
   **/
  private Strand orig_query_forward_strand;

  /**
   *  Set by the constructor to be the original reverse strand for the subject
   *  sequence.
   **/
  private Strand orig_subject_reverse_strand;

  /**
   *  Set by the constructor to be the original reverse strand for the query
   *  sequence.
   **/
  private Strand orig_query_reverse_strand;

  /**
   *  One of the two Entry objects that we are comparing.
   **/
  final private EntryGroup subject_entry_group;

  /**
   *  One of the two Entry objects that we are comparing.
   **/
  final private EntryGroup query_entry_group;

  /**
   *  The selected matches.  null means no matches are selected.
   **/
  private AlignMatchVector selected_matches = null;

  /**
   *  The objects that are listening for AlignmentSelectionChangeEvents.
   **/
  private java.util.Vector selection_change_listeners =
    new java.util.Vector ();

  /**
   *  The number of shades of red and blue to use for percentage ID colouring.
   **/
  private static int NUMBER_OF_SHADES = 13;

  /**
   *  The Reds used to display the percent identity of matches.
   **/
  private Color [] red_percent_id_colours;

  /**
   *  The Blues used to display the percent identity of matches.
   **/
  private Color [] blue_percent_id_colours;

  /**
   *  The scroll bar used to set the minimum length of the visible matches.
   **/
  private JScrollBar scroll_bar = null;

  /**
   *  Matches with scores below this value will not be shown.
   **/
  private int minimum_score = 0;

  /**
   *  Matches with scores above this value will not be shown.
   **/
  private int maximum_score = 99999999;

  /**
   *  Matches with percent identity values below this number will not be shown.
   **/
  private int minimum_percent_id = 0;

  /**
   *  Matches with percent identity values above this number will not be shown.
   **/
  private int maximum_percent_id = 100;

  /**
   *  True if we should offer to flip the query sequence when the user
   *  double clicks on a flipped match.
   **/
  private boolean offer_to_flip_flag = false;

  /**
   *  If true ignore self matches (ie query start == subject start && query
   *  end == subject end)
   **/
  private boolean ignore_self_match_flag = false;

  /**
   *  A Vector of those objects that are listening for AlignmentEvents
   **/
  private java.util.Vector alignment_event_listeners =
    new java.util.Vector ();

  /**
   *  If true then the FeatureDisplays above and below this AlignmentViewer
   *  should scroll together.
   **/
  private boolean displays_are_locked = true;

  /**
   *  Setting this to true will temporarily disable selectFromQueryRange() and
   *  selectFromSubjectRange() until enableSelection().  This is need to allow
   *  the selections of the top and bottom FeatureDisplays to be set without
   *  changing which AlignMatches are selected.
   **/
  private boolean disable_selection_from_ranges = false;
}
