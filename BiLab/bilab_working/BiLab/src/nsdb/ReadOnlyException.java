package nsdb;


/**
* nsdb/ReadOnlyException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/nsdb_write.idl
* Monday, August 23, 2004 12:02:43 PM BST
*/

public final class ReadOnlyException extends org.omg.CORBA.UserException
{

  public ReadOnlyException ()
  {
    super(ReadOnlyExceptionHelper.id());
  } // ctor


  public ReadOnlyException (String $reason)
  {
    super(ReadOnlyExceptionHelper.id() + "  " + $reason);
  } // ctor

} // class ReadOnlyException