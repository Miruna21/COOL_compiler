package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class CaseBranch extends ASTNode {
    private final Id name;
    private final Type type;
    private final Expression body;

    public CaseBranch(ParserRuleContext context, Token token, Id name, Type type, Expression body) {
        super(context, token);
        this.name = name;
        this.type = type;
        this.body = body;
    }

    public Id getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Expression getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
