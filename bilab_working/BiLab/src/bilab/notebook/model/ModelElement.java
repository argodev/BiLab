/**
* This document is a part of the source code and related artifacts for BiLab, an open source interactive workbench for 
* computational biologists.
*
* http://computing.ornl.gov/
*
* Copyright © 2011 Oak Ridge National Laboratory
*
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
* Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any 
* later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
* details.
*
* You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to 
* the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
* The license is also available at: http://www.gnu.org/copyleft/lgpl.html
*/

/*******************************************************************************
 * Copyright (c) 2004 Elias Volanakis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
�* Contributors:
�*����Elias Volanakis - initial API and implementation
�*******************************************************************************/
package bilab.notebook.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Abstract prototype of a model element.
 * <p>This class provides features necessary for all model elements, like:</p>
 * <ul>
 * <li>property-change support (used to notify edit parts of model changes),</li> 
 * <li>property-source support (used to display property values in the Property View) and</li>
 * <li>serialization support (the model hierarchy must be serializable, so that the editor can
 * save and restore a binary representation. You might not need this, if you store the model
 * a non-binary form like XML).</li>
 * </ul>
 * @author Elias Volanakis
 */
public abstract class ModelElement implements IPropertySource, Serializable {
/** An empty property descriptor. */
private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

private static final long serialVersionUID = 1;
/** Delegate used to implemenent property-change-support. */
private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

/** 
 * Attach a non-null PropertyChangeListener to this object.
 * @param l a non-null PropertyChangeListener instance
 * @throws IllegalArgumentException if the parameter is null
 */
public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
	if (l == null) {
		throw new IllegalArgumentException();
	}
	pcsDelegate.addPropertyChangeListener(l);
}

/** 
 * Report a property change to registered listeners (for example edit parts).
 * @param property the programmatic name of the property that changed
 * @param oldValue the old value of this property
 * @param newValue the new value of this property
 */
protected void firePropertyChange(String property, Object oldValue, Object newValue) {
	if (pcsDelegate.hasListeners(property)) {
		pcsDelegate.firePropertyChange(property, oldValue, newValue);
	}
}

/**
 * Returns a value for this property source that can be edited in a property sheet.
 * <p>My personal rule of thumb:</p>
 * <ul>
 * <li>model elements should return themselves and</li> 
 * <li>custom IPropertySource implementations (like DimensionPropertySource in the GEF-logic
 * example) should return an editable value.</li>
 * </ul>
 * <p>Override only if necessary.</p>
 * @return this instance
 */
public Object getEditableValue() {
	return this;
}

/**
 * Children should override this. The default implementation returns an empty array.
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return EMPTY_ARRAY;
}

/**
 * Children should override this. The default implementation returns null.
 */
public Object getPropertyValue(Object id) {
	return null;
}

/**
 * Children should override this. The default implementation returns false.
 */
public boolean isPropertySet(Object id) {
	return false;
}

/** 
 * Deserialization constructor. Initializes transient fields.
 * @see java.io.Serializable
 */
private void readObject(ObjectInputStream in) 
throws IOException, ClassNotFoundException {
	in.defaultReadObject();
	pcsDelegate = new PropertyChangeSupport(this);
}

/** 
 * Remove a PropertyChangeListener from this component.
 * @param l a PropertyChangeListener instance
 */
public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
	if (l != null) {
		pcsDelegate.removePropertyChangeListener(l);
	}
}

/**
 * Children should override this. The default implementation does nothing.
 */
public void resetPropertyValue(Object id) {
	// do nothing
}

/**
 * Children should override this. The default implementation does nothing.
 */
public void setPropertyValue(Object id, Object value) {
	// do nothing
}
}
