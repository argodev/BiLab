/* GraphMenu.java
 *
 * created: Tue Sep 18 2001
 *
 * This file is part of Artemis
 *
 * Copyright (C) 2001  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/GraphMenu.java,v 1.1 2004/06/09 09:46:57 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.plot.*;
import uk.ac.sanger.artemis.sequence.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.*;

/**
 *  This menu controls one particular BasePlotGroup.
 *
 *  @author Kim Rutherford <kmr@sanger.ac.uk>
 *  @version $Id: GraphMenu.java,v 1.1 2004/06/09 09:46:57 tjc Exp $
 **/

public class GraphMenu extends JMenu {
  /**
   *  Create a new GraphMenu object and all it's menu items.
   *  @param frame The JFrame that owns this JMenu.
   *  @param entry_group The EntryGroup containing the sequence to plot.
   *  @param base_plot_group The BasePlotGroup that this menu controls.
   *  @param view_menu This ViewMenu is updated when a usage plot is added.
   *  @param add_menu This AddMenu is updated when a usage plot is added.
   *  @param menu_name The name of the new menu.
   **/
  public GraphMenu (final JFrame frame,
                    final EntryGroup entry_group,
                    final BasePlotGroup base_plot_group,
                    final FeatureDisplay feature_display,
                    final String menu_name) {
    super (menu_name);
    this.frame = frame;
    this.entry_group = entry_group;
    this.base_plot_group = base_plot_group;
    this.view_menu = view_menu;
    this.add_menu = add_menu;
    this.feature_display = feature_display;

    final BaseAlgorithm [] orig_algorithms =
      base_plot_group.getPlotAlgorithms ();

    final JMenuItem hide_all_graphs_item = new JMenuItem ("Hide All Graphs");
    hide_all_graphs_item.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent event) {
        final BaseAlgorithm [] current_algorithms =
          base_plot_group.getPlotAlgorithms ();

        for (int i = 0 ; i < current_algorithms.length ; ++i) {
          final BaseAlgorithm this_algorithm = current_algorithms[i];

          base_plot_group.setVisibleByAlgorithm (this_algorithm, false);

          final JCheckBoxMenuItem this_menu_item =
            (JCheckBoxMenuItem) algorithm_menu_items.elementAt (i);

          this_menu_item.setState (false);
        }
        if (getParent () != null) {
          // XXX change to revalidate().
          frame.validate ();
        }
      }
    });
    add (hide_all_graphs_item);

    addSeparator ();

    if (Options.readWritePossible ()) {
      final JMenuItem usage_plot_item = new JMenuItem ("Add Usage Plots ...");
      usage_plot_item.addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent event) {
          addUsagePlot ();
        }
      });
      add (usage_plot_item);

      final JMenuItem user_plot_item = new JMenuItem ("Add User Plot ...");
      user_plot_item.addActionListener (new ActionListener () {
        public void actionPerformed (ActionEvent event) {
          addUserPlot ();
        }
      });

      add (user_plot_item);

      addSeparator ();
    }

    for (int i = 0 ; i < orig_algorithms.length ; ++i) {
      final BaseAlgorithm this_algorithm = orig_algorithms[i];

      addAlgorithm (this_algorithm, false);
    }

    if (Options.getOptions ().getProperty ("codon_usage_file") != null) {
      final String codon_usage_file_name =
        Options.getOptions ().getProperty ("codon_usage_file");

      try {
        addUsagePlot (new File (codon_usage_file_name), true, false);
        addUsagePlot (new File (codon_usage_file_name), false, false);

        if (getParent () != null) {
          // XXX change to revalidate().
          frame.validate ();
        }
      } catch (IOException e) {
        new MessageDialog (frame, "error while reading usage data: " + e);
      }
    }
  }

  /**
   *  Create a new GraphMenu object and all it's menu items.
   *  @param frame The JFrame that owns this JMenu.
   *  @param entry_group The EntryGroup containing the sequence to plot.
   *  @param base_plot_group The BasePlotGroup that this menu controls.
   *  @param view_menu This ViewMenu is updated when a usage plot is added.
   *  @param add_menu This AddMenu is updated when a usage plot is added.
   *  @param menu_name The name of the new menu.
   **/
  public GraphMenu (final JFrame frame,
                    final EntryGroup entry_group,
                    final BasePlotGroup base_plot_group,
                    final FeatureDisplay feature_display) {
    this (frame, entry_group, base_plot_group, feature_display, "Graph");
  }

  /**
   *  Add a menu item for the given algorithm to the Display menu.
   *  @param is_visible The JCheckBoxMenuItem starts in the true state if and
   *    only if this is true.
   *  @return The new JCheckBoxMenuItem
   **/
  public JCheckBoxMenuItem addAlgorithm (final BaseAlgorithm algorithm,
                                        final boolean is_visible) {

    final JCheckBoxMenuItem new_item =
      new JCheckBoxMenuItem (algorithm.getAlgorithmName ());

    new_item.setState (is_visible);

    new_item.addItemListener (new ItemListener () {
      public void itemStateChanged(ItemEvent event) {
        base_plot_group.setVisibleByAlgorithm (algorithm,
                                               new_item.getState ());
      }
    });

    add (new_item);

    algorithm_menu_items.addElement (new_item);

    return new_item;
  }

  /**
   *  Ask the user for a file name, then read the codon usage data from that
   *  file, then make and add forward and a reverse BasePlot component using
   *  the data.
   **/
  public void addUsagePlot () {
    final JFrame frame = Utilities.getComponentFrame (base_plot_group);

    final StickyFileChooser dialog = new StickyFileChooser ();

    dialog.setDialogTitle ("Select a codon usage data file name ...");
    dialog.setDialogType (JFileChooser.OPEN_DIALOG);

    final int status = dialog.showOpenDialog (frame);

    if (status != JFileChooser.APPROVE_OPTION ||
        dialog.getSelectedFile () == null) {
      return;
    }

    final File file =
      new File (dialog.getCurrentDirectory (),
                dialog.getSelectedFile ().getName ());

    if (file.length () != 0) {
      try {
        final BasePlot new_forward_plot =
          addUsagePlot (file, true, true);
        final BasePlot new_reverse_plot =
          addUsagePlot (file, false, true);

        final Algorithm forward_algorithm = new_forward_plot.getAlgorithm ();
        final Algorithm reverse_algorithm = new_reverse_plot.getAlgorithm ();

        base_plot_group.setVisibleByAlgorithm (forward_algorithm, true);
        base_plot_group.setVisibleByAlgorithm (reverse_algorithm, true);
      } catch (IOException e) {
        new MessageDialog (Utilities.getComponentFrame (base_plot_group),
                           "error while reading usage data: " + e);
      }
    }
  }

  /**
   *  Read the codon usage data from the given File, then make and add a
   *  BasePlot component using the data.
   *  @param use_forward_strand The plot will be a forward plot if and only if
   *    this is true.
   *  @param is_visible The plot will start off visible if and only if this is
   *    true.
   *  @return The BasePlot that was added.
   **/
  private BasePlot addUsagePlot (final File codon_usage_file,
                                 final boolean use_forward_strand,
                                 final boolean is_visible)
      throws IOException {
    final CodonUsageAlgorithm codon_usage_algorithm;

    if (use_forward_strand) {
      final Strand forward_strand =
        entry_group.getBases ().getForwardStrand ();

      final CodonUsageWeight usage_weights =
        new CodonUsageWeight (codon_usage_file, forward_strand);

      codon_usage_algorithm =
        new CodonUsageAlgorithm (forward_strand, usage_weights);
    } else {
      final Strand backward_strand =
        entry_group.getBases ().getReverseStrand ();

      final CodonUsageWeight usage_weights =
        new CodonUsageWeight (codon_usage_file, backward_strand);

      codon_usage_algorithm =
        new CodonUsageAlgorithm (backward_strand, usage_weights);
    }

    addAlgorithm (codon_usage_algorithm, is_visible);

    final BasePlot new_plot =
      base_plot_group.addAlgorithm (codon_usage_algorithm);

    base_plot_group.setVisibleByAlgorithm (codon_usage_algorithm, is_visible);

    // XXX hack to force the BasePlot to initialise
    final DisplayAdjustmentEvent event =
      new DisplayAdjustmentEvent (this,
                                  feature_display.getFirstVisibleForwardBase (),
                                  feature_display.getLastVisibleForwardBase (),
                                  feature_display.getMaxVisibleBases (),
                                  feature_display.getScaleValue (),
                                  feature_display.getScaleFactor (),
                                  feature_display.isRevCompDisplay (),
                                  DisplayAdjustmentEvent.ALL_CHANGE_ADJUST_EVENT);

    base_plot_group.displayAdjustmentValueChanged (event);

    return new_plot;
  }

  /**
   *  Add a UserDataAlgorithm to the display.
   **/
  private void addUserPlot () {
    final JFrame frame = Utilities.getComponentFrame (base_plot_group);

    final StickyFileChooser dialog = new StickyFileChooser ();

    dialog.setDialogTitle ("Select a data file name ...");
    dialog.setDialogType (JFileChooser.OPEN_DIALOG);

    final int status = dialog.showOpenDialog (frame);

    if (status != JFileChooser.APPROVE_OPTION ||
        dialog.getSelectedFile () == null) {
      return;
    }

    final File file =
      new File (dialog.getCurrentDirectory (),
                dialog.getSelectedFile ().getName ());

    if (file.length () != 0) {
      final uk.ac.sanger.artemis.util.Document document =
        new uk.ac.sanger.artemis.util.FileDocument (file);

      final Strand forward_strand =
        getEntryGroup ().getBases ().getForwardStrand ();

      try {
        final UserDataAlgorithm new_algorithm =
          new UserDataAlgorithm (forward_strand, document);

        final BasePlot new_base_plot =
          base_plot_group.addAlgorithm (new_algorithm);

        base_plot_group.setVisibleByAlgorithm (new_algorithm, true);

        addAlgorithm (new_algorithm, true);

        // XXX hack to force the BasePlot to initialise
        final DisplayAdjustmentEvent event =
          new DisplayAdjustmentEvent (this,
                                      feature_display.getFirstVisibleForwardBase (),
                                      feature_display.getLastVisibleForwardBase (),
                                      feature_display.getMaxVisibleBases (),
                                      feature_display.getScaleValue (),
                                      feature_display.getScaleFactor (),
                                      feature_display.isRevCompDisplay (),
                                      DisplayAdjustmentEvent.ALL_CHANGE_ADJUST_EVENT);

        base_plot_group.displayAdjustmentValueChanged (event);

        if (getParent () != null) {
          // XXX change to revalidate().
          frame.validate ();
        }
      } catch (IOException e) {
        new MessageDialog (Utilities.getComponentFrame (base_plot_group),
                           "error while reading user data: " + e);
      }
    }
  }

  /**
   *  Return the JFrame that was passed to the constructor.
   **/
  public JFrame getParentFrame () {
    return frame;
  }

  /**
   *  Return the EntryGroup that was passed to the constructor.
   **/
  private EntryGroup getEntryGroup () {
    return entry_group;
  }

  /**
   *  The JFrame reference that was passed to the constructor.
   **/
  private JFrame frame;

  /**
   *  The BasePlotGroup that was passed to the constructor.
   **/
  private BasePlotGroup base_plot_group;

  /**
   *  The EntryGroup object that was passed to the constructor.
   **/
  private EntryGroup entry_group = null;

  /**
   *  This ViewMenu is updated when a usage plot is added.
   **/
  private ViewMenu view_menu;

  /**
   *  This AddMenu is updated when a usage plot is added.
   **/
  private AddMenu add_menu;

  /**
   *  The FeatureDisplay that was passed to the constructor.
   **/
  private FeatureDisplay feature_display;

  /**
   *  This list of the CheckboxMenuItems for the graphs is stored so that we
   *  can turn them all off with "Hide All Graphs".
   **/
  private Vector algorithm_menu_items = new Vector ();
}
