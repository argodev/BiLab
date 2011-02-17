/* GFFStreamFeature.java
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/io/GFFStreamFeature.java,v 1.1 2004/06/09 09:49:33 tjc Exp $
 */

package uk.ac.sanger.artemis.io;

import uk.ac.sanger.artemis.util.*;

import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *  A StreamFeature that thinks it is a GFF feature.
 *
 *  @author Kim Rutherford
 *  @version $Id: GFFStreamFeature.java,v 1.1 2004/06/09 09:49:33 tjc Exp $
 **/

public class GFFStreamFeature
    extends SimpleDocumentFeature
    implements DocumentFeature, StreamFeature, ComparableFeature {

  /**
   *  Create a new GFFStreamFeature object.  The feature should be added
   *  to an Entry (with Entry.add ()).
   *  @param key The new feature key
   *  @param location The Location object for the new feature
   *  @param qualifiers The qualifiers for the new feature
   **/
  public GFFStreamFeature (final Key key,
                           final Location location,
                           final QualifierVector qualifiers) {
    super (null);
    try {
      setKey (key);
      setLocation (location);
      setQualifiers (qualifiers);
      if (getQualifierByName ("score") == null) {
        setQualifier (new Qualifier ("score", "."));
      }
      if (getQualifierByName ("gff_source") == null) {
        setQualifier (new Qualifier ("gff_source", "artemis"));
      }
      if (getQualifierByName ("gff_seqname") == null) {
        setQualifier (new Qualifier ("gff_seqname", "."));
      }
    } catch (EntryInformationException e) {
      // this should never happen because the feature will not be in an Entry
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (ReadOnlyException e) {
      // this should never happen because the feature will not be in an Entry
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (OutOfRangeException e) {
      // this should never happen because the feature will not be in an Entry
      throw new Error ("internal error - unexpected exception: " + e);
    }
  }

  /**
   *  Create a new GFFStreamFeature with the same key, location and
   *  qualifiers as the given feature.  The feature should be added to an
   *  Entry (with Entry.add ()).
   *  @param feature The feature to copy.
   **/
  public GFFStreamFeature (final Feature feature) {
    this (feature.getKey (), feature.getLocation (), feature.getQualifiers ());

    if (feature instanceof GFFStreamFeature) {
      gff_lines = new StringVector (((GFFStreamFeature)feature).gff_lines);
    }
  }

  /**
   *  Return the reference of a new copy of this Feature.
   **/
  public Feature copy () {
    final Feature return_value = new GFFStreamFeature (this);

    return return_value;
  }

  /**
   *  Create a new GFFStreamFeature from the given line.  The String should be
   *  in gene finder format.
   **/
  private GFFStreamFeature (final String line)
      throws ReadFormatException {
    super (null);

    final StringVector line_bits = StringVector.getStrings (line, "\t", true);

    if (line_bits.size () < 8) {
      throw new ReadFormatException ("invalid GFF line: 8 fields needed " +
                                      "(got " + line_bits.size () +
                                     " fields) from: " + line);
    }

    final String start_base_string = line_bits.elementAt (3).trim ();
    final String end_base_string = line_bits.elementAt (4).trim ();

    final int start_base;
    final int end_base;

    try {
      start_base = Integer.parseInt (start_base_string);
    } catch (NumberFormatException e) {
      throw new ReadFormatException ("Could not understand the start base " +
                                     "of a GFF feature: " + start_base_string);
    }

    try {
      end_base = Integer.parseInt (end_base_string);
    } catch (NumberFormatException e) {
      throw new ReadFormatException ("Could not understand the end base " +
                                     "of a GFF feature: " + end_base_string);
    }

    // start of qualifier parsing and setting
    try {

      final boolean complement_flag;

      if (line_bits.elementAt (6).equals ("+")) {
        complement_flag = false;
      } else {
        if (line_bits.elementAt (6).equals ("-")) {
          complement_flag = true;
        } else {
          // must be unstranded
          complement_flag = false;

          // best we can do
          final String note_string = "this feature is unstranded";

          setQualifier (new Qualifier ("note", note_string));
        }
      }

      if (line_bits.size () == 9) {
        final String rest_of_line = line_bits.elementAt (8);

        // parse the rest of the line as ACeDB format attributes
        final Hashtable attributes = parseAttributes (rest_of_line);

        for (final java.util.Enumeration attribute_enum = attributes.keys ();
             attribute_enum.hasMoreElements();) {
          final String name = (String) attribute_enum.nextElement();

          final StringVector values = (StringVector) attributes.get (name);

          if (values.size () == 0) {
            setQualifier (new Qualifier (name));
          } else {
            setQualifier (new Qualifier (name, values));
          }
        }
      }

      final Qualifier gff_seqname =
        new Qualifier ("gff_seqname", line_bits.elementAt (0));

      setQualifier (gff_seqname);

      final Key key = new Key (line_bits.elementAt (2));

      setKey (key);

      final Qualifier source_qualifier =
        new Qualifier ("gff_source", line_bits.elementAt (1));

      setQualifier (source_qualifier);

      final Qualifier score_qualifier =
        new Qualifier ("score", line_bits.elementAt (5));

      setQualifier (score_qualifier);

      String frame = line_bits.elementAt (7);

      if (frame.equals ("0")) {
        frame = "1";
      } else {
        if (frame.equals ("1")) {
          frame = "2";
        } else {
          if (frame.equals ("2")) {
            frame = "3";
          } else {
            frame = ".";
          }
        }
      }

      if (!frame.equals ("1") && !frame.equals (".")) {
        final Qualifier codon_start_qualifier =
          new Qualifier ("codon_start", frame);

        setQualifier (codon_start_qualifier);
      }

      if (start_base > end_base) {
        throw new ReadFormatException ("start position is greater than end " +
                                       "position: " + start_base + " > " +
                                       end_base);
      }

      if (start_base < 0) {
        throw new ReadFormatException ("start position must be positive: " +
                                       start_base); 
      }
      
      final Range location_range = new Range (start_base, end_base);

      final RangeVector location_ranges = new RangeVector (location_range);

      setLocation (new Location (location_ranges, complement_flag));
    } catch (ReadOnlyException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (EntryInformationException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (OutOfRangeException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }

    this.gff_lines = new StringVector (line);
  }

  /**
   *  Helper method for the constructor - returns a String that is the
   *  concatenation of the Strings in the given StringVector.  The strings
   *  will be separated by four spaces
   **/
  private String joinStringVector (final StringVector string_vector) {
    final StringBuffer return_buffer = new StringBuffer ();

    for (int i = 0 ; i < string_vector.size () ; ++i) {
      if (i != 0) {
        return_buffer.append ("    ");
      }
      return_buffer.append (string_vector.elementAt (i));
    }

    return return_buffer.toString ();
  }

  /**
   *  Read and return a GFFStreamFeature from a stream.  A feature must be the
   *  next thing in the stream.
   *  @param stream the Feature is read from this stream
   *  @exception IOException thrown if there is a problem reading the Feature -
   *    most likely ReadFormatException.
   *  @exception InvalidRelationException Thrown if this Feature cannot contain
   *    the given Qualifier.
   *  @return null if in_stream is at the end of file when the method is
   *    called
   */
  protected static GFFStreamFeature readFromStream (LinePushBackReader stream)
      throws IOException, InvalidRelationException {

    String line = stream.readLine ();

    if (line == null) {
      return null;
    }

    try {
      final GFFStreamFeature new_feature = new GFFStreamFeature (line);

      return new_feature;
    } catch (ReadFormatException exception) {
      // re-throw the exception with the line number added

      final String new_error_string = exception.getMessage ();

      throw new ReadFormatException (new_error_string,
                                     stream.getLineNumber ());
    }
  }

  /**
   *  Read the details of a feature from an EMBL stream into the current
   *  object.
   *  @param entry_information The EntryInformation object of the Entry that
   *    will contain the Feature.
   *  @param in_stream the Feature is read from this stream
   *  @exception IOException thrown if there is a problem reading the Feature -
   *    most likely ReadFormatException if the stream does not contain GFF
   *    feature.
   **/
  public void setFromStream (final EntryInformation entry_information,
                             final LinePushBackReader in_stream)
      throws IOException, InvalidRelationException, ReadOnlyException {
    throw new ReadOnlyException ();
  }

  /**
   *  Write this Feature to the given stream.
   *  @param writer The stream to write to.
   *  @exception IOException thrown if there is an io problem while writing
   *    the Feature.
   **/
  public void writeToStream (final Writer writer)
      throws IOException {
    // for now GFF features are read-only so just write what we read
    if (gff_lines == null) {
      final RangeVector ranges = getLocation ().getRanges ();

      for (int i = 0 ; i < ranges.size () ; ++i) {
        final Range this_range = ranges.elementAt (i);
        Qualifier   seqname    = getQualifierByName ("gff_seqname");
        Qualifier   source     = getQualifierByName ("gff_source");
        Qualifier   score      = getQualifierByName ("score");
        Qualifier   group      = getQualifierByName ("group");

        if (seqname == null) {
          seqname = new Qualifier ("gff_seqname", "");
        }

        if (source == null) {
          source = new Qualifier ("source", "");
        }

        if (score == null) {
          score = new Qualifier ("score", "");
        }

        if (group == null || group.getValues () == null ||
            group.getValues ().elementAt (0).equals ("")) {
          final Qualifier gene = getQualifierByName ("gene");

          if (gene == null) {
            group = new Qualifier ("group", "");
          } else {
            group = gene;
          }
        }

        String frame = ".";

        final Qualifier codon_start = getQualifierByName ("codon_start");

        if (codon_start != null && i == 0) {
          frame = codon_start.getValues ().elementAt (0);

          if (frame.equals ("1")) {
            frame = "0";
          } else {
            if (frame.equals ("2")) {
              frame = "1";
            } else {
              if (frame.equals ("3")) {
                frame = "2";
              } else {
                frame = ".";
              }
            }
          }
        }

        final String attribute_string = unParseAttributes ();

        writer.write (seqname.getValues ().elementAt (0) + "\t" +
                      source.getValues ().elementAt (0) + "\t" +
                      getKey () + "\t" +
                      this_range.getStart () + "\t" +
                      this_range.getEnd () + "\t" +
                      score.getValues () .elementAt (0)+ "\t" +
                      (getLocation ().isComplement () ? "-\t" : "+\t") +
                      frame + "\t" +
                      attribute_string + "\n");
      }
    } else {
      for (int i = 0 ; i < gff_lines.size () ; ++i) {
        writer.write (gff_lines.elementAt (i) + "\n");
      }
    }
  }

  /**
   *  Return a String containing the qualifiers of this feature in a form
   *  suitable for using as the last field of a GFF line.  The codon_start
   *  attribute is not included since GFF has a frame field.  gff_seqname,
   *  gff_source and score aren't included since they have corresponding
   *  fields.
   **/
  private String unParseAttributes () {
    final StringBuffer buffer = new StringBuffer ();

    final QualifierVector qualifiers = getQualifiers ();

    final QualifierVector qualifiers_to_write = new QualifierVector ();

    for (int i = 0 ; i < qualifiers.size () ; ++i) {
      final Qualifier this_qualifier = qualifiers.elementAt (i);

      final String name = this_qualifier.getName ();

      if (name.equals ("codon_start") || name.equals ("gff_source") ||
          name.equals ("gff_seqname") || name.equals ("score")) {
        continue;
      }

      if (i != 0) {
        buffer.append (" ; ");
      }

      final StringVector values = this_qualifier.getValues ();

      buffer.append (name);

      if (values != null) {
        for (int value_index = 0 ;
             value_index < values.size () ;
             ++value_index) {
          final String this_value = values.elementAt (value_index);

          buffer.append (' ');
          try {
            buffer.append (Integer.valueOf (this_value));
          } catch (NumberFormatException _) {
            // not an integer
            try {
              buffer.append (Double.valueOf (this_value));
            } catch (NumberFormatException __) {
              // not a double or integer so quote it
              buffer.append ('"' + this_value + '"');
            }
          }
        }
      }
    }

    return buffer.toString ();
  }

  /**
   *  Parse the given String as ACeDB format attributes.
   *  Adapted from code by Matthew Pocock for the BioJava project.
   *  @return Return a Hashtable.  Each key is an attribute name and each value
   *    of the Hashtable is a StringVector containing the attribute values.
   *    If the attribute has no value then the Hashtable value will be a zero
   *    length vector.
   **/
  private Hashtable parseAttributes (final String att_val_list) {
    Hashtable attributes = new Hashtable ();

    StringTokenizer tokeniser = new StringTokenizer (att_val_list, ";", false);

    while (tokeniser.hasMoreTokens()) {
      final String this_token = tokeniser.nextToken().trim();
      final int index_of_first_space = this_token.indexOf(" ");

      final String att_name;
      final StringVector att_values = new StringVector ();

      if (index_of_first_space == -1) {
        att_name = this_token;
      } else {
        att_name = this_token.substring (0, index_of_first_space);

        String rest_of_token =
          this_token.substring (index_of_first_space).trim();

        while (rest_of_token.length () > 0) {
          if (rest_of_token.startsWith ("\"")) {
            int quote_index = 0;
            do {
              quote_index++;
              quote_index = rest_of_token.indexOf ("\"", quote_index);
            } while (quote_index > -1 &&
                     rest_of_token.charAt(quote_index - 1) == '\\');

            if (quote_index < 0) {
              // no closing quote - panic
              final Hashtable panic_attributes = new Hashtable ();
              final StringVector notes = new StringVector ();
              notes.add (att_val_list);
              panic_attributes.put ("note", notes);

              return panic_attributes;
            }

            final String next_bit = rest_of_token.substring (1, quote_index);
            att_values.add (next_bit);
            rest_of_token = rest_of_token.substring (quote_index + 1).trim();
          } else {
            final int index_of_next_space = rest_of_token.indexOf (" ");

            if (index_of_next_space == -1) {
              att_values.add (rest_of_token);
              rest_of_token = "";
            } else {
              final String next_bit =
                rest_of_token.substring (0, index_of_next_space);

              att_values.add (next_bit);
              rest_of_token =
                rest_of_token.substring (index_of_next_space).trim ();
            }
          }
        }
      }

      if (attributes.get (att_name) != null) {
        ((StringVector) attributes.get (att_name)).add (att_values);
      } else {
        attributes.put (att_name, att_values);
      }
    }

    return attributes;
  }

  /**
   *  The DocumentEntry object that contains this Feature as passed to the
   *  constructor.
   **/
  private DocumentEntry entry;

  /**
   *  This is the line of GFF input that was read to get this
   *  GFFStreamFeature.  A GFFStreamFeature that was created from multiple GFF
   *  lines will have a gff_lines variable that contains multiple line.
   **/
  StringVector gff_lines = null;
}
