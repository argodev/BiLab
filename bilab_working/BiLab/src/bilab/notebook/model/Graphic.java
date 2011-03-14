/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
 */

package bilab.notebook.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import bilab.BilabPlugin;

/**
 * Abstract prototype of a shape. Has a size (width and height), a location (x
 * and y position) and a list of incoming and outgoing connections. Use
 * subclasses to instantiate a specific shape.
 * 
 * @see org.eclipse.gef.examples.shapes.model.RectangularGraphic
 * @see org.eclipse.gef.examples.shapes.model.EllipticalGraphic
 * @author Elias Volanakis
 */
public abstract class Graphic extends ModelElement {

    /**
     * A static array of property descriptors. There is one IPropertyDescriptor
     * entry per editable property.
     * 
     * @see #getPropertyDescriptors()
     * @see #getPropertyValue(Object)
     * @see #setPropertyValue(Object, Object)
     */
    private static IPropertyDescriptor[] descriptors;
    /**
     * ID for the Height property value (used for by the corresponding property
     * descriptor).
     */
    private static final String HEIGHT_PROP = "Shape.Height";
    /** Property ID to use when the location of this shape is modified. */
    public static final String LOCATION_PROP = "Shape.Location";
    private static final long serialVersionUID = 1;
    /** Property ID to use then the size of this shape is modified. */
    public static final String SIZE_PROP = "Shape.Size";
    /** Property ID to use when the list of outgoing connections is modified. */
    public static final String SOURCE_CONNECTIONS_PROP = "Shape.SourceConn";
    /** Property ID to use when the list of incoming connections is modified. */
    public static final String TARGET_CONNECTIONS_PROP = "Shape.TargetConn";
    /**
     * ID for the Width property value (used for by the corresponding property
     * descriptor).
     */
    private static final String WIDTH_PROP = "Shape.Width";

    /**
     * ID for the X property value (used for by the corresponding property
     * descriptor).
     */
    private static final String XPOS_PROP = "Shape.xPos";
    /**
     * ID for the Y property value (used for by the corresponding property
     * descriptor).
     */
    private static final String YPOS_PROP = "Shape.yPos";

    /*
     * Initializes the property descriptors array.
     * 
     * @see #getPropertyDescriptors()
     * 
     * @see #getPropertyValue(Object)
     * 
     * @see #setPropertyValue(Object, Object)
     */
    static {
        descriptors = new IPropertyDescriptor[] {
                new TextPropertyDescriptor(XPOS_PROP, "X"), // id and
                                                            // description pair
                new TextPropertyDescriptor(YPOS_PROP, "Y"),
                new TextPropertyDescriptor(WIDTH_PROP, "Width"),
                new TextPropertyDescriptor(HEIGHT_PROP, "Height"), };
        // use a custom cell editor validator for all four array entries
        for (int i = 0; i < descriptors.length; i++) {
            ((PropertyDescriptor) descriptors[i])
                    .setValidator(new ICellEditorValidator() {
                        @Override
                        public String isValid(final Object value) {
                            int intValue = -1;
                            try {
                                intValue = Integer.parseInt((String) value);
                            } catch (final NumberFormatException exc) {
                                return "Not a number";
                            }
                            return (intValue >= 0) ? null
                                    : "Value must be >=  0";
                        }
                    });
        }
    } // static

    protected static Image createImage(final String name) {
        try {
            final InputStream stream = BilabPlugin.findResourceStream(name);
            final Image image = new Image(null, stream);
            try {
                stream.close();
            } catch (final IOException e) {
            } // ignore close errors
            return image;
        } catch (final IOException ioe) {
            bilab.Notify
                    .devWarning(Graphic.class, "resource not found:" + name);
            return null;
        }
    }

    /** Location of this shape. */
    private final Point location = new Point(0, 0);
    /** Size of this shape. */
    private final Dimension size = new Dimension(50, 50);
    /** List of outgoing Connections. */
    private final List sourceConnections = new ArrayList();
    /** List of incoming Connections. */
    private final List targetConnections = new ArrayList();

    /**
     * Add an incoming or outgoing connection to this shape.
     * 
     * @param conn
     *            a non-null connection instance
     * @throws IllegalArgumentException
     *             if the connection is null or has not distinct endpoints
     */
    void addConnection(final Connection conn) {
        if (conn == null || conn.getSource() == conn.getTarget()) {
            throw new IllegalArgumentException();
        }
        if (conn.getSource() == this) {
            sourceConnections.add(conn);
            firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
        } else if (conn.getTarget() == this) {
            targetConnections.add(conn);
            firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
        }
    }

    /**
     * Return a pictogram (small icon) describing this model element. Children
     * should override this method and return an appropriate Image.
     * 
     * @return a 16x16 Image or null
     */
    public abstract Image getIcon();

    /**
     * Return the Location of this shape.
     * 
     * @return a non-null location instance
     */
    public Point getLocation() {
        return location.getCopy();
    }

    /**
     * Returns an array of IPropertyDescriptors for this shape.
     * <p>
     * The returned array is used to fill the property view, when the edit-part
     * corresponding to this model element is selected.
     * </p>
     * 
     * @see #descriptors
     * @see #getPropertyValue(Object)
     * @see #setPropertyValue(Object, Object)
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return descriptors;
    }

    /**
     * Return the property value for the given propertyId, or null.
     * <p>
     * The property view uses the IDs from the IPropertyDescriptors array to
     * obtain the value of the corresponding properties.
     * </p>
     * 
     * @see #descriptors
     * @see #getPropertyDescriptors()
     */
    @Override
    public Object getPropertyValue(final Object propertyId) {
        if (XPOS_PROP.equals(propertyId)) {
            return Integer.toString(location.x);
        }
        if (YPOS_PROP.equals(propertyId)) {
            return Integer.toString(location.y);
        }
        if (HEIGHT_PROP.equals(propertyId)) {
            return Integer.toString(size.height);
        }
        if (WIDTH_PROP.equals(propertyId)) {
            return Integer.toString(size.width);
        }
        return super.getPropertyValue(propertyId);
    }

    /**
     * Return the Size of this shape.
     * 
     * @return a non-null Dimension instance
     */
    public Dimension getSize() {
        return size.getCopy();
    }

    /**
     * Return a List of outgoing Connections.
     */
    public List getSourceConnections() {
        return new ArrayList(sourceConnections);
    }

    /**
     * Return a List of incoming Connections.
     */
    public List getTargetConnections() {
        return new ArrayList(targetConnections);
    }

    /**
     * Remove an incoming or outgoing connection from this shape.
     * 
     * @param conn
     *            a non-null connection instance
     * @throws IllegalArgumentException
     *             if the parameter is null
     */
    void removeConnection(final Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException();
        }
        if (conn.getSource() == this) {
            sourceConnections.remove(conn);
            firePropertyChange(SOURCE_CONNECTIONS_PROP, null, conn);
        } else if (conn.getTarget() == this) {
            targetConnections.remove(conn);
            firePropertyChange(TARGET_CONNECTIONS_PROP, null, conn);
        }
    }

    /**
     * Set the Location of this shape.
     * 
     * @param newLocation
     *            a non-null Point instance
     * @throws IllegalArgumentException
     *             if the parameter is null
     */
    public void setLocation(final Point newLocation) {
        if (newLocation == null) {
            throw new IllegalArgumentException();
        }
        location.setLocation(newLocation);
        firePropertyChange(LOCATION_PROP, null, location);
    }

    /**
     * Set the property value for the given property id. If no matching id is
     * found, the call is forwarded to the superclass.
     * <p>
     * The property view uses the IDs from the IPropertyDescriptors array to set
     * the values of the corresponding properties.
     * </p>
     * 
     * @see #descriptors
     * @see #getPropertyDescriptors()
     */
    @Override
    public void setPropertyValue(final Object propertyId, final Object value) {
        if (XPOS_PROP.equals(propertyId)) {
            final int x = Integer.parseInt((String) value);
            setLocation(new Point(x, location.y));
        } else if (YPOS_PROP.equals(propertyId)) {
            final int y = Integer.parseInt((String) value);
            setLocation(new Point(location.x, y));
        } else if (HEIGHT_PROP.equals(propertyId)) {
            final int height = Integer.parseInt((String) value);
            setSize(new Dimension(size.width, height));
        } else if (WIDTH_PROP.equals(propertyId)) {
            final int width = Integer.parseInt((String) value);
            setSize(new Dimension(width, size.height));
        } else {
            super.setPropertyValue(propertyId, value);
        }
    }

    /**
     * Set the Size of this shape. Will not modify the size if newSize is null.
     * 
     * @param newSize
     *            a non-null Dimension instance or null
     */
    public void setSize(final Dimension newSize) {
        if (newSize != null) {
            size.setSize(newSize);
            firePropertyChange(SIZE_PROP, null, size);
        }
    }
}