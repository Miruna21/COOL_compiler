package cool.ast;

import cool.ast.nodes.*;

public class ASTBaseVisitor implements cool.ast.ASTVisitor<Void> {
    int indent = 0;

    @Override
    public Void visit(Program program) {
        printIndent("program");
        indent++;
        for (var class_ : program.getClasses())
            class_.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Class_ class_) {
        printIndent("class");
        indent++;
        class_.getType().accept(this);
        if (class_.getParent() != null)
            class_.getParent().accept(this);
        for (var feature : class_.getFeatures())
            feature.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Attribute attribute) {
        printIndent("attribute");
        indent++;
        attribute.getName().accept(this);
        attribute.getType().accept(this);
        if (attribute.getInit() != null)
            attribute.getInit().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Method method) {
        printIndent("method");
        indent++;
        method.getName().accept(this);
        for (var formal : method.getFormals())
            formal.accept(this);
        method.getType().accept(this);
        method.getBody().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Type type) {
        printIndent(type.getToken().getText());
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndent("formal");
        indent++;
        formal.getArgName().accept(this);
        formal.getType().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Local local) {
        printIndent("local");
        indent++;
        local.getName().accept(this);
        local.getType().accept(this);
        if (local.getInit() != null)
            local.getInit().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Expression expression) {
        return null;
    }

    @Override
    public Void visit(Id id) {
        printIndent(id.getToken().getText());
        return null;
    }

    @Override
    public Void visit(Int int_) {
        printIndent(int_.getToken().getText());
        return null;
    }

    @Override
    public Void visit(String_ string) {
        printIndent(string.getToken().getText());
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        printIndent(bool.getToken().getText());
        return null;
    }

    @Override
    public Void visit(UnaryMinus unaryMinus) {
        printIndent("unary_minus");
        indent++;
        unaryMinus.getExpr().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Not not) {
        printIndent("not");
        indent++;
        not.getExpr().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Negate negate) {
        printIndent(negate.getToken().getText());
        indent++;
        negate.getExpr().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        printIndent("isvoid");
        indent++;
        isVoid.getExpr().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(New new_) {
        printIndent("new");
        indent++;
        new_.getType().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        printIndent(arithmetic.getToken().getText());
        indent++;
        arithmetic.getLeftExpr().accept(this);
        arithmetic.getRightExpr().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        printIndent(relational.getToken().getText());
        indent++;
        relational.getLeftExpr().accept(this);
        relational.getRightExpr().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Case case_) {
        printIndent("case");
        indent++;
        case_.getCond().accept(this);
        for (var branch : case_.getCaseBranches())
            branch.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(CaseBranch caseBranch) {
        printIndent("case branch");
        indent++;
        caseBranch.getName().accept(this);
        caseBranch.getType().accept(this);
        caseBranch.getBody().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Let let) {
        printIndent("let");
        indent++;
        let.getLocal().accept(this);
        let.getBody().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Block block) {
        printIndent("block");
        indent++;
        for (var expr : block.getExpressions())
            expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(While while_) {
        printIndent("while");
        indent++;
        while_.getCond().accept(this);
        while_.getBody().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(If if_) {
        printIndent("if");
        indent++;
        if_.getCond().accept(this);
        if_.getThenBranch().accept(this);
        if_.getElseBranch().accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Dispatch dispatch) {
        return null;
    }

    @Override
    public Void visit(AtDispatch atDispatch) {
        printIndent(".");
        indent++;
        atDispatch.getExpr().accept(this);
        atDispatch.getParent().accept(this);
        atDispatch.getMethodName().accept(this);
        for (var arg : atDispatch.getArgs())
            arg.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(SimpleDispatch simpleDispatch) {
        printIndent("implicit dispatch");
        indent++;
        simpleDispatch.getMethodName().accept(this);
        for (var arg : simpleDispatch.getArgs())
            arg.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(ExplicitDispatch explicitDispatch) {
        printIndent(".");
        indent++;
        explicitDispatch.getExpr().accept(this);
        explicitDispatch.getMethodName().accept(this);
        for (var arg : explicitDispatch.getArgs())
            arg.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        printIndent(assign.getToken().getText());
        indent++;
        assign.getName().accept(this);
        assign.getExpr().accept(this);
        indent--;
        return null;
    }

    void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("  ");
        System.out.println(str);
    }
}
