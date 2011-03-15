package scigol;


import java.util.*;

  
public class Vector
{
  public Vector()
  {
    a = new ArrayList();
  }

  
  public Vector(double d)
  {
    a = new ArrayList();
    a.add(new Double(d));
  }
  
  
  public Vector(List l) 
  {
    a = new ArrayList();
    
    for(int i=0; i<l.size();i++) 
      appendElement( ((Any)l.get_Item(i)).value );
  }
  
  
  
  // dot product
  public double dot(Vector v)
  {
    if (v.op_Card() != op_Card())
      throw new IllegalArgumentException("vector arguments must have same dimension");
    
    double dp = 0;
    
    for(int a=0; a<op_Card();a++) {
      Object v1o = get_Item(a).value;
      Value v1e = new Value(v1o);
      Double element1 = (Double)(TypeManager.performImplicitConversion(TypeSpec.typeOf(v1e), TypeSpec.realTypeSpec, v1e).getValue());
      
      Object v2o = v.get_Item(a).value;
      Value v2e = new Value(v2o);
      Double element2 = (Double)(TypeManager.performImplicitConversion(TypeSpec.typeOf(v2e), TypeSpec.realTypeSpec, v2e).getValue());
 
      dp += element1.doubleValue() * element2.doubleValue();
    }

    return dp;
  }
  
  
  // zero vector of given dimension
  public static Vector zero(int dim)
  {
    Vector v = new Vector();
    for(int d=0; d<dim; d++)
      v.a.add(new Double(0));
    return v;
  }
  
  
  
  
  
  @accessor
  public int get_size()
  {
    return a.size();
  }
  
  
  
  @accessor
  public Vector get_Item(Range r) 
  {
    r = r.normalize(a.size());
    
    if ((r.start<0) || (r.start>=a.size()))
      throw new IndexOutOfBoundsException("vector range start "+r.start+" out of range 0.."+(get_size()-1));
    if ((r.end<0) || (r.end>=a.size()))
      throw new IndexOutOfBoundsException("vector range end "+r.end+" out of range 0.."+(get_size()-1));

    if (r.start>r.end) return new Vector();
    if (r.start==r.end) {
      Vector v = new Vector();
      v.appendElement(a.get(r.start));
      return v;
    }
    
    Vector vec = new Vector();
    for(int i=r.start; i<=r.end; i++)
      vec.a.add(a.get(i));
    return vec;
  }

  
  @accessor
  public void set_Item(Range r, Vector value) 
  {
    r = r.normalize(a.size());
    
    if ((r.start<0) || (r.start>=a.size()))
      throw new IndexOutOfBoundsException("vector range start "+r.start+" out of range 0.."+(get_size()-1));
    if ((r.end<0) || (r.end>=a.size()))
      throw new IndexOutOfBoundsException("vector range end "+r.end+" out of range 0.."+(get_size()-1));
    
    if (r.start>r.end) {
      if (value.get_size() != 0)
        throw new IllegalArgumentException("can't assign vector with "+value.get_size()+" elements to a range of 0 elements");
      return;
    }
    if (r.start==r.end) {
      a.set(r.start, value.a.get(0));
      if (value.get_size() != 0)
        throw new IllegalArgumentException("can't assign vector with "+value.get_size()+" elements to a range of 1 element");
      return;
    }
    
    if (value.get_size() != (r.end-r.start+1))
      throw new IllegalArgumentException("can't assign vector with "+value.get_size()+" elements to a range of "+(r.end-r.start+1)+" elements");
    
    for(int i=r.start; i<=r.end; i++)
      a.set(i, value.a.get(i-r.start));
    
  }
  
  
  
  @accessor
  public Any get_Item(int i)
  {
    if ((i >= 0) && (i < a.size()))
      return new Any(a.get(i));
    else
      throw new IndexOutOfBoundsException("vector index "+i+" out of range 0.."+a.size());
  }
  

  @accessor
  public void set_Item(int i, Any value)
  {
    if ((i >= 0) && (i < a.size()))
      a.set(i, value.value);
    else
      throw new IndexOutOfBoundsException("vector index "+i+" out of range 0.."+a.size());
  }
  
  
  
  
  public static Vector op_UnaryNegation(Vector v) 
  {
    Vector nv = new Vector();
    for(int a=0; a<v.op_Card();a++) {
      Object ve = v.get_Item(a);
      
      Value e = new Value(ve);
      
      nv.appendElement( Math.performOverloadedOperation("operator-",null,e).getValue() );
    }
    return nv;
  }
  
  
  
  public static Vector op_Subtraction(Vector v1, Vector v2) 
  {
    //!! temp impl.
    Debug.Assert(v1.op_Card() == v2.op_Card(), "vector sized don't match (this should be a semantic error)");
    
    Vector mv2 = op_UnaryNegation(v2);
    return op_Addition(v1,mv2);
  }
  
  
  public static Vector op_Addition(Vector v1, Vector v2) 
  {
    //!! temp impl.
    Debug.Assert(v1.op_Card() == v2.op_Card(), "vector sized don't match (this should be a semantic error)");
    
    Vector v3 = new Vector();
    
    for(int a=0; a<v1.op_Card();a++) {
      Object v1e = v1.get_Item(a);
      Object v2e = v2.get_Item(a);
      
      Value e1 = new Value(v1e);
      Value e2 = new Value(v2e);
      
      v3.appendElement( Math.performOverloadedOperation("operator+",e1,e2).getValue() );
    }
    
    return v3;
  }
  
  
  public static Vector op_Multiply(Vector v1, Double s)
  {
    //!!! hack for demo!!!
    Vector v2 = new Vector();
    
    for(int a=0; a<v1.op_Card();a++) {
      Object v1o = v1.get_Item(a).value;
      Value v1e = new Value(v1o);
      Double element = (Double)(TypeManager.performImplicitConversion(TypeSpec.typeOf(v1e), TypeSpec.realTypeSpec, v1e).getValue());
      
      Double e1 = new Double(element.doubleValue()*s.doubleValue());
      
      v2.appendElement( e1 );
    }
    
    return v2;
  }
  
  

  public static int op_Card(Vector v)
  {
    return v.op_Card();
  }
  
  //!!! remove this
  public int op_Card()
  {
    return a.size();
  }
  
  
  public static Object op_Norm(Vector v) 
  {
    if (v.a.get(0) instanceof Double) {
      double ss = 0;
      for(int i=0; i<v.a.size(); i++)
        ss += ((Double)v.a.get(i)).doubleValue() * ((Double)v.a.get(i)).doubleValue();
      return new Double(java.lang.Math.sqrt(ss));
    }
    else if (v.a.get(0) instanceof Float) {
      double ss = 0;
      for(int i=0; i<v.a.size(); i++)
        ss += (double)( ((Float)v.a.get(i)).floatValue() * ((Float)v.a.get(i)).floatValue() );
      return new Double(java.lang.Math.sqrt(ss));
    }
    else 
      ScigolTreeParser.semanticError("can't compute the norm (operator||) of non-numeric vector elements");
    return null;
  }


  //@hide or something!!!
  public void appendElement(Object e) 
  {
    if (a.size() > 0) {
      TypeSpec etype = new TypeSpec(a.get(0).getClass());  // existing element type
      TypeSpec ntype = new TypeSpec(e.getClass()); // new element type
      if (!etype.equals(ntype) ) {
        if (!TypeManager.existsImplicitConversion(etype,ntype,new Value(e)))
          ScigolTreeParser.semanticError("expected element of type '"+etype+"', not '"+ntype+"'");
        else { // convert existing elements to wider type
          ArrayList na = new ArrayList();
          for(int i=0; i<a.size(); i++)
            na.add( TypeManager.performImplicitConversion(etype, ntype, new Value(a.get(i))).getValue() );
          a = na;
        }
      }
    }

    a.add(e);
  }
  
  
  public String toString()
  {
    String s = "[";
    for(int i=0; i<a.size(); i++) {
      s += a.get(i).toString();
      if (i!=a.size()-1) s+=" ";
    }
    s += "]";
    return s;
  }
  
  
  protected ArrayList a;
  
}

