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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// / scope for identifiers within a namespace
public class NamespaceScope extends Scope {
    public enum NamespaceType {
        Local, External
    }

    public class UsingEntry {
        public UsingType type;

        public NamespaceScope scope;

        public String aliasName;

        public String aliasedName;

        public UsingEntry(final NamespaceScope namespaceScope) { // using
                                                                 // namespace
            type = UsingType.Namespace;
            scope = namespaceScope;
            aliasName = aliasedName = null;
        }

        public UsingEntry(final String aliased, final NamespaceScope scope) { // using
                                                                              // namespace.name
            type = UsingType.Name;
            aliasName = null;
            aliasedName = aliased;
            this.scope = scope;
        }

        public UsingEntry(final String alias, final String aliased,
                final NamespaceScope scope) { // using namespace.name as alias
            type = UsingType.Alias;
            this.scope = scope;
            aliasName = alias;
            aliasedName = aliased;
        }

        public String fullAliasedName() {
            return scope.fullNamespaceName() + "." + aliasedName;
        }

        public boolean isAlias() {
            return (type == UsingType.Alias);
        }

        public boolean isNamespace() {
            return (type == UsingType.Namespace);
        }

        public String name() {
            if (type == UsingType.Namespace) {
                return scope.fullNamespaceName();
            } else if (type == UsingType.Name) {
                return aliasedName;
            } else if (type == UsingType.Alias) {
                return aliasName;
            }
            Debug.Assert(false);
            return null;
        }
    }

    public enum UsingType {
        Namespace, Name, Alias
    }

    // static constructor
    static {
        loadedPackages = new HashMap<String, LinkedList<String>>();

        // register some select classes
        registerLibraryClass("scigol", "accessor");
        registerLibraryClass("scigol", "Any");
        registerLibraryClass("scigol", "Func");
        registerLibraryClass("scigol", "List");
        registerLibraryClass("scigol", "Map");
        registerLibraryClass("scigol", "Math");
        registerLibraryClass("scigol", "Matrix");
        registerLibraryClass("scigol", "Num");
        registerLibraryClass("scigol", "Range");
        registerLibraryClass("scigol", "redirect");
        registerLibraryClass("scigol", "TypeSpec");
        registerLibraryClass("scigol", "Value");
        registerLibraryClass("scigol", "Vector");
        // registerLibraryClass("scigol","Class");
        registerLibraryClass("scigol", "TypeManager");
    }

    public static AbstractList<String> getNamespaceNames() {
        final LinkedList<String> namespaceNames = new LinkedList<String>();
        for (final String packageName : loadedPackages.keySet()) {
            namespaceNames.add(packageName);
        }
        return namespaceNames;
    }

    static boolean loadedLibrariesContainName(final String fullName) {
        try {
            return java.lang.Class.forName(fullName) != null;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    static boolean loadedLibrariesContainNamespace(final String namespaceName) {
        // java packages are mapped to scigol namespaces

        // some quick short-circuit tests
        if (namespaceName.equals("java") || (namespaceName.equals("javax"))) {
            return true;
        }
        return Package.getPackage(namespaceName) != null;
    }

    public static Type loadedLibrariesGetType(final String fullName) {
        try {
            return java.lang.Class.forName(fullName);
        } catch (final ClassNotFoundException e) {
            return null;
        } catch (final NoClassDefFoundError e) {
            return null;
        }
    }

    public static void loadLibrary(final String fullPathName) {
        // Java has no way to get the list of classes in a package!
        // So here we open the jar, identify all class names from their entries
        // and record them by package name

        // Also, ask the classloader to load one class so that it loads the jar
        // too (if is hasn't already)
        boolean first = true;

        try {
            final File file = new File(fullPathName);
            final JarFile jarFile = new JarFile(file);

            final Enumeration<JarEntry> fileEntries = jarFile.entries();

            while (fileEntries.hasMoreElements()) {
                final JarEntry fileEntry = fileEntries.nextElement();

                if (!fileEntry.isDirectory()
                        && fileEntry.getName().endsWith(".class")) {

                    // first remove the .class file to get the class name
                    final String name = fileEntry.getName();

                    final int lastDotIndex = name.lastIndexOf('.');
                    final int lastSlashIndex = name.lastIndexOf('/');
                    final String className = name.substring(lastSlashIndex + 1,
                            lastDotIndex);
                    final String packageName = name
                            .substring(0, lastSlashIndex).replace('/', '.');

                    final boolean innerClass = (className.indexOf('$') != -1);

                    if (!innerClass) {

                        LinkedList<String> classes = loadedPackages
                                .get(packageName);
                        if (classes == null) {
                            classes = new LinkedList<String>();
                        }

                        classes.add(className);

                        loadedPackages.put(packageName, classes);

                        if (first) {
                            first = false;
                            try {
                                java.lang.Class.forName(packageName + "."
                                        + className);
                            } catch (final ClassNotFoundException e) {
                                throw new ScigolException(
                                        "ClassLoader unable to load library '"
                                                + fullPathName
                                                + "' (not in classpath?).");
                            } catch (final NoClassDefFoundError e) {
                                throw new ScigolException(
                                        "Classloader unable to load library '"
                                                + fullPathName
                                                + "' (not in classpath?).");
                            }
                        }

                    }

                }

            }

        } catch (final IOException e) {
            throw new ScigolException("unable to load library '" + fullPathName
                    + "' - " + e);
        }

    }

    public static NamespaceScope newGlobalNamespaceScope() {
        final NamespaceScope ns = new NamespaceScope();
        ns.initGlobalScope();
        ns._namespaceType = NamespaceType.Local;
        return ns;
    }

    // if name is an existing namespace of outer, return it; otherwise construct
    // a new local namespace
    public static NamespaceScope newOrExistingNamespaceScope(final String name,
            final Scope outer) {
        Debug.Assert(outer.isNamespaceScope(),
                "namespaces can only be nested within namespaces");
        final NamespaceScope outerNamespace = (NamespaceScope) outer;

        // construct fully qualified name
        String fullName = name;
        final String outerFullName = outerNamespace.fullNamespaceName(); // this
                                                                         // is
                                                                         // outer
                                                                         // of
                                                                         // 'new'
                                                                         // one
        if (outerFullName != "") {
            fullName = outerFullName + "." + fullName;
        }

        final NamespaceScope existingNamespace = outerNamespace
                .getNamespaceScope(fullName);
        if (existingNamespace != null) {
            return existingNamespace;
        } else {
            return new NamespaceScope(name, outer);
        }
    }

    // manually register a class (useful if we don't want all classes in a .jar
    // visible, just select ones)
    public static void registerLibraryClass(final String packageName,
            final String className) {
        // first, ensure the class exists by the ClassLoader
        final String fullName = packageName + "." + className;
        try {
            java.lang.Class.forName(fullName);
        } catch (final ClassNotFoundException e) {
            Debug.Warning("ClassLoader unable to class '" + fullName
                    + "' (not in classpath?).");
            throw new ScigolException("ClassLoader unable to class '"
                    + fullName + "' (not in classpath?).");
        } catch (final NoClassDefFoundError e) {
            Debug.Warning("ClassLoader unable to class '" + fullName
                    + "' (not in classpath?).");
            throw new ScigolException("Classloader unable to class '"
                    + fullName + "' (not in classpath?).");
        }

        LinkedList<String> classes = loadedPackages.get(packageName);
        if (classes == null) {
            classes = new LinkedList<String>();
        }

        classes.add(className);

        loadedPackages.put(packageName, classes);

    }

    protected NamespaceType _namespaceType;

    protected String _namespaceName;

    protected Hashtable _namespaces; // only for global namespace scope, a map
                                     // of fully-qualified ns names to namespace
                                     // Scopes

    protected Hashtable _entries; // identifier name keyed map of ArrayList's of
                                  // Entrys in this namespace scope

    protected Hashtable _usings; // map of using entries

    // map from package names to list of classes in the package
    protected static HashMap<String, LinkedList<String>> loadedPackages;

    protected NamespaceScope() {
        _namespaceName = "";
        _namespaceType = NamespaceType.Local;
        _namespaces = null;
        _outer = null;
        _entries = new Hashtable();
        _usings = new Hashtable();
    }

    // construct new *local* namespace
    public NamespaceScope(final String name, final Scope outer) {
        Debug.Assert(outer.isNamespaceScope(),
                "namespaces can only be nested within namespaces");

        _namespaceName = name; // partially qualified name
        _namespaceType = NamespaceType.Local;
        _namespaces = null;
        _outer = outer;
        _entries = new Hashtable();
        _usings = new Hashtable();

        final NamespaceScope gs = getGlobalScope();

        // add ourself to the global table of local namespaces
        String fullName = name;
        final String outerFullName = ((NamespaceScope) _outer)
                .fullNamespaceName(); // this is outer of 'new' one
        if (outerFullName != "") {
            fullName = outerFullName + "." + fullName;
        }
        gs._namespaces.put(fullName, this);
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

    public void addUsingAlias(final String alias, final String aliased,
            final String namespaceName) {
        final NamespaceScope namespaceScope = (namespaceName.length() > 0) ? getNamespaceScope(namespaceName)
                : this;
        if (namespaceScope == null) {
            ScigolTreeParser.semanticError(_location, "no name '"
                    + namespaceName + "." + aliased + "' can be found");
        }

        if (alias != null) {
            if (_usings.containsKey(alias)) {
                ScigolTreeParser.semanticError(
                        _location,
                        "the name '"
                                + alias
                                + "' is already an alias for '"
                                + ((UsingEntry) _usings.get(alias))
                                        .fullAliasedName() + "'");
            }
        } else {
            if (_usings.containsKey(aliased)) {
                ScigolTreeParser.semanticError(_location, "the name '"
                        + aliased + "' is already used for '"
                        + ((UsingEntry) _usings.get(aliased)).fullAliasedName()
                        + "'");
            }
        }

        if (!namespaceScope.contains(aliased)) {
            ScigolTreeParser.semanticError(_location, "the name '" + aliased
                    + "' cannot be found in namespace '" + namespaceName + "'");
        }

        if (alias != null) {
            _usings.put(alias, new UsingEntry(alias, aliased, namespaceScope));
        } else {
            _usings.put(aliased, new UsingEntry(aliased, namespaceScope));
        }

    }

    /*
     * public UsingEntry getUsingEntry(String id) { Debug.Assert(_scopeType ==
     * ScopeType.Namespace); ArrayList entryList = new ArrayList(); // all
     * entries in which id appears (either directly or as an aliased) foreach
     * (UsingEntry entry in _usings.Values) { if (!entry.isNamespace()) { if
     * (entry.name() == id) entryList.Add(entry); } else { if
     * (entry.scope.contains(id)) entryList.add(entry); } }
     * 
     * Debug.Assert(entryList.Count != 0,
     * "can't call getUsingEntry for id's not in the using list"); if
     * (entryList.Count == 1) return (UsingEntry)entryList[0];
     * 
     * // found multiple candidates String s =
     * "the name '"+id+"' is ambiguous in the namespace '"
     * +fullNamespaceName()+"' as it was introduced " +
     * "via using expressions from the following namespaces (either directly or via an alias): "
     * ; foreach(UsingEntry entry in entryList) { s +=
     * entry.scope.fullNamespaceName
     * ()+(entry.isAlias()?"(as "+entry.aliasedName+")":"")+", "; }
     * 
     * s +=
     * "and hence must either be fully qualified, or the ambiguity remved via aliasing"
     * ;
     * 
     * ScigolTreeParser.semanticError(_location,s); return new UsingEntry(); }
     */

    public void addUsingName(final String aliased, final String namespaceName) {
        addUsingAlias(null, aliased, namespaceName);
    };

    public void addUsingNamespace(final String namespaceName) {
        Debug.Assert(namespaceName != null);

        final NamespaceScope namespaceScope = getNamespaceScope(namespaceName);
        if (namespaceScope == null) {
            ScigolTreeParser.semanticError(_location, "no namespace named '"
                    + namespaceName + "' can be found");
        }

        if (_usings.containsKey(namespaceName)) {
            return; // already using
        }

        _usings.put(namespaceName, new UsingEntry(namespaceScope));
    };

    // library management

    @Override
    public boolean contains(final String name) {
        if (_namespaceType == NamespaceType.Local) {
            return (_entries.containsKey(name) || containsUsing(name));
        } else { // external
            final String fullName = _namespaceName + "." + name;

            return (loadedLibrariesGetType(fullName) != null);
        }
    }

    // helpers for usings
    boolean containsUsing(final String name) {
        for (final Object o : _usings.keySet()) {
            final String key = (String) o;
            final UsingEntry e = (UsingEntry) _usings.get(key);
            if (!e.isNamespace()) {
                if (e.name() == name) {
                    return e.scope.contains(e.aliasedName);
                }
            } else {
                if (e.scope.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String fullNamespaceName() {
        String fn = _namespaceName;
        Scope s = this;
        while (s.getOuter() != null) {
            s = s.getOuter();
            if ((s instanceof NamespaceScope)
                    && (((NamespaceScope) s).getNamespaceName().length() > 0)) {
                fn = ((NamespaceScope) s).getNamespaceName() + "." + fn;
            }
        }
        return fn;
    }

    // / get all declared (non-using/non-alias) entries in the current scope
    // named 'name' (or all if name==null)
    public Entry[] getDeclaredEntries(final String name, final Object instance) {
        final ArrayList nsentries = new ArrayList();

        if (_namespaceType == NamespaceType.Local) {
            if (name != null) {
                ArrayList entryList = null;
                if (_entries.containsKey(name)) {
                    entryList = (ArrayList) _entries.get(name);
                }

                if (entryList != null) {
                    for (final Object o : entryList) {
                        final Entry entry = (Entry) o;
                        nsentries.add(entry);
                    }
                }
            } else { // get all
                for (final Object o : _entries.keySet()) {
                    final String key = (String) o;
                    final ArrayList entryList = (ArrayList) _entries.get(key);
                    for (final Object eo : entryList) {
                        final Entry entry = (Entry) eo;
                        nsentries.add(entry);
                    }
                }
            }

        }

        // now external Java packages
        //
        // NB: There can be local scigol namespaces with the same names as Java
        // packages.
        // in this case we return boht the local entries and any we find in the
        // Java package

        // there are no overloaded names in Java packages, so if a name is
        // supplied,
        // then it can refer to at most one entry. If no name is supplied return
        // the names all all Types in the java package

        if (name != null) {

            // create an entry for Java type
            final String fullName = _namespaceName + "." + name;
            final Type t = loadedLibrariesGetType(fullName);
            if (t != null) {

                final Entry entry = new Entry(name, new TypeSpec(
                        TypeSpec.typeType), new TypeSpec(t), null,
                        EnumSet.of(TypeSpec.Modifier.Public),
                        EnumSet.noneOf(Entry.Flags.class), -1, this);
                if (t instanceof java.lang.Class) {
                    entry.addAnnotations(((java.lang.Class) t).getAnnotations());
                }

                nsentries.add(entry);
            }
        } else { // get all entries

            if (_namespaceName.length() > 0) {

                final Package p = Package.getPackage(_namespaceName);
                if (p != null) {

                    // package exists, so now get all the classes in it from the
                    // loaded libraries list
                    final LinkedList<String> classes = loadedPackages
                            .get(_namespaceName);
                    if (classes != null) {
                        // need to create an entry for each one
                        for (final String className : classes) {

                            final String fullName = _namespaceName + "."
                                    + className;

                            final Type t = loadedLibrariesGetType(fullName);
                            if (t != null) {
                                final Entry entry = new Entry(className,
                                        new TypeSpec(TypeSpec.typeType),
                                        new TypeSpec(t), null,
                                        EnumSet.of(TypeSpec.Modifier.Public),
                                        EnumSet.noneOf(Entry.Flags.class), -1,
                                        this);
                                if (t instanceof java.lang.Class) {
                                    entry.addAnnotations(((java.lang.Class) t)
                                            .getAnnotations());
                                }
                                nsentries.add(entry);
                            }
                            // else
                            // Debug.Warning("class '"+fullName+"' was in the loaded package list, but the ClassLoader was unable to find it");
                        }
                    }

                }

            }
        }

        return Entry.toArray(nsentries);
    }

    // / get all entries in the current scope named 'name' (or all if
    // name==null)
    @Override
    public Entry[] getEntries(final String name, final Object instance) {
        final ArrayList nsentries = new ArrayList();
        final Entry[] declaredEntries = getDeclaredEntries(name, instance);
        for (final Entry e : declaredEntries) {
            nsentries.add(e);
        }

        // now add using/alias Entries
        if (_namespaceType == NamespaceType.Local) {

            if ((name == null) || containsUsing(name)) {
                final Entry[] usingEntries = getUsingEntries(name);
                for (final Object o : usingEntries) {
                    final Entry e = (Entry) o;
                    nsentries.add(e);
                }
            }

        }

        return Entry.toArray(nsentries);
    }

    public String getNamespaceName() {
        return _namespaceName;
    }

    /*
     * public static Assembly[] getLoadedAssemblies() { Assembly[] la = new
     * Assembly[loadedAssemblies.Count]; for (int i = 0; i <
     * loadedAssemblies.Count; i++) la[i] = (Assembly)loadedAssemblies[i];
     * return la; }
     */

    // get Scope for fully qualified namespace, or null if not found
    public NamespaceScope getNamespaceScope(final String fullName) {
        final NamespaceScope gs = getGlobalScope();

        if (gs._namespaces.containsKey(fullName)) {
            return (NamespaceScope) gs._namespaces.get(fullName);
        }

        // look for an extern Java namespace
        if (loadedLibrariesContainNamespace(fullName)) {
            // create an external namespace
            final NamespaceScope ns = new NamespaceScope();
            ns._namespaceName = fullName;
            ns._outer = gs;
            ns._usings = new Hashtable();
            ns._namespaceType = NamespaceType.External;
            return ns;
        }

        return null;
    }

    Entry[] getUsingEntries(final String name) {
        final ArrayList entries = new ArrayList();

        for (final Object o : _usings.keySet()) {
            final String key = (String) o;
            final UsingEntry e = (UsingEntry) _usings.get(key);
            if (!e.isNamespace()) {
                if ((name == null) || (e.name() == name)) {
                    final Entry[] nsEntries = e.scope.getEntries(e.aliasedName,
                            null);
                    for (final Entry entry : nsEntries) {
                        if (!e.isAlias()) {
                            entries.add(entry);
                        } else {
                            final Entry aliasEntry = entry
                                    .createAliasEntry(e.aliasName);
                            entries.add(aliasEntry);
                        }
                    }
                }
            } else { // whole namespace
                final Entry[] nsEntries = e.scope.getEntries(name, null);
                for (int i = 0; i < nsEntries.length; i++) {
                    entries.add(nsEntries[i]);
                }
            }
        }

        // convert to array
        return Entry.toArray(entries);
    };

    protected void initGlobalScope() {
        _namespaceName = "";
        _outer = null;
        _namespaces = new Hashtable();
        _usings = new Hashtable();
    }

    public boolean isExternal() {
        return (_namespaceType == NamespaceType.External);
    }

    @Override
    public boolean isNamespaceScope() {
        return true;
    }

    @Override
    public Entry[] lookup(final String name, final FuncInfo callSig,
            final Object[] args, final Object instance) {
        // if this namespace contains any definition of name at all, it hides
        // those in
        // any outer namespace, so just perform overload resolution (if
        // necessary) to resolve it
        // (otherwise, defer to the outer namespace scope)
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
        } else if (containsUsing(name)) { // check using names
            Entry[] matches = getUsingEntries(name);
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

        if (_outer != null) {
            return _outer.lookup(name, callSig, args, instance);
        } else {
            return new Entry[0];
        }
    }

    @Override
    public String toString() {
        String s = "namespace " + fullNamespaceName() + ":\n";
        final Entry[] entries = getEntries(null, null); // all
        for (final Entry entry : entries) {
            final String id = entry.name;
            String typeString = entry.type.toString();
            if (entry.type.isAny() || entry.type.isNum()) {
                final TypeSpec vtype = TypeSpec.typeOf(TypeSpec
                        .unwrapAnyOrNum(entry.getStaticValue()));
                typeString += "(" + vtype + ")";
            }
            s += entry.modifiers + " " + id + " : " + typeString + " = "
                    + entry.getStaticValue() + "\n";
        }
        return s;
    }

    // to addUsingNamespace for all known top-level namespaces
    // (e.g. use in interactive interpreter)
    public void usingAll() {
        Debug.Assert(_namespaceName != null);

        for (final Object o : _namespaces.keySet()) {
            final String nsName = (String) o;
            addUsingNamespace(nsName);
        }
    }

}
