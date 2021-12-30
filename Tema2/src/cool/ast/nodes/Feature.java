package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Feature extends ASTNode {

    public Feature(ParserRuleContext context, Token token) {
        super(context, token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}
