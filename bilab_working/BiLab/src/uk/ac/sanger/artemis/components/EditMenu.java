/* EditMenu.java
 *
 * created: Thu Dec  3 1998
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/EditMenu.java,v 1.1 2004/06/09 09:46:18 tjc Exp $
 **/

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.sequence.*;
import uk.ac.sanger.artemis.util.*;
import uk.ac.sanger.artemis.io.Range;
import uk.ac.sanger.artemis.io.RangeVector;
import uk.ac.sanger.artemis.io.Key;
import uk.ac.sanger.artemis.io.Location;
import uk.ac.sanger.artemis.io.Qualifier;
import uk.ac.sanger.artemis.io.QualifierInfo;
import uk.ac.sanger.artemis.io.QualifierInfoVector;
import uk.ac.sanger.artemis.io.QualifierVector;
import uk.ac.sanger.artemis.io.QualifierParseException;
import uk.ac.sanger.artemis.io.InvalidQualifierException;
import uk.ac.sanger.artemis.io.InvalidRelationException;
import uk.ac.sanger.artemis.io.EntryInformation;
import uk.ac.sanger.artemis.io.EntryInformationException;
import uk.ac.sanger.artemis.io.OutOfDateException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;

/**
 *  A menu with editing commands.
 *
 *  @author Kim Rutherford
 *  @version $Id: EditMenu.java,v 1.1 2004/06/09 09:46:18 tjc Exp $
 **/

public class EditMenu extends SelectionMenu
    implements EntryGroupChangeListener, EntryChangeListener {
  /**
   *  Create a new EditMenu object.
   *  @param frame The JFrame that owns this JMenu.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   *  @param goto_event_source The object the we will call makeBaseVisible ()
   *    on.
   *  @param entry_group The EntryGroup object where new features/entries will
   *    be added.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   *  @param menu_name The name of the new menu.
   **/
  public EditMenu (final JFrame frame,
                   final Selection selection,
                   final GotoEventSource goto_event_source,
                   final EntryGroup entry_group,
                   final BasePlotGroup base_plot_group,
                   final String menu_name) {
    super (frame, menu_name, selection);

    this.entry_group = entry_group;
    this.goto_event_source = goto_event_source;
    this.base_plot_group = base_plot_group;

    getEntryGroup ().addEntryGroupChangeListener (this);
    getEntryGroup ().addEntryChangeListener (this);

    refreshMenu ();
  }

  /**
   *  Create a new EditMenu object and use "Edit" as the menu name.
   *  @param frame The JFrame that owns this JMenu.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   *  @param goto_event_source The object the we will call makeBaseVisible ()
   *    on.
   *  @param entry_group The EntryGroup object where new features/entries will
   *    be added.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public EditMenu (final JFrame frame,
                   final Selection selection,
                   final GotoEventSource goto_event_source,
                   final EntryGroup entry_group,
                   final BasePlotGroup base_plot_group) {
    this (frame, selection, goto_event_source, entry_group,
          base_plot_group, "Edit");
  }

  /**
   *  The shortcut for Edit Selected Features.
   **/
  final static KeyStroke EDIT_FEATURES_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_E, InputEvent.CTRL_MASK);
  final static public int EDIT_FEATURES_KEY_CODE = KeyEvent.VK_E;

  /**
   *  The shortcut for Merge Selected Features.
   **/
  final static KeyStroke MERGE_FEATURES_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_M, InputEvent.CTRL_MASK);
  final static public int MERGE_FEATURES_KEY_CODE = KeyEvent.VK_M;

  /**
   *  The shortcut for Duplicate Selected Features.
   **/
  final static KeyStroke DUPLICATE_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_D, InputEvent.CTRL_MASK);
  final static public int DUPLICATE_KEY_CODE = KeyEvent.VK_D;

  /**
   *  The shortcut for Delete Selected Features.
   **/
  final static KeyStroke DELETE_FEATURES_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_DELETE, InputEvent.CTRL_MASK);
  final static public int DELETE_FEATURES_KEY_CODE = KeyEvent.VK_DELETE;

  /**
   *  The shortcut for Trim Selected Features.
   **/
  final static KeyStroke TRIM_FEATURES_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_T, InputEvent.CTRL_MASK);
  final static public int TRIM_FEATURES_KEY_CODE = KeyEvent.VK_T;

  /**
   *  The shortcut for Trim Selected Features To Next Any.
   **/
  final static KeyStroke TRIM_FEATURES_TO_NEXT_ANY_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_Y, InputEvent.CTRL_MASK);
  final static public int TRIM_FEATURES_TO_NEXT_ANY_KEY_CODE = KeyEvent.VK_Y;

  /**
   *  The shortcut for Extend to Previous Stop Codon.
   **/
  final static public int EXTEND_TO_PREVIOUS_STOP_CODON_KEY_CODE =
    KeyEvent.VK_Q;
  final static KeyStroke EXTEND_TO_PREVIOUS_STOP_CODON_KEY =
    makeMenuKeyStroke (EXTEND_TO_PREVIOUS_STOP_CODON_KEY_CODE);

  /**
   *  The shortcut for Undo.
   **/
  final static public int UNDO_KEY_CODE = KeyEvent.VK_U;
  final static KeyStroke UNDO_KEY =
    KeyStroke.getKeyStroke (UNDO_KEY_CODE, InputEvent.CTRL_MASK);

  /**
   *  Implementation of the EntryGroupChangeListener interface.  We listen to
   *  EntryGroupChange events so that we can update the display if entries
   *  are added or deleted.
   **/
  public void entryGroupChanged (final EntryGroupChangeEvent event) {
    switch (event.getType ()) {
    case EntryGroupChangeEvent.ENTRY_ADDED:
    case EntryGroupChangeEvent.ENTRY_DELETED:
    case EntryGroupChangeEvent.ENTRY_INACTIVE:
    case EntryGroupChangeEvent.ENTRY_ACTIVE:
    case EntryGroupChangeEvent.NEW_DEFAULT_ENTRY:
      refreshMenu ();
      break;
    }
  }

  /**
   *  Implementation of the EntryChangeListener interface.
   **/
  public void entryChanged (final EntryChangeEvent event) {
    if (event.getType () == EntryChangeEvent.NAME_CHANGED) {
      refreshMenu ();
    }
  }

  /**
   *  Update the menus to the reflect the current contents of the EntryGroup.
   **/
  private void refreshMenu () {
    removeAll ();

    undo_item = new JMenuItem ("Undo");
    undo_item.setAccelerator (UNDO_KEY);
    undo_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        undo (getParentFrame (), getSelection (), getEntryGroup ());
      }
    });

    edit_feature_item = new JMenuItem ("Edit Selected Features");
    edit_feature_item.setAccelerator (EDIT_FEATURES_KEY);
    edit_feature_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        editSelectedFeatures (getParentFrame (), getEntryGroup (),
                              getSelection (), getGotoEventSource ());
      }
    });

    edit_subsequence_item = new JMenuItem ("Edit Subsequence (and Features)");
    edit_subsequence_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        editSubSequence ();
      }
    });

    add_qualifiers_item = new JMenuItem ("Change Qualifiers Of Selected ...");
    add_qualifiers_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        addQualifiers (getParentFrame (), getSelection ());
      }
    });

    remove_qualifier_item = new JMenuItem ("Remove Qualifier Of Selected ...");
    remove_qualifier_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        removeQualifier (getParentFrame (), getSelection ());
      }
    });


    merge_features_item = new JMenuItem ("Merge Selected Features");
    merge_features_item.setAccelerator (MERGE_FEATURES_KEY);
    merge_features_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        mergeFeatures (getParentFrame (), getSelection (), getEntryGroup ());
      }
    });

    unmerge_feature_item = new JMenuItem ("Unmerge Selected Feature");
    unmerge_feature_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        unmergeFeature (getParentFrame (), getSelection (), getEntryGroup ());
      }
    });

    duplicate_item = new JMenuItem ("Duplicate Selected Features");
    duplicate_item.setAccelerator (DUPLICATE_KEY);
    duplicate_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        duplicateFeatures (getParentFrame (), getSelection (),
                           getEntryGroup ());
      }
    });

    delete_features_item = new JMenuItem ("Delete Selected Features");
    delete_features_item.setAccelerator (DELETE_FEATURES_KEY);
    delete_features_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        deleteSelectedFeatures (getParentFrame (), getSelection (),
                                getEntryGroup ());
      }
    });

    delete_segments_item = new JMenuItem ("Delete Selected Exons");
    delete_segments_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        deleteSelectedSegments ();
      }
    });

    delete_introns_item =
      new JMenuItem ("Remove Introns of Selected Features");
    delete_introns_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        removeIntrons ();
      }
    });

    edit_header_item = new JMenuItem ("Edit Header Of Default Entry");
    edit_header_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        editHeader ();
      }
    });

    move_features_menu = new JMenu ("Move Selected Features To");
    copy_features_menu = new JMenu ("Copy Selected Features To");

    if (entry_group == null || getEntryGroup ().size () == 0) {
      move_features_menu.add (new JMenuItem ("(No Entries Currently)"));
      copy_features_menu.add (new JMenuItem ("(No Entries Currently)"));
    } else {
      for (int i = 0 ; i < getEntryGroup ().size () ; ++i) {
        final Entry this_entry = getEntryGroup ().elementAt (i);

        String entry_name = this_entry.getName ();

        if (entry_name == null) {
          entry_name = "no name";
        }

        final JMenuItem move_to_item = new JMenuItem (entry_name);

        move_to_item.addActionListener (new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            // unselect, move, then reselect (for speed)
            final FeatureVector selected_features =
              getSelection ().getAllFeatures ();
            getSelection ().clear ();
            moveFeatures (selected_features, this_entry);
            getSelection ().set (selected_features);
          }
        });
        move_features_menu.add (move_to_item);

        final JMenuItem copy_to_item = new JMenuItem (entry_name);

        copy_to_item.addActionListener (new ActionListener () {
          public void actionPerformed (ActionEvent event) {
            copyFeatures (getSelection ().getAllFeatures (), this_entry);
          }
        });
        copy_features_menu.add (copy_to_item);
      }
    }

    trim_to_any_item = new JMenuItem ("Trim Selected Features To Any");
    trim_to_any_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        EditMenu.trimSelected (getParentFrame (), getSelection (),
                               getEntryGroup (), true, false);
      }
    });

    trim_item = new JMenuItem ("Trim Selected Features To Met");
    trim_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        EditMenu.trimSelected (getParentFrame (), getSelection (),
                               getEntryGroup (), false, false);
      }
    });

    trim_to_next_any_item =
      new JMenuItem ("Trim Selected Features To Next Any");
    trim_to_next_any_item.setAccelerator (TRIM_FEATURES_TO_NEXT_ANY_KEY);
    trim_to_next_any_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        EditMenu.trimSelected (getParentFrame (), getSelection (),
                               getEntryGroup (), true, true);
      }
    });

    trim_to_next_item = new JMenuItem ("Trim Selected Features To Next Met");
    trim_to_next_item.setAccelerator (TRIM_FEATURES_KEY);
    trim_to_next_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        EditMenu.trimSelected (getParentFrame (), getSelection (),
                               getEntryGroup (), false, true);
      }
    });

    extend_to_prev_stop_item =
      new JMenuItem ("Extend to Previous Stop Codon");
    extend_to_prev_stop_item.setAccelerator (EXTEND_TO_PREVIOUS_STOP_CODON_KEY);
    extend_to_prev_stop_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        extendToORF (getParentFrame (), getSelection (),
                     getEntryGroup (), false);
      }
    });

    extend_to_next_stop_item = new JMenuItem ("Extend to Next Stop Codon");
    extend_to_next_stop_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        extendToORF (getParentFrame (), getSelection (),
                     getEntryGroup (), true);
      }
    });

    fix_stop_codons_item = new JMenuItem ("Fix Stop Codons");
    fix_stop_codons_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        fixStopCodons ();
      }
    });

    auto_gene_name_item = new JMenuItem ("Automatically Create Gene Names");
    auto_gene_name_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        autoGeneName ();
      }
    });

    fix_gene_names_item = new JMenuItem ("Fix Gene Names");
    fix_gene_names_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        fixGeneNames (getParentFrame (), getEntryGroup (),
                      getSelection ());
      }
    });

    reverse_complement_item = new JMenuItem ("Reverse And Complement");
    reverse_complement_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        reverseAndComplement ();
      }
    });

    delete_bases_item = new JMenuItem ("Delete Selected Bases");
    delete_bases_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        deleteSelectedBases ();
      }
    });

    add_bases_item = new JMenuItem ("Add Bases At Selection");
    add_bases_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        addBases ();
      }
    });

    if (Options.readWritePossible ()) {
      // only the standalone version can save or read
      add_bases_from_file_item = new JMenuItem ("Add Bases From File ...");
      add_bases_from_file_item.addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent event) {
          addBasesFromFile ();
        }
      });
    }

    if (Options.getOptions ().getPropertyTruthValue ("val_mode")) {
      add (edit_feature_item);
      add (edit_subsequence_item);
      addSeparator ();
      add (edit_header_item);
      addSeparator ();
    }

    if (Options.getOptions ().getUndoLevels () > 0) {
      add (undo_item);
      addSeparator ();
    }

    if (!Options.getOptions ().getPropertyTruthValue ("val_mode")) {
      add (edit_feature_item);
      add (edit_subsequence_item);
      addSeparator ();
      add (edit_header_item);
      addSeparator ();
    }

    add (add_qualifiers_item);
    add (remove_qualifier_item);
    add (duplicate_item);
    add (merge_features_item);
    add (unmerge_feature_item);
    add (delete_features_item);
    add (delete_segments_item);
    add (delete_introns_item);
    addSeparator ();
    add (move_features_menu);
    add (copy_features_menu);
    addSeparator ();
    add (trim_item);
    add (trim_to_any_item);
    add (trim_to_next_item);
    add (trim_to_next_any_item);
    add (extend_to_prev_stop_item);
    add (extend_to_next_stop_item);
    add (fix_stop_codons_item);
    addSeparator ();
    add (auto_gene_name_item);
    add (fix_gene_names_item);
    add (reverse_complement_item);
    add (delete_bases_item);
    add (add_bases_item);
    if (Options.readWritePossible ()) {
      // only the standalone version can save or read
      add (add_bases_from_file_item);
    }
  }

  /**
   *  Undo the last change by calling ActionController.undo().
   *  @param frame The Frame to use for MessageDialog components.
   *  @param selection The current Selection - needs to be cleared before undo
   *  @param entry_group Used to get the ActionController for calling
   *    ActionController.undo().
   **/
  public static void undo (final JFrame frame,
                           final Selection selection,
                           final EntryGroup entry_group) {
    if (Options.getOptions ().getUndoLevels () == 0) {
      // undo disabled
      return;
    }

    if (entry_group.getActionController ().canUndo ()) {
      // clear the selection because something in the selection might
      // disappear after the undo() eg. create a feature, select it then undo
      selection.clear ();
    }

    if (!entry_group.getActionController ().undo ()) {
      new MessageDialog (frame, "sorry - no further undo information");
    }
  }

  /**
   *  editSelectedFeatures () etc. will only edit this many features.
   **/
  private static final int MAXIMUM_SELECTED_FEATURES = 25;

  /**
   *  Open an edit window (FeatureEdit) for each of the selected features.
   *  The edit component will listen for feature change events and update
   *  itself.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The selected features to edit.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction() and endAction().
   **/
  static void editSelectedFeatures (final JFrame frame,
                                    final EntryGroup entry_group,
                                    final Selection selection,
                                    final GotoEventSource goto_event_source) {
    final FeatureVector features_to_edit = selection.getAllFeatures ();

    if (features_to_edit.size () > MAXIMUM_SELECTED_FEATURES) {
      new MessageDialog (frame, "warning: only editing the first " +
                         MAXIMUM_SELECTED_FEATURES +
                         " selected features");
    }

    for (int i = 0 ;
         i < features_to_edit.size () && i < MAXIMUM_SELECTED_FEATURES ;
         ++i) {
      final Feature selection_feature = features_to_edit.elementAt (i);

      new FeatureEdit (selection_feature, entry_group, selection,
                       goto_event_source).show ();
    }

    selection.set (features_to_edit);
  }

  /**
   *  Create a new EntryEdit component that contains only the selected
   *  sequence and the features in the selected range.
   **/
  private void editSubSequence () {
    if (getSelection ().isEmpty ()) {
      new MessageDialog (getParentFrame (), "nothing selected");
    }

    final Range range = getSelection ().getSelectionRange ();

    final EntryGroup new_entry_group = getEntryGroup ().truncate (range);

    new EntryEdit (new_entry_group).show ();
  }

  /**
   *  Open a EntryHeaderEdit window for the default entry.
   **/
  private void editHeader () {
    final Entry default_entry = getEntryGroup ().getDefaultEntry ();

    if (default_entry == null) {
      final String message =
        "there is no default entry";
      new MessageDialog (getParentFrame (), message);
    } else {
      if (default_entry.isReadOnly ()) {
        new MessageDialog (getParentFrame (),
                           "the default entry is read-only " +
                           "- cannot continue");
        return;
      }

      new EntryHeaderEdit (entry_group, default_entry);
    }
  }

  /**
   *  Merge the selected features into one Feature.  If there are selected
   *  segments then the owning Feature of each segment will be the Feature
   *  that is merged.  This method will create a new Feature.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the features to merge.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction() and endAction().
   **/
  static void mergeFeatures (final JFrame frame,
                             final Selection selection,
                             final EntryGroup entry_group) {
    try {
      entry_group.getActionController ().startAction ();

      if (!checkForSelectionFeatures (frame, selection,
                                      10, "really merge all (>10) " +
                                      "selected features?")) {
        return;
      }

      final FeatureVector features_to_merge = selection.getAllFeatures ();

      if (features_to_merge.size () < 2) {
        new MessageDialog (frame,
                           "nothing to merge - select more than one feature");
        return;
      }

      final Feature merge_feature = features_to_merge.elementAt (0);

      // make sure all the features are on the same strand
      for (int i = 1 ; i < features_to_merge.size () ; ++i) {
        final Feature this_feature = features_to_merge.elementAt (i);

        if (this_feature.isForwardFeature () !=
            merge_feature.isForwardFeature ()) {
          new MessageDialog (frame,
                             "all the features in a merge must be on the " +
                             "same strand");
          return;
        }

        if (! this_feature.getKey ().equals (merge_feature.getKey ())) {
          new MessageDialog (frame,
                             "all the features in a merge must have the " +
                             "same key");
          return;
        }
      }

      if (Options.getOptions ().isNoddyMode ()) {
        final YesNoDialog dialog =
          new YesNoDialog (frame,
                           "Are you sure you want to merge the selected " +
                           "features?");

        if (!dialog.getResult ()) {
          return;
        }
      }

      final Feature new_feature;

      try {
        new_feature = merge_feature.duplicate ();
      } catch (ReadOnlyException e) {
        final String message =
          "one or more of the features is read-only or is in a " +
          "read-only entry - cannot continue";
        new MessageDialog (frame, message);
        return;
      }

      for (int i = 1 ; i < features_to_merge.size () ; ++i) {
        final Feature this_feature = features_to_merge.elementAt (i);

        final QualifierVector qualifiers = this_feature.getQualifiers ();

        for (int qualifier_index = 0 ;
             qualifier_index < qualifiers.size () ;
             ++qualifier_index) {
          final Qualifier this_qualifier =
            qualifiers.elementAt (qualifier_index);

          try {
            new_feature.addQualifierValues (this_qualifier);
          } catch (EntryInformationException e) {
            try {
              new_feature.removeFromEntry ();
            } catch (ReadOnlyException _) {
              // give up ...
            }
            final String message =
              "destination entry does not support all the qualifiers " +
              "needed by " + this_feature.getIDString ();
            new MessageDialog (frame, message);
          } catch (ReadOnlyException e) {
            final String message = "the new feature is read-only so " +
              "some qualifiers have been lost";
            new MessageDialog (frame, message);
          }
        }

        final FeatureSegmentVector segments = this_feature.getSegments ();

        for (int segment_index = 0 ;
             segment_index < segments.size () ;
             ++segment_index) {
          final FeatureSegment this_segment =
            segments.elementAt (segment_index);

          final Range this_range = this_segment.getRawRange ();

          try {
            new_feature.addSegment (this_range);
          } catch (ReadOnlyException e) {
            final String message =
              "merging failed because the entry is read-only";
            new MessageDialog (frame, message);
            try {
              new_feature.removeFromEntry ();
            } catch (ReadOnlyException _) {
              // oh dear ...
            }
          }
        }
      }

      // this is set to true when a merge is done in the next (inner) loop
      boolean keep_looping = true;

      // this is a bit inefficient, but there aren't normally many segments
  LOOP:
      while (keep_looping) {
        final FeatureSegmentVector feature_segments =
          new_feature.getSegments ();

        keep_looping = false;

        // now merge overlapping ranges
        for (int segment_index = 0 ;
             segment_index < feature_segments.size () - 1;
             ++segment_index) {

          final FeatureSegment this_segment =
            feature_segments.elementAt (segment_index);
          final MarkerRange this_range = this_segment.getMarkerRange ();

          final FeatureSegment next_segment =
            feature_segments.elementAt (segment_index + 1);
          final MarkerRange next_range = next_segment.getMarkerRange ();

          // if it overlaps the next Range then merge it
          if (this_range.overlaps (next_range) &&
              this_segment.getFrameID () == next_segment.getFrameID ()) {
            try {
              final Range new_range =
                this_range.combineRanges (next_range, false).getRawRange ();
              new_feature.addSegment (new_range);
              new_feature.removeSegment (this_segment);
              new_feature.removeSegment (next_segment);

              // start again
              keep_looping = true;
              continue LOOP;
            } catch (ReadOnlyException e) {
              final String message =
                "merging failed because the entry is read-only";
              new MessageDialog (frame, message);
            } catch (LastSegmentException e) {
              throw new Error ("internal error - tried to remove " +
                               "last segment: " + e);
            }
          }
        }
      }

      boolean delete_old_features;

      if (Options.getOptions ().isNoddyMode ()) {
        final YesNoDialog delete_old_dialog =
          new YesNoDialog (frame, "delete old features?");

        delete_old_features = delete_old_dialog.getResult ();
      } else {
        delete_old_features = true;
      }

      if (delete_old_features) {
        if (getReadOnlyFeatures (features_to_merge).size () > 0) {
          new MessageDialog (frame, "deletion failed because the features " +
                             "are read-only");
       } else {
          for (int i = 0 ; i < features_to_merge.size () ; ++i) {
            try {
              features_to_merge.elementAt (i).removeFromEntry ();
            } catch (ReadOnlyException e) {
              new MessageDialog (frame, "deletion failed one or more of the " +
                                 "features are read-only");
            }
          }
        }
      }

      selection.set (new_feature);
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  If the selection contains exactly two segments and those segments are
   *  adjacent in the same feature, split the feature into two pieces.  The
   *  orignal feature is truncated and a new feature is created.  The
   *  qualifiers of the old feature are copied to new feature.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the segments to unmerge.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction() and endAction().
   **/
  static void unmergeFeature (final JFrame frame,
                              final Selection selection,
                              final EntryGroup entry_group) {
    try {
      entry_group.getActionController ().startAction ();

      final FeatureSegmentVector selected_segments =
        selection.getSelectedSegments ();

      if (selected_segments.size () != 2) {
        final String message =
          "you need to select exactly two exons use unmerge";
        new MessageDialog (frame, message);
        return;
      }

      FeatureSegment first_segment = selected_segments.elementAt (0);
      FeatureSegment second_segment = selected_segments.elementAt (1);

      if (first_segment.getFeature () != second_segment.getFeature ()) {
        final String message =
          "you need to select two exons from the same feature to use unmerge";
        new MessageDialog (frame, message);
        return;
      }

      final Feature segment_feature = first_segment.getFeature ();

      final FeatureSegmentVector all_feature_segments =
        segment_feature.getSegments ();

      int index_of_first_segment =
        all_feature_segments.indexOf (first_segment);
      int index_of_second_segment =
        all_feature_segments.indexOf (second_segment);

      if (index_of_first_segment - index_of_second_segment < -1 ||
          index_of_first_segment - index_of_second_segment > 1) {
        final String message =
          "you need to select two adjacent exons to use unmerge";
        new MessageDialog (frame, message);
        return;
      }

      if (index_of_second_segment <  index_of_first_segment) {
        // swap the segments for consistency
        final FeatureSegment temp_segment = first_segment;
        final int temp_segment_index = index_of_first_segment;

        first_segment = second_segment;
        index_of_first_segment = index_of_second_segment;

        second_segment = temp_segment;
        index_of_second_segment = temp_segment_index;
      }

      try {
        final Feature new_feature = segment_feature.duplicate ();

        // we set the Selection later
        selection.clear ();

        // delete the segments starting at index_of_second_segment from
        // segment_feature and delete the segments up to (and including)
        // index_of_first_segment from new_feature

        for (int i = all_feature_segments.size () - 1 ;
             i >= index_of_second_segment ;
             --i) {
          segment_feature.getSegments ().elementAt (i).removeFromFeature ();
        }

        // remove the first segment of new_feature index_of_first_segment times
        for (int i = 0 ; i <= index_of_first_segment ; ++i) {
          new_feature.getSegments ().elementAt (0).removeFromFeature ();
        }

        selection.set (segment_feature.getSegments ().lastElement ());
        selection.add (new_feature.getSegments ().elementAt (0));
      } catch (ReadOnlyException e) {
        final String message =
          "the selected exons (in " +
          segment_feature.getIDString () +
          ") are in a read only entry - cannot continue";
        new MessageDialog (frame, message);
      } catch (LastSegmentException e) {
        throw new Error ("internal error - unexpected exception: " + e);
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Create a QualifierEditor JFrame that acts on the selected features.
   *  @param frame The JFrame to use for MessageDialog components.
   **/
  private void addQualifiers (final JFrame frame, final Selection selection) {
    if (!checkForSelectionFeatures (frame, selection)) {
      return;
    }

    final FeatureVector selected_features = selection.getAllFeatures ();

    if (getReadOnlyFeatures (selected_features).size () > 0) {
      new MessageDialog (frame,
                         "one or more of the selected features is read-only " +
                         "- cannot continue");
      return;
    }

    final QualifierEditor qualifier_editor =
      new QualifierEditor (selected_features, getEntryGroup ());

    qualifier_editor.setVisible (true);
  }

  /**
   *  Offer the user a choice of qualifier to remove from the selected features
   **/
  private void removeQualifier (final JFrame frame, final Selection selection) {
    if (!checkForSelectionFeatures (frame, selection)) {
      return;
    }

    final FeatureVector selected_features = selection.getAllFeatures ();

    final StringVector qualifier_names =
      Feature.getAllQualifierNames (selected_features);

    if (qualifier_names.size () == 0) {
      new MessageDialog (getParentFrame (), "feature has no qualifiers");
      return;
    }

    final ChoiceFrame choice_frame =
      new ChoiceFrame ("Select a qualifer name", qualifier_names);

    final JComboBox choice = choice_frame.getChoice ();


    final int MAX_VISIBLE_ROWS = 30;
    
    choice.setMaximumRowCount (MAX_VISIBLE_ROWS);
    
    choice.addItemListener (new ItemListener () {
      public void itemStateChanged (ItemEvent _) {
        removeQualifierFromFeatures (selected_features,
                                     (String) choice.getSelectedItem ());
        choice_frame.setVisible (false);
        choice_frame.dispose ();
      }
    });

    choice_frame.getOKButton ().addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        removeQualifierFromFeatures (selected_features,
                                     (String) choice.getSelectedItem ());
      }
    });

    choice_frame.setVisible (true);
  }

  /**
   *  Remove the qualifier given by qualifier_name from the given features.
   *  Silently ignore features that don't have that qualifier.  Warn the user
   *  about read-only features.
   **/
  private void removeQualifierFromFeatures (final FeatureVector features,
                                            final String qualifier_name) {
    boolean found_read_only = false;

    try {
      entry_group.getActionController ().startAction ();

      for (int i = 0 ; i < features.size () ; ++i) {
        final Feature this_feature = features.elementAt (i);
        try {
          this_feature.removeQualifierByName (qualifier_name);
        } catch (OutOfDateException _) {
          // ignore
        } catch (EntryInformationException _) {
          // ignore
        } catch (ReadOnlyException _) {
          found_read_only = true;
        }
      }

      if (found_read_only) {
        final String message =
          "ignored one or more read-only features";
        new MessageDialog (getParentFrame (), message);
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Duplicate the selected Feature objects.  If there are selected segments
   *  then the owning Feature of each segment will be duplicated.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the features to merge.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction() and endAction().
   **/
  static void duplicateFeatures (final JFrame frame,
                                 final Selection selection,
                                 final EntryGroup entry_group) {
    try {
      entry_group.getActionController ().startAction ();

      if (getReadOnlyFeatures (selection.getAllFeatures ()).size () > 0) {
        new MessageDialog (frame,
                           "one or more of the selected features is read-only " +
                           "- cannot continue");
        return;
      }

      if (Options.getOptions ().isNoddyMode ()) {
        final YesNoDialog dialog =
          new YesNoDialog (frame,
                           "Are you sure you want to duplicate the selected " +
                           "features?");

        if (!dialog.getResult ()) {
          return;
        }
      } else {
        if (!checkForSelectionFeatures (frame, selection,
                                        100, "really duplicate all (>100) " +
                                        "selected features?")) {
          return;
        }
      }

      final FeatureVector features_to_duplicate =
        selection.getAllFeatures ();

      for (int i = 0 ; i < features_to_duplicate.size () ; ++i) {
        final Feature this_feature = features_to_duplicate.elementAt (i);

        try {
          this_feature.duplicate ();
        } catch (ReadOnlyException e) {
          final String message =
            "one of the selected features (" + this_feature.getIDString () +
            ")  is read only - cannot continue";
          new MessageDialog (frame, message);
          return;
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Delete the selected features.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the features to merge.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction() and endAction().
   **/
  static void deleteSelectedFeatures (final JFrame frame,
                                      final Selection selection,
                                      final EntryGroup entry_group) {
    try {
      entry_group.getActionController ().startAction ();

      final FeatureVector features_to_delete = selection.getAllFeatures ();

      final String feature_count_string;

      if (features_to_delete.size () == 1) {
        feature_count_string = "the selected feature";
      } else {
        feature_count_string = features_to_delete.size () + " features";
      }

      if (Options.getOptions ().isNoddyMode ()) {
        // 0 means always popup a YesNoDialog
        if (!checkForSelectionFeatures (frame, selection, 0,
                                        "really delete " +
                                        feature_count_string + "?")) {
          return;
        }
      }

      // clear the selection now so that it doesn't need to be updated as each
      // feature is deleted
      selection.clear ();

      while (features_to_delete.size () > 0) {
        // delete in reverse order for speed

        final Feature current_selection_feature =
          features_to_delete.lastElement ();

        features_to_delete.removeElementAt (features_to_delete.size () - 1);

        try {
          current_selection_feature.removeFromEntry ();
        } catch (ReadOnlyException e) {
          selection.set (current_selection_feature);

          if (features_to_delete.size () == 1) {
            final String message =
              "the selected feature (" +
              current_selection_feature.getIDString () +
              ") is read only - cannot continue";
            new MessageDialog (frame, message);

          } else {
            final String message =
              "one of the selected features (" +
              current_selection_feature.getIDString () +
              ") is read only - cannot continue";
            new MessageDialog (frame, message);
          }

          features_to_delete.add (current_selection_feature);

          // reset the select so that the user can see what it was
          selection.set (features_to_delete);

          return;
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Delete the selected feature segments.
   **/
  private void deleteSelectedSegments () {
    try {
      entry_group.getActionController ().startAction ();

      final FeatureSegmentVector segments_to_delete =
        (FeatureSegmentVector) getSelection ().getSelectedSegments ().clone ();

      if (Options.getOptions ().isNoddyMode ()) {
        // 0 means always popup a YesNoDialog
        if (!checkForSelectionFeatureSegments (0, "really delete " +
                                               (segments_to_delete.size ()==1 ?
                                                "the selected exon?" :
                                                segments_to_delete.size () +
                                                " exons?"))) {
          return;
        }
      }

      for (int i = 0 ; i < segments_to_delete.size () ; ++i) {
        final FeatureSegment selection_segment =
          segments_to_delete.elementAt (i);

        try {
          getSelection ().remove (selection_segment);
          selection_segment.removeFromFeature ();
        } catch (ReadOnlyException e) {
          final String message =
            "one of the selected exons (in " +
            selection_segment.getFeature ().getIDString () +
            ") is in a read only - cannot continue";
          new MessageDialog (getParentFrame (), message);
        } catch (LastSegmentException e) {
          final String message =
            "the last exon in this feature: " +
            selection_segment.getFeature ().getIDString () +
            " cannot be removed (all features must have at least one exon)";
          new MessageDialog (getParentFrame (), message);
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Remove all introns from the selected features.
   **/
  private void removeIntrons () {
    if (!checkForSelectionFeatures ()) {
      return;
    }

    boolean found_read_only = false;

    try {
      entry_group.getActionController ().startAction ();

      final FeatureVector features = getSelection ().getAllFeatures ();

      for (int i = 0 ; i < features.size () ; ++i) {
        final Feature this_feature = features.elementAt (i);
        try {
          final RangeVector new_ranges = new RangeVector ();
          final Range new_range = this_feature.getMaxRawRange ();
          new_ranges.add (new_range);
          final Location old_location = this_feature.getLocation ();
          final Location new_location =
            new Location (new_ranges, old_location.isComplement ());
          this_feature.setLocation (new_location);
        } catch (OutOfRangeException e) {
          throw new Error ("internal error - unexpected exception: " + e);
        } catch (ReadOnlyException _) {
          found_read_only = true;
        }
      }

      if (found_read_only) {
        final String message =
          "ignored one or more read-only features";
        new MessageDialog (getParentFrame (), message);
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Move the given features to the given destination entry.
   **/
  private void moveFeatures (final FeatureVector features,
                             final Entry destination_entry) {
    try {
      entry_group.getActionController ().startAction ();

      if (features.size () == 0) {
        return;
      }

      final FeatureVector read_only_features =
        getReadOnlyFeatures (features);

      if (read_only_features.size () > 0) {
        final String message =
          (features.size () == 1 ?
           "the selected feature (" +
           read_only_features.elementAt (0).getIDString () +
           ") is read only " :
           "some of the selected features (eg. " +
           read_only_features.elementAt (0).getIDString () +
           ") are read only - ")  +
          "cannot continue";
        new MessageDialog (getParentFrame (), message);
        return;
      }

  FEATURES:
      for (int i = 0 ; i < features.size () ; ++i) {
        final Feature this_feature = features.elementAt (i);

        // check that we don't loop forever (perhaps due to bugs in
        // handleOpenException())
        final int MAX_COUNT = 100;

        int count = 0;

        while (count++ < MAX_COUNT) {
          try {
            this_feature.moveTo (destination_entry, false);
            continue FEATURES;
          } catch (OutOfRangeException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          } catch (EntryInformationException e) {
            final EntryInformation dest_entry_information =
              destination_entry.getEntryInformation ();
            dest_entry_information.fixException (e);
            continue;
          } catch (ReadOnlyException e) {
            final String message =
              "either the source or destination for one of the features is " +
              "read only - cannot continue";
            new MessageDialog (getParentFrame (), message);
            return;
          }
        }

        final String message =
          "internal error while copying";
        new MessageDialog (getParentFrame (), message);

        throw new Error ("internal error in EditMenu.copyFeatures() - infinite loop");
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Copy the given features to the given destination entry.
   **/
  private void copyFeatures (final FeatureVector features,
                             final Entry destination_entry) {
    try {
      entry_group.getActionController ().startAction ();

  FEATURES:
      for (int i = 0 ; i < features.size () ; ++i) {
        final Feature this_feature = features.elementAt (i);

        // check that we don't loop forever (perhaps due to bugs in
        // handleOpenException())
        final int MAX_COUNT = 100;

        int count = 0;

        while (count++ < MAX_COUNT) {
          try {
            this_feature.copyTo (destination_entry);
            continue FEATURES;
          } catch (OutOfRangeException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          } catch (EntryInformationException e) {
            final EntryInformation dest_entry_information =
              destination_entry.getEntryInformation ();
            dest_entry_information.fixException (e);
            continue;
          } catch (ReadOnlyException e) {
            final String message =
              "the destination entry is read only - cannot continue";
            new MessageDialog (getParentFrame (), message);
            return;
          }
        }

        final String message = "internal error while copying";
        new MessageDialog (getParentFrame (), message);

        throw new Error ("internal error in EditMenu.copyFeatures() - " +
                           "infinite loop");
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Calls fixStopCodon () on each selected feature.
   **/
  private void fixStopCodons () {
    try {
      entry_group.getActionController ().startAction ();

      if (!checkForSelectionFeatures ()) {
        return;
      }

      // features that can't be fixed
      final FeatureVector bad_features = new FeatureVector ();

      final FeatureVector features_to_fix = getSelection ().getAllFeatures ();

      for (int i = 0 ; i < features_to_fix.size () ; ++i) {
        final Feature selection_feature = features_to_fix.elementAt (i);

        if (!selection_feature.isCDS ()) {
          final String message =
            "Warning: some of the selected features are not coding features. " +
            "Continue?";

          final YesNoDialog yes_no_dialog =
            new YesNoDialog (getParentFrame (), message);

          if (yes_no_dialog.getResult ()) {
            break;
          } else {
            return;
          }
        }
      }

      for (int i = 0 ; i < features_to_fix.size () ; ++i) {
        final Feature selection_feature = features_to_fix.elementAt (i);

        try {
          if (!selection_feature.fixStopCodon ()) {
            bad_features.add (selection_feature);
          }
        } catch (ReadOnlyException e) {
          final String message =
            "one of the entries is read only - cannot continue";
          new MessageDialog (getParentFrame (), message);
          break;
        }
      }

      if (bad_features.size () > 0) {
        // select the bad feature
        getSelection ().set (bad_features);

        final String message =
          "Warning: could not fix the stop codon for some of the features. " +
          "View them now?";

        final YesNoDialog yes_no_dialog =
          new YesNoDialog (getParentFrame (), message);

        if (yes_no_dialog.getResult ()) {
          final FeaturePredicate predicate =
            new FeatureFromVectorPredicate (bad_features);

          final String filter_name =
            "features that can't be fixed (filtered from: " +
            getParentFrame ().getTitle () + ")";

          final FilteredEntryGroup filtered_entry_group =
            new FilteredEntryGroup (entry_group, predicate, filter_name);

          final FeatureListFrame feature_list_frame =
            new FeatureListFrame (filter_name,
                                  getSelection (),
                                  goto_event_source, filtered_entry_group,
                                  base_plot_group);

          feature_list_frame.setVisible (true);
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Calls Feature.trimStart () on all selected Feature objects.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction().
   *  @param trim_to_any If true then the features will be trimmed to ATG, GTG
   *    or TTG, otherwise the features will be trimmed to ATG only.
   *  @param trim_to_next If true then the features will be trimmed to the
   *    next start codon (dependent on trim_to_any) regardless of whether the
   *    feature currently start on a start codon.  If false then the feature
   *    will only be trimmed if the feature doesn't start on a start codon.
   **/
  static public void trimSelected (final JFrame frame,
                                   final Selection selection,
                                   final EntryGroup entry_group,
                                   final boolean trim_to_any,
                                   final boolean trim_to_next) {
    try {
      entry_group.getActionController ().startAction ();

      if (!checkForSelectionFeatures (frame, selection)) {
        return;
      }

      final FeatureVector features_to_trim = selection.getAllFeatures ();

      // this will contain the features that could not be trimmed
      final FeatureVector failed_features = new FeatureVector ();

      for (int i = 0 ; i < features_to_trim.size () ; ++i) {
        final Feature selection_feature = features_to_trim.elementAt (i);

        try {
          if (!selection_feature.trimStart (trim_to_any, trim_to_next)) {
            failed_features.add (selection_feature);
          }
        } catch (ReadOnlyException e) {
          final String message =
            "one or more of the of the selected features are read only " +
            "- cannot continue";
          new MessageDialog (frame, message);
          break;
        }
      }

      if (failed_features.size () > 0) {
        selection.set (failed_features);

        if (failed_features.size () == 1) {
          final String message = "could not trim the feature";

          new MessageDialog (frame, message);

        } else {
          final String message =
            "some features could not be trimmed (they are now selected)";

          new MessageDialog (frame, message);
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Extend the selected segments/features so that they reach to the start or
   *  end of their containing ORF.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   *  @param entry_group Used to get the ActionController for calling
   *    startAction() and endAction().
   *  @param extend_to_next_stop If true the feature is extended to the next
   *    stop codon (but it won't include the next stop).  If false the feature
   *    is extended to the previous stop codon (but it won't include the
   *    previous stop).
   **/
  public static void extendToORF (final JFrame frame,
                                  final Selection selection,
                                  final EntryGroup entry_group,
                                  final boolean extend_to_next_stop) {
    try {
      entry_group.getActionController ().startAction ();

      if (!checkForSelectionFeatures (frame, selection)) {
        return;
      }

      final FeatureVector features_to_extend = selection.getAllFeatures ();

      for (int i = 0 ; i < features_to_extend.size () ; ++i) {
        final Feature selection_feature = features_to_extend.elementAt (i);

        if (selection_feature.isReadOnly ()) {
          final String message =
            "one or more of the of the selected features are read only " +
            "- cannot continue";
          new MessageDialog (frame, message);
          break;
        }

        if (selection_feature.getSegments ().size () < 1) {
          continue;
        }

        final FeatureSegmentVector feature_segments =
          selection_feature.getSegments ();

        if (feature_segments.size () == 0) {
          continue;
        }

        final Strand strand = selection_feature.getStrand ();

        if (extend_to_next_stop) {
          if (selection_feature.hasValidStopCodon ()) {
            continue;
          }

          final Marker feature_end_marker =
            selection_feature.getLastBaseMarker ();

          Marker end_marker_copy = null;

          // get the marker of the first base of the last codon in the range
          // (we want to keep in the same frame)
          try {
            if (selection_feature.getBaseCount () == 1) {
              end_marker_copy = feature_end_marker;
            } else {
              if (selection_feature.getBaseCount () == 2) {
                end_marker_copy = feature_end_marker.moveBy (-1);
              } else {
                // go to the start of the codon
                end_marker_copy = feature_end_marker.moveBy (-2);
              }
            }

            // adjust for /codon_start
            final int frame_shift =
              selection_feature.getCodonStart () - 1;

            // the number of bases from the start of translation to the
            // end of the feature
            final int bases_from_start_pos_mod_3 =
              (3 + selection_feature.getBaseCount () - frame_shift) % 3;

            end_marker_copy =
              end_marker_copy.moveBy (-bases_from_start_pos_mod_3);
          } catch (OutOfRangeException e) {
            end_marker_copy = feature_end_marker;
          }

          final MarkerRange end_orf_range =
            strand.getORFAroundMarker (end_marker_copy, true);

          if (end_orf_range == null) {
            // end_marker_copy is at the start base of a stop codon
            continue;
          }

          final Marker new_end_marker = end_orf_range.getEnd ();

          try {
            feature_end_marker.setPosition (new_end_marker.getPosition ());
          } catch (OutOfRangeException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          }

        } else {
          final AminoAcidSequence translation =
            selection_feature.getTranslation ();

          if (translation.length () > 0) {
            final char first_aa = translation.elementAt (0);

            if (AminoAcidSequence.isStopCodon (first_aa)) {
              continue;
            }
          }

          final Marker feature_start_marker =
            selection_feature.getFirstBaseMarker ();

          Marker start_marker_copy = feature_start_marker;

          final int frame_shift =
            selection_feature.getCodonStart () - 1;

          if (frame_shift != 0) {
            try {
              start_marker_copy =
                feature_start_marker.moveBy (frame_shift);
            } catch (OutOfRangeException e) {
              // ignore and hope for the best
            }
          }

          final MarkerRange start_orf_range =
            strand.getORFAroundMarker (start_marker_copy, true);

          final Marker new_start_marker = start_orf_range.getStart ();

          try {
            feature_start_marker.setPosition (new_start_marker.getPosition ());
            selection_feature.removeQualifierByName ("codon_start");
          } catch (EntryInformationException _) {
            // ignore - if we can't remove codon_start, then it won't have
            // been there to start with
          } catch (OutOfDateException _) {
            // ignore
          } catch (ReadOnlyException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          } catch (OutOfRangeException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          }
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Reverse and complement the sequence and all the features in all Entry
   *  objects.
   **/
  private void reverseAndComplement () {
    if (getEntryGroup ().isReadOnly ()) {
      final String message =
        "one or more of the entries or features are read only - " +
        "cannot continue";
      new MessageDialog (getParentFrame (), message);
      return;
    }

    final YesNoDialog dialog =
      new YesNoDialog (getParentFrame (),
                       "Are you sure you want to reverse complement all " +
                       "entries?");

    if (!dialog.getResult ()) {
      return;
    }

    try {
      getEntryGroup ().reverseComplement ();

      makeSelectionStartVisible ();
    } catch (ReadOnlyException e) {
      final String message =
        "one of the entries is read only - cannot continue";
      new MessageDialog (getParentFrame (), message);
      return;
    }
  }

  /**
   *  Delete the selected bases after asking the user for confimation.
   **/
  private void deleteSelectedBases () {
    if (!checkForSelectionRange ()) {
      return;
    }

    final MarkerRange marker_range = getSelection ().getMarkerRange ();

    if (marker_range.getCount () == getEntryGroup ().getSequenceLength ()) {
      new MessageDialog (getParentFrame (), "You can't delete every base");
      return;
    }

    if (getEntryGroup ().isReadOnly ()) {
      new MessageDialog (getParentFrame (),
                         "one of the current entries or features is " +
                         "read-only - connot continue");
      return;
    }

    final Range raw_range = marker_range.getRawRange ();

    final FeatureVector features_in_range;

    final EntryVector old_active_entries =
      getEntryGroup ().getActiveEntries ();

    // make all the entries active so that getFeaturesInRange () gets
    // everything
    for (int i = 0 ; i < getEntryGroup ().size () ; ++i) {
      getEntryGroup ().setIsActive (i, true);
    }

    try {
      features_in_range =
        getEntryGroup ().getFeaturesInRange (raw_range);
    } catch (OutOfRangeException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }

    if (features_in_range.size () != 0) {
      final FeatureVector read_only_features =
        getReadOnlyFeatures (features_in_range);

      if (read_only_features.size () > 0) {
        getSelection ().set (read_only_features);
        final String message =
          "one or more of the features in the selected range are " +
          "read only - cannot continue";
        new MessageDialog (getParentFrame (), message);
        getSelection ().setMarkerRange (marker_range);
        return;
      }

      for (int feature_index = 0 ;
           feature_index < features_in_range.size () ;
           ++feature_index) {
        final Feature this_feature =
          features_in_range.elementAt (feature_index);

        final FeatureSegmentVector segments = this_feature.getSegments ();

        for (int i = 0 ; i < segments.size () ; ++i) {
          final FeatureSegment this_segment = segments.elementAt (i);

          final Range this_segment_range = this_segment.getRawRange ();

          if (raw_range.getStart () <= this_segment_range.getStart () &&
              raw_range.getEnd () >= this_segment_range.getStart () ||
              raw_range.getStart () <= this_segment_range.getEnd () &&
              raw_range.getEnd () >= this_segment_range.getEnd ()) {
            new MessageDialog (getParentFrame (),
                               "the selected range overlaps the " +
                               "start or end of an exon - cannot continue");
            return;
          }
        }
      }
    }

    // reset the EntryGroup to the state it was in originally
    for (int i = 0 ; i < getEntryGroup ().size () ; ++i) {
      final Entry this_entry = getEntryGroup ().elementAt (i);
      final boolean old_active_flag =
        old_active_entries.contains (this_entry);
      getEntryGroup ().setIsActive (i, old_active_flag);
    }

    if (Options.getOptions ().isNoddyMode ()) {
      final YesNoDialog dialog =
        new YesNoDialog (getParentFrame (),
                         "Are you sure you want to delete the " +
                         "selected bases?");
      if (!dialog.getResult ()) {
        return;
      }
    }

    try {
      getSelection ().setMarkerRange (null);

      // deleteRange () is static and uses the strand reference from the
      // MarkerRange to decide which Strand to edit
      Strand.deleteRange (marker_range);
    } catch (ReadOnlyException e) {
      getSelection ().setMarkerRange (marker_range);

      new MessageDialog (getParentFrame (),
                         "the bases are read only - cannot delete");
      return;
    }
  }

  /**
   *  Ask the user for some bases and then add them at the start of the
   *  selected range.
   **/
  private void addBases () {
    if (!checkForSelectionRange ()) {
      return;
    }

    if (getEntryGroup ().isReadOnly ()) {
      new MessageDialog (getParentFrame (),
                         "one of the current entries or features is " +
                         "read-only - connot continue");
      return;
    }

    final MarkerRange range = getSelection ().getMarkerRange ();

    final TextRequester text_requester =
      new TextRequester ("enter the bases to insert before base " +
                         range.getStart ().getPosition () +
                         (range.isForwardMarker () ?
                          " on the forward strand" :
                          " on the reverse strand") + ":",
                         18, "");

    text_requester.addTextRequesterListener (new TextRequesterListener () {
      public void actionPerformed (final TextRequesterEvent event) {
        if (event.getType () == TextRequesterEvent.CANCEL) {
          return;
        }

        final String bases_string = event.getRequesterText ().trim ();

        if (bases_string.length () == 0) {
          new MessageDialog (getParentFrame (), "no bases inserted");
          return;
        }

        try {
          // addBases () is static and uses the strand reference from the
          // start Marker to decide which Strand to edit
          Strand.addBases (range.getStart (), bases_string);
        } catch (ReadOnlyException e) {
          new MessageDialog (getParentFrame (),
                             "sorry the bases are read only");
          return;
        } catch (org.biojava.bio.symbol.IllegalSymbolException e) {
          new MessageDialog (getParentFrame (),
                             "sorry - new sequence contains non-IUB base " +
                             "codes");
          return;
        }
      }
    });

    text_requester.show ();
  }


  /**
   *  Ask the user for some bases and then add them at the start of the
   *  selected range.
   **/
  private void addBasesFromFile () {
    if (!checkForSelectionRange ()) {
      return;
    }

    if (getEntryGroup ().isReadOnly ()) {
      new MessageDialog (getParentFrame (),
                         "one of the current entries or features is " +
                         "read-only - connot continue");
      return;
    }

    final MarkerRange range = getSelection ().getMarkerRange ();

    // XXX add an InputStreamProgressListener
    final EntrySourceVector entry_sources =
      Utilities.getEntrySources (getParentFrame (), null);

    EntrySource filesystem_entry_source = null;

    for (int source_index = 0 ;
         source_index < entry_sources.size () ;
         ++source_index) {
      final EntrySource this_source =
        entry_sources.elementAt (source_index);

      if (this_source.isFullEntrySource ()) {
        continue;
      }

      if (this_source.getSourceName ().equals ("Filesystem")) {
        filesystem_entry_source = this_source;
      }
    }

    if (filesystem_entry_source == null) {
      throw new Error ("internal error - can't find a file system to read " +
                       "from");
    }

    try {
      final Entry new_entry = filesystem_entry_source.getEntry (true);

      final Bases bases = new_entry.getBases ();

      final String bases_string = bases.toString ();

      if (bases_string.length () == 0) {
        new MessageDialog (getParentFrame (), "no bases inserted");
        return;
      }

      // addBases () is static and uses the strand reference from the
      // start Marker to decide which Strand to edit
      Strand.addBases (range.getStart (), bases_string);
    } catch (ReadOnlyException e) {
      new MessageDialog (getParentFrame (),
                         "sorry the bases are read only");
      return;
    } catch (IOException e) {
      new MessageDialog (getParentFrame (),
                         "error while reading: " + e.getMessage ());
      return;
    } catch (OutOfRangeException e) {
      new MessageDialog (getParentFrame (),
                         "out of range exception while reading: " +
                         e.getMessage ());
      return;
    } catch (NoSequenceException e) {
      new MessageDialog (getParentFrame (),
                         "sorry the file did not contain any sequence");
      return;
    } catch (org.biojava.bio.symbol.IllegalSymbolException e) {
      new MessageDialog (getParentFrame (),
                         "sorry - new sequence contains non-IUB base " +
                         "codes");
      return;
    }
  }

  /**
   *  Add a qualifier to the given features.  The qualifier will normally be a
   *  gene name.
   *  @param prefix_string prefix of the new qualifier eg. SPAC
   *  @param start_number The number to start counting at eg. 1 give SPAC0001
   *    for the first CDS
   *  @param increment The amount to increment by at each gene.
   *  @param qualifier_name The name of the qualifier to add
   *  @param tag_complement_names If True a lowercase "c" will be appended to
   *    the new qualifier values on the reverse strand. 
   **/
  private void autoGeneNameHelper (final FeatureVector features_to_name,
                                   final String prefix_string,
                                   final int start_number,
                                   final int increment,
                                   final String qualifier_name,
                                   final boolean tag_complement_names) {
    try {
      entry_group.getActionController ().startAction ();

      int current_number = start_number;

      for (int i = 0 ; i < features_to_name.size () ; ++i) {
        final Feature this_feature = features_to_name.elementAt (i);

        final Key key = this_feature.getKey ();

        if (key.equals ("CDS")) {
          final String number_string;

          if (current_number < 10) {
            number_string = "000" + current_number;
          } else {
            if (current_number < 100) {
              number_string = "00" + current_number;
            } else {
              if (current_number < 1000) {
                number_string = "0" + current_number;
              } else {
                number_string = String.valueOf (current_number);
              }
            }
          }

          try {
            final Qualifier new_qualifier =
              new Qualifier (qualifier_name, prefix_string +
                             number_string +
                             (!tag_complement_names ||
                              this_feature.isForwardFeature () ?
                              "" :
                              "c"));

            this_feature.addQualifierValues (new_qualifier);
          } catch (EntryInformationException e) {
            new MessageDialog (getParentFrame (),
                               "/" + qualifier_name +
                               " not supported by this entry " +
                               "- cannot continue");
            return;
          } catch (ReadOnlyException e) {
            new MessageDialog (getParentFrame (),
                               "one of the features is in a read-only " +
                               "entry - cannot continue");
            return;
          }

          current_number += increment;
        }
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Automatically create gene names for all CDS features in the active
   *  entries.
   **/
  private void autoGeneName () {
    if (!checkForSelectionFeatures (getParentFrame (), getSelection ())) {
      return;
    }

    final FeatureVector features_to_name = getSelection ().getAllFeatures ();

    if (getReadOnlyFeatures (features_to_name).size () > 0) {
      new MessageDialog (getParentFrame (),
                         "one or more of the current features is " +
                         "read-only - cannot continue");
      return;
    }

    final TextDialog prefix_dialog =
      new TextDialog (getParentFrame (),
                      "enter the start characters of the new gene names:",
                      18, "");

    final String prefix_text = prefix_dialog.getText ();

    if (prefix_text == null) {
      return;
    }

    final String prefix_string = prefix_text.trim ();

    if (prefix_string.length () == 0) {
      new MessageDialog (getParentFrame (), "no prefix given");
      return;
    }

    final TextDialog start_number_dialog =
      new TextDialog (getParentFrame (),
                      "start counting at:", 18, "1");

    final String start_number_text = start_number_dialog.getText ();

    if (start_number_text == null) {
      return;
    }

    String start_string = start_number_text.trim ();

    if (start_string.length () == 0) {
      new MessageDialog (getParentFrame (), "no start given");
      return;
    }

    final int start_value;

    try {
      start_value = Integer.valueOf (start_string).intValue ();
    } catch (NumberFormatException e) {
      new MessageDialog (getParentFrame (),
                         "this is not a number: " + start_string);
      return;
    }

    final TextDialog increment_dialog =
      new TextDialog (getParentFrame (),
                      "increment number by:", 18, "1");

    final String increment_text = increment_dialog.getText ();

    if (increment_text == null) {
      return;
    }

    String increment_string = increment_text.trim ();

    if (increment_string.length () == 0) {
      new MessageDialog (getParentFrame (), "no increment given");
      return;
    }

    final int increment_value;

    try {
      increment_value = Integer.valueOf (increment_string).intValue ();
    } catch (NumberFormatException e) {
      new MessageDialog (getParentFrame (),
                         "this is not a number: " + increment_string);
      return;
    }

    final TextDialog qualifier_name_dialog =
      new TextDialog (getParentFrame (),
                      "enter a qualifier name to use",
                      18, "gene");

    final String qualifier_name_text = qualifier_name_dialog.getText ();

    if (qualifier_name_text == null) {
      return;
    }

    final String qualifier_name_string = qualifier_name_text.trim ();

    if (qualifier_name_string.length () == 0) {
      new MessageDialog (getParentFrame (), "no qualifier name given");
      return;
    }

    final YesNoDialog complement_tag_dialog =
      new YesNoDialog (getParentFrame (),
                       "append \"c\" to names of reverse strand features?");

    autoGeneNameHelper (features_to_name,
                        prefix_string, start_value, increment_value,
                        qualifier_name_string,
                        complement_tag_dialog.getResult ());
  }

  /**
   *  Helper function for fixGeneNames().  Add the gene name from the CDS to
   *  neighbouring/overlapping mRNA, intron, exon, gene, 5'UTR and 3'UTR
   *  features.  Warn about inconsistencies.
   *  @return true if all went well, false if fixing should stop immediately
   **/
  private static boolean fixGeneNamesHelper (final JFrame frame,
                                             final EntryGroup entry_group,
                                             final Feature cds_to_fix) {
    if (cds_to_fix.isReadOnly ()) {
      final String message =
        "one or more of the of the selected features are read only " +
        "- cannot continue";
      new MessageDialog (frame, message);
      return false;
    }

    try {
      final Strand cds_to_fix_strand = cds_to_fix.getStrand ();

      Marker cds_start_marker = cds_to_fix.getFirstBaseMarker ();
      Marker cds_end_marker = cds_to_fix.getLastBaseMarker ();

      // move the start one base back and the end one base forward so that
      // when we call getFeaturesInRange() we get the UTRs

      try {
        cds_start_marker = cds_start_marker.moveBy (-1);
      } catch (OutOfRangeException _) {
        // ignore and use the original cds_start_marker
      }

      try {
        cds_end_marker = cds_end_marker.moveBy (1);
      } catch (OutOfRangeException _) {
        // ignore and use the original cds_end_marker
      }

      final Range search_range;

      if (cds_start_marker.getRawPosition () <
          cds_end_marker.getRawPosition ()) {
        search_range =
          new Range (cds_start_marker.getRawPosition (),
                     cds_end_marker.getRawPosition ());
     } else {
       search_range =
         new Range (cds_end_marker.getRawPosition (),
                    cds_start_marker.getRawPosition ());
     }

      final FeatureVector features_in_range =
        entry_group.getFeaturesInRange (search_range);

      final FeatureVector features_to_change =
        new FeatureVector ();

      for (int i = 0 ; i < features_in_range.size () ; ++i) {
        final Feature this_feature = features_in_range.elementAt (i);

        if (this_feature.getStrand () == cds_to_fix_strand &&
            (this_feature.isCDS () ||
             this_feature.getKey ().equals ("mRNA") ||
             this_feature.getKey ().equals ("intron") ||
             this_feature.getKey ().equals ("exon") ||
             this_feature.getKey ().equals ("5'UTR") &&
             (cds_to_fix.getFirstBase () ==
              this_feature.getLastBase () + 1) ||
             this_feature.getKey ().equals ("3'UTR") &&
             (cds_to_fix.getLastBase () + 1 ==
              this_feature.getFirstBase ()) ||
             this_feature.getKey ().equals ("gene"))) {

          if (this_feature.isReadOnly ()) {
            final String message =
              "one or more of the of the overlapping features are read only " +
              "- cannot continue";
            new MessageDialog (frame, message);
            return false;
          }
          features_to_change.add (this_feature);
        }
      }

      final FeatureVector overlapping_cds_features = new FeatureVector ();

      for (int i = 0 ; i < features_to_change.size () ; ++i) {
        final Feature this_test_feature = features_to_change.elementAt (i);

        if (this_test_feature != cds_to_fix &&
            this_test_feature.isCDS ()) {
          overlapping_cds_features.add (this_test_feature);
          features_to_change.remove (this_test_feature);
        }
      }

      if (overlapping_cds_features.size () > 0) {
        final String message =
          "your CDS (" + cds_to_fix.getIDString () +
          ") overlaps " + overlapping_cds_features.size () +
          " other CDS feature" +
          (overlapping_cds_features.size () == 1 ?
           "" : "s") + " - continue?";

        final YesNoDialog dialog =
          new YesNoDialog (frame, message);

        if (!dialog.getResult ()) {
          return false;
        }
      }

      final StringVector gene_names = new StringVector ();

      for (int feature_index = 0 ;
           feature_index < features_to_change.size () ;
           ++feature_index) {
        final Feature test_feature =
          features_to_change.elementAt (feature_index);

        final StringVector test_feature_gene_names =
          test_feature.getValuesOfQualifier ("gene");

        if (test_feature_gene_names != null) {
          for (int test_feature_gene_name_index = 0 ;
               test_feature_gene_name_index < test_feature_gene_names.size ();
               ++test_feature_gene_name_index) {
            final String this_gene_name =
              test_feature_gene_names.elementAt (test_feature_gene_name_index);

            if (!gene_names.contains (this_gene_name)) {
              gene_names.add (this_gene_name);
            }
          }
        }
      }

      if (gene_names.size () == 0) {
        // ignore this feature, but continue with the other features
        return true;
      }

      for (int feature_index = 0 ;
           feature_index < features_to_change.size () ;
           ++feature_index) {
        final Feature this_feature =
          features_to_change.elementAt (feature_index);
        final Qualifier qualifier = new Qualifier ("gene", gene_names);

        this_feature.setQualifier (qualifier);
      }
    } catch (OutOfRangeException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (InvalidRelationException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (EntryInformationException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (ReadOnlyException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }

    return true;
  }

  /**
   *  For each selected CDS, add the gene name from the CDS to
   *  neighbouring/overlapping mRNA, intron, exon, gene, 5'UTR and 3'UTR
   *  features.  Warn about inconsistencies.
   *  @param frame The Frame to use for MessageDialog components.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   **/
  public static void fixGeneNames (final JFrame frame,
                                   final EntryGroup entry_group,
                                   final Selection selection) {
    try {
      entry_group.getActionController ().startAction ();

      final FeatureVector features_to_fix = selection.getAllFeatures ();

      int cds_features_found = 0;

      for (int i = 0 ; i < features_to_fix.size () ; ++i) {
        final Feature selection_feature = features_to_fix.elementAt (i);

        if (selection_feature.isCDS ()) {
          ++cds_features_found;
          if (!fixGeneNamesHelper (frame, entry_group, selection_feature)) {
            return;
          }
        }
      }

      if (cds_features_found == 0) {
        new MessageDialog (frame, "no CDS features selected");
      }
    } finally {
      entry_group.getActionController ().endAction ();
    }
  }

  /**
   *  Return the EntryGroup that was passed to the constructor.
   **/
  private EntryGroup getEntryGroup () {
    return entry_group;
  }

  /**
   *  This method sends an GotoEvent to all the GotoEvent listeners that will
   *  make the first base of the selection visible.
   **/
  private void makeSelectionStartVisible () {
    final GotoEvent new_event =
      new GotoEvent (this, getSelection ().getStartBaseOfSelection ());

    goto_event_source.sendGotoEvent (new_event);
  }

  /**
   *  Return the GotoEventSource object that was passed to the constructor.
   **/
  private GotoEventSource getGotoEventSource () {
    return goto_event_source;
  }

  /**
   *  Returns a Vector containing those features in the given vector of
   *  features which are read only or are in a read only entry.
   **/
  private static FeatureVector
    getReadOnlyFeatures (final FeatureVector features) {
    final FeatureVector return_vector = new FeatureVector ();

    for (int i = 0 ; i < features.size () ; ++i) {
      final Feature this_feature = features.elementAt (i);
      if (this_feature.isReadOnly ()) {
        return_vector.add (this_feature);
      }
    }

    return return_vector;
  }

  /**
   *  The GotoEventSource object that was passed to the constructor.
   **/
  private GotoEventSource goto_event_source = null;

  /**
   *  The EntryGroup object that was passed to the constructor.
   **/
  private EntryGroup entry_group = null;

  /**
   *  The BasePlotGroup object that was passed to the constructor.
   **/
  private BasePlotGroup base_plot_group = null;

  private JMenuItem undo_item = null;
  private JMenuItem edit_feature_item = null;
  private JMenuItem raw_edit_feature_item = null;
  private JMenuItem duplicate_item = null;
  private JMenuItem merge_features_item = null;
  private JMenuItem unmerge_feature_item = null;
  private JMenuItem delete_features_item = null;
  private JMenuItem edit_header_item = null;
  private JMenuItem add_qualifiers_item = null;
  private JMenuItem remove_qualifier_item = null;
  private JMenuItem delete_segments_item = null;
  private JMenuItem delete_introns_item = null;
  private JMenuItem edit_subsequence_item = null;
  private JMenuItem paste_item = null;
  private JMenu move_features_menu = null;
  private JMenu copy_features_menu = null;
  private JMenuItem trim_item = null;
  private JMenuItem trim_to_any_item = null;
  private JMenuItem trim_to_next_item = null;
  private JMenuItem trim_to_next_any_item = null;
  private JMenuItem extend_to_next_stop_item = null;
  private JMenuItem extend_to_prev_stop_item = null;
  private JMenuItem auto_label_item = null;
  private JMenuItem auto_gene_name_item = null;
  private JMenuItem fix_gene_names_item = null;
  private JMenuItem fix_stop_codons_item = null;
  private JMenuItem reverse_complement_item = null;
  private JMenuItem delete_bases_item = null;
  private JMenuItem add_bases_item = null;
  private JMenuItem add_bases_from_file_item = null;
}
