package type;


/**
* type/InvalidRelation.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from corba/types.idl
* Monday, August 23, 2004 12:02:36 PM BST
*/


/**
   * If an object reference given as an input parameter is invalid, in the
   * context of the current interface, an InvalidRelation is raised
   */
public final class InvalidRelation extends org.omg.CORBA.UserException
{
  public String reason = null;

  public InvalidRelation ()
  {
    super(InvalidRelationHelper.id());
  } // ctor

  public InvalidRelation (String _reason)
  {
    super(InvalidRelationHelper.id());
    reason = _reason;
  } // ctor


  public InvalidRelation (String $reason, String _reason)
  {
    super(InvalidRelationHelper.id() + "  " + $reason);
    reason = _reason;
  } // ctor

} // class InvalidRelation