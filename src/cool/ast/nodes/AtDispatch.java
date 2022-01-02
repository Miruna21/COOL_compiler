package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class AtDispatch extends Dispatch {
    private final Expression expr;
    private final Type parent;
    private final Id methodName;
    private final List<Expression> args;

    public AtDispatch(ParserRuleContext context, Token token, Expression expr, Type parent, Id methodName, List<Expression> args) {
        super(context, token);
        this.expr = expr;
        this.parent = parent;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public Expression getExpr() {
        return expr;
    }
    @Override
    public Type getParent() {
        return parent;
    }
    @Override
    public Id getMethodName() {
        return methodName;
    }
    @Override
    public List<Expression> getArgs() {
        return args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
