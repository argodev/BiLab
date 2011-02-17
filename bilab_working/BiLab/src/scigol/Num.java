package scigol;


  
/// holder for a value of any numeric type (int,dint,real,sreal,byte)
///  (essentially tags the value of being assignable to any numeric type)  
public class Num
{
  public Num()
  {
    value = new Integer(0);
  }
  
  
  public Num(Object wrappedvalue)
  {
    if (wrappedvalue instanceof Num) 
      value = ((Num)wrappedvalue).value; // don't nest
    else if (wrappedvalue instanceof Any)
      value = ((Any)wrappedvalue).value; // don't next Any in Num
    else {
      Debug.Assert(TypeSpec.typeOf(wrappedvalue).isANum(),"must be one of the num types (int, dint, real, sreal, byte)");
      value = wrappedvalue;
    }
  }
  
  
  /*!!! what is this doing here??
  public static Num operator_plus(Num n1, Num n2) 
   { return new Num(Math.plus(new Value(n1.value), new Value(n2.value)).getValue()); }
  */
  
  public String toString()
  {
    if (value == null) 
      return "null";
    else
      return value.toString();
  }
  
  
  public Object value;
  
}
