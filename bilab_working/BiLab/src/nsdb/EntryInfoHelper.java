package nsdb;


/**
* nsdb/EntryInfoHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:40 PM BST
*/


/**
       * The EMBL database contains information, which is not really part of the
       * sequence information. This information is stored in the EntryInfo.
       */
abstract public class EntryInfoHelper
{
  private static String  _id = "IDL:nsdb/EntryInfo:1.0";

  public static void insert (org.omg.CORBA.Any a, nsdb.EntryInfo that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static nsdb.EntryInfo extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (nsdb.EntryInfoHelper.id (), "EntryInfo");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static nsdb.EntryInfo read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_EntryInfoStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, nsdb.EntryInfo value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static nsdb.EntryInfo narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof nsdb.EntryInfo)
      return (nsdb.EntryInfo)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      nsdb._EntryInfoStub stub = new nsdb._EntryInfoStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}