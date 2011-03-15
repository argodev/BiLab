package scigol;

import java.util.ArrayList;

public class Vector {
    public static Vector op_Addition(final Vector v1, final Vector v2) {
        // !! temp impl.
        Debug.Assert(v1.op_Card() == v2.op_Card(),
                "vector sized don't match (this should be a semantic error)");

        final Vector v3 = new Vector();

        for (int a = 0; a < v1.op_Card(); a++) {
            final Object v1e = v1.get_Item(a);
            final Object v2e = v2.get_Item(a);

            final Value e1 = new Value(v1e);
            final Value e2 = new Value(v2e);

            v3.appendElement(Math.performOverloadedOperation("operator+", e1,
                    e2).getValue());
        }

        return v3;
    }

    public static int op_Card(final Vector v) {
        return v.op_Card();
    }

    public static Vector op_Multiply(final Vector v1, final Double s) {
        // !!! hack for demo!!!
        final Vector v2 = new Vector();

        for (int a = 0; a < v1.op_Card(); a++) {
            final Object v1o = v1.get_Item(a).value;
            final Value v1e = new Value(v1o);
            final Double element = (Double) (TypeManager
                    .performImplicitConversion(TypeSpec.typeOf(v1e),
                            TypeSpec.realTypeSpec, v1e).getValue());

            final Double e1 = new Double(element.doubleValue()
                    * s.doubleValue());

            v2.appendElement(e1);
        }

        return v2;
    }

    public static Object op_Norm(final Vector v) {
        if (v.a.get(0) instanceof Double) {
            double ss = 0;
            for (int i = 0; i < v.a.size(); i++) {
                ss += ((Double) v.a.get(i)).doubleValue()
                        * ((Double) v.a.get(i)).doubleValue();
            }
            return new Double(java.lang.Math.sqrt(ss));
        } else if (v.a.get(0) instanceof Float) {
            double ss = 0;
            for (int i = 0; i < v.a.size(); i++) {
                ss += (((Float) v.a.get(i)).floatValue() * ((Float) v.a.get(i))
                        .floatValue());
            }
            return new Double(java.lang.Math.sqrt(ss));
        } else {
            ScigolTreeParser
                    .semanticError("can't compute the norm (operator||) of non-numeric vector elements");
        }
        return null;
    }

    public static Vector op_Subtraction(final Vector v1, final Vector v2) {
        // !! temp impl.
        Debug.Assert(v1.op_Card() == v2.op_Card(),
                "vector sized don't match (this should be a semantic error)");

        final Vector mv2 = op_UnaryNegation(v2);
        return op_Addition(v1, mv2);
    }

    public static Vector op_UnaryNegation(final Vector v) {
        final Vector nv = new Vector();
        for (int a = 0; a < v.op_Card(); a++) {
            final Object ve = v.get_Item(a);

            final Value e = new Value(ve);

            nv.appendElement(Math.performOverloadedOperation("operator-", null,
                    e).getValue());
        }
        return nv;
    }

    // zero vector of given dimension
    public static Vector zero(final int dim) {
        final Vector v = new Vector();
        for (int d = 0; d < dim; d++) {
            v.a.add(new Double(0));
        }
        return v;
    }

    protected ArrayList a;

    public Vector() {
        a = new ArrayList();
    }

    public Vector(final double d) {
        a = new ArrayList();
        a.add(new Double(d));
    }

    public Vector(final List l) {
        a = new ArrayList();

        for (int i = 0; i < l.size(); i++) {
            appendElement((l.get_Item(i)).value);
        }
    }

    // @hide or something!!!
    public void appendElement(final Object e) {
        if (a.size() > 0) {
            final TypeSpec etype = new TypeSpec(a.get(0).getClass()); // existing
                                                                      // element
                                                                      // type
            final TypeSpec ntype = new TypeSpec(e.getClass()); // new element
                                                               // type
            if (!etype.equals(ntype)) {
                if (!TypeManager.existsImplicitConversion(etype, ntype,
                        new Value(e))) {
                    ScigolTreeParser.semanticError("expected element of type '"
                            + etype + "', not '" + ntype + "'");
                } else { // convert existing elements to wider type
                    final ArrayList na = new ArrayList();
                    for (int i = 0; i < a.size(); i++) {
                        na.add(TypeManager.performImplicitConversion(etype,
                                ntype, new Value(a.get(i))).getValue());
                    }
                    a = na;
                }
            }
        }

        a.add(e);
    }

    // dot product
    public double dot(final Vector v) {
        if (v.op_Card() != op_Card()) {
            throw new IllegalArgumentException(
                    "vector arguments must have same dimension");
        }

        double dp = 0;

        for (int a = 0; a < op_Card(); a++) {
            final Object v1o = get_Item(a).value;
            final Value v1e = new Value(v1o);
            final Double element1 = (Double) (TypeManager
                    .performImplicitConversion(TypeSpec.typeOf(v1e),
                            TypeSpec.realTypeSpec, v1e).getValue());

            final Object v2o = v.get_Item(a).value;
            final Value v2e = new Value(v2o);
            final Double element2 = (Double) (TypeManager
                    .performImplicitConversion(TypeSpec.typeOf(v2e),
                            TypeSpec.realTypeSpec, v2e).getValue());

            dp += element1.doubleValue() * element2.doubleValue();
        }

        return dp;
    }

    @accessor
    public Any get_Item(final int i) {
        if ((i >= 0) && (i < a.size())) {
            return new Any(a.get(i));
        } else {
            throw new IndexOutOfBoundsException("vector index " + i
                    + " out of range 0.." + a.size());
        }
    }

    @accessor
    public Vector get_Item(Range r) {
        r = r.normalize(a.size());

        if ((r.start < 0) || (r.start >= a.size())) {
            throw new IndexOutOfBoundsException("vector range start " + r.start
                    + " out of range 0.." + (get_size() - 1));
        }
        if ((r.end < 0) || (r.end >= a.size())) {
            throw new IndexOutOfBoundsException("vector range end " + r.end
                    + " out of range 0.." + (get_size() - 1));
        }

        if (r.start > r.end) {
            return new Vector();
        }
        if (r.start == r.end) {
            final Vector v = new Vector();
            v.appendElement(a.get(r.start));
            return v;
        }

        final Vector vec = new Vector();
        for (int i = r.start; i <= r.end; i++) {
            vec.a.add(a.get(i));
        }
        return vec;
    }

    @accessor
    public int get_size() {
        return a.size();
    }

    // !!! remove this
    public int op_Card() {
        return a.size();
    }

    @accessor
    public void set_Item(final int i, final Any value) {
        if ((i >= 0) && (i < a.size())) {
            a.set(i, value.value);
        } else {
            throw new IndexOutOfBoundsException("vector index " + i
                    + " out of range 0.." + a.size());
        }
    }

    @accessor
    public void set_Item(Range r, final Vector value) {
        r = r.normalize(a.size());

        if ((r.start < 0) || (r.start >= a.size())) {
            throw new IndexOutOfBoundsException("vector range start " + r.start
                    + " out of range 0.." + (get_size() - 1));
        }
        if ((r.end < 0) || (r.end >= a.size())) {
            throw new IndexOutOfBoundsException("vector range end " + r.end
                    + " out of range 0.." + (get_size() - 1));
        }

        if (r.start > r.end) {
            if (value.get_size() != 0) {
                throw new IllegalArgumentException("can't assign vector with "
                        + value.get_size()
                        + " elements to a range of 0 elements");
            }
            return;
        }
        if (r.start == r.end) {
            a.set(r.start, value.a.get(0));
            if (value.get_size() != 0) {
                throw new IllegalArgumentException("can't assign vector with "
                        + value.get_size()
                        + " elements to a range of 1 element");
            }
            return;
        }

        if (value.get_size() != (r.end - r.start + 1)) {
            throw new IllegalArgumentException("can't assign vector with "
                    + value.get_size() + " elements to a range of "
                    + (r.end - r.start + 1) + " elements");
        }

        for (int i = r.start; i <= r.end; i++) {
            a.set(i, value.a.get(i - r.start));
        }

    }

    @Override
    public String toString() {
        String s = "[";
        for (int i = 0; i < a.size(); i++) {
            s += a.get(i).toString();
            if (i != a.size() - 1) {
                s += " ";
            }
        }
        s += "]";
        return s;
    }

}
