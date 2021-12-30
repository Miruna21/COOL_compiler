package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class SimpleDispatch extends Dispatch {
    private final Id methodName;
    private final List<Expression> args;

    public SimpleDispatch(ParserRuleContext context, Token token, Id methodName, List<Expression> args) {
        super(context, token);
        this.methodName = methodName;
        this.args = args;
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
    public Expression getExpr() {
        return null;
    }
    @Override
    public Type getParent() {
        return null;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
