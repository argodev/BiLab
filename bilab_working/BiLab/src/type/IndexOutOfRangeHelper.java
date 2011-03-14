package type;

/**
 * type/IndexOutOfRangeHelper.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:36 PM BST
 */

/**
 * If a number indicating a position in a sequence is outside the limits of the
 * sequence, or more elements are associated to an object than it can handle, an
 * IndexOutOfRange exception is raised
 */
abstract public class IndexOutOfRangeHelper {
    private static String _id = "IDL:type/IndexOutOfRange:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    private static boolean __active = false;

    public static type.IndexOutOfRange extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a,
            final type.IndexOutOfRange that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static type.IndexOutOfRange read(
            final org.omg.CORBA.portable.InputStream istream) {
        final type.IndexOutOfRange value = new type.IndexOutOfRange();
        // read and discard the repository ID
        istream.read_string();
        value.reason = istream.read_string();
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
                    final org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember[1];
                    org.omg.CORBA.TypeCode _tcOf_members0 = null;
                    _tcOf_members0 = org.omg.CORBA.ORB.init().create_string_tc(
                            0);
                    _members0[0] = new org.omg.CORBA.StructMember("reason",
                            _tcOf_members0, null);
                    __typeCode = org.omg.CORBA.ORB.init().create_exception_tc(
                            type.IndexOutOfRangeHelper.id(), "IndexOutOfRange",
                            _members0);
                    __active = false;
                }
            }
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final type.IndexOutOfRange value) {
        // write the repository ID
        ostream.write_string(id());
        ostream.write_string(value.reason);
    }

}
