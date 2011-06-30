package nsdb.NucFeaturePackage;


/**
* nsdb/NucFeaturePackage/QualifierHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb.idl
* Monday, August 23, 2004 12:02:41 PM BST
*/


/**
       * Qualifier.<p>
       * <dl>
       * <dt> name
       * <dd> name of the qualifier
       * <dt> values
       * <dd> sequence of QualifierValues. All QualifierValues associated with
       *      a single Qualifier are of the same type
       * </dl>
       */
abstract public class QualifierHelper
{
  private static String  _id = "IDL:nsdb/NucFeature/Qualifier:1.0";

  public static void insert (org.omg.CORBA.Any a, nsdb.NucFeaturePackage.Qualifier that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static nsdb.NucFeaturePackage.Qualifier extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [2];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "name",
            _tcOf_members0,
            null);
          _tcOf_members0 = nsdb.NucFeaturePackage.QualifierValue_uHelper.type ();
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (nsdb.NucFeaturePackage.QualifierValueListHelper.id (), "QualifierValueList", _tcOf_members0);
          _members0[1] = new org.omg.CORBA.StructMember (
            "values",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (nsdb.NucFeaturePackage.QualifierHelper.id (), "Qualifier", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static nsdb.NucFeaturePackage.Qualifier read (org.omg.CORBA.portable.InputStream istream)
  {
    nsdb.NucFeaturePackage.Qualifier value = new nsdb.NucFeaturePackage.Qualifier ();
    value.name = istream.read_string ();
    value.values = nsdb.NucFeaturePackage.QualifierValueListHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, nsdb.NucFeaturePackage.Qualifier value)
  {
    ostream.write_string (value.name);
    nsdb.NucFeaturePackage.QualifierValueListHelper.write (ostream, value.values);
  }

}
