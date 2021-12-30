package cool.structures;

import cool.ast.ASTVisitor;
import cool.ast.nodes.*;
import cool.lexer.CoolLexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResolutionPassVisitor implements ASTVisitor<TypeSymbol> {
    ClassSymbol currentClass = null;

    @Override
    public TypeSymbol visit(Program program) {
        TypeSymbol mainType = SymbolTable.globals.lookupTypes("Main", null);

        if (mainType == null) {
            SymbolTable.error(program.getContext(), program.getToken(), "No class Main");
            return null;
        }

        if (mainType.lookupMethod("main") == null) {
            SymbolTable.error(program.getContext(), program.getToken(), "No method main in class Main");
            return null;
        }

        for (var classes : program.getClasses())
            classes.accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(Class_ class_) {
        currentClass = (ClassSymbol) class_.getType().getSymbol();

        for (var feature : class_.getFeatures())
            feature.accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(Attribute attribute) {
        var id = attribute.getName();
        var symbol = (IdSymbol) id.getSymbol();

        if (symbol == null)
            return null;

        var typeSymbol = symbol.getType();

        // daca gasesc acelasi nume de atribut pe lantul de mostenire => eroare (nu pot suprascrie atributele)
        if (currentClass.getParent().lookup(id.getToken().getText()) != null) {
            SymbolTable.error(attribute.getContext(), id.getToken(), "Class " + currentClass +
                    " redefines inherited attribute " + id.getToken().getText());
            return null;
        }

        TypeSymbol initExprType;

        if (attribute.getInit() != null) {
            initExprType = attribute.getInit().accept(this);

            if (initExprType != null)
                if (!checkTypeCompatibility(typeSymbol, initExprType)) {
                    SymbolTable.error(attribute.getContext(), attribute.getInit().getToken(), "Type " + initExprType +
                            " of initialization expression of attribute " + symbol.getName() + " is incompatible with declared type " +
                            typeSymbol);
                    return null;
                }
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Method method) {
        var id = method.getName();
        var symbol = (MethodSymbol) id.getSymbol();

        if (symbol == null)
            return null;

        var typeSymbol = symbol.getType();
        var methodFormals = method.getFormals();

        // daca gasesc acelasi nume de metoda pe lantul de mostenire => verific daca a fost suprascrisa corect metoda respectiva
        var parentClass = (TypeSymbol) currentClass.getParent();
        var parentMethod = (MethodSymbol) parentClass.lookupMethod(id.getToken().getText());
        if (parentMethod != null) {
            // metoda a fost adaugata in scope-ul unei clase, dar nu are un tip corect
            if (parentMethod.getType() == null)
                return null;

            var parentFormals = parentMethod.getFormals();

            if (parentFormals.size() != methodFormals.size()) {
                SymbolTable.error(method.getContext(), id.getToken(), "Class " + currentClass +
                        " overrides method " + id.getToken().getText() + " with different number of formal parameters");
                return null;
            }

            int i = 0;
            for (Map.Entry<String, IdSymbol> entry : parentFormals.entrySet()) {
                var methodFormalSymbol = (IdSymbol) methodFormals.get(i).getArgName().getSymbol();
                var parentFormalSymbol = entry.getValue();

                var methodFormalType = methodFormalSymbol.getType();
                var parentFormalType = parentFormalSymbol.getType();

                if (parentFormalType != methodFormalType) {
                    SymbolTable.error(method.getContext(), methodFormals.get(i).getType().getToken(),
                            "Class " + currentClass + " overrides method " + id.getToken().getText() +
                                    " but changes type of formal parameter " + methodFormalSymbol.getName() + " from "
                                    + parentFormalSymbol.getType() + " to " +  methodFormalType);
                    return null;
                }
                i++;
            }

            var methodReturnType = symbol.getType();
            var parentReturnType = parentMethod.getType();

            if (parentReturnType != methodReturnType && !(parentReturnType instanceof SelfTypeSymbol)) {
                SymbolTable.error(method.getContext(), method.getType().getToken(), "Class " +
                        currentClass + " overrides method " + id.getToken().getText() +
                        " but changes return type from " + parentReturnType + " to " + methodReturnType);
                return null;
            }
        }

        for (var formals : methodFormals)
            formals.accept(this);

        var bodyType = method.getBody().accept(this);

        if (bodyType != null)
            if (!checkTypeCompatibility(typeSymbol, bodyType)) {
                SymbolTable.error(method.getContext(), method.getBody().getToken(), "Type " + bodyType +
                        " of the body of method " + symbol.getName() + " is incompatible with declared return type " +
                        typeSymbol);
                return null;
            }

        return null;
    }

    @Override
    public TypeSymbol visit(Type type) {
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        return null;
    }

    @Override
    public TypeSymbol visit(Let let) {
        let.getLocal().accept(this);
        return let.getBody().accept(this);
    }

    @Override
    public TypeSymbol visit(Local local) {
        var localSymbol = (IdSymbol) local.getName().getSymbol();
        if (localSymbol == null)
            return null;

        var localType = localSymbol.getType();

        if (local.getInit() != null) {
            var initExprType = local.getInit().accept(this);

            if (initExprType == null)
                return null;

            if (!checkTypeCompatibility(localType, initExprType)) {
                SymbolTable.error(local.getContext(), local.getInit().getToken(), "Type " + initExprType +
                        " of initialization expression of identifier " + localSymbol.getName() +
                        " is incompatible with declared type " + localType);
                return null;
            }
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Case case_) {
        case_.getCond().accept(this);

        if (case_.getCaseBranches().size() == 1)
            return case_.getCaseBranches().get(0).accept(this);

        TypeSymbol lub = case_.getCaseBranches().get(0).accept(this);

        for (var branch : case_.getCaseBranches()) {
            var caseBranchType = branch.accept(this);
            if (caseBranchType == null)
                return ClassSymbol.OBJECT;

            lub = findLeastUpperBound(lub, caseBranchType);
        }

        return lub;
    }

    @Override
    public TypeSymbol visit(CaseBranch caseBranch) {
        return caseBranch.getBody().accept(this);
    }

    @Override
    public TypeSymbol visit(Expression expression) {
        return null;
    }

    @Override
    public TypeSymbol visit(Id id) {  // aici n-am un tip default de intors => intorc null in caz de eroare semantica
        var scope = id.getScope();
        if (scope == null)
            return null;

        var symbol = scope.lookup(id.getToken().getText());

        if (symbol == null) {
            SymbolTable.error(id.getContext(), id.getToken(), "Undefined identifier " + id.getToken().getText());
            return null;
        }

        id.setSymbol(symbol);
        var typeSymbol = (IdSymbol) symbol;

        return typeSymbol.getType();
    }

    @Override
    public TypeSymbol visit(Int int_) {
        return ClassSymbol.INT;
    }

    @Override
    public TypeSymbol visit(String_ string) {
        return ClassSymbol.STRING;
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        return ClassSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(UnaryMinus unaryMinus) {
        var op = unaryMinus.getToken().getText();
        var exprType = unaryMinus.getExpr().accept(this);

        if (exprType == null)
            return null;

        if (exprType != ClassSymbol.INT) {
            SymbolTable.error(unaryMinus.getContext(), unaryMinus.getExpr().getToken(), "Operand of " +
                    op + " has type " + exprType + " instead of Int");
        }

        return ClassSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Not not) {
        var exprType = not.getExpr().accept(this);

        if (exprType != null)
            if (exprType != ClassSymbol.BOOL) {
                SymbolTable.error(not.getContext(), not.getExpr().getToken(), "Operand of not has type " +
                        exprType + " instead of Bool");
                return null;
            }

        return ClassSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(Negate negate) {
        var op = negate.getToken().getText();
        var exprType = negate.getExpr().accept(this);

        if (exprType != null)
            if (exprType != ClassSymbol.INT) {
                SymbolTable.error(negate.getContext(), negate.getExpr().getToken(), "Operand of " +
                        op + " has type " + exprType + " instead of Int");
            }

        return ClassSymbol.INT;
    }

    @Override
    public TypeSymbol visit(IsVoid isVoid) {
        isVoid.getExpr().accept(this);
        return ClassSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(New new_) {
        return (TypeSymbol) new_.getType().getSymbol();
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        var op = arithmetic.getToken().getText();
        var leftType = arithmetic.getLeftExpr().accept(this);
        var rightType = arithmetic.getRightExpr().accept(this);

        if (leftType != null)
            if (leftType != ClassSymbol.INT) {
                SymbolTable.error(arithmetic.getContext(), arithmetic.getLeftExpr().getToken(), "Operand of " +
                        op + " has type " + leftType + " instead of Int");
            }

        if (rightType != null)
            if (rightType != ClassSymbol.INT) {
                SymbolTable.error(arithmetic.getContext(), arithmetic.getRightExpr().getToken(), "Operand of " +
                        op + " has type " + rightType + " instead of Int");
            }

        return ClassSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        var op = relational.getToken().getText();
        var leftType = relational.getLeftExpr().accept(this);
        var rightType = relational.getRightExpr().accept(this);

        if (relational.getToken().getType() == CoolLexer.EQUAL && (leftType != null && rightType != null)) {
            if ((leftType == ClassSymbol.INT || rightType == ClassSymbol.INT) || (leftType == ClassSymbol.BOOL
                    || rightType == ClassSymbol.BOOL) || (leftType == ClassSymbol.STRING || rightType == ClassSymbol.STRING)) {

                if (leftType != rightType) {
                    SymbolTable.error(relational.getContext(), relational.getToken(), "Cannot compare " +
                            leftType + " with " + rightType);
                }
            }
        } else {
            if (leftType != null)
                if (leftType != ClassSymbol.INT) {
                    SymbolTable.error(relational.getContext(), relational.getLeftExpr().getToken(), "Operand of " +
                            op + " has type " + leftType + " instead of Int");
                }

            if (rightType != null)
                if (rightType != ClassSymbol.INT) {
                    SymbolTable.error(relational.getContext(), relational.getRightExpr().getToken(), "Operand of " +
                            op + " has type " + rightType + " instead of Int");
                }
        }

        return ClassSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(Block block) {
        TypeSymbol lastExprType = null;
        for (var expr : block.getExpressions())
            lastExprType = expr.accept(this);

        return lastExprType;
    }

    @Override
    public TypeSymbol visit(While while_) {
        var condType = while_.getCond().accept(this);

        if (condType != null)
            if (condType != ClassSymbol.BOOL) {
                SymbolTable.error(while_.getContext(), while_.getCond().getToken(), "While condition has type " +
                        condType + " instead of Bool");
            }
        while_.getBody().accept(this);

        return ClassSymbol.OBJECT;
    }

    private TypeSymbol findLeastUpperBound(TypeSymbol type1, TypeSymbol type2) {
        if (type1 == type2)  // doua tipuri egale (chiar si SELF_TYPE din aceeasi clasa)
            return type1;

        if ((type1 instanceof SelfTypeSymbol) && (type2 instanceof SelfTypeSymbol))
            return ClassSymbol.OBJECT;  // SELF_TYPE din clase diferite
        else if (type1 instanceof SelfTypeSymbol)
            return findLeastUpperBound(type1.getCrtClass(), type2);
        else if (type2 instanceof SelfTypeSymbol)
            return findLeastUpperBound(type1, type2.getCrtClass());

        if (type1 == ClassSymbol.OBJECT || type2 == ClassSymbol.OBJECT)
            return ClassSymbol.OBJECT;

        var node1 = type1;
        List<TypeSymbol> path1 = new ArrayList<>();

        while (node1 != ClassSymbol.OBJECT) {
            path1.add(node1);
            node1 = (TypeSymbol) node1.getParent();
        }

        var node2 = type2;
        List<TypeSymbol> path2 = new ArrayList<>();

        while (node2 != ClassSymbol.OBJECT) {
            path2.add(node2);
            node2 = (TypeSymbol) node2.getParent();
        }

        var path1Size = path1.size();
        var path2Size = path2.size();

        int i = path1Size - 1;
        int j = path2Size - 1;
        TypeSymbol lub = ClassSymbol.OBJECT;

        while (true) {
            if (path1.get(i) != path2.get(j)) {
                break;
            } else {
                lub = path1.get(i);
                i--;
                j--;
                if (i < 0 || j < 0) {
                    break;
                }
            }
        }

        return lub;
    }

    @Override
    public TypeSymbol visit(If if_) {
        var condType = if_.getCond().accept(this);

        if (condType != null)
            if (condType != ClassSymbol.BOOL) {
                SymbolTable.error(if_.getContext(), if_.getCond().getToken(), "If condition has type " +
                        condType + " instead of Bool");
            }

        var thenBranchType = if_.getThenBranch().accept(this);
        var elseBranchType = if_.getElseBranch().accept(this);

        if (thenBranchType != null && elseBranchType != null)
            return findLeastUpperBound(thenBranchType, elseBranchType);

        return ClassSymbol.OBJECT;
    }

    @Override
    public TypeSymbol visit(Assign assign) {  // aici n am un tip default de intors => intorc null in caz de eroare
        var id = assign.getName();

        if (id.getToken().getText().compareTo("self") == 0) {
            SymbolTable.error(assign.getContext(), assign.getName().getToken(), "Cannot assign to self");
            return null;
        }

        var varType = assign.getName().accept(this);
        var exprType = assign.getExpr().accept(this);

        if (varType == null || exprType == null)
            return null;

        if (!checkTypeCompatibility(varType, exprType)) {
            SymbolTable.error(assign.getContext(), assign.getExpr().getToken(), "Type " + exprType +
                    " of assigned expression is incompatible with declared type " + varType +
                    " of identifier " + id.getToken().getText());
            return null;
        }

        return varType;
    }

    private boolean checkTypeCompatibility(TypeSymbol generalType, TypeSymbol specificType) {
        if (generalType == specificType)  // doua tipuri egale (chiar si SELF_TYPE din aceeasi clasa)
            return true;

        if ((generalType instanceof SelfTypeSymbol) && (specificType instanceof SelfTypeSymbol))
            return false;  // SELF_TYPE din clase diferite
        else if (specificType instanceof SelfTypeSymbol)
            return checkTypeCompatibility(generalType, specificType.getCrtClass());
        else if (generalType instanceof SelfTypeSymbol) {
            return false;
        }

        var parent = specificType.getParent();

        while (parent != SymbolTable.globals) {
            if (parent == generalType)
                return true;

            parent = parent.getParent();
        }

        return false;
    }

    private void checkMethodCall(Dispatch dispatch, MethodSymbol symbol, TypeSymbol methodClass,
                    Map<String, IdSymbol> formals, List<Expression> callArgs) {

        if (formals.size() != callArgs.size()) {
            SymbolTable.error(dispatch.getContext(), dispatch.getMethodName().getToken(),
                    "Method " + symbol.getName() + " of class " + methodClass.getName()
                            + " is applied to wrong number of arguments");
            return;
        }

        int i = 0;
        for (var formal : formals.entrySet()) {
            var arg = callArgs.get(i);
            var argType = arg.accept(this);

            var formalSymbol = (IdSymbol) formal.getValue();
            var formalType = formalSymbol.getType();

            if (!checkTypeCompatibility(formalType, argType)) {
                SymbolTable.error(dispatch.getContext(), arg.getToken(), "In call to method " + symbol.getName() +
                        " of class " + methodClass + ", actual type " + argType + " of formal parameter "
                        + formalSymbol.getName() + " is incompatible with declared type " + formalType);
                return;
            }
            i++;
        }
    }

    @Override
    public TypeSymbol visit(Dispatch dispatch) {
        return null;
    }

    @Override
    public TypeSymbol visit(AtDispatch atDispatch) {
        var methodId = atDispatch.getMethodName();
        var exprType = atDispatch.getExpr().accept(this);

        if (exprType == null)
            return null;

        var parentType = (TypeSymbol) atDispatch.getParent().getSymbol();

        if (!checkTypeCompatibility(parentType, exprType)) {
            SymbolTable.error(atDispatch.getContext(), atDispatch.getParent().getToken(), "Type " +
                    parentType + " of static dispatch is not a superclass of type " + exprType);
            return null;
        }

        MethodSymbol symbol;

        symbol = (MethodSymbol) parentType.lookupMethod(methodId.getToken().getText());

        if (symbol == null) {
            SymbolTable.error(atDispatch.getContext(), methodId.getToken(),
                    "Undefined method " + methodId.getToken().getText() + " in class " + parentType);
            return null;
        }

        // metoda a fost adaugata in scope-ul unei clase, dar nu are tipul corect
        if (symbol.getType() == null)
            return null;

        // adnotez nodul din AST corespunzator numelui metodei cu simbolul gasit
        methodId.setSymbol(symbol);

        var formals = symbol.getFormals();
        var callArgs = atDispatch.getArgs();

        checkMethodCall(atDispatch, symbol, parentType, formals, callArgs);

        TypeSymbol callType;
        if (symbol.getType() instanceof SelfTypeSymbol)
            callType = exprType;
        else
            callType = symbol.getType();

        return callType;
    }

    @Override
    public TypeSymbol visit(SimpleDispatch simpleDispatch) {
        var methodId = simpleDispatch.getMethodName();
        var symbol = (MethodSymbol) currentClass.lookupMethod(methodId.getToken().getText());

        if (symbol == null) {
            SymbolTable.error(simpleDispatch.getContext(), methodId.getToken(),
                    "Undefined method " + methodId.getToken().getText() + " in class " + currentClass);
            return null;
        }

        // metoda a fost adaugata in scope-ul unei clase, dar nu are tipul corect
        if (symbol.getType() == null)
            return null;

        // adnotez nodul din AST corespunzator numelui metodei cu simbolul gasit
        methodId.setSymbol(symbol);

        var formals = symbol.getFormals();
        var callArgs = simpleDispatch.getArgs();

        checkMethodCall(simpleDispatch, symbol, currentClass, formals, callArgs);

        return symbol.getType();
    }

    @Override
    public TypeSymbol visit(ExplicitDispatch explicitDispatch) {
        var methodId = explicitDispatch.getMethodName();
        var exprType = explicitDispatch.getExpr().accept(this);

        if (exprType == null)
            return null;

        MethodSymbol symbol;
        TypeSymbol methodClass;

        if (exprType instanceof SelfTypeSymbol)
            methodClass = currentClass;
        else
            methodClass = exprType;

        symbol = (MethodSymbol) methodClass.lookupMethod(methodId.getToken().getText());

        if (symbol == null) {
            SymbolTable.error(explicitDispatch.getContext(), methodId.getToken(),
                    "Undefined method " + methodId.getToken().getText() + " in class " + methodClass);
            return null;
        }

        // metoda a fost adaugata in scope-ul unei clase, dar nu are tipul corect
        if (symbol.getType() == null)
            return null;

        // adnotez nodul din AST corespunzator numelui metodei cu simbolul gasit
        methodId.setSymbol(symbol);

        var formals = symbol.getFormals();
        var callArgs = explicitDispatch.getArgs();

        checkMethodCall(explicitDispatch, symbol, methodClass, formals, callArgs);

        TypeSymbol callType;
        if (symbol.getType() instanceof SelfTypeSymbol && exprType instanceof SelfTypeSymbol)  // x <- self.f1() => ST(A) <- ST(C).ST(A), C < A
            callType = symbol.getType();
        else if (symbol.getType() instanceof SelfTypeSymbol)
            callType = exprType;
        else
            callType = symbol.getType();

        return callType;
    }
}
