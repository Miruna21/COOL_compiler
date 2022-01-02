package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Relational extends Expression {
    private final Expression leftExpr;
    private final Expression rightExpr;

    public Relational(ParserRuleContext context, Token token, Expression leftExpr, Expression rightExpr) {
        super(context, token);
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    public Expression getLeftExpr() {
        return leftExpr;
    }

    public Expression getRightExpr() {
        return rightExpr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
