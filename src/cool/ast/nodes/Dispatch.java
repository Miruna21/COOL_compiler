package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public abstract class Dispatch extends Expression {
    Dispatch(ParserRuleContext context, Token token) {
        super(context, token);
    }

    public abstract Expression getExpr();
    public abstract Type getParent();
    public abstract Id getMethodName();
    public abstract List<Expression> getArgs();

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
