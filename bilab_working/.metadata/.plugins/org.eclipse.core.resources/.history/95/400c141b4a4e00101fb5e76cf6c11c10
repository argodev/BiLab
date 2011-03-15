package scigol;

// import java.util.*;

public class Math {

    // if a compatible operator overload exists for the value v1 [& v2], call
    // it, otherwise return null
    protected static Value callOverloadedOperator(final Value v1,
            final Value v2, final String opname) {
        Debug.Depricated("Math.callOverloadedOperator");

        // Note that, the builtin non-object class types (vector, matrix, etc.)
        // are treated
        // like user-defined type in that they define their own operators via
        // CLI methods
        // So, here we only need to consider the simple builtins (char, bye,
        // int, etc.)

        /*
         * 
         * boolean binary = (v2!=null);
         * 
         * Object n1 = v1.value; Object n2 = binary?v2.value:null;
         * 
         * Debug.Assert(n1 != null); Debug.Assert(!(n1 instanceof Value),
         * "Values can't nest Values"); Debug.Assert((!binary) || !(n2
         * instanceof Value), "Values can't nest Values");
         * 
         * // unwarp Any's if (n1 instanceof Any) { n1 = ((Any)n1).value; v1 =
         * new Value(n1); if (n1 == null) ScigolTreeParser.semanticError(
         * "a null value cannot appear on the LHS of "+opname); }
         * 
         * // unwrap num's if (n1 instanceof Num) { n1 = ((Num)n1).value; v1 =
         * new Value(n1); if (n1 == null) ScigolTreeParser.semanticError(
         * "a null value cannot appear on the LHS of "+opname); }
         * 
         * 
         * if (binary && (n2 instanceof Any)) { n2 = ((Any)n2).value; v2 = new
         * Value(n2); } else if (binary && (n2 instanceof Num)) { n2 =
         * ((Num)n2).value; v2 = new Value(n2); }
         * 
         * TypeSpec t1 = TypeSpec.typeOf(v1); TypeSpec t2 =
         * binary?TypeSpec.typeOf(v2):null;
         * 
         * if (t1.isClassOrBuiltinClass() || (binary &&
         * t2.isClassOrBuiltinClass())) { Func op = null;
         * 
         * // check for operator overload ArrayList args = new
         * ArrayList(binary?2:1); args.add(n1); if (binary) args.add(n2);
         * FuncInfo callSig = new FuncInfo(args);
         * 
         * // first check t1 if (t1.isClassOrBuiltinClass()) { ClassScope
         * classScope = new ClassScope(t1); Symbol s = new Symbol(classScope,
         * opname, n1);
         * 
         * if (s.exists()) { // found an operator if (s.isAmbiguous())
         * s.disambiguate(callSig, args); op = (Func)s.getValue(); } }
         * 
         * if (op == null) { // try t2 for a match if (binary && (n2 != null) &&
         * t2.isClassOrBuiltinClass()) { ClassScope classScope = new
         * ClassScope(t2); Symbol s = new Symbol(classScope, opname, n2); if
         * (s.exists()) { // found an operator if (s.isAmbiguous())
         * s.disambiguate(callSig, args); op = (Func)s.getValue(); } } }
         * 
         * if (op != null) { Object[] aargs = op.info.convertParameters(callSig,
         * args, false); return new Value(op.call(null, aargs)); // call it } }
         */
        return null;
    }

    // / cardinality
    public static Value card(final Value v1) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,null,"operator#"); if (ov !=
         * null) return ov;
         * 
         * Object n1 = v1.value;
         * 
         * Debug.Assert(n1 != null);
         * 
         * if ((n1 instanceof Integer) || (n1 instanceof Long) || (n1 instanceof
         * Double) || (n1 instanceof Float)) return v1;
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " has no unary operator# (cardinality)");
         */
        return null;
    }

    // / compare two values if possible
    public static int compareTo(Value v1, Value v2) {
        TypeSpec t1 = TypeSpec.typeOf(v1);
        TypeSpec t2 = TypeSpec.typeOf(v2);

        if (t1.isConvertableTo(t2)) {
            v1 = TypeSpec.convertTo(t2, v1);
            t1 = t2;
        } else if (t2.isConvertableTo(t1)) {
            v2 = TypeSpec.convertTo(t1, v2);
            t2 = t1;
        } else {
            ScigolTreeParser.semanticError("can't compare objects of type '"
                    + t1 + "' and '" + t2 + "'.");
        }

        final Object o1 = v1.getValue();
        final Object o2 = v2.getValue();

        if (o1 instanceof Comparable) {
            final Comparable c1 = (Comparable) o1;
            return c1.compareTo(o2);
        } else if (o2 instanceof Comparable) {
            final Comparable c2 = (Comparable) o2;
            return -c2.compareTo(o1);
        } else {
            ScigolTreeParser.semanticError("can't compare objects of type '"
                    + t1 + "' and '" + t2 + "'.");
        }
        return 0;
    }

    // / divide two values
    public static Value divide(final Value v1, final Value v2) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,v2,"operator/"); if (ov != null)
         * return ov;
         * 
         * Object n1 = v1.value; Object n2 = v2.value;
         * 
         * if (n1 instanceof Integer) {
         * 
         * if (n2 instanceof Integer) return new Value((int)n1 /
         * (double)(int)n2); else if (n2 instanceof Long) return new
         * Value((int)n1 / (double)(long)n2); else if (n2 instanceof Double)
         * return new Value((int)n1 / (double)n2); else if (n2 instanceof Float)
         * return new Value((int)n1 / (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary /");
         * 
         * } else if (n1 instanceof Long) {
         * 
         * if (n2 instanceof Int32) return new Value((long)n1 /
         * (double)(int)n2); else if (instanceof Long) return new Value((long)n1
         * / (double)(long)n2); else if (instanceof is Double) return new
         * Value((long)n1 / (double)n2); else if (n2 instanceof Float) return
         * new Value((long)n1 / (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary /");
         * 
         * } else if (n1 instanceof Double) {
         * 
         * if (n2 instanceof Int32) return new Value((double)n1 / (int)n2); else
         * if (n2 instanceof Long) return new Value((double)n1 / (long)n2); else
         * if (n2 instanceof Double) return new Value((double)n1 / (double)n2);
         * else if (n2 instanceof Single) return new Value((double)n1 /
         * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary /"); } else if (n1 is Single) {
         * if (n2 is Integer) return new Value((float)n1 / (int)n2); else if (n2
         * is Long) return new Value((float)n1 / (long)n2); else if (n2 is
         * Double) return new Value((float)n1 / (double)n2); else if (n2 is
         * Float) return new Value((float)n1 / (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary /"); }
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " cannot appear on the LHS of binary /");
         */
        return null;
    }

    // / check to value for equality
    // / for non-simple builtins, look for overloaded operator==, and if not
    // found calls
    // / either v1.compareTo if Comparable or v1.equals
    public static Value equality(final Value v1, final Value v2) {
        final Value ov = callOverloadedOperator(v1, v2, "operator==");
        if (ov != null) {
            return ov;
        }

        final Object n1 = v1.getValue();
        final Object n2 = v2.getValue();

        if ((n1 instanceof Comparable) && (n1.getClass().equals(n2.getClass()))) {
            final Comparable c1 = (Comparable) n1;
            return new Value(new Boolean(c1.compareTo(n2) == 0));
        }

        if (n1 instanceof Integer) {

            if (n2 instanceof Integer) {
                return new Value(new Boolean(
                        ((Integer) n1).intValue() == ((Integer) n2).intValue()));
            } else if (n2 instanceof Long) {
                return new Value(new Boolean(
                        ((Integer) n1).intValue() == ((Long) n2).longValue()));
            } else if (n2 instanceof Double) {
                return new Value(new Boolean(
                        ((Integer) n1).intValue() == ((Double) n2)
                                .doubleValue()));
            } else if (n2 instanceof Float) {
                return new Value(new Boolean(
                        ((Integer) n1).intValue() == ((Float) n2).floatValue()));
            }

            ScigolTreeParser.semanticError("type " + TypeSpec.typeName(n2)
                    + " cannot appear on the RHS of binary ==");

        } else if (n1 instanceof Long) {

            if (n2 instanceof Integer) {
                return new Value(new Boolean(
                        ((Long) n1).longValue() == ((Integer) n2).longValue()));
            } else if (n2 instanceof Long) {
                return new Value(new Boolean(
                        ((Long) n1).longValue() == ((Long) n2).longValue()));
            } else if (n2 instanceof Double) {
                return new Value(new Boolean(
                        ((Long) n1).longValue() == ((Double) n2).longValue()));
            } else if (n2 instanceof Float) {
                return new Value(new Boolean(
                        ((Long) n1).longValue() == ((Float) n2).longValue()));
            }

            ScigolTreeParser.semanticError("type " + TypeSpec.typeName(n2)
                    + " cannot appear on the RHS of binary ==");

        } else if (n1 instanceof Double) {

            if (n2 instanceof Integer) {
                return new Value(new Boolean(
                        ((Double) n1).doubleValue() == ((Integer) n2)
                                .doubleValue()));
            } else if (n2 instanceof Long) {
                return new Value(new Boolean(
                        ((Double) n1).doubleValue() == ((Long) n2)
                                .doubleValue()));
            } else if (n2 instanceof Double) {
                return new Value(new Boolean(
                        ((Double) n1).doubleValue() == ((Double) n2)
                                .doubleValue()));
            } else if (n2 instanceof Float) {
                return new Value(new Boolean(
                        ((Double) n1).doubleValue() == ((Float) n2)
                                .doubleValue()));
            }

            ScigolTreeParser.semanticError("type " + TypeSpec.typeName(n2)
                    + " cannot appear on the RHS of binary ==");
        } else if (n1 instanceof Float) {
            if (n2 instanceof Integer) {
                return new Value(new Boolean(
                        ((Float) n1).floatValue() == ((Integer) n2)
                                .floatValue()));
            } else if (n2 instanceof Long) {
                return new Value(new Boolean(
                        ((Float) n1).floatValue() == ((Long) n2).floatValue()));
            } else if (n2 instanceof Double) {
                return new Value(
                        new Boolean(
                                ((Float) n1).floatValue() == ((Double) n2)
                                        .floatValue()));
            } else if (n2 instanceof Float) {
                return new Value(new Boolean(
                        ((Float) n1).floatValue() == ((Float) n2).floatValue()));
            }

            ScigolTreeParser.semanticError("type " + TypeSpec.typeName(n2)
                    + " cannot appear on the RHS of binary ==");
        }

        ScigolTreeParser.semanticError("type " + TypeSpec.typeName(n1)
                + " cannot appear on the LHS of binary ==");
        return null;
    }

    public static Value inequality(final Value v1, final Value v2) {
        final Value ov = callOverloadedOperator(v1, v2, "operator!=");
        if (ov != null) {
            return ov;
        }

        final Object n1 = v1.getValue();
        final Object n2 = v2.getValue();

        if ((n1 instanceof Comparable) && (n1.getClass().equals(n2.getClass()))) {
            final Comparable c1 = (Comparable) n1;
            return new Value(new Boolean(c1.compareTo(n2) != 0));
        }

        final Value eq = equality(v1, v2);
        return new Value(new Boolean(
                !(((Boolean) eq.getValue()).booleanValue())));
    }

    // / true if o has the logical value true, throws if it isn't of type bool
    public static boolean isLogicalTrue(final Value v) {
        if (v.getValue() instanceof Boolean) {
            return ((Boolean) v.getValue()).booleanValue();
        }

        final TypeSpec ot = TypeSpec.typeOf(v);
        final TypeSpec boolType = new TypeSpec(TypeSpec.boolType);
        if (!TypeManager.existsImplicitConversion(ot, boolType, v)) {
            ScigolTreeParser.semanticError("can't convert type '" + ot
                    + "' to type 'bool'");
        }
        return ((Boolean) (TypeManager.performImplicitConversion(ot, boolType,
                v).getValue())).booleanValue();
    }

    // / subtract two values
    public static Value minus(final Value v1, final Value v2) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,v2,"operator-"); if (ov != null)
         * return ov;
         * 
         * Object n1 = v1.value; Object n2 = v2.value;
         * 
         * if (n1 instanceof Int32) {
         * 
         * if (n2 instanceof Int32) return new Value((int)n1 - (int)n2); else if
         * (n2 instanceof Int64) return new Value((int)n1 - (long)n2); else if
         * (n2 instanceof Double) return new Value((int)n1 - (double)n2); else
         * if (n2 instanceof Single) return new Value((int)n1 - (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary -");
         * 
         * } else if (n1 instanceof Int64) {
         * 
         * if (n2 instanceof Int32) return new Value((long)n1 - (int)n2); else
         * if (n2 instanceof Int64) return new Value((long)n1 - (long)n2); else
         * if (n2 instanceof Double) return new Value((long)n1 - (double)n2);
         * else if (n2 instanceof Single) return new Value((long)n1 -
         * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary -");
         * 
         * } else if (n1 instanceof Double) {
         * 
         * if (n2 instanceof Int32) return new Value((double)n1 - (int)n2); else
         * if (n2 instanceof Int64) return new Value((double)n1 - (long)n2);
         * else if (n2 instanceof Double) return new Value((double)n1 -
         * (double)n2); else if (n2 instanceof Single) return new
         * Value((double)n1 - (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary -"); } else if (n1 instanceof
         * Single) { if (n2 instanceof Int32) return new Value((float)n1 -
         * (int)n2); else if (n2 instanceof Int64) return new Value((float)n1 -
         * (long)n2); else if (n2 instanceof Double) return new Value((float)n1
         * - (double)n2); else if (n2 instanceof Single) return new
         * Value((float)n1 - (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary -"); }
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " cannot appear on the LHS of binary -");
         */
        return null;
    }

    // / modulo two values
    public static Value modulo(final Value v1, final Value v2) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,v2,"operator%"); if (ov != null)
         * return ov;
         * 
         * Object n1 = v1.value; Object n2 = v2.value;
         * 
         * if ((n1 instanceof Integer) && (n2 instanceof Integer)) { return new
         * Value( ((int)n1) % ((int)n2) ); } else Debug.Unimplemented("mod");
         */
        return null;
    }

    // / multiply two values
    public static Value multiply(final Value v1, final Value v2) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,v2,"operator*"); if (ov != null)
         * return ov;
         * 
         * Object n1 = v1.value; Object n2 = v2.value;
         * 
         * if (n1 instanceof Int32) {
         * 
         * if (n2 instanceof Int32) return new Value((int)n1 * (int)n2); else if
         * (n2 instanceof Int64) return new Value((int)n1 * (long)n2); else if
         * (n2 instanceof Double) return new Value((int)n1 * (double)n2); else
         * if (n2 instanceof Single) return new Value((int)n1 * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary *");
         * 
         * } else if (n1 instanceof Int64) {
         * 
         * if (n2 instanceof Int32) return new Value((long)n1 * (int)n2); else
         * if (n2 instanceof Int64) return new Value((long)n1 * (long)n2); else
         * if (n2 instanceof Double) return new Value((long)n1 * (double)n2);
         * else if (n2 instanceof Single) return new Value((long)n1 *
         * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary *");
         * 
         * } else if (n1 instanceof Double) {
         * 
         * if (n2 instanceof Int32) return new Value((double)n1 * (int)n2); else
         * if (n2 instanceof Int64) return new Value((double)n1 * (long)n2);
         * else if (n2 instanceof Double) return new Value((double)n1 *
         * (double)n2); else if (n2 instanceof Single) return new
         * Value((double)n1 * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary *"); } else if (n1 instanceof
         * Single) { if (n2 instanceof Int32) return new Value((float)n1 *
         * (int)n2); else if (n2 instanceof Int64) return new Value((float)n1 *
         * (long)n2); else if (n2 instanceof Double) return new Value((float)n1
         * * (double)n2); else if (n2 instanceof Single) return new
         * Value((float)n1 * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary *"); }
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " cannot appear on the LHS of binary *");
         */
        return null;
    }

    // / norm
    public static Value norm(final Value v1) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,null,"operator||"); if (ov !=
         * null) return ov;
         * 
         * Object n1 = v1.value;
         * 
         * Debug.Assert(n1 != null);
         * 
         * if (n1 instanceof Int32) return new Value(System.Math.Abs((int)n1));
         * else if (n1 instanceof Long) return new
         * Value(System.Math.Abs((long)n1)); else if (n1 instanceof Double)
         * return new Value(System.Math.Abs((double)n1)); else if (n1 instanceof
         * Float) return new Value(System.Math.Abs((float)n1));
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " has no unary operator|| (norm)");
         */
        return null;
    }

    protected static Object performBuiltinOperator(final TypeSpec t,
            final Object l, final Object r, final TypeSpec.Operator op) {
        // ... if only Java had macros .. :(

        switch (op) {
            case Multiply: {
                if (t.isByte()) {
                    return new Byte(
                            (byte) (((Byte) l).byteValue() * ((Byte) r)
                                    .byteValue()));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() * ((Character) r)
                                    .charValue()));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue()
                            * ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue()
                            * ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue()
                            * ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue()
                            * ((Double) r).doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Division: {
                if (t.isByte()) {
                    return new Byte(
                            (byte) (((Byte) l).byteValue() / ((Byte) r)
                                    .byteValue()));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() / ((Character) r)
                                    .charValue()));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue()
                            / ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue()
                            / ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue()
                            / ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue()
                            / ((Double) r).doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Modulus: {
                if (t.isByte()) {
                    return new Byte(
                            (byte) (((Byte) l).byteValue() % ((Byte) r)
                                    .byteValue()));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() % ((Character) r)
                                    .charValue()));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue()
                            % ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue()
                            % ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue()
                            % ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue()
                            % ((Double) r).doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Addition: {
                if (t.isByte()) {
                    return new Byte(
                            (byte) (((Byte) l).byteValue() + ((Byte) r)
                                    .byteValue()));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() + ((Character) r)
                                    .charValue()));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue()
                            + ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue()
                            + ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue()
                            + ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue()
                            + ((Double) r).doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Subtraction: {
                if (t.isByte()) {
                    return new Byte(
                            (byte) (((Byte) l).byteValue() - ((Byte) r)
                                    .byteValue()));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() - ((Character) r)
                                    .charValue()));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue()
                            - ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue()
                            - ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue()
                            - ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue()
                            - ((Double) r).doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case LessThan: {
                if (t.isByte()) {
                    return new Boolean(
                            ((Byte) l).byteValue() < ((Byte) r).byteValue());
                } else if (t.isChar()) {
                    return new Boolean(
                            ((Character) l).charValue() < ((Character) r)
                                    .charValue());
                } else if (t.isInt()) {
                    return new Boolean(
                            ((Integer) l).intValue() < ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Boolean(
                            ((Long) l).longValue() < ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Boolean(
                            ((Float) l).floatValue() < ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Boolean(
                            ((Double) l).doubleValue() < ((Double) r)
                                    .doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case GreaterThan: {
                if (t.isByte()) {
                    return new Boolean(
                            ((Byte) l).byteValue() > ((Byte) r).byteValue());
                } else if (t.isChar()) {
                    return new Boolean(
                            ((Character) l).charValue() > ((Character) r)
                                    .charValue());
                } else if (t.isInt()) {
                    return new Boolean(
                            ((Integer) l).intValue() > ((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Boolean(
                            ((Long) l).longValue() > ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Boolean(
                            ((Float) l).floatValue() > ((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Boolean(
                            ((Double) l).doubleValue() > ((Double) r)
                                    .doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case LessThanOrEqual: {
                if (t.isByte()) {
                    return new Boolean(
                            ((Byte) l).byteValue() <= ((Byte) r).byteValue());
                } else if (t.isChar()) {
                    return new Boolean(
                            ((Character) l).charValue() <= ((Character) r)
                                    .charValue());
                } else if (t.isInt()) {
                    return new Boolean(
                            ((Integer) l).intValue() <= ((Integer) r)
                                    .intValue());
                } else if (t.isDint()) {
                    return new Boolean(
                            ((Long) l).longValue() <= ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Boolean(
                            ((Float) l).floatValue() <= ((Float) r)
                                    .floatValue());
                } else if (t.isReal()) {
                    return new Boolean(
                            ((Double) l).doubleValue() <= ((Double) r)
                                    .doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case GreaterThanOrEqual: {
                if (t.isByte()) {
                    return new Boolean(
                            ((Byte) l).byteValue() >= ((Byte) r).byteValue());
                } else if (t.isChar()) {
                    return new Boolean(
                            ((Character) l).charValue() >= ((Character) r)
                                    .charValue());
                } else if (t.isInt()) {
                    return new Boolean(
                            ((Integer) l).intValue() >= ((Integer) r)
                                    .intValue());
                } else if (t.isDint()) {
                    return new Boolean(
                            ((Long) l).longValue() >= ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Boolean(
                            ((Float) l).floatValue() >= ((Float) r)
                                    .floatValue());
                } else if (t.isReal()) {
                    return new Boolean(
                            ((Double) l).doubleValue() >= ((Double) r)
                                    .doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Equality: {
                if (t.isByte()) {
                    return new Boolean(
                            ((Byte) l).byteValue() == ((Byte) r).byteValue());
                } else if (t.isChar()) {
                    return new Boolean(
                            ((Character) l).charValue() == ((Character) r)
                                    .charValue());
                } else if (t.isInt()) {
                    return new Boolean(
                            ((Integer) l).intValue() == ((Integer) r)
                                    .intValue());
                } else if (t.isDint()) {
                    return new Boolean(
                            ((Long) l).longValue() == ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Boolean(
                            ((Float) l).floatValue() == ((Float) r)
                                    .floatValue());
                } else if (t.isReal()) {
                    return new Boolean(
                            ((Double) l).doubleValue() == ((Double) r)
                                    .doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Inequality: {
                if (t.isByte()) {
                    return new Boolean(
                            ((Byte) l).byteValue() != ((Byte) r).byteValue());
                } else if (t.isChar()) {
                    return new Boolean(
                            ((Character) l).charValue() != ((Character) r)
                                    .charValue());
                } else if (t.isInt()) {
                    return new Boolean(
                            ((Integer) l).intValue() != ((Integer) r)
                                    .intValue());
                } else if (t.isDint()) {
                    return new Boolean(
                            ((Long) l).longValue() != ((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Boolean(
                            ((Float) l).floatValue() != ((Float) r)
                                    .floatValue());
                } else if (t.isReal()) {
                    return new Boolean(
                            ((Double) l).doubleValue() != ((Double) r)
                                    .doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case UnaryPlus: {
                if (t.isByte()) {
                    return r;
                } else if (t.isChar()) {
                    return r;
                } else if (t.isInt()) {
                    return r;
                } else if (t.isDint()) {
                    return r;
                } else if (t.isSreal()) {
                    return r;
                } else if (t.isReal()) {
                    return r;
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case UnaryNegation: {
                if (t.isByte()) {
                    return new Byte((byte) (-((Byte) r).byteValue()));
                } else if (t.isChar()) {
                    return new Character((char) (-((Character) r).charValue()));
                } else if (t.isInt()) {
                    return new Integer(-((Integer) r).intValue());
                } else if (t.isDint()) {
                    return new Long(-((Long) r).longValue());
                } else if (t.isSreal()) {
                    return new Float(-((Float) r).floatValue());
                } else if (t.isReal()) {
                    return new Double(-((Double) r).doubleValue());
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Increment: {
                if (t.isByte()) {
                    return new Byte((byte) (((Byte) l).byteValue() + (byte) 1));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() + (char) 1));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue() + 1);
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue() + 1);
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue() + 1);
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue() + 1);
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Decrement: {
                if (t.isByte()) {
                    return new Byte((byte) (((Byte) l).byteValue() - (byte) 1));
                } else if (t.isChar()) {
                    return new Character(
                            (char) (((Character) l).charValue() - (char) 1));
                } else if (t.isInt()) {
                    return new Integer(((Integer) l).intValue() - 1);
                } else if (t.isDint()) {
                    return new Long(((Long) l).longValue() - 1);
                } else if (t.isSreal()) {
                    return new Float(((Float) l).floatValue() - 1);
                } else if (t.isReal()) {
                    return new Double(((Double) l).doubleValue() - 1);
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Norm: {
                if (t.isByte()) {
                    return new Byte((byte) java.lang.Math.abs(((Byte) l)
                            .byteValue()));
                } else if (t.isChar()) {
                    return new Character(
                            (char) java.lang.Math.abs(((Character) l)
                                    .charValue()));
                } else if (t.isInt()) {
                    return new Integer(java.lang.Math.abs(((Integer) l)
                            .intValue()));
                } else if (t.isDint()) {
                    return new Long(java.lang.Math.abs(((Long) l).longValue()));
                } else if (t.isSreal()) {
                    return new Float(java.lang.Math.abs(((Float) l)
                            .floatValue()));
                } else if (t.isReal()) {
                    return new Double(java.lang.Math.abs(((Double) l)
                            .doubleValue()));
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Cardinality: {
                return new Integer(1);
            }
            case Power: {
                if (t.isByte()) {
                    return new Double(java.lang.Math.pow(
                            ((Byte) l).byteValue(), ((Byte) r).byteValue()));
                } else if (t.isChar()) {
                    return new Double(java.lang.Math.pow(
                            ((Character) l).charValue(),
                            ((Character) r).charValue()));
                } else if (t.isInt()) {
                    return new Double(java.lang.Math.pow(
                            ((Integer) l).intValue(), ((Integer) r).intValue()));
                } else if (t.isDint()) {
                    return new Double(java.lang.Math.pow(
                            ((Long) l).longValue(), ((Long) r).longValue()));
                } else if (t.isSreal()) {
                    return new Double(java.lang.Math.pow(
                            ((Float) l).floatValue(), ((Float) r).floatValue()));
                } else if (t.isReal()) {
                    return new Double(java.lang.Math.pow(
                            ((Double) l).doubleValue(),
                            ((Double) r).doubleValue()));
                } else {
                    Debug.Assert(false);
                }
            }
                break;
            case Prime: {
                return new Integer(1);
            }
            default:
                Debug.Assert(false, "bad/unknown/unimplemented operator");
                break;
        }
        return null;
    }

    // perform an operation 'opname' on the given values (user-defined or
    // builtin)
    // if a prefix/post fix unary operator, either lhs or rhs may be null
    public static Value performOverloadedOperation(final String opname,
            Value lhs, Value rhs) {
        Debug.Assert(
                !opname.equals("operator->"),
                "the conversion operator 'operator->' is handled through, TypeManager.performUserDefinedConversion()");
        // Debug.WL("performOverloadedOperation:"+opname);
        // Debug.WL(" on "+lhs);
        // Debug.WL(" and "+rhs);
        TypeSpec tlhs = (lhs != null) ? TypeSpec.typeOf(lhs) : new TypeSpec(
                TypeSpec.anyType);
        TypeSpec trhs = (rhs != null) ? TypeSpec.typeOf(rhs) : new TypeSpec(
                TypeSpec.anyType);

        // Note that, the builtin non-object class types (vector, matrix, etc.)
        // are treated
        // like user-defined type in that they define their own operators via
        // Java methods
        // So, here we only need to consider the simple builtins (char, byte,
        // int, etc.) seperately

        // first construct appropriate call signature
        boolean binary = TypeSpec.isBinaryOperator(opname);
        if ((opname.equals("operator+")) || (opname.equals("operator-"))) { // check
                                                                            // for
                                                                            // unary
                                                                            // -/+
            if ((lhs == null) || (rhs == null)) {
                binary = false;
            }
        }

        final TypeSpec[] paramTypes = new TypeSpec[binary ? 2 : 1];
        Object[] args = new Object[binary ? 2 : 1];
        if (binary) {
            paramTypes[0] = tlhs;
            args[0] = lhs.getValue();
            paramTypes[1] = trhs;
            args[1] = rhs.getValue();
        } else {

            if (lhs != null) {
                paramTypes[0] = tlhs;
                args[0] = lhs.getValue();
            } else {
                paramTypes[0] = trhs;
                args[0] = rhs.getValue();
            }
        }
        final FuncInfo callSig = new FuncInfo(paramTypes, new TypeSpec(
                TypeSpec.anyType));

        // now, look for user-defined operators
        final Entry[] ops = TypeManager.resolveOperatorOverload(opname,
                callSig, args);
        if (ops.length > 1) {
            ScigolTreeParser.semanticError("call of overloaded operator '"
                    + opname + "' is ambiguous for signature " + callSig);
        }

        if (ops.length == 1) { // one and only, use it
            // all operators are static, so the Func value is in the entry
            final Func opFunc = (Func) ops[0].getStaticValue();
            // Debug.WriteLine("found operator "+opname+" for "+tlhs+" op "+trhs+" from type "+(ops[0].scope
            // as ClassScope).classType+" with signature "+opFunc.info);
            Debug.Assert(opFunc != null, "no Func to call!");
            args = opFunc.getInfo().convertParameters(callSig, args,
                    opFunc.isExternal());

            final Object result = opFunc.call(null, args);

            return new Value(result);
        }

        // we still don't have an operator, so consider the builtin operators
        if (tlhs.isBuiltin() || trhs.isBuiltin()) {

            // unwrap Any's
            if (tlhs.isAny() && (lhs != null) && (lhs.getValue() != null)) {
                lhs = new Value(((Any) lhs.getValue()).value);
                tlhs = TypeSpec.typeOf(lhs);
            }
            if (trhs.isAny() && (rhs != null) && (rhs.getValue() != null)) {
                rhs = new Value(((Any) rhs.getValue()).value);
                trhs = TypeSpec.typeOf(rhs);
            }

            // operator+ is concatenation for strings. It has the special
            // property that explicit
            // conversions from the rhs to string are attempted (i.e. not just
            // implicit ones)
            if (binary && (opname == "operator+") && tlhs.isString()) {

                // null string values become the string "null" via operator+
                final String lhsString = (lhs.getValue() != null) ? (String) lhs
                        .getValue() : "null";

                String rhsString = null;
                if (rhs.getValue() == null) {
                    rhsString = "null";
                } else {
                    if (!trhs.isString()) {
                        // for speed, if the trhs is a builtin, just call
                        // ToString() directly rather than
                        // having explicit conversion look around for it
                        if (trhs.isBuiltin()) {
                            rhsString = rhs.getValue().toString();
                        } else {
                            final Object o = TypeManager
                                    .performExplicitConversion(trhs, tlhs, rhs);
                            // null return signifies no conversion
                            if (o instanceof Value) {
                                rhsString = (String) ((Value) o).getValue();
                            } else {
                                ScigolTreeParser
                                        .semanticError("no conversion to string"); // skip
                                                                                   // down
                                                                                   // to
                                                                                   // error
                            }
                        }
                    } else {
                        rhsString = (String) rhs.getValue();
                    }
                }

                if (rhsString == null) {
                    rhsString = "null"; // conversion may have returned null
                }

                return new Value(lhsString + rhsString); // concatenate
            }

            // since string maps to the Java String class, pretend it has an
            // operator# that gives the length
            if (!binary && (opname == "operator#") && trhs.isString()) {

                // null string values have 0 length via operator#
                if (rhs.getValue() == null) {
                    return new Value(0);
                }

                final String rhsString = (String) rhs.getValue();
                return new Value(new Integer(rhsString.length()));
            }

            // !!! may want to consider checking for and using IComparable for
            // operators>/</>=/<= and maybe ==,!=
            // (maybe that would be better where CLI ToString is interpreted as
            // operator->(->string) to interpret
            // IComparable methods as operator>/< etc. ?)

            // builtin numeric operators
            if (((lhs == null) || tlhs.isANum())
                    && ((rhs == null) || trhs.isANum())) { // (non-null sides
                                                           // are Nums)

                // unwrap Num's
                if (tlhs.isNum() && (lhs != null) && (lhs.getValue() != null)) {
                    lhs = new Value(((Num) lhs.getValue()).value);
                    tlhs = TypeSpec.typeOf(lhs);
                }
                if (trhs.isNum() && (rhs != null) && (rhs.getValue() != null)) {
                    rhs = new Value(((Num) rhs.getValue()).value);
                    trhs = TypeSpec.typeOf(rhs);
                }

                // now, we find the most-encompassing type of tlhs & trhs and
                // convert
                // the other to it
                boolean typesIdentical = true;
                if (binary && !tlhs.equals(trhs)) {
                    if (TypeManager.encompasses(tlhs, trhs)) { // convert rhs to
                                                               // tlhs
                        rhs = TypeManager.performImplicitConversion(trhs, tlhs,
                                rhs);
                        trhs = tlhs;
                    } else if (TypeManager.encompasses(trhs, tlhs)) { // convert
                                                                      // lhs to
                                                                      // trhs
                        lhs = TypeManager.performImplicitConversion(tlhs, trhs,
                                lhs);
                        tlhs = trhs;
                    } else {
                        typesIdentical = false;
                    }
                }

                if (typesIdentical) {

                    TypeSpec.Operator op = TypeSpec.operatorByName(opname);
                    if (!binary) {
                        if (opname.equals("operator+")) {
                            op = TypeSpec.Operator.UnaryPlus;
                        }
                        if (opname.equals("operator-")) {
                            op = TypeSpec.Operator.UnaryNegation;
                        }
                    }

                    if (binary) {
                        return new Value(performBuiltinOperator(tlhs,
                                lhs.getValue(), rhs.getValue(), op));
                    } else {
                        // some unary operators use lhs (e.g. e') and some use
                        // rhs (e.g. #e)
                        final TypeSpec t = (lhs == null) ? trhs : tlhs;
                        final Object v = (lhs == null) ? rhs.getValue() : lhs
                                .getValue();
                        return new Value(performBuiltinOperator(t, v, v, op));
                    }

                }

            }

        }

        ScigolTreeParser.semanticError("no suitable operator '" + opname
                + "' could be found for call with signature " + callSig);
        return null;
    }

    // / add two values
    public static Value plus(final Value v1, final Value v2) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,v2,"operator+"); if (ov != null)
         * return ov;
         * 
         * Object n1 = v1.getValue(); Object n2 = v2.getValue();
         * 
         * Debug.Assert((n1 != null) && (n2 != null));
         * 
         * if (n1 instanceof Integer) {
         * 
         * if (n2 instanceof Integer) return new Value(new Integer((int)n1 +
         * (int)n2)); else if (n2 instanceof Long) return new Value((int)n1 +
         * (long)n2); else if (n2 instanceof Double) return new Value((int)n1 +
         * (double)n2); else if (n2 instanceof Float) return new Value((int)n1 +
         * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary +");
         * 
         * } else if (n1 instanceof Long) {
         * 
         * if (n2 instanceof Int32) return new Value((long)n1 + (int)n2); else
         * if (n2 instanceof Int64) return new Value((long)n1 + (long)n2); else
         * if (n2 instanceof Double) return new Value((long)n1 + (double)n2);
         * else if (n2 instanceof Single) return new Value((long)n1 +
         * (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary +");
         * 
         * } else if (n1 instanceof Double) {
         * 
         * if (n2 instanceof Int32) return new Value((double)n1 + (int)n2); else
         * if (n2 instanceof Int64) return new Value((double)n1 + (long)n2);
         * else if (n2 instanceof Double) return new Value((double)n1 +
         * (double)n2); else if (n2 instanceof Single) return new
         * Value((double)n1 + (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary +"); } else if (n1 instanceof
         * Single) { if (n2 instanceof Int32) return new Value((float)n1 +
         * (int)n2); else if (n2 instanceof Int64) return new Value((float)n1 +
         * (long)n2); else if (n2 instanceof Double) return new Value((float)n1
         * + (double)n2); else if (n2 instanceof Single) return new
         * Value((float)n1 + (float)n2);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n2)+
         * " cannot appear on the RHS of binary +"); } else if (n1 instanceof
         * String) { // string concatenation return new
         * Value(((string)n1)+n2.ToString()); }
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " cannot appear on the LHS of binary +"); return null; }
         * 
         * 
         * /// unary + public static Value unaryplus(Value v1) {
         * Debug.Depricated("Math.<operation>"); Value ov =
         * callOverloadedOperator(v1,null,"operator+"); if (ov != null) return
         * ov;
         * 
         * Object n1 = v1.getValue();
         * 
         * Debug.Assert(n1 != null);
         * 
         * if ((n1 instanceof Integer) || (n1 instanceof Long) || (n1 instanceof
         * Double) || (n1 instanceof Float)) return v1;
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " has no unary operator+");
         */
        return null;
    }

    public static Value power(final Value v1, final Value v2) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,v2,"operator^"); if (ov != null)
         * return ov;
         * 
         * Object n1 = v1.value; Object n2 = v2.value;
         * 
         * if ((n1 instanceof Double) && (n2 instanceof Double)) { return new
         * Value( System.Math.Pow((double)n1, (double)n2) ); } else if ((n1
         * instanceof Int32) && (n2 instanceof Integer)) { return new Value(
         * System.Math.Pow((int)n1, (int)n2) ); } else if ((n1 instanceof
         * Double) && (n2 instanceof Integer)) { return new Value(
         * System.Math.Pow((double)n1, (int)n2) ); } else
         * Debug.Unimplemented("power");
         */
        return null;
    }

    public static Value prime(final Value v1) {

        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,null,"operator'"); if (ov !=
         * null) return ov;
         * 
         * Object n1 = v1.value;
         * 
         * // for all numeric types, the ' operator has no effect if ((n1
         * instanceof Integer) || (n1 instanceof Long) || (n1 instanceof Double)
         * || (n1 instanceof Float)) return v1;
         * 
         * // for vector & matrix, do transpose if ((n1 is Matrix) || (n1 is
         * Vector)) Debug.Unimplemented("transpose");
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " doesn't have an operator'");
         */
        return null;
    }

    // / unary -
    public static Value unaryminus(final Value v1) {
        Debug.Depricated("Math.<operation>");
        /*
         * Value ov = callOverloadedOperator(v1,null,"operator-"); if (ov !=
         * null) return ov;
         * 
         * Object n1 = v1.getValue();
         * 
         * Debug.Assert(n1 != null);
         * 
         * if (n1 instanceof Integer) return new Value(-(int)n1); else if (n1
         * instanceof Long) return new Value(-(long)n1); else if (n1 instanceof
         * Double) return new Value(-(double)n1); else if (n1 instanceof Float)
         * return new Value(-(float)n1);
         * 
         * ScigolTreeParser.semanticError("type "+TypeSpec.typeName(n1)+
         * " has no unary operator+");
         */
        return null;
    }

}
