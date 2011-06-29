/* FeatureSegmentVector.java
 *
 * created: Thu Oct 29 1998
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/FeatureSegmentVector.java,v 1.1 2004/06/09 09:44:48 tjc Exp $
 */

package uk.ac.sanger.artemis;

import uk.ac.sanger.artemis.util.FastVector;

/**
 *  This class implements a Vector of FeatureSegment objects.
 *
 *  @author Kim Rutherford
 *  @version $Id: FeatureSegmentVector.java,v 1.1 2004/06/09 09:44:48 tjc Exp $
 *
 **/

public class FeatureSegmentVector {
  /**
   *  Create a new vector of FeatureSegment objects.
   **/
  public FeatureSegmentVector () {
    
  }

  /**
   *  Performs the same function as Vector.addElement ()
   **/
  public void addElement (FeatureSegment segment) {
    vector.add (segment);
  }

  /**
   *  Performs the same function as Vector.addElement ()
   **/
  public void add (FeatureSegment segment) {
    vector.add (segment);
  }

  /**
   *  Performs the same function as Vector.insertElementAt ()
   **/
  public void insertElementAt (FeatureSegment segment, int index) {
    vector.insertElementAt (segment, index);
  }

  /**
   *  Performs the same function as Vector.setElementAt ()
   **/
  public void setElementAt (FeatureSegment segment, int index) {
    vector.setElementAt (segment, index);
  }

  /**
   *  Performs the same function as Vector.elementAt ()
   **/
  public FeatureSegment elementAt (int index) {
    return (FeatureSegment) vector.elementAt (index);
  }

  /**
   *  Performs the same function as Vector.lastElement ()
   **/
  public FeatureSegment lastElement () {
    return (FeatureSegment) vector.lastElement ();
  }

  /**
   *  Performs the same function as Vector.removeElement ()
   **/
  public boolean removeElement (FeatureSegment segment) {
    return vector.remove (segment);
  }
  
  /**
   *  Performs the same function as Vector.indexOf ()
   **/
  public int indexOf (FeatureSegment segment) {
    return vector.indexOf (segment);
  }

  /**
   *  Return true if this object contains the given FeatureSegment.
   **/
  public boolean contains (FeatureSegment segment) {
    if (indexOf (segment) == -1) {
      return false;
    } else {
      return true;
    }
  }

  /**
   *  Performs the same function as Vector.removeAllElement ()
   **/
  public void removeAllElements () {
    vector.removeAllElements ();
  }

  /**
   *  Performs the same function as Vector.removeElementAt ()
   **/
  public void removeElementAt (int index) {
    vector.removeElementAt (index);
  }

  /**
   *  Performs the same function as Vector.size ()
   */
  public int size () {
    return vector.size ();
  }

  /**
   *  Add a feature to the end of the Vector.
   **/
  public void addElementAtEnd (FeatureSegment segment) {
    vector.insertElementAt (segment, size ());
  }

  /**
   *  Create a new FeatureVector with the same contents as this one.
   **/
  public Object clone () {
    final FeatureSegmentVector return_vector = new FeatureSegmentVector ();
    return_vector.vector = (FastVector) vector.clone ();
    return return_vector;
  }

  /**
   *  Storage for FeatureSegment objects.
   */
  private FastVector vector = new FastVector ();
}
