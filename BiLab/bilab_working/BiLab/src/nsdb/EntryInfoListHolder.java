package nsdb;


/**
* nsdb/EntryInfoListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:40 PM BST
*/

public final class EntryInfoListHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.EntryInfo value[] = null;

  public EntryInfoListHolder ()
  {
  }

  public EntryInfoListHolder (nsdb.EntryInfo[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.EntryInfoListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.EntryInfoListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.EntryInfoListHelper.type ();
  }

}