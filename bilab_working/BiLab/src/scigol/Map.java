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

import java.util.HashMap;

// Map (aka hashtable, associative array, dictionary)
public class Map extends HashMap // !!! make this implement an interface and
                                 // wrap the HashMap instead
{
    public static int op_Card(final Map m) {
        return m.size();
    }

    public Map() {
        super(2);
    }

    public void add(Object key, Object value) {
        key = TypeSpec.unwrapAnyOrNum(key);
        value = TypeSpec.unwrapAnyOrNum(value);
        super.put(key, value);
    }

    public boolean contains(final Object key) {
        return (super.containsKey(TypeSpec.unwrapAnyOrNum(key)));
    }

    @accessor
    public Any get_Item(Object key) {
        key = TypeSpec.unwrapAnyOrNum(key);
        return new Any(get(key));
    }

    @Override
    public Object remove(final Object key) {
        return super.remove(TypeSpec.unwrapAnyOrNum(key));
    }

    @accessor
    public void set_Item(Object key, final Any value) {
        key = TypeSpec.unwrapAnyOrNum(key);
        put(key, TypeSpec.unwrapAnyOrNum(value));
    }

    @Override
    public String toString() {
        String s = null;
        final int size = keySet().size();
        if (size > 1) {
            s = "[\n";
            for (final Object key : keySet()) {
                final Object value = get(key);
                s += " " + key.toString() + " -> "
                        + ((value != null) ? value.toString() : "null") + "\n";
            }
            s += "]";
        } else {
            if (size == 0) {
                s = "[]";
            } else {
                final Object key = keySet().iterator().next();
                final Object value = get(key);
                s = "[ " + key.toString() + " -> "
                        + ((value != null) ? value.toString() : "null") + " ]";
            }
        }
        return s;
    }

}
