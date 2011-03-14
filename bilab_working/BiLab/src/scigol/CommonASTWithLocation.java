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

import antlr.CommonAST;
import antlr.Token;
import antlr.collections.AST;

public class CommonASTWithLocation extends CommonAST {
    public Location loc;

    // protected CommonASTWithLocation(CommonASTWithLocation another) :
    // base(another)
    // {
    // _location = another._location;
    // }

    public CommonASTWithLocation() {
        loc = new Location(0, 0, "?");
    }

    public CommonASTWithLocation(final Token tok) {
        if (!(tok instanceof CommonTokenWithLocation)) {
            initialize(new CommonTokenWithLocation(tok));
        } else {
            initialize(tok);
        }
    }

    @Override
    public void initialize(final AST t) {
        super.initialize(t);
        loc = new Location(0, 0, "?");
    }

    public void initialize(final CommonTokenWithLocation tok) {
        super.initialize(tok);
        loc = new Location(tok.getLine(), tok.getColumn(), tok.getFilename());
    }

    @Override
    public void initialize(final int t, final String txt) {
        super.initialize(t, txt);
        loc = new Location(0, 0, "?");
    }

    public void initialize(final int t, final String txt,
            final Location location) {
        initialize(t, txt);
        loc = location;
    }

    // override public object Clone()
    // {
    // return new CommonASTWithLocation(this);
    // }

    @Override
    public void initialize(final Token tok) {
        if (tok instanceof CommonTokenWithLocation) {
            initialize((CommonTokenWithLocation) tok);
        } else {
            super.initialize(tok);
            loc = new Location(tok.getLine(), tok.getColumn(),
                    tok.getFilename());
        }
    }
}
