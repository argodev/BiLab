package type;

/**
 * type/ulongListHelper.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/types.idl Monday, August 23, 2004 12:02:36 PM BST
 */

abstract public class ulongListHelper {
    private static String _id = "IDL:type/ulongList:1.0";

    private static org.omg.CORBA.TypeCode __typeCode = null;

    public static int[] extract(final org.omg.CORBA.Any a) {
        return read(a.create_input_stream());
    }

    public static String id() {
        return _id;
    }

    public static void insert(final org.omg.CORBA.Any a, final int[] that) {
        final org.omg.CORBA.portable.OutputStream out = a
                .create_output_stream();
        a.type(type());
        write(out, that);
        a.read_value(out.create_input_stream(), type());
    }

    public static int[] read(final org.omg.CORBA.portable.InputStream istream) {
        int value[] = null;
        final int _len0 = istream.read_long();
        value = new int[_len0];
        istream.read_ulong_array(value, 0, _len0);
        return value;
    }

    synchronized public static org.omg.CORBA.TypeCode type() {
        if (__typeCode == null) {
            __typeCode = org.omg.CORBA.ORB.init().get_primitive_tc(
                    org.omg.CORBA.TCKind.tk_ulong);
            __typeCode = org.omg.CORBA.ORB.init().create_sequence_tc(0,
                    __typeCode);
            __typeCode = org.omg.CORBA.ORB.init().create_alias_tc(
                    type.ulongListHelper.id(), "ulongList", __typeCode);
        }
        return __typeCode;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream ostream,
            final int[] value) {
        ostream.write_long(value.length);
        ostream.write_ulong_array(value, 0, value.length);
    }

}
