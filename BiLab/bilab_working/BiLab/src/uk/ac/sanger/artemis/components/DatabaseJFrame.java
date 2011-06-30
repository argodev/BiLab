/* DatabaseJFrame.java
 *
 * created: June 2005
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/DatabaseJFrame.java,v 1.8 2006/01/10 12:04:32 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.Entry;
import uk.ac.sanger.artemis.sequence.*;
import uk.ac.sanger.artemis.util.InputStreamProgressListener;
import uk.ac.sanger.artemis.util.OutOfRangeException;
import uk.ac.sanger.artemis.util.DatabaseDocument;
import uk.ac.sanger.artemis.io.DatabaseDocumentEntry;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.io.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DatabaseJFrame extends JFrame
{
  private JLabel status_line = new JLabel("");

  private boolean splitGFFEntry = true;

  public DatabaseJFrame(final DatabaseEntrySource entry_source,
      final ArtemisMain art_main)
  {
    super("PSU Organism");
    final JTree tree = entry_source.getDatabaseTree();

    // Listen for when the selection changes.
    MouseListener mouseListener = new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        if(e.getClickCount() == 2 && !e.isPopupTrigger())
          showSelected(entry_source, tree, art_main);
      }
    };
    tree.addMouseListener(mouseListener);

    JScrollPane scroll = new JScrollPane(tree);

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension dim_frame = new Dimension(screen.width * 2 / 10,
                                        screen.height * 6 / 10);
    scroll.setPreferredSize(dim_frame);

    setJMenuBar(makeMenuBar(entry_source, tree, art_main));

    JPanel pane = (JPanel)getContentPane();
    pane.add(scroll, BorderLayout.CENTER);

    final FontMetrics fm = this.getFontMetrics(status_line.getFont());

    final int font_height = fm.getHeight() + 10;
    status_line.setMinimumSize(new Dimension(100, font_height));
    status_line.setPreferredSize(new Dimension(100, font_height));

    Border loweredbevel = BorderFactory.createLoweredBevelBorder();
    Border raisedbevel = BorderFactory.createRaisedBevelBorder();
    Border compound = BorderFactory.createCompoundBorder(raisedbevel,
                                                         loweredbevel);
    status_line.setBorder(compound);
    pane.add(status_line, BorderLayout.SOUTH);

    pack();
 //   Utilities.rightJustifyFrame(this);
  }

  /**
   * 
   * Show the selected sequence in the tree
   * 
   */
  private void showSelected(final DatabaseEntrySource entry_source,
      final JTree tree, final ArtemisMain art_main)
  {
    Cursor cbusy = new Cursor(Cursor.WAIT_CURSOR);
    Cursor cdone = new Cursor(Cursor.DEFAULT_CURSOR);

    String node_name = null;

    try
    {
      node_name = entry_source.getSelectedNode(tree);
      String id = entry_source.getEntryID(node_name);
      if(id != null)
        getEntryEditFromDatabase(id, entry_source, tree,
                                 art_main, node_name);
    }
    catch(NullPointerException npe)
    {
    }
  }

  /**
   * 
   * Retrieve a database entry.
   * 
   */
  private void getEntryEditFromDatabase(final String id,
      final DatabaseEntrySource entry_source, final JTree tree,
      final ArtemisMain art_main, final String node_name)
  {
    SwingWorker entryWorker = new SwingWorker()
    {
      public Object construct()
      {
        Cursor cbusy = new Cursor(Cursor.WAIT_CURSOR);
        Cursor cdone = new Cursor(Cursor.DEFAULT_CURSOR);

        status_line.setText("Retrieving sequence....");
        tree.setCursor(cbusy);
        try
        {
          final InputStreamProgressListener progress_listener = 
                           art_main.getInputStreamProgressListener();

          entry_source.setSplitGFF(splitGFFEntry);

          String schema = entry_source.getSchemaOfSelectedNode(tree);

          final Entry entry = entry_source.getEntry(id, schema,
                                                    progress_listener);


          DatabaseDocumentEntry db_entry =
                         (DatabaseDocumentEntry)entry.getEMBLEntry();

          int ind = 0;
          String name = node_name;
          if( (ind = name.lastIndexOf("-")) > -1)
            name = name.substring(ind+1).trim();

          ((DatabaseDocument)db_entry.getDocument()).setName(name);

          if(entry == null)
          {
            tree.setCursor(cdone);
            status_line.setText("No entry.");
            return null;
          }

          final EntryEdit new_entry_edit = art_main.makeEntryEdit(entry);

          // add gff entries
          if(splitGFFEntry)
          {
//          DatabaseDocumentEntry db_entry = 
//                       (DatabaseDocumentEntry)entry.getEMBLEntry();

            final DatabaseDocumentEntry[] entries = entry_source.makeFromGff(
                        (DatabaseDocument)db_entry.getDocument(), id, schema);
            for(int i = 0; i < entries.length; i++)
            {
              if(entries[i] == null)
                continue;

              final Entry new_entry = new Entry(new_entry_edit.getEntryGroup().getBases(),
                                                entries[i]);
              new_entry_edit.getEntryGroup().add(new_entry);
            }
          }

          new_entry_edit.setVisible(true);
          status_line.setText("Sequence loaded.");
        }
        catch(OutOfRangeException e)
        {
          new MessageDialog(art_main, "read failed: one of the features in "
              + " the entry has an out of range " + "location: "
              + e.getMessage());
        }
        catch(NoSequenceException e)
        {
          new MessageDialog(art_main, "read failed: entry contains no sequence");
        }
        catch(IOException e)
        {
          new MessageDialog(art_main, "read failed due to IO error: " + e);
        }
        tree.setCursor(cdone);
        return null;
      }

    };
    entryWorker.start();

  }

  private JMenuBar makeMenuBar(final DatabaseEntrySource entry_source,
                               final JTree tree, final ArtemisMain art_main)
  {
    JMenuBar mBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    mBar.add(fileMenu);

    JMenuItem fileShow = new JMenuItem("Open Selected Sequence ...");
    fileShow.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showSelected(entry_source, tree, art_main);
      }
    });
    fileMenu.add(fileShow);
    fileMenu.add(new JSeparator());

    JMenuItem fileMenuClose = new JMenuItem("Close");
    fileMenuClose.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        setVisible(false);
      }
    });
    fileMenu.add(fileMenuClose);

    JMenu optionMenu = new JMenu("Options");
    mBar.add(optionMenu);

    final JCheckBoxMenuItem splitGFF = new JCheckBoxMenuItem(
                    "Split GFF into entries", splitGFFEntry);
    splitGFF.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if(splitGFF.isSelected())
          splitGFFEntry = true;
        else
          splitGFFEntry = false;
      }
    });
    optionMenu.add(splitGFF);

    return mBar;
  }
}
