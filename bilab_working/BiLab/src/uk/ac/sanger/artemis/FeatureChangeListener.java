/* FeatureChangeListener.java
 *
 * created: Wed Oct 21 1998
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/FeatureChangeListener.java,v 1.1 2004/06/09 09:44:38 tjc Exp $
 */

package uk.ac.sanger.artemis;

/**
 *  The FeatureChangeListener interface is implemented by those classes that
 *  need to listen for changes to Feature objects.
 *
 *  @author Kim Rutherford
 *  @version $Id: FeatureChangeListener.java,v 1.1 2004/06/09 09:44:38 tjc Exp $
 *
 **/

public interface FeatureChangeListener extends ChangeListener {
  /**
   *  Invoked when a Feature object is changed.
   **/
  void featureChanged (FeatureChangeEvent event);
}


