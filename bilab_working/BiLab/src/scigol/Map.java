package scigol;

import java.util.*;

  
// Map (aka hashtable, associative array, dictionary)  
public class Map extends HashMap // !!! make this implement an interface and wrap the HashMap instead
{
  public Map()
  {
    super(2);
  }

  
  
  public void add(Object key, Object value)
  {
    key = TypeSpec.unwrapAnyOrNum(key);
    value = TypeSpec.unwrapAnyOrNum(value);
    super.put(key, value);
  }
  
  
  public boolean contains(Object key)  
  {
    return( super.containsKey( TypeSpec.unwrapAnyOrNum(key) ) );
  }

  public Object remove(Object key)  
  {
    return super.remove( TypeSpec.unwrapAnyOrNum(key) );
  }

  
  
  @accessor
  public Any get_Item(Object key)
  {
    key = TypeSpec.unwrapAnyOrNum(key);
    return new Any(get(key));
  }
    
  @accessor
  public void set_Item(Object key, Any value)
  {
    key = TypeSpec.unwrapAnyOrNum(key);
    put(key, TypeSpec.unwrapAnyOrNum(value));
  }
  


  public static int op_Card(Map m)
  {
    return m.size();
  }
  
  
  
  
  public String toString()
  {
    String s = null;
    int size = keySet().size();
    if (size > 1) {
      s = "[\n";
      for(Object key : keySet()) {
        Object value = get(key);
        s += " "+key.toString()+" -> "+((value!=null)?value.toString():"null")+"\n";
      }
      s += "]";
    }
    else {
      if (size == 0)
        s = "[]";
      else {
        Object key = keySet().iterator().next();
        Object value = get(key);
        s = "[ "+key.toString()+" -> "+((value!=null)?value.toString():"null")+" ]";
      }
    }
    return s;
  }
  
  
}

