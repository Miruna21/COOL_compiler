package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Attribute extends Feature {
    private final Type type;
    private final Id name;
    private final Expression init;

    public Attribute(ParserRuleContext context, Token token, Type type, Id name, Expression init) {
        super(context, token);
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public Type getType() {
        return type;
    }

    public Id getName() {
        return name;
    }

    public Expression getInit() {
        return init;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
