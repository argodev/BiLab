package nsdb;


/**
* nsdb/OutOfDate.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb_write.idl
* Monday, August 23, 2004 12:02:43 PM BST
*/

public final class OutOfDate extends org.omg.CORBA.UserException
{

  public OutOfDate ()
  {
    super(OutOfDateHelper.id());
  } // ctor


  public OutOfDate (String $reason)
  {
    super(OutOfDateHelper.id() + "  " + $reason);
  } // ctor

} // class OutOfDate
