package type;

/**
 * type/DateListHolder.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/types.idl Monday, August 23, 2004 12:02:36 PM BST
 */

public final class DateListHolder implements org.omg.CORBA.portable.Streamable {
    public type.Date value[] = null;

    public DateListHolder() {
    }

    public DateListHolder(final type.Date[] initialValue) {
        value = initialValue;
    }

    @Override
    public void _read(final org.omg.CORBA.portable.InputStream i) {
        value = type.DateListHelper.read(i);
    }

    @Override
    public org.omg.CORBA.TypeCode _type() {
        return type.DateListHelper.type();
    }

    @Override
    public void _write(final org.omg.CORBA.portable.OutputStream o) {
        type.DateListHelper.write(o, value);
    }

}
