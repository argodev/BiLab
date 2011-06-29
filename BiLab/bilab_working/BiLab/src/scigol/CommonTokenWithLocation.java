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

import antlr.CommonToken;
import antlr.Token;

public class CommonTokenWithLocation extends CommonToken {
    public Location loc;

    public CommonTokenWithLocation() {
        loc = new Location();
    }

    public CommonTokenWithLocation(final int t, final String txt) {
        super(t, txt);
        loc = new Location();
    }

    public CommonTokenWithLocation(final String s) {
        super(s);
        loc = new Location();
    }

    public CommonTokenWithLocation(final Token tok) {
        super(tok.getType(), tok.getText());
        setLine(tok.getLine());
        setColumn(tok.getColumn());
        if (tok.getFilename() != null) {
            setFilename(tok.getFilename());
        } else {
            setFilename("?");
        }

        if (!(tok instanceof CommonTokenWithLocation)) {
            loc = new Location();
        } else {
            loc = ((CommonTokenWithLocation) tok).loc;
        }
    }

    @Override
    public String getFilename() {
        if ((loc != null) && (loc.filename != null)) {
            return loc.filename;
        } else {
            return "?";
        }
    }

    @Override
    public void setColumn(final int c) {
        if (loc == null) {
            loc = new Location();
        }
        loc.column = c;
        super.setColumn(c);
    }

    @Override
    public void setFilename(final String name) {
        Debug.Assert(name != null);
        if (loc == null) {
            loc = new Location();
        }
        loc.filename = name;
    }

    @Override
    public void setLine(final int l) {
        if (loc == null) {
            loc = new Location();
        }
        loc.line = l;
        super.setLine(l);
    }
}
