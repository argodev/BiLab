/* InputStreamProgressDialog.java
 *
 * created: Thu Aug  8 2002
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/InputStreamProgressDialog.java,v 1.1 2004/06/09 09:46:58 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import java.awt.*;
import java.awt.event.*;

import uk.ac.sanger.artemis.util.InputStreamProgressListener;
import uk.ac.sanger.artemis.util.InputStreamProgressEvent;

import javax.swing.*;

/**
 *  A JDialog the show the byte count of an InputStream (via
 *  InputStreamProgressEvent objects)
 *
 *  @author Kim Rutherford <kmr@sanger.ac.uk>
 *  @version $Id: InputStreamProgressDialog.java,v 1.1 2004/06/09 09:46:58 tjc Exp $
 **/

public class InputStreamProgressDialog extends JDialog {
  /**
   *  Create a blocking InputStreamProgressDialog JFrame.
   *  @param parent The parent window.
   *  @param message The message to display in the JDialog and to use as the
   *    frame title.
   **/
  public InputStreamProgressDialog (final JFrame parent,
                                    final String message) {
    this (parent, message, message, true);
  }

  /**
   *  Create a new InputStreamProgressDialog JFrame.
   *  @param parent The parent window.
   *  @param title The title of the new JDialog.
   *  @param message The message to display in the JDialog.
   *  @param modal If true, dialog blocks input to the parent window when
   *    shown.
   **/
  public InputStreamProgressDialog (final JFrame parent_frame,
                                    final String title,
                                    final String message,
                                    final boolean modal) {
    super (parent_frame, title, modal);
    getContentPane ().add (new JLabel (message), "North");

    final JLabel bytes_label = new JLabel ("                               ");
    getContentPane ().add (bytes_label, "Center");

    final JPanel panel = new JPanel ();

    panel.add (ok_button);
    ok_button.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent e) {
        InputStreamProgressDialog.this.dispose ();
      }
    });

    addWindowListener (new WindowAdapter () {
      public void windowClosing (WindowEvent event) {
        InputStreamProgressDialog.this.dispose ();
      }
    });

    stream_progress_listener = new InputStreamProgressListener () {
      public void progressMade (final InputStreamProgressEvent event) {

        final int char_count = event.getCharCount ();
        if (char_count == -1) {
          bytes_label.setText ("");
        } else {
          setVisible (true);
          bytes_label.setText ("Characters read so far: " + char_count);
        }
      }
    };

    getContentPane ().add (panel, "South");
    pack ();

    final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation (new Point ((screen.width - getSize ().width) / 2,
                            (screen.height - getSize ().height) / 2));

    setVisible (false);
  }

  /**
   *  Return an InputStreamProgressListener to pass to FileProgressDocument.
   **/
  public InputStreamProgressListener getInputStreamProgressListener () {
    return stream_progress_listener;
  }

  /**
   *  An InputStreamProgressListener used to update the label with the
   *  current number of chars read.
   **/
  private InputStreamProgressListener stream_progress_listener = null;

  final private JButton ok_button = new JButton ("OK");
}
