package cool.structures;

import cool.ast.ASTVisitor;
import cool.ast.nodes.*;
import cool.compiler.Compiler;
import cool.parser.CoolParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    final static int PROTOTYPE_LENGTH = 12;

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
            node.setTotal_attributes(attrIndex);
        }

        for (MethodSymbol method : node.getMethods().values()) {
            method.setIndex(methodIndex);
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

        ST methods = createMethods(class_);

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
        if (classSymbol.getTotal_attributes() != 0) {
            classProt.add("attributes", attributes).add("dim", dim);
        } else if (classSymbol == ClassSymbol.INT || classSymbol == ClassSymbol.BOOL) {
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

    public ST createMethods(ClassSymbol classSymbol) {
        ST methods;
        if (classSymbol.getParent() == SymbolTable.globals) {
            methods = templates.getInstanceOf("sequence");
        } else {
            methods = createMethods((ClassSymbol) classSymbol.getParent());
        }

        for (MethodSymbol method : classSymbol.getMethods().values()) {
            String methodName = method.getName();
            ST methodST = templates.getInstanceOf("word");
            methodST.add("value", classSymbol.getName() + "." + methodName);
            methods.add("e", methodST);
        }

        return methods;
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
        return null;
    }

    @Override
    public ST visit(Negate negate) {
        return null;
    }

    @Override
    public ST visit(IsVoid isVoid) {
        return null;
    }

    @Override
    public ST visit(New new_) {
        return null;
    }

    @Override
    public ST visit(Arithmetic arithmetic) {
        return null;
    }

    @Override
    public ST visit(Relational relational) {
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public ST visit(Dispatch dispatch) {
        return null;
    }

    @Override
    public ST visit(AtDispatch atDispatch) {
        return null;
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
        return null;
    }

    private String getFileName(ParserRuleContext ctx) {
        while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();

        return new File(Compiler.fileNames.get(ctx)).getName();
    }
}
