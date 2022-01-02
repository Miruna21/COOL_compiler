package cool.structures;

public class LetScope implements Scope {
    private IdSymbol local;

    private final Scope parent;

    public LetScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        local = (IdSymbol) sym;

        return true;
    }

    @Override
    public Symbol lookup(String name) {
        if (local.getName().compareTo(name) == 0) {
            return local;
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
