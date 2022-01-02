package cool.structures;

public interface Scope {
    boolean add(Symbol sym);
    
    Symbol lookup(String str);

    TypeSymbol lookupTypes(String name, ClassSymbol crtClass);

    Symbol lookupMethod(String str);
    
    Scope getParent();
}
