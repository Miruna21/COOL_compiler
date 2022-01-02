package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Negate extends Expression {
    private final Expression expr;

    public Negate(ParserRuleContext context, Token token, Expression expr) {
        super(context, token);
        this.expr = expr;
    }

    public Expression getExpr() {
        return expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
