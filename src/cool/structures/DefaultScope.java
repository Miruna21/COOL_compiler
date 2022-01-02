package cool.structures;

import java.util.*;

public class DefaultScope implements Scope {
    
    private final Map<String, ClassSymbol> classSymbols = new LinkedHashMap<>();
    private final Map<ClassSymbol, SelfTypeSymbol> selfTypeSymbols = new HashMap<>();
    
    private final Scope parent;
    
    public DefaultScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (classSymbols.containsKey(sym.getName()))
            return false;

        classSymbols.put(sym.getName(), (ClassSymbol) sym);
        selfTypeSymbols.put((ClassSymbol) sym, new SelfTypeSymbol("SELF_TYPE", (ClassSymbol) sym));
        
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = classSymbols.get(name);
        
        if (sym != null)
            return sym;
        
        if (parent != null)
            return parent.lookup(name);
        
        return null;
    }

    @Override
    public TypeSymbol lookupTypes(String name, ClassSymbol crtClass) {
        if (name.compareTo("SELF_TYPE") == 0) {
            return selfTypeSymbols.get(crtClass);
        }

        return (ClassSymbol) lookup(name);
    }

    @Override
    public Symbol lookupMethod(String str) {
        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
}
