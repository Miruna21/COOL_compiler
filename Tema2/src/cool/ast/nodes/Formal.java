package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Formal extends ASTNode {
    private final Type type;
    private final Id argName;

    public Formal(ParserRuleContext context, Token token, Type type, Id argName) {
        super(context, token);
        this.type = type;
        this.argName = argName;
    }

    public Type getType() {
        return type;
    }

    public Id getArgName() {
        return argName;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
