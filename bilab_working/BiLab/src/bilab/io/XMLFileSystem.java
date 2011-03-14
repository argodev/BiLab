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

// !!! change this to be a generic XML file system implementing IFileSystem

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// A file-system like store within an XML file
// (the file must be a local memory-mappable file)
//
// There are two formats for notebook files. Both are XML files with
// a similar structure. The first is a normal XML file containing all
// the information contained in the notebook.
// The second is a similar XML file, but with its bytes specially
// laid-out (via padding with white-space). It also contains some
// extra attributed on some elements with file byte offset hints
// to aid in quickly seeking to specific elements.
// These 'random access structured' notebook files can be directly
// modified, in-place, efficiently by this implementation. They
// are marked as such via the RandomAccess="true" attribute of the
// top-level notebook element.
// If this class is constructed with a File that is not of the RandomAccess
// type, it will be re-written as such first.
public class XMLFileSystem {
    // notebook structure information
    protected static class PageInfo {
        public PageInfo() {
        }
    }

    protected static class SectionInfo {
        public String name;

        int fileOffset;

        public SectionInfo(final String name) {
            this.name = name;
        }
    }

    protected static class VersionInfo {
        public String versionName;

        public VersionInfo(final String name) {
            versionName = name;
        }
    }

    ArrayList<VersionInfo> notebookInfo = new ArrayList<VersionInfo>();;

    // Create a file-system associated with the specified file
    // The file will be created if is doesn't already exist)
    public XMLFileSystem(final File file) throws IOException {
        // first see if this file is a map structured notebook file, and if not
        // re-write it into a map structured one

        /*
         * file.createNewFile(); rafile = new RandomAccessFile(file, "rw");
         * channel = rafile.getChannel(); buff =
         * channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());
         */
        // if the size is 0, write a new notebook header
        // if (channel.size() == 0)
        // rewriteHeader();
    }

    // look for the mappable attribute of the notebook element and see if it is
    // true
    boolean isMappable(final File file) {
        return false;
    }

    // private MappedByteBuffer buff;
    // private RandomAccessFile rafile;
    // private FileChannel channel;
}