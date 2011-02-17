/* Utilities.java
 *
 * created: Tue Sep 18 2001
 *
 * This file is part of Artemis
 * 
 * Copyright(C) 2001  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/Utilities.java,v 1.1 2004/06/09 09:47:57 tjc Exp $
 */

package uk.ac.sanger.artemis.components;

import uk.ac.sanger.artemis.util.InputStreamProgressListener;
import uk.ac.sanger.artemis.EntrySourceVector;
import uk.ac.sanger.artemis.Options;

import java.awt.*;
import java.net.MalformedURLException;
import javax.swing.*;

/**
 *  Utilities methods used by the uk.ac.sanger.artemis.components package.
 *
 *  @author Kim Rutherford <kmr@sanger.ac.uk>
 *  @version $Id: Utilities.java,v 1.1 2004/06/09 09:47:57 tjc Exp $
 **/

public class Utilities 
{
  /**
   *  Return the JFrame that contains the given component.
   **/
  public static JFrame getComponentFrame(final JComponent component) 
  {
    if(component.getTopLevelAncestor() instanceof JFrame) 
      return(JFrame) component.getTopLevelAncestor();
    else 
      return null;
  }

  /**
   *  Move the given JFrame to the centre of the screen.
   **/
  public static void centreFrame(final JFrame frame) 
  {
    final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    final int x_position =(screen.width - frame.getSize().width) / 2;
    int y_position =(screen.height - frame.getSize().height) / 2;

    if(y_position < 10) 
      y_position = 10;

    frame.setLocation(new Point(x_position, y_position));
  }

  /**
   *  Find the parent Frame of the given component and re-centre it on the
   *  screen.
   **/
  public static void centreOwningFrame(final JComponent component) 
  {
    final JFrame frame = getComponentFrame(component);
    centreFrame(frame);
  }

  /**
   *  Returns a Vector containing the possible Entry sources for this
   *  application.
   *  @param frame The component that is creating the EntrySource objects.
   *   (Used for requesters.)
   *  @param listener InputStreamProgressEvent objects will be sent to this
   *    listener as progress on reading is made.
   **/
  public static EntrySourceVector getEntrySources(final JFrame frame,
                     final InputStreamProgressListener listener) 
  {
    final EntrySourceVector return_vector = new EntrySourceVector();
    
    return_vector.add(new FileDialogEntrySource(frame, listener));
    return_vector.add(new DbfetchEntrySource(frame));

    // return_vector.add(new BioJavaEntrySource());
    
    // this doesn't work on a v1.2 system so it is taken out with perl when
    // necessary 
    // CORBA_START_MARKER

    // The location of the IOR for the corba server at EMBL.  Can be
    // overridden using the options file. 
    final String embl_ior_url =
      Options.getOptions().getProperty("embl_ior_url");

    if(embl_ior_url != null) 
    {
      try 
      {
        return_vector.add(new EMBLCorbaEntrySource(frame, embl_ior_url));
      } 
      catch(MalformedURLException e) 
      {
        new MessageDialog(frame, "the url given for the embl database is " +
                          "badly formatted: " + e.getMessage());
      }
    }

    // The location of the IOR for the pathogens group read-write corba
    // server. 
    final String db_ior_url =
      Options.getOptions().getProperty("db_ior_url");
    
    if(db_ior_url != null) 
    {
      try 
      {
        return_vector.add(new WritableEMBLCorbaEntrySource(frame,
                                                           db_ior_url));
      }
      catch(MalformedURLException e)
      {
        new MessageDialog(frame, "the url given for the embl database is " +
                           "badly formatted: " + e.getMessage());
      }
    }
    // CORBA_END_MARKER

    return return_vector;
  }
}
