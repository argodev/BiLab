/* ViewMenu.java
 *
 * created: Tue Dec 29 1998
 *
 * This file is part of Artemis
 *
 * Copyright (C) 1998,1999,2000,2002  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/ViewMenu.java,v 1.1 2004/06/09 09:47:58 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.sequence.*;
import uk.ac.sanger.artemis.plot.*;

import uk.ac.sanger.artemis.util.*;
import uk.ac.sanger.artemis.io.InvalidRelationException;
import uk.ac.sanger.artemis.io.Key;
import uk.ac.sanger.artemis.io.InvalidKeyException;
import uk.ac.sanger.artemis.io.Range;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 *  A popup menu with viewing commands.
 *
 *  @author Kim Rutherford
 *  @version $Id: ViewMenu.java,v 1.1 2004/06/09 09:47:58 tjc Exp $
 **/

public class ViewMenu extends SelectionMenu {
  /**
   *  Create a new ViewMenu object.
   *  @param frame The JFrame that owns this JMenu.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   *  @param entry_group The EntryGroup object where new features/entries will
   *    be added.
   *  @param goto_event_source The object that we will call makeBaseVisible ()
   *    on.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   *  @param menu_name The name of the new menu.
   **/
  public ViewMenu (final JFrame frame,
                   final Selection selection,
                   final GotoEventSource goto_event_source,
                   final EntryGroup entry_group,
                   final BasePlotGroup base_plot_group,
                   final String menu_name) {
    super (frame, menu_name, selection);

    this.entry_group = entry_group;
    this.selection = selection;
    this.goto_event_source = goto_event_source;

    this.base_plot_group = base_plot_group;

    plot_features_item = new JMenuItem ("Show Feature Plots");
    plot_features_item.setAccelerator (PLOT_FEATURES_KEY);
    plot_features_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        plotSelectedFeatures (getParentFrame (), getSelection ());
      }
    });

    view_feature_item = new JMenuItem ("View Selected Features");
    view_feature_item.setAccelerator (VIEW_FEATURES_KEY);
    view_feature_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewSelectedFeatures (getParentFrame (), getSelection ());
      }
    });

    view_selection_item = new JMenuItem ("View Selection");
    view_selection_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        new SelectionViewer (getSelection (), entry_group);
      }
    });

    feature_info_item = new JMenuItem ("Show Feature Statistics");
    feature_info_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewSelectedFeatureInfo ();
      }
    });

    view_bases_item = new JMenuItem ("View Bases Of Selection");
    view_bases_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewSelectedBases (true);
      }
    });

    view_bases_as_fasta_item =
      new JMenuItem ("View Bases Of Selection As FASTA");
    view_bases_as_fasta_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewSelectedBases (false);
      }
    });

    view_aa_item = new JMenuItem ("View Amino Acids Of Selection");
    view_aa_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewSelectedAminoAcids (true);
      }
    });

    view_aa_as_fasta_item =
      new JMenuItem ("View Amino Acids Of Selection As FASTA");
    view_aa_as_fasta_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewSelectedAminoAcids (false);
      }
    });

    overview_item = new JMenuItem ("Show Overview");
    overview_item.setAccelerator (OVERVIEW_KEY);
    overview_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        new EntryGroupInfoDisplay (getParentFrame (), entry_group);
      }
    });

    forward_overview_item = new JMenuItem ("Show Forward Strand Overview");
    forward_overview_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        new EntryGroupInfoDisplay (getParentFrame (), entry_group,
                                   EntryGroupInfoDisplay.FORWARD);
      }
    });

    reverse_overview_item = new JMenuItem ("Show Reverse Strand Overview");
    reverse_overview_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        new EntryGroupInfoDisplay (getParentFrame (), entry_group,
                                   EntryGroupInfoDisplay.REVERSE);
      }
    });

    view_cds_item = new JMenuItem ("Show CDS Genes And Products");
    view_cds_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        final FeaturePredicate feature_predicate =
          new FeatureKeyPredicate (Key.CDS);

        final String filter_name =
          "CDS features (filtered from: " +
          getParentFrame ().getTitle () + ")";

        final FilteredEntryGroup filtered_entry_group =
          new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

        final FeatureListFrame feature_list_frame =
          new FeatureListFrame (filter_name,
                                selection, goto_event_source,
                                filtered_entry_group,
                                base_plot_group);

        feature_list_frame.getFeatureList ().setShowGenes (true);
        feature_list_frame.getFeatureList ().setShowProducts (true);

        feature_list_frame.setVisible (true);
      }
    });

    JMenu search_results_menu = null;

    search_results_menu = new JMenu ("Search Results");

    final boolean sanger_options =
      Options.getOptions ().getPropertyTruthValue ("sanger_options");

    final ExternalProgramVector external_programs =
      Options.getOptions ().getExternalPrograms ();

    final StringVector external_program_names = new StringVector ();

    for (int i = 0 ; i < external_programs.size () ; ++i) {
      final ExternalProgram external_program =
        external_programs.elementAt (i);

      final String new_name = external_program.getName ();

      if (!external_program_names.contains (new_name)) {
        external_program_names.add (new_name);
      }
    }

    for (int i = 0 ; i < external_program_names.size () ; ++i) {
      final String external_program_name =
        external_program_names.elementAt (i);

      final JMenuItem new_menu =
        makeSearchResultsMenu (external_program_name, false, sanger_options);
      search_results_menu.add (new_menu);
    }

    if (sanger_options) {
      search_results_menu.addSeparator ();

      for (int i = 0 ; i < external_program_names.size () ; ++i) {
        final String external_program_name =
          external_program_names.elementAt (i);

        final JMenuItem new_menu =
          makeSearchResultsMenu (external_program_name, true, sanger_options);
        search_results_menu.add (new_menu);
      }
    }

    final int MAX_FILTER_FEATURE_COUNT = 10000;

    final JMenu feature_filters_menu = new JMenu ("Feature Filters");

    final JMenuItem bad_start_codons_item =
      new JMenuItem ("Suspicious Start Codons ...");
    bad_start_codons_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showBadStartCodons (getParentFrame (),
                              selection, entry_group, goto_event_source,
                              base_plot_group);
        }
      }
    });

    final JMenuItem bad_stop_codons_item =
      new JMenuItem ("Suspicious Stop Codons ...");
    bad_stop_codons_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showBadStopCodons (getParentFrame (),
                             selection, entry_group, goto_event_source,
                             base_plot_group);
        }
      }
    });

    final JMenuItem stop_codons_in_translation =
      new JMenuItem ("Stop Codons In Translation ...");
    stop_codons_in_translation.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showStopsInTranslation (getParentFrame (),
                                  selection, entry_group, goto_event_source,
                                  base_plot_group);
        }
      }
    });

    final JMenuItem bad_feature_keys_item =
      new JMenuItem ("Non EMBL Keys ...");
    bad_feature_keys_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showNonEMBLKeys (getParentFrame (),
                           selection, entry_group, goto_event_source,
                           base_plot_group);
        }
      }
    });

    final JMenuItem duplicated_keys_item =
      new JMenuItem ("Duplicated Features ...");
    duplicated_keys_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showDuplicatedFeatures (getParentFrame (),
                                  selection, entry_group, goto_event_source,
                                  base_plot_group);
        }
      }
    });

    final JMenuItem overlapping_cds_features_item =
      new JMenuItem ("Overlapping CDS Features ...");
    overlapping_cds_features_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showOverlappingCDSs (getParentFrame (),
                               selection, entry_group, goto_event_source,
                               base_plot_group);
        }
      }
    });

    final JMenuItem same_stop_cds_features_item =
      new JMenuItem ("CDSs Sharing Stop Codons ...");
    same_stop_cds_features_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showFeaturesWithSameStopCodons (getParentFrame (),
                                          selection, entry_group,
                                          goto_event_source,
                                          base_plot_group);
        }
      }
    });

    final JMenuItem missing_qualifier_features_item =
      new JMenuItem ("Features Missing Required Qualifiers ...");
    missing_qualifier_features_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showMissingQualifierFeatures (getParentFrame (),
                                        selection,
                                        entry_group, goto_event_source,
                                        base_plot_group);
        }
      }
    });

    final JMenuItem filter_by_key_item =
      new JMenuItem ("Filter By Key ...");
    filter_by_key_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        if (checkEntryGroupSize (MAX_FILTER_FEATURE_COUNT)) {
          showFilterByKey (getParentFrame (),
                           selection, entry_group, goto_event_source,
                           base_plot_group);
        }
      }
    });

    final JMenuItem filter_by_selection_item =
      new JMenuItem ("Selected Features ...");
    filter_by_selection_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        showFilterBySelection (getParentFrame (),
                               selection, entry_group, goto_event_source,
                               base_plot_group);
      }
    });

    feature_filters_menu.add (bad_start_codons_item);
    feature_filters_menu.add (bad_stop_codons_item);
    feature_filters_menu.add (stop_codons_in_translation);
    feature_filters_menu.add (bad_feature_keys_item);
    feature_filters_menu.add (duplicated_keys_item);
    feature_filters_menu.add (overlapping_cds_features_item);
    feature_filters_menu.add (same_stop_cds_features_item);
    feature_filters_menu.add (missing_qualifier_features_item);
    feature_filters_menu.addSeparator ();
    feature_filters_menu.add (filter_by_key_item);
    feature_filters_menu.add (filter_by_selection_item);

    add (view_feature_item);
    add (view_selection_item);
    addSeparator ();
    if (search_results_menu != null) {
      add (search_results_menu);
    }
    add (view_cds_item);
    add (feature_filters_menu);
    addSeparator ();
    add (overview_item);
    add (forward_overview_item);
    add (reverse_overview_item);
    addSeparator ();
    add (view_bases_item);
    add (view_bases_as_fasta_item);
    add (view_aa_item);
    add (view_aa_as_fasta_item);
    addSeparator ();
    add (feature_info_item);
    add (plot_features_item);
  }

  /**
   *  Create a new ViewMenu object.
   *  @param entry_edit The EntryEdit that owns this JMenu.
   *  @param selection The Selection that the commands in the menu will
   *    operate on.
   *  @param entry_group The EntryGroup object where new features/entries will
   *    be added.
   *  @param goto_event_source The object that we will call makeBaseVisible ()
   *    on.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public ViewMenu (final JFrame frame,
                   final Selection selection,
                   final GotoEventSource goto_event_source,
                   final EntryGroup entry_group,
                   final BasePlotGroup base_plot_group) {
    this (frame, selection, goto_event_source, entry_group,
          base_plot_group, "View");
  }

  /**
   *  The shortcut for Show Feature Plots.
   **/
  final static KeyStroke PLOT_FEATURES_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_W, InputEvent.CTRL_MASK);

  final static public int PLOT_FEATURES_KEY_CODE = KeyEvent.VK_W;

  /**
   *  The shortcut for View Selected Features.
   **/
  final static KeyStroke VIEW_FEATURES_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_V, InputEvent.CTRL_MASK);

  final static public int VIEW_FEATURES_KEY_CODE = KeyEvent.VK_V;

  /**
   *  The shortcut for Show Overview.
   **/
  final static KeyStroke OVERVIEW_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_O, InputEvent.CTRL_MASK);

  final static public int OVERVIEW_KEY_CODE = KeyEvent.VK_O;

  /**
   *  The shortcut for View FASTA in browser.
   **/
  final static KeyStroke FASTA_IN_BROWSER_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_F, InputEvent.CTRL_MASK);

  final static public int FASTA_IN_BROWSER_KEY_CODE = KeyEvent.VK_F;

  /**
   *  The shortcut for View FASTA.
   **/
  final static KeyStroke VIEW_FASTA_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_R, InputEvent.CTRL_MASK);

  final static public int VIEW_FASTA_KEY_CODE = KeyEvent.VK_R;

  /**
   *  The shortcut for View BLASTP in browser.
   **/
  final static KeyStroke BLASTP_IN_BROWSER_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_B, InputEvent.CTRL_MASK);

  final static public int BLASTP_IN_BROWSER_KEY_CODE = KeyEvent.VK_B;

  /**
   *  The shortcut for View BLASTP.
   **/
  final static KeyStroke VIEW_BLASTP_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_BACK_QUOTE , InputEvent.CTRL_MASK);

  final static public int VIEW_BLASTP_KEY_CODE = KeyEvent.VK_BACK_QUOTE;

  /**
   *  The shortcut for View HTH.
   **/
  final static KeyStroke VIEW_HTH_KEY =
    KeyStroke.getKeyStroke (KeyEvent.VK_H, InputEvent.CTRL_MASK);

  final static public int VIEW_HTH_KEY_CODE = KeyEvent.VK_H;

  /**
   *  Make a JMenuItem for viewing the results of running the given program.
   *  @param send_to_browser if true the results should be sent straight to
   *    the web browser rather than using a SearchResultViewer object.
   *  @param sanger_options true if the sanger_options is set to true in the
   *    options file.
   **/
  private JMenuItem makeSearchResultsMenu (final String program_name,
                                          final boolean send_to_browser,
                                          final boolean sanger_options) {
    final String new_menu_name;

    if (send_to_browser) {
      new_menu_name = program_name + " results (in browser)";
    } else {
      new_menu_name = program_name + " results";
    }

    final JMenuItem new_menu;

    if ((sanger_options && send_to_browser || !sanger_options)
        && program_name.equals ("fasta")) {
      new_menu = new JMenuItem (new_menu_name);
      new_menu.setAccelerator (FASTA_IN_BROWSER_KEY);
    } else {
      if ((sanger_options && send_to_browser || !sanger_options)
          && program_name.equals ("blastp")) {
        new_menu = new JMenuItem (new_menu_name);
        new_menu.setAccelerator (BLASTP_IN_BROWSER_KEY);
      } else {
        if (program_name.equals ("fasta")) {
          new_menu = new JMenuItem (new_menu_name);
          new_menu.setAccelerator (VIEW_FASTA_KEY);
        } else {
          if (program_name.equals ("blastp")) {
            new_menu = new JMenuItem (new_menu_name);
            new_menu.setAccelerator (VIEW_BLASTP_KEY);
          } else {
            if (program_name.equals ("hth")) {
              new_menu = new JMenuItem (new_menu_name);
              new_menu.setAccelerator (VIEW_HTH_KEY);
            } else {
              new_menu = new JMenuItem (new_menu_name);
            }
          }
        }
      }
    }

    new_menu.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        viewExternalResults (getParentFrame (), getSelection (),
                             program_name, send_to_browser);
      }
    });

    return new_menu;
  }

  /**
   *  Popup a FeatureListFrame containing the non-pseudo CDS features that
   *  have invalid start codons.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showBadStartCodons (final JFrame parent_frame,
                                         final Selection selection,
                                         final EntryGroup entry_group,
                                         final GotoEventSource goto_source,
                                         final BasePlotGroup
                                           base_plot_group) {
    final FeatureKeyQualifierPredicate cds_predicate =
      new FeatureKeyQualifierPredicate (Key.CDS, "pseudo", false);

    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature feature) {
          if (!cds_predicate.testPredicate (feature)) {
            return false;
          }

          if (feature.hasValidStartCodon ()) {
            return false;
          } else {
            return true;
          }
        }
      };

    final String filter_name =
      "CDS features with suspicious start codons (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing the non-pseudo CDS features that
   *  have invalid stop codons.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showBadStopCodons (final JFrame parent_frame,
                                        final Selection selection,
                                        final EntryGroup entry_group,
                                        final GotoEventSource goto_source,
                                        final BasePlotGroup
                                          base_plot_group) {
    final FeatureKeyQualifierPredicate cds_predicate =
      new FeatureKeyQualifierPredicate (Key.CDS, "pseudo", false);

    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature feature) {
          if (!cds_predicate.testPredicate (feature)) {
            return false;
          }

          if (feature.hasValidStopCodon ()) {
            return false;
          } else {
            return true;
          }
        }
      };

    final String filter_name =
      "CDS features with suspicious stop codons (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing the non-pseudo CDS features that
   *  contain a stop codon in the translation.
   *  @param parent_frame The parent Frame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this Menu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showStopsInTranslation (final Frame parent_frame,
                                             final Selection selection,
                                             final EntryGroup entry_group,
                                             final GotoEventSource goto_source,
                                             final BasePlotGroup
                                               base_plot_group) {
    final FeatureKeyQualifierPredicate cds_predicate =
      new FeatureKeyQualifierPredicate (Key.CDS, "pseudo", false);

    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature feature) {
          if (!cds_predicate.testPredicate (feature)) {
            return false;
          }

          final AminoAcidSequence amino_acids = feature.getTranslation ();

          if (amino_acids.containsStopCodon ()) {
            return true;
          } else {
            return false;
          }
        }
      };

    final String filter_name =
      "CDS features with stop codon(s) in translation (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing the features that have non-EMBL keys.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showNonEMBLKeys (final JFrame parent_frame,
                                      final Selection selection,
                                      final EntryGroup entry_group,
                                      final GotoEventSource goto_source,
                                      final BasePlotGroup
                                        base_plot_group) {
    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature feature) {
          if (feature.hasValidEMBLKey ()) {
            return false;
          } else {
            return true;
          }
        }
      };

    final String filter_name =
      "features with a non-EMBL key (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }


  /**
   *  Popup a FeatureListFrame containing the features that have the same key
   *  and location as another features (ie. duplicates).
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showDuplicatedFeatures (final JFrame parent_frame,
                                             final Selection selection,
                                             final EntryGroup entry_group,
                                             final GotoEventSource goto_source,
                                             final BasePlotGroup
                                               base_plot_group) {
    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature feature) {
          final Entry feature_entry = feature.getEntry ();

          final int feature_index = feature_entry.indexOf (feature);

          if (feature_index + 1 == feature_entry.getFeatureCount ()) {
            // last in the Entry
            return false;
          }

          final Feature next_feature =
            feature_entry.getFeature (feature_index + 1);

          if (feature.getKey ().equals (next_feature.getKey ()) &&
              feature.getLocation ().equals (next_feature.getLocation ())) {
            return true;
          } else {
            return false;
          }
        }
      };

    final String filter_name =
      "duplicated Features (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing those CDS features that overlap with
   *  the next feature.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showOverlappingCDSs (final JFrame parent_frame,
                                          final Selection selection,
                                          final EntryGroup entry_group,
                                          final GotoEventSource goto_source,
                                          final BasePlotGroup
                                            base_plot_group) {
    final FeatureKeyPredicate cds_predicate =
      new FeatureKeyPredicate (Key.CDS);

    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature test_feature) {
          if (!cds_predicate.testPredicate (test_feature)) {
            return false;
          }

          final Range feature_range = test_feature.getMaxRawRange ();

          final FeatureVector overlapping_features;

          try {
            overlapping_features =
              entry_group.getFeaturesInRange (feature_range);
          } catch (OutOfRangeException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          }

          for (int i = 0 ; i < overlapping_features.size () ; ++i) {
            final Feature current_feature = overlapping_features.elementAt (i);

            if (current_feature != test_feature &&
                cds_predicate.testPredicate (current_feature)) {
              return true;
            }
          }

          return false;
        }
      };

    final String filter_name =
      "overlapping CDS features (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing those CDS features that overlap with
   *  the next feature.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void
    showFeaturesWithSameStopCodons (final JFrame parent_frame,
                                    final Selection selection,
                                    final EntryGroup entry_group,
                                    final GotoEventSource goto_source,
                                    final BasePlotGroup base_plot_group) {
    final FeatureKeyPredicate cds_predicate =
      new FeatureKeyPredicate (Key.CDS);

    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature test_feature) {
          if (!cds_predicate.testPredicate (test_feature)) {
            return false;
          }

          final Range feature_range = test_feature.getMaxRawRange ();

          final FeatureVector overlapping_features;

          try {
            overlapping_features =
              entry_group.getFeaturesInRange (feature_range);
          } catch (OutOfRangeException e) {
            throw new Error ("internal error - unexpected exception: " + e);
          }

          for (int i = 0 ; i < overlapping_features.size () ; ++i) {
            final Feature current_feature = overlapping_features.elementAt (i);

            if (current_feature == test_feature) {
              continue;
            }

            if (current_feature != test_feature &&
                cds_predicate.testPredicate (current_feature) &&
                (current_feature.getLastBase () ==
                 test_feature.getLastBase ()) &&
                (current_feature.getSegments ().lastElement ().getFrameID () ==
                 test_feature.getSegments ().lastElement ().getFrameID ())) {
              return true;
            }
          }

          return false;
        }
      };

    final String filter_name =
      "CDS features with the same stop codon as another (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing the features that are missing
   *  required EMBL qualifiers.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showMissingQualifierFeatures (final JFrame parent_frame,
                                                   final Selection selection,
                                                   final EntryGroup
                                                     entry_group,
                                                   final GotoEventSource
                                                     goto_source,
                                                   final BasePlotGroup
                                                     base_plot_group) {
    final FeaturePredicate feature_predicate =
      new FeaturePredicate () {
        public boolean testPredicate (final Feature feature) {
          if (feature.hasRequiredQualifiers ()) {
            return false;
          } else {
            return true;
          }
        }
      };

    final String filter_name =
      "features that are missing a required EMBL " +
      "qualifier (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, feature_predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing only those features that have the
   *  key choosen by the user.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showFilterByKey (final JFrame parent_frame,
                                      final Selection selection,
                                      final EntryGroup entry_group,
                                      final GotoEventSource goto_source,
                                      final BasePlotGroup base_plot_group) {
    final KeyChooser key_chooser;

    key_chooser = new KeyChooser (Options.getArtemisEntryInformation (),
                                  new Key ("misc_feature"));

    key_chooser.getKeyChoice ().addItemListener (new ItemListener () {
      public void itemStateChanged (ItemEvent e) {
        if (e.getStateChange () == ItemEvent.SELECTED) {
          showFilterByKeyHelper (parent_frame,
                                 key_chooser.getKeyChoice ().getSelectedItem (),
                                 selection, entry_group, goto_source,
                                 base_plot_group);
          key_chooser.setVisible (false);
          key_chooser.dispose ();
        }
      }
    });

    key_chooser.getOKButton ().addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent _) {
        showFilterByKeyHelper (parent_frame,
                               key_chooser.getKeyChoice ().getSelectedItem (),
                               selection, entry_group, goto_source,
                               base_plot_group);
        key_chooser.setVisible (false);
        key_chooser.dispose ();
      }
    });

    key_chooser.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing only those features that have the
   *  key choosen by the user.
   *  @param parent_frame The parent JFrame.
   *  @param key The key to use in the filter.
   *  @param selection The Selection to pass to the FeatureList.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showFilterByKeyHelper (final JFrame parent_frame,
                                            final Key key,
                                            final Selection selection,
                                            final EntryGroup entry_group,
                                            final GotoEventSource
                                              goto_source,
                                            final BasePlotGroup
                                              base_plot_group) {
    final String filter_name =
      "features with key: " + key + " (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group,
                              new FeatureKeyPredicate (key), filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  Popup a FeatureListFrame containing only those features that are
   *  currently in the Selection.
   *  @param parent_frame The parent JFrame.
   *  @param selection The Selection to pass to the FeatureList and to use to
   *    filter the entry_group.
   *  @param entry_group The EntryGroup to pass to the FilteredEntryGroup.
   *  @param goto_source The GotoEventSource to pass to the FeatureList.
   *  @param base_plot_group The BasePlotGroup associated with this JMenu -
   *    needed to call getCodonUsageAlgorithm()
   **/
  public static void showFilterBySelection (final JFrame parent_frame,
                                            final Selection selection,
                                            final EntryGroup entry_group,
                                            final GotoEventSource
                                              goto_source,
                                            final BasePlotGroup
                                              base_plot_group) {
    final FeaturePredicate predicate =
      new FeatureFromVectorPredicate (selection.getAllFeatures ());

    final String filter_name =
      "features from the selection (filtered from: " +
      parent_frame.getTitle () + ")";

    final FilteredEntryGroup filtered_entry_group =
      new FilteredEntryGroup (entry_group, predicate, filter_name);

    final FeatureListFrame feature_list_frame =
      new FeatureListFrame (filter_name,
                            selection, goto_source, filtered_entry_group,
                            base_plot_group);

    feature_list_frame.setVisible (true);
  }

  /**
   *  viewSelectedFeatures(), plotSelectedFeatures() etc. will only show this
   *  many features.
   **/
  private static final int MAXIMUM_SELECTED_FEATURES = 25;

  /**
   *  Open a view window for each of the selected features.  The viewer will
   *  listen for feature change events and update itself.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the features to merge.
   **/
  static void viewSelectedFeatures (final JFrame frame,
                                    final Selection selection) {
    final FeatureVector features_to_view = selection.getAllFeatures ();

    if (features_to_view.size () > MAXIMUM_SELECTED_FEATURES) {
      new MessageDialog (frame, "warning: only viewing the first " +
                         MAXIMUM_SELECTED_FEATURES + " selected features");
    }

    for (int i = 0 ;
         i < features_to_view.size () && i < MAXIMUM_SELECTED_FEATURES ;
         ++i) {
      final Feature selection_feature = features_to_view.elementAt (i);

      new FeatureViewer (selection_feature);
    }
  }

  /**
   *  Open an plot viewing window for each of the selected features.  The plot
   *  viewer will listen for feature change events and update itself.
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the features to plot.
   **/
  static void plotSelectedFeatures (final JFrame frame,
                                    final Selection selection) {
    final FeatureVector features_to_plot = selection.getAllFeatures ();

    if (features_to_plot.size () > MAXIMUM_SELECTED_FEATURES) {
      new MessageDialog (frame, "warning: only showing plots for the first " +
                         MAXIMUM_SELECTED_FEATURES + " selected features");
    }

    for (int i = 0 ;
         i < features_to_plot.size () && i < MAXIMUM_SELECTED_FEATURES ;
         ++i) {
      final Feature selection_feature = features_to_plot.elementAt (i);

      new FeaturePlotGroup (selection_feature);
    }
  }

  /**
   *  Show the output file from an external program (like fasta) for the
   *  selected Feature objects.  The name of the file to read is stored in a
   *  feature qualifier.  The qualifier used is the program name plus "_file".
   *  @param frame The JFrame to use for MessageDialog components.
   *  @param selection The Selection containing the features to merge.
   *  @param send_to_browser if true the results should be sent straight to
   *    the web browser rather than using a SearchResultViewer object.
   **/
  static void viewExternalResults (final JFrame frame,
                                   final Selection selection,
                                   final String program_name,
                                   final boolean send_to_browser) {
    final FeatureVector features_to_view = selection.getAllFeatures ();

    if (features_to_view.size () > MAXIMUM_SELECTED_FEATURES) {
      new MessageDialog (frame, "warning: only viewing results from " +
                         "the first " + MAXIMUM_SELECTED_FEATURES +
                         " selected features");
    }

    for (int i = 0 ;
         i < features_to_view.size () && i < MAXIMUM_SELECTED_FEATURES ;
         ++i) {
      final Feature this_feature = features_to_view.elementAt (i);

      String qualifier_value;

      try {
        qualifier_value =
          this_feature.getValueOfQualifier (program_name + "_file");
      } catch (InvalidRelationException e) {
        qualifier_value = null;
      }

      String file_name = qualifier_value;

      if (file_name == null || file_name.length () == 0) {
        new MessageDialog (frame,
                           "Message",
                           "No " + program_name + " results for " +
                           this_feature.getIDString ());

        continue;
      }

      // remove the program name string (if any) from the qualifier, so that
      // we can try to read the file with and without the prefix.
      if (file_name.startsWith (program_name + File.separatorChar)) {
        file_name = file_name.substring (program_name.length () + 1);
      }

      Document root_document = this_feature.getEntry ().getRootDocument ();

      if (root_document == null) {
        root_document = new FileDocument (new File ("."));
      }

      try {
        Document document = null;

        // try the current directory with the program name added:
        // ./fasta/abc.seq.00001.out
        final File dir_name = new File (program_name);

        final Document [] possible_documents =
          new Document [] {
            root_document.append (program_name).append (file_name),
            root_document.append (file_name),
            new FileDocument (new File (file_name)),
            new FileDocument (dir_name).append (file_name),
          };

        for (int possible_document_index = 0 ;
             possible_document_index < possible_documents.length ;
             ++possible_document_index) {

          final Document this_document =
            possible_documents[possible_document_index];

          if (this_document.readable ()) {
            document = this_document;
            break;
          } else {
            final File gzip_file =
              new File (this_document.toString () + ".gz");
            final Document gzip_document =
              new FileDocument (gzip_file);

            if (gzip_document.readable ()) {
              document = gzip_document;
              break;
            }
          }
        }

        if (document == null) {
          final String message_string =
            "No " + program_name + " results for " +
            this_feature.getIDString () + " (file not found: " +
            qualifier_value + ")";

          new MessageDialog (frame, message_string);

          continue;
        }

        if (send_to_browser) {
          SearchResultViewer.sendToBrowser (document.toString ());
        } else {
          new SearchResultViewer (program_name + " results for " +
                                  this_feature.getIDString () + " from " +
                                  document,
                                  document);
        }
      } catch (ExternalProgramException e) {
        new MessageDialog (frame,
                           "error while open results file: " + e);
      } catch (IOException e) {
        new MessageDialog (frame,
                           "error while open results file: " + e);
      }
    }
  }

  /**
   *  Open a FeatureInfo component for each of the selected features.  The
   *  new component will listen for feature change events and update itself.
   **/
  private void viewSelectedFeatureInfo () {
    final FeatureVector features_to_view = getSelection ().getAllFeatures ();

    if (features_to_view.size () > MAXIMUM_SELECTED_FEATURES) {
      new MessageDialog (getParentFrame (),
                         "warning: only viewing the statistics for " +
                         "the first " + MAXIMUM_SELECTED_FEATURES +
                         " selected features");
    }

    for (int i = 0 ;
         i < features_to_view.size () && i < MAXIMUM_SELECTED_FEATURES ;
         ++i) {
      final Feature selection_feature = features_to_view.elementAt (i);

      new FeatureInfo (selection_feature,
                       base_plot_group.getCodonUsageAlgorithm ());
    }
  }

  /**
   *  View the bases of the selected features.  This creates a
   *  FeatureBaseViewer for each feature.
   *  @param include_numbers If true then the sequence will be numbered
   *    (every second line of the display will be numbers rather than
   *    sequence).
   **/
  private void viewSelectedBases (final boolean include_numbers) {
    if (getSelection ().isEmpty ()) {
      new MessageDialog (getParentFrame (), "Nothing selected");
      return;
    }

    final MarkerRange range = selection.getMarkerRange ();

    if (range == null) {
      final FeatureVector features_to_view = getSelection ().getAllFeatures ();

      if (features_to_view.size () > MAXIMUM_SELECTED_FEATURES) {
        new MessageDialog (getParentFrame (),
                           "waning: only viewing bases for " +
                           "the first " + MAXIMUM_SELECTED_FEATURES +
                           " selected features");
      }

      for (int i = 0 ;
           i < features_to_view.size () && i < MAXIMUM_SELECTED_FEATURES ;
           ++i) {
        final Feature this_feature = features_to_view.elementAt (i);

        new FeatureBaseViewer (this_feature, include_numbers);
      }
    } else {
      final SequenceViewer sequence_viewer =
        new SequenceViewer ("Selected bases", include_numbers);

      final String bases = getSelection ().getSelectionText ();
      sequence_viewer.setSequence (null, bases);
    }
  }

  /**
   *  View the bases of the selected CDS features.  This creates a
   *  FeatureBaseViewer for each CDS feature.
   *  @param include_numbers If true then the amino acids will be numbered
   *    (every second line of the display will be numbers rather than
   *    sequence).
   **/
  private void viewSelectedAminoAcids (final boolean include_numbers) {
    if (getSelection ().isEmpty ()) {
      new MessageDialog (getParentFrame (), "Nothing selected");
      return;
    }

    final MarkerRange range = selection.getMarkerRange ();

    if (range == null) {
      final FeatureVector features_to_view = getSelection ().getAllFeatures ();

      if (features_to_view.size () > MAXIMUM_SELECTED_FEATURES) {
        new MessageDialog (getParentFrame (),
                           "warning: only viewing amino acids for " +
                           "the first " + MAXIMUM_SELECTED_FEATURES +
                           " selected features");
      }

      for (int i = 0 ;
           i < features_to_view.size () && i < MAXIMUM_SELECTED_FEATURES ;
           ++i) {
        final Feature this_feature = features_to_view.elementAt (i);

        new FeatureAminoAcidViewer (this_feature, include_numbers);
      }
    } else {
      final SequenceViewer sequence_viewer =
        new SequenceViewer ("Selected bases (translated)", include_numbers);

      final String bases = getSelection ().getSelectionText ();

      final AminoAcidSequence amino_acids =
        AminoAcidSequence.getTranslation (bases, true);

      sequence_viewer.setSequence (null, amino_acids.toString ());
    }
  }

  /**
   *  Check that the number of features in the EntryGroup is less than the
   *  given number.  If it is less return true.  If not then popup a
   *  YesNoDialog asking for confirmation and return true if and only if the
   *  user chooses yes.
   **/
  private boolean checkEntryGroupSize (final int max_size) {
    final int feature_count = getEntryGroup ().getAllFeaturesCount ();

    if (feature_count < max_size) {
      return true;
    } else {
      final YesNoDialog dialog =
        new YesNoDialog (getParentFrame (),
                         "there are " + feature_count + " features in the " +
                         "active entries - continue?");

      return dialog.getResult ();
    }
  }

  /**
   *  Return the EntryGroup that was passed to the constructor.
   **/
  private EntryGroup getEntryGroup () {
    return entry_group;
  }

  private JMenuItem feature_info_item = null;
  private JMenuItem plot_features_item = null;
  private JMenuItem view_feature_item = null;
  private JMenuItem view_selection_item = null;
  private JMenuItem view_fasta_item = null;
  private JMenuItem view_bases_item = null;
  private JMenuItem view_bases_as_fasta_item = null;
  private JMenuItem view_aa_item = null;
  private JMenuItem view_aa_as_fasta_item = null;
  private JMenuItem overview_item = null;
  private JMenuItem forward_overview_item = null;
  private JMenuItem reverse_overview_item = null;
  private JMenuItem view_cds_item = null;

  /**
   *  The EntryGroup that was passed to the constructor.
   **/
  private EntryGroup entry_group = null;

  /**
   *  The Selection that was passed to the constructor.
   **/
  private Selection selection = null;

  /**
   *  The GotoEventSource that was passed to the constructor.
   **/
  private GotoEventSource goto_event_source = null;

  private BasePlotGroup base_plot_group;
}
