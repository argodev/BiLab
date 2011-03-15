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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bilab.Util;

// simple interface to the local OS file-system
public class LocalFileSystem implements IFileSystem {

    private final File localRoot;

    private String currentDirectory = ".";

    // new file-system rooted at the local root ("/")
    public LocalFileSystem() {
        localRoot = new File("/");
    }

    // new file-system rooted at specified local directory
    public LocalFileSystem(final String rootDir) throws IOException {
        localRoot = new File(Util.toNativePathSeparator(rootDir));
        if (!localRoot.isDirectory()) {
            throw new IOException("local rootDir '" + rootDir
                    + "' must be a directory");
        }
    }

    @Override
    public boolean changeDir(final String subdirName) {
        if (subdirName.equals(".")) {
            return true;
        }

        if (subdirName.equals("/")) {
            currentDirectory = ".";
        }

        if (subdirName.equals("..")) {

            if (currentDirectory.equals(".")) {
                return true;
            }

            final File parent = (new File(
                    Util.toNativePathSeparator(currentDirectory)))
                    .getParentFile();

            if (parent != null) {
                currentDirectory = Util
                        .toForwardPathSeparator(parent.getPath());
            } else {
                return false;
            }
        } else {
            String newDir = subdirName;

            if (!subdirName.startsWith("/")) { // if not absolute prepend
                                               // currentDirectory
                newDir = (currentDirectory.equals(".")) ? "/" + subdirName
                        : currentDirectory + "/" + subdirName;
            }

            final File local = localPath(newDir);

            if (local.exists()) {
                currentDirectory = newDir;
            } else {
                return false;
            }
        }

        // the current directory doesn't need a drive letter as it is relative
        // to the localRoot, so remove
        // it if present
        if (currentDirectory.charAt(0) == '/') {
            currentDirectory = currentDirectory.substring(1);
        }
        if ((currentDirectory.length() >= 3)
                && currentDirectory.substring(1, 3).equals(":/")) {
            currentDirectory = currentDirectory.substring(3);
        }

        return true;
    }

    @Override
    public String currentDir() {
        String cwd = null;

        if (currentDirectory.equals(".")) {
            cwd = Util.toForwardPathSeparator(localRoot.getAbsolutePath()
                    .toString());
        } else {
            if (localRoot.equals(new File(File.separator))) {
                cwd = Util.toForwardPathSeparator(localRoot.getAbsolutePath()
                        .toString()) + currentDirectory;
            } else {
                cwd = Util.toForwardPathSeparator(localRoot.getAbsolutePath()
                        .toString()) + "/" + currentDirectory;
            }
        }

        if (cwd.charAt(0) != '/') {
            cwd = "/" + cwd;
        }

        return cwd;
    }

    @Override
    public void delete(final String path, final boolean recursive)
            throws IOException {
        if (exists(path)) {
            if ((!recursive) || !isDirectory(path)) {
                localPath(path).delete();
            } else {
                final String[] contents = listDirectoryContents(path);
                for (final String item : contents) {
                    delete(path + "/" + item, true);
                }
                delete(path, false);
            }
        }
    }

    @Override
    public final boolean exists(final String path) {
        return localPath(path).exists();
    }

    public final String getLocalPath(final String path) {
        return localPath(path).getAbsolutePath().toString();
    }

    @Override
    public final boolean isDirectory(final String path) {
        return localPath(path).isDirectory();
    }

    @Override
    public String[] listDirectoryContents(final String directory)
            throws IOException {
        if (!exists(directory)) {
            throw new IOException("directory '" + directory + "' not found");
        }

        final File localDir = localPath(directory);
        return localDir.list();
    }

    protected final File localPath(final String path) {
        if (path.equals("/")) {
            return localRoot;
        }

        final boolean containsDriveLetter = ((path.length() >= 3) && path
                .substring(1, 3).equals(":/")) // e.g. C:/
                || ((path.length() >= 4) && (path.charAt(0) == '/') && (path
                        .substring(2, 4).equals(":/"))); // e.g. /C:/
        final boolean isAbsolute = (path.charAt(0) == '/')
                || containsDriveLetter;

        File local = null;
        if (isAbsolute) {
            // form path from local directory that represents the root of this
            // filesystem and the absolute path
            // (note that under windows, an absolute path prefixed with a drive
            // letter is supported. For example,
            // if the localRoot is "\" and the path is /C:/subdir, the local
            // path will be C:\subdir. It is
            // an error is the current drive isn't C (in this example).
            //

            if (!containsDriveLetter) {
                // remove leading '/'
                local = new File(localRoot.getAbsolutePath(),
                        Util.toNativePathSeparator(path.substring(1)));
            } else { // special case when path contains a drive letter
                // check the letter is the same drive as the localRoot's drive
                // \TODO !!!

                // it is an error to include the drive letter is the localRoot
                // isn't the root of the drive
                // (i.e. can't accept a drive absolute path if this filesystem's
                // root is a sub-directory)
                if (!localRoot.equals(new File(File.separator))) {
                    throw new IllegalArgumentException(
                            "invalid path "
                                    + path
                                    + " for filesystem rooted in the local filesystem directory '"
                                    + localRoot + "'");
                }

                String newPath = (path.charAt(0) == '/') ? path.substring(1)
                        : path; // first, remove leading '/'
                if (newPath.substring(1, 3).equals(":/")) {
                    newPath = newPath.substring(3);
                } // remove ?:/

                local = new File(localRoot.getAbsolutePath(),
                        Util.toNativePathSeparator(newPath));
            }
        } else {
            // relative to current dir
            final String fullPath = (currentDirectory.equals(".")) ? path
                    : currentDirectory + "/" + path;

            local = new File(localRoot.getAbsolutePath(),
                    Util.toNativePathSeparator(fullPath));
        }

        return local;
    }

    @Override
    public void makeDirectory(final String dirName) throws IOException {
        if (exists(dirName)) {
            return;
        } // already exists
        final File local = localPath(dirName);
        if (!local.mkdir()) {
            throw new IOException("unable to create new directory '" + dirName
                    + "'.");
        }
    }

    @Override
    public InputStream readFile(final String file) throws FileNotFoundException {
        try {
            if (exists(file)) {
                return new FileInputStream(localPath(file));
            }
        } catch (final FileNotFoundException e) {
        }

        throw new FileNotFoundException("not found '" + file + "'");
    }

    @Override
    public OutputStream writeFile(final String file, final boolean append)
            throws IOException {
        if (append && !exists(file)) {
            throw new FileNotFoundException(
                    "can't append to non-existent file '" + file + "'.");
        }

        try {
            return new FileOutputStream(localPath(file), append);
        } catch (final IOException e) {
            throw new IOException("unable to write to file '" + file + "' - "
                    + e.getMessage());
        }
    }
}