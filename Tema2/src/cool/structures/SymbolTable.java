package cool.structures;

import java.io.File;

import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;

public class SymbolTable {
    public static Scope globals;
    
    private static boolean semanticErrors;
    
    public static void defineBasicClasses() {
        globals = new DefaultScope(null);
        semanticErrors = false;
        
        // Populate global scope with Basic classes and Basic methods
        globals.add(ClassSymbol.OBJECT);
        ClassSymbol.OBJECT.setParent(globals);
        ClassSymbol.OBJECT.addChild(ClassSymbol.IO);
        ClassSymbol.OBJECT.addChild(ClassSymbol.INT);
        ClassSymbol.OBJECT.addChild(ClassSymbol.STRING);
        ClassSymbol.OBJECT.addChild(ClassSymbol.BOOL);

        globals.add(ClassSymbol.INT);
        ClassSymbol.INT.setParent(ClassSymbol.OBJECT);

        globals.add(ClassSymbol.BOOL);
        ClassSymbol.BOOL.setParent(ClassSymbol.OBJECT);

        globals.add(ClassSymbol.STRING);
        ClassSymbol.STRING.setParent(ClassSymbol.OBJECT);

        globals.add(ClassSymbol.IO);
        ClassSymbol.IO.setParent(ClassSymbol.OBJECT);

        addObjectMethods();
        addStringMethods();
        addIOMethods();
    }

    private static void addObjectMethods() {
        var method1 = new MethodSymbol(ClassSymbol.OBJECT, "abort");
        method1.setType(ClassSymbol.OBJECT);
        ClassSymbol.OBJECT.add(method1);

        var method2 = new MethodSymbol(ClassSymbol.OBJECT, "type_name");
        method2.setType(ClassSymbol.STRING);
        ClassSymbol.OBJECT.add(method2);

        var method3 = new MethodSymbol(ClassSymbol.OBJECT, "copy");
        method3.setType(globals.lookupTypes("SELF_TYPE", ClassSymbol.OBJECT));
        ClassSymbol.OBJECT.add(method3);
    }

    private static void addStringMethods() {
        var method1 = new MethodSymbol(ClassSymbol.STRING, "length");
        method1.setType(ClassSymbol.INT);
        ClassSymbol.STRING.add(method1);

        var method2 = new MethodSymbol(ClassSymbol.STRING, "concat");
        method2.setType(ClassSymbol.STRING);
        var m2_formal1 = new IdSymbol("s");
        m2_formal1.setType(ClassSymbol.STRING);
        method2.add(m2_formal1);
        ClassSymbol.STRING.add(method2);

        var method3 = new MethodSymbol(ClassSymbol.STRING, "substr");
        method3.setType(ClassSymbol.STRING);
        var m3_formal1 = new IdSymbol("i");
        m3_formal1.setType(ClassSymbol.INT);
        method3.add(m3_formal1);
        var m3_formal2 = new IdSymbol("l");
        m3_formal2.setType(ClassSymbol.INT);
        method3.add(m3_formal2);
        ClassSymbol.STRING.add(method3);
    }

    private static void addIOMethods() {
        var method1 = new MethodSymbol(ClassSymbol.IO, "out_string");
        method1.setType(globals.lookupTypes("SELF_TYPE", ClassSymbol.IO));
        var m1_formal1 = new IdSymbol("x");
        m1_formal1.setType(ClassSymbol.STRING);
        method1.add(m1_formal1);
        ClassSymbol.IO.add(method1);

        var method2 = new MethodSymbol(ClassSymbol.IO, "out_int");
        method2.setType(globals.lookupTypes("SELF_TYPE", ClassSymbol.IO));
        var m2_formal1 = new IdSymbol("x");
        m2_formal1.setType(ClassSymbol.INT);
        method2.add(m2_formal1);
        ClassSymbol.IO.add(method2);

        var method3 = new MethodSymbol(ClassSymbol.IO, "in_string");
        method3.setType(ClassSymbol.STRING);
        ClassSymbol.IO.add(method3);

        var method4 = new MethodSymbol(ClassSymbol.IO, "in_int");
        method4.setType(ClassSymbol.INT);
        ClassSymbol.IO.add(method4);
    }
    
    /**
     * Displays a semantic error message.
     * 
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();
        
        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static void error(String str) {
        String message = "Semantic error: " + str;
        
        System.err.println(message);
        
        semanticErrors = true;
    }
    
    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }
}
