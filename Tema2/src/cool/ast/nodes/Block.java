package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Block extends Expression {
    private final List<Expression> expressions;

    public Block(ParserRuleContext context, Token token, List<Expression> expressions) {
        super(context, token);
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
