package type;

/**
 * type/InvalidArgumentValueHolder.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.1" from corba/types.idl Monday, August 23, 2004
 * 12:02:36 PM BST
 */

/**
 * controlled values (i.e. an attribute can only contain a value taken from a
 * well defined range of values, or an in parameter to a method has a restricted
 * set of acceptable values) are presented as typescodes, used in as union. If a
 * method tries to set a controlled value attribute/parameter to an invalid
 * value, an invalidValue is raised
 */
public final class InvalidArgumentValueHolder implements
        org.omg.CORBA.portable.Streamable {
    public type.InvalidArgumentValue value = null;

    public InvalidArgumentValueHolder() {
    }

    public InvalidArgumentValueHolder(
            final type.InvalidArgumentValue initialValue) {
        value = initialValue;
    }

    @Override
    public void _read(final org.omg.CORBA.portable.InputStream i) {
        value = type.InvalidArgumentValueHelper.read(i);
    }

    @Override
    public org.omg.CORBA.TypeCode _type() {
        return type.InvalidArgumentValueHelper.type();
    }

    @Override
    public void _write(final org.omg.CORBA.portable.OutputStream o) {
        type.InvalidArgumentValueHelper.write(o, value);
    }

}
