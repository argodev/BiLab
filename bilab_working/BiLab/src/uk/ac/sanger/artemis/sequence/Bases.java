/* Bases.java
 *
 * created: Sun Oct 11 1998
 *
 * This file is part of Artemis
 *
 * Copyright (C) 1998,1999,2000  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/sequence/Bases.java,v 1.1 2004/06/09 09:52:15 tjc Exp $
 */

package uk.ac.sanger.artemis.sequence;

import uk.ac.sanger.artemis.util.*;
import uk.ac.sanger.artemis.io.Range;
import uk.ac.sanger.artemis.io.EmblStreamSequence;
import uk.ac.sanger.artemis.io.Sequence;

import org.biojava.bio.symbol.IllegalSymbolException;

import java.util.Vector;
import java.util.WeakHashMap;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *  This class is a wrapper for the uk.ac.sanger.artemis.io.Sequence class
 *  that allows us to control what is done to the sequence and to send events
 *  to interested objects when changes happen.  Note: a '@' character is used
 *  as a marker when we don't have a base letter, for example complementing a
 *  non-base letter returns '@'.
 *
 *  @author Kim Rutherford
 *  @version $Id: Bases.java,v 1.1 2004/06/09 09:52:15 tjc Exp $ */

public class Bases {
  /**
   *  Indicates the bases should be read in the forward direction for a
   *  particular operation.
   **/
  static public final int FORWARD = 1;

  /**
   *  Indicates the bases should be read in the reverse direction for a
   *  particular operation.
   **/
  static public final int REVERSE = 2;

  /**
   *  The lowest possible value for use with addSequenceChangeListener ().
   **/
  static public final int MIN_PRIORITY = -5;

  /**
   *  An arbitrary value for use with addSequenceChangeListener ().
   **/
  static public final int MEDIUM_PRIORITY = 0;

  /**
   *  The highest possible value for use with addSequenceChangeListener ().
   **/
  static public final int MAX_PRIORITY = 5;

  /**
   *  Create a new Bases object.
   *  @param sequence The raw sequence that the new object will use.
   **/
  public Bases (final Sequence sequence) {
    this.embl_sequence = sequence;

    forward_stop_codon_cache = null;
    reverse_stop_codon_cache = null;

    forward_strand = new Strand (this, FORWARD);
    reverse_strand = new Strand (this, REVERSE);

    for (int i = 0 ; i < listener_hash_map_array.length ; ++i) {
      listener_hash_map_array [i] = new WeakHashMap ();
    }
  }

  /**
   *  Return the object representing the forward sequence of bases for this
   *  object.
   **/
  public Strand getForwardStrand () {
    return forward_strand;
  }

  /**
   *  Return the object representing the reverse complemented sequence of
   *  bases for this Bases objects.
   **/
  public Strand getReverseStrand () {
    return reverse_strand;
  }

  /**
   *  Returns the length of the sequence in bases.
   **/
  public int getLength () {
    return embl_sequence.length ();
  }

  /**
   *  Return a String representation of the sequence.
   **/
  public String toString () {
    return embl_sequence.toString ();
  }

  /**
   *  Reverse and complement both of the Strand objects (by swapping them and
   *  reverse complementing the sequence).
   *  @exception ReadOnlyException If the Bases cannot be changed.
   **/
  public void reverseComplement ()
      throws ReadOnlyException {
    forward_stop_codon_cache = null;
    reverse_stop_codon_cache = null;

    final Strand temp = forward_strand;
    forward_strand = reverse_strand;
    reverse_strand = temp;

    final String new_sequence =
      reverseComplement (getSequence ().getSubSequence (1, getLength ()));

    try {
      getSequence ().setFromString (new_sequence);
    } catch (IllegalSymbolException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }

    final SequenceChangeEvent event =
      new SequenceChangeEvent (this, SequenceChangeEvent.REVERSE_COMPLEMENT);

    fireSequenceChangeEvent (event);
  }

  /**
   *  This array is used to convert between bases and indices.  See
   *  getIndexOfBase ()
   **/
  public final static char [] letter_index = {
    't', 'c', 'a', 'g', 'n'
  };

  /**
   *  Given a base letter return its index where t = 0, c = 1, a = 2, g = 3, 4
   *  otherwise.
   *  See letter_index.
   **/
  public static int getIndexOfBase (final char base) {
    switch (base) {
    case 't':
    case 'u':
      return 0;
    case 'c':
      return 1;
    case 'a':
      return 2;
    case 'g':
      return 3;
    }

    return 4;
  }

  /**
   *  Return the complement of the given Range.  eg. if the sequence length is
   *  100 and the Range is 1..10 then the return value will be 90..100.
   **/
  public Range complementRange (final Range range) {
    final int real_start = getComplementPosition (range.getEnd ());
    final int real_end   = getComplementPosition (range.getStart ());

    try {
      final Range real_range = new Range (real_start, real_end);

      return real_range;
    } catch (OutOfRangeException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }
  }

  /**
   *  Return the complement of the given position on the sequence.  eg. if the
   *  sequence length is 100 and the position is 10 then the return value will
   *  be 90.
   **/
  public int getComplementPosition (final int position) {
    return getLength () - position + 1;
  }

  /**
   *  Return the raw of a base position on this object.  The raw position of a
   *  base on the forward strand is the same as the position itself.  The raw
   *  position of a base on the reverse strand is position of the
   *  corresponding bases on the forward strand.
   *  @param position The position of the base.
   *  @param direction The direction (strand) that the position refers to.
   **/
  public int getRawPosition (final int position, final int direction) {
    if (direction == FORWARD) {
      return position;
    } else {
      return getComplementPosition (position);
    }
  }

  /**
   *  Translate a sequence of bases into the corresponding single letter amino
   *  acid codes.
   *  @param range The range of the bases to translated.  If the range.start
   *    - range.end + 1 is not a multiple of three the last codon is
   *    incomplete and will not be translated.  If the range is out of range
   *    ie. it has a start or end less than one or greater than the length of
   *    the sequence, then the out of range codons will be translated as '.'.
   *  @param direction The direction of the translation.  If FORWARD the
   *    translation will happen as expected, if REVERSE the translation will
   *    be done on the reverse complement.
   *  @param unknown_is_x If this parameter is true codons that contain
   *    ambiguous bases will be translated as 'x', if false they will be
   *    translated as '.'
   *  @return The translated sequence in one letter abbreviated form.
   **/
  public AminoAcidSequence getTranslation (final Range range,
                                           final int direction,
                                           final boolean unknown_is_x) {
    // getSubSequence () will return a sequence going in the right direction
    // so we don't have to worry.
    final String sub_sequence = getSubSequence (range, direction);

    return AminoAcidSequence.getTranslation (sub_sequence, unknown_is_x);
  }

  /**
   *  Return an array containing the positions of the codons that match the
   *  strings given by the query_codons argument.  Only those codons that are
   *  in the same frame as the first base of the range are returned.
   *  @param range The inclusive range of bases to get the codons from.
   *  @param direction The direction of the translation.  REVERSE means
   *    translate the reverse complement bases (the positions in the range
   *    argument are complemented first.)
   *  @param query_codons The codons to search for.  Each element of this
   *    vector should be a string that is 3 characters long.
   *  @return An array containing the positions of the first base of the
   *    codons.  This array is padded with zeros at the end.
   **/
  public int [] getMatchingCodons (final Range range, final int direction,
                                   final StringVector query_codons) {
    final Range real_range;

    if (direction == FORWARD) {
      real_range = range;
    } else {
      real_range = complementRange (range);
    }

    // guess the number of codons in getCount () bases - there are
    // query_codons.size() search codons in every 64 codons if G+C is 50%
    // and we have getCount()/3 codons to look at.

    float at_content = (100 - getAverageGCPercent ()) / 100;

    int array_start_size =
      (int) (range.getCount () *
             at_content * at_content * (2 - at_content) *
             query_codons.size () / 64);

    if (array_start_size < 20) {
      array_start_size = 20;
    }

    // this array will be resized as necessary
    int [] return_positions = new int [array_start_size];

    int current_return_array_index = 0;

    final String sequence_string =
      getSequence ().getSubSequence (1, getLength ());

    final int range_start_index = real_range.getStart () - 1;
    final int range_end_index = real_range.getEnd () - 1;

    if (direction == FORWARD) {
      for (int i = range_start_index ; i < range_end_index - 2 ; i += 3) {
        if (i < 0 || i >= sequence_string.length () - 2) {
          continue;
        }

        boolean is_matching_codon =
          isMatchingCodon (sequence_string, i, direction, query_codons);

        if (is_matching_codon) {
          if (current_return_array_index == return_positions.length) {
            // first reallocate the array
            final int [] new_array =
              new int [return_positions.length * 3 / 2 + 1];

            System.arraycopy (return_positions, 0,
                              new_array, 0,
                              return_positions.length);
            return_positions = new_array;
          }

          return_positions[current_return_array_index] = i + 1;

          ++current_return_array_index;
        }
      }
    } else {

      for (int i = range_end_index ; i > range_start_index + 2 ; i -= 3) {
        if (i < 2 || i >= sequence_string.length ()) {
          continue;
        }

        boolean is_matching_codon =
          isMatchingCodon (sequence_string, i, direction, query_codons);

        if (is_matching_codon) {
          if (current_return_array_index == return_positions.length) {
            // first reallocate the array
            final int [] new_array =
              new int [return_positions.length * 3 / 2 + 1];

            System.arraycopy (return_positions, 0,
                              new_array, 0,
                              return_positions.length);
            return_positions = new_array;
          }

          // return the complemented base position
          return_positions[current_return_array_index] =
            sequence_string.length () - i;

          ++current_return_array_index;
        }
      }
    }

    return return_positions;

  }

  /**
   *  Check a three character substring and return true if and only if the
   *  three bases match an element of the query_codons argument.  If the
   *  direction is REVERSE then the three bases to check are at start_index,
   *  start_index - 1 and start_index - 2.  In that case true is returned if
   *  and only the complement of those three bases matches.
   **/
  private boolean isMatchingCodon (final String sequence_string,
                                   final int start_index,
                                   final int direction,
                                   final StringVector query_codons) {
    for (int query_codon_index = 0 ;
         query_codon_index < query_codons.size () ;
         ++query_codon_index) {
      if (isMatchingCodon (sequence_string, start_index, direction,
                           query_codons.elementAt (query_codon_index))) {
        return true;
      }
    }

    return false;
  }

  /**
   *  Check a three character substring and return true if and only if the
   *  three bases match the query_codon argument.  If the direction is
   *  REVERSE then the three bases to check are at start_index, start_index -
   *  1 and start_index - 2.  In that case true is returned if and only the
   *  complement of those three bases matches.
   **/
  private boolean isMatchingCodon (final String sequence_string,
                                   final int start_index,
                                   final int direction,
                                   final String query_codon) {
    if (direction == FORWARD) {
      if (query_codon.charAt (0) == sequence_string.charAt (start_index) &&
          query_codon.charAt (1) == sequence_string.charAt (start_index + 1) &&
          query_codon.charAt (2) == sequence_string.charAt (start_index + 2)) {
        return true;
      }
    } else {
      final char first_letter =
        complement (sequence_string.charAt (start_index));
      final char second_letter =
        complement (sequence_string.charAt (start_index - 1));
      final char third_letter =
        complement (sequence_string.charAt (start_index - 2));

      if (query_codon.charAt (0) == first_letter &&
          query_codon.charAt (1) == second_letter &&
          query_codon.charAt (2) == third_letter) {
        return true;
      }
    }

    return false;
  }

  /**
   *  A cache of the forward stop codon positions.
   *  0 means not set/not cached yet, 1 not a stop codon, 2 is a stop codon.
   **/
  private byte [] forward_stop_codon_cache = null;

  /**
   *  A cache of the reverse stop codon positions.
   *  0 means not set/not cached yet, 1 not a stop codon, 2 is a stop codon.
   **/
  private byte [] reverse_stop_codon_cache = null;

  /**
   *  Returns forward_stop_codon_cache after allocating it (if it is null).
   **/
  private byte [] getForwardStopCodonCache () {
    if (forward_stop_codon_cache == null) {
      forward_stop_codon_cache = new byte [getLength ()];
    }

    return forward_stop_codon_cache;
  }

  /**
   *  Returns reverse_stop_codon_cache after allocating it (if it is null).
   **/
  private byte [] getReverseStopCodonCache () {
    if (reverse_stop_codon_cache == null) {
      reverse_stop_codon_cache = new byte [getLength ()];
    }

    return reverse_stop_codon_cache;
  }

  /**
   *  Return an array containing the positions of the stop codons.  Only those
   *  codons that are in the same frame as the first base of the range are
   *  returned.
   *  @param range The inclusive range of bases to get the stop codons from.
   *  @param direction The direction of the translation.  REVERSE means
   *    translate the reverse complement bases (the positions in the range
   *    argument are complemented first.)
   *  @return An array containing the positions of the first base of the stop
   *    codons.  This array is padded with zeros at the end.
   **/
  public int [] getStopCodons (final Range range, final int direction) {
    final Range real_range;

    if (direction == FORWARD) {
      real_range = range;
    } else {
      real_range = complementRange (range);
    }

    // guess the number of stop codons in getCount () bases - there are 3
    // stop codons in every 64 codons if G+C is 50% and we have getCount()/3
    // codons to look at.

    float at_content = (100 - getAverageGCPercent ()) / 100;

    int array_start_size =
      (int) (range.getCount () *
             at_content * at_content * (2 - at_content) * 3 / 64);

    if (array_start_size < 20) {
      array_start_size = 20;
    }

    // this array will be resized as necessary
    int [] return_positions = new int [array_start_size];

    int current_return_array_index = 0;

    final String sequence_string =
      getSequence ().getSubSequence (1, getLength ());

    final int range_start_index = real_range.getStart () - 1;
    final int range_end_index = real_range.getEnd () - 1;

    final byte [] forward_stop_codon_flags = getForwardStopCodonCache ();
    final byte [] reverse_stop_codon_flags = getReverseStopCodonCache ();

    if (direction == FORWARD) {
      for (int i = range_start_index ; i < range_end_index - 2 ; i += 3) {
        if (i < 0 || i >= sequence_string.length () - 2) {
          continue;
        }

        final boolean is_stop_codon;

        if (forward_stop_codon_flags[i] == 0) {
          // not cached yet
          if (isStopCodon (sequence_string, i, direction)) {
            forward_stop_codon_flags[i] = 2;
            is_stop_codon = true;
          } else {
            forward_stop_codon_flags[i] = 1;
            is_stop_codon = false;
          }
        } else {
          // used the cached value
          if (forward_stop_codon_flags[i] == 2) {
            is_stop_codon = true;
          } else {
            is_stop_codon = false;
          }
        }

        if (is_stop_codon) {
          if (current_return_array_index == return_positions.length) {
            // first reallocate the array
            final int [] new_array =
              new int [return_positions.length * 3 / 2 + 1];

            System.arraycopy (return_positions, 0,
                              new_array, 0,
                              return_positions.length);
            return_positions = new_array;
          }

          if (is_stop_codon) {
            return_positions[current_return_array_index] = i + 1;
          } else {
            // negative position marks an illegal codon
            return_positions[current_return_array_index] = -(i + 1);
          }
          ++current_return_array_index;
        }
      }
    } else {

      for (int i = range_end_index ; i > range_start_index + 2 ; i -= 3) {
        if (i < 2 || i >= sequence_string.length ()) {
          continue;
        }

        final boolean is_stop_codon;

        if (reverse_stop_codon_flags[i] == 0) {
          if (isStopCodon (sequence_string, i, direction)) {
            reverse_stop_codon_flags[i] = 2;
            is_stop_codon = true;
          } else {
            reverse_stop_codon_flags[i] = 1;
            is_stop_codon = false;
          }
        } else {
          if (reverse_stop_codon_flags[i] == 2) {
            is_stop_codon = true;
          } else {
            is_stop_codon = false;
          }
        }

        if (is_stop_codon) {
          if (current_return_array_index == return_positions.length) {
            // first reallocate the array
            final int [] new_array =
              new int [return_positions.length * 3 / 2 + 1];

            System.arraycopy (return_positions, 0,
                              new_array, 0,
                              return_positions.length);
            return_positions = new_array;
          }

          if (is_stop_codon) {
            // return the complemented base position
            return_positions[current_return_array_index] =
              sequence_string.length () - i;
          } else {
            // return the complemented base position
            return_positions[current_return_array_index] =
              -(sequence_string.length () - i);
          }
          ++current_return_array_index;
        }
      }
    }

    return return_positions;
  }

  /**
   *  Return the base at the given position.
   **/
  public char getBaseAt (final int position)
      throws OutOfRangeException {
    if (position > getLength ()) {
      throw new OutOfRangeException (position + " > " + getLength ());
    }
    if (position < 1) {
      throw new OutOfRangeException (position + " < " + 1);
    }
    return toString ().charAt (position - 1);
  }

  /**
   *  Return a sub sequence of the bases from this object.
   *  @param range The range of the bases to be extracted.
   *  @param direction The direction of the returned sequence.  If FORWARD the
   *    sub sequence will be as expected, if REVERSE it will be reverse
   *    complemented.
   *  @return The extracted sequence, which will include the end bases of the
   *    range.
   **/
  public String getSubSequence (final Range range, final int direction) {
    final Range real_range;

    if (direction == FORWARD) {
      real_range = range;
    } else {
      real_range = complementRange (range);
    }

    // we need to make sure that we pass in-range coordinates to
    // Sequence.getSubSequence()
    final int sub_seq_start_index;
    final int sub_seq_end_index;

    if (real_range.getStart () < 1) {
      sub_seq_start_index = 1;
    } else {
      sub_seq_start_index = real_range.getStart ();
    }

    if (real_range.getEnd () > getLength ()) {
      sub_seq_end_index = getLength ();
    } else {
      sub_seq_end_index = real_range.getEnd ();
    }

    String sub_sequence =
      getSequence ().getSubSequence (sub_seq_start_index, sub_seq_end_index);

    // sanity checks - if the user asks for more bases than we
    // have, we return the symbol "@" for the out-of-range bases.
    if (real_range.getStart () < 1) {
      final int dummy_base_count = 1 - real_range.getStart ();
      final char [] dummy_bases = new char [dummy_base_count];

      for (int i = 0 ; i < dummy_base_count ; ++i) {
        dummy_bases[i] = '@';
      }

      sub_sequence = new String (dummy_bases) + sub_sequence;
    }

    if (real_range.getEnd () > getLength ()) {
      final int dummy_base_count = real_range.getEnd () - getLength ();
      final char [] dummy_bases = new char [dummy_base_count];

      for (int i = 0 ; i < dummy_base_count ; ++i) {
        dummy_bases[i] = '@';
      }

      sub_sequence = sub_sequence + new String (dummy_bases);
    }

    if (FORWARD == direction) {
      return sub_sequence;
    } else {
      return reverseComplement (sub_sequence);
    }
  }

  /**
   *  This method truncates the sequence use the start and end of the argument.
   *  @param constraint This contains the start and end base of the new
   *    sequence.
   *  @return the Bases truncated into the new coordinate system.
   **/
  public Bases truncate (final Range constraint) {
    final String bases_string = getSubSequence (constraint, FORWARD);

    final Sequence new_sequence = new EmblStreamSequence (bases_string);

    return new Bases (new_sequence);
  }

  /**
   *  Delete the bases in the given range and send out a SequenceChange event
   *  to all the listeners.
   *  @param range The inclusive range of bases to delete.
   *  @return A String containing the deleted bases.
   *  @exception ReadOnlyException If this Bases object cannot be changed.
   **/
  public String deleteRange (final Range range)
      throws ReadOnlyException {
    forward_stop_codon_cache = null;
    reverse_stop_codon_cache = null;

    final String removed_bases =
      getSequence ().getSubSequence (range.getStart (), range.getEnd ());

    final String new_sequence =
      getSequence ().getSubSequence (1, range.getStart () - 1) +
      getSequence ().getSubSequence (range.getEnd () + 1,
                                     embl_sequence.length ());

    try {
      embl_sequence.setFromString (new_sequence);
    } catch (IllegalSymbolException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } 

    final SequenceChangeEvent event =
      new SequenceChangeEvent (this,
                               SequenceChangeEvent.DELETION,
                               range.getStart (),
                               removed_bases);

    fireSequenceChangeEvent (event);

    return removed_bases;
  }

  /**
   *  Insert the given bases at the given base position and send out a
   *  SequenceChange event to all the listeners.
   *  @param position The bases are inserted just before this base position if
   *    direction is FORWARD or just after if direction is REVERSE.
   *  @param direction If this is FORWARD, then the bases is the bases String
   *    will be inserted just before the base given by position.  If this is
   *    REVERSE the bases will be reversed, complemented and inserted just
   *    after the position.
   *  @param bases The bases to add (or the reverse complement of the bases to
   *    add if direction is REVERSE).
   *  @exception ReadOnlyException If this Bases object cannot be changed.
   **/
  public void addBases (final int position, final int direction,
                        final String bases)
      throws ReadOnlyException, IllegalSymbolException {
    forward_stop_codon_cache = null;
    reverse_stop_codon_cache = null;

    final String new_sequence;

    final int real_position;

    final String real_bases;

    if (direction == FORWARD) {
      real_position = position;
      real_bases = bases.toLowerCase ();
    } else {
      real_position = position + 1;
      real_bases = reverseComplement (bases.toLowerCase ());
    }

    new_sequence =
      getSequence ().getSubSequence (1, real_position - 1) +
      real_bases +
      getSequence ().getSubSequence (real_position, getLength ());

    getSequence ().setFromString (new_sequence);

    final SequenceChangeEvent event =
      new SequenceChangeEvent (this,
                               SequenceChangeEvent.INSERTION,
                               real_position,
                               real_bases);

    fireSequenceChangeEvent (event);

    return;
  }

  /**
   *  There is one element in this array for each possible
   *  SequenceChangeListener priority.  This array is changed by
   *  addSequenceChangeListener() and removeSequenceChangeListener().
   **/
  final private WeakHashMap listener_hash_map_array [] =
    new WeakHashMap [MAX_PRIORITY - MIN_PRIORITY + 1];

  /**
   *  Adds the specified event listener to the list of object that receive
   *  sequence change events from this object.
   *  @param l the event change listener.
   *  @param priority The listeners are stored in a priority queue using this
   *    value.  Larger priority means that the listener will receive the event
   *    sooner (than lower priority listeners).  Values less than MIN_PRIORITY
   *    will be treated like MIN_PRIORITY values higher than MAX_PRIORITY will
   *    be treated like MAX_PRIORITY.
   **/
  public void addSequenceChangeListener (final SequenceChangeListener l,
                                         int priority) {
    if (priority < MIN_PRIORITY) {
      priority = MIN_PRIORITY;
    }

    if (priority > MAX_PRIORITY) {
      priority = MAX_PRIORITY;
    }

    listener_hash_map_array [priority - MIN_PRIORITY].put (l, null);
  }

  /**
   *  Removes the specified event listener so that it no longer receives
   *  sequence change events from this object.
   *  @param l the event change listener.
   **/
  public void removeSequenceChangeListener (final SequenceChangeListener l) {
    for (int i = 0 ; i < listener_hash_map_array.length ; ++i) {
      final WeakHashMap this_hash_map = listener_hash_map_array [i];

      if (this_hash_map.containsKey (l)) {
        this_hash_map.remove (l);
        return;
      }
    }
  }

  /**
   *  Send a SequenceChangeEvent to each object that is listening for it.
   **/
  private void fireSequenceChangeEvent (final SequenceChangeEvent event) {
    for (int i = listener_hash_map_array.length - 1 ; i >= 0 ; --i) {
      final WeakHashMap this_hash_map = listener_hash_map_array [i];

      if (this_hash_map != null) {
        final Iterator iter = this_hash_map.keySet ().iterator ();

        while (iter.hasNext()) {
          final SequenceChangeListener this_listener =
            (SequenceChangeListener) iter.next();
          this_listener.sequenceChanged (event);
        }
      }
    }
  }

  /**
   *  Return the average gc percent for the sequence.
   **/
  public float getAverageGCPercent () {
    return ((float)(getSequence ().getCCount () +
                    getSequence ().getGCount ())) /
      getSequence ().length () * 100;
  }

  /**
   *  Return the average AG percent for the sequence as a percentage.
   **/
  public float getAverageAGPercent () {
    return ((float)(getSequence ().getACount () +
                    getSequence ().getGCount ())) /
      getSequence ().length () * 100;
  }

  /**
   *  Return the number of 'A's in this Bases object.
   **/
  public int getACount () {
    return getSequence ().getACount ();
  }

  /**
   *  Return the number of 'T's in this Bases object.
   **/
  public int getTCount () {
    return getSequence ().getTCount ();
  }

  /**
   *  Return the number of 'G's in this Bases object.
   **/
  public int getGCount () {
    return getSequence ().getGCount ();
  }

  /**
   *  Return the number of 'C's in this Bases object.
   **/
  public int getCCount () {
    return getSequence ().getCCount ();
  }

  /**
   *  Return a String containing the reverse complement of the argument
   *  String.  For example an argument of "aatc" will result in "gatt".
   **/
  public static String reverseComplement (final String sequence_string) {
    StringBuffer return_buffer = new StringBuffer (sequence_string.length ());

    for (int i = sequence_string.length () - 1 ; i >= 0 ; --i) {
      return_buffer.append (complement (sequence_string.charAt (i)));
    }

    return return_buffer.toString ();
  }

  /**
   *  Return a String containing the complement of the argument String.  For
   *  example an argument of "aatc" will result in "ttag".
   **/
  public static String complement (final String sequence_string) {
    StringBuffer return_buffer = new StringBuffer (sequence_string.length ());

    for (int i = 0 ; i < sequence_string.length () ; ++i) {
      return_buffer.append (complement (sequence_string.charAt (i)));
    }

    return return_buffer.toString ();
  }

  /**
   *  Returns the complement base of it's argument - c for g, a for t etc.
   *  The argument may be upper or lower case, but the result is always lower
   *  case.  This also works for IUB base codes: the complement of 'y' is 'r'
   *  because 'y' is 'c' or 't' and 'r' is 'a' or 'g', the complement of 'n'
   *  or 'x' (any base) is 'n'.
   **/
  public static char complement (final char base) {
    switch (base) {
    case 'a': case 'A': return 't';
    case 't': case 'T': case 'u': case 'U': return 'a';
    case 'g': case 'G': return 'c';
    case 'c': case 'C': return 'g';
    case 'r': case 'R': return 'y';
    case 'y': case 'Y': return 'r';
    case 'k': case 'K': return 'm';
    case 'm': case 'M': return 'k';
    case 's': case 'S': return 's';
    case 'w': case 'W': return 'w';
    case 'b': case 'B': return 'v';
    case 'd': case 'D': return 'h';
    case 'h': case 'H': return 'd';
    case 'v': case 'V': return 'b';
    case 'n': case 'N': return 'n';
    case 'x': case 'X': return 'x';
    default:
      return '@';
//      throw new Error ("in Bases.complement - tried to complement a letter " +
//                       "that isn't a base");
    }
  }

  /**
   *  Return the Sequence object that was passed to the constructor.
   **/
  public Sequence getSequence () {
    return embl_sequence;
  }

  /**
   *  Check a three character substring and return true if and only if the
   *  three bases translate to a stop codon.  If the direction is REVERSE
   *  then the three bases to check are at start_index, start_index - 1 and
   *  start_index - 2.  In that case true is returned if and only the
   *  complement of those three bases is a stop codon.
   *  Codons that contain an X are considered to be stop codons.
   **/
  private static boolean isStopCodon (final String sequence_string,
                                      final int start_index,
                                      final int direction) {
    final char translation;

    if (direction == FORWARD) {
      final char first_letter = sequence_string.charAt (start_index);
      final char second_letter = sequence_string.charAt (start_index + 1);
      final char third_letter = sequence_string.charAt (start_index + 2);

      if (first_letter == 'x' || second_letter == 'x' || third_letter == 'x') {
        // codons that contain an X are considered to be stop codons.
        return true;
      }
      
      translation = AminoAcidSequence.getCodonTranslation (first_letter,
                                                           second_letter,
                                                           third_letter);
    } else {
      final char first_letter =
        complement (sequence_string.charAt (start_index - 2));
      final char second_letter =
        complement (sequence_string.charAt (start_index - 1));
      final char third_letter =
        complement (sequence_string.charAt (start_index));

      if (first_letter == 'x' || second_letter == 'x' || third_letter == 'x') {
        // codons that contain an X are considered to be stop codons.
        return true;
      }

      translation = AminoAcidSequence.getCodonTranslation (third_letter,
                                                           second_letter,
                                                           first_letter);
    }

    if (translation == '+' || translation == '*' || translation == '#') {
      return true;
    } else {
      return false;
    }
  }

  /**
   *  Check a three character substring and return true if and only if the
   *  three bases are legal (see isLegalBase ()).
   **/
  private static boolean isLegalCodon (final String sequence_string,
                                       final int start_index,
                                       final int direction) {
    if (direction == FORWARD) {
      if (isLegalBase (sequence_string.charAt (start_index)) &&
          isLegalBase (sequence_string.charAt (start_index + 1)) &&
          isLegalBase (sequence_string.charAt (start_index + 2))) {
        return true;
      }
    } else {
      if (isLegalBase (sequence_string.charAt (start_index)) &&
          isLegalBase (sequence_string.charAt (start_index - 1)) &&
          isLegalBase (sequence_string.charAt (start_index - 2))) {
        return true;
      }
    }

    // this isn't a stop codon
    return false;

  }

  /**
   *  Return true if and only if the given base character is one of 'a', 't',
   *  'c', 'g' or 'u'.
   **/
  public static boolean isLegalBase (final char base_char) {
    switch (base_char) {
    case 'a': case 'A': return true;
    case 't': case 'T': return true;
    case 'u': case 'U': return true;
    case 'g': case 'G': return true;
    case 'c': case 'C': return true;
    default:
      return false;
    }
  }

  /**
   *  The underlying sequence object that holds the data for this object.
   *  This is the same object that was passed to the constructor.
   **/
  private Sequence embl_sequence;

  /**
   *  The object representing the forward sequence of bases.
   **/
  private Strand forward_strand;

  /**
   *  The object representing the reverse (reverse complemented)
   *  sequence of bases.
   **/
  private Strand reverse_strand;
}
