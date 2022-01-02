package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Assign extends Expression {
    private final Id name;
    private final Expression expr;

    public Assign(ParserRuleContext context, Token token, Id name, Expression expr) {
        super(context, token);
        this.name = name;
        this.expr = expr;
    }

    public Id getName() {
        return name;
    }

    public Expression getExpr() {
        return expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
