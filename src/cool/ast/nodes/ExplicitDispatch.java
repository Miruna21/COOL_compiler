package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class ExplicitDispatch extends Dispatch {
    private final Expression expr;
    private final Id methodName;
    private final List<Expression> args;

    public ExplicitDispatch(ParserRuleContext context, Token token, Expression expr, Id methodName, List<Expression> args) {
        super(context, token);
        this.expr = expr;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public Expression getExpr() {
        return expr;
    }
    @Override
    public Id getMethodName() {
        return methodName;
    }
    @Override
    public List<Expression> getArgs() {
        return args;
    }
    @Override
    public Type getParent() {
        return null;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
