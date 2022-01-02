package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Method extends Feature {
    private final Type type;
    private final Id name;
    private final List<Formal> formals;
    private final Expression body;

    public Method(ParserRuleContext context, Token token, Type type, Id name, List<Formal> formals, Expression body) {
        super(context, token);
        this.type = type;
        this.name = name;
        this.formals = formals;
        this.body = body;
    }

    public Type getType() {
        return type;
    }

    public Id getName() {
        return name;
    }

    public List<Formal> getFormals() {
        return formals;
    }

    public Expression getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
