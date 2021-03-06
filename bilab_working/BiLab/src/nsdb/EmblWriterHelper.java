package nsdb;


/**
* nsdb/EmblWriterHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb_write.idl
* Monday, August 23, 2004 12:02:44 PM BST
*/

abstract public class EmblWriterHelper
{
  private static String  _id = "IDL:nsdb/EmblWriter:1.0";

  public static void insert (org.omg.CORBA.Any a, nsdb.EmblWriter that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static nsdb.EmblWriter extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (nsdb.EmblWriterHelper.id (), "EmblWriter");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static nsdb.EmblWriter read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_EmblWriterStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, nsdb.EmblWriter value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static nsdb.EmblWriter narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof nsdb.EmblWriter)
      return (nsdb.EmblWriter)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      nsdb._EmblWriterStub stub = new nsdb._EmblWriterStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
