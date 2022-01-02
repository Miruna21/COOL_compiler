package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class MethodSymbol extends Symbol implements Scope {
    protected Map<String, IdSymbol> formals = new LinkedHashMap<>();
    protected TypeSymbol returnType;
    protected Scope parent;

    public MethodSymbol(Scope parent, String name) {
        super(name);
        this.parent = parent;
    }

    public TypeSymbol getType() {
        return returnType;
    }

    public void setType(TypeSymbol type) {
        this.returnType = type;
    }

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (formals.containsKey(sym.getName()))
            return false;

        formals.put(sym.getName(), (IdSymbol) sym);

        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = formals.get(name);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookup(name);

        return null;
    }

    @Override
    public TypeSymbol lookupTypes(String name, ClassSymbol crtClass) {
        return null;
    }

    @Override
    public Symbol lookupMethod(String str) {
        return null;
    }

    public Map<String, IdSymbol> getFormals() {
        return formals;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
}
