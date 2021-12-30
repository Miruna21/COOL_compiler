package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Class_ extends ASTNode {
    private final Type type;
    private final Type parent;
    private final List<Feature> features;

    public Class_(ParserRuleContext context, Token token, Type type, Type parent, List<Feature> features) {
        super(context, token);
        this.type = type;
        this.parent = parent;
        this.features = features;
    }

    public Type getType() {
        return type;
    }

    public Type getParent() {
        return parent;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
