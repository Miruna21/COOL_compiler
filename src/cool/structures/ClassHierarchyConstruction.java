package cool.structures;

import cool.ast.nodes.*;

public class ClassHierarchyConstruction {
    Scope currentScope = SymbolTable.globals;

    public Void visit(Program program) {
        for (var class_ : program.getClasses())
            visit(class_);

        return null;
    }

    public Void visit(Class_ class_) {
        var type = class_.getType();
        boolean illegalParent = false;

        if (type.getToken().getText().compareTo("SELF_TYPE") == 0) {
            SymbolTable.error(class_.getContext(), type.getToken(), "Class has illegal name SELF_TYPE");
            type.setSymbol(null);
            return null;
        }

        ClassSymbol symbol;

        if (class_.getParent() != null) {
            String parentName = class_.getParent().getToken().getText();
            if (parentName.compareTo("Int") == 0 || parentName.compareTo("Bool")  == 0|| parentName.compareTo("String") == 0 ||
                    parentName.compareTo("SELF_TYPE") == 0) {
                SymbolTable.error(class_.getContext(), class_.getParent().getToken(), "Class " + type.getToken().getText() +
                        " has illegal parent " + parentName);
                type.setSymbol(null);
                illegalParent = true;
            }
            symbol = new ClassSymbol(parentName, type.getToken().getText());
        } else {
            symbol = new ClassSymbol("Object", type.getToken().getText());
            symbol.setParent(ClassSymbol.OBJECT);

            ClassSymbol.OBJECT.addChild(symbol);
        }

        if (!currentScope.add(symbol)) {
            SymbolTable.error(class_.getContext(), class_.getType().getToken(), "Class " + type.getToken().getText() + " is redefined");
            type.setSymbol(null);
            return null;
        }

        // adnotez type-ul nodului curent din arbore cu un nou simbol
        if (!illegalParent)
            type.setSymbol(symbol);

        return null;
    }

    public boolean checkHierarchy(Program program) {
        boolean correct = true;
        for (var class_ : program.getClasses()) {
            if (!check(class_))
                correct = false;
        }

        return correct;
    }

    private boolean check(Class_ class_) {
        var id = class_.getType();
        var parent = class_.getParent();
        var symbol = (ClassSymbol) id.getSymbol();

        if (parent != null && symbol != null) {
            // caut tipul parintelui in scope-ul global
            var parentSymbol = (ClassSymbol) currentScope.lookup(parent.getToken().getText());

            if (parentSymbol == null) {
                SymbolTable.error(class_.getContext(), parent.getToken(), "Class " + id.getToken().getText() +
                        " has undefined parent " + parent.getToken().getText());
                return false;
            }
            symbol.setParent(parentSymbol);
            parentSymbol.addChild(symbol);

            var crtClassName = parentSymbol.getParentName();
            while (true) {
                ClassSymbol crtClassSymbol = (ClassSymbol) currentScope.lookup(crtClassName);

                if (crtClassSymbol == null)  // stramosul nu exista in ierarhie
                    break;

                if (crtClassName.compareTo("Object") == 0)
                    break;
                else if (crtClassName.compareTo(id.getToken().getText()) == 0) {
                    SymbolTable.error(class_.getContext(), id.getToken(), "Inheritance cycle for class " +
                            id.getToken().getText());
                    return false;
                }

                crtClassName = crtClassSymbol.getParentName();
            }
        }

        return true;
    }
}
