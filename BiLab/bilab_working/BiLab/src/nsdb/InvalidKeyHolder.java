package nsdb;

/**
* nsdb/InvalidKeyHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb_write.idl
* Monday, August 23, 2004 12:02:44 PM BST
*/

public final class InvalidKeyHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.InvalidKey value = null;

  public InvalidKeyHolder ()
  {
  }

  public InvalidKeyHolder (nsdb.InvalidKey initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.InvalidKeyHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.InvalidKeyHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.InvalidKeyHelper.type ();
  }

}
