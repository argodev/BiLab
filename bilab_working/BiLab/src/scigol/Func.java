package scigol;


import antlr.RecognitionException;
import antlr.collections.AST;

import java.util.*;
import java.lang.reflect.*;


/// internal holder for a value of a function type
public class Func 
{
  
  // create Local type func
  public Func(FuncInfo info, Scope outerScope, ScigolTreeParser treeParser, AST body, AST preCondition, AST postCondition)
  {
    Debug.Assert(info.isFormal(),"can only construct a func value with an info that has formal parameter names");
    Debug.Assert((body==null) || (outerScope != null), "scope required for local (non-null) funcs");

    _funcType = FuncType.Local;
    _info = info;
    _scope = outerScope;
    _treeParser = treeParser;
    _expr = body;
    _pre = preCondition;
    _post = postCondition;
    _isConstructor = false;
  }

  
  // create External type func
  public Func(Type classType, Member member)
  {
    _funcType = FuncType.External;
    _scope = null;
    _classType = classType;
    _member = member;    
    _isConstructor = (member instanceof Constructor);
  }

  

  public Scope getOuterScope()
  {
    return _scope; 
  }
  
  
  public FuncInfo getInfo()
  {
    if (_funcType == FuncType.Local)
      return _info; 
    else
      return new FuncInfo(_member);
  }
  
  public void setInfo(FuncInfo value)
  { 
    if (_funcType == FuncType.Local)
      _info = value; 
    else
      Debug.Unimplemented();
  }

  
  public ScigolTreeParser getParser()
  {
    return _treeParser; 
  }
  
  
  public AST getValue() 
  {
    Debug.Assert(_funcType == FuncType.Local); return _expr;
  }
  
  public void setValue(AST value)
  {
    Debug.Assert(_funcType == FuncType.Local); _expr = value;
  }
  
  
  
  public boolean isExternal()
  {
    return (_funcType == FuncType.External); 
  }
  
  
  public boolean isConstructor()
  {
    return _isConstructor;
  }
  
  public void setIsConstructor(boolean value)
  { 
    _isConstructor = value;
    Debug.Assert(_scope.isClassScope(),"func can't be a constructor if it wasn't defined in a class scope!");
  }
  
  

  // call this Func with specified args (and instance if non-static)
  //  NB: this doesn't do virtual lookup on Local type funcs, only External type funcs
  // The args are required to match the number and type of the formal parameters
  public Object call(Object instance, Object[] args) 
  {
//Debug.WriteLine("entering Func:"+this+" call("+((instance==null)?"nullinst":instance.ToString())
//                  +","+((args==null)?"nullargs":args.ToString()+"["+args.Length+"]")+") - isConstructor="+_isConstructor);    
    if (_funcType == FuncType.Local) {

// if _isConstructor, need to call base constructor somewhere - here??!!!

      // make a new scope for execution of the func body
      Scope callScope = new LocalScope(_scope);

      Scope savedScope = _treeParser.scope;
      _treeParser.scope = callScope;
      
      // set this - if non-static/constructor
      if (instance != null) {
        Debug.Assert(_scope.isClassScope(),"can only have instances of classes");
        callScope.addEntry("this",TypeSpec.typeOf(instance),instance, null, EnumSet.of(TypeSpec.Modifier.Public));
      }

      
      // setup args
      String[] pnames = _info.getParamNames();
      TypeSpec[] ptypes = _info.getParamTypes();

      // check we have an appropriate number of args
      int numArgs = (args==null)?0:args.length;
      Debug.Assert(numArgs == _info.numArgs(), "call must have correct args");
      
      for(int a=0; a<_info.numArgs(); a++) // for each formal parameter
          callScope.addEntry(pnames[a],ptypes[a],args[a],null,EnumSet.of(TypeSpec.Modifier.Public));

        
      // now evaluate the pre condition expression, if any
      TypeSpec boolType = new TypeSpec(TypeSpec.boolType);
      
      if (_pre != null) {
        // interpret pre
        Object preValue = null;
        try {
          preValue = _treeParser.expressionList(_pre,false);
        } catch (antlr.RecognitionException e) {
          ScigolTreeParser.semanticError("error evaluating pre-condition in func "+_info+" - "+e.getMessage(),e);
        }
        if (!(preValue instanceof Value)) preValue = new Value(preValue);
        TypeSpec t = TypeSpec.typeOf(preValue);
        if (!TypeManager.existsImplicitConversion(t, boolType, (Value)preValue))
          ScigolTreeParser.semanticError("a precondition expression must evaluate to type 'bool' (in func "+_info+")");
        boolean pre = ((Boolean)TypeManager.performImplicitConversion(t, boolType, (Value)preValue).getValue()).booleanValue();
        if (!pre)
          ScigolTreeParser.semanticError("unmet pre-condition in func "+_info);
      }
        
        
      Object retValue = new Any(null);  
        
      // if this is a no-op function (a null func), just skip to the post condition expression
      if (_expr != null) {
        
        // interpret
        
        retValue = null;
        try {
          retValue = _treeParser.expressionList(_expr,false);
        } catch (antlr.RecognitionException e) {
          ScigolTreeParser.semanticError("error in function "+_info+" - "+e.getMessage(),e);
        }
        if (retValue instanceof Value) retValue = ((Value)retValue).getValue();
  
        // if this call is a constructor, reutrn the instance, not the _expr value
        if (_isConstructor)
          retValue = instance;
        else {
          if (_info.getReturnType() == null) // if signature has no return, return null (rather than leaking _expr value)
            retValue = null;
          else
            if ((retValue != null) && !TypeManager.existsImplicitConversion(TypeSpec.typeOf(retValue),_info.getReturnType(),new Value(retValue)))
              ScigolTreeParser.semanticError("func "+_info+" evaluated to a value of type '"+TypeSpec.typeOf(retValue)+"' when the return type '"+_info.getReturnType()+"' was expected");
        }
        
      }
      
      
      // now evaluate the post condition expression (with new local, 'value')

      if (_post != null) {
        // inject func body return value as local 'value' for post body
        Value value = new Value(retValue);
        callScope.addEntry("value",TypeSpec.typeOf(value),retValue,null,EnumSet.of(TypeSpec.Modifier.Public));

        // interpret post
        Object postValue = null;
        try {
          postValue = _treeParser.expressionList(_post,false);
        } catch (RecognitionException e) {
          ScigolTreeParser.semanticError("error evaluating post-condition in func "+_info+" - "+e.getMessage(),e);
        }
        if (!(postValue instanceof Value)) postValue = new Value(postValue);
        TypeSpec t = TypeSpec.typeOf(postValue);
        if (!TypeManager.existsImplicitConversion(t, boolType, (Value)postValue))
          ScigolTreeParser.semanticError("a postcondition expression must evaluate to type 'bool' (in func "+_info+")");
        boolean post = ((Boolean)TypeManager.performImplicitConversion(t, boolType, (Value)postValue).getValue()).booleanValue();
        if (!post)
          ScigolTreeParser.semanticError("unmet post-condition in func "+_info);
        
        // fetch back 'value' from callScope, in case post conidition expression modified it
        //  (allows the post condition to change the return value - handy for debugging)
        Entry[] entries = callScope.getEntries("value",null);
        Debug.Assert(entries.length==1,"should be just one 'value' in the local callScope!");
        retValue = entries[0].getStaticValue();
      }
      
//Debug.WriteLine("leaving local Func.call("+((instance==null)?"nullinst":instance.ToString())
//                  +","+((args==null)?"nullargs":args.ToString()+"["+args.Length+"]")+") "+
//                  +" with retValue="+retValue+" (type:"+((retValue!=null)?retValue.GetType().ToString():"null")+")"+" - isConstructor="+_isConstructor);
      
      _treeParser.scope = savedScope;
      return retValue;
      
    }
    else { // external Java call
      
      Object retValue = null;
      if (instance instanceof scigol.Class) instance = ((scigol.Class)instance).getSysValue();

      // if any of the args are external scigol.Class instances, unwrap them
      for(int i=0; i<args.length;i++) {
        if (args[i] instanceof scigol.Class) {
          scigol.Class c = (scigol.Class)args[i];
          if (c.isExternal())
            args[i] = c.getSysValue();
        }
      }
      
      
      try
      {
//Debug.WL("Member=" + _member + " instance=" + instance);//!!!
//for(int i=0; i<args.length;i++) Debug.Write(" arg"+i+":"+args[i].getClass());
//Debug.WL("");

        if (_member instanceof Method)
          retValue = ((Method)_member).invoke(instance, args);
        else
          retValue = ((Constructor)_member).newInstance(args);
//Debug.WriteLine("leaving extern Func.call("+((instance==null)?"nullinst":instance.toString())
//                  +","+((args==null)?"nullargs":args.toString()+"["+args.length+"]")+") "
//                  +" with retValue="+retValue+" of type:"+((retValue!=null)?TypeSpec.typeOf(retValue).toString():"")+" - isConstructor="+_isConstructor);
      } catch (InvocationTargetException e) {
        // rethrow anything we can (essentially unwarps it from InvocationTargetException)
        if (e.getCause() != null) {
          if (e.getCause() instanceof RuntimeException)
            throw (RuntimeException)e.getCause(); // re-throw the cause directly, if known
          if (e.getCause() instanceof java.lang.Error)
            throw (java.lang.Error)e.getCause();
          ScigolTreeParser.semanticError("error invoking method or constructor - "+e.getCause().getMessage());
        }
        ScigolTreeParser.semanticError("error invoking method or constructor - "+e.getMessage());
      } catch (InstantiationException e) {
        ScigolTreeParser.semanticError("cannnot instantiate abstract class "+_member.getDeclaringClass().getName());
      } catch (IllegalAccessException e) {
        ScigolTreeParser.semanticError("access error invoking method or constructor - "+e.getMessage());
      }
      

      return retValue;
    }
  }

  
  // convenience
  public Object call(Object instance, ArrayList args) 
  {
    Object[] aargs = new Object[args.size()];
    for(int a=0; a<aargs.length;a++) aargs[a] = args.get(a);
    return call(instance, aargs);
  }

  
  public String toString()
  {
    String s = getInfo().toString();
    if (_funcType == FuncType.Local) {
      if (_expr != null) 
        s += " {...}";
      else
        s += " {null}";
    }
    else {
      if (_member != null)
        s += " {...}";
      else
      s += " {null}";
    }
    return s;
  }
  
  public String toStringArgs(Object[] args)
  {
    String s = getInfo().toStringArgs(args);
    if (_funcType == FuncType.Local) {
      if (_expr != null) 
        s += " {...}";
      else
        s += " {null}";
    }
    else {
      if (_member != null)
        s += " {...}";
      else
      s += " {null}";
    }
    return s;
  }
  
  
  
  
  protected Scope _scope; // Scope in which func defined

  protected enum FuncType { Local, External };
  
  protected FuncType _funcType; // is the Func a Scigol one or a general CLI one?
  
//  protected Object _instance; // if this func is a non-static method, this is the object instance 
  
  // Local functions only
  protected FuncInfo _info;
  protected ScigolTreeParser _treeParser; // the parser to use to interpret _expr
  protected AST _expr;
  protected AST _pre;  // precondition & postcondition contract bool expressions (or null if none specified)
  protected AST _post;
  protected boolean _isConstructor; // if this func is a class constructor, 
                                 // it needs to ask the claa scope to initialize the object first
  
  // extern functions
  protected Type   _classType;
  protected Member _member;
  
}
