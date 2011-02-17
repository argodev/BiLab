package scigol;

  
/// represents something that can be assigned (i.e. a Left Value)
///  (e.g. a Symbol, a vector/matrix or Symbols or an indexed Symbol)
public class LValue
{
  public LValue(Symbol s)
  {
    _isProperty=false;
    _symbol = s;
  }

  
  public LValue(Symbol s, FuncInfo callSig, Object[] propArgs)
  {
    _isProperty=true;
    _propArgs = propArgs;
    _propCallSig = callSig;
    _symbol = s;
  }
  
  
  public boolean isBoundProperty()
  {
    return _isProperty;
  }
  
  
  
  public Object getValue() 
  {
    if (!_isProperty)
      return _symbol.getValue();
    else {
      if (_propCallSig.numArgs() == 0)
        return _symbol.getValue();
      else 
        return _symbol.getValue(_propCallSig, _propArgs);
    }
  }
  
  
  public void setValue(Object value) 
  {
    if (!_isProperty)
      _symbol.setValue(value);
    else {
      if (_propCallSig.numArgs() == 0)
        _symbol.setValue(value);
      else 
        _symbol.setValue(_propCallSig, _propArgs, value);
    }
  }
  
  
  public TypeSpec getType()
  {
    if (!_isProperty)
      return _symbol.getType();
    else {
      if (_propCallSig.numArgs() == 0)
        return _symbol.getType();
      else
        return _symbol.getType(_propCallSig, _propArgs);
    }
  }
  
  
  public Symbol getSymbol()
  {
    return _symbol; 
  }
  
  
  public String toString()
  {
    return _symbol.toString();
  }

  
  protected Symbol _symbol; 

  protected boolean  _isProperty;
  protected FuncInfo _propCallSig;
  protected Object[] _propArgs;
}
