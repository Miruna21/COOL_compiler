package cool.structures;

import cool.ast.*;
import cool.ast.nodes.*;

public class DefinitionPassVisitor implements ASTVisitor<Void> {
    Scope currentScope = null;
    ClassSymbol currentClass = null;

    @Override
    public Void visit(Program program) {
        currentScope = SymbolTable.globals;

        if (currentScope.lookupTypes("Main", null) == null)
            return null;

        for (var class_ : program.getClasses())
            class_.accept(this);

        return null;
    }

    @Override
    public Void visit(Class_ class_) {
        var id = class_.getType();
        var symbol = (ClassSymbol) id.getSymbol();

        currentScope = symbol;
        currentClass = symbol;

        var selfSymbol = new IdSymbol("self");
        selfSymbol.setType(SymbolTable.globals.lookupTypes("SELF_TYPE", currentClass));
        currentScope.add(selfSymbol);

        // continui parcurgerea AST-ului
        for (var feature : class_.getFeatures())
            feature.accept(this);

        currentScope = symbol.getParent();

        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        var id = attribute.getName();
        var type = attribute.getType();
        var attributeClass = (ClassSymbol) currentScope;

        if (id.getToken().getText().compareTo("self") == 0) {
            SymbolTable.error(attribute.getContext(), id.getToken(), "Class " + attributeClass.getName() +
                    " has attribute with illegal name self");
            return null;
        }

        var symbol = new IdSymbol(id.getToken().getText());

        if (!attributeClass.add(symbol)) {
            SymbolTable.error(attribute.getContext(), id.getToken(), "Class " + attributeClass.getName() +
                    " redefines attribute " + id.getToken().getText());
            return null;
        }

        // caut tipul atributului in scope-ul global
        var typeSymbol = SymbolTable.globals.lookupTypes(type.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(attribute.getContext(), type.getToken(), "Class " + attributeClass.getName() +
                    " has attribute " + id.getToken().getText() + " with undefined type " + type.getToken().getText());
            return null;
        }

        symbol.setType(typeSymbol);
        // adnotez id-ul nodului curent din arbore cu un nou simbol
        id.setSymbol(symbol);
        id.setScope(currentScope);

        // continui parcurgerea arborelui
        if (attribute.getInit() != null)
            attribute.getInit().accept(this);  // nu ajung sa retin scope-ul expresiei de initializare in unele cazuri

        return null;
    }

    @Override
    public Void visit(Method method) {
        var id = method.getName();
        var type = method.getType();
        var methodClass = (ClassSymbol) currentScope;
        var symbol = new MethodSymbol(currentScope, id.getToken().getText());

        if (!methodClass.add(symbol)) {
            SymbolTable.error(method.getContext(), id.getToken(), "Class " + methodClass.getName() +
                    " redefines method " + id.getToken().getText());
            return null;
        }

        // caut tipul de retur al metodei in scope-ul global
        var typeSymbol = SymbolTable.globals.lookupTypes(type.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(method.getContext(), type.getToken(), "Class " + methodClass.getName() +
                    " has method " + id.getToken().getText() + " with undefined return type " + type.getToken().getText());
            return null;
        }

        symbol.setType(typeSymbol);
        // adnotez id-ul nodului curent din arbore cu un nou simbol
        id.setSymbol(symbol);
        id.setScope(symbol);

        currentScope = symbol;
        // continui parcurgerea arborelui
        for (var formals : method.getFormals())
            formals.accept(this);

        method.getBody().accept(this);

        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        var id = formal.getArgName();
        var type = formal.getType();
        var formalMethod = (MethodSymbol) currentScope;
        var methodClass = (ClassSymbol) currentScope.getParent();

        if (id.getToken().getText().compareTo("self") == 0) {
            SymbolTable.error(formal.getContext(), id.getToken(), "Method " + formalMethod.getName() +
                    " of class " + methodClass.getName() + " has formal parameter with illegal name self");
            return null;
        }

        if (type.getToken().getText().compareTo("SELF_TYPE") == 0) {
            SymbolTable.error(formal.getContext(), type.getToken(), "Method " + formalMethod.getName() +
                    " of class " + methodClass.getName() + " has formal parameter " + id.getToken().getText() +
                    " with illegal type " + type.getToken().getText());
            return null;
        }

        var symbol = new IdSymbol(id.getToken().getText());

        if (!formalMethod.add(symbol)) {
            SymbolTable.error(formal.getContext(), id.getToken(), "Method " + formalMethod.getName() +
                    " of class " + methodClass.getName() + " redefines formal parameter " + id.getToken().getText());
            return null;
        }

        // caut tipul parametrului formal in scope-ul global
        var typeSymbol = SymbolTable.globals.lookupTypes(type.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(formal.getContext(), type.getToken(), "Method " + formalMethod.getName() +
                    " of class " + methodClass.getName() + " has formal parameter " + id.getToken().getText() +
                    " with undefined type " + type.getToken().getText());
            return null;
        }
        symbol.setType(typeSymbol);
        // adnotez id-ul nodului curent din arbore cu un nou simbol
        id.setSymbol(symbol);
        id.setScope(currentScope);

        return null;
    }

    @Override
    public Void visit(Let let) {
        currentScope = new LetScope(currentScope);
        let.getLocal().accept(this);
        let.getBody().accept(this);
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(Local local) {
        var id = local.getName();
        var type = local.getType();

        if (id.getToken().getText().compareTo("self") == 0) {
            SymbolTable.error(local.getContext(), id.getToken(), "Let variable has illegal name self");
            return null;
        }

        var symbol = new IdSymbol(id.getToken().getText());
        var localVariableScope = currentScope;
        localVariableScope.add(symbol);

        // caut tipul variabilei locale in scope-ul global
        var typeSymbol = SymbolTable.globals.lookupTypes(type.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(local.getContext(), type.getToken(), "Let variable " + id.getToken().getText()
                    + " has undefined type " + type.getToken().getText());
            return null;
        }
        symbol.setType(typeSymbol);
        // adnotez id-ul nodului curent din arbore cu un nou simbol
        id.setSymbol(symbol);
        id.setScope(localVariableScope);

        currentScope = currentScope.getParent();
        if (local.getInit() != null)
            local.getInit().accept(this); // nu ajung sa retin scope-ul expresiei de initializare in unele cazuri
        currentScope = localVariableScope;
        return null;
    }

    @Override
    public Void visit(Case case_) {
        case_.getCond().accept(this);

        for (var branch : case_.getCaseBranches())
            branch.accept(this);

        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        var id = caseBranch.getName();
        var type = caseBranch.getType();

        if (id.getToken().getText().compareTo("self") == 0) {
            SymbolTable.error(caseBranch.getContext(), id.getToken(), "Case variable has illegal name self");
            return null;
        }

        if (type.getToken().getText().compareTo("SELF_TYPE") == 0) {
            SymbolTable.error(caseBranch.getContext(), type.getToken(), "Case variable " + id.getToken().getText() +
                    " has illegal type SELF_TYPE");
            return null;
        }

        var caseScope = new CaseScope(currentScope);

        var symbol = new IdSymbol(id.getToken().getText());
        caseScope.add(symbol);

        // caut tipul variabilei din case in scope-ul global
        var typeSymbol = SymbolTable.globals.lookupTypes(type.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(caseBranch.getContext(), type.getToken(), "Case variable " + id.getToken().getText()
                    + " has undefined type " + type.getToken().getText());
            return null;
        }
        symbol.setType(typeSymbol);
        // adnotez id-ul nodului curent din arbore cu un nou simbol
        id.setSymbol(symbol);
        id.setScope(caseScope);

        currentScope = caseScope;
        caseBranch.getBody().accept(this);
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(Expression expression) {
        return null;
    }

    @Override
    public Void visit(Id id) {
        id.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(Int int_) {
        return null;
    }

    @Override
    public Void visit(String_ string) {
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        return null;
    }

    @Override
    public Void visit(UnaryMinus unaryMinus) {
        unaryMinus.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Not not) {
        not.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Negate negate) {
        negate.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        isVoid.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(New new_) {
        var type = new_.getType();
        var typeSymbol = SymbolTable.globals.lookupTypes(type.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(new_.getContext(), type.getToken(), "new is used with undefined type " +
                    type.getToken().getText());
            return null;
        }
        type.setSymbol(typeSymbol);

        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        arithmetic.getLeftExpr().accept(this);
        arithmetic.getRightExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        relational.getLeftExpr().accept(this);
        relational.getRightExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Block block) {
        for (var expr : block.getExpressions())
            expr.accept(this);

        return null;
    }

    @Override
    public Void visit(While while_) {
        while_.getCond().accept(this);
        while_.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(If if_) {
        if_.getCond().accept(this);
        if_.getThenBranch().accept(this);
        if_.getElseBranch().accept(this);
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        assign.getName().accept(this);
        assign.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(Dispatch dispatch) {
        return null;
    }

    @Override
    public Void visit(AtDispatch atDispatch) {
        var parentType = atDispatch.getParent();

        if (parentType.getToken().getText().compareTo("SELF_TYPE") == 0) {
            SymbolTable.error(atDispatch.getContext(), parentType.getToken(), "Type of static dispatch cannot be SELF_TYPE");
            return null;
        }

        // caut parintele pe care se face static dispatch in scope-ul global
        var typeSymbol = SymbolTable.globals.lookupTypes(parentType.getToken().getText(), currentClass);

        if (typeSymbol == null) {
            SymbolTable.error(atDispatch.getContext(), parentType.getToken(), "Type " + parentType.getToken().getText() +
                    " of static dispatch is undefined");
            return null;
        }
        parentType.setSymbol(typeSymbol);

        atDispatch.getExpr().accept(this);
        atDispatch.getMethodName().accept(this);

        for (var arg : atDispatch.getArgs())
            arg.accept(this);

        return null;
    }

    @Override
    public Void visit(SimpleDispatch simpleDispatch) {
        simpleDispatch.getMethodName().accept(this);

        for (var arg : simpleDispatch.getArgs())
            arg.accept(this);

        return null;
    }

    @Override
    public Void visit(ExplicitDispatch explicitDispatch) {
        explicitDispatch.getExpr().accept(this);
        explicitDispatch.getMethodName().accept(this);

        for (var arg : explicitDispatch.getArgs())
            arg.accept(this);

        return null;
    }
}
