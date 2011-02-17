package scigol;


import java.util.*;

  
/// represents the location of a value that has a symbolic name associated with it
///  (e.g. a local variable or func parameter, or a object field etc.)
///  (NB: can represent multiple locations, in the case of an overloaded name)
///   In this case disambiguate(callSig) must be called first (with null for a field))
public class Symbol
{
  // Symbol for a variable in a scope
  public Symbol(Scope scope, String id, Object instance)
  {
    init(scope, id, instance);
  }
  
  
  public Symbol(Scope scope, String id)
  {
    init(scope, id, null);
  }
  
  
  public Location getDefinitionLocation()
  {
    if (_location != null) return _location; else return new Location();
  }
   
  public void setDefinitionLocation(Location value)
  {
    _location = value; 
  }
  
  
  protected void init(Scope scope, String id, Object instance)
  {
    Debug.Assert(id != null);
    _location = null;
    _scope = scope;
    _instance = instance;
    
    _entries = scope.lookup(id, null, null, instance);
  }
  

  // value from the location associated with this symbol
  public Object getValue() 
  {
    Debug.Assert(exists(),"symbol doesn't exist");
    
    // if dealing with a property, call getValue instead
    if (getEntry().isProperty())
      return getValue(new FuncInfo(), new Object[0]);  
    
    if (isAmbiguous()) 
      ScigolTreeParser.semanticError(_location, "the name '"+getName()+"' is ambiguous in the current context. Candidates are:\n"+entriesToString());
    
//Debug.WriteLine("symbol lookup of "+_id+" got "+_scope.value(_instance,_id));
    Entry _entry = _entries[0];
    
    if (_entry.scope.isClassScope()) { // class memer
      if (!_entry.isStatic())
        Debug.Assert(_instance != null, "need an instance for instance member access to "+_entry.name+" in scope:\n"+_scope);

      return Class.getMemberValue(_entry, _instance);
    }
    else { // local or namespace variable
      return _entry.getStaticValue();
    }
  }
  
  
  public void setValue(Object value) 
  {
    Debug.Assert(exists(),"symbol doesn't exist");

    // is dealing with a property, call setValue instead
    if (getEntry().isProperty()) {
      setValue(new FuncInfo(), new Object[0], value);
      return;
    }

    if (isAmbiguous()) 
      ScigolTreeParser.semanticError(_location, "the name '"+getName()+"' is ambiguous in the current context. Candidates are:\n"+entriesToString());
    
//Debug.WriteLine("Symbol: symbol "+_id+" value set to "+value);//!!!
    Entry _entry = _entries[0];

    if (_entry.scope.isClassScope()) {
      if (!_entry.isStatic())
        Debug.Assert(_instance != null, "need an instance for instance member access to "+_entry.name+" in scope:\n"+_scope);

      Class.setMemberValue(_entry, value, _instance);       
    }
    else { // local or namespace variable
      _entry.setStaticValue( value );
    }
      
  }

  
  
  
  
  // for properties that require args
  public Object getValue(FuncInfo callSig, Object[] args)
  {
    Debug.Assert(exists(),"symbol doesn't exist");
    Debug.Assert(_entries[0].isProperty(), "symbol isn't a property!");
    
    Entry entry = _entries[0];

    if (isAmbiguous()) {
//!!!! move disambiguation into Class      
      // disambiguate based on getter call signature
      String propName = _entries[0].name;
      String accessorName = FuncInfo.accessorName(propName, true);
      FuncInfo accessorCallSig = callSig.accessorSig("get", TypeSpec.anyTypeSpec);
      
      Entry[] entries = _scope.lookup(accessorName, accessorCallSig, args, _instance);

      if (entries.length>1) // still
        ScigolTreeParser.semanticError(_location, "the property '"+getName()+"' is ambiguous in the current context. Candidates are:\n"+entriesToString());
      else if (entries.length == 0)
        ScigolTreeParser.semanticError(_location, "a property '"+getName()+"' compatible with the call signature "+accessorCallSig+" could not be found in the current context. Candidates are:\n"+entriesToString());
      
      Entry accessorEntry = entries[0];
      
      // get back to a property entry from the set accessor entry
      entry = accessorEntry.propertyEntry;
    }
    
    return Class.getPropertyValue(entry, callSig, args, _instance);
  }


  // for properties that require args
  public void setValue(FuncInfo callSig, Object[] args, Object value) 
  {
    Debug.Assert(exists(),"symbol doesn't exist");
    Debug.Assert(_entries[0].isProperty(), "symbol isn't a property!");
    
    Entry entry = _entries[0];

    if (isAmbiguous()) {
//!!!! move disambiguation into Class      
      // disambiguate based on setter call signature
      String propName = _entries[0].name;
      String accessorName = FuncInfo.accessorName(propName, false);
      FuncInfo accessorCallSig = callSig.accessorSig("set", TypeSpec.typeOf(value));
      Object[] accessorArgs = new Object[args.length+1];
      for(int i=0; i<args.length;i++) accessorArgs[i] = args[i];
      accessorArgs[args.length] = value;
      
      Entry[] entries = _scope.lookup(accessorName, accessorCallSig, accessorArgs, _instance);

      if (entries.length>1) // still ambiguous
        ScigolTreeParser.semanticError(_location, "the property '"+getName()+"' is ambiguous in the current context. Candidates are:\n"+entriesToString());
      else if (entries.length == 0)
        ScigolTreeParser.semanticError(_location, "a property '"+getName()+"' compatible with the call signature "+accessorCallSig+" could not be found in the current context. Candidates are:\n"+entriesToString());

      Entry accessorEntry = entries[0];
      
      // get back to a property entry from the set accessor entry
      entry = accessorEntry.propertyEntry;
    }
    
    Class.setPropertyValue(entry, callSig, args, value, _instance);
  }
  
  
  
  
  // for properties that require args
  public TypeSpec getType(FuncInfo callSig, Object[] args)
  {
    Debug.Assert(exists(),"symbol doesn't exist");
    Debug.Assert(_entries[0].isProperty(), "symbol isn't a property!");
    
    Entry entry = _entries[0];

    if (isAmbiguous()) {
//!!!! move disambiguation into Class      
      // disambiguate based on getter call signature
      String propName = _entries[0].name;
      String accessorName = FuncInfo.accessorName(propName, true);
      FuncInfo accessorCallSig = callSig.accessorSig("get", TypeSpec.anyTypeSpec);
      
      Entry[] entries = _scope.lookup(accessorName, accessorCallSig, args, _instance);

      if (entries.length>1) // still
        ScigolTreeParser.semanticError(_location, "the property '"+getName()+"' is ambiguous in the current context. Candidates are:\n"+entriesToString());
      else if (entries.length == 0)
        ScigolTreeParser.semanticError(_location, "a property '"+getName()+"' compatible with the call signature "+accessorCallSig+" could not be found in the current context. Candidates are:\n"+entriesToString());

      Entry accessorEntry = entries[0];
      Debug.Assert(accessorEntry.isAccessor(), "prop isn't connected to an accessor!");

      // get back to a property entry from the set accessor entry
      entry = accessorEntry.propertyEntry;
    }

    return entry.type;
  }

  
  
  
  
  public TypeSpec getType()
  {
    Debug.Assert(exists(),"symbol doesn't exist");
    
    if (isAmbiguous()) {
      ScigolTreeParser.semanticError(_location, "the name '"+getName()+"' is ambiguous in the current context. Candidates are:\n"+entriesToString());
    }

    Entry entry = _entries[0];
    return entry.type;
  }
  
  
  
  
  
  
  
  public Entry getEntry()
  {
    Debug.Assert(exists(), "can't get entry of non-existent symbol");
    Debug.Assert(!isAmbiguous(), "can't get entry until overload resolution performed");
    return _entries[0];
  }
  
  
  
  
  
  
  // if symbol refers to multiple methods with the same id, try to select one
  //  (if args is non-null use the arg values to match)
  public void disambiguate(FuncInfo callSig, Object[] args)
  {
    if (!isAmbiguous()) return;

    String name = _entries[0].name;
    
    _entries = _scope.lookup(name, callSig, args, _instance);

    if (_entries.length != 1) {
      if (_entries.length == 0)
        ScigolTreeParser.semanticError(_location, "the name '"+name+"' cannot be found with a signature matching "+callSig);
      ScigolTreeParser.semanticError(_location, "the name '"+name+"' is ambiguous when called with signature "+callSig);
    }
  }
  
  
  // convenience of above
  public void disambiguate(FuncInfo callSig, ArrayList args)
  {
    Object[] aargs = new Object[args.size()];
    for(int a=0; a<args.size();a++)
      aargs[a] = args.get(a);
    disambiguate(callSig, aargs);
  }
  
  
  
  // returns true if the symbol potentially refers to multiple locations (e.g. an overloaded function)
  public boolean isAmbiguous()
  {
    return (_entries.length > 1);
  }
  
  
  public boolean exists()
  {
    return (_entries.length > 0);
  }
  
  
  
  public Scope getScope()
  {
    return _scope; 
  }
  
  
  // get the symbol name
  public String getName()
  {
    return _entries[0].name; 
  }
  
  
  public Object getInstance()
  {
    return _instance; 
  }
  
  
  public String toString()
  {
    if (_location != null)
      return (_location.isKnown()?"["+_location.toString()+"] ":"")+getName()+":"+getType()+" = "+getValue();
    else
      return getName()+":"+getType()+" = "+getValue();
  }

  // make a string which lists the entries in _entries, for providing candiates in error messages
  protected String entriesToString() 
  {
    String err="";
    for(int c=0; c<_entries.length;c++) {
      Entry e = _entries[c];
      err += " "+e.name;
      if ((e.location!=null) && (e.location.isKnown())) err+= "["+e.location.toString()+"]";
      err += " :"+e.type;
      if (e.scope.isClassScope())
        err += " in "+((ClassScope)e.scope).getClassType();
      if (c!=_entries.length-1) err += "\n";
    }
    return err;    
  }

  
  protected Scope _scope; // scope in which name defined
  protected Entry[] _entries; // all entries having the same name (but possibly different declaring scopes)
  protected Object _instance;
  protected Location _location;
}

