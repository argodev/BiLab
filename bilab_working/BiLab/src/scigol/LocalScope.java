package scigol;

import java.util.*;

  
/// a scope for local identifiers in a block
public class LocalScope extends Scope
{
  public LocalScope(Scope outer)
  {
    _entries = new Hashtable();
    _outer = outer;
  }


  public boolean isLocalScope() { return true; }

  
  
  public Entry[] getEntries(String name, Object instance)
  {
    if (_entries.containsKey(name))
      return Entry.toArray((ArrayList)_entries.get(name));
    else
      return new Entry[0];
  }

  
  public Entry addEntry(Entry e)
  {
    e.scope = this;
    
    // find mapped list for this name and add e; if no existing map list, create a new one
    String name = e.name;
    ArrayList el = (ArrayList)_entries.get(name);
    if (el == null) {
      el = new ArrayList();
      el.add(e);
      _entries.put(name, el);
    }
    else {
      el.add(e);
    }
    return e;
  }

  
  

  public Entry[] lookup(String name, FuncInfo callSig, Object[] args, Object instance)
  {
    // if this block contains any definition of name at all, it hides those in
    //  any outer scope, so just perform overload resolution (if necessary) to resolve it
    // (otherwise, defer to the outer scope)
    if (contains(name)) {
      Entry[] matches = getEntries(name, instance);
      if (matches.length == 1) return matches;
      // overloaded func, try to resolve it (will return 0 elements if no match, or >1 elements if ambiguous)
      matches = TypeManager.resolveOverload(matches, callSig, args);
      // if found a unique or ambiguous match, return, otherwise defer to outer scope
      if (matches.length > 0) return matches; 
    }

    if (getOuter() != null) 
      return getOuter().lookup(name, callSig, args, instance);
    else
      return new Entry[0];
  }

  
  public boolean contains(String name) { return _entries.containsKey(name); }


  
  protected Hashtable _entries; // identifier name keyed map of ArrayList's of Entrys

}

