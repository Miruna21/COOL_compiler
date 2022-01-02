package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class New extends Expression {
    private final Type type;

    public New(ParserRuleContext context, Token token, Type type) {
        super(context, token);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
