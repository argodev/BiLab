package scigol;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;

// !!! consider either removing TypeSpec in favor of using Type[C#]
// for type and TypeManager for the util functions
// (or maybe even deriving from Type[C#] and adding extra info?)
// In that case, as Type[C#] cannot represent functions, we'd have
// to use MemberInfo)[C#] (MethodInfo(C#) etc.) for that

// / represents the type of a Scigol value
public class TypeSpec {
    // what kind of type
    public enum Kind {
        Builtin, Class, Function
    }

    public enum Modifier {
        Public, Protected, Private, Final, Static, Override, Abstract, Const, Implicit, New
    }

    public enum Operator {
        Multiply("operator*", "op_Multiply"), Division("operator/",
                "op_Division"), Modulus("operator%", "op_Modulus"), Addition(
                "operator+", "op_Addition"), Subtraction("operator-",
                "op_Subtraction"), LessThan("operator<", "op_LessThan"), GreaterThan(
                "operator>", "op_GreaterThan"), LessThanOrEqual("operator<=",
                "op_LessThanOrEqual"), GreaterThanOrEqual("operator>=",
                "op_GreaterThanOrEqual"), Equality("operator==", "op_Equality"), Inequality(
                "operator!=", "op_Inequality"), UnaryPlus("operator+",
                "op_UnaryPlus", false), UnaryNegation("operator-",
                "op_UnaryNegation", false), Increment("operator++",
                "op_Increment"), Decrement("operator--", "op_Decrement"),
        // LeftShift, RightShift,
        // BitwiseAnd,
        // ExclusiveOr,
        // BitwiseOr,
        // LogicalAnd,
        // LogicalOr,
        // LogicalNot, OnesComplement,
        Norm("operator||", "op_Norm", false), Cardinality("operator#",
                "op_Card", false), Power("operator^", "op_Power"), Prime(
                "operator'", "op_Prime", false), Conversion("operator->", "",
                false);

        public String srcName;

        public String javaName;

        boolean binary;

        Operator(final String srcName, final String javaName) {
            this.srcName = srcName;
            this.javaName = javaName;
            this.binary = true;
        }

        Operator(final String srcName, final String javaName,
                final boolean binary) {
            this.srcName = srcName;
            this.javaName = javaName;
            this.binary = binary;
        }
    }

    // construct from a Method or Constructor (i.e. a func)

    public static java.lang.Class charType = Character.class;

    public static java.lang.Class stringType = String.class;

    public static java.lang.Class boolType = Boolean.class;

    public static java.lang.Class intType = Integer.class;

    public static java.lang.Class rangeType = Range.class;

    public static java.lang.Class dintType = Long.class;

    public static java.lang.Class realType = Double.class;

    public static java.lang.Class srealType = Float.class;

    public static java.lang.Class typeType = TypeSpec.class;

    public static java.lang.Class vectorType = scigol.Vector.class;

    public static java.lang.Class matrixType = scigol.Matrix.class;

    public static java.lang.Class listType = scigol.List.class;

    public static java.lang.Class mapType = scigol.Map.class;
    
    // shortcuts for system types corresponding to builtin types
    public static java.lang.Class objectType = Object.class;

    public static java.lang.Class anyType = Any.class;

    public static java.lang.Class numType = Num.class;;

    public static java.lang.Class byteType = Byte.class;
    
    public static TypeSpec objectTypeSpec = new TypeSpec(objectType);

    public static TypeSpec anyTypeSpec = new TypeSpec(anyType);

    public static TypeSpec numTypeSpec = new TypeSpec(numType);

    public static TypeSpec byteTypeSpec = new TypeSpec(byteType);

    public static TypeSpec charTypeSpec = new TypeSpec(charType);

    public static TypeSpec stringTypeSpec = new TypeSpec(stringType);

    public static TypeSpec boolTypeSpec = new TypeSpec(boolType);
    public static TypeSpec intTypeSpec = new TypeSpec(intType);
    public static TypeSpec rangeTypeSpec = new TypeSpec(rangeType);
    public static TypeSpec dintTypeSpec = new TypeSpec(dintType);
    public static TypeSpec realTypeSpec = new TypeSpec(realType);
    public static TypeSpec srealTypeSpec = new TypeSpec(srealType);
    public static TypeSpec typeTypeSpec = new TypeSpec(typeType);
    public static TypeSpec vectorTypeSpec = new TypeSpec(vectorType);
    public static TypeSpec matrixTypeSpec = new TypeSpec(matrixType);
    public static TypeSpec listTypeSpec = new TypeSpec(listType);
    public static TypeSpec mapTypeSpec = new TypeSpec(mapType);

    // / if value type isConvertable to totype, convert it
    public static Value convertTo(final TypeSpec totype, Value value) {
        Debug.Depricated("TypeSpec.convertTo()");
        Debug.Assert(isConvertable(typeOf(value), totype, value),
                "isConvertable");

        if (value.getValue() instanceof Any) { // unwrap Any's
            value = new Value(((Any) value.getValue()).value);
        }

        if (value.getValue() == null) {
            return new Value(new Any(null)); // null can be assigned to anything
        }

        if (typeOf(value).equals(totype)) {
            return value; // trivial case, nothing to do
        }

        if (totype.equals(anyType)) {
            return new Value(new Any(value));
        }

        if (totype.equals(objectType)) {
            return value;
        }
        /*
         * if (value.value instanceof Integer) { if (totype.equals(numType))
         * return new Value(new Num(value.value)); else if
         * (totype.equals(dintType)) return new Value((long)(int)value.value);
         * else if (totype.equals(srealType)) return new
         * Value((float)(int)value.value); else if (totype.equals(realType))
         * return new Value((double)(int)value.value); } else if (value.value
         * instanceof Long) { if (totype.equals(numType)) return new Value(new
         * Num(value.value)); else if (totype.equals(realType)) return new
         * Value((double)(long)value.value); } else if (value.value instanceof
         * Float) { if (totype.equals(numType)) return new Value(new
         * Num(value.value)); else if (totype.equals(realType)) return new
         * Value((double)(float)value.value); } else if (value.value is Byte) {
         * if (totype.Equals(numType)) return new Value(new Num(value.value));
         * else if (totype.Equals(charType)) return new
         * Value((char)(byte)value.value); else if (totype.Equals(intType))
         * return new Value((int)(byte)value.value); else if
         * (totype.Equals(dintType)) return new Value((long)(byte)value.value);
         * } else if (value.value is Char) { if (totype.Equals(byteType)) return
         * new Value((byte)(char)value.value); else if (totype.Equals(intType))
         * return new Value((int)(char)value.value); else if
         * (totype.Equals(dintType)) return new Value((long)(char)value.value);
         * else if (totype.Equals(stringType)) return new Value(new
         * String((char)value.value,1)); } else if (value.value is Func) {
         * Debug.Assert(totype.isFunc());
         * 
         * // Since we can assume from & to are call compatible, nothing to do
         * to // convert the types return value;
         * 
         * // Func f = value.value as Func; // return new Value(new
         * Func(totype.funcInfo, f.outerScope, f.parser, f.value)); // func
         * value assignment }
         * 
         * // do arbitrary assignment //...!!!
         */
        Debug.Assert(false, "can't convert");
        return null;
    }

    // if CLI method has a CovariantReturnAttribute, return its type, else
    // return null
    public static TypeSpec covariantReturn(final Member minfo) {
        Debug.Unimplemented("covariantReturn");
        /*
         * object[] attributes =
         * minfo.GetCustomAttributes(typeof(CovariantReturnAttribute),false);
         * Debug.Assert(attributes.Length <= 1,
         * "can't have multiple covariant return attributes"); if
         * (attributes.Length == 1) return (attributes[0] as
         * CovariantReturnAttribute).returnType; return null;
         */
        return null;
    }

    // returns true if an operator is binary, false if unary (undefined if both)
    public static boolean isBinaryOperator(final String sourceName) {
        for (final Operator op : Operator.values()) {
            if (op.srcName.equals(sourceName)) {
                return op.binary;
            }
        }
        return false;
    }

    protected static boolean isBuiltin(final Type type) {
        if ((type instanceof java.lang.Class)
                && (((java.lang.Class) type).isPrimitive())) {
            return true;
        }
        return type.equals(objectType) || type.equals(anyType)
                || type.equals(numType) || type.equals(byteType)
                || type.equals(charType) || type.equals(stringType)
                || type.equals(boolType) || type.equals(intType)
                || type.equals(rangeType) || type.equals(dintType)
                || type.equals(realType) || type.equals(srealType)
                || type.equals(typeType) || type.equals(vectorType)
                || type.equals(matrixType) || type.equals(listType)
                || type.equals(mapType);
    }

    // / is it possible to convert from type 'from' to type 'to' without loss
    // / (e.g. true for from=int to=real)
    public static boolean isConvertable(final TypeSpec from, final TypeSpec to) {
        return isConvertable(from, to, null);
    }

    // / is it possible to convert from type 'from' of value 'value', to type
    // 'to' without loss
    // / (if from is of type 'any' then convertability depends on the specific
    // dynamic type
    // / of 'value')
    public static boolean isConvertable(TypeSpec from, final TypeSpec to,
            final Value value) {
        Debug.Depricated("TypeSpec.isConvertable()");
        Debug.Assert(from._type != null);
        Debug.Assert(to._type != null);
        // Debug.WriteLine("isConvertable "+from+" to "+to+" with value="+((value==null)?"none":((value.value!=null)?value.value.ToString():"null")));
        // //!!!

        // if 'from' is any and we have a value, use the value's actual type
        if (from.isAny() && (value != null)) {
            if (((Any) value.getValue()).value != null) {
                from = typeOf(((Any) value.getValue()).value);
            } else {
                return true; // a null value, can be 'converted' to anything
            }
        }

        if (from.equals(to)) {
            return true; // easy to convert same types => do nothing
        }
        if (to.equals(anyType)) {
            return true; // anything can be converted to 'any'
        }
        if (to.equals(objectType)) {
            return true; // anything can be converted to 'object'
        }

        if ((value != null) && (value.getValue() != null) && from.isAny()) {
            Debug.Assert(typeOf(value).isAny(),
                    "value must have type 'any' of from.isAny()");
        }

        if (from.isFunc() && !to.isFunc()) {
            return false; // can't convert func type to non-func type
        }
        if ((!from.isFunc()) && to.isFunc()) {
            return false; // can't convert non-func to func type
        }

        if (!from.isFunc() && !to.isFunc()) {
            if (to._type.equals(numType)) {
                return (from._type.equals(intType)
                        || from._type.equals(dintType)
                        || from._type.equals(realType)
                        || from._type.equals(srealType) || from._type
                        .equals(byteType));
            }

            if (from._type.equals(intType)) {
                return (to._type.equals(dintType) || to._type.equals(srealType) || to._type
                        .equals(realType));
            } else if (from._type.equals(dintType)) {
                return (to._type.equals(realType));
            } else if (from._type.equals(srealType)) {
                return (to._type.equals(realType));
            } else if (from._type.equals(byteType)) {
                return (to._type.equals(charType) || to._type.equals(intType) || to._type
                        .equals(dintType));
            }

            if (from._type.equals(charType)) {
                return (to._type.equals(byteType) || to._type.equals(intType)
                        || to._type.equals(dintType) || to._type
                        .equals(stringType));
            }

            if ((to._type instanceof Type) && (from._type instanceof Type)) {
                return false;// !!!((Type)to._type).IsAssignableFrom((Type)from._type);
            } else {
                return false;
            }
        } else {
            return ((FuncInfo) from._type).callCompatible((FuncInfo) to._type);
        }
    }

    public static boolean isJavaOperator(final String JavaName) {
        if ((JavaName.equals("op_Implicit"))
                || (JavaName.equals("op_Explicit"))
                || (JavaName.equals("toString"))) {
            return true;
        }
        // if ((JavaName == "get_Item") || JavaName.equals("set_Item")) return
        // true; // !!! that's handeled as a property, not an operator (just
        // happens to be called operator() )
        for (final Operator op : Operator.values()) {
            if (op.javaName.equals(JavaName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOperator(final String sourceName) {
        for (final Operator op : Operator.values()) {
            if (op.srcName.equals(sourceName)) {
                return true;
            }
        }
        // if (sourceName.equals("operator()")) // !!! should this and above
        // handle special case operator() as an operator or a property??
        // return true;
        return false;
    }

    public static EnumSet<Modifier> modifiersFromJavaModifiers(
            final int javaModifiers) {
        final int m = javaModifiers;
        final EnumSet<Modifier> mods = EnumSet.noneOf(TypeSpec.Modifier.class);
        if (java.lang.reflect.Modifier.isPublic(m)) {
            mods.add(TypeSpec.Modifier.Public);
        }
        if (java.lang.reflect.Modifier.isProtected(m)) {
            mods.add(TypeSpec.Modifier.Protected);
        }
        if (java.lang.reflect.Modifier.isPrivate(m)) {
            mods.add(TypeSpec.Modifier.Private);
        }
        if (java.lang.reflect.Modifier.isFinal(m)) {
            mods.add(TypeSpec.Modifier.Final);
        }
        if (java.lang.reflect.Modifier.isStatic(m)) {
            mods.add(TypeSpec.Modifier.Static);
        }
        if (java.lang.reflect.Modifier.isAbstract(m)) {
            mods.add(TypeSpec.Modifier.Abstract);
        }
        return mods;
    }

    public static String modifiersString(final EnumSet<Modifier> m) {
        String s = "";
        if (m.contains(Modifier.New)) {
            s += "new ";
        }
        if (m.contains(Modifier.Const)) {
            s += "const ";
        }
        if (m.contains(Modifier.Implicit)) {
            s += "implicit ";
        }
        if (m.contains(Modifier.Public)) {
            s += "public ";
        }
        if (m.contains(Modifier.Protected)) {
            s += "protected ";
        }
        if (m.contains(Modifier.Private)) {
            s += "private ";
        }
        if (m.contains(Modifier.Final)) {
            s += "final ";
        }
        if (m.contains(Modifier.Static)) {
            s += "static ";
        }
        if (m.contains(Modifier.Override)) {
            s += "override ";
        }
        if (m.contains(Modifier.Abstract)) {
            s += "abstract ";
        }
        if (s.length() > 0) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    // helpers

    public static Operator operatorByName(final String sourceName) {
        for (final Operator op : Operator.values()) {
            if (op.srcName.equals(sourceName)) {
                return op;
            }
        }
        Debug.Assert(false, "bad operator source name");
        return null;
    }

    // convert from source code method name to its Java name when arity is
    // unambiguous
    // (e.g. from operator+ to op_Addition)
    public static String operatorJavaName(final String sourceName) {
        if (sourceName.equals("operator->")) {
            Debug.Assert(false, "amgibuous source operator name:" + sourceName);
        }
        for (final Operator op : Operator.values()) {
            if (op.srcName.equals(sourceName)) {
                return op.javaName;
            }
        }
        // !!! what about operator()? see comment below
        Debug.Assert(false, "invalid source operator name:" + sourceName);
        return null;
    }

    // convert from source code method name to its Java name
    // (e.g. from operator+ to op_Addition)
    public static String operatorJavaName(final String sourceName,
            final boolean binary) {
        if (sourceName.equals("operator->")) {
            Debug.Assert(false, "amgibuous source operator name:" + sourceName);
        }
        for (final Operator op : Operator.values()) {
            if (op.srcName.equals(sourceName) && (op.binary == binary)) {
                return op.javaName;
            }
        }

        Debug.Assert(false, "invalid source operator name:" + sourceName);
        return null;
    }

    // convert from Java method name to its source code name
    // (e.g. from op_Addition to operator+)
    public static String operatorSourceName(final String javaName) {
        for (final Operator op : Operator.values()) {
            if (op.javaName.equals(javaName)) {
                return op.srcName;
            }
        }
        if ((javaName.equals("op_Implicit"))
                || (javaName.equals("op_Explicit"))
                || (javaName.equals("toString"))) {
            return "operator->";
        }
        return javaName;
    }

    protected static Type sysTypeOf(final String name) {
        // check builtins
        if (name.equals("object")) {
            return objectType;
        }
        if (name.equals("int")) {
            return intType;
        }
        if (name.equals("range")) {
            return rangeType;
        }
        if (name.equals("dint")) {
            return dintType;
        }
        if (name.equals("real")) {
            return realType;
        }
        if (name.equals("sreal")) {
            return srealType;
        }
        if (name.equals("bool")) {
            return boolType;
        }
        if (name.equals("byte")) {
            return byteType;
        }
        if (name.equals("char")) {
            return charType;
        }
        if (name.equals("string")) {
            return stringType;
        }
        if (name.equals("vector")) {
            return vectorType;
        }
        if (name.equals("matrix")) {
            return matrixType;
        }
        if (name.equals("list")) {
            return listType;
        }
        if (name.equals("map")) {
            return mapType;
        }
        if (name.equals("type")) {
            return typeType;
        }
        if (name.equals("num")) {
            return numType;
        }
        if (name.equals("any")) {
            return anyType;
        }
        // need to make this look in all loaded assemblies, not just the current
        // & mscorlib!!!
        // (either that, or make selectionExpression use the global namespace to
        // fetch the CLI type)
        try {
            return java.lang.Class.forName(name);
        } catch (final ClassNotFoundException e) {
            return null;
        }
    }

    public static String typeName(final Object o) {
        return typeName(new Value(o));
    }

    public static String typeName(final Value v) {
        Debug.Assert(v != null);
        return TypeSpec.typeOf(v).typeName();
    }

    public static TypeSpec typeOf(final Object o) {
        if (o == null) {
            return new TypeSpec(anyType);
        }
        if (o instanceof Value) {
            return typeOf((Value) o);
        }

        if (o instanceof Func) {
            return new TypeSpec(((Func) o).getInfo());
        } else if (o instanceof Class) {
            return new TypeSpec(((Class) o).getInfo());
        }

        return new TypeSpec(o.getClass());
    }

    public static TypeSpec typeOf(final String name) {
        final Type sysType = sysTypeOf(name);
        if (sysType == null) {

            // !!! handle func type spec strings here too

            return null; // unknown type
        }
        return new TypeSpec(sysType);
    }

    // !!! this should avoid evaluating the Value v if it is possible to deduce
    // the type
    // another way. For example, add a Value.type that if LValue, calls a
    // LValue.type,
    // which calls Symbol.type which just returns entry.type
    public static TypeSpec typeOf(final Value v) {
        Debug.Assert(v != null, "Value v is null");
        return v.getType();
    }

    public static Object unwrapAny(Object v) {
        if (v instanceof Value) {
            v = ((Value) v).getValue();
        }
        if ((v != null) && (v instanceof Any)) {
            return ((Any) v).value;
        }
        return v;
    }

    public static Object unwrapAnyOrNum(Object v) {
        if (v instanceof Value) {
            v = ((Value) v).getValue();
        }
        if (v != null) {
            if (v instanceof Any) {
                return ((Any) v).value;
            }
            if (v instanceof Num) {
                return ((Num) v).value;
            }
        }
        return v;
    }

    public static Value unwrapAnyOrNumValue(final Value v) {
        if (v.getValue() != null) {
            if (v.getValue() instanceof Any) {
                return new Value(((Any) v.getValue()).value);
            }
            if (v.getValue() instanceof Num) {
                return new Value(((Num) v.getValue()).value);
            }
        }
        return v;
    }

    public static Value unwrapAnyValue(final Value v) {
        if ((v.getValue() != null) && (v.getValue() instanceof Any)) {
            return new Value(((Any) v.getValue()).value);
        }
        return v;
    }

    public static Object valueOf(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Value) {
            return ((Value) o).getValue();
        } else {
            return o;
        }
    }

    protected Kind _kind;

    // description of type
    // a Type for Builtin or SysType, a ClassInfo or FuncInfo for Class or
    // Function
    protected Object _type;


    // any
    public TypeSpec() {
        _kind = Kind.Builtin;
        _type = anyType;
    }

    public TypeSpec(final ClassInfo ci) {
        Debug.Assert(ci != null);
        _kind = Kind.Class;
        _type = ci;
    }

    public TypeSpec(final FuncInfo fi) {
        Debug.Assert(fi != null);
        _kind = Kind.Function;
        _type = fi;
    }

    public TypeSpec(final Member member) {
        _kind = Kind.Function;
        _type = new FuncInfo(member);
    }

    public TypeSpec(final String name) {
        final Type javaType = sysTypeOf(name);
        init(javaType);
    }

    // construct from a Java type (automatically recognizes builtins)
    public TypeSpec(final Type type) {
        Debug.Assert(!type.equals(Void.TYPE), "can't have a TypeSpec for Void");
        init(type);
    }

    // construct a value for this type. For builtins and functions, the value
    // is set to the default for that type (e.g. 0 for numbers, null for
    // strings), and
    // for classes any members with initializers are initialized and a
    // constructor compatible with the callSig with args is called (and
    // null is returned if no appropriate constructor is found)
    public Value constructValue(final FuncInfo callSig, final ArrayList args,
            final ScigolTreeParser treeParser) {
        if (_kind == Kind.Builtin) {
            // !!! this should use the value args[0] as a default, if supplied!
            Debug.Assert((callSig == null) || (callSig.numArgs() == 0),
                    "builtin construction with args not implemented");
            final java.lang.Class sysClass = (java.lang.Class) _type;

            // construct the default value of type t, or null if no
            // no-argument constructor is accessible
            try {
                final Constructor ctor = sysClass
                        .getConstructor(new java.lang.Class[0]);
                return new Value(ctor.newInstance(new Object[0]));
            } catch (final NoSuchMethodException e) {
                // nope, just doesn't have a default constructor, so initialize
                // it to null
                return new Value(new Any(null));
            } catch (final InvocationTargetException e) {
                throw new RuntimeException("construction exception",
                        e.getCause());
            } catch (final InstantiationException e) {
                Debug.Assert(false, "a builtin is abstract?!");
            } catch (final IllegalAccessException e) {
                throw new RuntimeException("construction access exception",
                        e.getCause());
            }

        } else if (_kind == Kind.Class) {
            Debug.Assert(callSig != null,
                    "must supply an instantiation call signature");
            final ClassInfo classInfo = (ClassInfo) _type;
            return new Value(classInfo.instantiateClass(callSig, args,
                    treeParser));
        } else if (_kind == Kind.Function) {
            // !!! this should use the value args[0] if supplied (and a
            // compatible func)
            return new Value(new Func(getFuncInfo(), null, null, null, null,
                    null));
        }
        Debug.Assert(false, "unhandled type Kind");
        return null;
    }

    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o instanceof TypeSpec) {
            return equals((TypeSpec) o);
        } else if (o instanceof Type) {
            return equals((Type) o);
        }
        return false;
    }

    public boolean equals(final Type t) {
        if (t == null) {
            return false;
        }
        return equals(new TypeSpec(t));
    }

    public boolean equals(final TypeSpec t) {
        if (t == null) {
            return false;
        }
        if (_kind == Kind.Builtin) {
            if (!t.isBuiltin()) {
                return false;
            }
            return getSysType().equals(t.getSysType());
        } else if (_kind == Kind.Class) {
            if (t._kind != Kind.Class) {
                return false;
            }
            return getClassInfo().equals(t.getClassInfo());
        } else {
            if (!t.isFunc()) {
                return false;
            }
            return getFuncInfo().equals(t.getFuncInfo());
        }
    }

    public ClassInfo getClassInfo() {
        // convenience
        if (isBuiltinClass()) {
            return new ClassInfo((java.lang.Class) getSysType());
        }

        Debug.Assert(isClass() || isInterface(),
                "only classes & interfaces have classInfo");
        Debug.Assert(_type instanceof ClassInfo);
        return (ClassInfo) _type;
    }

    public FuncInfo getFuncInfo() {
        Debug.Assert(isFunc(), "only functions have funcInfo");
        Debug.Assert(_type instanceof FuncInfo);
        return (FuncInfo) _type;
    }

    public Type getSysType() {
        if (_kind == Kind.Builtin) {
            return (Type) _type;
        } else if (_kind == Kind.Class) {
            return ((ClassInfo) _type).getSysType();
        }
        Debug.Assert(false, "type has no Java type");
        return null;
    }

    protected void init(final Type type) {
        Debug.Assert(type != null);

        if (isBuiltin(type)) {
            _kind = Kind.Builtin;
            _type = type;

            // switch primitives for boxed versions
            if ((type instanceof java.lang.Class)
                    && ((java.lang.Class) type).isPrimitive()) {
                final java.lang.Class pclass = (java.lang.Class) type;
                if (pclass.equals(Byte.TYPE)) {
                    _type = byteType;
                } else if (pclass.equals(Character.TYPE)) {
                    _type = charType;
                } else if (pclass.equals(Boolean.TYPE)) {
                    _type = boolType;
                } else if (pclass.equals(Short.TYPE)) {
                    _type = intType; // !!! scigol doesn't have a short -
                                     // probably should add one sint?
                } else if (pclass.equals(Integer.TYPE)) {
                    _type = intType;
                } else if (pclass.equals(Long.TYPE)) {
                    _type = dintType;
                } else if (pclass.equals(Double.TYPE)) {
                    _type = realType;
                } else if (pclass.equals(Float.TYPE)) {
                    _type = srealType;
                } else {
                    Debug.Assert(false, "unsupported java primitive type:"
                            + type);
                }
            }
        } else if (type instanceof java.lang.Class) {

            Debug.Assert(
                    !type.equals(Class.class),
                    "can't recover ClassInfo from Type of Class object, construct TypeSpec from Class object instead");

            _kind = Kind.Class;
            _type = new ClassInfo((java.lang.Class) type);
        } else { // some other java type (e.g. ?)
            // !!!! this needs to be handled differently.
            _kind = Kind.Builtin;
            _type = type;
        }

    }

    public boolean isA(final TypeSpec t) {
        if (t.isBuiltinObject()) {
            return true; // everything is an object
        }
        if (equals(t)) {
            return true; // equivelent types
        }

        if (isClassOrInterface()) {
            if (t.isBuiltinClass()) {
                // handle case where Java class derives from the Java class of a
                // builtin (e.g. bilab.dbseq isA map)
                if (getClassInfo().isExternal()
                        && (((java.lang.Class) t.getSysType())
                                .isAssignableFrom((java.lang.Class) getSysType()))) {
                    return true;
                }
            } else if (t.isClassOrInterface()) {
                return getClassInfo().isA(t.getClassInfo());
            }
        }

        return false;
    }

    public boolean isAbstractClass() {
        if (_kind == Kind.Class) {
            return ((ClassInfo) _type).isAbstract();
        }
        return false;
    }

    public boolean isAbstractClassOrInterface() {
        return isAbstractClass() || isInterface();
    }

    public boolean isANum() {
        if (isNum()) {
            return true;
        }
        if (_kind != Kind.Builtin) {
            return false;
        }
        final Type sysType = (Type) _type;
        return sysType.equals(intType) || sysType.equals(dintType)
                || sysType.equals(realType) || sysType.equals(srealType)
                || sysType.equals(byteType) || sysType.equals(charType);
    }

    public boolean isAny() {
        return (_kind == Kind.Builtin) && ((Type) _type).equals(anyType);
    }

    public boolean isBool() {
        return (_type instanceof Type) && (_type.equals(boolType));
    }

    public boolean isBuiltin() {
        return (_kind == Kind.Builtin);
    }

    public boolean isBuiltinClass() {
        return isBuiltin()
                && (_type.equals(objectType) || _type.equals(stringType)
                        || _type.equals(rangeType) || _type.equals(vectorType)
                        || _type.equals(matrixType) || _type.equals(listType) || _type
                        .equals(mapType));
    }

    public boolean isBuiltinObject() {
        return (_kind == Kind.Builtin) && ((Type) _type).equals(objectType);
    }

    public boolean isByte() {
        return (_type instanceof Type) && (_type.equals(byteType));
    }

    public boolean isChar() {
        return (_type instanceof Type) && (_type.equals(charType));
    }

    public boolean isClass() {
        if (_kind == Kind.Class) {
            return !((ClassInfo) _type).isInterface();
        }
        return false;
    }

    public boolean isClassOrBuiltinClass() {
        return isClass() || isBuiltinClass();
    }

    public boolean isClassOrBuiltinClassOrInterface() {
        return isClassOrInterface() || isBuiltinClass();
    }

    public boolean isClassOrInterface() {
        return (_kind == Kind.Class);
    }

    public boolean isConvertableTo(final TypeSpec to) {
        return isConvertable(this, to, null);
    }

    public boolean isConvertableTo(final TypeSpec to, final Value value) {
        return isConvertable(this, to, value);
    }

    public boolean isDint() {
        return (_type instanceof Type) && (_type.equals(dintType));
    }

    public boolean isFunc() {
        return (_kind == Kind.Function);
    }

    public boolean isInt() {
        return (_type instanceof Type) && (_type.equals(intType));
    }

    public boolean isInterface() {
        if (_kind == Kind.Class) {
            return ((ClassInfo) _type).isInterface();
        }
        return false;
    }

    public boolean isList() {
        return (_type instanceof Type) && (_type.equals(listType));
    }

    public boolean isMap() {
        return (_type instanceof Type) && (_type.equals(mapType));
    }

    public boolean isMatrix() {
        return (_type instanceof Type) && (_type.equals(matrixType));
    }

    /*
     * convert to take EnumSet<Modifier> & return ? public static TypeAttributes
     * typeAttributesFromModifiers(TypeSpec.Modifiers modifiers, bool nested) {
     * //!!! check these attrs are correct
     * 
     * TypeAttributes attrs = (TypeAttributes)0;
     * 
     * if ((modifiers & TypeSpec.Modifiers.Public) != 0) attrs |=
     * (nested)?TypeAttributes.NestedPublic:TypeAttributes.Public; if
     * ((modifiers & TypeSpec.Modifiers.Protected) != 0) attrs |=
     * (nested)?TypeAttributes.NestedFamily:TypeAttributes.NestedAssembly; if
     * ((modifiers & TypeSpec.Modifiers.Private) != 0) attrs |=
     * (nested)?TypeAttributes.NestedPrivate:TypeAttributes.NotPublic; if
     * ((modifiers & TypeSpec.Modifiers.Final) != 0) attrs |=
     * TypeAttributes.Sealed;
     * 
     * return attrs; }
     */

    /*
     * public static TypeSpec.Modifiers
     * modifiersFromMethodAttributes(MethodAttributes attrs) {
     * TypeSpec.Modifiers modifiers = (TypeSpec.Modifiers)0;
     * 
     * if ((attrs & MethodAttributes.Public) != 0) modifiers |=
     * TypeSpec.Modifiers.Public; if (((attrs & MethodAttributes.Family) != 0)
     * && ((modifiers&Modifiers.Public)==0)) modifiers |=
     * TypeSpec.Modifiers.Protected; if ((attrs & MethodAttributes.Private) !=
     * 0) modifiers |= TypeSpec.Modifiers.Private; if ((attrs &
     * MethodAttributes.Final) != 0) modifiers |= TypeSpec.Modifiers.Final; if
     * ((attrs & MethodAttributes.Static) != 0) modifiers |=
     * TypeSpec.Modifiers.Static; if ((attrs & MethodAttributes.Abstract) != 0)
     * modifiers |= TypeSpec.Modifiers.Abstract;
     * 
     * return modifiers; }
     * 
     * 
     * 
     * public static MethodAttributes
     * methodAttributesFromModifiers(TypeSpec.Modifiers modifiers) {
     * MethodAttributes attrs = (MethodAttributes)0;
     * 
     * if ((modifiers & TypeSpec.Modifiers.Public) != 0) attrs |=
     * MethodAttributes.Public; if ((modifiers & TypeSpec.Modifiers.Protected)
     * != 0) attrs |= MethodAttributes.Family; if ((modifiers &
     * TypeSpec.Modifiers.Private) != 0) attrs |= MethodAttributes.Private; if
     * ((modifiers & TypeSpec.Modifiers.Final) != 0) attrs |=
     * MethodAttributes.Final; if ((modifiers & TypeSpec.Modifiers.Static) != 0)
     * attrs |= MethodAttributes.Static;
     * 
     * return attrs; }
     * 
     * 
     * public static TypeSpec.Modifiers
     * modifiersFromFieldAttributes(FieldAttributes attrs) { TypeSpec.Modifiers
     * modifiers = (TypeSpec.Modifiers)0;
     * 
     * if ((attrs & FieldAttributes.Public) != 0) modifiers |=
     * TypeSpec.Modifiers.Public; if ((attrs & FieldAttributes.Family) != 0)
     * modifiers |= TypeSpec.Modifiers.Protected; if ((attrs &
     * FieldAttributes.Private) != 0) modifiers |= TypeSpec.Modifiers.Private;
     * if ((attrs & FieldAttributes.Static) != 0) modifiers |=
     * TypeSpec.Modifiers.Static; if ((attrs & FieldAttributes.InitOnly) != 0)
     * modifiers |= TypeSpec.Modifiers.Const;
     * 
     * return modifiers;
     * 
     * }
     * 
     * 
     * public static FieldAttributes
     * fieldAttributesFromModifiers(TypeSpec.Modifiers modifiers) {
     * FieldAttributes attrs = (FieldAttributes)0;
     * 
     * if ((modifiers & TypeSpec.Modifiers.Public) != 0) attrs |=
     * FieldAttributes.Public; if ((modifiers & TypeSpec.Modifiers.Protected) !=
     * 0) attrs |= FieldAttributes.Family; if ((modifiers &
     * TypeSpec.Modifiers.Private) != 0) attrs |= FieldAttributes.Private; if
     * ((modifiers & TypeSpec.Modifiers.Static) != 0) attrs |=
     * FieldAttributes.Static;
     * 
     * return attrs; }
     */

    public boolean isNum() {
        return (_kind == Kind.Builtin) && ((Type) _type).equals(numType);
    }

    public boolean isRange() {
        return (_type instanceof Type) && (_type.equals(rangeType));
    }

    public boolean isReal() {
        return (_type instanceof Type) && (_type.equals(realType));
    }

    public boolean isSreal() {
        return (_type instanceof Type) && (_type.equals(srealType));
    }

    public boolean isString() {
        return (_type instanceof Type) && (_type.equals(stringType));
    }

    public boolean isType() {
        if (_kind == Kind.Builtin) {
            return ((Type) _type).equals(typeType);
        }
        if (_kind == Kind.Class) {
            return ((ClassInfo) _type).isTypeSpec();
        }
        return false;
    }

    public boolean isVector() {
        return (_type instanceof Type) && (_type.equals(vectorType));
    }

    public String toString() {
        return typeName();
    }

    // get the string type name
    public String typeName() {
        if (_kind == Kind.Builtin) {
            final Type t = (Type) _type;
            if (t.equals(intType)) {
                return "int";
            } else if (t.equals(rangeType)) {
                return "range";
            } else if (t.equals(dintType)) {
                return "dint";
            } else if (t.equals(realType)) {
                return "real";
            } else if (t.equals(srealType)) {
                return "sreal";
            } else if (t.equals(boolType)) {
                return "bool";
            } else if (t.equals(byteType)) {
                return "byte";
            } else if (t.equals(charType)) {
                return "char";
            } else if (t.equals(stringType)) {
                return "string";
            } else if (t.equals(vectorType)) {
                return "vector";
            } else if (t.equals(matrixType)) {
                return "matrix";
            } else if (t.equals(listType)) {
                return "list";
            } else if (t.equals(mapType)) {
                return "map";
            } else if (t.equals(typeType)) {
                return "type";
            } else if (t.equals(numType)) {
                return "num";
            } else if (t.equals(anyType)) {
                return "any";
            } else if (t.equals(objectType)) {
                return "object";
            }

            Debug.Assert(false, "unrecognized builtin type:" + t);
        } else if (_kind == Kind.Function) {
            return getFuncInfo().toString();
        } else if (_kind == Kind.Class) {
            return getClassInfo().toString();
        }

        return "any";
    }

    /*
     * protected static OperatorNames[] oper_names;
     * 
     * static TypeSpec() { oper_names = new OperatorNames[(int)Operator.TOP];
     * oper_names[(int)Operator.Multiply] = new OperatorNames("operator*",
     * "op_Multiply"); oper_names[(int)Operator.Division] = new
     * OperatorNames("operator/", "op_Division");
     * oper_names[(int)Operator.Modulus] = new OperatorNames("operator%",
     * "op_Modulus"); oper_names[(int)Operator.Addition] = new
     * OperatorNames("operator+", "op_Addition");
     * oper_names[(int)Operator.Subtraction] = new OperatorNames("operator-",
     * "op_Subtraction"); oper_names[(int)Operator.LessThan] = new
     * OperatorNames("operator<", "op_LessThan");
     * oper_names[(int)Operator.GreaterThan] = new OperatorNames("operator>",
     * "op_GreaterThan"); oper_names[(int)Operator.LessThanOrEqual] = new
     * OperatorNames("operator<=","op_LessThanOrEqual");
     * oper_names[(int)Operator.GreaterThanOrEqual] = new
     * OperatorNames("operator>=","op_GreaterThanOrEqual");
     * oper_names[(int)Operator.Equality] = new
     * OperatorNames("operator==","op_Equality");
     * oper_names[(int)Operator.Inequality] = new
     * OperatorNames("operator!=","op_Inequality");
     * oper_names[(int)Operator.UnaryPlus] = new OperatorNames("operator+",
     * "op_UnaryPlus",false); oper_names[(int)Operator.UnaryNegation] = new
     * OperatorNames("operator-", "op_UnaryNegation",false);
     * oper_names[(int)Operator.Increment] = new
     * OperatorNames("operator++","op_Increment");
     * oper_names[(int)Operator.Decrement] = new
     * OperatorNames("operator--","op_Decrement");
     * oper_names[(int)Operator.Norm] = new
     * OperatorNames("operator||","op_Norm",false);
     * oper_names[(int)Operator.Cardinality] = new OperatorNames("operator#",
     * "op_Card",false); oper_names[(int)Operator.Power] = new
     * OperatorNames("operator^", "op_Power"); oper_names[(int)Operator.Prime] =
     * new OperatorNames("operator'", "op_Prime",false);
     * oper_names[(int)Operator.Conversion] = new OperatorNames("operator->",
     * "",false);
     * 
     * }
     */

}
