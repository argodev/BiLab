package scigol;


import java.lang.reflect.*;
import java.io.*;
import java.util.jar.*;
import java.util.*;


  
/// scope for identifiers within a namespace
public class NamespaceScope extends Scope
{
  protected NamespaceScope()
  {
    _namespaceName = "";
    _namespaceType = NamespaceType.Local;
    _namespaces = null;
    _outer = null;
    _entries = new Hashtable();
    _usings = new Hashtable();
  }
 
 
  // construct new *local* namespace
  public NamespaceScope(String name, Scope outer)
  {
    Debug.Assert(outer.isNamespaceScope(), "namespaces can only be nested within namespaces");
    
    _namespaceName = name;  // partially qualified name
    _namespaceType = NamespaceType.Local;
    _namespaces = null;
    _outer = outer;
    _entries = new Hashtable();
    _usings = new Hashtable();
    
    NamespaceScope gs = getGlobalScope();
    
    // add ourself to the global table of local namespaces
    String fullName = name;
    String outerFullName = ((NamespaceScope)_outer).fullNamespaceName(); // this is outer of 'new' one
    if (outerFullName != "") fullName = outerFullName + "." + fullName;
    gs._namespaces.put(fullName, this);
  }

  
  public boolean isNamespaceScope() { return true; }

  
  
  // if name is an existing namespace of outer, return it; otherwise construct
  //  a new local namespace
  public static NamespaceScope newOrExistingNamespaceScope(String name, Scope outer)
  {
    Debug.Assert(outer.isNamespaceScope(), "namespaces can only be nested within namespaces");
    NamespaceScope outerNamespace = (NamespaceScope)outer;
    
    // construct fully qualified name
    String fullName = name;
    String outerFullName = outerNamespace.fullNamespaceName(); // this is outer of 'new' one
    if (outerFullName != "") fullName = outerFullName + "." + fullName;
    
    NamespaceScope existingNamespace = outerNamespace.getNamespaceScope(fullName);
    if (existingNamespace != null)
      return existingNamespace;
    else
      return new NamespaceScope(name, outer);
  }
  
  
  
  public static NamespaceScope newGlobalNamespaceScope()
  {
    NamespaceScope ns = new NamespaceScope();
    ns.initGlobalScope();
    ns._namespaceType = NamespaceType.Local;
    return ns;
  }
  
  
  protected void initGlobalScope()
  {
    _namespaceName = "";
    _outer = null;
    _namespaces = new Hashtable();
    _usings = new Hashtable();
  }
  
  
  
  
  public String fullNamespaceName()
  {
    String fn = _namespaceName;
    Scope s = this;
    while (s.getOuter() != null) {
      s = s.getOuter();
      if ((s instanceof NamespaceScope) && (((NamespaceScope)s).getNamespaceName().length() > 0))
        fn = ((NamespaceScope)s).getNamespaceName() + "." + fn;
    }
    return fn;
  }
  
  
  

  public String getNamespaceName()
  {
    return _namespaceName; 
  }

  
  public boolean isExternal()
  {
    return (_namespaceType == NamespaceType.External);
  }
  
  
  
  
  // get Scope for fully qualified namespace, or null if not found
  public NamespaceScope getNamespaceScope(String fullName)
  {
    NamespaceScope gs = (NamespaceScope)getGlobalScope();

    if (gs._namespaces.containsKey(fullName)) // found an existing ns
      return (NamespaceScope)gs._namespaces.get(fullName);
    
    // look for an extern Java namespace
    if (loadedLibrariesContainNamespace(fullName)) {
      // create an external namespace
      NamespaceScope ns = new NamespaceScope();
      ns._namespaceName = fullName;
      ns._outer = gs;
      ns._usings = new Hashtable();
      ns._namespaceType = NamespaceType.External;
      return ns;
    }
    
    return null;
  }
  
  
  
  
  /// get all declared (non-using/non-alias) entries in the current scope named 'name' (or all if name==null)
  public Entry[] getDeclaredEntries(String name, Object instance)
  {
    ArrayList nsentries = new ArrayList();
    
    if (_namespaceType == NamespaceType.Local) {
      if (name!=null) {
        ArrayList entryList = null;
        if (_entries.containsKey(name)) 
          entryList = (ArrayList)_entries.get(name);
        
        if (entryList != null)
          for (Object o : entryList) {
            Entry entry = (Entry)o;
            nsentries.add(entry); 
          }
      }
      else { // get all
        for (Object o : _entries.keySet()) {
          String key = (String)o;
          ArrayList entryList = (ArrayList)_entries.get(key);
          for(Object eo : entryList) {
            Entry entry = (Entry)eo;
            nsentries.add(entry);
          }
        }
      }
      
    }
    
    //  now external Java packages
    //  
    //  NB: There can be local scigol namespaces with the same names as Java packages.
    //  in this case we return boht the local entries and any we find in the Java package
    
    //  there are no overloaded names in Java packages, so if a name is supplied,
    //  then it can refer to at most one entry.  If no name is supplied return
    //  the names all all Types in the java package
    
    if (name != null) {
      
      // create an entry for Java type
      String fullName = _namespaceName+"."+name;
      Type t = loadedLibrariesGetType(fullName);
      if (t != null) {
        
        Entry entry = new Entry(name, new TypeSpec(TypeSpec.typeType), 
            new TypeSpec(t), null, EnumSet.of(TypeSpec.Modifier.Public), EnumSet.noneOf(Entry.Flags.class),-1,this);
        if (t instanceof java.lang.Class)
        entry.addAnnotations( ((java.lang.Class)t).getAnnotations() );
        
        nsentries.add(entry);
      }      
    }
    else { //  get all entries
      
      if (_namespaceName.length() > 0) {
      
        Package p = Package.getPackage(_namespaceName);
        if (p != null) {
          
          // package exists, so now get all the classes in it from the loaded libraries list
          LinkedList<String> classes = loadedPackages.get(_namespaceName);
          if (classes != null) {
            // need to create an entry for each one
            for(String className : classes) {
              
              String fullName = _namespaceName+"."+className;

              Type t = loadedLibrariesGetType(fullName);
              if (t != null) {
                Entry entry = new Entry(className, new TypeSpec(TypeSpec.typeType), 
                    new TypeSpec(t), null, EnumSet.of(TypeSpec.Modifier.Public), EnumSet.noneOf(Entry.Flags.class),-1,this);
                if (t instanceof java.lang.Class)
                  entry.addAnnotations( ((java.lang.Class)t).getAnnotations() );
                nsentries.add(entry);
              }
              //else
              //  Debug.Warning("class '"+fullName+"' was in the loaded package list, but the ClassLoader was unable to find it");
            }
          }
          
        }
      
      }
    }
    
    
    return Entry.toArray(nsentries);
  }  
  
  
  
  
  
  /// get all entries in the current scope named 'name' (or all if name==null)
  public Entry[] getEntries(String name, Object instance)
  {
    ArrayList nsentries = new ArrayList();
    Entry[] declaredEntries = getDeclaredEntries(name, instance);
    for(Entry e : declaredEntries) nsentries.add(e);
      
    
    // now add using/alias Entries
    if (_namespaceType == NamespaceType.Local) {
      
      if ((name==null) || containsUsing(name)) {
        Entry[] usingEntries = getUsingEntries(name);
        for(Object o : usingEntries) {
          Entry e = (Entry)o;
          nsentries.add(e);
        }
      }
        
    }
  
    return Entry.toArray(nsentries);
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
      _entries.put(name,el);
    }
    else {
      el.add(e);
    }
    return e;
  }
  

  public Entry[] lookup(String name, FuncInfo callSig, Object[] args, Object instance)
  {
    // if this namespace contains any definition of name at all, it hides those in
    //  any outer namespace, so just perform overload resolution (if necessary) to resolve it
    // (otherwise, defer to the outer namespace scope)
    if (contains(name)) {
      Entry[] matches = getEntries(name, instance);
      if (matches.length == 1) return matches;
      // overloaded func, try to resolve it (will return 0 elements if no match, or >1 elements if ambiguous)
      matches = TypeManager.resolveOverload(matches, callSig, args);
      // if found a unique or ambiguous match, return, otherwise defer to outer scope
      if (matches.length > 0) return matches; 
    }
    else if (containsUsing(name)) { // check using names
      Entry[] matches = getUsingEntries(name);
      if (matches.length == 1) return matches;
      // overloaded func, try to resolve it (will return 0 elements if no match, or >1 elements if ambiguous)
      matches = TypeManager.resolveOverload(matches, callSig, args);
      // if found a unique or ambiguous match, return, otherwise defer to outer scope
      if (matches.length > 0) return matches; 
    }

    if (_outer != null) {
      return _outer.lookup(name, callSig, args, instance);
    }
    else
      return new Entry[0];
  }

  

  public boolean contains(String name) 
  {
    if (_namespaceType == NamespaceType.Local) {
      return (_entries.containsKey(name) || containsUsing(name));
    }
    else { // external
      String fullName = _namespaceName+"."+name;

      return (loadedLibrariesGetType(fullName) != null);
    }
  }
  

  // to addUsingNamespace for all known top-level namespaces 
  //  (e.g. use in interactive interpreter)
  public void usingAll() 
  {
    Debug.Assert(_namespaceName != null);

    for(Object o : _namespaces.keySet()) {
      String nsName = (String)o;
      addUsingNamespace(nsName);   
    }
  }
  
  
  public void addUsingNamespace(String namespaceName) 
  {
    Debug.Assert(namespaceName != null);

    NamespaceScope namespaceScope = getNamespaceScope(namespaceName);
    if (namespaceScope == null)
      ScigolTreeParser.semanticError(_location,"no namespace named '"+namespaceName+"' can be found");

    if (_usings.containsKey(namespaceName)) return; // already using
    
    _usings.put(namespaceName, new UsingEntry(namespaceScope));
  }  


  
  public void addUsingName(String aliased, String namespaceName) 
  { addUsingAlias(null, aliased, namespaceName); }
  
  
  public void addUsingAlias(String alias, String aliased, String namespaceName) 
  {
    NamespaceScope namespaceScope = (namespaceName.length()>0)?getNamespaceScope(namespaceName):this;
    if (namespaceScope == null)
      ScigolTreeParser.semanticError(_location,"no name '"+namespaceName+"."+aliased+"' can be found");
    
    if (alias != null) {
      if (_usings.containsKey(alias))
        ScigolTreeParser.semanticError(_location,"the name '"+alias+"' is already an alias for '"+((UsingEntry)_usings.get(alias)).fullAliasedName()+"'");
    }
    else {
      if (_usings.containsKey(aliased))
        ScigolTreeParser.semanticError(_location,"the name '"+aliased+"' is already used for '"+((UsingEntry)_usings.get(aliased)).fullAliasedName()+"'");
    }

    if (!namespaceScope.contains(aliased))
      ScigolTreeParser.semanticError(_location,"the name '"+aliased+"' cannot be found in namespace '"+namespaceName+"'");
    
    if (alias != null)
      _usings.put(alias, new UsingEntry(alias, aliased, namespaceScope));
    else
      _usings.put(aliased, new UsingEntry(aliased, namespaceScope));
    
  }

  
  
  


  
  

  

  
  
  public String toString()
  {
    String s = "namespace "+fullNamespaceName()+":\n";
    Entry[] entries = getEntries(null, null); // all
    for (Entry entry : entries) {
      String id = entry.name;
      String typeString = entry.type.toString();
      if (entry.type.isAny() || entry.type.isNum()) {
        TypeSpec vtype = TypeSpec.typeOf(TypeSpec.unwrapAnyOrNum(entry.getStaticValue()));
        typeString += "("+vtype+")";
      }
      s += entry.modifiers + " " + id+" : "+typeString+" = "+entry.getStaticValue()+"\n";
    }
    return s;
  }
  
  
  
  
  
  
  
  // helpers for usings
  boolean containsUsing(String name)
  {
    for(Object o : _usings.keySet()) {
      String key = (String)o;
      UsingEntry e = (UsingEntry)_usings.get(key);
      if (!e.isNamespace()) {
        if (e.name() == name) {
          return e.scope.contains(e.aliasedName);
        }
      }
      else {
        if (e.scope.contains(name)) return true;
      }
    }
    return false;
  }
  
  
  Entry[] getUsingEntries(String name)
  {
    ArrayList entries = new ArrayList();

    for(Object o : _usings.keySet()) {
      String key = (String)o;
      UsingEntry e = (UsingEntry)_usings.get(key);
      if (!e.isNamespace()) {
        if ((name==null) || (e.name() == name)) {
          Entry[] nsEntries = e.scope.getEntries(e.aliasedName, null);
          for(Entry entry : nsEntries) {
            if (!e.isAlias())
              entries.add(entry);
            else {
              Entry aliasEntry = entry.createAliasEntry(e.aliasName);
              entries.add(aliasEntry);
            }
          }
        }
      }
      else { // whole namespace
        Entry[] nsEntries = e.scope.getEntries(name, null);
        for(int i=0; i<nsEntries.length; i++) {
          entries.add(nsEntries[i]);
        }
      }
    }

    // convert to array
    return Entry.toArray(entries);
  }
  
  
  
/*  
  public UsingEntry getUsingEntry(String id) 
  {
    Debug.Assert(_scopeType == ScopeType.Namespace);
    ArrayList entryList = new ArrayList(); // all entries in which id appears (either directly or as an aliased)
    foreach (UsingEntry entry in _usings.Values) {
      if (!entry.isNamespace()) {
        if (entry.name() == id) 
          entryList.Add(entry);
      }
      else {
        if (entry.scope.contains(id)) 
          entryList.add(entry);
      }
    }
    
    Debug.Assert(entryList.Count != 0, "can't call getUsingEntry for id's not in the using list");
    if (entryList.Count == 1) return (UsingEntry)entryList[0];
    
    // found multiple candidates
    String s = "the name '"+id+"' is ambiguous in the namespace '"+fullNamespaceName()+"' as it was introduced "
            +"via using expressions from the following namespaces (either directly or via an alias): ";
    foreach(UsingEntry entry in entryList) {
      s += entry.scope.fullNamespaceName()+(entry.isAlias()?"(as "+entry.aliasedName+")":"")+", ";
    }
    
    s += "and hence must either be fully qualified, or the ambiguity remved via aliasing";

    ScigolTreeParser.semanticError(_location,s);
    return new UsingEntry();
  }
  */
  
  
  

  
  
  public enum UsingType { Namespace, Name, Alias };
  
  public class UsingEntry {
    public UsingEntry(NamespaceScope namespaceScope) { // using namespace
      type = UsingType.Namespace;
      scope = namespaceScope;
      aliasName = aliasedName = null;
    }
    
    public UsingEntry(String alias, String aliased, NamespaceScope scope) { // using namespace.name as alias
      type = UsingType.Alias;
      this.scope = scope;
      aliasName = alias;
      aliasedName = aliased;
    }
    
    public UsingEntry(String aliased, NamespaceScope scope) { // using namespace.name
      type = UsingType.Name;
      aliasName = null;
      aliasedName = aliased;
      this.scope = scope;
    }
    
    public String fullAliasedName() { return scope.fullNamespaceName()+"."+aliasedName; }
    
    public String name() { 
      if (type == UsingType.Namespace) return scope.fullNamespaceName();
      else if (type == UsingType.Name) return aliasedName;
      else if (type == UsingType.Alias) return aliasName;
      Debug.Assert(false); return null;
    }
    
    public boolean isNamespace() { return (type == UsingType.Namespace); }
    
    public boolean isAlias() { return (type == UsingType.Alias); }
    
    public UsingType type;
    public NamespaceScope scope;
    public String aliasName;
    public String aliasedName;
  };
  
  
  
  
  
  // library management
  
  
  static boolean loadedLibrariesContainNamespace(String namespaceName)
  {
    // java packages are mapped to scigol namespaces
    
    // some quick short-circuit tests
    if (namespaceName.equals("java") || (namespaceName.equals("javax"))) return true;
    return Package.getPackage(namespaceName) != null;
  }
  
  
  static boolean loadedLibrariesContainName(String fullName)
  {
    try {
      return java.lang.Class.forName(fullName) != null;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
  
  
  public static Type loadedLibrariesGetType(String fullName)
  {
    try {
      return java.lang.Class.forName(fullName);
    }
    catch (ClassNotFoundException e) {
      return null;
    }
    catch (NoClassDefFoundError e) {
      return null;
    }
  }
  
  
  public static void loadLibrary(String fullPathName)
  {
    // Java has no way to get the list of classes in a package!
    // So here we open the jar, identify all class names from their entries
    // and record them by package name
    
    // Also, ask the classloader to load one class so that it loads the jar too (if is hasn't already)
    boolean first = true;
    
    try {
      File file = new File(fullPathName);
      JarFile jarFile = new JarFile(file); 

      Enumeration<JarEntry> fileEntries = jarFile.entries();
      
      while (fileEntries.hasMoreElements()) {
        JarEntry fileEntry = fileEntries.nextElement();

        if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".class")) {
          
          // first remove the .class file to get the class name
          String name = fileEntry.getName();
          
          int lastDotIndex = name.lastIndexOf('.');
          int lastSlashIndex = name.lastIndexOf('/');
          String className = name.substring(lastSlashIndex+1,lastDotIndex);
          String packageName = name.substring(0,lastSlashIndex).replace('/','.');
          
          boolean innerClass = (className.indexOf('$') != -1);
          
          if (!innerClass) {  
          
            LinkedList<String> classes = loadedPackages.get(packageName);
            if (classes == null)
              classes = new LinkedList<String>();
            
            classes.add(className);
            
            loadedPackages.put(packageName, classes);
            
            if (first) {
              first = false;
              try {
                java.lang.Class.forName(packageName+"."+className);
              }
              catch (ClassNotFoundException e) {
                throw new ScigolException("ClassLoader unable to load library '"+fullPathName+"' (not in classpath?).");
              }
              catch (NoClassDefFoundError e) {
                throw new ScigolException("Classloader unable to load library '"+fullPathName+"' (not in classpath?).");
              }
            }
            
          }
          
        }

      }
      
    } catch (IOException e) {
      throw new ScigolException("unable to load library '"+fullPathName+"' - "+e);
    }
    
  }
  
  
  public static AbstractList<String> getNamespaceNames()
  {
    LinkedList<String> namespaceNames = new LinkedList<String>();
    for(String packageName : loadedPackages.keySet())
      namespaceNames.add(packageName);
    return namespaceNames;
  }
  
  
  // manually register a class (useful if we don't want all classes in a .jar visible, just select ones)
  public static void registerLibraryClass(String packageName, String className)
  {
    // first, ensure the class exists by the ClassLoader
    String fullName = packageName+"."+className;
    try {
      java.lang.Class.forName(fullName);
    }
    catch (ClassNotFoundException e) {
      Debug.Warning("ClassLoader unable to class '"+fullName+"' (not in classpath?).");
      throw new ScigolException("ClassLoader unable to class '"+fullName+"' (not in classpath?).");
    }
    catch (NoClassDefFoundError e) {
      Debug.Warning("ClassLoader unable to class '"+fullName+"' (not in classpath?).");
      throw new ScigolException("Classloader unable to class '"+fullName+"' (not in classpath?).");
    }
    
    
    LinkedList<String> classes = loadedPackages.get(packageName);
    if (classes == null)
      classes = new LinkedList<String>();
    
    classes.add(className);
    
    loadedPackages.put(packageName, classes);
    
  }

  
/*
  public static Assembly[] getLoadedAssemblies()
  {
    Assembly[] la = new Assembly[loadedAssemblies.Count];
    for (int i = 0; i < loadedAssemblies.Count; i++)
      la[i] = (Assembly)loadedAssemblies[i];
    return la;
  }
*/

  // static constructor
  static {
    loadedPackages = new HashMap<String,LinkedList<String> >();
    
    // register some select classes
    registerLibraryClass("scigol","accessor");
    registerLibraryClass("scigol","Any");
    registerLibraryClass("scigol","Func");
    registerLibraryClass("scigol","List");
    registerLibraryClass("scigol","Map");
    registerLibraryClass("scigol","Math");
    registerLibraryClass("scigol","Matrix");
    registerLibraryClass("scigol","Num");
    registerLibraryClass("scigol","Range");
    registerLibraryClass("scigol","redirect");
    registerLibraryClass("scigol","TypeSpec");
    registerLibraryClass("scigol","Value");
    registerLibraryClass("scigol","Vector");
    //registerLibraryClass("scigol","Class");
    registerLibraryClass("scigol","TypeManager");
  }

  
  
  public enum NamespaceType { Local, External };
  
  protected NamespaceType _namespaceType;
  protected String _namespaceName;

  protected Hashtable _namespaces; // only for global namespace scope, a map of fully-qualified ns names to namespace Scopes

  protected Hashtable _entries; // identifier name keyed map of ArrayList's of Entrys in this namespace scope

  protected Hashtable _usings; // map of using entries
  
  
  
  // map from package names to list of classes in the package
  protected static HashMap<String,LinkedList<String> > loadedPackages;
  
}

