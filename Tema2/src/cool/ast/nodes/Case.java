package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Case extends Expression {
    private final Expression cond;
    private final List<CaseBranch> caseBranches;

    public Case(ParserRuleContext context, Token token, Expression cond, List<CaseBranch> caseBranches) {
        super(context, token);
        this.cond = cond;
        this.caseBranches = caseBranches;
    }

    public Expression getCond() {
        return cond;
    }

    public List<CaseBranch> getCaseBranches() {
        return caseBranches;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
