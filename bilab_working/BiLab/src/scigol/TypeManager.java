package scigol;


import java.util.*;

import java.lang.reflect.Type;
import java.lang.reflect.Member;



public class TypeManager
{

  
  
  // assuming entry is a conversion 'operator' declared in the given type, what is the 
  //  source type of the conversion
  public static TypeSpec conversionOperatorFromType(TypeSpec declaringType, Entry entry)
  {
    Debug.Assert( (entry.name.equals(".ctor")) || (entry.name.equals("operator->")), "must be a convertsion 'operator'");
    
    FuncInfo fi = entry.type.getFuncInfo();
    if (fi.numRequiredArgs() == 1) // constructor or static operator->
      return fi.getParamTypes()[0];
    else
      return declaringType; // instance operator->
  }

  // assuming entry is a conversion 'operator' declared in the given type, what is the 
  //  target type of the conversion
  public static TypeSpec conversionOperatorToType(TypeSpec declaringType, Entry entry)
  {
    Debug.Assert( (entry.name.equals(".ctor")) || (entry.name.equals("operator->")), "must be a convertsion 'operator'");
    
    if (entry.name.equals(".ctor")) return declaringType; // constructor

    return entry.type.getFuncInfo().getReturnType(); // operator->
  }
  
  
  
  public static Value callConversionOperator(TypeSpec declaringType, Entry entry, Value fromValue)
  {
    Debug.Assert(entry.type.isFunc(), "can only call func!");
    
    // handle different ways to call a conversion 'operator' depending on if it is a constructor,
    //  a static operator-> or an instance operator->
    
    TypeSpec[] ts = new TypeSpec[1];
    ts[0] = TypeSpec.typeOf(fromValue);
    FuncInfo callSig = new FuncInfo(ts, declaringType);
    ArrayList args = new ArrayList(); args.add(fromValue.getValue());
    Scope classScope = new ClassScope(declaringType);

    if (entry.name.equals(".ctor")) {
      
      // pull the tree-parser out of the Func, then use ClassInfo.instantiateClass()
      Debug.Assert(declaringType.isClass() || declaringType.isBuiltinClass(), "declaring type must be a class");

      Func ctorFunc = (Func)scigol.Class.getMemberValue(entry,null);
      ScigolTreeParser tparser = ctorFunc.getParser();
      scigol.Class c = declaringType.getClassInfo().instantiateClass(callSig, args, tparser);

      if (c.isExternal())
        return new Value(c.getSysValue());
      else
        return new Value(c);
    }
    else { // operator->
      
      if (entry.isStatic()) { // static single arg operator

        Func opFunc = (Func)scigol.Class.getMemberValue(entry,null);
        Object r = opFunc.call(null, args);
        
        return new Value(r);
      }
      else { // instance, no arg operator

        if (fromValue.getValue() == null)
          ScigolTreeParser.semanticError("a null instance was supplied in call to instance 'operator->' of type '"+declaringType+"' when an object was expected");
        
        // we can only call an instance operator-> if the fromValue is the instance of declaringType
        Debug.Assert( TypeSpec.typeOf(fromValue).equals(declaringType), "fromValue must be the instance of declaringType="+declaringType);
        Debug.Assert(fromValue.getValue() != null, "need an instance!");
        Object instance = fromValue.getValue();
        Debug.Assert(instance instanceof Class, "instance must be class");

        Func opFunc = (Func)scigol.Class.getMemberValue(entry,instance);
//Debug.WL("calling instance func:"+opFunc);
//Debug.WL(" on instance "+instance);
        Object r = (opFunc!=null)? opFunc.call(instance, new Object[0]) : null;
        
        return new Value(r);
      }
      
    }
    
  }
  
  
  
  
  
  
  // gets any conversion 'operators' (including constructors if t is concrete) of type t
  //  (except conversion to self - i.e. 'copy' constructors and operator->(->self) )
  public static Entry[] getUserDefinedConversionOperatorsOf(TypeSpec t)
  {
//Debug.WriteLine("getUserDefinedConversionOperatorsOf("+t+")");
    if (!t.isClass() && !t.isBuiltinClass()) return new Entry[0]; // only classes have conversion operators
    
    ArrayList entries = new ArrayList();
    Scope classScope = new ClassScope(t);
    
    // gather all constructors that take a single argument and all instance or static
    //  operator-> methods
    
    if (!t.isAbstractClassOrInterface()) { // t is concrete (otherwise ignore constructors)
      
      Entry[] ctors = classScope.getEntries(".ctor",null);
    
      for (Entry entry : ctors) {
        Debug.Assert(entry.type.isFunc(), ".ctor isn't a func! type:"+entry.type);
        if (entry.type.getFuncInfo().numRequiredArgs() == 1) {
          if (!entry.type.getFuncInfo().getParamTypes()[0].equals(t)) // ignore 'copy' constructor (conversion of self->self)
            entries.add(entry);
        }
      }
      
    }


    // now operator->
    Entry[] ops = classScope.getEntries("operator->", null);
//Debug.WL(" got entries #="+ops.Length+" t="+t); //.classInfo.ToStringFull());    
    for (Entry entry : ops) {
      Debug.Assert(entry.type.isFunc(), "entry for operator-> isn't a func; type:"+entry.type);
      FuncInfo fi = entry.type.getFuncInfo();
      // is it an instance operator-> that takes a single arg?
      boolean isInstanceOp = (!entry.isStatic() && (fi.numRequiredArgs() == 0));
      // is it a static operator-> that takes a single arg where either the arg type of the
      //  return type is t?
      boolean isStaticOp = (entry.isStatic() && (fi.numRequiredArgs() == 1)
                         && ( fi.getParamTypes()[0].equals(t) || fi.getReturnType().equals(t) ));

//Debug.WL(" considering "+entry.name+" "+fi+" isInstanceOp="+isInstanceOp+" isStaticOp="+isStaticOp+" entry.index="+entry.index);                         
      if (!entry.isAbstract() 
              && (entry.index >= 0) ) { // !!!currently, methods inherited from Java types can't be called - so don't consider them!!!
        if (isInstanceOp) {
          // don't bother with operator->(->self) - not useful
          if (!fi.getReturnType().equals(t)) {
            entries.add(entry);
//Debug.WriteLine(" got op:"+entry.type.funcInfo+" of "+t);        
          }
        }
        else if (isStaticOp) {
          // don't bother with operator->(a->a) either
          if (!fi.getParamTypes()[0].equals(fi.getReturnType())) {
            entries.add(entry);
//Debug.WriteLine(" got op:"+entry.type.funcInfo+" of "+t);        
          }
        }
      }
    }
    
    
    return Entry.toArray(entries);
  }
  
  
  
  
  
  // Implicit conversion
  

  public static boolean existsImplicitNumericConversion(TypeSpec from, TypeSpec to)
  {
    if (!from.isBuiltin() || !to.isBuiltin()) return false;

    if ((from == to) || from.equals(to)) return true;
    if (to.isAny()) return true; // anything can be converted to any
    if (from.isANum() && to.isNum()) return true; // any numeric can be converted to num
    
    if (from.isByte())
      return to.isChar() || to.isInt() || to.isDint() || to.isSreal() || to.isReal() || to.isRange() || to.isVector() || to.isMatrix();
    if (from.isChar())
      return to.isInt() || to.isDint() || to.isSreal() || to.isReal() || to.isRange() || to.isVector() || to.isMatrix();
    if (from.isInt())
      return to.isDint() || to.isSreal() || to.isReal() || to.isRange() || to.isVector() || to.isMatrix();
    if (from.isDint())
      return to.isSreal() || to.isReal() || to.isVector() || to.isMatrix();
    if (from.isSreal())
      return to.isReal() || to.isVector() || to.isMatrix();
    if (from.isReal())
      return to.isVector() || to.isMatrix();
    if (from.isVector())
      return to.isMatrix();
    
    return false;    
  }

  
  
  public static Value performImplicitNumericConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    Debug.Assert(existsImplicitNumericConversion(from, to), "implicit numeric conversion doesn't exist!");
    
    if ((from == to) || from.equals(to)) return fromValue;
    
    if (to.isAny()) {
      if (from.isNum() && (fromValue.getValue()!=null)) 
        return new Value(new Any( ((Num)fromValue.getValue()).value ));
      else
        return new Value(new Any(fromValue.getValue()));
    }
    
    if (from.isANum() && to.isNum()) {
      Object value = fromValue.getValue();
      if (from.isNum() && (value!=null))
        value = ((Num)fromValue.getValue()).value; // unwrap num
      return new Value(new Num(value));
    }
    
    
    if (fromValue.getValue() == null) return fromValue;
    
    Object v = fromValue.getValue();
    
    if (from.isByte()) {
      if (to.isChar()) return new Value(new Character((char)((Byte)v).byteValue()));
      if (to.isInt()) return new Value(new Integer((int)((Byte)v).byteValue()));
      if (to.isDint()) return new Value(new Long((long)((Byte)v).byteValue()));
      if (to.isSreal()) return new Value(new Float((float)((Byte)v).byteValue()));
      if (to.isReal()) return new Value(new Double((double)((Byte)v).byteValue()));
      if (to.isRange()) return new Value(new Range((int)((Byte)v).byteValue(), (int)((Byte)v).byteValue()));
      if (to.isVector()) return new Value(new Vector((double)((Byte)v).byteValue()));
      if (to.isMatrix()) return new Value(new Matrix((double)((Byte)v).byteValue()));
    }
    
    if (from.isChar()) {
      if (to.isInt()) return new Value(new Integer((int)((Character)v).charValue()));
      if (to.isDint()) return new Value(new Long((long)((Character)v).charValue()));
      if (to.isSreal()) return new Value(new Float((float)((Character)v).charValue()));
      if (to.isReal()) return new Value(new Double((double)((Character)v).charValue()));
      if (to.isRange()) return new Value(new Range((int)((Character)v).charValue(), (int)((Character)v).charValue()));
      if (to.isVector()) return new Value(new Vector((double)((Character)v).charValue()));
      if (to.isMatrix()) return new Value(new Matrix((double)((Character)v).charValue()));
    }
    
    if (from.isInt()) {
      if (to.isDint()) return new Value(new Long((long)((Integer)v).intValue()));
      if (to.isSreal()) return new Value(new Float((float)((Integer)v).intValue()));
      if (to.isReal()) return new Value(new Double((double)((Integer)v).intValue()));
      if (to.isRange()) return new Value(new Range(((Integer)v).intValue(), ((Integer)v).intValue()));
      if (to.isVector()) return new Value(new Vector((double)((Integer)v).intValue()));
      if (to.isMatrix()) return new Value(new Matrix((double)((Integer)v).intValue()));
    }
    
    if (from.isDint()) {
      if (to.isSreal()) return new Value(new Float((float)((Long)v).longValue()));
      if (to.isReal()) return new Value(new Double((double)((Long)v).longValue()));
      if (to.isVector()) return new Value(new Vector((double)((Long)v).longValue()));
      if (to.isMatrix()) return new Value(new Matrix((double)((Long)v).longValue()));
    }
    
    
    if (from.isSreal()) {
      if (to.isReal()) return new Value(new Double((double)((Float)v).floatValue()));
      if (to.isVector()) return new Value(new Vector((double)((Float)v).floatValue()));
      if (to.isMatrix()) return new Value(new Matrix((double)((Float)v).floatValue()));
    }
    
    if (from.isReal()) {
      if (to.isVector()) return new Value(new Vector(((Double)v).doubleValue()));
      if (to.isMatrix()) return new Value(new Matrix(((Double)v).doubleValue()));
    }
    
    if (from.isVector()) {
      if (to.isMatrix()) return new Value(new Matrix((Vector)v));
    }
    
    Debug.Assert(false,"unimplemented or bad performImplicitNumericConversion("+from+", "+to+")");
    return null;
  }
  
  
  
  
  public static boolean existsImplicitReferenceConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if ((from == to) || from.equals(to)) return true;
   
    if (to.isBuiltinObject()) return true; // anything to object
    if (to.isAny()) return true; // anything to any
    if (from.isAny()) return true; // from any to anything else 

    if (from.isNum() && to.isANum()) return true; // from num to any other numeric type

    if (from.isA(to)) return true;

    if (from.isList() && to.isVector()) return true;

    if (fromValue != null) {
      if (fromValue.getValue() == null) return true; // a null value can be like anything
    
      // if any, it can be implicitly converted to its actual type (if non-null)
      if (from.isAny())
        if (TypeSpec.typeOf(((Any)fromValue.getValue()).value).equals(to))
          return true;
      
      if (to.isString() && from.isBuiltin()) 
        return true;
    }
    
   return false; 
  }
  
  
  
  public static Value performImplicitReferenceConversion(TypeSpec from, TypeSpec to, Value fromValue) 
  {
    if ((from == to) || from.equals(to)) return fromValue;
    
    if (to.isBuiltinObject()) return fromValue; // anything to object
    if (to.isAny()) {
      if (from.isNum()) return new Value(new Any(((Num)fromValue.getValue()).value));
      return new Value(new Any(fromValue.getValue()));
    }

    if (from.isA(to)) return fromValue; // from is a subclass of to
    
    if (fromValue.getValue() == null) return fromValue; // null is compatible with anything

    if (from.isList() && to.isVector()) 
      return new Value(new Vector((List)fromValue.getValue()));
    
    // if any, it can be implicitly converted to anything (if explicitly possible)
    if (from.isAny()) {
      Object svalue = ((Any)fromValue.getValue()).value;
      TypeSpec stype = TypeSpec.typeOf(svalue);
      
      if (stype.equals(to)) // simple unwrapping
        return new Value(svalue);
      
      if (stype.isA(to)) return new Value(svalue); // actual from is a subclass of to
      
      // implicit conversion from any behaves like an explicit conversion
      //  to the target type
      return performExplicitConversion(stype, to, new Value(svalue));
    }
    
    // num behaves something like any except it can obly be implicitly 
    //  converted to numeric types
    if (from.isNum() && to.isANum()) {
      Object svalue = ((Num)fromValue.getValue()).value;
      TypeSpec stype = TypeSpec.typeOf(svalue);
      
      if (stype.equals(to)) // simple unwrapping
        return new Value(svalue);

      if (stype.isA(to)) return new Value(svalue); // actual from is a subclass of to

      // implicit conversion from num behaves like an explicit conversion
      //  to the target type
      return performExplicitConversion(stype, to, new Value(svalue));
    }
    
    
    
    if (to.isString() && from.isBuiltin()) {
      if (from.isChar()) { // a single char -> a single char string containing it
        Character c = (Character)fromValue.getValue();
        char[] ca = new char[1];
        ca[0] = c.charValue();
        return new Value( new String(ca) );
      }
      return new Value(fromValue.getValue().toString());
    }
    
    Debug.Assert(false,"unimplemented or bad performImplicitReferenceConversion("+from+", "+to+")");
    return null;
  }
  

  
  
  public static boolean existsImplicitUserDefinedConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    // just try to perform the conversion, if it doesn't work, return false
    
    Value r = performUserDefinedConversion(from, to, fromValue, true);
    return (r != null);    
  }
  
  
  
  public static Value performImplicitUserDefinedConversion(TypeSpec from, TypeSpec to, Value fromValue) 
  {
    return performUserDefinedConversion(from, to, fromValue, true);
  }
  
  

  
  public static boolean existsImplicitConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if ((from == to) || from.equals(to)) return true;
    
    if (existsImplicitNumericConversion(from, to)) return true;
    if (existsImplicitReferenceConversion(from, to, fromValue)) return true;
    if (existsImplicitUserDefinedConversion(from, to, fromValue)) return true;
    
    return false;
  }

  
  
  // user-defined conversions have precedence over builtin conversions
  public static Value performImplicitConversion(TypeSpec from, TypeSpec to, Value fromValue) 
  {
    Debug.Assert( (fromValue.getValue() == null) || (TypeSpec.typeOf(fromValue).isA(from)), "fromValue (type "+TypeSpec.typeOf(fromValue)+") must be of type 'from' (= "+from+") or a derived type");

    if ((from == to) || from.equals(to)) return fromValue;

    // first try a user-define conversion.  If none exist, try the builtin conversions.
    Value r = performImplicitUserDefinedConversion(from, to, fromValue);
    if (r != null) return r;
    
    if (existsImplicitNumericConversion(from, to))
      return performImplicitNumericConversion(from, to, fromValue);
    
    if (existsImplicitReferenceConversion(from, to, fromValue))
      return performImplicitReferenceConversion(from, to, fromValue);

    ScigolTreeParser.semanticError("there is no implicit conversion available from type '"+from+"' to type '"+to+"'");
    return null;
  }
  
  
  
  
  
  
  // Explicit conversion

  
  public static boolean existsExplicitNumericConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    // numeric conversions for which no implicit conversion exists
    if (from.isANum() && !existsImplicitNumericConversion(from, to)) return true;

    if (from.isANum() && (to.isANum() || (to.isAny())) ) return true;
    
    if ((from.isVector() || from.isMatrix() || from.isList() || from.isString()) && to.isANum())
      return true;
    
    return false;
  }

  
  
  // helper
  public static Value performExplicitVectorOrMatrixConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    Debug.Assert((from.isVector() || from.isMatrix()) && to.isANum() && !to.isNum(), "only vector or matrix to concrete numeric values");
    
    // just convert the single element to 'real' and them another implicit conversion from 'real' to 'to'
    
    Object v = fromValue.getValue();
    Value realValue = null;
    
    if (from.isMatrix()) { // single element matrix -> real
      Matrix m = (Matrix)v;
      if ((m.get_rows() == 1) && (m.get_cols()==1)) {
        Object element = m.get_Item(0,0);
        TypeSpec eltType = TypeSpec.typeOf(element);
        if (existsImplicitNumericConversion(eltType, TypeSpec.realTypeSpec))
          realValue = performImplicitNumericConversion(eltType, TypeSpec.realTypeSpec, new Value(element));
        else
          ScigolTreeParser.semanticError("conversion from type 'matrix' to type '"+to+"' requires the matrix to have a single element that is convertable to '"+to+"'");
      }
      else
        ScigolTreeParser.semanticError("conversion from type 'matrix' to type '"+to+"' requires the matrix to have a single element that is convertable to '"+to+"'");
    }
    
    if (from.isVector()) { // single element vector -> real
      scigol.Vector vec = (Vector)v;
      if (vec.get_size() == 1) {
        Object element = vec.get_Item(0);
        TypeSpec eltType = TypeSpec.typeOf(element);
        if (existsImplicitNumericConversion(eltType, TypeSpec.realTypeSpec))
          realValue = performImplicitNumericConversion(eltType, TypeSpec.realTypeSpec, new Value(element));
        else
          ScigolTreeParser.semanticError("conversion from type 'vector' to type '"+to+"' requires the vector to have a single element that is convertable to '"+to+"'");
      }
      else
        ScigolTreeParser.semanticError("conversion from type 'vector' to type '"+to+"' requires the vector to have a single element that is convertable to '"+to+"'");
    }
    
    
    // now convert the real to 'to'
    return performExplicitNumericConversion(TypeSpec.realTypeSpec, to, realValue);
  }
  
  
  
  public static Value performExplicitNumericConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if (!existsImplicitNumericConversion(from, to)) {
    
      if ((from.isANum() || from.isVector() || from.isMatrix()) && (to.isANum() || (to.isAny())) ) {

        // if null, just return a null        
        if (fromValue.getValue() == null) {
          if (to.isNum()) return new Value(new Num(null));
          if (to.isAny()) return new Value(new Any(null));
          return fromValue;
        }
        
        // unwrap num
        if (from.isNum()) {
          fromValue = new Value( ((Num)fromValue.getValue()).value );
          from = TypeSpec.typeOf(fromValue);
        
          // again, if null, just return null
          if (fromValue.getValue() == null) {
            if (to.isNum()) return new Value(new Num(null));
            if (to.isAny()) return new Value(new Any(null));
            return fromValue;
          }
        }
        
        if (to.isAny()) return new Value(new Any(fromValue.getValue())); // wrap in any
        
        if (from.equals(to)) return fromValue; // same types (now unwraped)
        
        // now we just have conversions between concrete numeric types (no nums, anys or nulls)
        Object v = fromValue.getValue();

        if (to.isVector()) {
          if (from.isMatrix()) { // single row matrix -> vector
            Matrix m = (Matrix)v;
            if (m.get_rows() == 1) return new Value(m.row(0));
            if (m.get_cols() == 1) return new Value(m.col(0));
            if (m.get_rows() == 0) return new Value(new Vector());
            ScigolTreeParser.semanticError("conversion from type 'matrix' to type 'vector' requires the matrix to have either 1 row or 1 column or 0 elements");
          }
        }
        
        if (to.isReal()) {
          if (from.isMatrix() || from.isVector()) // single element matrix/vector -> real
            return performExplicitVectorOrMatrixConversion(from, to, fromValue);
        }
        
        
        if (to.isSreal()) {
          if (from.isMatrix() || from.isVector()) // single element matrix/vector -> sreal
            return performExplicitVectorOrMatrixConversion(from, to, fromValue);
          
          if (from.isReal()) return new Value( new Float((float)((Double)v).doubleValue()) );
        }
        
        if (to.isDint()) {
          if (from.isMatrix() || from.isVector()) // single element matrix/vector -> dint
            return performExplicitVectorOrMatrixConversion(from, to, fromValue);
          
          if (from.isReal()) return new Value( new Long((long)((Double)v).doubleValue()) );
          if (from.isSreal()) return new Value( new Long((long)((Float)v).floatValue()) );
        }
        
        if (to.isInt()) {
          if (from.isMatrix() || from.isVector()) // single element matrix/vector -> int
            return performExplicitVectorOrMatrixConversion(from, to, fromValue);
          
          if (from.isReal()) return new Value( new Integer((int)((Double)v).doubleValue()) );
          if (from.isSreal()) return new Value( new Integer((int)((Float)v).floatValue()) );
          if (from.isDint()) return new Value( new Integer((int)((Long)v).longValue()) );
        }
        
        if (to.isChar()) {
          if (from.isMatrix() || from.isVector()) // single element matrix/vector -> char
            return performExplicitVectorOrMatrixConversion(from, to, fromValue);

          if (from.isReal()) return new Value( new Character((char)((Double)v).doubleValue()) );
          if (from.isSreal()) return new Value( new Character((char)((Float)v).floatValue()) );
          if (from.isDint()) return new Value( new Character((char)((Long)v).longValue()) );
          if (from.isInt()) return new Value( new Character((char)((Integer)v).intValue()) );
        }
        
        if (to.isByte()) {
          if (from.isMatrix() || from.isVector()) // single element matrix/vector -> byte
            return performExplicitVectorOrMatrixConversion(from, to, fromValue);
          
          if (from.isReal()) return new Value( new Byte((byte)((Double)v).doubleValue()) );
          if (from.isSreal()) return new Value( new Byte((byte)((Float)v).floatValue()) );
          if (from.isDint()) return new Value( new Byte((byte)((Long)v).longValue()) );
          if (from.isInt()) return new Value( new Byte((byte)((Integer)v).intValue()) );
          if (from.isChar()) return new Value( new Byte((byte)((Character)v).charValue()) );
        }
        
      }
       
       
      // list -> num, list must have a single element that is convertable to a num
      // string -> num, parse it
      if ((from.isList() || from.isString()) && to.isANum()) {
        
        if (from.isList()) {
          List l = (List)fromValue.getValue();
          if (l.get_size() == 1) {
            Object element = l.get_Item(0);
            TypeSpec eltType = TypeSpec.typeOf(element);
            if (existsImplicitNumericConversion(eltType, to))
              return performImplicitNumericConversion(eltType, to, new Value(element));
          }
          ScigolTreeParser.semanticError("conversion from type 'list' to type '"+to+"' requires the list to have a single element that is convertable to '"+to+"'");
        }
        
        if (from.isString()) {
          if (fromValue.getValue() == null)
            ScigolTreeParser.semanticError("cannot convert a null string to type '"+to+"'");
          
          if (to.isANum()) {
            
            if (!to.isChar()) {
              // parse it as a real, then convert to required smaller type
              double n = 0;
              try {
                n = Double.parseDouble((String)fromValue.getValue());
              } catch (Exception e) {
                ScigolTreeParser.semanticError("cannot convert string to type '"+to+"' - "+e.getMessage());
              }
              
              return performExplicitNumericConversion(new TypeSpec(TypeSpec.realType), to, new Value(new Double(n)));
            }
            else { // string -> char - special case, string must have a single char 
              String fromString = (String)fromValue.getValue();
              if (fromString == null)
                ScigolTreeParser.semanticError("cannot convert a null string into a char");
              if (fromString.length() != 1)
                ScigolTreeParser.semanticError("conversion of a string into a char requires the string to contain a single character");
              return new Value( new Character((char)(fromString.charAt(0))) );
            }
          }
        }
        
      }
      
      Debug.Assert(false,"unimplemented or bad performExplicitNumericConversion("+from+", "+to+")");
      
    } 
    else
      return performImplicitNumericConversion(from, to, fromValue);
    
    return null;
  }
  
  

  
  public static boolean existsExplicitReferenceConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if (from.isBuiltinObject()) return true; // can narrow object to anything
    
    if (from.isString() && to.isBool()) return true;

    if (to.isA(from)) return true; // narrow
    
    if (to.isInterface()) return true;  // can cast to any interface type

    return false;
  }
  
  
  public static Value performExplicitReferenceConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if (!existsExplicitReferenceConversion(from, to, fromValue)) return null;
    
    if (from.isString() && to.isBool()) {
      String fromString = (String)fromValue.getValue();
      if (fromString == null) return new Value(new Boolean(false));
      if (fromString.startsWith("true") || fromString.startsWith("True")) return new Value(new Boolean(true));
      return new Value(new Boolean(false)); // any string that isn't true/True is false by default
    }
    
    
    Debug.Unimplemented("performExplicitReferenceConversion");
    return null;
  }
  
  
  
  
  public static boolean existsExplicitUserDefinedConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    // just try to perform the conversion, if it doesn't work, return false
    
    Value r = performUserDefinedConversion(from, to, fromValue, false);
    return (r != null);    
  }


  public static Value performExplicitUserDefinedConversion( TypeSpec from, TypeSpec to, Value fromValue) 
  {
    return performUserDefinedConversion(from, to, fromValue, false);
  }

  
  
  public static boolean existsExplicitConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if (existsImplicitConversion(from, to, fromValue)) return true;
    if (existsExplicitNumericConversion(from, to, fromValue)) return true;
    if (existsExplicitReferenceConversion(from, to, fromValue)) return true;
    if (existsExplicitUserDefinedConversion(from, to, fromValue)) return true;
    
    return false;    
  }

  
  public static Value performExplicitConversion(TypeSpec from, TypeSpec to, Value fromValue) 
  {
    // although implicit conversion exist from any to other types, sometimes we may encounter
    //  an explicit convertion (same for num)
    if (fromValue.getValue() != null) {
      fromValue = TypeSpec.unwrapAnyValue(fromValue);
      if (from.isAny() || from.isNum()) 
        from = TypeSpec.typeOf(fromValue);
    }
    
    Debug.Assert( (fromValue.getValue() == null) || (TypeSpec.typeOf(fromValue).equals(from)), "fromValue (type "+TypeSpec.typeOf(fromValue)+") must be type of 'from' (= "+from+")");

    // user-defined explicit conversions have precedence, then user-defined implicit 
    //  conversions, followed by the others
//Debug.WL("1:performExplicitConversion "+from+"->"+to);
    Value r = performExplicitUserDefinedConversion(from, to, fromValue);
    if (r != null) return r;

    r = performImplicitUserDefinedConversion(from, to, fromValue);
    if (r != null) return r;
//Debug.WL("1: no performImplicitUserDefinedConversion found");
    // try an implicit conversion
    if (existsImplicitConversion(from, to, fromValue)) 
      return performImplicitConversion(from, to, fromValue);

    if (existsExplicitNumericConversion(from, to, fromValue)) 
      return performExplicitNumericConversion(from, to, fromValue);
    
    return performExplicitReferenceConversion(from, to, fromValue);
  }



  // Standard conversions (those that are used in the context of user-defined conversions,
  //   to convert to the argment type and from the return type of a user-defined conversion
  //   operator
  
  public static boolean existsImplicitStandardConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if (existsImplicitNumericConversion(from, to)) return true;
    if (existsImplicitReferenceConversion(from, to, fromValue)) return true;

    return false;
  }
  
  
  public static boolean existsExplicitStandardConversion(TypeSpec from, TypeSpec to, Value fromValue)
  {
    if (existsImplicitStandardConversion(from, to, fromValue)) return true;
    if (existsExplicitNumericConversion(from, to, fromValue) || existsExplicitReferenceConversion(from, to, fromValue))
      return true; // !!! might not be exactly correct
      
    return false;
  }

  
  
  
  // encompassing / encompassed computations
  
  
  public static boolean encompassedBy(TypeSpec a, TypeSpec b)
  {
    return (!a.isInterface() && !b.isInterface() && existsImplicitStandardConversion(a,b,null));
  }
  
  
  public static boolean encompasses(TypeSpec b, TypeSpec a)
  {
    return (!a.isInterface() && !b.isInterface() && existsImplicitStandardConversion(a,b,null));
  }
  
  
  public static TypeSpec mostEncompassingType(ArrayList types)
  {
    if (types.size() == 0) return null;
    if (types.size() == 1) return (TypeSpec)types.get(0);
    
    TypeSpec mostEncompassing = (TypeSpec)types.get(0);
    for (Object o : types) {
      TypeSpec type = (TypeSpec)o;
      if (encompasses(type, mostEncompassing)) {
//        Debug.WriteLine(""+type+" encompasses "+mostEncompassing);
        mostEncompassing = type;
      }
      else {
//        Debug.WriteLine(""+type+" doesn't encompasses "+mostEncompassing);
        if (!encompasses(mostEncompassing, type)) {
//          Debug.WriteLine(" and "+mostEncompassing+" doesn't encompass "+type);
          return null;
        }
//        else
//          Debug.WriteLine(" and "+mostEncompassing+" encompasses "+type);
          
      }
    }
    
    return mostEncompassing;
  }

  
  public static TypeSpec mostEncompassedType(ArrayList types)
  {
    if (types.size() == 0) return null;
    if (types.size() == 1) return (TypeSpec)types.get(0);
    
    TypeSpec mostEncompassed = (TypeSpec)types.get(0);
    for (Object o : types) {
      TypeSpec type = (TypeSpec)o;
      if (encompassedBy(type, mostEncompassed)) {
        mostEncompassed = type;
      }
      else {
        if (!encompassedBy(mostEncompassed, type)) {
          return null;
        }
      }
    }
    
    return mostEncompassed;
  }
  
  
  
  
  
  // helper
  protected  static class ConvOperator {
      public ConvOperator(TypeSpec t, Entry e) { declaringType=t; methodEntry=e; }
      public TypeSpec    declaringType;
      public Entry methodEntry;
      
      public TypeSpec from() { return TypeManager.conversionOperatorFromType(declaringType, methodEntry); }
      public TypeSpec to() { return TypeManager.conversionOperatorToType(declaringType, methodEntry); }
    };

  
  
  public static Value performUserDefinedConversion(TypeSpec from, TypeSpec to, Value fromValue, boolean implicitConversion) 
  {
    // NB: may be called with fromValue==null just to check if conversion exists
    
    // unwap from if any or num
    if (fromValue != null) {
      
      if (from.isAny()) {
        if (fromValue.getValue() != null) {
          fromValue = new Value(((Any)fromValue.getValue()).value); 
          from = TypeSpec.typeOf(fromValue); 
        }
        else {
          fromValue = new Value(); // null Value
        }
      }
      else if (from.isNum()) {
        if (fromValue.getValue() != null) {
          fromValue = new Value(((Num)fromValue.getValue()).value); 
          from = TypeSpec.typeOf(fromValue); 
        }
        else {
          fromValue = new Value(); // null Value
        }
      }
      else
        fromValue = TypeSpec.unwrapAnyOrNumValue(fromValue);
      
    }

    if ((fromValue!=null) && (fromValue.getValue()!=null))
      Debug.Assert(TypeSpec.typeOf(fromValue).equals(from), "fromValue (type "+TypeSpec.typeOf(fromValue)+") must be of type 'from'="+from);

    
    // (NB: we are treating the builtin class types (like Vector, Matrix) as user-defined here as
    //  then we can utilize their Java method conversions
    boolean fromIsSimpleBuiltin = (from.isBuiltin() && !from.isBuiltinClass()) || from.isBuiltinObject();
    boolean toIsSimpleBuiltin = (to.isBuiltin() && !to.isBuiltinClass()) || to.isBuiltinObject();
    
    if (fromIsSimpleBuiltin && toIsSimpleBuiltin)
      return null; // no user-defined conversions in simple builtin types!
    
    // First, construct a set of operators from 'from' and 'to' that convert from a type
    //  encompassing 'from' to a type encompassed by 'to'
    // (only considering implicit operators, if 'implicit' is true

    Entry[] fromOps =                  getUserDefinedConversionOperatorsOf(from);
    Entry[] toOps = (!from.equals(to))?getUserDefinedConversionOperatorsOf(to):new Entry[0]; // don't duplicate
    
    ArrayList applicableOps = new ArrayList();
    
    for(Entry entry : fromOps) {
      TypeSpec convertFrom = conversionOperatorFromType(from, entry);
      TypeSpec convertTo = conversionOperatorToType(from, entry);
      if ( encompasses(convertFrom,from) && encompassedBy(convertTo, to) 
           && (!implicitConversion || entry.isImplicit() ) )
        applicableOps.add( new TypeManager.ConvOperator(from, entry) );
    }
    
    for(Entry entry : toOps) {
      TypeSpec convertFrom = conversionOperatorFromType(to, entry);
      TypeSpec convertTo = conversionOperatorToType(to, entry);
      if ( encompasses(convertFrom,from) && encompassedBy(convertTo, to) 
           && (!implicitConversion || entry.isImplicit() ) )
        applicableOps.add( new TypeManager.ConvOperator(to, entry) );
    }

    // if the set of applicable operators is empty, there is no user-defined conversion
    if (applicableOps.size() == 0) return null;
    
    
    // Now we need to find the most specific from type and the most specific to type of the conversions
    //  if one of the conversion from types is actually 'from', then thats it,otherwise it is
    //  the most encompassed
    TypeSpec mostSpecificFrom = null;
    for(Object o : applicableOps) {
      ConvOperator op = (ConvOperator)o;
      if (op.from().equals(from)) { mostSpecificFrom = from; break; }
    }
    
    if (mostSpecificFrom == null) { // didn't find 'from' specifically, look for most encompassed
      
      ArrayList fromTypes = new ArrayList(); // make a list of conversion from types
      for(Object op : applicableOps) fromTypes.add( ((ConvOperator)op).from());
      
      mostSpecificFrom = mostEncompassedType(fromTypes);
      
      if (mostSpecificFrom == null) // no most encompassed type
        ScigolTreeParser.semanticError(""+(implicitConversion?"implicit ":"")+"conversion from type '"+from+"' to type '"+to+"' is ambiguous");
    }
    
    
    // OK, now do the analogous thing for the convert to types
    TypeSpec mostSpecificTo = null;
    for(Object op : applicableOps)
      if (((ConvOperator)op).to().equals(to)) { mostSpecificTo = to; break; }
    
    if (mostSpecificTo == null) { // didn't find 'to' specifically, look for most encompassing
      
      ArrayList toTypes = new ArrayList(); // make a list of conversion to types
      for(Object op : applicableOps) toTypes.add(((ConvOperator)op).to());
      
      mostSpecificTo = mostEncompassingType(toTypes);
      
      if (mostSpecificTo == null) // no most encompassing type
        ScigolTreeParser.semanticError(""+(implicitConversion?"implicit ":"")+"conversion from type '"+from+"' to type '"+to+"' is ambiguous");
    }
    

    // Now find an 'operator' in applicableOps that converts from 'mostSpecificFrom' to 'mostSpecificTo'
    //  if there are none or more that one, the conversion is ambiguous
    
    ConvOperator conversionOp = null;
    for(Object o : applicableOps) {
      ConvOperator op = (ConvOperator)o;
      if (op.from().equals(mostSpecificFrom) && op.to().equals(mostSpecificTo)) {
        if (conversionOp == null)
          conversionOp = op; // found one
        else  {// alreay had one, more than one is ambiguous
//Debug.WL("HERE had "+op.from()+"->"+op.to()+" in "+conversionOp.declaringType+" don't need in "+op.declaringType);
          ScigolTreeParser.semanticError(""+(implicitConversion?"implicit ":"")+"conversion from type '"+from+"' to type '"+to+"' is ambiguous");
}          
      }
    }
    
    if (conversionOp == null) // didn't find any
      ScigolTreeParser.semanticError(""+(implicitConversion?"implicit ":"")+"conversion from type '"+from+"' to type '"+to+"' is ambiguous");
      
    
    // if we don't have a fromValue to convert, this is as far as we go.  Just return
    //  a non-null *Value* to indicate that the conversion exists
    if (fromValue==null) return new Value(); // null Value
    
    
    // Finally, we can actually perform the conversion using 'conversionOp'
    // 3 steps:
    //  1) convert                                   from -> mostSpecificFrom
    //  2) use conversionOp to convert   mostSpecificFrom -> mostSpecificTo
    //  3) convert                         mostSpecificTo -> to
    
    Value v = fromValue;
    if (!from.equals(mostSpecificFrom)) 
      v = performImplicitConversion(from, mostSpecificFrom, v); // 1
//Debug.WriteLine("STEP2, calling conversion operator:"+mostSpecificFrom+"->"+mostSpecificTo+" of "+conversionOp.declaringType+" : "+conversionOp.methodEntry);
    v = callConversionOperator(conversionOp.declaringType, conversionOp.methodEntry, v); // 2
    Debug.Assert(v != null, "call of conversion operator failed");
    Debug.Assert(TypeSpec.typeOf(v).equals(mostSpecificTo), "unexpected return type from conversion operator");
    
    if (!to.equals(mostSpecificTo))
      v = performImplicitConversion(mostSpecificTo, to, v); // 3

    return v;    
  }
  
  
  
  
  // function/method overload resolution
  
  // returns >0 if from->to1 is a better conversion than from->to2
  //         =0 if neither is better
  //         <0 if from->to2 is a better conversion than from->to1
  public static int betterConversion(TypeSpec from, TypeSpec to1, TypeSpec to2, Value fromValue)
  {
    if (to1.equals(to2)) return 0;
    
    if (from.equals(to1)) return 1;
    if (from.equals(to2)) return -1;
    
    // unwrap 'num' or 'any'
    if ((fromValue != null) && (fromValue.getValue() != null)) {
      if (from.isAny()) {
        fromValue = new Value( ((Any)fromValue.getValue()).value );
        from = TypeSpec.typeOf(fromValue.getValue());
      }
      else if (from.isNum()) {
        fromValue = new Value( ((Num)fromValue.getValue()).value );
        from = TypeSpec.typeOf(fromValue.getValue());
      }
    }
    
    if (from.equals(to1)) return 1;
    if (from.equals(to2)) return -1;
    
    boolean implicitConversion1to2Exists = existsImplicitConversion(to1,to2,null);
    boolean implicitConversion2to1Exists = existsImplicitConversion(to2,to1,null);
    
    if (implicitConversion1to2Exists && !implicitConversion2to1Exists) return 1;
    if (implicitConversion2to1Exists && !implicitConversion1to2Exists) return -1;
    
    return 0;
  }
  
  
  // true if P is a better match to callSig than Q with args 'args'
  public static boolean betterFuncMatch(FuncInfo P, FuncInfo Q, FuncInfo callSig, Object[] args)
  {
    Debug.Assert((args == null) || (args.length == callSig.numArgs()));
    Debug.Assert(callSig.numArgs() >= P.numRequiredArgs());
    Debug.Assert(callSig.numArgs() >= Q.numRequiredArgs());
    Debug.Assert(callSig.numArgs() <= P.numArgs());
    Debug.Assert(callSig.numArgs() <= Q.numArgs());
    
    boolean atLeastOneArgPbetterQ = false;
    
    for(int a=0; a<callSig.numArgs(); a++) {
      
      TypeSpec fromArgType = callSig.getParamTypes()[a];
      Value fromArg = (args!=null)?new Value(args[a]):null;
      TypeSpec argPType = P.getParamTypes()[a];
      TypeSpec argQType = Q.getParamTypes()[a];
      
      int c = betterConversion(fromArgType, argPType, argQType, fromArg);
      
      boolean PbetterQ = (c > 0);
      boolean PworseQ = (c < 0);
      if (PworseQ) return false;
      if (PbetterQ) atLeastOneArgPbetterQ = true;
      
    }
    
    return atLeastOneArgPbetterQ;
  }
  
  
  
  // find a candidate func that is better than all the other candidates
  //  returns either 0 entries if 0 candidates, 1 entry if there exists an unambiguously best func
  //  or all candidates if there isn't any single best func entry
  public static Entry[] bestFuncMatch(Entry[] candidates, FuncInfo callSig, Object[] args)
  {
    if (candidates.length <= 1) return candidates; // 0 or 1 (no candidates, or already unamgiguous)
    
    for(Entry candidateBest : candidates) {
      boolean betterThanAll = true;
      for(Entry candidate : candidates) {
        if (    (candidate!=candidateBest) 
             && !betterFuncMatch(candidateBest.type.getFuncInfo(), candidate.type.getFuncInfo(), callSig, args)) {
          betterThanAll = false;
          break;
        }
      }
      
      if (betterThanAll) {
        Entry[] e = new Entry[1];
        e[0] = candidateBest;   // found one unambiguously best
        return e; 
      }
    }
    
    return candidates; // ambiguous - no best exists, all are candidates still
  }

  
  
  // filter the list of candidates func's to those applicable to callSig with args 'args'
  public static Entry[] applicableFuncs(Entry[] candidates, FuncInfo callSig, Object[] args)
  {
    if (candidates.length == 0) return candidates;
    
    // a candidate is applicable if it has an appropriate number of arguments & an implicit conversion
    //  exists from each argument type to its corresponding parameter type
    ArrayList applicableEntries = new ArrayList();
    for(Entry entry : candidates) {
      boolean isFunc = entry.type.isFunc(); // non-funcs definately aren't applicable
      FuncInfo candidateSig = isFunc?entry.type.getFuncInfo():null;
      
      if (isFunc) {
        // has enough args, but not too many
        if ((callSig.numArgs() >= candidateSig.numRequiredArgs()) && (callSig.numArgs() <= candidateSig.numArgs()))
        {
          boolean conversionsExist = true;
          for(int a=0; (a<callSig.numArgs()) && conversionsExist;a++) {
            if (!existsImplicitConversion(callSig.getParamTypes()[a], candidateSig.getParamTypes()[a], new Value(args[a])))
              conversionsExist = false;
          }
          
          if (conversionsExist)
            applicableEntries.add(entry);
        }
      }
    }
    
    return Entry.toArray(applicableEntries);
  }
  
  
  
  
  // resolve overloaded func given some candidates
  //  returns 0 element array if no candidates matches callSig, 1 element if a best candidate exists,
  //  or all applicable candidates if ambiguous
  // (if callSig is null, returns all candidates)
  public static Entry[] resolveOverload(Entry[] candidates, FuncInfo callSig, Object[] args)
  {
    if (callSig == null) return candidates;
    
    Entry[] applicable = applicableFuncs(candidates, callSig, args);
    if (applicable.length <= 1) return applicable; // if nothing or just one applicable, return now
    
    return bestFuncMatch(applicable, callSig, args);
  }
    
  
  
  
  
  
  
  // user defined operator overloading (i.e. +, -, ++, etc. etc.)

  
  public static Entry[] getUserDefinedOverloadOperatorsOf(TypeSpec t, String opname, FuncInfo callSig, Object[] args)
  {
    if (!t.isClass() && !t.isBuiltinClass()) return new Entry[0]; // only classes can overload operators
    
    // if class t has any 'opname' operators, then they are the candiates, otherwise
    //  we look in the base class (recursively) for operators
    //  (i.e. the candidate set never has operators from both class t and a base of t)

    boolean foundOperators = false;
    Entry[] ops = null;
    ArrayList candidates = null;
    TypeSpec classType = t;
    while ((classType != null) && !classType.isBuiltinObject() && !foundOperators) {
      
      ops = classType.getClassInfo().getDeclaredEntries(opname);

      // filter out abstract, non-static and non-applicable ops
      ops = applicableFuncs(ops, callSig, args);
      candidates = new ArrayList();
      if (ops.length > 0) {
        for(Entry e : ops) {
//Debug.WL("applicable op for "+t+" with signature "+callSig+" is "+e.name+":"+e.type);
          if (e.isStatic() && !e.isAbstract())
            candidates.add(e);
        }
      }
      
      if (candidates.size()>0)
        foundOperators = true;
      else 
        classType = classType.getClassInfo().getBaseClass();
    }

    return Entry.toArray(candidates);   
  }
  
  
  
  // find applicable user-defined operators based on callSig parameter types
  //  returns 0 if none found, 1 is a uniquely best choice available, or >1 if ambiguous
  // NB: builtin operators are not considered
  public static Entry[] resolveOperatorOverload(String opname, FuncInfo callSig, Object[] args)
  {
    Debug.Assert(callSig != null, "callSig required");

    // the candidate operators are drawn from the types of the arguments
    //  (after unwrapping any & num)
    
    FuncInfo newCallSig = new FuncInfo(callSig);
    Object[] newArgs = (Object[])args.clone();
    
    for(int a=0; a < newCallSig.numArgs(); a++) {
      TypeSpec ptype = newCallSig.getParamTypes()[a];
      if (ptype.isAny() || ptype.isNum()) {
        newArgs[a] = TypeSpec.unwrapAnyOrNum(newArgs[a]);
        newCallSig.getParamTypes()[a] = TypeSpec.typeOf(newArgs[a]);
      }
    }
    
    
    ArrayList candidates = new ArrayList();
    
    for(int a=0; a < newCallSig.numArgs(); a++) {
      
      TypeSpec ptype = newCallSig.getParamTypes()[a];
      
      // don't get operators for types we've already seen
      boolean seen = false;
      for(int p=0; (p<a) && !seen; p++)
        if (newCallSig.getParamTypes()[p].equals(ptype)) 
          seen=true;
          
      Entry[] ops = new Entry[0];
      if (!seen)      
        ops = getUserDefinedOverloadOperatorsOf( ptype, opname, newCallSig, args );
      
      for(Entry e : ops) candidates.add(e);
    }
    
    // if there is more than one candidate, locate the best
    //  (by analogy with normal method overload resolution)
//!!! do we want to use newCallSig here or not??    
    return bestFuncMatch(Entry.toArray(candidates), callSig, args);
  }
  
  
  
  
  
  
}

