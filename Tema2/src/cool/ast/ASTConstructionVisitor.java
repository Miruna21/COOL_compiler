package cool.ast;

import cool.ast.nodes.*;
import cool.parser.CoolParser;
import cool.parser.CoolParserBaseVisitor;

import java.util.LinkedList;
import java.util.List;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        List<Class_> classes = new LinkedList<>();

        for (var class_ : ctx.class_()) {
            classes.add((Class_) visit(class_));
        }
        return new Program(ctx, ctx.start, classes);
    }

    @Override
    public ASTNode visitClass_(CoolParser.Class_Context ctx) {
        List<Feature> features = new LinkedList<>();

        for (var feature : ctx.features) {
            features.add((Feature) visit(feature));
        }

        return new Class_(ctx, ctx.start, new Type(ctx, ctx.type),
                ctx.parent != null ? new Type(ctx, ctx.parent): null, features);
    }

    @Override
    public ASTNode visitFeature(CoolParser.FeatureContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public ASTNode visitFormal(CoolParser.FormalContext ctx) {
        return new Formal(ctx, ctx.start, new Type(ctx, ctx.type), new Id(ctx, ctx.name));
    }

    @Override
    public ASTNode visitAttribute(CoolParser.AttributeContext ctx) {
        return new Attribute(ctx, ctx.start, new Type(ctx, ctx.type), new Id(ctx, ctx.name),
                ctx.init != null? (Expression) visit(ctx.init):null);
    }

    @Override
    public ASTNode visitMethod(CoolParser.MethodContext ctx) {
        List<Formal> formals = new LinkedList<>();

        for (var formal : ctx.formals) {
            formals.add((Formal) visit(formal));
        }

        return new Method(ctx, ctx.start, new Type(ctx, ctx.type), new Id(ctx, ctx.name), formals, (Expression) visit(ctx.body));
    }

    @Override
    public ASTNode visitNew(CoolParser.NewContext ctx) {
        return new New(ctx, ctx.start, new Type(ctx, ctx.type));
    }

    @Override
    public ASTNode visitIdentifier(CoolParser.IdentifierContext ctx) {
        return new Id(ctx, ctx.ID().getSymbol());
    }

    @Override
    public ASTNode visitConditional(CoolParser.ConditionalContext ctx) {
        return new If(ctx, ctx.start, (Expression)visit(ctx.cond),
                (Expression)visit(ctx.thenBranch),
                (Expression)visit(ctx.elseBranch));
    }

    @Override
    public ASTNode visitString(CoolParser.StringContext ctx) {
        return new String_(ctx, ctx.STRING().getSymbol());
    }

    @Override
    public ASTNode visitAssignment(CoolParser.AssignmentContext ctx) {
        return new Assign(ctx, ctx.ASSIGN().getSymbol(), new Id(ctx, ctx.name), (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitIsvoid(CoolParser.IsvoidContext ctx) {
        return new IsVoid(ctx, ctx.start, (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitCmp(CoolParser.CmpContext ctx) {
        return new Relational(ctx, ctx.op, (Expression) visit(ctx.left), (Expression) visit(ctx.right));
    }

    @Override
    public ASTNode visitFalse(CoolParser.FalseContext ctx) {
        return new Bool(ctx, ctx.FALSE().getSymbol());
    }

    @Override
    public ASTNode visitAt_dispatch(CoolParser.At_dispatchContext ctx) {
        List<Expression> args = new LinkedList<>();

        for (var arg : ctx.args) {
            args.add((Expression) visit(arg));
        }

        return new AtDispatch(ctx, ctx.start, (Expression) visit(ctx.e), new Type(ctx, ctx.parent),
                new Id(ctx, ctx.method_name), args);
    }

    @Override
    public ASTNode visitInt(CoolParser.IntContext ctx) {
        return new Int(ctx, ctx.INT().getSymbol());
    }

    @Override
    public ASTNode visitPlus_minus(CoolParser.Plus_minusContext ctx) {
        return new Arithmetic(ctx, ctx.op, (Expression)visit(ctx.left),
                (Expression)visit(ctx.right));
    }

    @Override
    public ASTNode visitMult_div(CoolParser.Mult_divContext ctx) {
        return new Arithmetic(ctx, ctx.op, (Expression)visit(ctx.left),
                (Expression)visit(ctx.right));
    }

    @Override
    public ASTNode visitNot(CoolParser.NotContext ctx) {
        return new Not(ctx, ctx.start, (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitParen(CoolParser.ParenContext ctx) {
        return visit(ctx.e);
    }

    @Override
    public ASTNode visitExplicit_dispatch(CoolParser.Explicit_dispatchContext ctx) {
        List<Expression> args = new LinkedList<>();

        for (var arg : ctx.args) {
            args.add((Expression) visit(arg));
        }

        return new ExplicitDispatch(ctx, ctx.start, (Expression) visit(ctx.e),
                new Id(ctx, ctx.method_name), args);
    }

    @Override
    public ASTNode visitLoop(CoolParser.LoopContext ctx) {
        return new While(ctx, ctx.start, (Expression) visit(ctx.cond), (Expression) visit(ctx.body));
    }

    @Override
    public ASTNode visitNegate(CoolParser.NegateContext ctx) {
        return new Negate(ctx, ctx.start, (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitTrue(CoolParser.TrueContext ctx) {
        return new Bool(ctx, ctx.TRUE().getSymbol());
    }

    @Override
    public ASTNode visitSimple_dispatch(CoolParser.Simple_dispatchContext ctx) {
        List<Expression> args = new LinkedList<>();

        for (var arg : ctx.args) {
            args.add((Expression) visit(arg));
        }

        return new SimpleDispatch(ctx, ctx.start, new Id(ctx, ctx.method_name), args);
    }

    @Override
    public ASTNode visitBlock(CoolParser.BlockContext ctx) {
        List<Expression> expressions = new LinkedList<>();

        for (var expr : ctx.e) {
            expressions.add((Expression) visit(expr));
        }

        return new Block(ctx, ctx.start, expressions);
    }

    @Override
    public ASTNode visitLocal_(CoolParser.Local_Context ctx){
        return new Local(ctx, ctx.start, new Type(ctx, ctx.type), new Id(ctx, ctx.name),
                ctx.init != null? (Expression) visit(ctx.init):null);
    }

    @Override
    public ASTNode visitLet(CoolParser.LetContext ctx) {
        var noLetVariables = ctx.local_().size();
        if (noLetVariables == 1)
            return new Let(ctx, ctx.start, (Local) visit(ctx.local_(0)), (Expression) visit(ctx.body));

        Let inLet = null;

        for (int i = noLetVariables - 1; i >= 0; i--) {
            var localVariable = (Local) visit(ctx.local_(i));

            if (i == 0) {
                inLet = new Let(ctx, ctx.start, localVariable, inLet);
                continue;
            }

            if (i == noLetVariables - 1)
                inLet = new Let(ctx, ctx.local_(i).start, localVariable, (Expression) visit(ctx.body));
            else
                inLet = new Let(ctx, ctx.local_(i).start, localVariable, inLet);
        }

        return inLet;
    }

    @Override
    public ASTNode visitCase(CoolParser.CaseContext ctx) {
        List<CaseBranch> caseBranches = new LinkedList<>();

        for (var branch : ctx.branches) {
            caseBranches.add((CaseBranch) visit(branch));
        }

        return new Case(ctx, ctx.start, (Expression) visit(ctx.cond), caseBranches);
    }

    @Override
    public ASTNode visitCase_branch(CoolParser.Case_branchContext ctx) {
        return new CaseBranch(ctx, ctx.start, new Id(ctx, ctx.name), new Type(ctx, ctx.type), (Expression) visit(ctx.body));
    }
}
