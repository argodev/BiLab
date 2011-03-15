package scigol;

//import antlr.collections.AST;

import java.util.*;
//import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/// internal holder for a value of a class type
public class Class 
{
  
  /// create Local type class
  public Class(ClassInfo info, ArrayList members)
  {
    _classType = ClassType.Local;
    _info = info;
    _members = members;
    for(Object o : _members) Debug.Assert(!(o instanceof Value), "members can't be Values");
  }
  
  
  /// create External type class
  public Class(ClassInfo info, Object value)
  {
    Debug.Assert(!(value instanceof Value),"must be an Object (not a Value)");
    _classType = ClassType.External;
    _info = info;
    _sysValue = value;
    _scope = null;
  }

  
  
  // helpers for calling property accessors
  
  protected static void setLocalProperty(Entry e, FuncInfo callSig, Object[] args, Object value, scigol.Class instance) 
  {
    Debug.Assert((instance != null) || (e.isStatic()), "instance required for non-static/const properties");
    ClassScope classScope = (ClassScope)e.scope;
    
    if (!e.hasSetter())
      ScigolTreeParser.semanticError("the value of property '"+e.name+"' of class "+((ClassScope)e.scope).getClassType()+"' cannot be set (no set accessor)");

    String accessorName = FuncInfo.accessorName(e.name,false);
//!!! need to do the accessor func overload resolution here - don't just choose [0]   
//!!! propably need some connection from a property Entry to it's accessor Entries and vice-versa 
    Entry accessorEntry = classScope.getClassType().getClassInfo().getDeclaredEntries(accessorName)[0];
        
    // add the 'value' arg to the callSig & args
    FuncInfo setterCallSig = callSig.accessorSig("set", e.type);
    Object[] setterArgs = new Object[args.length+1];
    for(int i = 0; i < args.length; i++) setterArgs[i] = args[i];
    setterArgs[args.length] = value;
    
    Func accessor = (Func)getMemberValue(accessorEntry, instance);
    
    Object[] convertedArgs = accessor.getInfo().convertParameters(setterCallSig, setterArgs, false);

    accessor.call(instance, convertedArgs);
  }
  
  
  
  protected static Object getLocalProperty(Entry e, FuncInfo callSig, Object[] args, scigol.Class instance) 
  {
    Debug.Assert((instance != null) || (e.isStatic()), "instance required for non-static/const properties");
    ClassScope classScope = (ClassScope)e.scope;
    
    if (!e.hasGetter())
      ScigolTreeParser.semanticError("the value of property '"+e.name+"' of class "+((ClassScope)e.scope).getClassType()+"' cannot be read (no get accessor)");

    String accessorName = FuncInfo.accessorName(e.name,true);
    Entry accessorEntry = classScope.getClassType().getClassInfo().getDeclaredEntries(accessorName)[0];
    
    Func accessor = (Func)getMemberValue(accessorEntry, instance);

    Object[] convertedArgs = accessor.getInfo().convertParameters(callSig, args, false);
    
    Object getterRet = accessor.call(instance, convertedArgs);
    
    // now check that the getter returned the appropriate type
    TypeSpec getterRetType = TypeSpec.typeOf(getterRet);
    if (!TypeManager.existsImplicitConversion(getterRetType, e.type, new Value(getterRet)))
      ScigolTreeParser.semanticError("the 'get' accessor for property '"+e.name+"' of class "+((ClassScope)e.scope).getClassType()
                                       +"' should evaluate to the property type '"+e.type+"', not type '"+getterRetType+"'"); 

    getterRet = TypeManager.performImplicitConversion(getterRetType, e.type, new Value(getterRet)).getValue();
                                       
    return getterRet;
  }
  
  
  
  
  protected static void setExternalProperty(Entry e, FuncInfo callSig, Object[] args, Object value, Object sysValue)
  {
    // get the type from the instance, or if static get the entry's declaring type
    java.lang.Class type = (sysValue!=null)?sysValue.getClass():null;
    if (type==null) { // static
      Debug.Assert(e.scope instanceof ClassScope, "property isn't in a class!");
      type = (java.lang.Class)((ClassScope)e.scope).getClassType().getSysType();
    }

    Entry.EntryPair ep = (Entry.EntryPair)e.getStaticValue();
    if (ep.setter == null)
      ScigolTreeParser.semanticError("the value of property '"+e.name+"' of class '"+(new ClassInfo(type))+"' cannot be set (no set accessor)");

    // add the 'value' arg to the callSig & args
    FuncInfo setterCallSig = callSig.accessorSig("set", e.type);
    Object[] setterArgs = new Object[args.length+1];
    for(int i=0; i<args.length; i++) setterArgs[i] = args[i];
    setterArgs[args.length] = value;

    Func setter = (Func)ep.setter.getStaticValue();
    Object[] convertedArgs = setter.getInfo().convertParameters(setterCallSig, setterArgs, true);

    setter.call(sysValue, convertedArgs);
  }
  
  
  
  protected static Object getExternalProperty(Entry e, FuncInfo callSig, Object[] args, Object sysValue)
  {
    // get the type from the instance, or if static get the entry's declaring type
    java.lang.Class type = (sysValue!=null)?sysValue.getClass():null;
    if (type==null) { // static
      Debug.Assert(e.scope instanceof ClassScope, "property isn't in a class!");
      type = (java.lang.Class)((ClassScope)e.scope).getClassType().getSysType();
    }

    Entry.EntryPair ep = (Entry.EntryPair)e.getStaticValue();
    if (ep.getter == null)
      ScigolTreeParser.semanticError("the value of property '"+e.name+"' of class '"+(new ClassInfo(type))+"' cannot be read (no get accessor)");

    Func getter = (Func)ep.getter.getStaticValue();
    
    Object[] convertedArgs = getter.getInfo().convertParameters(callSig, args, true);
    
    Object getterRet = getter.call(sysValue, convertedArgs);
    
    // now check that the getter returned the appropriate type
    TypeSpec getterRetType = TypeSpec.typeOf(getterRet);
    if (!TypeManager.existsImplicitConversion(getterRetType, e.type, new Value(getterRet)))
      ScigolTreeParser.semanticError("the 'get' accessor for property '"+e.name+"' of class "+(new ClassInfo(type))
                                       +"' should evaluate to the property type '"+e.type+"', not type '"+getterRetType+"'"); 

    getterRet = TypeManager.performImplicitConversion(getterRetType, e.type, new Value(getterRet)).getValue();
                                       
    return getterRet;
  }
  
  
  // locate the external Method corresponding to the entry e
  //  (taking into consideration covariant property types)
  protected static Method findExternalPropertyAccessor(java.lang.Class declaringClass, String accessorName, Entry e, FuncInfo callSig, Object[] args)
  {
    // get Method from name & signature
    boolean getAccessor = accessorName.equals("get");
    
    Entry.EntryPair ep = (Entry.EntryPair)e.getStaticValue();
    Entry accessorEntry = null;
    if (getAccessor) {
      if (ep.getter == null) return null; // property has no getter
      accessorEntry = ep.getter;
    }
    else {
      if (ep.setter == null) return null; // property has no setter
      accessorEntry = ep.setter;
    }

    String accessorMethodName = accessorEntry.name;
    java.lang.Class[] argTypes = accessorEntry.type.getFuncInfo().getExternParamTypes();
Debug.WL("static value's type of property accessor entry is:"+accessorEntry.getStaticValue().getClass());
    try {
Debug.Write("looking for accessor "+accessorMethodName+" of class "+declaringClass+" with arg types :");
for(int i=0; i<argTypes.length;i++) Debug.Write("arg"+i+":"+argTypes[i]);
Debug.WL("");
      return declaringClass.getDeclaredMethod(accessorMethodName, argTypes);
    } catch (NoSuchMethodException ex) {
Debug.WL(" not found");
      return null;
    }
  }
  
  
  
  public static Object getPropertyValue(Entry e, FuncInfo callSig, Object[] args, Object instance) 
  {
    Debug.Assert(e!=null);
    Debug.Assert(e.isClassMember(), "e must be a class member");
    Debug.Assert(!e.isAbstract(), "e is an abstract member!");
    Debug.Assert(e.isProperty(),"e must be a property");
    if (args==null) args=new Object[0];
    
    boolean isExternal = ((ClassScope)e.scope).getClassType().getClassInfo().isExternal();
    //boolean isStatic = e.isStatic();

    if (!isExternal) {
      Debug.Assert(instance instanceof Class, "Class instance required");
      return getLocalProperty(e, callSig, args, (scigol.Class)instance);
    }
    else  { // external
      if (instance instanceof scigol.Class) instance = ((scigol.Class)instance).getSysValue();
      return getExternalProperty(e, callSig, args, instance);
    }
    
  }
  
  
  
  public static void setPropertyValue(Entry e, FuncInfo callSig, Object[] args, Object value, Object instance) 
  {
    Debug.Assert(e.isClassMember(), "e must be a class member");
    Debug.Assert(!e.isAbstract(), "e is an abstract member!");
    Debug.Assert(e.isProperty(),"e must be a property");
    if (args==null) args=new Object[0];

    boolean isExternal = ((ClassScope)e.scope).getClassType().getClassInfo().isExternal();
    //boolean isStatic = e.isStatic();

    if (!isExternal) {
      Debug.Assert(instance instanceof Class, "Class instance required");
      setLocalProperty(e, callSig, args, value, (scigol.Class)instance);
    }
    else { // external
      if (instance instanceof Class) instance = ((scigol.Class)instance).getSysValue();
      setExternalProperty(e, callSig, args, value, instance);
    }
  }
  
  
  


  
  
  
  public static Object getMemberValue(Entry e, Object instance) 
  {
    Debug.Assert(e.isClassMember(), "e must be a class member");
    Debug.Assert(!e.isAbstract(), "e is an abstract member!");
    Debug.Assert((instance != null) || e.isStatic(), "instance required");
    
    if (e.isProperty()) 
      return getPropertyValue(e,new FuncInfo(),null, instance);

    
    boolean isExternal = ((ClassScope)e.scope).getClassType().getClassInfo().isExternal();
    boolean isStatic = e.isStatic();

    if (!isExternal) {
      
      if (isStatic) { // static
        return e.getStaticValue();
      }
      else {
        Debug.Assert(e.index>=0, "inherited Java members can't currently be accessed in local classes (unimplemented)");
        Debug.Assert(instance instanceof Class,"Class instance required");
        return ((scigol.Class)instance)._members.get(e.index);
      }
    }
    else { // external
      
      if (instance instanceof scigol.Class) instance = ((scigol.Class)instance).getSysValue();
                                  
      TypeSpec declaringClassType = ((ClassScope)e.scope).getClassType();
      java.lang.Class sysClass =  (java.lang.Class)declaringClassType.getSysType();

      if (e.isField()) { // field
        
        if (e.type.isType()) {
          Debug.Unimplemented("nested types");
        }
        

        // get Field from name
        try {
          Field field = sysClass.getField(e.name);
        
          return field.get(instance);
          
        } catch (java.lang.NoSuchFieldException ex) {
          Debug.Assert(false,"unable to find field "+e.name+" of extern type '"+sysClass.getName()+"'");
        } catch (java.lang.IllegalAccessException ex) {
          ScigolTreeParser.semanticError("illegal access for field '"+e.name+"' of '"+sysClass.getName()+"' - "+ex.getMessage());
        }
      }
      else if (e.isMethod()) { // method
        // extern methods are like consts, the value is always in the entry staticValue
        
        Func func = (Func)e.getStaticValue();
        Debug.Assert(func != null, "entry for external method has no Func value!");
        
//        if (!func.isConstructor())
//          ScigolTreeParser.semanticError("can't directly access or call the constructor of class '"+_sysValue.GetType().FullName+"'");

        return func;
      }
      else
        Debug.Unimplemented("unknown member type of type '"+sysClass.getName()+"'");
      return null;
    }
  }
  
  
  
  public static void setMemberValue(Entry e, Object value, Object instance) 
  {
    Debug.Assert(e.isClassMember(), "e must be a class member");
    Debug.Assert(!e.isAbstract(), "e is an abstract member!");
    Debug.Assert((instance != null) || e.isStatic(), "instance required");

    if (e.isProperty()) 
        setPropertyValue(e, new FuncInfo(), null, value, instance);

    boolean isExternal = ((ClassScope)e.scope).getClassType().getClassInfo().isExternal();
    boolean isStatic = e.isStatic();

    if (!isExternal) {
      if (e.isStatic()) { // static
        e.setStaticValue(value);
      }
      else {
        Debug.Assert(e.index>=0, "inherited Java members can't currently be accessed in local classes (unimplemented)");
        Debug.Assert(instance instanceof scigol.Class, "Class instance required");
        ((scigol.Class)instance)._members.set(e.index, value);
      }
    }
    else { // external

      if (instance instanceof scigol.Class) instance = ((scigol.Class)instance).getSysValue();

      TypeSpec declaringClassType = ((ClassScope)e.scope).getClassType();
      java.lang.Class sysClass =  (java.lang.Class)declaringClassType.getSysType();
           
      if (e.isField()) { // field
        
        if (e.type.isType()) {
          ScigolTreeParser.semanticError("external classes have const nested type members, hence cannot be set");
        }
        
        // get Field from name
        try {
          Field field = sysClass.getField(e.name);
        
          field.set(instance, value);
          
        } catch (java.lang.NoSuchFieldException ex) {
          Debug.Assert(false,"unable to find field "+e.name+" of extern type '"+sysClass.getName()+"'");
        } catch (java.lang.IllegalAccessException ex) {
          ScigolTreeParser.semanticError("illegal access for field '"+e.name+"' of '"+sysClass.getName()+"' - "+ex.getMessage());
        }

      }
      else if (e.isMethod()) { // method
        ScigolTreeParser.semanticError("external classes have const method members, hence cannot be set");
      }
      else
        Debug.Unimplemented("unknown member type of type "+sysClass.getName());

    }
  }
  
  
  
  

  public Scope getOuterScope()
  {
    return _scope; 
  }
  

  public ClassInfo getInfo()
  {
    return _info; 
  }
  
  public ArrayList getMemberValues()
  {
    return _members; 
  }
  
  
  public Object getSysValue()
  {
    Debug.Assert(_classType == ClassType.External);
    return _sysValue; 
  }
  
  
  public boolean isExternal()
  {
    return (_classType == ClassType.External); 
  }
  
  
  public String toString() 
  {
    if (_classType == ClassType.External)
      return _sysValue.toString();
    
    return _info.instanceToString(this);
  }
  
  protected Scope _scope; // Scope in which class defined

  protected enum ClassType { Local, External }
  
  protected ClassType _classType; // is the Class a Scigol one (Local) or a general Java one (External)?
  
  // Local classes only
  protected ClassInfo _info;
  protected ArrayList _members; // values of members (in ClassInfo member index order)
  
  // extern classes
  Object _sysValue;
  
}

