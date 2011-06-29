package nsdb.NucFeaturePackage;

/**
* nsdb/NucFeaturePackage/QualifierHolder.java .
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
public final class QualifierHolder implements org.omg.CORBA.portable.Streamable
{
  public nsdb.NucFeaturePackage.Qualifier value = null;

  public QualifierHolder ()
  {
  }

  public QualifierHolder (nsdb.NucFeaturePackage.Qualifier initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = nsdb.NucFeaturePackage.QualifierHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    nsdb.NucFeaturePackage.QualifierHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return nsdb.NucFeaturePackage.QualifierHelper.type ();
  }

}
