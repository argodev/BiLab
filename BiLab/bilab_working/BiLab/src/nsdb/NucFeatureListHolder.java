package nsdb;


/**
* nsdb/NucFeatureListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:39 PM BST
*/

public final class NucFeatureListHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.NucFeature value[] = null;

  public NucFeatureListHolder ()
  {
  }

  public NucFeatureListHolder (nsdb.NucFeature[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.NucFeatureListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.NucFeatureListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.NucFeatureListHelper.type ();
  }

}
