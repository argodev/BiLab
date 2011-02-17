/* AlignMatch.java
 *
 * created: Wed Jul 14 1999
 *
 * This file is part of Artemis
 *
 * Copyright(C) 1999,2000  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/AlignMatch.java,v 1.1 2004/06/09 09:44:06 tjc Exp $
 */

package uk.ac.sanger.artemis;

import uk.ac.sanger.artemis.io.Range;

/**
 *  Each object of this class represents a single match from an alignment.
 *
 *  @author Kim Rutherford
 *  @version $Id: AlignMatch.java,v 1.1 2004/06/09 09:44:06 tjc Exp $
 **/

public class AlignMatch 
{
  /** The range of the match in the subject sequence. */
  private Range subject_sequence_range = null;

  /** The range of the match in the query sequence. */
  private Range query_sequence_range = null;

  /** The score that was passed to the constructor. */
  private int score = -1;

  /** 
   *  The percent identity that was passed to the constructor.
   **/
  private int percent_id = -1;

  /**
   *  true if and only if the query hits the reverse complement of the subject
   **/
  private boolean rev_match;

  /**
   *  Create a new AlignMatch object.
   *  @param rev_match true if and only if the query hits the reverse
   *    complement of the subject.
   *  @param score the score for this match, which should be -1 if and only if
   *    this match has no score.  The parameter must be >= -1.
   **/
  public AlignMatch(final Range subject_sequence_range,
                    final Range query_sequence_range,
                    final boolean rev_match,
                    final int score,
                    final int percent_id) 
  {
    this.subject_sequence_range = subject_sequence_range;
    this.query_sequence_range   = query_sequence_range;
    this.rev_match              = rev_match;
    this.score                  = score;
    this.percent_id             = percent_id;
  }

  /**
   *  Return the start(base) of the match in the subject sequence.
   **/
  public int getSubjectSequenceStart() 
  {
    return subject_sequence_range.getStart();
  }

  /**
   *  Return the end(base) of the match in the subject sequence.
   **/
  public int getSubjectSequenceEnd() 
  {
    return subject_sequence_range.getEnd();
  }

  /**
   *  Return the start(base) of the match in the query sequence.
   **/
  public int getQuerySequenceStart() 
  {
    if(isRevMatch())
      return query_sequence_range.getEnd();
    else
      return query_sequence_range.getStart();
  }

  /**
   *  Return the end(base) of the match in the query sequence.
   **/
  public int getQuerySequenceEnd() 
  {
    if(isRevMatch()) 
      return query_sequence_range.getStart();
    else 
      return query_sequence_range.getEnd();
  }

  /**
   *  Return the Range of the match in the subject sequence.
   **/
  public Range getSubjectSequenceRange() 
  {
    return subject_sequence_range;
  }

  /**
   *  Return the Range of the match in the query sequence.
   **/
  public Range getQuerySequenceRange() 
  {
    return query_sequence_range;
  }

  /**
   *  Returns true if and only if the query hits the reverse complement of the
   *  subject.
   **/
  public boolean isRevMatch() 
  {
    return rev_match;
  }

  /**
   *  Return the score of this match.
   **/
  public int getScore() 
  {
    return score;
  }

  /**
   *  Return the percent identity of this match.
   **/
  public int getPercentID() 
  {
    return percent_id;
  }

  /**
   *  Return true if and only if this is a self match(ie query start ==
   *  subject start && query end == subject end)
   **/
  public boolean isSelfMatch() 
  {
    if(getQuerySequenceStart() == getSubjectSequenceStart() && 
        getQuerySequenceEnd() == getSubjectSequenceEnd()) 
      return true;
    else 
      return false;
  }

}
