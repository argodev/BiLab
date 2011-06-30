/* TextRequesterEvent.java
 *
 * created: Wed Jan 13 1999
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
 * $Header: /cvsroot/pathsoft/artemis/uk/ac/sanger/artemis/components/TextRequesterEvent.java,v 1.1 2004/06/09 09:47:55 tjc Exp $
 **/

package uk.ac.sanger.artemis.components;

/**
 *  This event is sent when the user presses OK or Cancel in a TextRequester
 *  component.
 *
 *  @author Kim Rutherford
 *  @version $Id: TextRequesterEvent.java,v 1.1 2004/06/09 09:47:55 tjc Exp $
 **/

public class TextRequesterEvent extends java.util.EventObject {
  /**
   *  Event type - This event was generated by pressing the OK button.
   **/
  final public static int OK = 1;

  /**
   *  Event type - This event was generated by pressing the CANCEL button.
   **/
  final public static int CANCEL = 2;

  /**
   *  Create a new TextRequesterEvent object.
   *  @param source The TextRequester that generated the event.
   *  @param requester_text The contents of the TextField in the TextRequester.
   *  @param type The type of event.
   **/
  public TextRequesterEvent (final TextRequester source,
                             final String requester_text,
                             final int type) {
    super (source);
    this.requester_text = requester_text;
    this.type           = type;
  }

  /**
   *  Return the type of this event as passed to the constructor.
   **/
  public int getType () {
    return type;
  }

  /**
   *  Return the TextRequester contents String that was passed to the
   *  constructor.
   **/
  public String getRequesterText () {
    return requester_text;
  }

  /**
   *  This is the type of this event, as passed to the constructor
   **/
  private int type;

  /**
   *  The TextRequester contents String that was passed to the constructor.
   **/
  private String requester_text;
}


