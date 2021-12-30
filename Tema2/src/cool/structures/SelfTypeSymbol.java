package cool.structures;

public class SelfTypeSymbol extends TypeSymbol {
    private final ClassSymbol crtClass;

    public SelfTypeSymbol(String name, ClassSymbol crtClass) {
        super(name);
        this.crtClass = crtClass;
    }

    @Override
    public ClassSymbol getCrtClass() {
        return crtClass;
    }

    @Override
    public boolean add(Symbol sym) {
        return false;
    }

    @Override
    public Symbol lookup(String str) {
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
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
