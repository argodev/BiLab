package type;

/**
 * type/Revision.java . Generated by the IDL-to-Java compiler (portable),
 * version "3.1" from corba/types.idl Monday, August 23, 2004 12:02:37 PM BST
 */

/**
 * Revision
 * <dl>
 * <dt>date
 * <dd>datestamp of revision
 * <dt>type
 * <dd>type of revision. Valid types are defined in meta
 * </dl>
 */
public final class Revision implements org.omg.CORBA.portable.IDLEntity {
    public type.Date date = null;
    public String type = null;

    public Revision() {
    } // ctor

    public Revision(final type.Date _date, final String _type) {
        date = _date;
        type = _type;
    } // ctor

} // class Revision
