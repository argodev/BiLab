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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import antlr.RecognitionException;
import antlr.collections.AST;

// / Hold information about a class or interface type
// / (members, bases/interfaces)
public class ClassInfo {

    // to avoid infinite recursion in type comparison, we need to keep this
    // stack of which pairs
    // of classInfo's were currently evaluating.
    protected class ClassInfoPair {
        ClassInfo first, second;

        public ClassInfoPair(final ClassInfo first, final ClassInfo second) {
            this.first = first;
            this.second = second;
        }
    }

    protected enum ClassType {
        Local, External
    }

    // \TODO !!! store Java Annotations for all target types (methods,
    // constructors, , fields, classes etc.) - currently only methods supported
    protected static Entry[] externalGetAllDeclaredEntries(
            final ClassInfo declaringClass) {
        Debug.Assert(declaringClass.isExternal(),
                "isn't an external Java class");
        Debug.Assert(declaringClass.getSysType() instanceof java.lang.Class,
                "can only get members of a class or interface (not "
                        + declaringClass + ")");
        final java.lang.Class sysType = (java.lang.Class) declaringClass
                .getSysType();

        // check if type has been cached first
        if (javaTypes.containsKey(sysType)) {
            return javaTypes.get(sysType);
        }
        // Debug.WL("reflecting Java type: "+declaringClass);

        final ClassScope classScope = new ClassScope(new TypeSpec(
                declaringClass));

        // don't include accessors (include a property Entry upon encountering
        // accessor methods)

        final ArrayList entries = new ArrayList();

        // extract constructors

        final Constructor[] constructors = sysType.getDeclaredConstructors();

        for (final Constructor constructor : constructors) {
            final EnumSet<TypeSpec.Modifier> modifiers = TypeSpec
                    .modifiersFromJavaModifiers(constructor.getModifiers());

            final EnumSet<Entry.Flags> flags = EnumSet.of(Entry.Flags.Method);
            final TypeSpec type = new TypeSpec(constructor);
            type.getFuncInfo().setReturnType(new TypeSpec(declaringClass)); // constructor
                                                                            // 'returns'
                                                                            // an
                                                                            // instance
                                                                            // of
                                                                            // its
                                                                            // declaring
                                                                            // type
            final Func func = new Func(sysType, constructor);
            modifiers.add(TypeSpec.Modifier.Const); // Java class methods can't
                                                    // be assigned
            modifiers.add(TypeSpec.Modifier.Static); // and are effectively
                                                     // static
            entries.add(new Entry(".ctor", ".ctor", type, func, null,
                    modifiers, flags, -2, classScope));

        } // for constructors

        // now similarly, for methods
        final Method[] methods = sysType.getDeclaredMethods();

        for (final Method method : methods) {

            String name = method.getName();
            String javaName = null;
            final EnumSet<TypeSpec.Modifier> modifiers = TypeSpec
                    .modifiersFromJavaModifiers(method.getModifiers());

            if (TypeSpec.isJavaOperator(name)) {
                if (name.equals("op_Implicit") || name.equals("toString")) {
                    modifiers.add(TypeSpec.Modifier.Implicit);
                }
                javaName = name;
                name = TypeSpec.operatorSourceName(javaName);
            }

            boolean isAccessor = method.isAnnotationPresent(accessor.class);
            Debug.Assert(
                    !isAccessor
                            || (name.startsWith("get_") || name
                                    .startsWith("set_")),
                    "methods annotated as accessors must start with set_ or get_");

            // !!! eclipse compiler doesn't add annotations yet
            if (name.startsWith("get_") || name.startsWith("set_")
                    && !isAccessor) {
                // Debug.WL("warning - method '"+name+"' of type '"+declaringClass+"' isn't annotated @accessor");
                isAccessor = true;
            }

            if (!isAccessor) {
                final EnumSet<Entry.Flags> flags = EnumSet
                        .of(Entry.Flags.Method);
                final TypeSpec type = new TypeSpec(method);
                final Func func = new Func(sysType, method);
                if (isAccessor) {
                    flags.add(Entry.Flags.Accessor);
                }
                modifiers.add(TypeSpec.Modifier.Const); // Java class methods
                                                        // can't be assigned
                final Entry methodEntry = new Entry(name, javaName, type, func,
                        null, modifiers, flags, -2, classScope);
                methodEntry.addAnnotations(method.getAnnotations());
                entries.add(methodEntry);
            } else { // the method is an accessor, create property instead

                final String accessorName = name.substring(0, 3); // 'get' or
                                                                  // 'set'
                final String propertyName = FuncInfo.propertyName(name); // get
                                                                         // property
                                                                         // name
                                                                         // from
                                                                         // accessor
                                                                         // name
                                                                         // (i.e.
                                                                         // the
                                                                         // name
                                                                         // after
                                                                         // 'get_'/'set_')

                // create a property entry - tricky part is that it needs to be
                // connected to its accessor(s)

                // deduce property type
                final FuncInfo accessorFuncInfo = new FuncInfo(method);
                final TypeSpec propertyType = FuncInfo
                        .propertyTypeFromAccessor(accessorName,
                                accessorFuncInfo);

                // if there is already a property with the same name & signature
                // in the list, it is because we've hit
                // the other accessors already. If so, just add this one,
                // otherwise create a new property entry first

                // look for existing property with this name/signature
                Entry propEntry = null; // existing property entry (if found) or
                                        // new entry (created below)
                FuncInfo propArgsInfo = null; // existing property sig (if
                                              // found) or new sig (created
                                              // below)

                for (final Object o : entries) {
                    final Entry entry = (Entry) o;
                    if (entry.isProperty() && entry.name.equals(propertyName)
                            && entry.type.equals(propertyType)) {
                        // construct the property signature and check for
                        // equality
                        final FuncInfo propertyArgsInfo = accessorFuncInfo
                                .propertySig(accessorName); // sig corresponding
                                                            // to the accessor
                                                            // we've encountered
                                                            // in sysType
                        // the sig of the prop we've found existing in entries
                        // can be deduces by getting it's accessors entries,
                        // which are
                        // stored in an EntryPair in entry.staticValue
                        final Entry.EntryPair ep = (Entry.EntryPair) entry
                                .getStaticValue();
                        if (ep.getter != null) {
                            propArgsInfo = ep.getter.type.getFuncInfo()
                                    .propertySig("get");
                        } else if (ep.setter != null) {
                            propArgsInfo = ep.setter.type.getFuncInfo()
                                    .propertySig("set");
                        } else {
                            Debug.Assert(false,
                                    "encountered property entry with no accessor entries!");
                        }

                        // finally, we can check if the sig is the same to see
                        // if we've found an accessor for a property we already
                        // created
                        if (propArgsInfo.equalsParams(propertyArgsInfo)) {
                            propEntry = entry; // yes
                            break; // no need to look further
                        }
                    }
                }

                // if an existing property matching this accessor method was
                // found, it is in propEntry, otherwise it is null (and we must
                // create a new property entry)

                // first, create accessor method entry
                final EnumSet<Entry.Flags> flags = EnumSet.of(
                        Entry.Flags.Method, Entry.Flags.Accessor);
                final TypeSpec type = new TypeSpec(method);
                final Func func = new Func(sysType, method);
                modifiers.add(TypeSpec.Modifier.Const); // Java class methods
                                                        // can't be assigned
                final Entry accessorEntry = new Entry(name, javaName, type,
                        func, null, modifiers, flags, -2, classScope);

                if (propEntry != null) {
                    // add this accessor to the existing property entry
                    final Entry.EntryPair ep = (Entry.EntryPair) propEntry
                            .getStaticValue();

                    if (accessorName.equals("get")) {
                        ep.getter = accessorEntry;
                    } else {
                        ep.setter = accessorEntry;
                    }

                    accessorEntry.propertyEntry = propEntry; // connect accessor
                                                             // back to property

                    // check for consistiency (i.e. that the return type of the
                    // getter is equal to the last arg type of the setter)
                    if ((ep.setter != null) && (ep.getter != null)) {
                        final TypeSpec[] setterArgTypes = ep.setter.type
                                .getFuncInfo().getParamTypes();
                        if (!setterArgTypes[setterArgTypes.length - 1]
                                .equals(ep.getter.type.getFuncInfo()
                                        .getReturnType())) {
                            Debug.Warning("external Java class '"
                                    + declaringClass
                                    + "' has property '"
                                    + propertyName
                                    + ":"
                                    + propertyType
                                    + "' with unmatched getter return type & setter value argument type.");
                        }
                    }

                    // Debug.WL("added "+accessorName+" accessor to property "+propertyName+":"+propertyType+" of type "+declaringClass+"' :"+accessorEntry.type);
                    // Debug.WL("added property "+propertyName+":"+propertyType+" of type "+declaringClass+"' get="+ep.getter.type+" set="+ep.setter.type);
                } else { // create new property entry

                    final EnumSet<Entry.Flags> pflags = EnumSet
                            .of(Entry.Flags.Property);
                    final EnumSet<TypeSpec.Modifier> pmodifiers = modifiers;
                    pmodifiers.remove(TypeSpec.Modifier.Const); // not
                                                                // applicable

                    propEntry = new Entry(propertyName, propertyType, null,
                            null, pmodifiers, pflags, -2, classScope);
                    accessorEntry.propertyEntry = propEntry; // connect accessor
                                                             // back to property

                    final Entry.EntryPair ep = new Entry.EntryPair();
                    if (accessorName.equals("get")) {
                        ep.getter = accessorEntry;
                    } else {
                        ep.setter = accessorEntry;
                    }

                    propEntry.setStaticValue(ep);

                    // add the new property entry to the list
                    entries.add(propEntry);

                    // Debug.WL("added new property "+propertyName+":"+propertyType+" to type "+declaringClass+" with "+accessorName+" accessor:"+accessorEntry.type);

                }
            }

        } // for methods

        // and finally fields
        final Field[] fields = sysType.getDeclaredFields();

        for (final Field field : fields) {

            final String fname = field.getName();
            final TypeSpec type = new TypeSpec(field.getType());
            final EnumSet<TypeSpec.Modifier> modifiers = TypeSpec
                    .modifiersFromJavaModifiers(field.getModifiers());

            entries.add(new Entry(fname, type, null, null, modifiers, EnumSet
                    .of(Entry.Flags.Field), -2, classScope));

        }

        // enter into java type cache for later
        final Entry[] entryArray = Entry.toArray(entries);
        javaTypes.put(sysType, entryArray);

        return entryArray;
    }

    protected static Entry[] externalGetDeclaredEntries(
            final ClassInfo declaringClass, final String findName) {
        // just get all the entries, then look for the one we want
        // Note, that if findName looks like a property accessor, also look for
        // property entries and
        // return the appropriate accessors

        final boolean getAll = (findName == null);

        final Entry[] allentries = externalGetAllDeclaredEntries(declaringClass);

        if (getAll) {
            return allentries;
        }

        final java.lang.Class sysType = (java.lang.Class) declaringClass
                .getSysType();
        final boolean findAccessor = (findName.startsWith("get_") || findName
                .startsWith("set_"));
        String findPropName = findName;
        if (findAccessor) {
            findPropName = FuncInfo.propertyName(findName);
        }

        final ArrayList entries = new ArrayList(); // the entries to return

        for (final Entry entry : allentries) {

            if (!findAccessor) {
                if (entry.name.equals(findName)) {
                    entries.add(entry);
                }
            } else {
                final String findAccessorName = findName.substring(0, 3); // 'set'
                                                                          // or
                                                                          // 'get'
                final String propFindName = FuncInfo.propertyName(findName);
                if (entry.isProperty() && entry.name.equals(propFindName)) {
                    final Entry.EntryPair ep = (Entry.EntryPair) entry
                            .getStaticValue();
                    if (findAccessorName.equals("get")) {
                        if (ep.getter != null) {
                            entries.add(ep.getter);
                        }
                    } else { // set
                        if (ep.setter != null) {
                            entries.add(ep.setter);
                        }
                    }
                }
            }

        } // for

        return Entry.toArray(entries);
    }

    protected ArrayList comparisonPairStack = new ArrayList(); // list of
                                                               // ClassInfoPair's

    // need a way to avoid infinite looping when making strings for types that
    // ultimately self refernece,
    // so keep a stack
    protected static ArrayList toStringArray = new ArrayList();

    // cache for Java types
    static HashMap<java.lang.Class, Entry[]> javaTypes = new HashMap<java.lang.Class, Entry[]>(); // !!
                                                                                                  // try
                                                                                                  // switching
                                                                                                  // to
                                                                                                  // a
                                                                                                  // map
                                                                                                  // that
                                                                                                  // uses
                                                                                                  // identity
                                                                                                  // instead
                                                                                                  // of
                                                                                                  // equality

    public static String entryToString(final Entry entry) {
        if (entry == null) {
            return "null";
        }
        String s = "  ";
        s += TypeSpec.modifiersString(entry.modifiers) + " ";
        String name = entry.name;
        if (TypeSpec.isOperator(name)) {
            name = TypeSpec.operatorSourceName(name);
        }
        s += name;
        final boolean staticNonNullFunc = entry.type.isFunc()
                && (entry.getStaticValue() != null);
        if (!staticNonNullFunc) {
            s += " :" + entry.type;
        } else {
            s += " = " + entry.getStaticValue();
        }
        if (entry.isProperty()) {
            s += " property ";
            if (entry.hasSetter()) {
                s += "set";
                if (entry.hasGetter()) {
                    s += "; get";
                }
            } else if (entry.hasGetter()) {
                s += "get";
            }
        }

        // if (!entry.isStatic()) // instance
        // s += " ["+entry.index+"]";
        s += "\n";
        return s;
    }

    protected Scope _outerScope; // Scope in which class defined

    protected ClassType _classType; // is the Class a Scigol one or a general
                                    // CLI one?

    protected boolean _isAbstract; // are any of the methods abstract?

    protected boolean _isInterface; // are all of the methods abstract?

    protected EnumSet<TypeSpec.Modifier> _modifiers;

    protected TypeSpec _superClass;

    protected ArrayList _interfaces; // list of TypeSpec

    protected boolean complete; // is the definition complete? (i.e.
                                // completeDefinition called?)

    /*
     * // true if this class directly depends on class c boolean
     * directlyDependsOn(ClassInfo c) { // directly depends if c is direct base
     * if ((_superClass != null) && _superClass.equals(c)) return true; //
     * directly depends on immediate most nested class it is declared within (if
     * any) // !!! might want to relax this if it makes no references to nested
     * classes members
     * 
     * // first first class scope we're nested within (if any) Scope outer =
     * _outerScope; while ((outer != null) && (!outer.isClassScope())) outer =
     * outer.outer; if ((outer != null) && (outer is ClassScope)) return
     * ((ClassScope)outer).classType.classInfo.equals(c); return false; }
     */

    // Local classes only
    protected ArrayList _members; // list of Entry of declared members

    // since dynamic types don't have names, a identity hint can be provided
    // which will be reported in
    // error messages to help identify the type (typically the first variable
    // name the type was assigned to,
    // or maybe the variabele that was assigned a func in which the type was
    // defined)
    protected String identityHint = null;

    // !!!!
    // this constructs the call sig param types from the actual types of the
    // args
    // However, that doesn't work if an arg is null, as it's type information is
    // lost!

    protected Location _location = new Location(); // source location where
                                                   // defined

    // extern classes
    java.lang.reflect.Type _sysType;

    protected ClassInfo() {
        _classType = ClassType.Local;
        _outerScope = null;
        _modifiers = EnumSet.of(TypeSpec.Modifier.Public);
        _superClass = TypeSpec.objectTypeSpec;
        _isInterface = false;
        _isAbstract = false;
        _interfaces = new ArrayList();
        _members = new ArrayList();
        addMember("self", EnumSet.of(Entry.Flags.Field), TypeSpec.typeTypeSpec,
                EnumSet.of(TypeSpec.Modifier.Static, TypeSpec.Modifier.Public),
                new TypeSpec(this), null);
        complete = false;
    }

    // create new External Java Class
    public ClassInfo(final java.lang.Class externalClassType) {
        _classType = ClassType.External;
        _sysType = externalClassType;
        _isAbstract = Modifier.isAbstract(externalClassType.getModifiers());
        _isInterface = externalClassType.isInterface();

        // set base class
        if (!_isInterface) {
            final java.lang.Class baseType = externalClassType.getSuperclass();
            _superClass = null; // NB: Object has null base
            if (baseType != null) {
                if (!baseType.equals(Void.TYPE)) {
                    _superClass = new TypeSpec(baseType);
                }
            }
        } else {
            _superClass = TypeSpec.objectTypeSpec; // interfaces inherit from
                                                   // Object too
        }

        // set implemented interfaces
        _interfaces = new ArrayList();
        final java.lang.Class[] interfaces = externalClassType.getInterfaces();
        for (final java.lang.Class it : interfaces) {
            _interfaces.add(new TypeSpec(it));
        }

        _members = null;
        complete = true;
    }

    // create a new Local Class (if superClass is null -> interface)
    public ClassInfo(final Scope outerScope, final TypeSpec superClass) {
        if ((superClass != null) && superClass.isBuiltin()
                && !superClass.isBuiltinObject()) {
            ScigolTreeParser
                    .semanticError("can only subclass builtin type 'object' (i.e. not '"
                            + superClass + "')");
        }

        _classType = ClassType.Local;
        _outerScope = outerScope;
        _modifiers = EnumSet.of(TypeSpec.Modifier.Public);
        _superClass = superClass;
        _isInterface = (superClass == null);
        if (_isInterface) {
            _superClass = new TypeSpec(TypeSpec.objectType);
        }
        _isAbstract = _isInterface;
        _interfaces = new ArrayList();
        _members = new ArrayList();
        // add special self field that is static reference to the type of this
        addMember("self", EnumSet.of(Entry.Flags.Field), TypeSpec.typeTypeSpec,
                EnumSet.of(TypeSpec.Modifier.Static, TypeSpec.Modifier.Public),
                new TypeSpec(this), null);

        complete = false;
    }

    // / indicate that this class/interface implements the specified interface
    // / (call this for each implemented interface *before* adding any members)
    public void addInterface(final TypeSpec interfaceType) {
        Debug.Assert(interfaceType.isInterface());
        Debug.Assert(!complete);
        _interfaces.add(interfaceType);
    }

    public void addMember(final String name, final EnumSet<Entry.Flags> flags,
            final TypeSpec type, final EnumSet<TypeSpec.Modifier> modifiers,
            final Object value, final AST initializer) {
        Debug.Assert(name != null);

        // if value is null => no initializer supplied, use default at
        // construction time
        // hence, don't allow out self as a type (because construction of self
        // at construction initialization
        // time is infinitely recursive!)
        if ((initializer == null) && type.isClass()
                && (!flags.contains(Entry.Flags.Property))
                && (type.getClassInfo() == this)) {
            ScigolTreeParser
                    .semanticError("instance members of type 'self' in class '"
                            + this
                            + "' must have an initializer supplied (as construction of default self() is recursive)");
        }

        String actualName = null;

        if (!type.isFunc()) { // adding a field

            Debug.Assert(flags.contains(Entry.Flags.Field));

            if (TypeSpec.isOperator(name)) {
                ScigolTreeParser
                        .semanticError("the name '"
                                + name
                                + "' is a special func member (method) name for operator overloading and cannot be used for fields (in class '"
                                + this + "')");
            }

            if (_isInterface && !(name == "self")) {
                ScigolTreeParser.semanticError("an interface '" + this
                        + "' cannot contain a field ('" + name + "')");
            }

            if (hasMember(name)) {

                // check for existing fields
                // ...!!!

                // if (declaresMember(name))
                // ScigolTreeParser.semanticError("class '"+this+"' already defines a member named '"+name+"' which is of type '"+getMemberType(name)+"'");

                // check for invalid field modifiers
                // ...!!!

            }

        } else { // adding a method

            Debug.Assert(flags.contains(Entry.Flags.Method));

            // convert special operator names, and check they are declared
            // appropriately
            if (TypeSpec.isOperator(name)) {
                if (!name.equals("operator->")) {
                    if ((!modifiers.contains(TypeSpec.Modifier.Static))
                            || (!modifiers.contains(TypeSpec.Modifier.Public))) {
                        ScigolTreeParser
                                .semanticError("operator '"
                                        + name
                                        + "' must be declared 'public static' in class '"
                                        + this + "'");
                    }

                    boolean binary = true;
                    if (type.getFuncInfo().numArgs() == 1) {
                        binary = false;
                    } else if ((type.getFuncInfo().numArgs() != 2)
                            || (type.getFuncInfo().numArgs() != type
                                    .getFuncInfo().numRequiredArgs())) {
                        ScigolTreeParser
                                .semanticError("operators must have one (unary) or two (binary) arguments (with no default values) in class '"
                                        + this + "'");
                    }

                    // !!! also check the binary/unary correcteness against
                    // names
                    // (e.g. no binary ++ or unary /)

                    // !!! check correct argument types & return

                    actualName = TypeSpec.operatorJavaName(name, binary);
                } else { // user-defined conversion operator

                    if (!modifiers.contains(TypeSpec.Modifier.Public)) {
                        ScigolTreeParser.semanticError("operator '" + name
                                + "' must be declared 'public' in class '"
                                + this + "'");
                    }

                    if (!modifiers.contains(TypeSpec.Modifier.Implicit)) {
                        actualName = "op_Implicit";
                    } else {
                        actualName = "op_Explicit";
                    }

                    // if static, must have one arg, else if instance, no args
                    if (modifiers.contains(TypeSpec.Modifier.Static)) {
                        if ((type.getFuncInfo().numArgs() != 1)
                                || (type.getFuncInfo().numRequiredArgs() != 1)) {
                            ScigolTreeParser
                                    .semanticError("static conversion operators must have one argument (with no default value) in class '"
                                            + this + "'");
                        }
                    } else { // instance
                        if (type.getFuncInfo().numArgs() != 0) {
                            ScigolTreeParser
                                    .semanticError("instance conversion operators must have no arguments in class '"
                                            + this + "'");
                        }

                        // exception for operator-> = func(->string) which we
                        // map to toString()
                        if (type.getFuncInfo().getReturnType().isString()) {
                            actualName = "toString";
                        }
                    }

                }

            }

            if (_isInterface) {
                modifiers.add(TypeSpec.Modifier.Abstract); // all interface
                                                           // methods are
                                                           // abstract (by
                                                           // definition)
            }

            /*
             * if (hasMethod(actualName, type)) {
             * 
             * if (declaresMethod(name,type)) { // only allow valid overloads
             * //...!!! } else { // if the existing declaration is in an
             * interface, this is OK, but if it is in a super class // the
             * override modifier must be specified (!!! check this logic)
             * //...!!! }
             * 
             * // check for invalid method modifiers //...!!!
             * 
             * }
             */

        }
        /*
         * Debug.Write("**** adding member:"+name+" ["+actualName+"]");
         * Debug.Write("  type:"+type); if (value != null)
         * Debug.WriteLine("  initializer type:"+value.GetType()); else
         * Debug.WriteLine("  no initializer");
         */
        final Entry e = new Entry(name, actualName, type, value, initializer,
                modifiers, flags, -1, new ClassScope(new TypeSpec(this)));
        _members.add(e);
    }

    protected boolean comparisonStackContains(final ClassInfoPair pair) {
        for (final Object o : comparisonPairStack) {
            final ClassInfoPair p = (ClassInfoPair) o;
            if (p.equals(pair)) {
                return true;
            }
        }
        return false;
    }

    // call this after defining methods but before use
    // (ensures definition is valid, adds any default constructors etc.)
    public void completeDefinition() {
        Debug.Assert(_classType == ClassType.Local,
                "can't call completeDefinition() on external class types");
        Debug.Assert(!complete, "class already complete");

        // check for constructors, if none, implement default no-arg no-op one
        if (!_isInterface && !declaresMember(".ctor")) {
            final FuncInfo fi = new FuncInfo();
            fi.setReturnType(new TypeSpec(this));
            addMember(".ctor", EnumSet.of(Entry.Flags.Method),
                    new TypeSpec(fi), EnumSet.of(TypeSpec.Modifier.Public,
                            TypeSpec.Modifier.Static), null, null);
        }

        if (!_isAbstract) {
            // compute the instance indcies of our declared instance members

            // next avail index
            int instanceIndex = 0;
            // !!! this doesn't exclude base properties - fix that
            if (_superClass != null) {
                instanceIndex += _superClass.getClassInfo()
                        .getAllInstanceEntries(null).length;
            }

            final Entry[] declaredMembers = getDeclaredEntries(null);

            for (final Entry entry : declaredMembers) {
                if (!entry.isStatic() && !entry.isProperty()) { // instance
                                                                // member (real,
                                                                // not property)
                    entry.index = instanceIndex++;
                } else {
                    entry.index = 0;
                }
            }

        }

        complete = true;

        // Debug.WriteLine("completed definition of:\n"+this.ToStringFull());
    }

    // convenience - returns true if at least one member named 'name' declared
    // in this class
    public boolean declaresMember(final String name) {
        Debug.Assert(name != null);
        final Entry[] declaredMembers = getDeclaredEntries(name);
        for (final Entry e : declaredMembers) {
            if (e.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    // get classes this class depends on
    public ClassInfo[] dependsOn() {
        // !!! implement - just return direct depends for now
        return directlyDependsOn();
    }

    // get classes this class directly depends on (its direct base and outermost
    // enclosing class (if any)
    public ClassInfo[] directlyDependsOn() {

        // first first class scope we're nested within (if any)
        Scope outer = _outerScope;
        while ((outer != null) && (!outer.isClassScope())) {
            outer = outer.getOuter();
        }
        if ((outer != null) && (outer instanceof ClassScope)) {
            final ClassInfo[] infos = new ClassInfo[2];
            if (_superClass != null) {
                infos[0] = ((ClassScope) outer).getClassType().getClassInfo();
                infos[1] = _superClass.getClassInfo();
                return infos;
            } else {
                infos[0] = ((ClassScope) outer).getClassType().getClassInfo();
                infos[1] = new ClassInfo(TypeSpec.objectType);
                return infos;
            }
        } else {
            final ClassInfo[] info = new ClassInfo[1];
            if (_superClass != null) {
                info[0] = _superClass.getClassInfo();
            } else {
                info[0] = new ClassInfo(TypeSpec.objectType);
            }
            return info;
        }

    }

    // true if this class represents the same type as info.
    // if both are external, name identity is used; otherwise structural
    // equivelance is used
    public boolean equals(final Object classInfo) {
        if (classInfo == null) {
            return false;
        }
        if (classInfo == this) {
            return true;
        }
        if (!(classInfo instanceof ClassInfo)) {
            return false;
        }

        final ClassInfo info = (ClassInfo) classInfo;

        // both external
        if ((_classType == ClassType.External)
                && (info._classType == ClassType.External)) {
            return _sysType.equals(info._sysType);
        }

        final ClassInfoPair currentPair = new ClassInfoPair(this, info);

        if (comparisonStackContains(currentPair)) {
            return true; // don't recurse
        }

        comparisonPairStack.add(currentPair);
        // Debug.WriteLine("comparing:"+this+" WITH "+info);
        // Debug.WriteLine("  ==>>"+this.ToStringFull()+" WITH "+info.ToStringFull());

        if ((_isInterface && !info._isInterface)
                || (!_isInterface && info._isInterface)) {
            comparisonPairStack.remove(currentPair);
            return false;
        }
        if ((_isAbstract && !info._isAbstract)
                || (!_isAbstract && info._isAbstract)) {
            comparisonPairStack.remove(currentPair);
            return false;
        }

        // same supers? (System.Object has a null super)
        if ((_superClass != null) && (info._superClass != null)) {
            if (!_superClass.equals(info._superClass)) {
                comparisonPairStack.remove(currentPair);
                return false;
            }
        }

        // same interfaces?
        if (_interfaces.size() != info._interfaces.size()) {
            comparisonPairStack.remove(currentPair);
            return false;
        }
        for (int i = 0; i < _interfaces.size(); i++) {
            if (!((TypeSpec) _interfaces.get(i))
                    .equals(info._interfaces.get(i))) {
                comparisonPairStack.remove(currentPair);
                return false;
            }
        }

        // normally, ClassInfo's that represent external types just hold the
        // Type _sysType of
        // it, but not the full member infotmation. Extract the full member
        // information if
        // necessary
        /*****
         * FIX ME FOR JAVA !!!
         * 
         * 
         * BindingFlags bindingFlags = BindingFlags.DeclaredOnly |
         * BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public |
         * BindingFlags.NonPublic; ClassInfo thisInfo = this; if (_classType ==
         * ClassType.External) { thisInfo = new ClassInfo(_sysType);
         * thisInfo._members = new ArrayList(); System.Reflection.MemberInfo[]
         * members = _sysType.GetMembers(bindingFlags);
         * foreach(System.Reflection.MemberInfo minfo in members)
         * thisInfo._members.Add(entryFromMemberInfo(minfo, thisInfo)); //!!!
         * handle properties in comparisons here... }
         * 
         * ClassInfo otherInfo = info; if (info._classType ==
         * ClassType.External) { otherInfo = new ClassInfo(info._sysType);
         * otherInfo._members = new ArrayList(); System.Reflection.MemberInfo[]
         * members = info._sysType.GetMembers(bindingFlags);
         * for(System.Reflection.MemberInfo minfo : members)
         * otherInfo._members.Add(entryFromMemberInfo(minfo, otherInfo)); //!!!
         * handle properties in comparisons here... }
         */

        final ClassInfo thisInfo = this;// tmp from above!!!
        final ClassInfo otherInfo = info;

        // first test for different numbers of members
        if (thisInfo._members.size() != otherInfo._members.size()) {
            comparisonPairStack.remove(currentPair);
            return false;
        }

        // now we have full member info for each class, do a member-by-member
        // comparison
        // for compatibility (i.e. every member in thisInfo must have a
        // compatible member present
        // in otherInfo at the same position)
        for (int i = 0; i < thisInfo._members.size(); i++) {
            final Entry thisMInfo = (Entry) thisInfo._members.get(i);
            final Entry otherMInfo = (Entry) otherInfo._members.get(i);

            if (thisMInfo.name != "self") { // avoid recursive comparison of
                                            // special member 'self'
                if (thisMInfo.name != otherMInfo.name) {
                    comparisonPairStack.remove(currentPair);
                    return false;
                }
                if (thisMInfo.name != ".ctor") {
                    // !!! probably don't need this complex logic as we can
                    // happily do a recursice call and let the stack avoid it!
                    // rather than recurse into members of type 'self', just
                    // assume they matching
                    // (if both class types equal to their respective 'self')
                    if (thisMInfo.type.isClass() // if this member is a class
                            && (thisMInfo.type.getClassInfo() == thisInfo)) { // and
                                                                              // that
                                                                              // class
                                                                              // is
                                                                              // 'self'
                        if ((!otherMInfo.type.isClass()) // if either the other
                                                         // member isn't a class
                                || (otherMInfo.type.isClass() // or it is,
                                && (otherMInfo.type.getClassInfo() != otherInfo))) // but
                                                                                   // not
                                                                                   // 'self'
                        {
                            comparisonPairStack.remove(currentPair);
                            return false;
                        } // then, no match
                    } else
                    // otherwise, normal case is just to compare the member
                    // types for compatibility
                    if (!thisMInfo.type.equals(otherMInfo.type)) {
                        comparisonPairStack.remove(currentPair);
                        return false;
                    }

                } else { // special case for constructors, avoid comparing
                         // return types
                    if (!otherMInfo.type.isFunc()) {
                        comparisonPairStack.remove(currentPair);
                        return false;
                    }
                    if (!thisMInfo.type.getFuncInfo().equalsParams(
                            otherMInfo.type.getFuncInfo())) {
                        comparisonPairStack.remove(currentPair);
                        return false;
                    }
                }
                if (thisMInfo.modifiers != otherMInfo.modifiers) {
                    comparisonPairStack.remove(currentPair);
                    return false;
                }
            }
        }

        comparisonPairStack.remove(currentPair);

        return true;
    }

    protected String externToStringFull() {
        if (!(_sysType instanceof java.lang.Class)) {
            return _sysType.toString();
        }

        String s = "";

        final java.lang.Class sysClass = (java.lang.Class) _sysType;

        // name
        s += sysClass.isInterface() ? "interface" : "class";
        s += " " + toString();

        // get bases/interaces
        boolean addedColon = false;
        final java.lang.Class baseClass = sysClass.getSuperclass();
        if ((baseClass != null) && (!baseClass.equals(Object.class))) {
            s += " : " + (new ClassInfo(baseClass)).toString();
            addedColon = true;
        }

        final java.lang.Class[] interfaces = sysClass.getInterfaces();
        for (final java.lang.Class baseInterface : interfaces) {
            if (!addedColon) {
                s += " : ";
                addedColon = true;
            } else {
                s += ", ";
            }

            s += new ClassInfo(baseInterface).toString();
        }

        s += "\n{\n";

        final Entry[] declEntries = externalGetDeclaredEntries(this, null);

        for (final Entry entry : declEntries) {
            if (!entry.isAccessor()) {
                s += entryToString(entry);
            }
        }
        s += "}";

        return s;
    }

    // get all entries (including those of base classes, interfaces; abstract &
    // hidden)
    // matching 'name' (all if name==null)
    public Entry[] getAllEntries(final String name) {
        final ArrayList entries = new ArrayList();

        // base interfaces
        for (final Object o : _interfaces) {
            final TypeSpec interfaceType = (TypeSpec) o;
            final Entry[] interfaceEntries = interfaceType.getClassInfo()
                    .getAllEntries(name);
            for (final Entry e : interfaceEntries) {
                entries.add(e);
            }
        }

        // base class
        if (_superClass != null) {
            final Entry[] superEntries = _superClass.getClassInfo()
                    .getAllEntries(name);
            for (final Entry e : superEntries) {
                entries.add(e);
            }
        }

        // our declared members
        final Entry[] declaredEntries = getDeclaredEntries(name);
        for (final Entry e : declaredEntries) {
            entries.add(e);
        }

        return Entry.toArray(entries);
    }

    public Entry[] getAllInstanceEntries(final String name) {
        final ArrayList instanceEntries = new ArrayList();
        final Entry[] allEntries = getAllEntries(name);
        for (final Entry e : allEntries) {
            if (!e.isStatic() && !e.isAbstract()) {
                instanceEntries.add(e);
            }
        }
        return Entry.toArray(instanceEntries);
    }

    // type of base class, or null if none
    public TypeSpec getBaseClass() {
        return _superClass;
    }

    // get declared Entries with name 'name' or all if name==null
    public Entry[] getDeclaredEntries(final String name) {

        if (_classType == ClassType.Local) {

            final ArrayList entries = new ArrayList();

            // grab _members with specified name

            for (final Object o : _members) {
                final Entry entry = (Entry) o;
                if (name == null) { // get any name
                    entries.add(entry);
                } else { // check for correct name
                    if (entry.name.equals(name)) {
                        entries.add(entry);
                    }
                }

            } // foreach

            return Entry.toArray(entries);

        } else { // external Java class
            return externalGetDeclaredEntries(this, name);
        }

    }

    public Location getDefinitionLocation() {
        if (_location == null) {
            return new Location();
        } else {
            return _location;
        }
    }

    public EnumSet<TypeSpec.Modifier> getModifiers() {
        return _modifiers;
    }

    public Scope getOuterScope() {
        return _outerScope;
    }

    public Type getSysType() {
        Debug.Assert(_classType == ClassType.External);
        return _sysType;
    }

    public int hashCode() {
        Debug.Unimplemented();
        return 0;
    }

    // convenience - returns true if at least one member named 'name' exists in
    // this class (i.e. including bases)
    public boolean hasMember(final String name) {
        Debug.Assert(name != null);
        final Entry[] allMembers = getAllEntries(name);
        for (final Entry e : allMembers) {
            if (e.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String instanceToString(final Class instance) {
        Debug.Assert(_classType == ClassType.Local);
        Debug.Assert(instance != null,
                "can't convert Class instance to string without an instance!");
        Debug.Assert(instance.getInfo().equals(this));

        String s = null;
        final ArrayList memberValues = instance.getMemberValues();
        // !!! change this to use
        // TypeManager.performExplicitConversion(self,'String'...)
        // if the toString method is defined, call it
        final FuncInfo callSig = new FuncInfo(); // no args
        if (declaresMember("toString")) {
            final Entry[] entries = lookup("toString", callSig, new Object[0]);
            if (entries.length != 1) {
                ScigolTreeParser
                        .semanticError("call of toString() is ambiguous or incompatible");
            }
            final Func f = (Func) Class.getMemberValue(entries[0], instance);

            s = (String) f.call(instance, new Object[0]);

        } else {

            final Entry[] instanceMembers = getAllInstanceEntries(null);
            s = "class {\n";
            for (final Entry entry : instanceMembers) {
                Debug.Assert(entry != null);
                // !!! currently inherited CLI members can't be accessed, use
                // "?" as the value
                Object value = (entry.index >= 0) ? Class.getMemberValue(entry,
                        instance) : "?";
                if (!(entry.type.isFunc() && ((value.equals("?")) || (((Func) value)
                        .getValue() == null)))) { // skip null funcs for now!!!
                    s += "  ";
                    s += TypeSpec.operatorSourceName(entry.name);
                    s += ":" + entry.type;
                    if (value != null) {
                        if (value instanceof String) {
                            value = "\"" + value + "\"";
                        }
                        s += " = " + value;
                    } else {
                        s += " = null";
                    }
                    s += "\n";
                }
            }
            s += "}";
        }

        return s;
    }

    // create a new instance of Class compatible with this info, initializes
    // initialized members, and calls a constructor if one matching args is
    // found (returns null if not)
    public scigol.Class instantiateClass(final FuncInfo ctorCallSig,
            ArrayList args, final ScigolTreeParser treeParser) {
        // if no constructor call signature supplied,
        // get types of args & make a FuncInfo for the constructor call
        // signature
        if (args == null) {
            args = new ArrayList();
        }
        FuncInfo callSig = ctorCallSig;
        if (callSig == null) {
            final TypeSpec[] paramTypes = new TypeSpec[args.size()];
            for (int a = 0; a < args.size(); a++) {
                paramTypes[a] = TypeSpec.typeOf(args.get(a));
            }
            callSig = new FuncInfo(paramTypes, new TypeSpec(this)); // call
                                                                    // signature
        }

        if (_isInterface || _isAbstract) {
            ScigolTreeParser.semanticError(callSig.getDefinitionLocation(),
                    "abstract class or interface '" + toString()
                            + "' cannot be instantiated");
        }

        if (!complete) {
            ScigolTreeParser.semanticError(callSig.getDefinitionLocation(),
                    "can't instantiate incomplete type '" + toString() + "'");
        }

        if (_classType == ClassType.Local) {

            // !!! consider instantiating the class with all null member values
            // first, then
            // evaluating each member initializer in the class scope with 'this'
            // set to the
            // new instance, so that initializer can use this like constructors
            // (and access
            // other instance members. Of course the evaluations will then
            // depend on the
            // member initialization order - that is OK though.)

            // instantiate values for each instance member
            final Entry[] instanceMembers = getAllInstanceEntries(null);
            final ArrayList memberValues = new ArrayList();
            for (final Entry entry : instanceMembers) {

                if (!entry.isProperty()) { // properties don't take storage,
                                           // they are just syntaxic sugar for
                                           // the accessor funcs

                    final boolean staticOrConst = entry.isStatic()
                            || entry.isConst();

                    // if member is static or const, the initializer has already
                    // been executed
                    if (staticOrConst) {
                        memberValues.add(entry.getStaticValue());
                    } else { // an instance member

                        // if an initializer was supplied, we need to executeut
                        // now; if not, construct a default
                        // value for the type
                        if (entry.initializer != null) { // initializer supplied
                            final AST initializerExprAST = entry.initializer;
                            // execute in the scope of the class declaration (so
                            // that func literals have the class
                            // as their outer scope)
                            final Scope savedScope = treeParser.scope;
                            treeParser.scope = new ClassScope(
                                    new TypeSpec(this));
                            Value initializer = null;
                            try {
                                initializer = treeParser
                                        .expr(initializerExprAST);
                            } catch (final RecognitionException e) {
                                ScigolTreeParser
                                        .semanticError("error instantiating class '"
                                                + this + " - " + e.getMessage());
                            }
                            treeParser.scope = savedScope;

                            // check the type of the initializer is compatible
                            // with the member type (and convert if necessary)
                            final TypeSpec initializerType = TypeSpec
                                    .typeOf(initializer);
                            if (TypeManager.existsImplicitConversion(
                                    initializerType, entry.type, initializer)) {
                                initializer = TypeManager
                                        .performImplicitConversion(
                                                initializerType, entry.type,
                                                initializer);
                            } else {
                                ScigolTreeParser
                                        .semanticError("initializer value for member '"
                                                + entry.name
                                                + ":"
                                                + entry.type
                                                + "' of class '"
                                                + this
                                                + "' has incompatible type '"
                                                + initializerType + "'");
                            }

                            memberValues.add(initializer.getValue());
                        } else { // no initializer, construct default (unless a
                                 // default already supplied - like the literal
                                 // func member special case)
                            if (entry.getStaticValue() == null) {
                                memberValues.add(entry.type.constructValue(
                                        null, null, treeParser).getValue());
                            } else {
                                memberValues.add(entry.getStaticValue());
                            }
                        }
                    }

                }

            } // for

            final scigol.Class newClass = new scigol.Class(this, memberValues);

            // look for a suitable constructor to call

            // convert args to an array
            Object[] aargs = new Object[args.size()];
            for (int a = 0; a < args.size(); a++) {
                aargs[a] = args.get(a);
            }

            // !!! ??? should constructors be inherited?? we'll look in
            // subclasses for now...
            final Entry[] ctors = lookup(".ctor", callSig, aargs);
            if (ctors.length == 0) {
                ScigolTreeParser
                        .semanticError(
                                callSig.getDefinitionLocation(),
                                "no contructor for type '"
                                        + this
                                        + "' with compatible parameter types for call signature "
                                        + callSig + " found");
            }
            if (ctors.length != 1) {
                ScigolTreeParser.semanticError(callSig.getDefinitionLocation(),
                        "contructor for type '" + this
                                + "' is ambiguous for call signature "
                                + callSig);
            }

            final Entry ctorEntry = ctors[0];

            if (ctorEntry.getStaticValue() != null) {
                Debug.Assert(ctorEntry.getStaticValue() instanceof Func,
                        ".ctor isn't a Func!");
            }

            // Func value is in Entry.value as constructors are 'static'
            final Func ctor = (ctorEntry.getStaticValue() != null) ? ((Func) ctorEntry
                    .getStaticValue()) : null;

            // convert args to the types expected
            if (ctor != null) {
                aargs = ctor.getInfo().convertParameters(callSig, aargs, false);
            }

            // now call it
            if (ctor != null) {
                ctor.call(newClass, aargs);
            }

            return newClass;
        } else { // external

            Object[] aargs = new Object[args.size()];
            for (int a = 0; a < args.size(); a++) {
                aargs[a] = args.get(a);
            }

            final Entry[] ctors = lookup(".ctor", callSig, aargs);

            if (ctors.length == 0) {
                ScigolTreeParser
                        .semanticError(
                                callSig.getDefinitionLocation(),
                                "no contructor for type '"
                                        + ((java.lang.Class) _sysType)
                                                .getName()
                                        + "' with compatible parameter types for call signature "
                                        + callSig + " found");
            }
            if (ctors.length != 1) {
                ScigolTreeParser.semanticError(
                        callSig.getDefinitionLocation(),
                        "contructor for type '"
                                + ((java.lang.Class) _sysType).getName()
                                + "' is ambiguous for call signature "
                                + callSig);
            }

            final Entry ctorEntry = ctors[0];

            if (ctorEntry.getStaticValue() != null) {
                Debug.Assert(ctorEntry.getStaticValue() instanceof Func,
                        ".ctor isn't a Func!");
            }

            // Func value is in Entry.value as constructors are 'static'
            final Func ctor = (ctorEntry.getStaticValue() != null) ? ((Func) ctorEntry
                    .getStaticValue()) : null;

            // convert args to the types expected
            if (ctor != null) {
                aargs = ctor.getInfo().convertParameters(callSig, aargs, true); // external
            }

            // now call it
            Object obj = null;
            if (ctor != null) {
                obj = ctor.call(null, aargs);
            }

            if (obj == null) {
                ScigolTreeParser.semanticError(callSig.getDefinitionLocation(),
                        "construction failed");
            }

            return new Class(this, obj);

        }

    }

    // returns true if this class is a subclass of, or implements, the
    // class/interface super
    public boolean isA(final ClassInfo superClass) {
        if (this == superClass) {
            return true; // quick short-circuit test
        }
        if (superClass.isBuiltinObject()) {
            return true; // everything isA object
        }

        if (isExternal()) {
            if (superClass.isExternal()) { // easy
                // return
                // ((java.lang.Class)_sysType).isInstance(superClass.getSysType());
                return ((java.lang.Class) superClass.getSysType())
                        .isAssignableFrom((java.lang.Class) _sysType);
            }

            return false; // Java types can't (currently) inherit scigol types

        } else {

            if (superClass.isExternal()) {
                return false;
            }

            if (isInterface() && superClass.isClass()) {
                return false;
            }

            if (isClass()) {

                if (superClass.isClass()) {

                    // now go up our inheritance chain until we find super, or
                    // come to the top
                    TypeSpec t = _superClass;
                    while (t != null) {

                        // if we've reached the top, we didn't see super
                        if (t.isBuiltinObject()) {
                            return false;
                        }

                        if (t.getClassInfo().equals(superClass)) {
                            return true; // found super in our inheritance
                        }

                        t = t.getClassInfo()._superClass;
                    }
                    return false;
                }

                Debug.WriteLine("Warning: isA() unimplemented");
                return false;
            } else { // we are an interface

                if (superClass.isClass()) {
                    return false; // interfaces can't inherit classes (except
                                  // object, which we tested for above)
                }

                // !!!
                Debug.WriteLine("Warning: interface.isA() unimplemented");
                return true;

            }

        }

    }

    /*
     * protected static Entry entryFromMemberInfo(MemberInfo minfo, ClassInfo
     * declaringClass) { Debug.Depricated(
     * "entryFromMemberInfo - use externalGetDeclaredEntries for correct property handling"
     * );
     * 
     * Debug.Assert(declaringClass.isExternal(),
     * "MemberInfo's come only from external CLI classes"); ClassScope
     * classScope = new ClassScope(new TypeSpec(declaringClass));
     * 
     * if (minfo instanceof MethodBase) { // a method
     * 
     * MethodBase mb = minfo as MethodBase; String name = mb.Name; String
     * CLIName = null; TypeSpec.Modifiers modifiers =
     * TypeSpec.modifiersFromMethodAttributes(mb.Attributes); modifiers |=
     * TypeSpec.Modifiers.Const; // CLI class methods can't be assigned if
     * (TypeSpec.isCLIOperator(name)) { if ((name == "op_Implicit") || (name ==
     * "ToString")) modifiers |= TypeSpec.Modifiers.Implicit; CLIName = name;
     * name = TypeSpec.operatorSourceName(CLIName); }
     * 
     * Entry.Flags flags = Entry.Flags.Method; if (mb.IsSpecialName &&
     * (name.StartsWith("get_") || name.StartsWith("set_"))) { flags |=
     * Entry.Flags.Accessor; }
     * 
     * TypeSpec type = new TypeSpec(mb); Func func = new
     * Func(declaringClass.sysType, mb);
     * 
     * return new Entry(name, CLIName, type, func, null, modifiers, flags, -2,
     * classScope); } else if (minfo instanceof FieldInfo) { // a field
     * 
     * FieldInfo fi = minfo as FieldInfo; String fname = fi.Name; TypeSpec type
     * = new TypeSpec(fi.FieldType); TypeSpec.Modifiers modifiers =
     * TypeSpec.modifiersFromFieldAttributes(fi.Attributes);
     * 
     * return new Entry(fname, type, null, null, modifiers, Entry.Flags.Field,
     * -2, classScope); } else if (minfo instanceof PropertyInfo) { // a
     * property
     * 
     * PropertyInfo pi = minfo as PropertyInfo;
     * 
     * MethodInfo getterInfo = pi.GetGetMethod(); MethodInfo setterInfo =
     * pi.GetSetMethod();
     * 
     * //if (getterInfo != null)
     * Debug.WriteLine("has getter - returns:"+getterInfo.ReturnType); //if
     * (setterInfo != null)
     * Debug.WriteLine("has setter - takes (#"+setterInfo.GetParameters
     * ().Length+"):"+setterInfo.GetParameters()[0].ParameterType);
     * 
     * //!! only handle simple properties for now (where setter takes one arg)
     * if ((setterInfo != null) && (getterInfo != null) &&
     * (setterInfo.GetParameters().Length==1))
     * Debug.Assert(getterInfo.ReturnType
     * .equals(setterInfo.GetParameters()[0].ParameterType),
     * "getter returns different type than that which setter takes");
     * 
     * if ((setterInfo != null) || (getterInfo != null)) {
     * 
     * Entry.Flags flags = Entry.Flags.Property; if (setterInfo != null) flags
     * |= Entry.Flags.HasSetter; if (getterInfo != null) flags |=
     * Entry.Flags.HasGetter;
     * 
     * TypeSpec type = new TypeSpec((getterInfo !=
     * null)?getterInfo.ReturnType:setterInfo.GetParameters()[0].ParameterType);
     * TypeSpec.Modifiers modifiers =
     * TypeSpec.modifiersFromMethodAttributes((getterInfo
     * !=null)?getterInfo.Attributes:setterInfo.Attributes);
     * 
     * String name = pi.Name; if (name == "Item") name = "operator()";
     * 
     * return new Entry(name, type, null, null, modifiers, flags, -2,
     * classScope); } } else if (minfo instanceof Type) { // a type field // CLI
     * subtypes are just like fields that happen to be of type 'type' and are
     * const //!!! set correct modifiers here return new Entry(minfo.Name, new
     * TypeSpec(TypeSpec.typeType), new TypeSpec(minfo as Type), null,
     * TypeSpec.Modifiers
     * .Public|TypeSpec.Modifiers.Const,Entry.Flags.Field,-2,classScope); } else
     * Debug
     * .Assert(false,"unhandled MemberInfo subclass:"+minfo.GetType()+"="+minfo
     * +" of type:"+minfo.DeclaringType);
     * 
     * return null; }
     */

    public boolean isAbstract() {
        return _isInterface || _isAbstract;
    }

    // returns true if this is the CLI System.Object class (i.e. the builtin
    // 'object')
    public boolean isBuiltinObject() {
        return (_classType == ClassType.External)
                && (_sysType.equals(TypeSpec.objectType));
    };

    // if not an interface, a class
    public boolean isClass() {
        return !_isInterface;
    }

    // returns true if this is a CLI type
    public boolean isExternal() {
        return (_classType == ClassType.External);
    }

    public boolean isInterface() {
        return _isInterface;
    }

    // does this class represent the external Java TypeSpec class?
    public boolean isTypeSpec() {
        if (_classType == ClassType.External) {
            return (_sysType.equals(TypeSpec.class));
        }
        return false;
    }

    // gets candiate members of this class. For fields, only 0 or 1 Entry is
    // returned,
    // for methods, overload resolution is performed and 0 Entries are returned
    // if there is
    // no match, 1 if the match it unique, or >1 is it is ambiguous
    // Overriding & hiding are taken into consideration and abstract methods are
    // not returned.
    // If callSig is null, all applicable methods are returned
    public Entry[] lookup(final String name, final FuncInfo callSig,
            final Object[] args) {
        // working our way back up the heirarchy, look for first occurance of
        // name
        // (in the case callSig non-null, keep going up to collect all relevant
        // methods)
        final ArrayList candidates = new ArrayList(); // list of relevant
                                                      // entries so far
        ClassInfo c = this;
        while (c != null) {
            final ArrayList currEntries = Entry.toArrayList(c
                    .getDeclaredEntries(name)); // entries in current class
                                                // being considered in the line
            if ((currEntries.size() == 1)
                    && !((Entry) currEntries.get(0)).type.isFunc()) {
                return Entry.toArray(currEntries);
            }

            // if currEntries contain a func with the same sig as one with the
            // same name
            // already in candidates, it must be overriden or hidden by what we
            // have in
            // candidates already (since we're working backward up the
            // heirarchy) - so skip it
            // Also, skip abstract methods
            final ArrayList newCurrEntries = (ArrayList) currEntries.clone(); // can't
                                                                              // remove
                                                                              // while
                                                                              // enumerating,
                                                                              // so
                                                                              // remove
                                                                              // from
                                                                              // a
                                                                              // copy
                                                                              // instead

            for (final Object o : currEntries) {
                final Entry ce = (Entry) o;

                if (ce.isAbstract()) {
                    newCurrEntries.remove(ce);
                } else { // non-abstract

                    final boolean ceIsFunc = ce.type.isFunc();
                    final FuncInfo ceFuncInfo = ceIsFunc ? ce.type
                            .getFuncInfo() : null;

                    for (final Object o2 : candidates) {
                        final Entry ee = (Entry) o2;
                        if ((ce.name.equals(ee.name))
                                && (!ceIsFunc || (ceFuncInfo
                                        .equalsParams(ee.type.getFuncInfo())))) {
                            newCurrEntries.remove(ce);
                        }
                    }
                }

            }

            // add remaining current Entries to list
            for (final Object e : newCurrEntries) {
                candidates.add(e);
            }

            // up to base/super class (if any)
            if (c._superClass != null) {
                c = c._superClass.getClassInfo();
            } else {
                c = null;
            }

        } // while

        // resolve potential overloading via callSig/args
        if (callSig == null) {
            return Entry.toArray(candidates);
        } else {
            return TypeManager.resolveOverload(Entry.toArray(candidates),
                    callSig, args);
        }
    }

    public String nonRecursiveToString() {
        if (_classType == ClassType.Local) {
            String s = _isInterface ? "interface {\n" : "class {\n";
            for (final Object o : _members) {
                final Entry entry = (Entry) o;
                if (!entry.isAccessor()) {
                    s += entryToString(entry);
                }
            }
            s += "}";
            return s;
        } else {
            return _sysType.toString();
        }
    }

    public void setDefinitionLocation(final Location value) {
        _location = value;
    }

    public void setIdentityHint(final String hint) {
        identityHint = hint;
    }

    public void setModifiers(final EnumSet<TypeSpec.Modifier> modifiers) {
        _modifiers = modifiers;

        // check for valid modifiers

        // interfaces can't be marked abstract (they are abstract by definition)
        if (_modifiers.contains(TypeSpec.Modifier.Abstract) && _isInterface) {
            ScigolTreeParser
                    .semanticError("an interface cannot have the 'abstract' modifier - it is abstract by definition");
        }

        if (_modifiers.contains(TypeSpec.Modifier.Static)) {
            ScigolTreeParser
                    .semanticError("an interface or class cannot have the 'static' modifier (perhaps you meant the 'type' variable to be static?)");
        }
    }

    public String toString() {
        if (_classType == ClassType.Local) {
            String s = "";

            if (_location.isKnown()) {
                s = "[" + _location;
                if (identityHint != null) {
                    s += ";" + identityHint;
                }
                s += "]";
            } else if (identityHint != null) {
                s += "[" + identityHint + "]";
            }

            if (toStringArray.size() == 0) {
                s = _isInterface ? "interface" + s : "class" + s;
            } else {
                if (!toStringArray.contains(this)) {
                    s += toStringFull();
                } else {
                    s = "self";
                }
            }
            return s;
        } else {
            if (_sysType instanceof java.lang.Class) {

                // !!! also, might want to remove the namespace from the
                // fully-qualified type name if it is in the current 'using
                // namespace scope' if it makes sense

                final java.lang.Class sysClass = (java.lang.Class) _sysType;
                if (!sysClass.isArray()) {
                    String s = sysClass.toString();
                    if (s.startsWith("class ")) {
                        s = s.substring(6, s.length());
                    } else if (s.startsWith("interface ")) {
                        s = s.substring(10, s.length());
                    }
                    return s;
                } else {
                    return new TypeSpec(sysClass.getComponentType()).typeName()
                            + "[]"; // special case for java arrays
                }

            } else {
                return _sysType.toString();
            }
        }
    }

    public String toStringFull() {
        if (toStringArray.contains(this)) {
            return "self";
        }

        toStringArray.add(this);
        final String s = (_classType == ClassType.Local) ? nonRecursiveToString()
                : externToStringFull();
        toStringArray.remove(this);
        return s;
    }

}
