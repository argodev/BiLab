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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.jmol.adapter.smarter.SmarterModelAdapter;
import org.jmol.api.ModelAdapter;
import org.openscience.jmol.viewer.JmolViewer;

class JmolPanel extends JPanel {
    JmolViewer viewer;
    ModelAdapter adapter;

    Dimension currentSize = new Dimension();

    JmolPanel() {
        adapter = new SmarterModelAdapter(null);
        viewer = new JmolViewer(this, adapter);
    }

    public JmolViewer getViewer() {
        return viewer;
    }

    @Override
    public void paint(final Graphics g) {
        // viewer.setScreenDimension(getSize(currentSize));
        getSize(currentSize);
        viewer.setScreenDimension(currentSize);
        final Rectangle rectClip = new Rectangle();
        g.getClipBounds(rectClip);
        viewer.renderScreenImage(g, currentSize, rectClip);
    }
}