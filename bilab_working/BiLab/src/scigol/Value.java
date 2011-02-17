package scigol;




/// An internal value (can be a LValue (left value) or an actual value (right value),
///  a partially qualified name or a overloaded func array (with instance) )
public class Value
{
  // construct a null rvalue
  public Value()
  {
    _isLeftValue = false;
    _value = null;
    _isNamespaceComponent = false;
  }
  
  
  
  // construct with an LValue
  public Value(LValue lvalue)
  {
    Debug.Assert(lvalue!=null,"lvalue can't be null");
    _isLeftValue = true;
    _value = lvalue;
    _isNamespaceComponent = false;
  }


  // construct with an rvalue, or a string partially qualified name
  //  (after which you should call setValueIsNamespaceComponent() to
  //   differentiate from a string rvalue)
  public Value(Object value)
  {
    if (value instanceof Value) {
      // copy
      Value v = (Value)value;
      _isLeftValue = v._isLeftValue;
      _isNamespaceComponent = v._isNamespaceComponent;
      _value = v._value;
    }
    else {
      _isLeftValue = false;
      _value = value;
      _isNamespaceComponent = false;
    }
    
    Debug.Assert(!(_value instanceof Value), "Values can't wrap Values");
  }
  

  
  
  
  // Value can hold a namepace component as a string (e.g. System), which is not a real value
  //  but is useful to treat as one for the purpose of performing selection (.) operations  
  public void setValueIsNamespaceComponent(Location l)
  {
    Debug.Assert(!_isLeftValue);
    Debug.Assert(_value instanceof String);
    _isLeftValue = false;
    _isNamespaceComponent = true;
    _loc = l;
  }

  
  public boolean isLValue()
  {
    return _isLeftValue;
  }
  
  
  public boolean isNamespaceComponent()
  {
    return _isNamespaceComponent;
  }
  
  
  public String getNamespaceComponentString()
  {
    Debug.Assert(isNamespaceComponent());
    return _value.toString();
  }
  
  
  // if this is an lvalue then it is evaluated, hence becoming an rvalue
  public Value rvalue() 
  {
    if (!_isLeftValue) return this;
    
    LValue lvalue = (LValue)_value;
    _value = lvalue.getValue();
    _isLeftValue = false;
    return this;
  }
  
  
  public void setValue(Object value) 
  {
    Debug.Assert(!(value instanceof Value));
    Debug.Assert(_isLeftValue, "can only set the value of a LValue");
    ((LValue)_value).setValue(value);
  }
  
  public Object getValue() 
  {
    if (_isLeftValue)
      return ((LValue)_value).getValue();
    else {
      if (!_isNamespaceComponent)
        return _value; 
      else {
        ScigolTreeParser.semanticError(_loc,"the symbol '"+_value.toString()+"' could not be found in the current or global scope");
        return null;
      }
    }
  }

  

  public TypeSpec getType() 
  {
    if (_isLeftValue)
      return ((LValue)_value).getType();
    else {
      if (!_isNamespaceComponent)
        return TypeSpec.typeOf(_value);
      else {
        ScigolTreeParser.semanticError(_loc,"the symbol '"+_value.toString()+"' could not be found in the current or global scope");
        return null;
      }
    }
  }


  public LValue getLValue()
  {
    Debug.Assert(_isLeftValue, "isn't an lvalue");
    Debug.Assert(_value instanceof LValue, "_value isn't an LValue");
    return ((LValue)_value);
  }
  
    
  
  public String toString()
  {
    Object v=_value;
    if (v==null)
      return "null";
    else
      return v.toString();
  }

  protected boolean   _isLeftValue;
  protected boolean   _isNamespaceComponent;
  protected Object    _value; 
  protected Location  _loc = null;
}
