package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class If extends Expression {
    private final Expression cond;
    private final Expression thenBranch;
    private final Expression elseBranch;

    public If(ParserRuleContext context, Token start, Expression cond, Expression thenBranch, Expression elseBranch) {
        super(context, start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression getCond() {
        return cond;
    }

    public Expression getThenBranch() {
        return thenBranch;
    }

    public Expression getElseBranch() {
        return elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
