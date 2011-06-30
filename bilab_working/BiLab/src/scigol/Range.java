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

// !!! consider supporting a stride as well

// / A range of element indices
// ranges are inclusive of both the start and end indices
// e.g. if l = (0,1,2,3) then l(0..2) == (0,1,2)
// -ve indices count from the sequence length being accessed, backward
// e.g -1 is the last element
public class Range {
    // public static int End = 2147483647; // 2^31-1

    public static int op_Card(final Range r) {
        if (sign(r.start) == sign(r.end)) {
            return r.end - r.start + 1;
        }
        return -1;
    }

    protected static int sign(final int i) {
        return (i >= 0) ? 1 : -1;
    }

    public int start;

    public int end;

    public Range() {
        start = 0;
        end = -1;
    }

    public Range(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public Range normalize(final int count) {
        final Range r = new Range(start, end);
        if (start < 0) {
            r.start = count + start;
        }
        if (end < 0) {
            r.end = count + end;
        }
        return r;
    }

    @Override
    public String toString() {
        return start + ".." + end;
    }

}
