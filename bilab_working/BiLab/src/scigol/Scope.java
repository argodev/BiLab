package scigol;


import antlr.collections.AST;

import java.util.*;

  
/// a scope for identifiers (in namespaces, local blocks, classes)
public abstract class Scope
{
  protected Scope() 
  {
    _outer = null;
    _location = null;
  }
  
  public boolean isNamespaceScope() { return false; }
  public boolean isLocalScope() { return false; }
  public boolean isClassScope() { return false; }
  
  public abstract Entry[] getEntries(String name, Object instance);
  
  public abstract Entry addEntry(Entry e);
  

  // performs overload resolution and enclosing scope lookups to try and revolve name to a single entry
  //  returns 0 element array if no match found, or >1 elements if ambiguous
  // (callSig may be null, in which case multiple entries will be returned for ambiguous names)
  // (args may be null if the argument values are not known.  If known, resolution will look into
  //  'any' and 'num' type arguments to discover their actual type)
  public abstract Entry[] lookup(String name, FuncInfo callSig, Object[] args, Object instance);

  
  // convenience
  public boolean contains(String name) { return getEntries(name,null).length != 0; }
  
  public Entry addEntry(String name, TypeSpec type, Object staticValue, AST initializer, EnumSet<TypeSpec.Modifier> modifiers, Scope declaringScope)
  { return addEntry(new Entry(name, type, staticValue, initializer, modifiers, EnumSet.noneOf(Entry.Flags.class), -1, declaringScope)); }

  public Entry addEntry(String name, TypeSpec type, Object staticValue, AST initializer, EnumSet<TypeSpec.Modifier> modifiers)
  { return addEntry(new Entry(name, type, staticValue, initializer, modifiers, EnumSet.noneOf(Entry.Flags.class), -1, this)); }

  public Entry addEntry(String name, TypeSpec type, Object staticValue, AST initializer, EnumSet<TypeSpec.Modifier> modifiers, Location l)
  { return addEntry(new Entry(name, type, staticValue, initializer, modifiers, EnumSet.noneOf(Entry.Flags.class), -1, this, l)); }
  
  
  // get enclosing Scope, or null if none
  public Scope getOuter() { return _outer; }
  public void setOuter(Scope value) { _outer = value; }
  
  
 
 
  // traverse back up the outer Scope list until the global scope (or return null if unavailable)
  public NamespaceScope getGlobalScope()
  {
    Scope s = this;
    while (s._outer != null)
      s = s._outer;
    if (s.isNamespaceScope())
      return (NamespaceScope)s;
    else
      return null;
  }
                                                                                                                                                                                                    
  
  public Location getDefinitionLocation()
  {
    if (_location==null) return new Location(); else return _location; 
  }
  
  public void setDefinitionLocation(Location value)
  {
    _location = value;
  }
  
  
  
  
  // helpers
  
    // utilitys so that a declaration can register the ident being
  //  declared for retrieval by a class type definition (for example) to use
  //  a sensible class name for injection into the CLI type system
  public void pushDeclarationIdent(String id)
  {
    identDeclarationStack.add(id);
  }
  
  public String popDeclarationIdent()
  {
    String id = null;
    if (identDeclarationStack.size() > 0) {
      id = (String)identDeclarationStack.get(identDeclarationStack.size()-1);
      identDeclarationStack.remove(id);
    }
    return id;
  }
  
  public String topDeclarationIdent()
  {
    if (identDeclarationStack.size() > 0) 
      return (String)identDeclarationStack.get(identDeclarationStack.size()-1);
    else
      return null;
  }
  
  
  
  protected ArrayList identDeclarationStack = new ArrayList();

  
  
  
  
  
  protected Scope    _outer;       // enclosing scope, or null
  protected Location _location;    // location where scope defined (if any)
  
}

