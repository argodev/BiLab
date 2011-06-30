/* RemoteFileDocument.java
 *
 * created: Aug 2005
 *
 * This file is part of Artemis
 * 
 * Copyright (C) 2005  Genome Research Limited
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
 */

package uk.ac.sanger.artemis.util;

import java.io.*;
import javax.swing.JOptionPane;
import uk.ac.sanger.artemis.components.*;
import uk.ac.sanger.artemis.components.SwingWorker;
import uk.ac.sanger.artemis.j2ssh.FileTransferProgressMonitor;
import uk.ac.sanger.artemis.j2ssh.FTProgress;

/**
 *  Objects of this class are Documents created from a file.
 *
 **/

public class RemoteFileDocument extends Document 
{
  /**
  *
  *  Create a new RemoteFileDocument from a File.
  *  @param location This should be a file or directory name. 
  *  @param remote_file File on server
  *
  **/
  public RemoteFileDocument(RemoteFileNode location) 
  {
    super(location);
  }

  /**
  *
  *  Append a String to the Document location with the correct separator.
  *  @param name The name to append.
  *
  **/
  public Document append(String name) throws IOException 
  {
    return null;
  }

  /**
  *
  *  Return the name of this Document (the last element of the Document
  *  location).
  *
  **/
  public String getName() 
  {
    return getRemoteFileNode().getFile();
  }

  /**
   *  Return a Document with the last element stripped off.
   **/
  public Document getParent()
  {
    return null;
  }
  
  /**
   *  Return true if and only if the Document refered to by this object exists
   *  and is readable.
   **/
  public boolean readable() 
  {
    return true;
  }


  /**
   *  Return true if and only if the Document refered to by this object exists
   *  and can be written to.
   **/
  public boolean writable() 
  {
    return true;
  }

  /**
   *  Create a new InputStream object from this Document.  The contents of the
   *  Document can be read from the InputStream.
   *  @exception IOException Thrown if the Document can't be read from
   *    (for example if it doesn't exist).
   **/
  public InputStream getInputStream ()
      throws IOException 
  {
    final RemoteFileNode node = getRemoteFileNode();

    FileTransferProgressMonitor monitor = new FileTransferProgressMonitor(null);
    FTProgress progress = monitor.add(node.getFile());
    final byte[] bytes = node.getFileContents(progress);
    monitor.close();

    final InputStream file_input_stream =
      new ProgressInputStream(new ByteArrayInputStream(bytes),
                              getProgressListeners());
    
    if(getName().endsWith(".gz")) 
      return new WorkingGZIPInputStream(file_input_stream);
    else 
      return file_input_stream;      
  }

  /**
   *  Create a new OutputStream object from this Document.  The Document can
   *  then be written to using the new object.  The old centents of the
   *  Document will be lost.
   *  @exception ReadOnlyException is thrown if the Document is read only.
   **/
  public OutputStream getOutputStream() throws IOException 
  {
    final File write_file = new File(System.getProperty("user.dir")+
                                     System.getProperty("file.separator")+
                                     getName());

    if(write_file.exists())
    {
      int n = JOptionPane.showConfirmDialog(null,
                 "The file :\n"+System.getProperty("user.dir")+
                                System.getProperty("file.separator")+
                                getName()+
                 "\nalready exists on the local disk.\nOverwrite?",
                 "Overwrite "+getName(),
                 JOptionPane.YES_NO_OPTION);
      if(n == JOptionPane.NO_OPTION)
        return null;
    }

    final FileOutputStream file_output_stream =
      new FileOutputStream(write_file);

    if(write_file.getName().endsWith(".gz")) 
      return new java.util.zip.GZIPOutputStream(file_output_stream);
    else
      return file_output_stream;
  }

  /**
  *
  * Save the entry back to the ssh server
  *
  */
  public void saveEntry(final File local_file)
  {
    SwingWorker putWorker = new SwingWorker()
    {
      FileTransferProgressMonitor monitor;
      public Object construct()
      {
        monitor = new FileTransferProgressMonitor(null);
        FTProgress progress = monitor.add(local_file.getName());

        getRemoteFileNode().put(local_file, progress);
        getRemoteFileNode().stat();
        monitor.close();
        return null;
      }

      public void finished()
      {
        if(monitor != null)
          monitor.close();
      }
    };
    putWorker.start();
  }

  /**
   *  Return the RemoteFileNode object that this RemoteFileDocument is encapsulating.
   **/
  public RemoteFileNode getRemoteFileNode() 
  {
    return (RemoteFileNode)getLocation();
  }

}
