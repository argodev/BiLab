package type;

/**
 * type/InvalidRelationHolder.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:36 PM BST
 */

/**
 * If an object reference given as an input parameter is invalid, in the context
 * of the current interface, an InvalidRelation is raised
 */
public final class InvalidRelationHolder implements
        org.omg.CORBA.portable.Streamable {
    public type.InvalidRelation value = null;

    public InvalidRelationHolder() {
    }

    public InvalidRelationHolder(final type.InvalidRelation initialValue) {
        value = initialValue;
    }

    @Override
    public void _read(final org.omg.CORBA.portable.InputStream i) {
        value = type.InvalidRelationHelper.read(i);
    }

    @Override
    public org.omg.CORBA.TypeCode _type() {
        return type.InvalidRelationHelper.type();
    }

    @Override
    public void _write(final org.omg.CORBA.portable.OutputStream o) {
        type.InvalidRelationHelper.write(o, value);
    }

}
