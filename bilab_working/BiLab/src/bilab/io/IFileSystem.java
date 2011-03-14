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

package bilab.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// represent a simple file system
// (e.g. the OS file system, a zip 'file-system', etc.)
public interface IFileSystem {
    public boolean changeDir(String subdirName) throws IOException;

    public String currentDir();

    public void delete(String path, boolean recursive) throws IOException;

    public boolean exists(String path);

    public boolean isDirectory(String path);

    public String[] listDirectoryContents(String directory) throws IOException;

    public void makeDirectory(String dirName) throws IOException;

    public InputStream readFile(String file) throws IOException;

    public OutputStream writeFile(String file, boolean append)
            throws IOException;
}