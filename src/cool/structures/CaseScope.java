package cool.structures;

public class CaseScope implements Scope {
    private IdSymbol branch;

    private final Scope parent;

    public CaseScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        branch = (IdSymbol) sym;
        return true;
    }

    @Override
    public Symbol lookup(String name) {
        if (branch.getName().compareTo(name) == 0) {
            return branch;
        }
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

    @Override
    public Scope getParent() {
        return parent;
    }
}
