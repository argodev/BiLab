package nsdb;


/**
* nsdb/LocationListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:39 PM BST
*/

public final class LocationListHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.Location value[] = null;

  public LocationListHolder ()
  {
  }

  public LocationListHolder (nsdb.Location[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.LocationListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.LocationListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.LocationListHelper.type ();
  }

}
