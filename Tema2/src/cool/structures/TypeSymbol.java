package cool.structures;

public abstract class TypeSymbol extends Symbol implements Scope {
    public TypeSymbol(String name) {
        super(name);
    }

    @Override
    public abstract boolean add(Symbol sym);

    @Override
    public abstract Symbol lookup(String str);

    @Override
    public abstract Symbol lookupMethod(String str);

    @Override
    public abstract Scope getParent();

    public abstract ClassSymbol getCrtClass();
}
