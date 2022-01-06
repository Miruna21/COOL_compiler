package cool.structures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassSymbol extends TypeSymbol {
    protected Map<String, IdSymbol> attributes = new LinkedHashMap<>();
    protected Map<String, MethodSymbol> methods = new LinkedHashMap<>();

    protected Scope parent;
    protected String parentName;
    protected List<ClassSymbol> children;
    protected Integer total_attributes = 0;

    public static final ClassSymbol OBJECT  = new ClassSymbol("","Object");
    public static final ClassSymbol INT   = new ClassSymbol("Object","Int");
    public static final ClassSymbol BOOL = new ClassSymbol("Object", "Bool");
    public static final ClassSymbol STRING  = new ClassSymbol("Object", "String");
    public static final ClassSymbol IO  = new ClassSymbol("Object", "IO");

    public ClassSymbol(String parentName, String name) {
        super(name);
        this.parentName = parentName;
        this.children = new ArrayList<>();
    }

    public String getParentName() {
        return parentName;
    }

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (sym instanceof IdSymbol) {
            if (attributes.containsKey(sym.getName()))
                return false;

            attributes.put(sym.getName(), (IdSymbol) sym);

        } else if (sym instanceof MethodSymbol) {
            if (methods.containsKey(sym.getName()))
                return false;

            methods.put(sym.getName(), (MethodSymbol) sym);
        } else
            return false;

        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = attributes.get(name);

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
    public Symbol lookupMethod(String name) {
        var sym = methods.get(name);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookupMethod(name);

        return null;
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public ClassSymbol getCrtClass() {
        return null;
    }

    public void addChild(ClassSymbol child) {
        children.add(child);
    }

    public List<ClassSymbol> getChildren() {
        return children;
    }

    public Integer getTotal_attributes() {
        return total_attributes;
    }

    public void setTotal_attributes(Integer total_attributes) {
        this.total_attributes = total_attributes;
    }

    public Map<String, IdSymbol> getAttributes() {
        return attributes;
    }

    public Map<String, MethodSymbol> getMethods() {
        return methods;
    }

    @Override
    public String toString() {
        return name;
    }
}

