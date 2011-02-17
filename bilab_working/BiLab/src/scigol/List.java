package scigol;


import java.util.Collection;
import java.util.ListIterator;
import java.util.Iterator;


// Linked list implementation of List

public class List implements java.util.List
{

  // constructors

  public List()
  {
    headitem = tailitem = null;
    length = 0;
  }


  //!!! make this explicit
  public List(int len)
  {
    headitem = tailitem = null;
    length=0;
    for(int i=0; i<len; i++) add(null);
  }


  public List(Object head)
  {
    headitem = tailitem = null;
    length=0;
    add(head);
  }

  public List(java.util.List list)
  {
    headitem = tailitem = null;
    length=0;
    for(int i=0; i<list.size(); i++)
      add(list.get(i));
  }


  public List(Vector v)
  {
    headitem = tailitem = null;
    length=0;
    for(int i=0; i<v.get_size(); i++)
      add( ((Any)v.get_Item(i)).value );
  }



  // virtual methods
  //  subclasses implementing alternative list storage must override these


  @accessor
  public Any get_head()
  {
    if (length==0)
      throw new IndexOutOfBoundsException("list () has no head");
    return new Any(headitem.item);
  }

  @accessor
  public void set_head(Any value)
  {
    if (length==0)
      throw new IndexOutOfBoundsException("list () has no head");
    headitem.item = ((Any)value).value;
  }



  @accessor
  List get_tail()
  {
    if (length <= 1)
      return new List(); // empty tail
    else
      return get_Item(new Range(1,length-1));
  }

  
  @accessor void set_tail(List value)
  {
    // tail replacement - just make a new list from head + value
    assign((length>0)?op_Addition(new List(get_head()),value):value);
  }
  
  

  @accessor
  public int get_size()
  {
    return length;
  }

  public int size() { return length; }
  
  

  public boolean add(Object value)
  {
    ItemNode item = new ItemNode(TypeSpec.unwrapAnyOrNum(value));

    if (tailitem == null) {
      tailitem = headitem = item;
    }
    else {
      tailitem.next = item;
      item.prev = tailitem;
      tailitem = item;
    }
    length++;
    return true;
  }
  
  
  public void add(int index, Object element)
  {
    Debug.Unimplemented();
  }


  public void clear()
  {
    headitem = tailitem = null;
    length = 0;
  }

  public boolean contains(Object value)
  {
    Debug.Unimplemented(); return false;
  }
  
  
  public boolean containsAll(Collection c)
  {
    for(Object o : c)
      if (!contains(o)) return false;
    return true;
  }
  

  public int indexOf(Object value)
  {
    Debug.Unimplemented(); return 0;
  }

  /*
  public void Insert(int index, Object value)
  {
    Debug.Unimplemented();
  }
*/

  public boolean remove(Object value)
  {
    Debug.Unimplemented();
    return false;
  }

  public Object remove(int index)
  {
    if ((index < 0) || (index >= length))
      throw new IndexOutOfBoundsException("list has no item with index "+index);

    if (index==0)
      return removeHead();
    else if (index == length-1)
      return removeTailItem();

    ItemNode item = findItem(index);
    item.prev.next = item.next;
    item.next.prev = item.prev;
    length--;
    return new Any(item.item);
  }



/*
  public void CopyTo(Array array, int index)
  {
    if (array == null) throw new ArgumentNullException("cannot copy into null Array array");
    if (index < 0) throw new ArgumentOutOfRangeException("index is less than zero");
    // if array multi-dim throw new ArgumentException("Array array is not 1 dimensional");
    if (index >= array.Length) throw new ArgumentException("index is >= array.Length");

    if (index+length > array.Length) throw new ArgumentException("not enough space from index in Array array for "+length+" elements");

    for(int i=index; i<index+length; i++)
      array.SetValue(this.get_Item(i-index), i);
  }
*/
/*
  public IEnumerator GetEnumerator()
  {
    return new Enumerator(this);
  }
*/
  public List append(List l2)
  {
    List newList = new List(this);
    for(Object e : l2) newList.add(e);
    return newList;
  }

  public boolean notEquals(List l2)
  {
    if (l2 == null) return true;
    for(int i=0; i<length;i++)
      if (get_Item(i).equals(l2.get_Item(i))) return false;
    return true;
  }



  // operators

  public boolean equals(Object l2)
  {
    l2 = TypeSpec.unwrapAnyOrNum(l2);
    if ((l2 == null) || (!(l2 instanceof List))) return false;
    return equals((List)l2);
  }


  public boolean equals(List l2)
  {
    if (l2 == null) return false;
    for(int i=0; i<length;i++)
      if (!get_Item(i).equals(l2.get_Item(i))) return false;
    return true;
  }


  
  @accessor
  public List get_Item(Range r)
  {
    r = r.normalize(length);

    if ((r.start<0) || (r.start>=length))
      throw new IndexOutOfBoundsException("list range start "+r.start+" out of range 0.."+(length-1));
    if ((r.end<0) || (r.end>=length))
      throw new IndexOutOfBoundsException("list range end "+r.end+" out of range 0.."+(length-1));

    if (r.start>r.end) return new List();
    if (r.start==r.end) return new List(new Any(getItem(r.start)));

    List list = new List();
    for(int i=r.start; i<=r.end; i++)
      list.add(getItem(i));
    return list;
  }
  
  
  @accessor
  public void set_Item(Range r, List value) 
  {
    r = r.normalize(length);

    if ((r.start<0) || (r.start>=length))
      throw new IndexOutOfBoundsException("list range start "+r.start+" out of range 0.."+(length-1));
    if ((r.end<0) || (r.end>=length))
      throw new IndexOutOfBoundsException("list range end "+r.end+" out of range 0.."+(length-1));

    if (r.start>r.end) {
      if (value.length != 0)
        throw new IllegalArgumentException("can't assign list with "+value.length+" elements to a range of 0 elements");
      return;
    }
    if (r.start==r.end) {
      setItem(r.start, value.get_Item(0));
      if (value.length != 0)
        throw new IllegalArgumentException("can't assign list with "+value.length+" elements to a range of 1 element");
      return;
    }

    if (value.length != (r.end-r.start+1))
      throw new IllegalArgumentException("can't assign list with "+value.length+" elements to a range of "+(r.end-r.start+1)+" elements");

    for(int i=r.start; i<=r.end; i++)
      setItem(i, value.get_Item(i-r.start));
  }
  
  
  


  
  @accessor
  public Any get_Item(int i)
  {
    if (i<0) i += length; // -ve => from end

    if ((i<0) || (i>=length))
      throw new IndexOutOfBoundsException("list index "+i+" out of range 0.."+(length-1));

    return new Any(getItem(i));
  }
  
  
  @accessor
  public void set_Item(int i, Any value) 
  {
    if (i<0) i += length; // -ve => from end

    if ((i<0) || (i>=length))
      throw new IndexOutOfBoundsException("list index "+i+" out of range 0.."+(length-1));

    setItem(i, value.value);
  }
    
  
  
  // more interface List members
  
  public Object get(int index) { return get_Item(index); }
  
  public Object set(int index, Object element) 
  {
    set_Item(index,new Any(element));
    return element;
  }
 
  public boolean isEmpty() { return get_size() == 0; }
  
  
  public boolean addAll(int index, Collection c)
  {
    Debug.Unimplemented();
    return false;
  }
  
  public boolean addAll(Collection c)
  {
    return addAll(0,c);
  }
  
  
  
  public boolean removeAll(Collection c)
  {
    if (isEmpty()) return false; ///!!! this needs to be smarter about returning true/false
    
    for(Object o : c)
      remove(o);
    return true;
  }
  
  
  public boolean retainAll(Collection c)
  {
    Debug.Unimplemented();
    return false;
  }
  
  
  public List subList(int fromIndex, int toIndex)
  {
    return get_Item(new Range(fromIndex, toIndex));
  }
  
  
  public int lastIndexOf(Object o)
  {
    Debug.Unimplemented();
    return -1;
  }
  
  
  
  public ListIterator listIterator()
  {
    Debug.Unimplemented();
    return null;
  }
  
  public ListIterator listIterator(int index)
  {
    Debug.Unimplemented();
    return null;
  }
  
  
  public Iterator iterator()
  {
    Debug.Unimplemented();
    return null;
  }
  
  
  
  
  
  

  public static List op_Addition(List l1, List l2) { return l1.append(l2); }
  public static boolean op_Equality(List l1, List l2) { return l1.equals(l2); }
  public static boolean op_InEquality(List l1, List l2) { return l1.notEquals(l2); }
  public static int op_Card(List l) { return l.length; }


  public String toString()
  {
    // !!! use iterator/enumerator for efficiency!!!
    String s = "(";
    for(int i=0; i<length;i++) {
      s += getItem(i).toString();
      if (i!=length-1)
        s += ", ";
    }
    s += ")";
    return s;
  }


  public Object[] toArray()
  {
    Object[] a = new Object[get_size()];
    for(int i=0; i<get_size();i++)
      a[i] = ((Any)get_Item(i)).value;
    return a;
  }
  
  /*
  public <T> T[] toArray(T[] a)
  {
    Debug.Unimplemented();
    return null;
  }
  */
  
  public Object[] toArray(Object a[])
  {
    Debug.Unimplemented();
    return null;
  }

  

  // enumerator
/*
  public class Enumerator : IEnumerator
  {
    public Enumerator(List l)
    {
      this.l = l;
      index = -1;
    }


    public void Reset( )
    {
      index = -1;
    }


    public object Current
    {
      get {
        return l[index];
      }
    }

    public boolean MoveNext()
    {
      ++index;
      return (index < l.Count);
    }

    protected List l;
    protected int index; //!!! change to use cursor or hold item for efficiency!!!
  }
*/




  // implementation

  protected void assign(List l)
  {
    clear();
    for(Object e : l) add(e);
  }



  protected int addToHead(Object value) 
  {
    ItemNode item = new ItemNode(TypeSpec.unwrapAnyOrNum(value));

    if (headitem == null) {
      headitem = tailitem = item;
    }
    else {
      item.next = headitem;
      item.prev = null;
      headitem.prev = item;
      headitem = item;
    }
    length++;
    return length-1;
  }


  protected boolean addToTail(Object value) { return add(value); }


  protected Any removeHead()
  {
    ItemNode h = headitem;

    if(h==null) return new Any(null);

    length--;

    if (h == tailitem) {
      tailitem = headitem = null;
      return new Any(h);
    }

    headitem = h.next;
    headitem.prev = null;
    return new Any(h);

  }


  protected Any removeTailItem()
  {
    ItemNode t = tailitem;

    if (t==null) return new Any(null);

    length--;

    if (t == headitem) {
      tailitem = headitem = null;
      return new Any(t);
    }

    tailitem = tailitem.prev;

    return new Any(t);
  }


  // inefficient indexing, scans from ends
  private ItemNode findItem(int i)
  {
    ItemNode item = null;

    if (i < length/2) { // scan from head
      item = headitem;
      while (i>0) {
        item = item.next;
        i--;
      }
    }
    else { // scan from tail
      item = tailitem;
      i = length-1-i;
      while (i>0) {
        item = item.prev;
        i--;
      }
    }
    return item;
  }


  private Object getItem(int i) 
  {
    return findItem(i).item;
  }

  private void setItem(int i, Object value) 
  {
    findItem(i).item = TypeSpec.unwrapAnyOrNum(value);
  }

  


  private ItemNode headitem, tailitem;
  private int length;
  
  //node class
  protected class ItemNode
  {
    public ItemNode(Object item) { this.item = item; prev=next=null; }
    public ItemNode(Object item, ItemNode prev, ItemNode next)
    { this.item = item; this.prev = prev; this.next = next; }

    public Object item;
    public ItemNode next, prev;
  };
}


