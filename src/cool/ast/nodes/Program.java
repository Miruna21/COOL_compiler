package cool.ast.nodes;

import cool.ast.ASTVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class Program extends ASTNode {
    private final List<Class_> classes;

    public Program(ParserRuleContext context, Token token, List<Class_> classes) {
        super(context, token);
        this.classes = classes;
    }

    public List<Class_> getClasses() {
        return classes;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
