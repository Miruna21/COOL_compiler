package cool.ast;

import cool.ast.nodes.*;

public interface ASTVisitor<T> {
    T visit(Program program);
    T visit(Class_ class_);
    T visit(Attribute attribute);
    T visit(Method method);
    T visit(Type type);
    T visit(Formal formal);
    T visit(Local local);
    T visit(Expression expression);
    T visit(Id id);
    T visit(Int int_);
    T visit(String_ string);
    T visit(Bool bool);
    T visit(UnaryMinus unaryMinus);
    T visit(Not not);
    T visit(Negate negate);
    T visit(IsVoid isVoid);
    T visit(New new_);
    T visit(Arithmetic arithmetic);
    T visit(Relational relational);
    T visit(Case case_);
    T visit(CaseBranch caseBranch);
    T visit(Let let);
    T visit(Block block);
    T visit(While while_);
    T visit(If if_);
    T visit(Dispatch dispatch);
    T visit(AtDispatch atDispatch);
    T visit(SimpleDispatch simpleDispatch);
    T visit(ExplicitDispatch explicitDispatch);
    T visit(Assign assign);
}

