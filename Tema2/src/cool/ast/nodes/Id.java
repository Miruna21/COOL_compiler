package cool.ast.nodes;

import cool.ast.ASTVisitor;
import cool.structures.Scope;
import cool.structures.Symbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Id extends Expression {
    private Symbol symbol;
    private Scope scope;

    public Id(ParserRuleContext context, Token token) {
        super(context, token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Scope getScope() {
        return scope;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
