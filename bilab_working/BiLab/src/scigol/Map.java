package scigol;

import java.util.HashMap;

// Map (aka hashtable, associative array, dictionary)
public class Map extends HashMap // !!! make this implement an interface and
                                 // wrap the HashMap instead
{
    public static int op_Card(final Map m) {
        return m.size();
    }

    public Map() {
        super(2);
    }

    public void add(Object key, Object value) {
        key = TypeSpec.unwrapAnyOrNum(key);
        value = TypeSpec.unwrapAnyOrNum(value);
        super.put(key, value);
    }

    public boolean contains(final Object key) {
        return (super.containsKey(TypeSpec.unwrapAnyOrNum(key)));
    }

    @accessor
    public Any get_Item(Object key) {
        key = TypeSpec.unwrapAnyOrNum(key);
        return new Any(get(key));
    }

    @Override
    public Object remove(final Object key) {
        return super.remove(TypeSpec.unwrapAnyOrNum(key));
    }

    @accessor
    public void set_Item(Object key, final Any value) {
        key = TypeSpec.unwrapAnyOrNum(key);
        put(key, TypeSpec.unwrapAnyOrNum(value));
    }

    @Override
    public String toString() {
        String s = null;
        final int size = keySet().size();
        if (size > 1) {
            s = "[\n";
            for (final Object key : keySet()) {
                final Object value = get(key);
                s += " " + key.toString() + " -> "
                        + ((value != null) ? value.toString() : "null") + "\n";
            }
            s += "]";
        } else {
            if (size == 0) {
                s = "[]";
            } else {
                final Object key = keySet().iterator().next();
                final Object value = get(key);
                s = "[ " + key.toString() + " -> "
                        + ((value != null) ? value.toString() : "null") + " ]";
            }
        }
        return s;
    }

}
