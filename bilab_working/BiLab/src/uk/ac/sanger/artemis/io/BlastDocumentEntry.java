/* BlastDocumentEntry.java
 *
 * created: Wed May  8 2002
 *
 * This file is part of Artemis
 *
 * Copyright (C) 2000  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/io/BlastDocumentEntry.java,v 1.1 2004/06/09 09:48:54 tjc Exp $
 */

package uk.ac.sanger.artemis.io;

import uk.ac.sanger.artemis.util.*;

import java.io.*;

/**
 *  A DocumentEntry that can read an Entry from a Document containing
 *  blastall -m 8  (ie. tabular blast output).
 *
 *  @author Kim Rutherford <kmr@sanger.ac.uk>
 *  @version $Id: BlastDocumentEntry.java,v 1.1 2004/06/09 09:48:54 tjc Exp $
 **/

public class BlastDocumentEntry extends SimpleDocumentEntry
    implements DocumentEntry {
  /**
   *  Create a new BlastDocumentEntry object associated with the given
   *  Document.
   *  @param document This is the file that we will read from.  This is also
   *    used for saving the entry back to the file it came from and to give
   *    the new object a name.
   *  @param listener The object that will listen for ReadEvents.
   *  @exception IOException thrown if there is a problem reading the entry -
   *    most likely ReadFormatException.
   **/
  public BlastDocumentEntry (final Document document,
                             final ReadListener listener)
      throws IOException, EntryInformationException {
    super (new BlastEntryInformation (), document, listener);
  }

  /**
   *  Create a new BlastDocumentEntry that will be a copy of the given
   *  Entry and has no Document associated with it.  The new
   *  BlastDocumentEntry cannot be saved to a file with save () unless
   *  save (Document) has been called first.  Some qualifier and location
   *  information will be lost.
   *  @param force If true then invalid qualifiers and any features with
   *    invalid keys in the new Entry will be quietly thrown away.  "Invalid"
   *    means that the key/qualifier is not allowed to occur in an Entry of
   *    this type (probably determined by the EntryInformation object of this
   *    Entry).  If false an EntryInformationException will be thrown for
   *    invalid keys or qualifiers.
   **/
  public BlastDocumentEntry (final Entry new_entry, final boolean force)
      throws EntryInformationException {
    super (new BlastEntryInformation (), new_entry, force);
  }

  /**
   *  Create a new empty BlastDocumentEntry object that has no Document
   *  associated with it.  The new BlastDocumentEntry cannot be saved to a
   *  file with save () unless save (Document) has been called first.  The
   *  save (Document) method will assign a Document.
   **/
  public BlastDocumentEntry (final EntryInformation entry_information) {
    super (new BlastEntryInformation ());
  }

  /**
   *  Returns true if and only if this entry is read only.  For now this
   *  always returns true - BlastDocumentEntry objects can't be changed.
   **/
  public boolean isReadOnly () {
    return true;
  }

  /**
   *  If the given feature can be added directly to this Entry, then return
   *  it, otherwise create and return a new feature of the appropriate type.
   *  @param copy if true then always new a new copy of the Feature.
   **/
  protected SimpleDocumentFeature makeNativeFeature (final Feature feature,
                                                     final boolean copy) {
    if (!copy && feature instanceof BlastStreamFeature) {
      return (BlastStreamFeature) feature;
    } else {
      return new BlastStreamFeature (feature);
    }
  }

  /**
   *  If the given Sequence can be added directly to this Entry, then return a
   *  copy of it, otherwise create and return a new feature of the appropriate
   *  type for this Entry.
   **/
  protected StreamSequence makeNativeSequence (final Sequence sequence) {
    return new FastaStreamSequence (sequence);
  }
}
