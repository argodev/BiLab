package scigol;
  
/// holder for a value of any type
///  (essentially tags the value of being assignable to any type)  
public class Any
{
  public Any()
  {
    value = null;
  }
  
  
  public Any(Object wrappedvalue)
  {
    Debug.Assert(!(wrappedvalue instanceof Value));
    if (wrappedvalue instanceof Any)
      value = ((Any)wrappedvalue).value; // don't nest
    else if (wrappedvalue instanceof Num)
      value = ((Num)wrappedvalue).value; // don't nest a Num into an Any
    else
      value = wrappedvalue;
  }

  
  
  public String toString()
  {
    if (value != null)
      return value.toString();
    else
      return "null";
  }
  
  
  public Object value;
  
}
