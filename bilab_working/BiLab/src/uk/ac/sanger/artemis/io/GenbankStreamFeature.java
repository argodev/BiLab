/* GenbankStreamFeature.java
 *
 * created: Mon Sep 13 1999
 *
 * This file is part of Artemis
 * 
 * Copyright (C) 1999  Genome Research Limited
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/io/GenbankStreamFeature.java,v 1.1 2004/06/09 09:49:37 tjc Exp $
 */

package uk.ac.sanger.artemis.io;

import uk.ac.sanger.artemis.util.LinePushBackReader;

import java.io.*;

/**
 *  A StreamFeature that thinks it is a GENBANK feature.
 *
 *  @author Kim Rutherford
 *  @version $Id: GenbankStreamFeature.java,v 1.1 2004/06/09 09:49:37 tjc Exp $
 **/

public class GenbankStreamFeature extends PublicDBStreamFeature {
  /**
   *  Create a new GenbankStreamFeature object.
   *  @param key The new feature key
   *  @param location The Location object for the new feature
   *  @param qualifiers The qualifiers for the new feature
   *  @exception InvalidRelationException Thrown if this Feature cannot contain
   *    the given Qualifier.
   **/
  public GenbankStreamFeature (Key key,
                               Location location,
                               QualifierVector qualifiers)
      throws InvalidRelationException {
    super (key, location, qualifiers);
  }

  /**
   *  Create a new GenbankStreamFeature with the same key, location and
   *  qualifiers as the given feature.  The feature should be added to an
   *  Entry (with Entry.add ()).
   *  @param feature The feature to copy.
   **/
  public GenbankStreamFeature (final Feature feature) {
    super (feature);
  }


  /**
   *  Create a new, blank GENBANK feature.  It will have no qualifiers, a key
   *  of CDS and a location of 1.
   **/
  public GenbankStreamFeature () {
    super (makeBlankFeature ());
  }

  /**
   *  Called by the constructor.
   **/
  private static EmblStreamFeature makeBlankFeature () {
    try {
      return new EmblStreamFeature (Key.CDS, new Location ("1"),
                                    new QualifierVector ()); 
    } catch (InvalidRelationException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    } catch (LocationParseException e) {
      throw new Error ("internal error - unexpected exception: " + e);
    }
  }

  /**
   *  Read and return a GenbankStreamFeature from a stream.  A feature must be
   *  the next thing in the stream.
   *  @param stream the Feature is read from this stream
   *  @exception IOException thrown if there is a problem reading the Feature -
   *    most likely ReadFormatException.
   *  @return null if in_stream is at the end of file when the method is called
   *  
   **/
  public static GenbankStreamFeature
    readFromStream (final LinePushBackReader in_stream)
      throws IOException {
    return (GenbankStreamFeature) readFromStream (in_stream,
                                                  LineGroup.GENBANK_FEATURE);
  }

  /**
   *  Return the reference of a new copy of this Feature.
   **/
  public Feature copy () {
    final Feature return_value = new GenbankStreamFeature (this);

//    System.out.println (return_value.getEntry ());

    return return_value;
  }
}
