header {
  package scigol;
  
  import java.util.*;
  import java.lang.annotation.*;
}

options {
  language="Java";
}

{
  //  
}



class ScigolTreeParser extends TreeParser;

options {
  importVocab=Scigol;
  defaultErrorHandler = false;     // Don't generate error handlers
}

// class code
{
  public ScigolTreeParser(Scope initialScope, boolean interactive)
  {
    super();
    this.interactive = interactive;
    globalScope = initialScope.getGlobalScope();
    scope = initialScope;
  }
 
 

  public static void semanticError(String msg) 
  {
    throw new ScigolException(msg);
  }
  
  public static void semanticError(String msg, Exception inner)
  {
    throw new ScigolException(msg, inner);
  }
 

 public static void semanticError(Location l, String msg) 
 {
   if ((l != null) && (l.isKnown()))
    throw new ScigolException("["+l+"] "+msg);
   else
     throw new ScigolException(msg);
 }
 
 
 public static Location locationOf(AST near)
 {
    if (near instanceof CommonASTWithLocation) 
      return ((CommonASTWithLocation)near).loc;
    else
      return new Location();
 }
 
 
 public static void semanticError(AST near, String msg) 
  {
    String loc = "";
    if (near instanceof CommonASTWithLocation) {
      CommonASTWithLocation locAST = (CommonASTWithLocation)near;
      if (locAST.loc.isKnown())
        loc += "["+locAST.loc.toString()+"] ";
    }
    throw new ScigolException(loc+msg);
  }
 
 
  public Scope globalScope;
  public boolean  interactive; // are we being run interactively?
  public Scope scope; // current scope 
  
}



program returns [Value v=null]
    : v=expr
    | v=namespaceBody
    ;

    
expr returns [Value v=null]
    : v=expressionList[true]
    | v=listLiteral
    | v=mapLiteral
    | v=namespaceScope
    | v=literal
    | v=arithmeticExpression
    | v=logCall
    | v=declaration
    | v=assignmentExpression
    | v=typeofExpression
    | v=symbol
    | v=applicationExpression
    | v=selectionExpression
    | v=ifExpression
    | v=whileExpression
    | v=forExpression    
    | v=forEachExpression
    | v=exceptionBlock
    | v=throwExpression
    ;

    
listLiteral returns [Value v=null]
    { List l = new List(); }
    : #(LIST (v=expr {l.add(v.getValue());} )* )
    { v = new Value(l); }
    ;
    
mapLiteral returns [Value v=null]
    { Map m = new Map(); Value key=null; Value val=null;}
    : #(MAP (key=expr GIVES val=expr {m.set_Item(key.getValue(), new Any(val.getValue()));} )* )
    { v = new Value(m); }
    ;
    
    
    
selectionExpression returns [Value v=null]
    {
      Value f;
    }
    : #(DOT f=expr id:IDENT)
    {
      String name = id.getText();

      String fid = "";
      // if there is any information regarding the identity of f, capture it to use
      //  as extra information in error messages
      if (f.isLValue()) fid = "'"+f.getLValue().getSymbol().getName()+"' ";
      
      if (f.isNamespaceComponent()) {
        // handle 'selection' of partially or fully qualified name
        //  if still partial, keep as a potential namespace component that can continue
        //  to have selection applied, or if fully qualified type name, retrieve the type
        
        // see if we have a complete qually-qualified name yet
        String component = f.getNamespaceComponentString();
        
        NamespaceScope nsScope = scope.getGlobalScope().getNamespaceScope(component);
        if ((nsScope != null) && nsScope.contains(name)) { // yes, we found something
          Symbol s = new Symbol(nsScope, name, null);
          LValue lv = new LValue(s);
          v = new Value(lv);
        }
        else { // nope, keep as potential qualified name component
          String qualifiedName = component+"."+name;
          
          //!!! temporary way to access external Java type, if there is a type matching
          // the full name loaded, use it
          java.lang.reflect.Type javaType = NamespaceScope.loadedLibrariesGetType(qualifiedName);
          TypeSpec gt = (javaType != null)?new TypeSpec(javaType):null;
          
          if (gt != null) { // yes, found a type
            v = new Value(gt);
          }
          else {
            v = new Value(qualifiedName);
            v.setValueIsNamespaceComponent(locationOf(id));
          }
        }
      }
      else {
        TypeSpec t = TypeSpec.typeOf(f.getValue());
        if (t.isAny()) {
          f = new Value(((Any)f.getValue()).value); // unwrap Any
          t = TypeSpec.typeOf(f.getValue());
        }
        
        if (t.isClass() || t.isBuiltinClass()) {
          // try to select a member
//Debug.WL("selecting member "+name+" from type "+f.getType());  //!!!        
          Scope classScope = new ClassScope(t); // create scope for existing class
          Symbol s = new Symbol(classScope, name, f.getValue());

          if (!s.exists())
            ScigolTreeParser.semanticError(id,"object "+fid+"of type '"+t+"' has no member named '"+name+"'");

          v = new Value(new LValue(s));
        }
        else if (t.isType()) {
          // try to select static field of class type
          
          TypeSpec typeExpr = (TypeSpec)f.getValue();
          if (!typeExpr.isClass()) 
            ScigolTreeParser.semanticError(id,"cannot select a member of object "+fid+"of non-class type '"+t+"'");
          
          Scope classScope = new ClassScope(typeExpr);
          Symbol s = new Symbol(classScope, name, null);
          
          if (!s.exists())
            ScigolTreeParser.semanticError(id,"object "+fid+"of type '"+typeExpr+"' has no member named '"+name+"'");
          
          v = new Value(new LValue(s));
        }
        else
          ScigolTreeParser.semanticError(id,"cannot select member '"+name+"' from object "+fid+"of type '"+t+"'");
      }
    }
    ;
    
    
    
applicationExpression returns [Value v=null]
    {
      Value f;
      ArrayList args=null;
    }
    : #(app:APPLICATION f=expr args=applicationArgs )
    {
      FuncInfo callSig = new FuncInfo(args);
      callSig.setDefinitionLocation( locationOf(app) );

      // first, if we have an lvalue that refers to an overloaded symbol, we need
      //  to resolve it to a single method based on the call signature
      boolean isFunc = false;
      Func func = null;

      // if f is either an rvalue, or an lvalue that isn't ambiguous
      if (!f.isLValue() || (f.isLValue() && !f.getLValue().getSymbol().isAmbiguous())) {

        // property?
        boolean isProperty = false;
        if (f.isLValue()) {
          Symbol s = f.getLValue().getSymbol();
          isProperty = s.getEntry().isProperty();
        }
        
        if (isProperty) {
          isFunc = false;
        }
        else {
          Object fo = f.getValue();
          if (fo != null) {
            isFunc = f.getType().isFunc();
            if (isFunc)
              func = (Func)fo;
          }
          else
            isFunc = true; // null is a no-op function that returns null, by definition
        }
      }
      else { // overloaded lvalue
        
//!!! this doesn't work if the lhs if an overloaded lvalue that is a bound property.
// it is assuming a func, but we can't know that.        
        
        Symbol s = f.getLValue().getSymbol();
//Debug.WriteLine("application of overloaded lvalue");
        s.disambiguate(callSig, args);
        // now we can go ahead and retrieve the func value
        isFunc = true;
        Object fo = f.getValue();
        if (fo != null)
          func = (Func)fo;
      }
      
      
      if (isFunc) { // function call
        
        if (func==null) {
//Debug.WriteLine("applicationExpression: call no-op");  
          v = null; // a null Func is a no-op
        }
        else { // call it
          
          Object[] convertedArgs = func.getInfo().convertParameters(callSig, args, func.isExternal());
//Debug.WriteLine("applicationExpression: call "+func.toStringArgs(convertedArgs));

          // retrieve instance
          Object instance = null;
          if (f.isLValue())
            instance = f.getLValue().getSymbol().getInstance();
          
          v = new Value(func.call(instance, convertedArgs));   
          
        }
        
      }
      else if (f.getType().isType()) {  // construction or explicit conversion? (looks like call of a type)
        TypeSpec t = (TypeSpec)f.getValue();
        
//Debug.WriteLine("constructing a "+t);

        // if there is a single argument, treat it like an explicit conversion (which will also
        //  call any compatible single argument constructors of t)
        v=null;
        if (callSig.numArgs() == 1) 
          v = TypeManager.performExplicitConversion(callSig.getParamTypes()[0],t,new Value(args.get(0)));
        
        if (v == null) // conversion failed, try other constructors matched via callSig
          v = new Value(t.constructValue(callSig,args,this));
        
        Debug.Assert(v!=null, "construction/conversion failed");
      }
      else { // assume property access

        Symbol s = f.getLValue().getSymbol();
        
        boolean isProperty = s.getEntry().isProperty();
        
        if (isProperty) {
          // we have an LValue that refers to a property member
          //  turn it into an property LValue with bound property arguments
//!!! if the property is already arg bound, evaluate it and treat this as another application!
if (f.getLValue().isBoundProperty())
  Debug.Unimplemented("property already bound");
          v = new Value(new LValue(f.getLValue().getSymbol(),callSig,FuncInfo.toArray(args)));
        }
        else {
          // perhaps we have a class, which has an operator() property (an indexer)?
          TypeSpec ftype = f.getType();
          boolean isClass = false;
          if (ftype.isClass() || ftype.isBuiltinClass()) 
            isClass = true;
          else if (ftype.isAny() || ftype.isNum()) {
            f = TypeSpec.unwrapAnyOrNumValue(f);
            ftype = f.getType();
            isClass = (ftype.isClass() || ftype.isBuiltinClass());
          }

          if (isClass) {
            
            // create an LValue for operator() (if it exists), bound with the arguments
            Scope classScope = new ClassScope(ftype); // create scope for class
            Symbol pSymbol = new Symbol(classScope, "operator()", f.getValue());
            
            if (!pSymbol.exists())
              ScigolTreeParser.semanticError(locationOf(app),"object of type '"+ftype+"' has no indexer ('operator()' property) defined, hence cannot be called (with call signature "+callSig+")");

            v = new Value(new LValue(pSymbol,callSig,FuncInfo.toArray(args)));
          }
          else
            ScigolTreeParser.semanticError(locationOf(app),"cannot call an object of type '"+f.getType()+"'.  To be `called` it must be either a type (construction), a func (call), or an object with an 'operator()' property (indexing).");
        }
      }
      
    }
    ;
    

applicationArgs returns [ArrayList args=new ArrayList()]
    { Value e; }
    : #(LPAREN (e=expr {args.add(e);})? 
               (COMMA e=expr { args.add(e);} )* 
       ) 
    ;



    
symbol returns [Value v=null]
    { int esc=0; }
    : (SCOPE_ESCAPE {esc++;} )* 
      id:IDENT 
      {
        String ident = id.getText();
        Scope searchScope = scope;
        int numEscs = esc;
        while (esc-- > 0) if (searchScope.getOuter() != null) searchScope =  searchScope.getOuter();
//Debug.WriteLine("#esc="+ numEscs+" id="+ident);
//Debug.WriteLine("current scope:\n"+scope);
//Debug.WriteLine("current scope.outer=\n"+scope.outer);
//Debug.WriteLine("search scope:\n"+searchScope);
        // get instance if applicable
        Symbol thiss = new Symbol(scope, "this", null);
        Object instance = thiss.exists()?thiss.getValue():null;
        
        Symbol s = new Symbol(searchScope, ident, instance);
        s.setDefinitionLocation( locationOf(id) );

        // check is this is a symbol that refers to a class instance member and complain if
        //  no instance is available (can only do this sucessfully here for non-ambiguous symbols)
        if ((instance == null) && s.exists() && (!s.isAmbiguous())) {
          Entry entry = s.getEntry();
          if (entry.isClassMember() && !entry.isStatic()) {
            TypeSpec declaringClass = ((ClassScope)entry.scope).getClassType();
            ScigolTreeParser.semanticError(id,"the instance member named '"+ident+"' of class '"+declaringClass+"' cannot be accessed without an instance object");
          }
        }
  
//if (s.exists()) {
//  Debug.WriteLine("got symbol "+ident);
//  Debug.WriteLine(" in scope:"+s.scope);
//  Debug.WriteLine("  having type "+s.type);
//}
        if (!s.exists()) { // still haven't found anything
          
          if (numEscs == 0) {
            
            // perhaps this is a global fully-qualified name (i.e. with namespace)
            //  then create a special 'Value' with the component which will be used
            //  via selection '.' to build the full name and then the top level
            //  namespace can be checked
            v = new Value(ident);
            v.setValueIsNamespaceComponent(locationOf(id));
          }
          else
            ScigolTreeParser.semanticError(id,"undefined identifier '"+ident+"' in the specified scope:\n"+searchScope);
        }
        else
          v = new Value(new LValue(s));
      }
    ;



assignmentExpression returns [Value v=null]
    { Value lhs=null, rhs=null; }
    : #(a:ASSIGN lhs=expr rhs=expr)
    {
      if (lhs.isLValue()) {
//        Debug.WriteLine("assigning:"+lhs.getLValue());
//        Debug.WriteLine("  to value:"+rhs);

        // check type & convert if necessary
        LValue l = lhs.getLValue();
        TypeSpec tlhs = l.getType(); 
        TypeSpec trhs = TypeSpec.typeOf(rhs);
        if (TypeManager.existsImplicitConversion(trhs, tlhs, rhs)) 
          lhs.setValue( TypeManager.performImplicitConversion(trhs, tlhs, rhs).getValue() );
        else
          ScigolTreeParser.semanticError(a,"incompatible right-hand-side (RHS) in assignment, value of type '"+trhs+"' is incompatible with LHS type '"+tlhs+"'");

        v = lhs;
      }
      else { // not assignable
        if (lhs.isNamespaceComponent()) {
          
          if (interactive) {
            // If we're in interactive mode, we allow undefined identifiers to be assigned to.
            // In this case, we declare the symbol as type 'any' or type 'num' depending on the
            //  type of the initializer
            String name = lhs.getNamespaceComponentString();
            int dotIndex = name.indexOf('.');
            if (dotIndex == -1) { // only auto declare simple names (not compound names like a.b)
              TypeSpec trhs = TypeSpec.typeOf(rhs.getValue()); 
              TypeSpec tlhs = new TypeSpec(TypeSpec.anyType);// default to 'any'

              if (trhs.isANum()) {
                tlhs = new TypeSpec(TypeSpec.numType);
                if (!(rhs.getValue() instanceof Num))
                  rhs = new Value(new Num(rhs.getValue())); // wrap rhs in Num
              }
              else
                rhs = new Value(new Any(rhs.getValue())); // wrap rhs in Any
              
              scope.addEntry(name, tlhs, rhs.getValue(), null, EnumSet.of(TypeSpec.Modifier.Public));
              v = new Value(new LValue(new Symbol(scope, name, null)));
            }
            else
              ScigolTreeParser.semanticError(a,"unknown name '"+lhs.getNamespaceComponentString()+"' on left-hand-side (LHS) of assignment expression");
          }
          else
            ScigolTreeParser.semanticError(a,"unknown name '"+lhs.getNamespaceComponentString()+"' on left-hand-side (LHS) of assignment expression");
        }
        else
          ScigolTreeParser.semanticError(a,"left-hand-side (LHS) of assignment expression is not assignable (not an lvalue)");
      }
    }
    ;
    
    
typeofExpression returns [Value v=null]
    { Value e=null; }
    : #("typeof" e=expr) 
    {
      if (e==null)
        v = new Value(TypeSpec.anyTypeSpec);
      else {
        if (!TypeSpec.typeOf(e).isType()) {
          // as a special case, if the value is null & it's an LValue, use the symbol's type
          boolean isNull = (e.getValue() == null) || ((e.getValue() instanceof Any) && (((Any)e.getValue()).value == null));
          if (e.isLValue() && isNull) {
            v = new Value( e.getLValue().getSymbol().getType() );
          }
          else {
            if (e.getValue() instanceof Any) // extract from Any
              e = new Value(((Any)e.getValue()).value);
          }
          
          if (v==null) // still no type
            v = new Value(TypeSpec.typeOf(e)); // deduce type from value
        }
        else // is a type, so it's type is type (!)
          v = new Value(TypeSpec.typeTypeSpec);
      }
    }
    ;
    
    
    
literal returns [Value v=null]
    { TypeSpec t; Func f; }
    : v=number
    | v=stringLit
    | v=charLit
    | t=typeLiteral     { v = new Value(t); }
    | f=functionLiteral { v = new Value(f); }
    ;
    

// list of expressions within its own local scope
expressionList[boolean inNewScope] returns [Value v=null]
    { Value a = null; }
    : #(el:EXPRLIST 
         { 
           if (inNewScope) { 
             scope = new LocalScope(scope);
             scope.setDefinitionLocation( locationOf(el) );
           }
         }
         (a=expr {if (a!=null) v=a;} )* 
         { 
           if (inNewScope) scope = scope.getOuter(); // exit scope 
         }         
       ) 
    ;


    
namespaceName returns [String name=""]
  : i:IDENT { name += i.getText();} 
    (DOT i2:IDENT { name += "."+i2.getText();} )* 
  ;
    
    
// list of expressions with a namespace scope    
namespaceScope returns [Value v=null]
    { String id; Scope savedOuterScope;}
    : #(ns:"namespace" 
         id=namespaceName 
         {
           if (!scope.isNamespaceScope())
             ScigolTreeParser.semanticError(locationOf(ns),"can't define namespace '"+id+
                   "' within current scope (namespaces can only be nested directly within other namespaces)");
           
           savedOuterScope = scope;
           scope = NamespaceScope.newOrExistingNamespaceScope(id, scope);
           scope.setDefinitionLocation( locationOf(ns) );
         }
         v = namespaceBody
         { 
           scope = savedOuterScope; // exit back to enclosing scope
         }         
       ) 
    ;
    
    
namespaceBody returns [Value v=null]
    : LCURLY
      (usingDecl)*
      (v=expr)*
    ;
    
    
    
    
usingDecl 
    { String id=null; Value source=null; }
    : #(ul:"using" id=namespaceName ("as" aliasId:IDENT)? ("from" source=stringLit)? )
    {
if (source != null) Debug.WriteLine("using ... from is not implemented!");
      Debug.Assert(scope.isNamespaceScope(), "using can only appear in namespace scope!");

      NamespaceScope nsscope = (NamespaceScope)scope;
      String alias = (aliasId!=null)?aliasId.getText():null;
      String aliased = null;
      String nsName = id;
      
      // if nsName isn't the name of a namespace, perhaps we have 'namespace.name' instead
      if (nsscope.getNamespaceScope(nsName) == null) {
        
        // remove the last . component to separate the namespace (if any) from the name
        int lastDot = id.lastIndexOf('.');
        if (lastDot != -1) {
          nsName = id.substring(0,lastDot);
          aliased = id.substring(lastDot+1);
//Debug.WL("nsName="+nsName);
//Debug.WL("aliased="+aliased);          
        }
      }
      
      if (alias==null) { // using namespace[.name]
        if (aliased == null) {
          nsscope.addUsingNamespace(nsName);
//Debug.WriteLine("using namespace="+nsName);
        }
        else {
          nsscope.addUsingName(aliased, nsName);
//Debug.WriteLine("using namespace="+nsName+" name="+aliased);
        }
      }
      else { // using alias
        if (aliased == null) {
          aliased = nsName;
          nsName = nsscope.fullNamespaceName();
        }
        
        nsscope.addUsingAlias(alias, aliased, nsName);
//Debug.WriteLine("using alias="+alias+" aliased="+aliased+" namespace="+  nsName);
      }
    }
    ;
    
    
    
    
arithmeticExpression returns [Value v=null]
    { Value lhs = null,rhs; }
    : #(PLUS  lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator+", lhs, rhs); }
    | #(MINUS lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator-", lhs, rhs); }
    | #(UNARY_PLUS  rhs=expr)    { v = Math.performOverloadedOperation("operator+", null, rhs); }
    | #(UNARY_MINUS rhs=expr)    { v = Math.performOverloadedOperation("operator-", null, rhs); }
    | #(STAR  lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator*", lhs, rhs); }
    | #(DIV   lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator/", lhs, rhs); }
    | #(MOD   lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator%", lhs, rhs); }
    | #(HAT   lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator^", lhs, rhs); }
    | #(EQUAL lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator==", lhs, rhs); }
    | #(NOT_EQUAL lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator!=", lhs, rhs); }
    | #(LTHAN lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator<", lhs, rhs); }
    | #(GTHAN lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator>", lhs, rhs); }
    | #(LTE   lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator<=", lhs, rhs); }
    | #(GTE   lhs=expr rhs=expr) { v = Math.performOverloadedOperation("operator>=", lhs, rhs); }
    | v=prefixExpression
    | v=postfixExpression
    | v=logicalAndExpression
    | v=logicalOrExpression
    | v=logicalNotExpression
    | v=isExpression
    | v=rangeExpression
    | v=normExpression
    | v=cardinalityExpression
    {
      Debug.Assert(v != null, "Value is null"); 
    }
    ;
    
    
    
rangeExpression returns [Value v=null]
    { Value lhs=null; Value rhs=null; }
    : #(dd:DOTDOT (lhs=expr rhs=expr)? )
    {
      if (lhs==null) lhs = new Value(0);
      if (rhs==null) rhs = new Value(-1);
      
      lhs.rvalue();
      rhs.rvalue();
      
      TypeSpec tlhs = TypeSpec.typeOf(lhs);
      TypeSpec trhs = TypeSpec.typeOf(rhs);
      TypeSpec intType = new TypeSpec(TypeSpec.intType);
      
      if (   !TypeManager.existsImplicitConversion(tlhs, intType, lhs)
          || !TypeManager.existsImplicitConversion(trhs, intType, rhs) )
        ScigolTreeParser.semanticError(locationOf(dd),"both the RHS and LHS of the range operator '..' must be compatible with type 'int'");
      
      int from = ((Integer)TypeManager.performImplicitConversion(tlhs, intType, lhs).getValue()).intValue();
      int to   = ((Integer)TypeManager.performImplicitConversion(trhs, intType, rhs).getValue()).intValue();
      
      if (  ((from<0) && (to<0) && !(from<=to))
          ||((from>0) && (to>0) && !(from<=to)) )
        ScigolTreeParser.semanticError(locationOf(dd),"invalid range '"+from+".."+to+"'");
      
      v = new Value(new Range(from, to));
    }
    ;    
    
    
    
isExpression returns [Value v=null]
    { Value lhs = null,rhs; boolean neg=false;}
    : (   #("is" lhs=expr rhs=expr)
        | #("isnt" lhs=expr rhs=expr { neg=true; } )
      )
    {
      //!!! may be able to do away with much of this any logic if Value (or something) takes care
      //  of Any extraction automatically
      
      TypeSpec anyType = new TypeSpec(TypeSpec.anyType);

      // if lhs is null it can only be identical with a null rhs
      if ((lhs.getValue() == null) || (TypeSpec.typeOf(lhs).equals(anyType) && (((Any)lhs.getValue()).value == null))) {
        // if rhs is not null, check if it is an Any and if so, look inside for a null value
        if (rhs != null) {
          TypeSpec trhs = TypeSpec.typeOf(rhs);
          if (trhs.equals(anyType)) { // extract from Any
            rhs = new Value(((Any)rhs.getValue()).value);
            trhs = TypeSpec.typeOf(rhs);
          }
          if (trhs.isType() && rhs.getValue().equals(anyType)) // 'is any'
            v = new Value(true);
          else
            v = new Value(rhs.getValue() == null); // lhs & rhs null (rhs was Any(null))
        }
        else
          v = new Value(true); // lhs & rhs null
      }
      else {
        // similarly, a null rhs can only be identical with a null lhs
        if ((rhs.getValue() == null) || (TypeSpec.typeOf(rhs).equals(anyType) && (((Any)rhs.getValue()).value == null))) {
          v = new Value(   (lhs.getValue() == null) 
                        || (   TypeSpec.typeOf(lhs).equals(anyType) 
                            && (((Any)lhs.getValue()).value == null)) );
        }
        else {
          // OK, both sides are non-null (but may be Any's, if so extract their values)
          TypeSpec tlhs = TypeSpec.typeOf(lhs);
          TypeSpec trhs = TypeSpec.typeOf(rhs);

          if (tlhs.equals(anyType)) {
            lhs = new Value(((Any)lhs.getValue()).value);
            tlhs = TypeSpec.typeOf(lhs);
          }
          if (trhs.equals(anyType)) {
            rhs = new Value(((Any)rhs.getValue()).value);
            trhs = TypeSpec.typeOf(rhs);
          }
          
          // finally, if rhs is of type 'type', do a type comparison, else do identity
          if (trhs.isType()) {
            if (rhs.getValue().equals(anyType))
              v = new Value(true); // 'is any'
            else
              v = new Value(TypeSpec.typeOf(lhs).equals(rhs.getValue())); // lhs is-a rhs?
          }
          else 
            v = new Value(lhs.getValue() == rhs.getValue()); // is lhs the same object instance as rhs? i.e. identity, not equality
        }
      }
      
      if (neg)
        v = new Value( new Boolean((!((Boolean)v.getValue()).booleanValue())) ); // flip sense for isnt
      
      Debug.Assert(v.getValue() instanceof Boolean);
    }
    ;
    
    
logicalOrExpression returns [Value v=null]
    { Value lhs; }
    :#("or" lhs=expr /*rhs=expr*/) // short-curcuit (lazy) evaluation
    { 
      if (Math.isLogicalTrue(lhs)) 
        v = new Value(true);
      else {
        AST logOrAST = #logicalOrExpression;
        AST rhsAST = logOrAST.getFirstChild().getNextSibling(); // skip over lhs
        Value rhs = expr(rhsAST);
        v = new Value(Math.isLogicalTrue(rhs));
      }
    } 
    ;     
    

normExpression returns [Value v=null]
    { Value e=null;}
    : #(bl:BAR e=expr)
    {
      if (e.getValue() == null)
        ScigolTreeParser.semanticError(locationOf(bl),"null value in operator|| (norm)");
      
      v = Math.performOverloadedOperation("operator||", e, null);
    }
    ;

    
cardinalityExpression returns [Value v=null]
    { Value e=null;}
    : #(HASH e=expr)
    {
      if (e.getValue() == null) 
        v = new Value(new Integer(0));
      else
        v = Math.performOverloadedOperation("operator#", null, e);
    }
    ;
    
    
logicalAndExpression returns [Value v=null]
    { Value lhs; }
    :#("and" lhs=expr /*rhs=expr*/) // short-curcuit (lazy) evaluation
    { 
      if (!Math.isLogicalTrue(lhs)) 
        v = new Value(new Boolean(false));
      else {
        AST logAndAST = #logicalAndExpression;
        AST rhsAST = logAndAST.getFirstChild().getNextSibling(); // skip over lhs
        Value rhs = expr(rhsAST);
        v = new Value(Math.isLogicalTrue(rhs));
      }
    } 
    ;     
    
    
logicalNotExpression returns [Value v=null]
    { Value rhs; }
    : (
         #(LNOT  rhs=expr)
       | #("not" rhs=expr)
      )
    {
      if (Math.isLogicalTrue(rhs))
        v = new Value(new Boolean(false));
      else
        v = new Value(new Boolean(true));
    }
    ;
    
    
    
prefixExpression returns [Value v=null]
    { Value e=null; boolean dec=false;}
    : (   #(INC e=expr)
        | #(DEC e=expr { dec=true; } )
      )
    {
      Debug.Unimplemented("prefix");
    }
    ;

postfixExpression returns [Value v=null]
    { Value e=null; boolean dec=false; boolean prime=false;}
    : (   #(POST_INC e=expr)
        | #(POST_DEC e=expr { dec=true; } )
        | #(PRIME    e=expr { prime=true; } )
      )
    {
      if (prime) 
        v = Math.performOverloadedOperation("operator'", e, null);
      else {
        // call appropriate operator (++/--)
        Debug.Unimplemented("post ++/--");
      }
    }
    ;
    

indexing returns [ArrayList indexValues=null]
    { Range r1=null; Range r2=null; }
    : #(LPAREN r1=eltRange (r2=eltRange)? )
    ;

eltRange returns [Range r=new Range()]
    { Value e=null; }
    : #(COLON (e=expr)* )
    ;
    




    
    
ifExpression returns [Value v=null]
    { Value test = null; } 
    : #(ift:"if" test=expr /*then=expr else=expr*/) // don't walk 'then' or 'else' exprs automatically
    {
      TypeSpec t = TypeSpec.typeOf(test);
      if (!TypeManager.existsImplicitConversion(t,TypeSpec.boolTypeSpec,test))
        ScigolTreeParser.semanticError(ift,"'if' test expression must be of type 'bool'");
      boolean btest = ((Boolean)TypeManager.performImplicitConversion(t, TypeSpec.boolTypeSpec, test).getValue()).booleanValue();
      AST ifAST = #ifExpression;
      AST thenAST = ifAST.getFirstChild().getNextSibling(); // skip over test
      AST elseAST = thenAST.getNextSibling();

      // conditionally walk either the then or else tree
      if (btest)
        v = expr(thenAST);
      else {
        if (elseAST != null)
          v = expr(elseAST);
      }
    }
    ;
    

whileExpression returns [Value v=null]
    { Value test = null; }    
    : #(w:"while" test=expr /*body=expr*/) // don't walk body expr automatically
    {
      AST whileAST = #whileExpression;
      AST testAST = whileAST.getFirstChild();
      AST bodyAST = whileAST.getFirstChild().getNextSibling(); // skip over test

      TypeSpec t = TypeSpec.typeOf(test);
      if (!TypeManager.existsImplicitConversion(t,TypeSpec.boolTypeSpec,test))
        ScigolTreeParser.semanticError(w,"'while' test expression must be of type 'bool'");
      boolean btest = ((Boolean)TypeManager.performImplicitConversion(t, TypeSpec.boolTypeSpec, test).getValue()).booleanValue();
      
      // keep executing the body tree while the test is true
      while (btest) {
        v = expr(bodyAST);
        
        // re-evaluate test
        test = expr(testAST); // execute test
        t = TypeSpec.typeOf(test);
        if (!TypeManager.existsImplicitConversion(t,TypeSpec.boolTypeSpec, test))
          ScigolTreeParser.semanticError(w,"'while' test expression must be of type 'bool'");
        btest = ((Boolean)TypeManager.performImplicitConversion(t, TypeSpec.boolTypeSpec, test).getValue()).booleanValue();
      }
    }
    
    | #(d:"do" v=expr test=expr) 
    {
      AST whileAST = #whileExpression;
      AST bodyAST = whileAST.getFirstChild();
      AST testAST = whileAST.getFirstChild().getNextSibling(); // skip over body
      
      TypeSpec t = TypeSpec.typeOf(test);
      if (!TypeManager.existsImplicitConversion(t,TypeSpec.boolTypeSpec,test))
        ScigolTreeParser.semanticError(d,"'do...while' test expression must be of type 'bool'");
      boolean btest = ((Boolean)TypeManager.performImplicitConversion(t, TypeSpec.boolTypeSpec, test).getValue()).booleanValue();
      
      // body & test have already been executed once automatically, now
      //  re-execute them again so long as test is true
      if (btest)
        do {
          v = expr(bodyAST);
          
          // re-evaluate test
          test = expr(testAST); // execute test
          t = TypeSpec.typeOf(test);
          if (!TypeManager.existsImplicitConversion(t,TypeSpec.boolTypeSpec,test))
            ScigolTreeParser.semanticError(d,"'do...while' test expression must be of type 'bool'");
          btest = ((Boolean)TypeManager.performImplicitConversion(t, TypeSpec.boolTypeSpec, test).getValue()).booleanValue();

        } while (btest);
    }
    ;

    
    
forExpression returns [Value v=null]
   { Value first = null; Value test = null; Value third=null;}
   : #("for" first=expr test=expr third=expr /*body=expr*/)  // don't walk body expr automatically
   {
     Debug.Unimplemented();
   }
   ;
    
    

// !!! consider notation foreach( x:string(i:int) in <object-with-int-indexer-of-strings> ) {..}
//                   or  foreach( x:string in <object-with-indexer-or-iterator-of-strings) {..}    
forEachExpression returns [Value v=null]
   { 
     Value t=null; 
     Value from=null; Value to=null; Value by=null;
     Value container=null;
   }
   : #("foreach" i:IDENT t=expr ( #(COLON from=expr to=expr (by=expr)? ) | container=expr ) /*body=expr*/)
   {
     Debug.Unimplemented();
   }
   ;    
    
    
    
    
exceptionBlock returns [Value v=null]
   { Value a=null;}
   : "try" /*tryExpr=expr "catch" (exceptId:IDENT COLON exceptType=expr)? catchExpr=expr*/
   {     
     AST blockAST = #exceptionBlock;
     AST tryBlockAST = blockAST.getFirstChild().getFirstChild();
     AST ast = blockAST.getFirstChild().getNextSibling();
     
     
     Debug.WriteLine("blockAST=\n"+blockAST.toStringTree());
     Debug.WriteLine("\nast=\n"+ast.toStringTree());
     //...
   }
   ;
    
    
throwExpression returns [Value v=null] // actually doesn't return - it throws a Java Exception directly
   { Value e=null; }
   : #(th:"throw" e=expr)
   {
     Object o = e.getValue();
     if (o instanceof Any) o = ((Any)o).value;
     if (!(o instanceof Exception))
       ScigolTreeParser.semanticError(th,"only exceptions can be thrown (not objects of type '"+TypeSpec.typeOf(e)+"')");
     
     // throw it
Debug.Unimplemented();     
     //throw ((Exception)o);
   }
   ;
      
    
    
qualifiedIdent returns [String name=""]
    : i:IDENT { name = i.getText(); }
      (i2:IDENT { name += "."+i2.getText(); } )*
    ;
    
    
annotation returns [ScigolAnnotation a=null]
    { 
      String annotName = null;
      ArrayList args = null; 
    }
	: #(ANNOT_START annotName=qualifiedIdent (args=applicationArgs)?)
	{
	
	  java.lang.reflect.Type javaType = NamespaceScope.loadedLibrariesGetType(annotName);
	  TypeSpec annotType = (javaType!=null)?new TypeSpec(javaType):null;
	  
	  if (annotType==null) 
	    ScigolTreeParser.semanticError("the annotation type '"+annotName+"' could not be found in the current scope");
	  else if (!annotType.isInterface() || !annotType.isA(new TypeSpec(Annotation.class)))
	    ScigolTreeParser.semanticError("the type '"+annotName+"' specified as an annotation must implement 'Annotation'");

      // now construct a ScigolAnnotation to hold the parameters
      a = new ScigolAnnotation(annotType);
      a.setMembers(args);
	}
	;    
    
    
declaration returns [Value v=null]
    { TypeSpec t = null; 
      Value e=null;
      Annotation annot=null; 
      LinkedList<Annotation> annotations = new LinkedList<Annotation>();
      EnumSet<TypeSpec.Modifier> modifiers = EnumSet.of(TypeSpec.Modifier.Public);
    }
    : #("let" (annot=annotation { annotations.add(annot); } )* 
        (modifiers=classModifiers)? i:IDENT { scope.pushDeclarationIdent(i.getText()); } 
        (tt:COLON t=typeExpression)? 
        (at:ASSIGN e=expr)?
      ) 
    {
//!!! do something with the modifiers

      scope.popDeclarationIdent();

      String id = i.getText();

      boolean typeSupplied = (t!=null);
      boolean initializerSupplied = (e!=null);
      
      if (!typeSupplied) { 
        if (!initializerSupplied) {
          // no initializer or type supplied
          ScigolTreeParser.semanticError("must supply either a type or an initializer in let declaration of identifier '"+id+"'");
          //e = new Value(new Any()); // null
          //t = new TypeSpec(e.GetType()); //huh?
        }
        else {
          // deduce type from type of initializer
          t = e.getType();
        }
      }
      else { // type supplied
        if (!initializerSupplied) {
          // construct default
          FuncInfo callSig = new FuncInfo();
          callSig.setDefinitionLocation( locationOf(i) );
          e = t.constructValue(callSig, null, this);
          
          //...!!! if is a class, call empty constructor
          
          if (e == null)
            ScigolTreeParser.semanticError(tt,"type "+t.typeName()+" doesn't have an accessible no-argument constructor");
        }
        else {
          // check that the type of the initializer is equals to (or convertable to)
          //  the specified type

          TypeSpec etype = e.getType();
          if (!TypeManager.existsImplicitConversion(etype, t, e))
            ScigolTreeParser.semanticError(at,"cannot declare variable '"+id+"' of type '"+t+"' with an incompatible initializer of type '"+etype.typeName()+"'");
                                                                                                                                                                                                                                             
          // convert e to type t
          e = TypeManager.performImplicitConversion(etype, t, e);
                                                                                                                                                                                                                                             
        }
      }

      
//!!! make this check appropriately for duplicate ident (considering allowable overloading, etc.)      
      
      // check if id is already defined in the current scope (ignore enclosing scopes)
      if (!t.isFunc() && scope.contains(id))
        ScigolTreeParser.semanticError(i,"variable "+id+" already declared in this scope");

      scope.addEntry(id, t, e.getValue(), null, modifiers).addAnnotations(annotations);

      // return an LValue for the declared symbol rather than just the initializer rvalue
      v = new Value(new LValue(new Symbol(scope,id,null)));
    }
    ;

    
typeExpression returns [TypeSpec t=null]
    { Value v=null; }
    : v=expr
    {
      if (v.getValue() == null)
        ScigolTreeParser.semanticError("found null where type expression was required");
      TypeSpec vtype = TypeSpec.typeOf(v);
      if (!vtype.isType()) {
        // as a special convenience, if we have a func(->type) value, call it (so the user can omit the ()'s to call the 'type generator function')
        if (vtype.isFunc() && vtype.getFuncInfo().getReturnType().isType() && (vtype.getFuncInfo().numRequiredArgs() == 0)) {
          Func f = (Func)v.getValue();
          FuncInfo callSig = new FuncInfo(new TypeSpec[0], new TypeSpec(TypeSpec.typeType)); // func(->type)
          callSig.setDefinitionLocation( new Location() );

          Object[] convertedArgs = f.getInfo().convertParameters(callSig, new Object[0], f.isExternal());

          // retrieve instance
          Object instance = null;
          if (v.isLValue())
            instance = v.getLValue().getSymbol().getInstance();
          
          Object ret = f.call(null, convertedArgs); // call it
          
          Debug.Assert(TypeSpec.typeOf(ret).isType(),"expected type return");

          t = (TypeSpec)ret;
        }
        else
          ScigolTreeParser.semanticError("an expression of type '"+TypeSpec.typeOf(v)+"' was found where type expression was required");
      }
      else
        t = (TypeSpec)v.getValue();
    }
    ;    
    
    
typeLiteral returns [TypeSpec t=null]
    { FuncInfo fi=null; }
    : b:BUILTIN_TYPE 
    { 
      t=new TypeSpec(b.getText());
      if (t==null)
        ScigolTreeParser.semanticError(b,"undefined type "+b.getText());
    } 
    | fi=funcType { t=new TypeSpec(fi); }
    | t=classType
    | t=interfaceType
    ;    
    
    
    
classType returns [TypeSpec t=null]
    { 
      EnumSet<TypeSpec.Modifier> modifiers = EnumSet.noneOf(TypeSpec.Modifier.class);
      ArrayList baseTypes = new ArrayList();
      Object memberValue = null;
    }
    : #(ct:"class" (modifiers=classModifiers)? (baseTypes=classBase)?
        { 
          // start new class declaration & add interfaces
          TypeSpec baseType = TypeSpec.objectTypeSpec;
          int firstInterface=0;
          if (baseTypes != null) {
            if (baseTypes.size() > 0) {
              TypeSpec firstType = (TypeSpec)baseTypes.get(0); 
              if (!firstType.isInterface()) {
                baseType = firstType; // super class
                firstInterface++;
              }
            }
          }
          ClassInfo info = new ClassInfo(scope, baseType);
          info.setModifiers( modifiers );
          if (ct instanceof CommonASTWithLocation)
          info.setDefinitionLocation( ((CommonASTWithLocation)ct).loc );
          if (baseTypes.size() > 1) {
            for(int ii=firstInterface; ii<baseTypes.size(); ii++) {
              TypeSpec iType = (TypeSpec)baseTypes.get(ii);
              info.addInterface(iType);   
            }
          }
          
          if (scope.topDeclarationIdent() != null)
            info.setIdentityHint(scope.topDeclarationIdent());
          
          // create a new scope for the class
          scope = new ClassScope(new TypeSpec(info));
          scope.setDefinitionLocation( locationOf(ct) );
        }
        (   
            ( #(CTOR  classConstructor[info] ) 
              {
              } 
            )
          | ( #(i:IDENT { scope.pushDeclarationIdent(i.getText()); } 
                classMember[i, false] 
              )
              {
                scope.popDeclarationIdent();
              }
            )
        )*
        {
          info.completeDefinition();
          t = ((ClassScope)scope).getClassType();

          scope = scope.getOuter(); // exit class scope
        }
      )
    ;    



interfaceType returns [TypeSpec t=null]
    { 
      EnumSet<TypeSpec.Modifier> modifiers = EnumSet.noneOf(TypeSpec.Modifier.class);
      ArrayList baseTypes = new ArrayList();
      Object memberValue = null;
    }
    : #(it:"interface" (modifiers=classModifiers)? (baseTypes=classBase)?
        { 
          // start new class declaration & add interfaces
          ClassInfo info = new ClassInfo(scope, null); // interface
          info.setModifiers( modifiers );
          if (it instanceof CommonASTWithLocation)
          info.setDefinitionLocation( ((CommonASTWithLocation)it).loc );
          if (baseTypes == null) baseTypes = new ArrayList();
          if (baseTypes.size() > 0) {
            for(int ii=0; ii<baseTypes.size(); ii++) {
              TypeSpec iType = (TypeSpec)baseTypes.get(ii);
              if (!iType.isBuiltinObject()) { // don't add object again
                if (!iType.isInterface()) // complain if not an interface
                  ScigolTreeParser.semanticError(locationOf(it), "interfaces can only inherit other interfaces (and 'object')");
                info.addInterface(iType);   
              }
            }
          }
          
          if (scope.topDeclarationIdent() != null)
            info.setIdentityHint(scope.topDeclarationIdent());
          
          // create a new scope for the class
          scope = new ClassScope(new TypeSpec(info));
          scope.setDefinitionLocation( locationOf(it) );
        }
        (   
          ( #(i:IDENT { scope.pushDeclarationIdent(i.getText()); } 
              classMember[i, true] 
            )
            {
              scope.popDeclarationIdent();
            }
          )
        )*
        {
          info.completeDefinition();
          t = ((ClassScope)scope).getClassType();

          scope = scope.getOuter(); // exit class scope
        }
      )
    ;    

    
    
    
    
classConstructor[ClassInfo declaringClass] 
    {
      EnumSet<TypeSpec.Modifier> modifiers = EnumSet.noneOf(TypeSpec.Modifier.class);
      Func func=null;
    }
    : ( (modifiers=classModifiers)? func=functionLiteral )
    {
      Debug.Assert(func.getOuterScope().isClassScope(),"members can only be in class scope");

      // if not Protected or Private, then default to Public
      if (   (!modifiers.contains(TypeSpec.Modifier.Private))
          && (!modifiers.contains(TypeSpec.Modifier.Protected)) )
        modifiers.add(TypeSpec.Modifier.Public);

      modifiers.add(TypeSpec.Modifier.Static); // constructors are always Static

      func.setIsConstructor( true );
      
      if (    (func.getInfo().getReturnType() == null)
           || (!func.getInfo().getReturnType().equals(new TypeSpec(declaringClass))) )
        ScigolTreeParser.semanticError(func.getInfo().getDefinitionLocation(),"class constructors must return the declaring class type (i.e. 'self')");
      
      scope.addEntry(".ctor", TypeSpec.typeOf(func), func, null, modifiers);
    }
    ;

    
    
classMember[AST memberName, boolean declaringInInterface] 
    { 
      EnumSet<TypeSpec.Modifier> modifiers = EnumSet.noneOf(TypeSpec.Modifier.class);
      TypeSpec dt=null; 
      Value e=null;
    }
    : (modifiers=m:classModifiers)? 
      (t:COLON dt=typeExpression)? 
      (  a:ASSIGN /*initializerAST=expr*/            // a regular initializer
       | p:PROP /* child propertyDeclaration */      // a property
      )?      
    {
      
//!!! use  declaringInInterface appropriately
     
      boolean propertyDecl = (p != null);
      
      boolean isConst  = modifiers.contains(TypeSpec.Modifier.Const);
      boolean isStatic = modifiers.contains(TypeSpec.Modifier.Static);
      boolean staticOrConst = isStatic || isConst;

      // if not Protected or Private, then default to Public
      if (   (!modifiers.contains(TypeSpec.Modifier.Private))
          && (!modifiers.contains(TypeSpec.Modifier.Protected)) )
        modifiers.add(TypeSpec.Modifier.Public);
        

      if (propertyDecl) {   
        //
        // handle a property declaration

        String ident = memberName.getText();
        
        if (dt==null) 
          ScigolTreeParser.semanticError(locationOf(memberName),"property member '"+ident+"' must explicitly declare a type");
        
        if (isStatic && (ident.equals("operator()")))
          ScigolTreeParser.semanticError(locationOf(memberName),"property member 'operator()' - an indexer - cannot be static");
        
        
        // Check for existing property with the same name (properties cannot be overloaded based on call signature)
        //....!!!

        
        FuncInfo propSig = new FuncInfo(); // *explicit* argument of property (with no return)
        boolean gotGetAccessor = false;
        boolean gotSetAccessor = false;
        Entry getterEntry = null;
        Entry setterEntry = null;
        
        // do some manual tree 'parsing' to get what we want without actually
        //  walking/executing the accessor bodies
        AST propAST = p.getFirstChild();
        AST nextAST = propAST.getFirstChild();
        if (nextAST.getType() == LPAREN) { // a formal param list (possibly empty) was supplied
          AST paramListAST = nextAST.getFirstChild();
          propSig = formalParamList(paramListAST);
          
          nextAST = nextAST.getNextSibling();
        }

        
        // while more accessors (max of two - get & set)
        while ((nextAST != null) && (nextAST.getType() == IDENT)) { 

          AST initializerAST = null;
          Object staticValue = null; 
          
          String accessorName = nextAST.getText();
          CommonASTWithLocation astWithLoc = (CommonASTWithLocation)nextAST;
          Location loc = (astWithLoc!=null)?astWithLoc.loc:new Location();

          if ( (!accessorName.equals("set")) && (!accessorName.equals("get")) ) {
            ScigolTreeParser.semanticError(loc,"the only valid property accessor names are 'get' and 'set'");
          }

          if (accessorName.equals("set")) {
            if (gotSetAccessor)
              ScigolTreeParser.semanticError(loc,"a property can only have one 'set' accessor");
            gotSetAccessor = true;
          }
          else if (accessorName.equals("get")) {
            if (gotGetAccessor)
              ScigolTreeParser.semanticError(loc,"a property can only have one 'get' accessor");
            gotGetAccessor = true;
          }
          
          AST accessorBodyAST = nextAST.getFirstChild();

          // make appropriate accessor signature
          FuncInfo accessorSig = propSig.accessorSig(accessorName, dt);

          if ((accessorBodyAST.getType() == LITERAL_pre) || (accessorBodyAST.getType() == LITERAL_post) || (accessorBodyAST.getType() == EXPRLIST)) {
            staticValue = functionLiteralBody(accessorBodyAST,accessorSig);
          }
          else { // accessor is initialized with an expression
            initializerAST = accessorBodyAST;
            
            // if static or const, evaluate it now, otherwise it will be evaluated at instantiation time
            if (staticOrConst) {
              Value v = expr(initializerAST);
              staticValue=v.getValue();
              initializerAST=null; // don't need it again
            }
            
          }
          
          
          // now enter the appropriate accessor func into the current class scope
          String funcName = FuncInfo.accessorName(ident, (accessorName.equals("get")));

          Entry accessorEntry = new Entry(funcName, new TypeSpec(accessorSig), staticValue, initializerAST, modifiers, EnumSet.of(Entry.Flags.Method,Entry.Flags.Accessor), -1, scope, loc);
          if (accessorName.equals("get"))
            getterEntry = accessorEntry;
          else
            setterEntry = accessorEntry;
          scope.addEntry(accessorEntry);
          
          
          nextAST = nextAST.getNextSibling();
          
        } // while
        
        
        // in addition to the accessor func members, we also add an Entry for the property itself
        EnumSet<Entry.Flags> flags = EnumSet.of(Entry.Flags.Property);
        if (gotGetAccessor) flags.add(Entry.Flags.HasGetter);
        if (gotSetAccessor) flags.add(Entry.Flags.HasSetter);
        Entry propEntry = new Entry(ident, dt, null, null, modifiers, flags, -1, scope, locationOf(memberName));

        // put a reference to the accessors into the property Entry (as it's staticValue)
        Entry.EntryPair accessors = new Entry.EntryPair();
        accessors.setter = setterEntry;
        accessors.getter = getterEntry;
        propEntry.setStaticValue( accessors );
        
        scope.addEntry(propEntry);
        
        // Also, put a reference to the propEntry in each accessor entry
        if (gotGetAccessor) getterEntry.propertyEntry = propEntry;
        if (gotSetAccessor) setterEntry.propertyEntry = propEntry;
        
      }
      else { 
        
        //
        // member is a regular (non-property) declaration
        
        boolean typeSupplied = (t != null);
        boolean initializerSupplied = (a != null);
        
        if (!typeSupplied && !initializerSupplied)
          ScigolTreeParser.semanticError(t,"member declaration must include a type or an initializer (or both)");
        
        // get initializer expr AST, if supplied
        AST initializerAST = null;
        if (initializerSupplied) initializerAST=a.getNextSibling();
        
  //      if (initializerSupplied) //!!!
  //        Debug.WriteLine("initializerAST="+initializerAST.toStringTree());
  //      else
  //        Debug.WriteLine("no initializer");
  
  
        // if this is a is static or const, the initializer is evaluated now,
        //  otherwise it is evaluated as instantiation time (in which case the type must be specified here)
        //  (NB: methods are a special case as for convenience they don't need to have their type
        //   specified *if* the initializer is a func literal - which specifies the signature/type)
  
        Object staticValue = null; 
        boolean typeKnown = typeSupplied;
  
        if (initializerSupplied) {
          
          if (staticOrConst) {
            Value v=expr(initializerAST); // eval initializer now
            staticValue=v.getValue();
            initializerAST = null; // don't need it again
            if (!typeSupplied) {
              dt = TypeSpec.typeOf(staticValue); // deduce type from initializer
              typeKnown = true;
            }
          }
          else {
  //!!! is it possible to have consistient behaviour and deduce the type too.  But we need
  //another rule that can tree parse the signature WITHOUT evaluating the default params (!) 
  //  (this has the problem that the signature will be different! - without defaults)
  
            // as a special convenience, if the initializer is a func literal and the type wasn't
            //  explicitly supplied, we can evaluate it now, saving the user from having 
            //  to specify it in this case (avoiding the member type being 'any')
            //  (i.e. we can deduce the func type by evaluating the literal).
            // NB: this has the side-effect that any parameter default expressions will be 
            //  evaluated now, rather than at initialization time!
            if ((!typeSupplied) && (initializerAST.getType() == ScigolLexer.LIT_FUNC)) {
              Value v = expr(initializerAST);
              staticValue=v.getValue();
              initializerAST = null; // don't need it again
            
              dt = TypeSpec.typeOf(staticValue);
              typeKnown = true;
            }
            
          }
        }
        else { // no initializer, instantiate default from type (either now or at instantiation)
          Debug.Assert(typeSupplied);
         
           if (staticOrConst) {
             Value v = dt.constructValue(null, null, this);
             staticValue = v.getValue();
           }
           else { // instance
             staticValue = null;
           }
          
        }
        
        
        if (!typeKnown)
          dt = new TypeSpec(TypeSpec.anyType);
  
        String name = scope.topDeclarationIdent();
        if (scope.contains(name)) { // does this scope already define a member named 'name'?
          // yes, that is only OK if it is a func (overloading) that isn't ambiguous in its call signature
          if (!typeKnown || (typeKnown && !dt.isFunc()))
            ScigolTreeParser.semanticError(t,"class already has a member named '"+name+"'");
        }
  
        // add to scope (will thow in the case of ambiguous func overloading)
        scope.addEntry(name, dt, staticValue, initializerAST, modifiers, locationOf(memberName));
        
      }
      
    }
    ;
    
    

classBase returns [ArrayList baseTypes=new ArrayList()]
    { TypeSpec baseType; }
    : #(COLON (baseType=typeExpression {baseTypes.add(baseType);})* )
    ;
    
classModifiers returns [EnumSet<TypeSpec.Modifier> modifiers]
    { modifiers = EnumSet.noneOf(TypeSpec.Modifier.class); }
    : #(MODIFIERS 
        (  "public"    { modifiers.add(TypeSpec.Modifier.Public); } 
         | "protected" { modifiers.add(TypeSpec.Modifier.Protected); }
         | "private"   { modifiers.add(TypeSpec.Modifier.Private); }
         | "static"    { modifiers.add(TypeSpec.Modifier.Static); }
         | "override"  { modifiers.add(TypeSpec.Modifier.Override); }
         | "final"     { modifiers.add(TypeSpec.Modifier.Final); }
        )*
      )
    ;
    
    
funcType returns [FuncInfo v=null]
    { TypeSpec rt=null; FuncInfo fi=null; }
    : #(fl:FUNC fi=paramTypeList (GIVES rt=typeExpression)? ) 
    {
      if (rt != null)
        fi.setReturnType( rt );
      fi.setDefinitionLocation( locationOf(fl) );
      v=fi;
    }
    ;    
    
funcSignature returns [FuncInfo v=null]
    { TypeSpec rt=null; FuncInfo fi=null; }
    : #(fl:FUNC fi=formalParamList (GIVES rt=typeExpression)? ) 
    {
      if (rt != null)
        fi.setReturnType( rt );
      fi.setDefinitionLocation( locationOf(fl) );
      v=fi;
    }
    ;    

    
    
paramTypeList returns [FuncInfo fi=null]
    { 
      TypeSpec t; 
      ArrayList ptypes = new ArrayList();
    }
    : ( 
        t=typeExpression
        {
          ptypes.add(t);
        }
      )*
    {
      int numArgs = ptypes.size();
      // convert the ArrayLists to arrays
      TypeSpec[] ptypesa = new TypeSpec[numArgs];

      for(int p=0; p<numArgs;p++) 
        ptypesa[p] = (TypeSpec)ptypes.get(p);
      
      // return a partially filled-out FuncInfo
      fi = new FuncInfo(ptypesa, null);
    }
    ;



    
formalParamList returns [FuncInfo fi=null]
    { 
      TypeSpec t;
      Value e=null; 
      ArrayList pnames = new ArrayList();
      ArrayList ptypes = new ArrayList();
      ArrayList pdefaults = new ArrayList();
      ArrayList phasdefault = new ArrayList();
    }
    : ( 
        i:IDENT t=typeExpression (ASSIGN e=expr)?
        {
          String paramName = i.getText();
          TypeSpec paramType = t;
          pnames.add(paramName);
          ptypes.add(paramType);
          phasdefault.add( (e != null) );
          if (e != null)
            pdefaults.add(e.getValue());
          else
            pdefaults.add(null);
          e=null;
        }
      )*
    {
      // convert the ArrayLists to arrays
      String[] pnamesa = new String[pnames.size()];
      TypeSpec[] ptypesa = new TypeSpec[ptypes.size()];
      Object[] pdefaultsa = new Object[pdefaults.size()];
      boolean[] phasdefaulta = new boolean[phasdefault.size()];

      for(int p=0; p<pnamesa.length;p++) {
        pnamesa[p] = (String)(String)pnames.get(p);
        ptypesa[p] = (TypeSpec)ptypes.get(p);
        pdefaultsa[p] = pdefaults.get(p);
        phasdefaulta[p] = ((Boolean)phasdefault.get(p)).booleanValue();
      }
      
      // return a partially filled-out FuncInfo
      fi = new FuncInfo(pnamesa, ptypesa, pdefaultsa, phasdefaulta, null);
      fi.setDefinitionLocation( locationOf(i) );
    }
    ;
    

    
functionLiteral returns [Func v=null]
    { FuncInfo sig; } 
    : #(LIT_FUNC sig=funcSignature v=functionLiteralBody[sig])  
    ;

    

functionLiteralBody[FuncInfo sig] returns [Func v=null]
    : ( pre:"pre" | post:"post" | exprList:EXPRLIST ) /* ... - don't walk/execute bodies here */
    {
      AST funcBodyAST = null;
      AST preBodyAST = null;
      AST postBodyAST = null;

      AST funcAST = #functionLiteralBody;
//      funcAST = (funcAST.getFirstChild()).getNextSibling(); // skip to after signature
       
      // if funcAST has an EXPRLIST token type, it is the start of the func body, else if it has pre or post
      //  it is a pre/postcondition expression

      if (funcAST.getType() == EXPRLIST) {
//Debug.WL("found func body first - no pre/post");        
        funcBodyAST = funcAST;
      }
      else {
        if ((funcAST.getType() == LITERAL_pre) || (funcAST.getType() == LITERAL_post)) {
//Debug.WL("got first pre/post");
          int type = funcAST.getType();        
          funcAST = funcAST.getNextSibling(); // skip over "pre"/"post"
          if (type == LITERAL_pre)
            preBodyAST = funcAST;
          else
            postBodyAST = funcAST;
          funcAST = funcAST.getNextSibling(); // skip over condition body
        }
        
        if (funcAST.getType() == EXPRLIST) {
//Debug.WL("got func body after first pre/post");          
          funcBodyAST = funcAST;
        }
        else {
          if ((funcAST.getType() == LITERAL_pre) || (funcAST.getType() == LITERAL_post)) {
//Debug.WL("got second pre/post");          
            int type = funcAST.getType();
            funcAST = funcAST.getNextSibling(); // skip over "pre"/"post"
            if (type == LITERAL_post) {
              if (postBodyAST != null) 
                ScigolTreeParser.semanticError(sig.getDefinitionLocation(), "a func can only define a 'pre' expression, a 'post' expression or both (but not multiple of either)");
              postBodyAST = funcAST;
            }
            else {
              if (preBodyAST != null) 
                ScigolTreeParser.semanticError(sig.getDefinitionLocation(), "a func can only define a 'pre' expression, a 'post' expression or both (but not multiple of either)");
              preBodyAST = funcAST;
            }
            funcAST = funcAST.getNextSibling(); // skip over condition body
            funcBodyAST = funcAST;
          }
          else
            Debug.Assert(false, "couldn't match pre/post or body");
        }
      }
      
      Debug.Assert(funcBodyAST!=null,"no func body!?");
      
      //!!!
//      if (preBodyAST!=null)
//        Debug.WL("preBodyAST:"+preBodyAST.toStringTree());
//      if (postBodyAST!=null)
//        Debug.WL("postBodyAST:"+postBodyAST.toStringTree());
      
      v = new Func(sig, scope, this, funcBodyAST, preBodyAST, postBodyAST);
//      Debug.WriteLine("funcBody:"+v+"AST:"+funcBodyAST.toStringTree()); 
    }
    ;
    
    

    
    
logCall returns [Value v=null]
    : #("logger" (#(COMMA i:IDENT))? v=expr) 
    { 
      if(i!=null) System.out.print(i.getText()+": ");
      System.out.println(v.toString()); 
    }
    ;
    
    
number returns [Value v=null]
    : i:NUM_INT    {v = new Value(new Integer(i.getText())); }
    | d:NUM_DINT   {v = new Value(new Long(d.getText())); }
    | r:NUM_REAL   {v = new Value(new Double(r.getText())); }
    | s:NUM_SREAL  {v = new Value(new Float(s.getText())); }
    |   LIT_TRUE   {v = new Value(new Boolean(true)); }
    |   LIT_FALSE  {v = new Value(new Boolean(false)); }
    |   LIT_NULL   { v = new Value(new Any(null)); }
    | v=matrixexpr
    ;
    
    
stringLit returns [Value v=null]
    : s:STRING_LITERAL 
    { 
      String str = new String(s.getText()); 
      if (str.charAt(0) == '\"')
        str = str.substring(1,str.length()-1); // remove surrouding double quotes
      else
        str = str.substring(2,str.length()-2); // remove surrouding back quote paris quotes
      v = new Value(str); // String
    }
    ;
    
    
charLit returns [Value v=null]
    : c:CHAR_LITERAL
    {
      String str = new String(c.getText()); 
      if (str.charAt(0) == '\"')
        str = str.substring(1,str.length()-2); // remove surrouding double quotes & suffix
      if (str.length() != 1)
        ScigolTreeParser.semanticError(locationOf(c),"character literals must contain a single character within the quotes");
      v = new Value(str.charAt(0)); // char
    }
    ;
    
    
    
matrixexpr returns [Value v=null]
    { Vector r; } // each row
    : #(MATRIX r=matrixrow { v=new Value(r); } 
               ( (SEMI)+ r=matrixrow { 
                                   if (v.getValue() instanceof Vector)
                                     v = new Value(new Matrix((Vector)v.getValue()));
                                   ((Matrix)v.getValue()).appendRowVector(r); 
                                 }
               )*
       )
    ;
    
matrixrow returns [ Vector v=new Vector() ] 
    { Value e; } // each element 
    : ( e=expr { v.appendElement(e.getValue()); } )+
    ;
    