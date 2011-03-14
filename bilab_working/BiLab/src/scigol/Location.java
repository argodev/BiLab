/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package scigol;

public class Location {
    public int line;

    public int column;

    public String filename;

    public Location() {
        line = column = -1;
        filename = "?";
    }

    public Location(final int line, final int col, final String filename) {
        if ((filename == null) || (filename == "")) {
            Debug.WriteLine("got filename:"
                    + ((filename == null) ? "null" : filename));
        }
        Debug.Assert((filename != null) && (filename != ""));
        this.line = line;
        column = col;
        this.filename = filename;
    }

    public boolean isKnown() {
        return (line != -1);
    }

    @Override
    public String toString() {
        if (!isKnown() || ((line == 0) && (column == 0))) {
            return filename;
        } else {
            return filename + ":" + line + ":" + column;
        }
    }

}
