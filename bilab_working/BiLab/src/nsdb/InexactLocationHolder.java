package nsdb;

/**
* nsdb/InexactLocationHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:40 PM BST
*/

public final class InexactLocationHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.InexactLocation value = null;

  public InexactLocationHolder ()
  {
  }

  public InexactLocationHolder (nsdb.InexactLocation initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.InexactLocationHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.InexactLocationHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.InexactLocationHelper.type ();
  }

}