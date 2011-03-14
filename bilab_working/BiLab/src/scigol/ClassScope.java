package scigol;

import java.util.EnumSet;

// / a scope for class members
public class ClassScope extends Scope {
    protected TypeSpec _classType; // can be internal (ClassInfo based) or
                                   // external (CLI) class

    public ClassScope(final TypeSpec classType) {
        Debug.Assert(
                classType.isClassOrBuiltinClass() || classType.isInterface(),
                "can't make a class scope from a non-class:" + classType);

        _classType = classType;
        _outer = classType.getClassInfo().getOuterScope();
    }

    @Override
    public Entry addEntry(final Entry e) {
        // !!! just hack an impl for now (cut&paste fro LocalScope) !!!
        final EnumSet<Entry.Flags> flags = e.flags;
        if (e.type.isFunc()) {
            flags.add(Entry.Flags.Method);
        } else {
            flags.add(Entry.Flags.Field);
        }

        _classType.getClassInfo().addMember(e.name, flags, e.type, e.modifiers,
                e.getStaticValue(), e.initializer);

        // !!!

        return e;
    }

    @Override
    public boolean contains(final String name) {
        // !! for now
        return super.contains(name);
    }

    public TypeSpec getClassType() {
        return _classType;
    }

    @Override
    public Entry[] getEntries(final String name, final Object instance) {
        final Entry[] entries = _classType.getClassInfo().lookup(name, null,
                null);
        /*
         * NO NEED // if we have an instance, fill in the instance member entry
         * values if (instance != null) {
         * 
         * Debug.Assert(instance is Class); Class classInstance = instance as
         * Class;
         * 
         * // need to make a shallow copy of the Entries so that we don't
         * overwrite the instance member // initializers with their value for
         * this instance Entry[] evaluatedEntries = new Entry[entries.Length];
         * 
         * for(int i=0; i<entries.Length; i++) { evaluatedEntries[i] =
         * entries[i].Clone() as Entry; evaluatedEntries[i].value =
         * classInstance.getMemberValue(evaluatedEntries[i]); }
         * 
         * entries = evaluatedEntries; }
         */
        return entries;
    }

    @Override
    public boolean isClassScope() {
        return true;
    }

    @Override
    public Entry[] lookup(final String name, final FuncInfo callSig,
            final Object[] args, final Object instance) {
        // !!! just hack an impl for now (cut&paste from LocalScope) !!!

        // if this block contains any definition of name at all, it hides those
        // in
        // any outer scope, so just perform overload resolution (if necessary)
        // to resolve it
        // (otherwise, defer to the outer scope)
        if (contains(name)) {
            Entry[] matches = getEntries(name, instance);
            if (matches.length == 1) {
                return matches;
            }
            // overloaded func, try to resolve it (will return 0 elements if no
            // match, or >1 elements if ambiguous)
            matches = TypeManager.resolveOverload(matches, callSig, args);

            // if found a unique or ambiguous match, return, otherwise defer to
            // outer scope
            if (matches.length > 0) {
                return matches;
            }
        }

        if (_outer != null) {
            return _outer.lookup(name, callSig, args, instance);
        } else {
            return new Entry[0];
            // !!!
        }
    }
}
