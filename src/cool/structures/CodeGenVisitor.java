package cool.structures;

import cool.ast.ASTVisitor;
import cool.ast.nodes.*;
import cool.compiler.Compiler;
import cool.lexer.CoolLexer;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.util.*;

public class CodeGenVisitor implements ASTVisitor<ST> {
    //for debugging
    //    static STGroupFile templates = new STGroupFile("src/cool/structures/cgen.stg");
    //for checker script
    static STGroupFile templates = new STGroupFile("cool/structures/cgen.stg");
    ST dataSection;
    ST textSection;
    ST tags;
    ST allObjects;
    ST allDispTables;
    ST nameTable;
    ST objTable;

    ST intConstants;
    ST strConstants;
    ST boolConstants;
    ST initMethods;

    List<ClassSymbol> dfsClasses = new ArrayList<>();
    Map<Integer, String> int_constants = new LinkedHashMap<>();
    Map<String, String> str_constants = new LinkedHashMap<>();
    Map<String, String> bool_constants = new LinkedHashMap<>();
    int int_literal_index = 0;
    int str_literal_index = 0;
    int bool_literal_index = 0;

    int classIndex = 0;
    int dispatchIndex = 0;
    int ifIndex = 0;
    int isVoidIndex = 0;
    int notIndex = 0;
    int equalityIndex = 0;

    final static int PROTOTYPE_LENGTH = 12;
    final static int ACTIVATION_RECORD_LENGTH = 12;

    int letIndex = 0;

    private void dfs(ClassSymbol node, int attrIndex, int methodIndex) {
        for (IdSymbol attr : node.getAttributes().values()) {
            if (attr.getName().equals("self")) {
                attr.setGroup(Symbol.ATTRIBUTE_GROUP);
                continue;
            }
            TypeSymbol type = attr.getType();
            type.setOffset(attrIndex * 4 + PROTOTYPE_LENGTH);
            attr.setIndex(attrIndex);
            attr.setGroup(Symbol.ATTRIBUTE_GROUP);
            attrIndex++;
        }
        node.setTotal_attributes(attrIndex);

        for (MethodSymbol method : node.getMethods().values()) {
//            method.setIndex(methodIndex);
            method.setGroup(Symbol.METHOD_GROUP);
            methodIndex++;
        }

        dfsClasses.add(node);

        for (ClassSymbol child : node.getChildren()) {
            child.setIndex(classIndex);
            child.setGroup(Symbol.TYPE_GROUP);

            classIndex++;
            dfs(child, attrIndex, methodIndex);
        }
    }

    private void generateClassRepresentation(ClassSymbol class_) {
        // reprezentarea claselor si a tabelurilor de metode
        allObjects.add("e", createClassST(class_));

        ST methods = createMethods(class_, new LinkedHashMap<>(), true);

        ST dispTable = templates.getInstanceOf("dispTab");
        dispTable.add("class", class_.getName()).add("methods", methods);

        allDispTables.add("e", dispTable);

        ST nameST = templates.getInstanceOf("word");
        nameST.add("value", get_or_generate_str(class_.getName()));
        nameTable.add("name_constant", nameST);

        ST prot_initST = templates.getInstanceOf("sequence");
        ST protST = templates.getInstanceOf("word");
        protST.add("value", class_.getName() + "_protObj");
        ST initST = templates.getInstanceOf("word");
        initST.add("value", class_.getName() + "_init");

        prot_initST.add("e", protST).add("e", initST);
        objTable.add("prot_init", prot_initST);
    }

    private void generateClassesRepresentations() {
        ClassSymbol root = ClassSymbol.OBJECT;
        root.setIndex(classIndex);
        root.setGroup(Symbol.TYPE_GROUP);

        classIndex++;
        dfs(root, 0, 0);

        for(ClassSymbol class_ : dfsClasses)
            generateClassRepresentation(class_);
    }

    private ST generateBaseInitMethod(ClassSymbol classSymbol) {
        ST init_method = templates.getInstanceOf("initMethod");

        if (classSymbol == ClassSymbol.OBJECT)
            init_method.add("class", classSymbol.getName());
        else {
            ClassSymbol parent = (ClassSymbol) classSymbol.getParent();
            init_method.add("class", classSymbol.getName()).add("parent_init_method", parent.getName() + "_init");
        }

        return init_method;
    }

    private void generateBaseInitMethods() {
        initMethods.add("e", generateBaseInitMethod(ClassSymbol.OBJECT))
                .add("e", generateBaseInitMethod(ClassSymbol.IO))
                .add("e", generateBaseInitMethod(ClassSymbol.INT))
                .add("e", generateBaseInitMethod(ClassSymbol.STRING))
                .add("e", generateBaseInitMethod(ClassSymbol.BOOL));
    }

    private void generateDefaultConstants() {
        get_or_generate_str("");
        get_or_generate_int(0);
        get_or_generate_bool("false");
        get_or_generate_bool("true");
    }

    private void generateTags() {
        tags.add("int_index", ClassSymbol.INT.getIndex())
                .add("string_index", ClassSymbol.STRING.getIndex())
                .add("bool_index", ClassSymbol.BOOL.getIndex());
    }

    private void initCodeSections() {
        tags = templates.getInstanceOf("tags");
        allObjects = templates.getInstanceOf("sequence");
        allDispTables = templates.getInstanceOf("sequence");
        nameTable = templates.getInstanceOf("nameTab");
        objTable = templates.getInstanceOf("objTab");

        intConstants = templates.getInstanceOf("sequence");
        strConstants = templates.getInstanceOf("sequence");
        boolConstants = templates.getInstanceOf("sequence");

        initMethods = templates.getInstanceOf("sequence");

        generateClassesRepresentations();
        generateTags();
        generateDefaultConstants();
        generateBaseInitMethods();
    }

    @Override
    public ST visit(Program program) {
        dataSection = templates.getInstanceOf("sequenceSpaced");
        textSection = templates.getInstanceOf("sequenceSpaced");
        initCodeSections();

        ST methodDefs = templates.getInstanceOf("sequence");
        for (ASTNode e : program.getClasses())
            methodDefs.add("e", e.accept(this));

        dataSection.add("e", tags);
        dataSection.add("e", strConstants).add("e", intConstants).add("e", boolConstants);
        dataSection.add("e", nameTable);
        dataSection.add("e", objTable);
        dataSection.add("e", allObjects);
        dataSection.add("e", allDispTables);
        textSection.add("e", initMethods);
        textSection.add("e", methodDefs);

        var programST = templates.getInstanceOf("program");
        programST.add("data", dataSection);
        programST.add("text", textSection);

        return programST;
    }

    public ST createAttributes(ClassSymbol classSymbol) {
        ST attributes;
        if (classSymbol.getParent() == SymbolTable.globals) {
            attributes = templates.getInstanceOf("sequence");
        } else {
            attributes = createAttributes((ClassSymbol) classSymbol.getParent());
        }

        for (IdSymbol attr : classSymbol.getAttributes().values()) {
            if (attr.getName().equals("self")) {
                continue;
            }
            TypeSymbol type = attr.getType();

            ST attrST = templates.getInstanceOf("word");

            String value;
            if (type == ClassSymbol.INT) {
                value = get_or_generate_int(0);
            } else if (type == ClassSymbol.STRING) {
                value = get_or_generate_str("");
            } else if (type == ClassSymbol.BOOL) {
                value = get_or_generate_bool("false");
            } else {
                value = "0";
            }
            attrST.add("value", value);
            attributes.add("e", attrST);
        }

        return attributes;
    }

    public ST createClassST(ClassSymbol classSymbol) {
        ST attributes = createAttributes(classSymbol);
        ST classProt =  templates.getInstanceOf("protObj");
        int dim = 3 + classSymbol.getTotal_attributes();

        classProt.add("class", classSymbol.getName()).add("index", classSymbol.getIndex())
                .add("disp_ptr", classSymbol.getName() + "_dispTab");

        // atribute fictive pentru clasele predefinite
        if (classSymbol.getTotal_attributes() != 0)
            classProt.add("attributes", attributes).add("dim", dim);
        else if (classSymbol == ClassSymbol.INT || classSymbol == ClassSymbol.BOOL) {
            ST pseudoAttribute = templates.getInstanceOf("word");
            pseudoAttribute.add("value", 0);
            classProt.add("attributes", pseudoAttribute).add("dim", dim + 1);
        } else if (classSymbol == ClassSymbol.STRING) {
            ST pseudoAttribute = templates.getInstanceOf("str_attribute");
            pseudoAttribute.add("len", get_or_generate_int(0)).add("str", "\"\"");
            classProt.add("attributes", pseudoAttribute).add("dim", dim + 2);
        } else {
            classProt.add("dim", dim);
        }

        return classProt;
    }

    public ST createMethods(ClassSymbol classSymbol, LinkedHashMap<String, MethodSymbol> methodsNames, boolean isFirst) {
        if (classSymbol.getParent() != SymbolTable.globals) {
            createMethods((ClassSymbol) classSymbol.getParent(), methodsNames, false);
        }

        for (MethodSymbol method : classSymbol.getMethods().values()) {
            String methodName = method.getName();
            methodsNames.put(methodName, method);
        }

        if (isFirst) {
            ST methods = templates.getInstanceOf("sequence");
            int index = 0;
            for(String methodName : methodsNames.keySet()) {
                MethodSymbol method = methodsNames.get(methodName);
                method.setIndex(index);
                index ++;

                String className = ((ClassSymbol)method.getParent()).getName();
                ST methodST = templates.getInstanceOf("word");
                methodST.add("value", className + "." + methodName);
                methods.add("e", methodST);
            }
            return methods;
        }

        return null;
    }

    @Override
    public ST visit(Class_ class_) {
        ClassSymbol currentClass = (ClassSymbol) class_.getType().getSymbol();
        String name = currentClass.getName();

        ST attributesST = templates.getInstanceOf("sequence");
        ST methodsST = templates.getInstanceOf("sequence");
        ST init_method = templates.getInstanceOf("initMethod");

        ClassSymbol parent = (ClassSymbol) currentClass.getParent();
        init_method.add("class", name).add("parent_init_method", parent.getName() + "_init");

        for (var feature : class_.getFeatures())
            if (feature instanceof Attribute)
                attributesST.add("e", feature.accept(this));
            else
                methodsST.add("e", feature.accept(this));

        if (currentClass.getAttributes().size() > 1)  // si self e atribut, teoretic
            init_method.add("attr_init_exprs", attributesST);

        initMethods.add("e", init_method);

        return methodsST;
    }

    @Override
    public ST visit(Attribute attribute) {
        Symbol symbol = attribute.getName().getSymbol();
        ST st = templates.getInstanceOf("sequence");

        if (attribute.getInit() != null) {
            st.add("e", attribute.getInit().accept(this));

            ST initST = templates.getInstanceOf("attr_set");
            initST.add("offset", symbol.getIndex() * 4 + PROTOTYPE_LENGTH);

            st.add("e", initST);
        }

        return st;
    }

    @Override
    public ST visit(Method method) {
        MethodSymbol methodSymbol = (MethodSymbol) method.getName().getSymbol();
        ClassSymbol currentClass = (ClassSymbol) methodSymbol.getParent();
        var st = templates.getInstanceOf("methodDef");
        var formals = method.getFormals();
        int n = formals.size();

        for (int i = 0; i < n; i++) {
            Symbol formalSymbol = formals.get(i).getArgName().getSymbol();
            formalSymbol.setIndex(i); // 12 + i * 4
            formalSymbol.setGroup(Symbol.FORMAL_GROUP);
        }

        st.add("class", currentClass.getName())
                .add("method", methodSymbol.getName())
                .add("body", method.getBody().accept(this))
                .add("offset", 4 * n + 12);

        return st;
    }

    @Override
    public ST visit(Type type) {
        return null;
    }

    @Override
    public ST visit(Formal formal) {
        return null;
    }

    @Override
    public ST visit(Local local) {
        return null;
    }

    @Override
    public ST visit(Expression expression) {
        return null;
    }

    @Override
    public ST visit(Id id) {
        Symbol symbol = id.getSymbol();
        ST st = null;

        if (symbol.getGroup() == Symbol.ATTRIBUTE_GROUP) {
            if (symbol.getName().compareTo("self") == 0) {
                st = templates.getInstanceOf("self_get");
            } else {
                st = templates.getInstanceOf("attr_get");
                st.add("offset", symbol.getIndex() * 4 + PROTOTYPE_LENGTH);
            }
        } else if (symbol.getGroup() == Symbol.FORMAL_GROUP) {
            st = templates.getInstanceOf("formal_get");
            st.add("offset", symbol.getIndex() * 4 + ACTIVATION_RECORD_LENGTH);
        } else if (symbol.getGroup() == Symbol.LOCAL_GROUP) {
            st = templates.getInstanceOf("formal_get");
            st.add("offset", -symbol.getIndex() * 4);
        }

        return st;
    }

    String get_or_generate_int(Integer literal) {
        if (!int_constants.containsKey(literal)) {
            generate_int_constant(literal);
        }
        return int_constants.get(literal);
    }

    String get_or_generate_str(String literal) {

        if (!str_constants.containsKey(literal)) {
            generate_str_constant(literal);
        }
        return str_constants.get(literal);
    }

    String get_or_generate_bool(String literal) {
        if (!bool_constants.containsKey(literal)) {
            generate_bool_constant(literal);
        }
        return bool_constants.get(literal);
    }

    void generate_int_constant(Integer literal) {
        int_constants.put(literal, "int_const" + int_literal_index);

        ST st = templates.getInstanceOf("int_const");
        st.add("index", int_literal_index).
                add("class_index", ClassSymbol.INT.getIndex())
                .add("value", literal);

        int_literal_index++;
        intConstants.add("e", st);
    }

    void generate_str_constant(String literal) {
        str_constants.put(literal, "str_const" + str_literal_index);

        ST st = templates.getInstanceOf("str_const");
        int len = literal.length();
        literal = literal.replace("\n", "\\n");
        int dim = (int) (4 + Math.ceil((len + 1)/ 4.0f));

        if (!int_constants.containsKey(len))
            generate_int_constant(len);

        String len_constant = int_constants.get(len);
        st.add("index", str_literal_index)
                .add("class_index", ClassSymbol.STRING.getIndex())
                .add("dim", dim).add("len_obj", len_constant)
                .add("str", "\"" + literal + "\"");

        str_literal_index++;
        strConstants.add("e", st);
    }

    void generate_bool_constant(String literal) {
        bool_constants.put(literal, "bool_const" + bool_literal_index);

        int value = 0;

        if (literal.compareTo("true") == 0)
            value = 1;

        ST st = templates.getInstanceOf("bool_const");
        st.add("index", bool_literal_index)
                .add("class_index", ClassSymbol.BOOL.getIndex())
                .add("value", value);
        bool_literal_index++;

        boolConstants.add("e", st);
    }

    @Override
    public ST visit(Int int_) {
        Integer literal = Integer.parseInt(int_.getToken().getText());

        String int_constant = get_or_generate_int(literal);
        ST st = templates.getInstanceOf("literal");
        st.add("value", int_constant);

        return st;
    }

    @Override
    public ST visit(String_ string) {
        String literal = string.getToken().getText();

        String str_constant = get_or_generate_str(literal);
        ST st = templates.getInstanceOf("literal");
        st.add("value", str_constant);

        return st;
    }

    @Override
    public ST visit(Bool bool) {
        String literal = bool.getToken().getText();

        String bool_constant = get_or_generate_bool(literal);
        ST st = templates.getInstanceOf("literal");
        st.add("value", bool_constant);

        return st;
    }

    @Override
    public ST visit(UnaryMinus unaryMinus) {
        return null;
    }

    @Override
    public ST visit(Not not) {
        ST st = templates.getInstanceOf("not");
        String false_const = get_or_generate_bool("false");
        String true_const = get_or_generate_bool("true");

        st.add("e", not.getExpr().accept(this))
                .add("true_const", true_const)
                .add("false_const", false_const)
                .add("index", notIndex);
        notIndex++;

        return st;
    }

    @Override
    public ST visit(Negate negate) {
        ST negSt = templates.getInstanceOf("neg");
        negSt.add("init", negate.getExpr().accept(this));

        return negSt;
    }

    @Override
    public ST visit(IsVoid isVoid) {
        ST isVoidST = templates.getInstanceOf("isvoid");

        ST expr = isVoid.getExpr().accept(this);
        String falseBool = get_or_generate_bool("false");
        String trueBool = get_or_generate_bool("true");

        isVoidST.add("index", isVoidIndex++).add("e", expr).add("false_const", falseBool)
                .add("true_const", trueBool);

        return isVoidST;
    }

    @Override
    public ST visit(New new_) {
        Symbol symbol = new_.getType().getSymbol();
        ST st;

        if (symbol instanceof SelfTypeSymbol)
            st = templates.getInstanceOf("new_self_type");
        else {
            st = templates.getInstanceOf("new_class");
            st.add("class", symbol.getName());
        }

        return st;
    }

    @Override
    public ST visit(Arithmetic arithmetic) {
        ST expr = templates.getInstanceOf("arithmetic");
        expr.add("init1", arithmetic.getLeftExpr().accept(this))
                .add("init2", arithmetic.getRightExpr().accept(this));
        ST op;
        if (arithmetic.getToken().getType() == CoolLexer.PLUS) {
            op = templates.getInstanceOf("plus");
        } else if (arithmetic.getToken().getType() == CoolLexer.MINUS) {
            op = templates.getInstanceOf("minus");
        } else if (arithmetic.getToken().getType() == CoolLexer.MULT) {
            op = templates.getInstanceOf("mult");
        } else if (arithmetic.getToken().getType() == CoolLexer.DIV) {
            op = templates.getInstanceOf("div");
        } else {
            return  null;
        }

        expr.add("op", op);

        return expr;
    }

    @Override
    public ST visit(Relational relational) {
        ST st = null;

        if (relational.getToken().getType() == CoolLexer.EQUAL) {
            st = templates.getInstanceOf("equality");

            String false_const = get_or_generate_bool("false");
            String true_const = get_or_generate_bool("true");

            st.add("e1", relational.getLeftExpr().accept(this))
                    .add("e2", relational.getRightExpr().accept(this))
                    .add("true_const", true_const)
                    .add("false_const", false_const)
                    .add("index", equalityIndex);
            equalityIndex++;
        }

        return st;
    }

    @Override
    public ST visit(Case case_) {
        return null;
    }

    @Override
    public ST visit(CaseBranch caseBranch) {
        return null;
    }

    @Override
    public ST visit(Let let) {
        letIndex += 1;
        ST letST = templates.getInstanceOf("let");
        ST initExprSt;

        Local localVar = let.getLocal();
        IdSymbol symbol = (IdSymbol)localVar.getName().getSymbol();
        symbol.setGroup(Symbol.LOCAL_GROUP);
        symbol.setIndex(letIndex);

        if (localVar.getInit() == null) {
            ST initExpr = templates.getInstanceOf("init_constant");
            String value;

            TypeSymbol type = ((IdSymbol)localVar.getName().getSymbol()).getType();
            if (type == ClassSymbol.INT) {
                value = get_or_generate_int(0);
            } else if (type == ClassSymbol.STRING) {
                value = get_or_generate_str("");
            } else if (type == ClassSymbol.BOOL) {
                value = get_or_generate_bool("false");
            } else {
                initExpr = templates.getInstanceOf("init_address");
                value = "0";
            }

            initExpr.add("value", value);
            initExprSt = templates.getInstanceOf("sequence");
            initExprSt.add("e", initExpr);
        } else {
            initExprSt = localVar.getInit().accept(this);
        }

        ST body = let.getBody().accept(this);

        letST.add("offset", letIndex * 4).add("init_expr", initExprSt).add("body", body);

        letIndex -= 1;
        return letST;
    }

    @Override
    public ST visit(Block block) {
        ST st = templates.getInstanceOf("sequence");

        for(var expr : block.getExpressions())
            st.add("e", expr.accept(this));

        return st;
    }

    @Override
    public ST visit(While while_) {
        return null;
    }

    @Override
    public ST visit(If if_) {
        var st = templates.getInstanceOf("if");
        st.add("e1", if_.getCond().accept(this))
                .add("e2", if_.getThenBranch().accept(this))
                .add("e3",  if_.getElseBranch().accept(this))
                .add("index", ifIndex);

        ifIndex++;
        return st;
    }

    @Override
    public ST visit(Dispatch dispatch) {
        return null;
    }

    @Override
    public ST visit(AtDispatch atDispatch) {
        Symbol symbol = atDispatch.getMethodName().getSymbol();
        ST st = templates.getInstanceOf("at_dispatch");

        String file_name = get_or_generate_str(getFileName(atDispatch.getContext()));
        int line_number = atDispatch.getToken().getLine();

        int n = atDispatch.getArgs().size() - 1;
        ST argsST = templates.getInstanceOf("sequence");

        for (int i = n; i >= 0; i--) {
            ST argST = templates.getInstanceOf("arg");
            argST.add("e", atDispatch.getArgs().get(i).accept(this));

            argsST.add("e", argST);
        }

        st.add("args", argsST).add("e", atDispatch.getExpr().accept(this))
                .add("index", dispatchIndex).add("class", atDispatch.getParent().getSymbol().getName()).add("file_name", file_name).add("line_number", line_number)
                .add("method_offset", symbol.getIndex() * 4);

        dispatchIndex++;
        return st;
    }

    @Override
    public ST visit(SimpleDispatch simpleDispatch) {
        Symbol symbol = simpleDispatch.getMethodName().getSymbol();
        ST st = templates.getInstanceOf("simple_dispatch");

        String file_name = get_or_generate_str(getFileName(simpleDispatch.getContext()));
        int line_number = simpleDispatch.getToken().getLine();

        int n = simpleDispatch.getArgs().size() - 1;
        ST argsST = templates.getInstanceOf("sequence");

        for (int i = n; i >= 0; i--) {
            ST argST = templates.getInstanceOf("arg");
            argST.add("e", simpleDispatch.getArgs().get(i).accept(this));

            argsST.add("e", argST);
        }

        st.add("args", argsST).add("index", dispatchIndex).add("file_name", file_name)
                .add("line_number", line_number).add("method_offset", symbol.getIndex() * 4);

        dispatchIndex++;
        return st;
    }

    @Override
    public ST visit(ExplicitDispatch explicitDispatch) {
        Symbol symbol = explicitDispatch.getMethodName().getSymbol();
        ST st = templates.getInstanceOf("dispatch");

        String file_name = get_or_generate_str(getFileName(explicitDispatch.getContext()));
        int line_number = explicitDispatch.getToken().getLine();

        int n = explicitDispatch.getArgs().size() - 1;
        ST argsST = templates.getInstanceOf("sequence");

        for (int i = n; i >= 0; i--) {
            ST argST = templates.getInstanceOf("arg");
            argST.add("e", explicitDispatch.getArgs().get(i).accept(this));

            argsST.add("e", argST);
        }

        st.add("args", argsST).add("e", explicitDispatch.getExpr().accept(this))
                .add("index", dispatchIndex).add("file_name", file_name).add("line_number", line_number)
                .add("method_offset", symbol.getIndex() * 4);

        dispatchIndex++;
        return st;
    }

    @Override
    public ST visit(Assign assign) {
        Symbol symbol = assign.getName().getSymbol();
        ST st = templates.getInstanceOf("sequence");
        st.add("e", assign.getExpr().accept(this));

        if (symbol.getGroup() == Symbol.ATTRIBUTE_GROUP) {
            ST attr_set = templates.getInstanceOf("attr_set");
            attr_set.add("offset", symbol.getIndex() * 4 + PROTOTYPE_LENGTH);
            st.add("e", attr_set);
        } else if (symbol.getGroup() == Symbol.FORMAL_GROUP) {
            ST formal_set = templates.getInstanceOf("formal_set");
            formal_set.add("offset", symbol.getIndex() * 4 + ACTIVATION_RECORD_LENGTH);
            st.add("e", formal_set);
        } else if (symbol.getGroup() == Symbol.LOCAL_GROUP) {
            ST formal_set = templates.getInstanceOf("formal_set");
            formal_set.add("offset", -symbol.getIndex() * 4);
            st.add("e", formal_set);
        }

        return st;
    }

    private String getFileName(ParserRuleContext ctx) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();

        return new File(Compiler.fileNames.get(ctx)).getName();
    }
}
