package type;

/**
 * type/DbXrefListHolder.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:37 PM BST
 */

public final class DbXrefListHolder implements
        org.omg.CORBA.portable.Streamable {
    public type.DbXref value[] = null;

    public DbXrefListHolder() {
    }

    public DbXrefListHolder(final type.DbXref[] initialValue) {
        value = initialValue;
    }

    @Override
    public void _read(final org.omg.CORBA.portable.InputStream i) {
        value = type.DbXrefListHelper.read(i);
    }

    @Override
    public org.omg.CORBA.TypeCode _type() {
        return type.DbXrefListHelper.type();
    }

    @Override
    public void _write(final org.omg.CORBA.portable.OutputStream o) {
        type.DbXrefListHelper.write(o, value);
    }

}
