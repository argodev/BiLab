package type;


/**
* type/FuzzyListHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/types.idl
* Monday, August 23, 2004 12:02:37 PM BST
*/

public final class FuzzyListHolder implements org.omg.CORBA.portable.Streamable
{
  public type.Fuzzy value[] = null;

  public FuzzyListHolder ()
  {
  }

  public FuzzyListHolder (type.Fuzzy[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = type.FuzzyListHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    type.FuzzyListHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return type.FuzzyListHelper.type ();
  }

}