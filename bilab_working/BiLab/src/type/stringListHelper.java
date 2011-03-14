package type;

/**
 * type/stringListHelper.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:36 PM BST
 */

/**
 * sequence of strings.
 */
abstract public class stringListHelper {
    private static String _id = "IDL:type/stringList:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    public static String[] extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a, final String[] that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static String[] read(final org.omg.CORBA.portable.InputStream istream) {
        String value[] = null;
        final int _len0 = istream.read_long();
        value = new String[_len0];
        for (int _o1 = 0; _o1 < value.length; ++_o1) {
            value[_o1] = istream.read_string();
        }
        return value;
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            __typeCode = org.omg.CORBA.ORB.init().create_string_tc(0);
            __typeCode = org.omg.CORBA.ORB.init().create_sequence_tc(0,
                    __typeCode);
            __typeCode = org.omg.CORBA.ORB.init().create_alias_tc(
                    type.stringListHelper.id(), "stringList", __typeCode);
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final String[] value) {
        ostream.write_long(value.length);
        for (int _i0 = 0; _i0 < value.length; ++_i0) {
            ostream.write_string(value[_i0]);
        }
    }

}
