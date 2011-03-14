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

import java.util.ArrayList;
import java.util.Hashtable;

// / a scope for local identifiers in a block
public class LocalScope extends Scope {
    protected Hashtable _entries; // identifier name keyed map of ArrayList's of
                                  // Entrys

    public LocalScope(final Scope outer) {
        _entries = new Hashtable();
        _outer = outer;
    }

    @Override
    public Entry addEntry(final Entry e) {
        e.scope = this;

        // find mapped list for this name and add e; if no existing map list,
        // create a new one
        final String name = e.name;
        ArrayList el = (ArrayList) _entries.get(name);
        if (el == null) {
            el = new ArrayList();
            el.add(e);
            _entries.put(name, el);
        } else {
            el.add(e);
        }
        return e;
    }

    @Override
    public boolean contains(final String name) {
        return _entries.containsKey(name);
    }

    @Override
    public Entry[] getEntries(final String name, final Object instance) {
        if (_entries.containsKey(name)) {
            return Entry.toArray((ArrayList) _entries.get(name));
        } else {
            return new Entry[0];
        }
    }

    @Override
    public boolean isLocalScope() {
        return true;
    }

    @Override
    public Entry[] lookup(final String name, final FuncInfo callSig,
            final Object[] args, final Object instance) {
        // if this block contains any definition of name at all, it hides those
        // in
        // any outer scope, so just perform overload resolution (if necessary)
        // to resolve it
        // (otherwise, defer to the outer scope)
        if (contains(name)) {
            Entry[] matches = getEntries(name, instance);
            if (matches.length == 1) {
                return matches;
            }
            // overloaded func, try to resolve it (will return 0 elements if no
            // match, or >1 elements if ambiguous)
            matches = TypeManager.resolveOverload(matches, callSig, args);
            // if found a unique or ambiguous match, return, otherwise defer to
            // outer scope
            if (matches.length > 0) {
                return matches;
            }
        }

        if (getOuter() != null) {
            return getOuter().lookup(name, callSig, args, instance);
        } else {
            return new Entry[0];
        }
    }

}
