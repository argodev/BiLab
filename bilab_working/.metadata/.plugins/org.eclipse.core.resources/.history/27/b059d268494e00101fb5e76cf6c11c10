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

package bilab;

// import jalview.FormatAdapter;
// import jalview.ScoreSequence;

import java.io.InputStream;
import java.util.LinkedList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

// A picture (e.g. a bitmap image or a vector drawing)
public class picture implements /* IAnnotated, */IResourceIOProvider {
    // !!! NB: reading an image required the UI thread to be running - which
    // isn't the case before the window opens (e.g. executing sample.sg)
    // should allow some kind of delayed loading for pictures. (perhaps ise
    // Display.getCurrent() and if it is null, delay loading)

    @Sophistication(Sophistication.Developer)
    protected enum PictureType {
        Image, Drawing
    }

    private static java.util.List<String> supportedResourceTypes;

    static {
        // list of supported resource name type (not extensions)
        supportedResourceTypes = new LinkedList<String>();
        supportedResourceTypes.add("PNG");
        supportedResourceTypes.add("GIF");
        supportedResourceTypes.add("JPG");
    }

    public static java.util.List<String> getSupportedResourceTypes() {
        return supportedResourceTypes;
    }

    public static Object importResource(final String resourceName,
            final String resourceType) {
        try {
            final InputStream inputStream = BilabPlugin
                    .findResourceStream(resourceName);
            if (inputStream == null) {
                throw new BilabException("unable to open resource:"
                        + resourceName);
            }

            final Display display = Display.getDefault();
            final picture pic = new picture();

            pic.image = new Image(display, inputStream);
            pic.picType = picture.PictureType.Image;
            pic.name = resourceName + " [bitmap]";

            inputStream.close();

            if (pic.image == null) {
                throw new BilabException("unsupported picture resource type:"
                        + resourceType);
            }

            return pic;

        } catch (final BilabException e) {
            throw e;
        } catch (final Exception e) {
            throw new BilabException(
                    "unable to locate/import resource as picture: "
                            + resourceName + " - " + e);
        }

    }

    private PictureType picType;

    private Image image;

    private String name;

    public picture() {
        final Display display = Display.getDefault();
        image = new Image(display, 10, 10); // !!! use a default image here
                                            // (like a cross or question mark
                                            // something)
        picType = PictureType.Image;
    }

    @Sophistication(Sophistication.Developer)
    public Image get_Image() {
        return image;
    }

    public String get_name() {
        return name;
    }

    @Sophistication(Sophistication.Developer)
    public PictureType get_PictureType() {
        return picType;
    }

}
