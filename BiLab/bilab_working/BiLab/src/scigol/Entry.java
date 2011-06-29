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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

import antlr.collections.AST;

// / Represent a symbol declared in a scope
// / e.g. a local variable in a block, a variable in a namespace or a member of
// a class or interface
public class Entry {
    public static class EntryPair {
        public Entry getter;
        public Entry setter;

        public EntryPair() {
            getter = setter = null;
        }

        public EntryPair(final Entry get, final Entry set) {
            getter = get;
            setter = set;
        }
    }

    public enum Flags {
        // ClassInfo specific
        Field, Method, Property, // exclusive, one of required
        HasGetter, HasSetter, // for Property
        Accessor, // Method is a propertu accessor (getter/setter)
        IsCovariant
        // for Property
    }

    public static Entry[] toArray(final ArrayList entryList) {
        if (entryList == null) {
            return new Entry[0];
        }
        final Entry[] a = new Entry[entryList.size()];
        for (int i = 0; i < entryList.size(); i++) {
            a[i] = (Entry) entryList.get(i);
        }
        return a;
    }

    // convenience
    public static ArrayList toArrayList(final Entry[] entryArray) {
        if (entryArray == null) {
            return new ArrayList();
        }
        final ArrayList l = new ArrayList(entryArray.length);
        for (int i = 0; i < entryArray.length; i++) {
            l.add(entryArray[i]);
        }
        return l;
    }

    public String name;;

    public String actualName; // JavaName is different from name (in case of
                              // operator), or aliased name in case of an alias
                              // Entry

    public TypeSpec type;

    public TypeSpec actualType; // for external covariant properties, this is
                                // the actual property type
    public Object _staticValue; // value of static class members, or local or
                                // namespace variables, EntryPair for properties
    public AST initializer; // instantiation time initialization expression, if
                            // any
    public EnumSet<TypeSpec.Modifier> modifiers;
    public Scope scope;
    public Location location;
    public int index; // used by ClassInfo for members (instance array index)
    public EnumSet<Flags> flags;
    public Entry aliasedEntry; // if this is an alias Entry, the aliased Entry;
                               // null otherwise
    public Entry propertyEntry; // if Entry is a property accessor, this is the
                                // property Entry
    public LinkedList<Annotation> annotations; // a list of annotations, if any,
                                               // or null

    public Entry(final String name, final String actualName,
            final TypeSpec type, final Object staticValue,
            final AST initializer, final EnumSet<TypeSpec.Modifier> modifiers,
            final EnumSet<Flags> flags, final int index, final Scope scope) {
        this.name = name;
        this.type = type;
        this._staticValue = staticValue;
        this.initializer = initializer;
        this.modifiers = modifiers;
        this.scope = scope;
        location = null;
        this.index = index;
        this.flags = flags;
        this.actualName = actualName;
        aliasedEntry = null;
        Debug.Assert(scope != null);
    }

    public Entry(final String name, final TypeSpec type,
            final Object staticValue, final AST initializer,
            final EnumSet<TypeSpec.Modifier> modifiers,
            final EnumSet<Flags> flags, final int index, final Scope scope) {
        this.name = name;
        this.type = type;
        this._staticValue = staticValue;
        this.initializer = initializer;
        this.modifiers = modifiers;
        this.scope = scope;
        location = null;
        this.index = index;
        this.flags = flags;
        actualName = null;
        aliasedEntry = null;
        Debug.Assert(scope != null);
    }

    public Entry(final String name, final TypeSpec type,
            final Object staticValue, final AST initializer,
            final EnumSet<TypeSpec.Modifier> modifiers,
            final EnumSet<Flags> flags, final int index, final Scope scope,
            final Location l) {
        this.name = name;
        this.type = type;
        this._staticValue = staticValue;
        this.initializer = initializer;
        this.modifiers = modifiers;
        this.scope = scope;
        location = null;
        this.index = index;
        this.flags = flags;
        this.location = l;
        this.actualName = null;
        aliasedEntry = null;
        Debug.Assert(scope != null);
    }

    public void addAnnotation(final Annotation a) {
        if (annotations == null) {
            annotations = new LinkedList<Annotation>();
        }
        annotations.add(a);
    }

    // convenience
    public void addAnnotations(final Annotation[] as) {
        if (annotations == null) {
            annotations = new LinkedList<Annotation>();
        }
        for (final Annotation a : as) {
            annotations.add(a);
        }
    }

    public void addAnnotations(final LinkedList<Annotation> as) {
        if (annotations == null) {
            annotations = new LinkedList<Annotation>();
        }
        for (final Annotation a : as) {
            annotations.add(a);
        }
    }

    // make a new Entry that is an alias for this Entry
    // (this Entry will be updated on changes to the value of the alias Entry)
    public Entry createAliasEntry(final String alias) {
        final Entry aliasEntry = new Entry(alias, actualName, type,
                _staticValue, initializer, modifiers, flags, index, scope);
        aliasEntry.aliasedEntry = this;
        aliasEntry.location = location;
        aliasEntry.actualName = name;
        aliasEntry.addAnnotations(this.getAnnotations());
        return aliasEntry;
    }

    public Annotation getAnnotation(final TypeSpec annotationType) {
        Debug.Assert(annotationType.isInterface());
        Debug.Assert(annotationType.isA(new TypeSpec(Annotation.class)));

        if (annotations == null) {
            return null;
        }

        for (final Annotation a : annotations) {
            if (a.annotationType().equals(annotationType.getSysType())) {
                return a;
            }
        }

        // didn't find it, look for a redirect
        for (final Annotation a : annotations) {
            if (a.annotationType().equals(redirect.class)) {
                String redirectTarget = null;
                if (a instanceof ScigolAnnotation) {
                    final Value v = (Value) ((ScigolAnnotation) a).getMembers()
                            .get(0);
                    redirectTarget = (String) v.getValue();
                } else {
                    redirectTarget = ((redirect) a).value();
                }

                // !!! only handle Java method redirects for now
                final int lastDotIndex = redirectTarget.lastIndexOf(".");
                final String redirectClassName = redirectTarget.substring(0,
                        lastDotIndex);
                final String redirectMethodName = redirectTarget
                        .substring(lastDotIndex + 1);

                final TypeSpec javaClassType = TypeSpec
                        .typeOf(redirectClassName);
                if (javaClassType != null) {

                    // this doesn't currently support redirect target overload
                    // resolution - the target name must be unique!!!
                    final Entry[] memberEntries = javaClassType.getClassInfo()
                            .getAllEntries(redirectMethodName);
                    if (memberEntries.length > 0) {

                        if (memberEntries.length > 1) {
                            ScigolTreeParser
                                    .semanticError("redirect annotation target '"
                                            + redirectTarget + "' is ambiguous"); // !!!
                                                                                  // add
                                                                                  // location
                        }

                        return memberEntries[0].getAnnotation(annotationType); // redirect
                                                                               // request
                    }
                }

                ScigolTreeParser.semanticError("redirect annotation target '"
                        + redirectTarget + "' not found"); // !!! add location

            }
        }

        return null;
    }

    public Annotation[] getAnnotations() {
        final Annotation[] as = new Annotation[annotations.size()];
        int i = 0;
        for (final Annotation a : annotations) {
            as[i++] = a;
        }
        return as;
    }

    public Object getStaticValue() {
        if (aliasedEntry != null) {
            return aliasedEntry.getStaticValue();
        }
        return _staticValue;
    }

    public boolean hasAnnotations() {
        return (annotations != null) && (annotations.size() > 0);
    }

    public boolean hasGetter() {
        return flags.contains(Flags.HasGetter);
    }

    public boolean hasSetter() {
        return flags.contains(Flags.HasSetter);
    }

    public boolean isAbstract() {
        return modifiers.contains(TypeSpec.Modifier.Abstract);
    }

    public boolean isAccessor() {
        return flags.contains(Flags.Accessor);
    }

    public boolean isClassMember() {
        return scope.isClassScope();
    }

    public boolean isConst() {
        return modifiers.contains(TypeSpec.Modifier.Const);
    }

    public boolean isCovariant() {
        return flags.contains(Flags.IsCovariant);
    }

    public boolean isField() {
        return flags.contains(Flags.Field);
    }

    public boolean isImplicit() {
        return modifiers.contains(TypeSpec.Modifier.Implicit);
    }

    public boolean isLocalName() {
        return scope.isLocalScope();
    }

    public boolean isMethod() {
        return flags.contains(Flags.Method);
    }

    public boolean isNamespaceName() {
        return scope.isNamespaceScope();
    }

    public boolean isProperty() {
        return flags.contains(Flags.Property);
    }

    public boolean isStatic() {
        return modifiers.contains(TypeSpec.Modifier.Static);
    }

    public void setStaticValue(final Object value) {
        if (aliasedEntry != null) {
            aliasedEntry.setStaticValue(value);
        } else {
            _staticValue = value;
        }
    }

    @Override
    public String toString() {
        String s = "";
        if (isField()) {
            s += "field";
        }
        if (isMethod()) {
            s += "method";
        }
        if (isProperty()) {
            s += "property";
        }
        if (isAccessor()) {
            s += " accessor";
        }
        s += " " + name;
        s += " mods:" + TypeSpec.modifiersString(modifiers) + ";";
        s += " actualName:" + actualName;
        s += " type:" + type;
        s += " actualType:" + actualType;
        s += " staticValue:" + _staticValue;
        return s;
    }

}
