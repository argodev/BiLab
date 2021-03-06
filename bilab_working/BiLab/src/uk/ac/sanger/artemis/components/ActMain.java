/* ActMain.java
 *
 * created: Wed May 10 2000
 *
 * This file is part of Artemis
 *
 * Copyright(C) 2000  Genome Research Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or(at your option) any later version.
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/ActMain.java,v 1.2 2004/06/29 08:46:13 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.*;
import uk.ac.sanger.artemis.sequence.Bases;

import uk.ac.sanger.artemis.util.*;
import uk.ac.sanger.artemis.io.EntryInformation;
import uk.ac.sanger.artemis.io.SimpleEntryInformation;

import java.awt.event.*;
import java.io.IOException;
import javax.swing.JFrame;

/**
 *  The main window for the Artemis Comparison Tool.
 *
 *  @author Kim Rutherford <kmr@sanger.ac.uk>
 *  @version $Id: ActMain.java,v 1.2 2004/06/29 08:46:13 tjc Exp $
 **/

public class ActMain extends Splash 
{
  /** Version String use for banner messages and title bars. */
  public static final String version = "Release 3";
  /** File manager */
  protected static FileManager filemanager = null;

  /**
   *  The constructor creates all the components for the main ACT window
   *  and sets up all the menu callbacks.
   **/
  public ActMain() 
  {
    super("Artemis Comparison Tool", "ACT", version);

    ActionListener open_listener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent event) 
      {
        makeOpenDialog();
      }
    };

    makeMenuItem(file_menu, "Open ...", open_listener);

    ActionListener quit_listener = new ActionListener() 
    {
      public void actionPerformed(ActionEvent event) 
      {
        exit();
      }
    };

    makeMenuItem(file_menu, "Quit", quit_listener);
  }

  /**
   *  Make a new Comparator component from the given files.
   *  @param frame The JFrame used when making a new MessageDialog.
   *  @param progress_listener The object to which InputStreamProgressEvents
   *    will be send while reading.  Can be null.
   *  @param file_names Alternating sequence and comparison data file names.
   *    Must be >= 3.  I there are an even number of file names the first
   *    file/sequence object will be added to the send of the display and the
   *    last comparison file will be assumed to be a a comparison between the
   *    last and first sequence files.
   **/
  public static boolean makeMultiComparator(final JFrame frame,
                          final InputStreamProgressListener progress_listener,
                          final String[] file_names) 
  {

    final ProgressThread progress_thread = new ProgressThread(null,
                                                "Loading Entry...");

    SwingWorker entryWorker = new SwingWorker()
    {
      public Object construct()
      {
        progress_thread.start();
        final EntryGroup[] entry_group_array =
          new EntryGroup[file_names.length / 2 + 1];

        final ComparisonData[] comparison_data_array =
          new ComparisonData[file_names.length / 2];

        for(int i = 0; i<file_names.length; i += 2) 
        {
          final EntryInformation entry_information =
            new SimpleEntryInformation(Options.getArtemisEntryInformation());

          final String this_file_name = file_names[i];

          final Document entry_document =
            DocumentFactory.makeDocument(this_file_name);

          if(progress_listener != null) 
            entry_document.addInputStreamProgressListener(progress_listener);

          final uk.ac.sanger.artemis.io.Entry embl_entry =
            EntryFileDialog.getEntryFromFile(frame, entry_document,
                                         entry_information,
                                         true);

          // getEntryFromFile() has alerted the user so we just need to quit
          if(embl_entry == null) 
            return null;

          final uk.ac.sanger.artemis.io.Sequence sequence =
                                             embl_entry.getSequence();

          if(sequence == null) 
          {
            new MessageDialog(frame, "This file contains no sequence: " +
                               this_file_name);
            return null;
          }

          final Bases embl_bases = new Bases(sequence);
          final EntryGroup entry_group = new SimpleEntryGroup(embl_bases);

          try 
          {
            final Entry entry = new Entry(entry_group.getBases(), embl_entry);
            entry_group.add(entry);
            entry_group_array[i / 2] = entry_group;
          } 
          catch(OutOfRangeException e) 
          {
            new MessageDialog(frame, "read failed: one of the features has an " +
                               "out of range location: " + e.getMessage());
            return null;
          }
        }

        // add the first entry at the end to make the MultiComparator
        // circular(-ish)
        if(file_names.length % 2 == 0) 
          entry_group_array[entry_group_array.length - 1] = entry_group_array[0];

        try
        {
          for(int i = 1 ; i < file_names.length ; i += 2) 
          {
            final String comparison_data_file_name = file_names[i];
            final Document comparison_data_document =
              DocumentFactory.makeDocument(comparison_data_file_name);

            comparison_data_array[i / 2] =
              ComparisonDataFactory.readComparisonData(comparison_data_document);

            final Bases prev_bases = entry_group_array[i/2].getBases();
            final Bases next_bases = entry_group_array[i/2 + 1].getBases();

            final ComparisonData swapped_comparison_data =
              comparison_data_array[i / 2].flipMatchesIfNeeded(prev_bases,
                                                            next_bases);

            if(swapped_comparison_data != null) 
              comparison_data_array[i / 2] = swapped_comparison_data;

            if(swapped_comparison_data != null)
            {
              final MessageFrame message_frame =
                new MessageFrame("note: hits from " + comparison_data_file_name +
                                  " have been flipped to match the " +
                                  "sequences");

              message_frame.setVisible(true);
            }
          }
        } 
        catch(IOException e) 
        {
          new MessageDialog(frame, "error while reading: " + e.getMessage());
          return null;
        }
        catch(OutOfRangeException e) 
        {
          new MessageDialog(frame, "comparison file read failed:  " +
                             "out of range error: " + e.getMessage());
          return null;
        }
        
        final MultiComparator comparator =
          new MultiComparator(entry_group_array,
                              comparison_data_array,
                              progress_listener);

        comparator.setVisible(true);
        return null;
      }

      public void finished()
      {
        if(progress_thread !=null)
          progress_thread.finished();
      }

    };
    entryWorker.start();

    return true;
  }

  /**
   *  Create a dialog that allow the user to the choose two files to compare
   *  and a file containing comparison data.
   **/
  private void makeOpenDialog() 
  {
    if(filemanager == null)
      filemanager = new FileManager(this,null);
    else
      filemanager.setVisible(true);
    new ComparatorDialog(this).setVisible(true);
  }

  /**
   *  Exit from ACT.
   **/
  protected void exit() 
  {
    System.exit(0);
  }

  /**
   *  Main entry point for ACT
   **/
  public static void main(final String [] args) 
  {
    final ActMain main_window = new ActMain();
    main_window.setVisible(true);

    final InputStreamProgressListener progress_listener =
      main_window.getInputStreamProgressListener();
    
    if(args.length >= 3) 
      ActMain.makeMultiComparator(main_window, progress_listener,
                                  args);
    else 
    {
      if(args.length != 0) 
      {
        System.err.println("Error - this program needs either no " +
                           " arguments or an odd number\n" +
                           "(3 or more):");
        System.err.println("   act sequence_1 comparison_data sequence_2");
        System.err.println("or");
        System.err.println("   act seq_1 comparison_data_2_v_1 seq_2 comparison_data_3_v_2 seq_3");
        System.err.println("or");
        System.err.println("   act");
        System.exit(1);
      }
    }
  }

}
