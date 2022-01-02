package cool.ast.nodes;
import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class ASTNode {
    private final ParserRuleContext context;
    private final Token token;
    public String debugStr = null;		// used in codegen

    public ASTNode(ParserRuleContext context, Token token) {
        this.context = context;
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public ParserRuleContext getContext() {
        return context;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

