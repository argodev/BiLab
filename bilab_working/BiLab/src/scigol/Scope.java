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
import java.util.EnumSet;

import antlr.collections.AST;

// / a scope for identifiers (in namespaces, local blocks, classes)
public abstract class Scope {
    protected ArrayList identDeclarationStack = new ArrayList();

    protected Scope _outer; // enclosing scope, or null
    protected Location _location; // location where scope defined (if any)

    protected Scope() {
        _outer = null;
        _location = null;
    }

    public abstract Entry addEntry(Entry e);

    public Entry addEntry(final String name, final TypeSpec type,
            final Object staticValue, final AST initializer,
            final EnumSet<TypeSpec.Modifier> modifiers) {
        return addEntry(new Entry(name, type, staticValue, initializer,
                modifiers, EnumSet.noneOf(Entry.Flags.class), -1, this));
    }

    public Entry addEntry(final String name, final TypeSpec type,
            final Object staticValue, final AST initializer,
            final EnumSet<TypeSpec.Modifier> modifiers, final Location l) {
        return addEntry(new Entry(name, type, staticValue, initializer,
                modifiers, EnumSet.noneOf(Entry.Flags.class), -1, this, l));
    }

    public Entry addEntry(final String name, final TypeSpec type,
            final Object staticValue, final AST initializer,
            final EnumSet<TypeSpec.Modifier> modifiers,
            final Scope declaringScope) {
        return addEntry(new Entry(name, type, staticValue, initializer,
                modifiers, EnumSet.noneOf(Entry.Flags.class), -1,
                declaringScope));
    }

    // convenience
    public boolean contains(final String name) {
        return getEntries(name, null).length != 0;
    }

    public Location getDefinitionLocation() {
        if (_location == null) {
            return new Location();
        } else {
            return _location;
        }
    }

    public abstract Entry[] getEntries(String name, Object instance);

    // traverse back up the outer Scope list until the global scope (or return
    // null if unavailable)
    public NamespaceScope getGlobalScope() {
        Scope s = this;
        while (s._outer != null) {
            s = s._outer;
        }
        if (s.isNamespaceScope()) {
            return (NamespaceScope) s;
        } else {
            return null;
        }
    }

    // get enclosing Scope, or null if none
    public Scope getOuter() {
        return _outer;
    }

    public boolean isClassScope() {
        return false;
    }

    public boolean isLocalScope() {
        return false;
    }

    public boolean isNamespaceScope() {
        return false;
    }

    // helpers

    // performs overload resolution and enclosing scope lookups to try and
    // revolve name to a single entry
    // returns 0 element array if no match found, or >1 elements if ambiguous
    // (callSig may be null, in which case multiple entries will be returned for
    // ambiguous names)
    // (args may be null if the argument values are not known. If known,
    // resolution will look into
    // 'any' and 'num' type arguments to discover their actual type)
    public abstract Entry[] lookup(String name, FuncInfo callSig,
            Object[] args, Object instance);

    public String popDeclarationIdent() {
        String id = null;
        if (identDeclarationStack.size() > 0) {
            id = (String) identDeclarationStack.get(identDeclarationStack
                    .size() - 1);
            identDeclarationStack.remove(id);
        }
        return id;
    }

    // utilitys so that a declaration can register the ident being
    // declared for retrieval by a class type definition (for example) to use
    // a sensible class name for injection into the CLI type system
    public void pushDeclarationIdent(final String id) {
        identDeclarationStack.add(id);
    }

    public void setDefinitionLocation(final Location value) {
        _location = value;
    }

    public void setOuter(final Scope value) {
        _outer = value;
    }

    public String topDeclarationIdent() {
        if (identDeclarationStack.size() > 0) {
            return (String) identDeclarationStack.get(identDeclarationStack
                    .size() - 1);
        } else {
            return null;
        }
    }

}
