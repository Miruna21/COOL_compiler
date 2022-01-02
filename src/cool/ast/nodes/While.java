package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class While extends Expression {
    private final Expression cond;
    private final Expression body;

    public While(ParserRuleContext context, Token token, Expression cond, Expression body) {
        super(context, token);
        this.cond = cond;
        this.body = body;
    }

    public Expression getCond() {
        return cond;
    }

    public Expression getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
