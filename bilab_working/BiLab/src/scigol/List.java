package scigol;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

// Linked list implementation of List

public class List implements java.util.List {

    // constructors

    // node class
    protected class ItemNode {
        public Object item;
        public ItemNode next, prev;

        public ItemNode(final Object item) {
            this.item = item;
            prev = next = null;
        }

        public ItemNode(final Object item, final ItemNode prev,
                final ItemNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    public static List op_Addition(final List l1, final List l2) {
        return l1.append(l2);
    }

    public static int op_Card(final List l) {
        return l.length;
    }

    public static boolean op_Equality(final List l1, final List l2) {
        return l1.equals(l2);
    }

    public static boolean op_InEquality(final List l1, final List l2) {
        return l1.notEquals(l2);
    }

    // virtual methods
    // subclasses implementing alternative list storage must override these

    private ItemNode headitem, tailitem;

    private int length;

    public List() {
        headitem = tailitem = null;
        length = 0;
    }

    // !!! make this explicit
    public List(final int len) {
        headitem = tailitem = null;
        length = 0;
        for (int i = 0; i < len; i++) {
            add(null);
        }
    }

    public List(final java.util.List list) {
        headitem = tailitem = null;
        length = 0;
        for (int i = 0; i < list.size(); i++) {
            add(list.get(i));
        }
    }

    public List(final Object head) {
        headitem = tailitem = null;
        length = 0;
        add(head);
    }

    public List(final Vector v) {
        headitem = tailitem = null;
        length = 0;
        for (int i = 0; i < v.get_size(); i++) {
            add((v.get_Item(i)).value);
        }
    }

    @Override
    public void add(final int index, final Object element) {
        Debug.Unimplemented();
    }

    @Override
    public boolean add(final Object value) {
        final ItemNode item = new ItemNode(TypeSpec.unwrapAnyOrNum(value));

        if (tailitem == null) {
            tailitem = headitem = item;
        } else {
            tailitem.next = item;
            item.prev = tailitem;
            tailitem = item;
        }
        length++;
        return true;
    }

    @Override
    public boolean addAll(final Collection c) {
        return addAll(0, c);
    }

    @Override
    public boolean addAll(final int index, final Collection c) {
        Debug.Unimplemented();
        return false;
    }

    protected int addToHead(final Object value) {
        final ItemNode item = new ItemNode(TypeSpec.unwrapAnyOrNum(value));

        if (headitem == null) {
            headitem = tailitem = item;
        } else {
            item.next = headitem;
            item.prev = null;
            headitem.prev = item;
            headitem = item;
        }
        length++;
        return length - 1;
    }

    /*
     * public void Insert(int index, Object value) { Debug.Unimplemented(); }
     */

    protected boolean addToTail(final Object value) {
        return add(value);
    }

    /*
     * public void CopyTo(Array array, int index) { if (array == null) throw new
     * ArgumentNullException("cannot copy into null Array array"); if (index <
     * 0) throw new ArgumentOutOfRangeException("index is less than zero"); //
     * if array multi-dim throw new
     * ArgumentException("Array array is not 1 dimensional"); if (index >=
     * array.Length) throw new ArgumentException("index is >= array.Length");
     * 
     * if (index+length > array.Length) throw new
     * ArgumentException("not enough space from index in Array array for "
     * +length+" elements");
     * 
     * for(int i=index; i<index+length; i++)
     * array.SetValue(this.get_Item(i-index), i); }
     */
    /*
     * public IEnumerator GetEnumerator() { return new Enumerator(this); }
     */
    public List append(final List l2) {
        final List newList = new List(this);
        for (final Object e : l2) {
            newList.add(e);
        }
        return newList;
    }

    protected void assign(final List l) {
        clear();
        for (final Object e : l) {
            add(e);
        }
    }

    @Override
    public void clear() {
        headitem = tailitem = null;
        length = 0;
    }

    // operators

    @Override
    public boolean contains(final Object value) {
        Debug.Unimplemented();
        return false;
    }

    @Override
    public boolean containsAll(final Collection c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(final List l2) {
        if (l2 == null) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (!get_Item(i).equals(l2.get_Item(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object l2) {
        l2 = TypeSpec.unwrapAnyOrNum(l2);
        if ((l2 == null) || (!(l2 instanceof List))) {
            return false;
        }
        return equals((List) l2);
    }

    // inefficient indexing, scans from ends
    private ItemNode findItem(int i) {
        ItemNode item = null;

        if (i < length / 2) { // scan from head
            item = headitem;
            while (i > 0) {
                item = item.next;
                i--;
            }
        } else { // scan from tail
            item = tailitem;
            i = length - 1 - i;
            while (i > 0) {
                item = item.prev;
                i--;
            }
        }
        return item;
    }

    @Override
    public Object get(final int index) {
        return get_Item(index);
    }

    // more interface List members

    @accessor
    public Any get_head() {
        if (length == 0) {
            throw new IndexOutOfBoundsException("list () has no head");
        }
        return new Any(headitem.item);
    }

    @accessor
    public Any get_Item(int i) {
        if (i < 0) {
            i += length; // -ve => from end
        }

        if ((i < 0) || (i >= length)) {
            throw new IndexOutOfBoundsException("list index " + i
                    + " out of range 0.." + (length - 1));
        }

        return new Any(getItem(i));
    }

    @accessor
    public List get_Item(Range r) {
        r = r.normalize(length);

        if ((r.start < 0) || (r.start >= length)) {
            throw new IndexOutOfBoundsException("list range start " + r.start
                    + " out of range 0.." + (length - 1));
        }
        if ((r.end < 0) || (r.end >= length)) {
            throw new IndexOutOfBoundsException("list range end " + r.end
                    + " out of range 0.." + (length - 1));
        }

        if (r.start > r.end) {
            return new List();
        }
        if (r.start == r.end) {
            return new List(new Any(getItem(r.start)));
        }

        final List list = new List();
        for (int i = r.start; i <= r.end; i++) {
            list.add(getItem(i));
        }
        return list;
    }

    @accessor
    public int get_size() {
        return length;
    }

    @accessor
    List get_tail() {
        if (length <= 1) {
            return new List(); // empty tail
        } else {
            return get_Item(new Range(1, length - 1));
        }
    }

    private Object getItem(final int i) {
        return findItem(i).item;
    }

    @Override
    public int indexOf(final Object value) {
        Debug.Unimplemented();
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return get_size() == 0;
    }

    @Override
    public Iterator iterator() {
        Debug.Unimplemented();
        return null;
    }

    @Override
    public int lastIndexOf(final Object o) {
        Debug.Unimplemented();
        return -1;
    }

    @Override
    public ListIterator listIterator() {
        Debug.Unimplemented();
        return null;
    }

    @Override
    public ListIterator listIterator(final int index) {
        Debug.Unimplemented();
        return null;
    }

    public boolean notEquals(final List l2) {
        if (l2 == null) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (get_Item(i).equals(l2.get_Item(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object remove(final int index) {
        if ((index < 0) || (index >= length)) {
            throw new IndexOutOfBoundsException("list has no item with index "
                    + index);
        }

        if (index == 0) {
            return removeHead();
        } else if (index == length - 1) {
            return removeTailItem();
        }

        final ItemNode item = findItem(index);
        item.prev.next = item.next;
        item.next.prev = item.prev;
        length--;
        return new Any(item.item);
    }

    @Override
    public boolean remove(final Object value) {
        Debug.Unimplemented();
        return false;
    }

    @Override
    public boolean removeAll(final Collection c) {
        if (isEmpty()) {
            return false; // /!!! this needs to be smarter about returning
                          // true/false
        }

        for (final Object o : c) {
            remove(o);
        }
        return true;
    }

    protected Any removeHead() {
        final ItemNode h = headitem;

        if (h == null) {
            return new Any(null);
        }

        length--;

        if (h == tailitem) {
            tailitem = headitem = null;
            return new Any(h);
        }

        headitem = h.next;
        headitem.prev = null;
        return new Any(h);

    }

    protected Any removeTailItem() {
        final ItemNode t = tailitem;

        if (t == null) {
            return new Any(null);
        }

        length--;

        if (t == headitem) {
            tailitem = headitem = null;
            return new Any(t);
        }

        tailitem = tailitem.prev;

        return new Any(t);
    }

    /*
     * public <T> T[] toArray(T[] a) { Debug.Unimplemented(); return null; }
     */

    @Override
    public boolean retainAll(final Collection c) {
        Debug.Unimplemented();
        return false;
    }

    // enumerator
    /*
     * public class Enumerator : IEnumerator { public Enumerator(List l) {
     * this.l = l; index = -1; }
     * 
     * 
     * public void Reset( ) { index = -1; }
     * 
     * 
     * public object Current { get { return l[index]; } }
     * 
     * public boolean MoveNext() { ++index; return (index < l.Count); }
     * 
     * protected List l; protected int index; //!!! change to use cursor or hold
     * item for efficiency!!! }
     */

    // implementation

    @Override
    public Object set(final int index, final Object element) {
        set_Item(index, new Any(element));
        return element;
    }

    @accessor
    public void set_head(final Any value) {
        if (length == 0) {
            throw new IndexOutOfBoundsException("list () has no head");
        }
        headitem.item = (value).value;
    }

    @accessor
    public void set_Item(int i, final Any value) {
        if (i < 0) {
            i += length; // -ve => from end
        }

        if ((i < 0) || (i >= length)) {
            throw new IndexOutOfBoundsException("list index " + i
                    + " out of range 0.." + (length - 1));
        }

        setItem(i, value.value);
    }

    @accessor
    public void set_Item(Range r, final List value) {
        r = r.normalize(length);

        if ((r.start < 0) || (r.start >= length)) {
            throw new IndexOutOfBoundsException("list range start " + r.start
                    + " out of range 0.." + (length - 1));
        }
        if ((r.end < 0) || (r.end >= length)) {
            throw new IndexOutOfBoundsException("list range end " + r.end
                    + " out of range 0.." + (length - 1));
        }

        if (r.start > r.end) {
            if (value.length != 0) {
                throw new IllegalArgumentException("can't assign list with "
                        + value.length + " elements to a range of 0 elements");
            }
            return;
        }
        if (r.start == r.end) {
            setItem(r.start, value.get_Item(0));
            if (value.length != 0) {
                throw new IllegalArgumentException("can't assign list with "
                        + value.length + " elements to a range of 1 element");
            }
            return;
        }

        if (value.length != (r.end - r.start + 1)) {
            throw new IllegalArgumentException("can't assign list with "
                    + value.length + " elements to a range of "
                    + (r.end - r.start + 1) + " elements");
        }

        for (int i = r.start; i <= r.end; i++) {
            setItem(i, value.get_Item(i - r.start));
        }
    }

    @accessor
    void set_tail(final List value) {
        // tail replacement - just make a new list from head + value
        assign((length > 0) ? op_Addition(new List(get_head()), value) : value);
    }

    private void setItem(final int i, final Object value) {
        findItem(i).item = TypeSpec.unwrapAnyOrNum(value);
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public List subList(final int fromIndex, final int toIndex) {
        return get_Item(new Range(fromIndex, toIndex));
    }

    @Override
    public Object[] toArray() {
        final Object[] a = new Object[get_size()];
        for (int i = 0; i < get_size(); i++) {
            a[i] = (get_Item(i)).value;
        }
        return a;
    }

    @Override
    public Object[] toArray(final Object a[]) {
        Debug.Unimplemented();
        return null;
    }

    @Override
    public String toString() {
        // !!! use iterator/enumerator for efficiency!!!
        String s = "(";
        for (int i = 0; i < length; i++) {
            s += getItem(i).toString();
            if (i != length - 1) {
                s += ", ";
            }
        }
        s += ")";
        return s;
    };
}
