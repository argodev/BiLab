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

// import antlr.collections.AST;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

// / internal holder for a value of a class type
public class Class {

    protected enum ClassType {
        Local, External
    }

    // locate the external Method corresponding to the entry e
    // (taking into consideration covariant property types)
    protected static Method findExternalPropertyAccessor(
            final java.lang.Class declaringClass, final String accessorName,
            final Entry e, final FuncInfo callSig, final Object[] args) {
        // get Method from name & signature
        final boolean getAccessor = accessorName.equals("get");

        final Entry.EntryPair ep = (Entry.EntryPair) e.getStaticValue();
        Entry accessorEntry = null;
        if (getAccessor) {
            if (ep.getter == null) {
                return null; // property has no getter
            }
            accessorEntry = ep.getter;
        } else {
            if (ep.setter == null) {
                return null; // property has no setter
            }
            accessorEntry = ep.setter;
        }

        final String accessorMethodName = accessorEntry.name;
        final java.lang.Class[] argTypes = accessorEntry.type.getFuncInfo()
                .getExternParamTypes();
        Debug.WL("static value's type of property accessor entry is:"
                + accessorEntry.getStaticValue().getClass());
        try {
            Debug.Write("looking for accessor " + accessorMethodName
                    + " of class " + declaringClass + " with arg types :");
            for (int i = 0; i < argTypes.length; i++) {
                Debug.Write("arg" + i + ":" + argTypes[i]);
            }
            Debug.WL("");
            return declaringClass.getDeclaredMethod(accessorMethodName,
                    argTypes);
        } catch (final NoSuchMethodException ex) {
            Debug.WL(" not found");
            return null;
        }
    }

    // helpers for calling property accessors

    protected static Object getExternalProperty(final Entry e,
            final FuncInfo callSig, final Object[] args, final Object sysValue) {
        // get the type from the instance, or if static get the entry's
        // declaring type
        java.lang.Class type = (sysValue != null) ? sysValue.getClass() : null;
        if (type == null) { // static
            Debug.Assert(e.scope instanceof ClassScope,
                    "property isn't in a class!");
            type = (java.lang.Class) ((ClassScope) e.scope).getClassType()
                    .getSysType();
        }

        final Entry.EntryPair ep = (Entry.EntryPair) e.getStaticValue();
        if (ep.getter == null) {
            ScigolTreeParser.semanticError("the value of property '" + e.name
                    + "' of class '" + (new ClassInfo(type))
                    + "' cannot be read (no get accessor)");
        }

        final Func getter = (Func) ep.getter.getStaticValue();

        final Object[] convertedArgs = getter.getInfo().convertParameters(
                callSig, args, true);

        Object getterRet = getter.call(sysValue, convertedArgs);

        // now check that the getter returned the appropriate type
        final TypeSpec getterRetType = TypeSpec.typeOf(getterRet);
        if (!TypeManager.existsImplicitConversion(getterRetType, e.type,
                new Value(getterRet))) {
            ScigolTreeParser.semanticError("the 'get' accessor for property '"
                    + e.name + "' of class " + (new ClassInfo(type))
                    + "' should evaluate to the property type '" + e.type
                    + "', not type '" + getterRetType + "'");
        }

        getterRet = TypeManager.performImplicitConversion(getterRetType,
                e.type, new Value(getterRet)).getValue();

        return getterRet;
    }

    protected static Object getLocalProperty(final Entry e,
            final FuncInfo callSig, final Object[] args,
            final scigol.Class instance) {
        Debug.Assert((instance != null) || (e.isStatic()),
                "instance required for non-static/const properties");
        final ClassScope classScope = (ClassScope) e.scope;

        if (!e.hasGetter()) {
            ScigolTreeParser.semanticError("the value of property '" + e.name
                    + "' of class " + ((ClassScope) e.scope).getClassType()
                    + "' cannot be read (no get accessor)");
        }

        final String accessorName = FuncInfo.accessorName(e.name, true);
        final Entry accessorEntry = classScope.getClassType().getClassInfo()
                .getDeclaredEntries(accessorName)[0];

        final Func accessor = (Func) getMemberValue(accessorEntry, instance);

        final Object[] convertedArgs = accessor.getInfo().convertParameters(
                callSig, args, false);

        Object getterRet = accessor.call(instance, convertedArgs);

        // now check that the getter returned the appropriate type
        final TypeSpec getterRetType = TypeSpec.typeOf(getterRet);
        if (!TypeManager.existsImplicitConversion(getterRetType, e.type,
                new Value(getterRet))) {
            ScigolTreeParser.semanticError("the 'get' accessor for property '"
                    + e.name + "' of class "
                    + ((ClassScope) e.scope).getClassType()
                    + "' should evaluate to the property type '" + e.type
                    + "', not type '" + getterRetType + "'");
        }

        getterRet = TypeManager.performImplicitConversion(getterRetType,
                e.type, new Value(getterRet)).getValue();

        return getterRet;
    }

    public static Object getMemberValue(final Entry e, Object instance) {
        Debug.Assert(e.isClassMember(), "e must be a class member");
        Debug.Assert(!e.isAbstract(), "e is an abstract member!");
        Debug.Assert((instance != null) || e.isStatic(), "instance required");

        if (e.isProperty()) {
            return getPropertyValue(e, new FuncInfo(), null, instance);
        }

        final boolean isExternal = ((ClassScope) e.scope).getClassType()
                .getClassInfo().isExternal();
        final boolean isStatic = e.isStatic();

        if (!isExternal) {

            if (isStatic) { // static
                return e.getStaticValue();
            } else {
                Debug.Assert(
                        e.index >= 0,
                        "inherited Java members can't currently be accessed in local classes (unimplemented)");
                Debug.Assert(instance instanceof Class,
                        "Class instance required");
                return ((scigol.Class) instance)._members.get(e.index);
            }
        } else { // external

            if (instance instanceof scigol.Class) {
                instance = ((scigol.Class) instance).getSysValue();
            }

            final TypeSpec declaringClassType = ((ClassScope) e.scope)
                    .getClassType();
            final java.lang.Class sysClass = (java.lang.Class) declaringClassType
                    .getSysType();

            if (e.isField()) { // field

                if (e.type.isType()) {
                    Debug.Unimplemented("nested types");
                }

                // get Field from name
                try {
                    final Field field = sysClass.getField(e.name);

                    return field.get(instance);

                } catch (final java.lang.NoSuchFieldException ex) {
                    Debug.Assert(false, "unable to find field " + e.name
                            + " of extern type '" + sysClass.getName() + "'");
                } catch (final java.lang.IllegalAccessException ex) {
                    ScigolTreeParser.semanticError("illegal access for field '"
                            + e.name + "' of '" + sysClass.getName() + "' - "
                            + ex.getMessage());
                }
            } else if (e.isMethod()) { // method
                // extern methods are like consts, the value is always in the
                // entry staticValue

                final Func func = (Func) e.getStaticValue();
                Debug.Assert(func != null,
                        "entry for external method has no Func value!");

                // if (!func.isConstructor())
                // ScigolTreeParser.semanticError("can't directly access or call the constructor of class '"+_sysValue.GetType().FullName+"'");

                return func;
            } else {
                Debug.Unimplemented("unknown member type of type '"
                        + sysClass.getName() + "'");
            }
            return null;
        }
    }

    public static Object getPropertyValue(final Entry e,
            final FuncInfo callSig, Object[] args, Object instance) {
        Debug.Assert(e != null);
        Debug.Assert(e.isClassMember(), "e must be a class member");
        Debug.Assert(!e.isAbstract(), "e is an abstract member!");
        Debug.Assert(e.isProperty(), "e must be a property");
        if (args == null) {
            args = new Object[0];
        }

        final boolean isExternal = ((ClassScope) e.scope).getClassType()
                .getClassInfo().isExternal();
        // boolean isStatic = e.isStatic();

        if (!isExternal) {
            Debug.Assert(instance instanceof Class, "Class instance required");
            return getLocalProperty(e, callSig, args, (scigol.Class) instance);
        } else { // external
            if (instance instanceof scigol.Class) {
                instance = ((scigol.Class) instance).getSysValue();
            }
            return getExternalProperty(e, callSig, args, instance);
        }

    }

    protected static void setExternalProperty(final Entry e,
            final FuncInfo callSig, final Object[] args, final Object value,
            final Object sysValue) {
        // get the type from the instance, or if static get the entry's
        // declaring type
        java.lang.Class type = (sysValue != null) ? sysValue.getClass() : null;
        if (type == null) { // static
            Debug.Assert(e.scope instanceof ClassScope,
                    "property isn't in a class!");
            type = (java.lang.Class) ((ClassScope) e.scope).getClassType()
                    .getSysType();
        }

        final Entry.EntryPair ep = (Entry.EntryPair) e.getStaticValue();
        if (ep.setter == null) {
            ScigolTreeParser.semanticError("the value of property '" + e.name
                    + "' of class '" + (new ClassInfo(type))
                    + "' cannot be set (no set accessor)");
        }

        // add the 'value' arg to the callSig & args
        final FuncInfo setterCallSig = callSig.accessorSig("set", e.type);
        final Object[] setterArgs = new Object[args.length + 1];
        for (int i = 0; i < args.length; i++) {
            setterArgs[i] = args[i];
        }
        setterArgs[args.length] = value;

        final Func setter = (Func) ep.setter.getStaticValue();
        final Object[] convertedArgs = setter.getInfo().convertParameters(
                setterCallSig, setterArgs, true);

        setter.call(sysValue, convertedArgs);
    }

    protected static void setLocalProperty(final Entry e,
            final FuncInfo callSig, final Object[] args, final Object value,
            final scigol.Class instance) {
        Debug.Assert((instance != null) || (e.isStatic()),
                "instance required for non-static/const properties");
        final ClassScope classScope = (ClassScope) e.scope;

        if (!e.hasSetter()) {
            ScigolTreeParser.semanticError("the value of property '" + e.name
                    + "' of class " + ((ClassScope) e.scope).getClassType()
                    + "' cannot be set (no set accessor)");
        }

        final String accessorName = FuncInfo.accessorName(e.name, false);
        // !!! need to do the accessor func overload resolution here - don't
        // just choose [0]
        // !!! propably need some connection from a property Entry to it's
        // accessor Entries and vice-versa
        final Entry accessorEntry = classScope.getClassType().getClassInfo()
                .getDeclaredEntries(accessorName)[0];

        // add the 'value' arg to the callSig & args
        final FuncInfo setterCallSig = callSig.accessorSig("set", e.type);
        final Object[] setterArgs = new Object[args.length + 1];
        for (int i = 0; i < args.length; i++) {
            setterArgs[i] = args[i];
        }
        setterArgs[args.length] = value;

        final Func accessor = (Func) getMemberValue(accessorEntry, instance);

        final Object[] convertedArgs = accessor.getInfo().convertParameters(
                setterCallSig, setterArgs, false);

        accessor.call(instance, convertedArgs);
    }

    public static void setMemberValue(final Entry e, final Object value,
            Object instance) {
        Debug.Assert(e.isClassMember(), "e must be a class member");
        Debug.Assert(!e.isAbstract(), "e is an abstract member!");
        Debug.Assert((instance != null) || e.isStatic(), "instance required");

        if (e.isProperty()) {
            setPropertyValue(e, new FuncInfo(), null, value, instance);
        }

        final boolean isExternal = ((ClassScope) e.scope).getClassType()
                .getClassInfo().isExternal();
        final boolean isStatic = e.isStatic();

        if (!isExternal) {
            if (e.isStatic()) { // static
                e.setStaticValue(value);
            } else {
                Debug.Assert(
                        e.index >= 0,
                        "inherited Java members can't currently be accessed in local classes (unimplemented)");
                Debug.Assert(instance instanceof scigol.Class,
                        "Class instance required");
                ((scigol.Class) instance)._members.set(e.index, value);
            }
        } else { // external

            if (instance instanceof scigol.Class) {
                instance = ((scigol.Class) instance).getSysValue();
            }

            final TypeSpec declaringClassType = ((ClassScope) e.scope)
                    .getClassType();
            final java.lang.Class sysClass = (java.lang.Class) declaringClassType
                    .getSysType();

            if (e.isField()) { // field

                if (e.type.isType()) {
                    ScigolTreeParser
                            .semanticError("external classes have const nested type members, hence cannot be set");
                }

                // get Field from name
                try {
                    final Field field = sysClass.getField(e.name);

                    field.set(instance, value);

                } catch (final java.lang.NoSuchFieldException ex) {
                    Debug.Assert(false, "unable to find field " + e.name
                            + " of extern type '" + sysClass.getName() + "'");
                } catch (final java.lang.IllegalAccessException ex) {
                    ScigolTreeParser.semanticError("illegal access for field '"
                            + e.name + "' of '" + sysClass.getName() + "' - "
                            + ex.getMessage());
                }

            } else if (e.isMethod()) { // method
                ScigolTreeParser
                        .semanticError("external classes have const method members, hence cannot be set");
            } else {
                Debug.Unimplemented("unknown member type of type "
                        + sysClass.getName());
            }

        }
    }

    public static void setPropertyValue(final Entry e, final FuncInfo callSig,
            Object[] args, final Object value, Object instance) {
        Debug.Assert(e.isClassMember(), "e must be a class member");
        Debug.Assert(!e.isAbstract(), "e is an abstract member!");
        Debug.Assert(e.isProperty(), "e must be a property");
        if (args == null) {
            args = new Object[0];
        }

        final boolean isExternal = ((ClassScope) e.scope).getClassType()
                .getClassInfo().isExternal();
        // boolean isStatic = e.isStatic();

        if (!isExternal) {
            Debug.Assert(instance instanceof Class, "Class instance required");
            setLocalProperty(e, callSig, args, value, (scigol.Class) instance);
        } else { // external
            if (instance instanceof Class) {
                instance = ((scigol.Class) instance).getSysValue();
            }
            setExternalProperty(e, callSig, args, value, instance);
        }
    }

    protected Scope _scope; // Scope in which class defined

    protected ClassType _classType; // is the Class a Scigol one (Local) or a
                                    // general Java one (External)?

    // Local classes only
    protected ClassInfo _info;

    protected ArrayList _members; // values of members (in ClassInfo member
                                  // index order)

    // extern classes
    Object _sysValue;

    // / create Local type class
    public Class(final ClassInfo info, final ArrayList members) {
        _classType = ClassType.Local;
        _info = info;
        _members = members;
        for (final Object o : _members) {
            Debug.Assert(!(o instanceof Value), "members can't be Values");
        }
    }

    // / create External type class
    public Class(final ClassInfo info, final Object value) {
        Debug.Assert(!(value instanceof Value),
                "must be an Object (not a Value)");
        _classType = ClassType.External;
        _info = info;
        _sysValue = value;
        _scope = null;
    }

    public ClassInfo getInfo() {
        return _info;
    }

    public ArrayList getMemberValues() {
        return _members;
    }

    public Scope getOuterScope() {
        return _scope;
    }

    public Object getSysValue() {
        Debug.Assert(_classType == ClassType.External);
        return _sysValue;
    }

    public boolean isExternal() {
        return (_classType == ClassType.External);
    }

    @Override
    public String toString() {
        if (_classType == ClassType.External) {
            return _sysValue.toString();
        }

        return _info.instanceToString(this);
    }

}
