package nsdb.LocationPackage;

/**
* nsdb/LocationPackage/LocOperatorHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:40 PM BST
*/


/**
       * Location operator. This is a node in the location tree that combines
       * nodes lower down in the tree
       * <dl>
       * <dt> op
       * <dd> operator defining what to do with the nodes
       * <dd> <dl>
       *      <dt> join
       *       <dd> The indicated elements should be joined (placed end-to-end)
       *            to form one contiguous sequence
       *      <dt> order
       *       <dd> The elements can be found in the specified order
       *            (5' to 3' direction), but nothing is implied about the
       *            reasonableness about joining them 
       *      </dl>
       * <dt> childIds
       * <dd> identifiers of the child nodes
       * <dd> Ids is an array of IDs relative to the current node
       *      e.g if the current node is at position x in the LocationNodes sequence
       *      and there is a childId with value j
       *      then the position of this child in the LocationNodes sequence will be x+j
       *</dl>
       */
public final class LocOperatorHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.LocationPackage.LocOperator value = null;

  public LocOperatorHolder ()
  {
  }

  public LocOperatorHolder (nsdb.LocationPackage.LocOperator initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.LocationPackage.LocOperatorHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.LocationPackage.LocOperatorHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.LocationPackage.LocOperatorHelper.type ();
  }

}
