/* GFFDocumentEntry.java
 *
 * created: Tue Sep 14 1999
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/io/GFFDocumentEntry.java,v 1.1 2004/06/09 09:49:29 tjc Exp $
 */

package uk.ac.sanger.artemis.io;

import uk.ac.sanger.artemis.util.*;

import java.io.*;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 *  A DocumentEntry that can read an GFF entry from a Document.
 *
 *  @author Kim Rutherford
 *  @version $Id: GFFDocumentEntry.java,v 1.1 2004/06/09 09:49:29 tjc Exp $
 **/

public class GFFDocumentEntry extends SimpleDocumentEntry
    implements DocumentEntry {
  /**
   *  Create a new GFFDocumentEntry object associated with the given
   *  Document.
   *  @param document This is the file that we will read from.  This is also
   *    used for saving the entry back to the file it came from and to give
   *    the new object a name.
   *  @param listener The object that will listen for ReadEvents.
   *  @exception IOException thrown if there is a problem reading the entry -
   *    most likely ReadFormatException.
   **/
  GFFDocumentEntry (final Document document, final ReadListener listener)
      throws IOException, EntryInformationException {
    super (new GFFEntryInformation (), document, listener);

    // join the separate exons into one feature (if appropriate)
    combineFeatures ();

    finished_constructor = true;
  }

  /**
   *  Create a new GFFDocumentEntry that will be a copy of the given Entry and
   *  has no Document associated with it.  The new GFFDocumentEntry cannot be
   *  saved to a file with save () unless save (Document) has been called
   *  first.  Some qualifier and location information will be lost.
   *  @param force If true then invalid qualifiers and any features with
   *    invalid keys in the new Entry will be quietly thrown away.  "Invalid"
   *    means that the key/qualifier is not allowed to occur in an Entry of
   *    this type (probably determined by the EntryInformation object of this
   *    Entry).  If false an EntryInformationException will be thrown for
   *    invalid keys or qualifiers.
   **/
  public GFFDocumentEntry (final Entry new_entry, final boolean force)
      throws EntryInformationException {
    super (new GFFEntryInformation (), new_entry, force);

    finished_constructor = true;
  }

  /**
   *  Create a new empty GFFDocumentEntry object that has no Document
   *  associated with it.  The new GFFDocumentEntry cannot be saved to a
   *  file with save () unless save (Document) has been called first.  The
   *  save (Document) method will assign a Document.
   **/
  public GFFDocumentEntry (final EntryInformation entry_information) {
    super (new GFFEntryInformation ());

    finished_constructor = true;
  }

  /**
   *  Returns true if and only if this entry is read only.  For now this
   *  always returns true - GFFDocumentEntry objects can't be changed.
   **/
  public boolean isReadOnly () {
    return finished_constructor;
  }

  /**
   *  If the given feature can be added directly to this Entry, then return
   *  it, otherwise create and return a new feature of the appropriate type.
   *  @param copy if true then always new a new copy of the Feature.
   **/
  protected SimpleDocumentFeature makeNativeFeature (final Feature feature,
                                                     final boolean copy) {
    if (!copy && feature instanceof GFFStreamFeature) {
      return (GFFStreamFeature) feature;
    } else {
      return new GFFStreamFeature (feature);
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

  /**
   *  Join the separate exons into one feature (if appropriate).
   **/
  private void combineFeatures () {
    final FeatureVector original_features = getAllFeatures ();

    // the key of these hashes will be the group name and the value is a
    // FeatureVector containing the feature that are in that group
    final Hashtable forward_feature_groups = new Hashtable ();
    final Hashtable reverse_feature_groups = new Hashtable ();

    for (int i = 0 ; i < original_features.size () ; ++i) {
      final Feature this_feature = original_features.elementAt (i);

      final Hashtable this_strand_feature_groups;

      if (this_feature.getLocation ().isComplement ()) {
        this_strand_feature_groups = reverse_feature_groups;
      } else {
        this_strand_feature_groups = forward_feature_groups;
      }

      final String group_name;

      try {
        if (this_feature.getQualifierByName ("gene") == null) {
          if (this_feature.getQualifierByName ("group") == null) {
            // no gene names and no groups - give up
            return;
          } else {
            final StringVector values =
              this_feature.getQualifierByName ("group").getValues ();
            if (values == null) {
              throw new Error ("internal error - " +
                               "no value for group qualifier");
            } else {
              group_name = values.elementAt (0);
            }
          }
        } else {
          final StringVector values =
            this_feature.getQualifierByName ("gene").getValues ();
          if (values == null) {
            throw new Error ("internal error - " +
                             "no value for gene qualifier");
          }
          group_name = values.elementAt (0);
        }
      } catch (InvalidRelationException e) {
        throw new Error ("internal error - unexpected exception: " + e);
      }

      final FeatureVector other_features =
        (FeatureVector) this_strand_feature_groups.get (group_name);

      if (other_features == null) {
        final FeatureVector new_feature_vector = new FeatureVector ();

        new_feature_vector.add (this_feature);

        this_strand_feature_groups.put (group_name, new_feature_vector);
      } else {
        other_features.add (this_feature);
      }
    }

    combineFeaturesFromHash (forward_feature_groups);
    combineFeaturesFromHash (reverse_feature_groups);
  }

  /**
   *  Combine the features (which are exons) and delete the orignals from this
   *  Entry.  The key of this hash will be the group name and the value is a
   *  FeatureVector containing the feature that are in that group.  Groups
   *  that have more than one member will be combined.
   **/
  private void combineFeaturesFromHash (final Hashtable feature_groups) {
    final Enumeration _enum = feature_groups.keys ();

    while (_enum.hasMoreElements ()) {
      final String name = (String) _enum.nextElement();

      final FeatureVector feature_group =
        (FeatureVector) feature_groups.get (name);

      if (feature_group.size () > 1) {
        // combine the features (exons) and delete the orignals

        final RangeVector new_range_vector = new RangeVector ();

        // storage for the original GFF lines.  the new feature will have a
        // multi-line gff_line
        StringVector new_gff_lines = new StringVector ();

        for (int i = 0 ; i < feature_group.size () ; ++i) {
          final GFFStreamFeature this_feature =
            (GFFStreamFeature) feature_group.elementAt (i);

          final Location this_feature_location = this_feature.getLocation ();

          if (this_feature_location.getRanges ().size () > 1) {
            throw new Error ("internal error - new location should have " +
                             "exactly one range");
          }

          final Range new_range =
            this_feature_location.getRanges ().elementAt (0);

          if (this_feature_location.isComplement ()) {
            new_range_vector.insertElementAt (new_range, 0);
          } else {
            new_range_vector.add (new_range);
          }

          removeInternal (this_feature);

          new_gff_lines.add (this_feature.gff_lines);
        }

        final Feature first_old_feature = feature_group.elementAt (0);

        final GFFStreamFeature new_feature =
          new GFFStreamFeature (first_old_feature);

        new_feature.gff_lines = new_gff_lines;

        final Location new_location =
          new Location (new_range_vector,
                        first_old_feature.getLocation ().isComplement ());

        try {
          new_feature.setLocation (new_location);

          final Qualifier gene_qualifier =
            new_feature.getQualifierByName ("gene");

          if (gene_qualifier != null &&
              gene_qualifier.getValues ().size () > 0 &&
              gene_qualifier.getValues ().elementAt (0).startsWith ("Phat")) {

            // special case to handle incorrect output of the Phat gene
            // prediction tool
            new_feature.removeQualifierByName ("codon_start");
          } else {
            final Qualifier old_codon_start_qualifier =
              first_old_feature.getQualifierByName ("codon_start");

            if (old_codon_start_qualifier != null) {
              new_feature.setQualifier (old_codon_start_qualifier);
            }
          }

          forcedAdd (new_feature);
        } catch (ReadOnlyException e) {
          throw new Error ("internal error - unexpected exception: " + e);
        } catch (OutOfRangeException e) {
          throw new Error ("internal error - unexpected exception: " + e);
        } catch (EntryInformationException e) {
          throw new Error ("internal error - unexpected exception: " + e);
        }
      }
    }
  }

  /**
   *
   **/
  private boolean finished_constructor = false;
}
