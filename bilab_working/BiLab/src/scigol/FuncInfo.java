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
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

// import antlr.collections.AST;

// / Hold information about a function type
// / (arg names & types, return type)
public class FuncInfo {
    public static String accessorName(final String propertyName,
            final boolean get) {
        String suffix = propertyName;
        if (propertyName.equals("operator()")) {
            suffix = "Item";
        }
        return get ? "get_" + suffix : "set_" + suffix;
    }

    public static String propertyName(final String accessorName) {
        String propName = accessorName.substring(4);
        if (propName.equals("Item")) {
            propName = "operator()";
        }
        return propName;
    }

    public static TypeSpec propertyTypeFromAccessor(final String accessorName,
            final FuncInfo accessorSig) {
        if (accessorName.equals("get")) {
            return accessorSig._returnType;
        } else if (accessorName.equals("set")) {
            return accessorSig._paramTypes[accessorSig.numArgs() - 1];
        } else {
            Debug.Assert(false, "invalid accessor name");
        }
        return null;
    }

    public static Object[] toArray(final ArrayList argList) {
        if (argList == null) {
            return new Object[0];
        }
        final Object[] a = new Object[argList.size()];
        for (int i = 0; i < argList.size(); i++) {
            a[i] = argList.get(i);
        }
        return a;
    }

    // convenience for args
    public static ArrayList toArrayList(final Object[] argArray) {
        if (argArray == null) {
            return new ArrayList();
        }
        final ArrayList l = new ArrayList(argArray.length);
        for (int i = 0; i < argArray.length; i++) {
            l.add(argArray[i]);
        }
        return l;
    }

    protected String[] _paramNames;

    protected TypeSpec[] _paramTypes;

    protected Object[] _paramDefaults;

    protected boolean[] _paramHasDefault;

    protected TypeSpec _returnType;

    protected Location _location = new Location();

    public FuncInfo() {
        // default to noarg, any return
        _paramNames = new String[0];
        _paramTypes = new TypeSpec[0];
        _paramDefaults = new Object[0];
        _paramHasDefault = new boolean[0];
        _returnType = TypeSpec.typeOf("any");
    }

    // construct from types of an argument list
    public FuncInfo(final ArrayList args) {
        if (args == null) {
            // default to noarg, any return
            _paramNames = new String[0];
            _paramTypes = new TypeSpec[0];
            _paramDefaults = new Object[0];
            _paramHasDefault = new boolean[0];
            _returnType = TypeSpec.typeOf("any");
        } else {
            final int n = args.size();
            _paramNames = new String[n];
            _paramTypes = new TypeSpec[n];
            _paramDefaults = new Object[n];
            _paramHasDefault = new boolean[n];
            _returnType = TypeSpec.typeOf("any");
            for (int a = 0; a < n; a++) {
                _paramNames[a] = null;
                _paramTypes[a] = null;
                if (args.get(a) instanceof Value) {
                    // if an arg is null and is an LValue, extract it's declared
                    // type
                    final Value v = (Value) args.get(a);
                    final boolean isNull = (v.getValue() == null)
                            || ((v.getValue() instanceof Any) && (((Any) v
                                    .getValue()).value == null));
                    if (isNull && v.isLValue()) {
                        _paramTypes[a] = v.getLValue().getSymbol().getType();
                    }
                    // if we have an Any value, try to deduce the type from the
                    // actual value within
                    else if (v.getValue() instanceof Any) { // if we have an Any
                        if (v.getValue() != null) {
                            _paramTypes[a] = TypeSpec.typeOf(((Any) v
                                    .getValue()).value);
                        }
                    }
                }
                if (_paramTypes[a] == null) {
                    _paramTypes[a] = TypeSpec.typeOf(args.get(a));
                }

                _paramDefaults[a] = null;
                _paramHasDefault[a] = false;
            }
        }

    }

    public FuncInfo(final FuncInfo fi) {
        _paramNames = fi._paramNames;
        _paramTypes = fi._paramTypes;
        _paramDefaults = fi._paramDefaults;
        _paramHasDefault = fi._paramHasDefault;
        _returnType = fi._returnType;
        _location = fi._location;
    }

    // construct from a method signature
    public FuncInfo(final Member member) {
        Debug.Assert(member != null, "no Member!");

        java.lang.Class[] parameters = null;

        if (member instanceof Method) {
            parameters = ((Method) member).getParameterTypes();
        } else {
            parameters = ((Constructor) member).getParameterTypes();
        }

        _paramNames = new String[parameters.length];
        _paramTypes = new TypeSpec[parameters.length];
        _paramDefaults = new Object[parameters.length];
        _paramHasDefault = new boolean[parameters.length];

        for (int p = 0; p < parameters.length; p++) {
            _paramTypes[p] = new TypeSpec(parameters[p]);
            _paramHasDefault[p] = false; // java doesn't support default
                                         // parameters
            _paramDefaults[p] = null;
            _paramNames[p] = "arg" + p; // !!! try to get the name from debug
                                        // info
            // or annotations
        }

        // get return type
        if (member instanceof Method) {
            final Type returnType = ((Method) member).getReturnType();
            if (returnType.equals(Void.TYPE)) {
                _returnType = TypeSpec.anyTypeSpec;
            } else {
                /*
                 * // if method has a CovariantReturn attribute, use its
                 * returnType as the func return type // (as covariant returns
                 * aren't currently supported in the CLI) TypeSpec
                 * covariantReturn = TypeSpec.covariantReturn(mb); if
                 * (covariantReturn != null) _returnType = covariantReturn; else
                 */
                _returnType = new TypeSpec(returnType);
            }
        } else if (member instanceof Constructor) {
            _returnType = new TypeSpec(
                    ((Constructor) member).getDeclaringClass());
        }

    }

    public FuncInfo(final String[] paramNames, final TypeSpec[] paramTypes,
            final Object[] paramDefaults, final boolean[] paramHasDefault,
            final TypeSpec returnType) {
        Debug.Assert(paramNames.length == paramTypes.length);
        Debug.Assert(paramNames.length == paramDefaults.length);
        Debug.Assert(paramNames.length == paramHasDefault.length);
        _paramNames = paramNames;
        _paramTypes = paramTypes;
        _paramDefaults = paramDefaults;
        _paramHasDefault = paramHasDefault;
        _returnType = returnType;
    }

    public FuncInfo(final TypeSpec[] paramTypes, final TypeSpec returnType) {
        final int numArgs = paramTypes.length;

        _paramNames = new String[numArgs]; // no param names
        _paramDefaults = new Object[numArgs];
        _paramHasDefault = new boolean[numArgs];
        for (int a = 0; a < numArgs; a++) {
            _paramNames[a] = null;
            _paramDefaults[a] = null;
            _paramHasDefault[a] = false;
        }

        _paramTypes = paramTypes;
        _returnType = returnType;
    }

    // make a property accessor signature from this property args and the
    // property type
    public FuncInfo accessorSig(final String accessorName,
            final TypeSpec propertyType) {
        FuncInfo fi = null;
        if (accessorName.equals("set")) { // add the propertyType as an extra
                                          // parameter named 'value'
            fi = new FuncInfo();
            final int numPropArgs = _paramTypes.length;

            final TypeSpec[] pTypes = new TypeSpec[numPropArgs + 1];
            for (int i = 0; i < numPropArgs; i++) {
                pTypes[i] = _paramTypes[i];
            }
            pTypes[numPropArgs] = propertyType;

            final String[] pNames = new String[numPropArgs + 1];
            for (int i = 0; i < numPropArgs; i++) {
                pNames[i] = _paramNames[i];
            }
            pNames[numPropArgs] = "value";

            final Object[] pDefaults = new Object[numPropArgs + 1];
            for (int i = 0; i < numPropArgs; i++) {
                pDefaults[i] = _paramDefaults[i];
            }
            pDefaults[numPropArgs] = null;

            final boolean[] pHasDefault = new boolean[numPropArgs + 1];
            for (int i = 0; i < numPropArgs; i++) {
                pHasDefault[i] = _paramHasDefault[i];
            }
            pHasDefault[numPropArgs] = false;

            fi._paramNames = pNames;
            fi._paramTypes = pTypes;
            fi._paramDefaults = pDefaults;
            fi._paramHasDefault = pHasDefault;
        } else if (accessorName.equals("get")) { // set the returnType to the
                                                 // propertyType
            fi = new FuncInfo(this);
            fi._returnType = propertyType;
        } else {
            Debug.Assert(false,
                    "invalid accessor kind name (should be 'get' or 'set'");
        }
        return fi;
    }

    // can this func be called using a call with signature callSig?
    // if callSig.returnType is any, then ignore return type compatibility
    // (NB: this doesn't consider implicit argument conversions, use
    // TypeManager.isMatchingFunc() for that)
    public boolean callCompatible(final FuncInfo callSig) {
        Debug.Assert(callSig != null);

        if (callSig.numArgs() < numRequiredArgs()) {
            return false; // not enough args
        }
        if (callSig.numArgs() > numArgs()) {
            return false; // too many args
        }

        // check types
        for (int a = 0; a < callSig.numArgs(); a++) {
            if (!_paramTypes[a].equals(callSig._paramTypes[a])) {
                return false;
            }
        }

        if (callSig.getReturnType() != null) {
            if (!callSig.getReturnType().equals(TypeSpec.typeOf("any"))) {
                if (!_returnType.equals(callSig.getReturnType())) {
                    return false;
                }
            }
        }

        return true;
    }

    // convenience version of above
    public Object[] convertParameters(final FuncInfo callSig,
            final ArrayList args, final boolean externCall) {
        final Object[] aargs = new Object[args.size()];
        for (int a = 0; a < aargs.length; a++) {
            aargs[a] = args.get(a);
        }
        return convertParameters(callSig, aargs, externCall);
    }

    // convert the args for a call with signature callSig that is compatible
    // with this
    // to exactly the types this func expects (including filling in defaults if
    // possible)
    // throws on arg type or number mismatch
    public Object[] convertParameters(final FuncInfo callSig, Object[] args,
            final boolean externCall) {
        if (args == null) {
            args = new Object[0];
        }

        Debug.Assert(callSig != null);
        Debug.Assert(callSig.numArgs() == args.length,
                "supplied arg count must match callSig");

        // if (externCall) //!!!
        // Debug.WriteLine("Warning: FuncInfo.convertParameters() - externCall being ignored (unimplemented)");

        if (numRequiredArgs() > callSig.numArgs()) {
            ScigolTreeParser.semanticError(callSig.getDefinitionLocation(),
                    "not enough arguments for function " + this
                            + " in call with signature " + callSig);
        }

        if (callSig.numArgs() > numArgs()) {
            ScigolTreeParser.semanticError(callSig.getDefinitionLocation(),
                    "too many arguments for function " + this
                            + " in call with signature " + callSig);
        }

        final Object[] convertedArgs = new Object[numArgs()];

        // for each formal param
        for (int a = 0; a < numArgs(); a++) {

            if (a < callSig.numArgs()) { // if param supplied
                Object arg = ((args[a] instanceof Value) ? (((Value) args[a])
                        .getValue()) : args[a]); // convert from Value if
                                                 // necessary

                TypeSpec argType = TypeSpec.typeOf(arg);
                if (!argType.isAny()) { // implicit conversion from any always
                                        // exists
                    // does an implicit conversion from the argType to the
                    // parameter type exist?
                    if (!TypeManager.existsImplicitConversion(argType,
                            _paramTypes[a], new Value(arg))) {
                        ScigolTreeParser
                                .semanticError(
                                        callSig.getDefinitionLocation(),
                                        "argument "
                                                + a
                                                + " ("
                                                + _paramNames[a]
                                                + ") to func with signature "
                                                + this
                                                + " is of type '"
                                                + argType
                                                + "' which is incompatible with parameter type '"
                                                + _paramTypes[a] + "'");
                    }
                } else {
                    arg = TypeSpec.unwrapAny(arg);
                    argType = TypeSpec.typeOf(arg);
                }

                // conversion
                final Value convertedArg = TypeManager
                        .performImplicitConversion(argType, _paramTypes[a],
                                new Value(arg));

                if (convertedArg == null) {
                    ScigolTreeParser
                            .semanticError(
                                    callSig.getDefinitionLocation(),
                                    "argument "
                                            + a
                                            + " ("
                                            + _paramNames[a]
                                            + ") to func with signature "
                                            + this
                                            + " is of type '"
                                            + argType
                                            + "' which cannot be converted into the required parameter type '"
                                            + _paramTypes[a] + "'");
                }

                convertedArgs[a] = convertedArg.getValue();
            } else { // not supplied
                // do we have a default value?
                if (!_paramHasDefault[a]) {
                    ScigolTreeParser
                            .semanticError(
                                    callSig.getDefinitionLocation(),
                                    "in call to function "
                                            + this
                                            + ", no argument was supplied for parameter "
                                            + _paramNames[a]
                                            + " (which has no default)");
                }

                // use default
                convertedArgs[a] = _paramDefaults[a];
            }

        } // for each param

        return convertedArgs;
    }

    // are func types equivelent? (same arg types & return type?)
    // NB: doens't consider param names or default values
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FuncInfo)) {
            return false;
        }

        final FuncInfo f = (FuncInfo) o;

        if (!equalsParams(f)) {
            return false;
        }

        if ((_returnType == null) && (f._returnType != null)) {
            return false;
        }
        if (_returnType != null) {
            if (!_returnType.equals(f._returnType)) {
                return false;
            }
        }

        return true;
    }

    // compare for equality, ignoring return types
    // NB: doens't consider param names or default values
    public boolean equalsParams(final FuncInfo f) {
        if (f == null) {
            return false;
        }
        if (f.numArgs() != numArgs()) {
            return false;
        }

        if (f.numRequiredArgs() != numRequiredArgs()) {
            return false;
        }

        for (int a = 0; a < numArgs(); a++) {
            if (!_paramTypes[a].equals(f._paramTypes[a])) {
                return false;
            }
        }
        return true;
    }

    public Location getDefinitionLocation() {
        if (_location != null) {
            return _location;
        } else {
            return new Location();
        }
    }

    public java.lang.Class[] getExternParamTypes() {
        final java.lang.Class[] eParamTypes = new java.lang.Class[_paramTypes.length];
        for (int i = 0; i < _paramTypes.length; i++) {
            final TypeSpec type = _paramTypes[i];
            if (type.isBuiltin() || type.isBuiltinClass()) {
                eParamTypes[i] = (java.lang.Class) type.getSysType();
            } else {
                if (type.isClassOrInterface()) {
                    final ClassInfo ci = type.getClassInfo();
                    if (ci.isExternal()) {
                        eParamTypes[i] = (java.lang.Class) ci.getSysType();
                    } else {
                        eParamTypes[i] = Class.class;
                    }
                } else {
                    eParamTypes[i] = Func.class;
                }
            }
        }
        return eParamTypes;
    }

    public Object[] getParamDefaults() {
        return _paramDefaults;
    }

    public boolean[] getParamHasDefault() {
        return _paramHasDefault;
    }

    public String[] getParamNames() {
        return _paramNames;
    }

    public TypeSpec[] getParamTypes() {
        return _paramTypes;
    }

    public TypeSpec getReturnType() {
        return _returnType;
    }

    // / is this a formal func type (i.e. including param names, etc.) or just a
    // signature type spec?
    public boolean isFormal() {
        if (_paramNames.length == 0) {
            return true; // equivelent for zero-param func
        }
        return (_paramNames[0] != null);
    }

    public int numArgs() {
        return _paramTypes.length;
    }

    public int numRequiredArgs() {
        int c = 0;
        for (final boolean paramHasDefault : _paramHasDefault) {
            if (!paramHasDefault) {
                c++;
            }
        }
        return c;
    }

    // make a property args from the this accessor sig and kind
    public FuncInfo propertySig(final String accessorName) {
        FuncInfo fi = null;
        if (accessorName.equals("set")) {

            // remove the last argument from the set accessor

            Debug.Assert(+_paramTypes.length > 0,
                    "a set accessor must have at least one argument (the value arg)");

            fi = new FuncInfo(this); // copy
            final int numPropArgs = _paramTypes.length - 1;

            final TypeSpec[] pTypes = new TypeSpec[numPropArgs];
            for (int i = 0; i < numPropArgs; i++) {
                pTypes[i] = _paramTypes[i];
            }

            final String[] pNames = new String[numPropArgs];
            for (int i = 0; i < numPropArgs; i++) {
                pNames[i] = _paramNames[i];
            }

            final Object[] pDefaults = new Object[numPropArgs];
            for (int i = 0; i < numPropArgs; i++) {
                pDefaults[i] = _paramDefaults[i];
            }

            final boolean[] pHasDefault = new boolean[numPropArgs];
            for (int i = 0; i < numPropArgs; i++) {
                pHasDefault[i] = _paramHasDefault[i];
            }

            fi._paramNames = pNames;
            fi._paramTypes = pTypes;
            fi._paramDefaults = pDefaults;
            fi._paramHasDefault = pHasDefault;
        } else if (accessorName.equals("get")) {
            // property args are the same as the get accessor args, no change
            fi = new FuncInfo(this); // copy
        } else {
            Debug.Assert(false,
                    "invalid accessor kind name (should be 'get' or 'set'");
        }
        return fi;
    }

    public void setDefinitionLocation(final Location value) {
        _location = value;
    }

    public void setReturnType(final TypeSpec value) {
        _returnType = value;
    }

    @Override
    public String toString() {
        return toStringArgs(null);
    }

    public String toStringArgs(final Object[] args) {
        boolean haveArgs = (args != null);
        if (haveArgs) {
            if (args.length < _paramNames.length) {
                haveArgs = false;
            }
        }
        String s = "func(";
        for (int i = 0; i < _paramNames.length; i++) {
            if (_paramNames[i] != null) {
                s += _paramNames[i] + ":";
            }
            s += _paramTypes[i].typeName();
            if (!haveArgs) {
                if (_paramHasDefault[i]) {
                    if (_paramDefaults[i] != null) {
                        String str = null;
                        try {
                            str = _paramDefaults[i].toString();
                        } catch (final java.lang.Exception e) {
                        }
                        if (str != null) {
                            s += " =" + str;
                        } else {
                            s += " =?";
                        }
                    } else {
                        s += " =null";
                    }
                }
            } else {
                final Object a = args[i];
                if (a != null) {
                    String str = null;
                    try {
                        str = a.toString();
                    } catch (final java.lang.Exception e) {
                    }
                    if (str != null) {
                        s += " =<" + str + ">";
                    } else {
                        s += " =<?>";
                    }
                } else {
                    s += " =<null>";
                }
            }
            if (i != _paramNames.length - 1) {
                s += ", ";
            }
        }
        if (_returnType != null) {
            s += " -> " + _returnType.typeName();
        }
        s += ")";

        return s;
    }

    public String toStringWithLocation() {
        String s = toStringArgs(null);
        if (_location.isKnown()) {
            s += "[" + _location + "]";
        }
        return s;
    }

}
