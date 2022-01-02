package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Let extends Expression {
    private final Local local;
    private final Expression body;

    public Let(ParserRuleContext context, Token token, Local local, Expression body) {
        super(context, token);
        this.local = local;
        this.body = body;
    }

    public Local getLocal() {
        return local;
    }

    public Expression getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
