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

// / holder for a value of any numeric type (int,dint,real,sreal,byte)
// / (essentially tags the value of being assignable to any numeric type)
public class Num {
    public Object value;

    public Num() {
        value = new Integer(0);
    }

    /*
     * !!! what is this doing here?? public static Num operator_plus(Num n1, Num
     * n2) { return new Num(Math.plus(new Value(n1.value), new
     * Value(n2.value)).getValue()); }
     */

    public Num(final Object wrappedvalue) {
        if (wrappedvalue instanceof Num) {
            value = ((Num) wrappedvalue).value; // don't nest
        } else if (wrappedvalue instanceof Any) {
            value = ((Any) wrappedvalue).value; // don't next Any in Num
        } else {
            Debug.Assert(TypeSpec.typeOf(wrappedvalue).isANum(),
                    "must be one of the num types (int, dint, real, sreal, byte)");
            value = wrappedvalue;
        }
    }

    @Override
    public String toString() {
        if (value == null) {
            return "null";
        } else {
            return value.toString();
        }
    }

}
