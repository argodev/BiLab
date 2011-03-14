package type;

/**
 * type/RepeatUnitHelper.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:37 PM BST
 */

/**
 * A RepeatUnit identifies the exact unit that is being repeated. It can be
 * either a base range, or can refer to the label of a labeled repeat_unit
 * feature (but not both; this datatype is likely to be superceded by a union).
 * <dt>start
 * <dd>position of first base in the first occurrence of the repeated segment
 * <dt>end
 * <dd>position of the last base in first occurence of the repeated segment
 * <dt>label
 * <dd>Currently, usually a textual description of the repeating segment, but
 * can refer to a labeled repeat_unit feature. This attribute may in future
 * become a proper DbXref to a feature;
 */
abstract public class RepeatUnitHelper {
    private static String _id = "IDL:type/RepeatUnit:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    private static boolean __active = false;

    public static type.RepeatUnit extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a,
            final type.RepeatUnit that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static type.RepeatUnit read(
            final org.omg.CORBA.portable.InputStream istream) {
        final type.RepeatUnit value = new type.RepeatUnit();
        value.start = istream.read_long();
        value.end = istream.read_long();
        value.label = istream.read_string();
        return value;
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            synchronized (org.omg.CORBA.TypeCode.class) {
                if (__typeCode == null) {
                    if (__active) {
                        return org.omg.CORBA.ORB.init()
                                .create_recursive_tc(_id);
                    }
                    __active = true;
                    final org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember[3];
                    org.omg.CORBA.TypeCode _tcOf_members0 = null;
                    _tcOf_members0 = org.omg.CORBA.ORB.init().get_primitive_tc(
                            org.omg.CORBA.TCKind.tk_long);
                    _members0[0] = new org.omg.CORBA.StructMember("start",
                            _tcOf_members0, null);
                    _tcOf_members0 = org.omg.CORBA.ORB.init().get_primitive_tc(
                            org.omg.CORBA.TCKind.tk_long);
                    _members0[1] = new org.omg.CORBA.StructMember("end",
                            _tcOf_members0, null);
                    _tcOf_members0 = org.omg.CORBA.ORB.init().create_string_tc(
                            0);
                    _members0[2] = new org.omg.CORBA.StructMember("label",
                            _tcOf_members0, null);
                    __typeCode = org.omg.CORBA.ORB.init()
                            .create_struct_tc(type.RepeatUnitHelper.id(),
                                    "RepeatUnit", _members0);
                    __active = false;
                }
            }
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final type.RepeatUnit value) {
        ostream.write_long(value.start);
        ostream.write_long(value.end);
        ostream.write_string(value.label);
    }

}
