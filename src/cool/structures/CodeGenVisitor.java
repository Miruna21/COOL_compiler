package cool.structures;

import cool.ast.ASTVisitor;
import cool.ast.nodes.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class CodeGenVisitor implements ASTVisitor<ST> {
    //for debugging
    //    static STGroupFile templates = new STGroupFile("src/cool/structures/cgen.stg");
    //for checker script
    static STGroupFile templates = new STGroupFile("cool/structures/cgen.stg");
    ST dataSection;
    ST textSection;
    ST allObjects;
    ST allDispTables;

    ST intConstants;
    ST strConstants;
    ST boolConstants;
    ST initMethods;

    Map<Integer, String> int_constants = new LinkedHashMap<>();
    Map<String, String> str_constants = new LinkedHashMap<>();
    Map<String, String> bool_constants = new LinkedHashMap<>();
    Integer int_literal_index = 0;
    Integer str_literal_index = 0;
    Integer bool_literal_index = 0;

    Integer classIndex = 0;

    int PROTOTYPE_LENGTH = 12;

    private void dfs(ClassSymbol node, int offset) {
        for (IdSymbol attr : node.getAttributes().values()) {
            if (attr.getName().equals("self")) {
                continue;
            }
            TypeSymbol type = attr.getType();
            type.setOffset(offset + PROTOTYPE_LENGTH);
            offset += 4;
            attr.setOffset(offset);
        }

        // reprezentarea claselor si a tabelurilor de metode
        allObjects.add("e", createClassST(node));

        ST methods = createMethods(node);

        ST dispTable = templates.getInstanceOf("dispTab");
        dispTable.add("class", node.getName()).add("methods", methods);

        allDispTables.add("e", dispTable);

        for (ClassSymbol child : node.getChildren()) {
            child.setClassIndex(classIndex);
            classIndex++;
            dfs(child, offset);
        }
    }

    private void generateClassesRepresentations() {
        ClassSymbol root = ClassSymbol.OBJECT;
        root.setClassIndex(classIndex);

        classIndex++;
        dfs(root, 0);
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

        get_or_generate_str("Object");
        get_or_generate_str("IO");
        get_or_generate_str("Int");
        get_or_generate_str("String");
        get_or_generate_str("Bool");
    }

    private void initCodeSections() {
        allObjects = templates.getInstanceOf("sequence");
        allDispTables = templates.getInstanceOf("sequence");

        intConstants = templates.getInstanceOf("sequence");
        strConstants = templates.getInstanceOf("sequence");
        boolConstants = templates.getInstanceOf("sequence");

        initMethods = templates.getInstanceOf("sequence");

        generateClassesRepresentations();
        generateDefaultConstants();
        generateBaseInitMethods();
    }

    @Override
    public ST visit(Program program) {
        dataSection = templates.getInstanceOf("sequenceSpaced");
        textSection = templates.getInstanceOf("sequenceSpaced");
        initCodeSections();

        for (ASTNode e : program.getClasses())
            textSection.add("e", e.accept(this));

        dataSection.add("e", allObjects);
        dataSection.add("e", allDispTables);
        dataSection.add("e", strConstants).add("e", intConstants).add("e", boolConstants);
        textSection.add("e", initMethods);

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

        classProt.add("class", classSymbol.getName()).add("index", classSymbol.getClassIndex())
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

        generate_str_constant(name);

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
        ST st = null;
        if (attribute.getInit() != null)
            st = attribute.getInit().accept(this);

        return st;
    }

    @Override
    public ST visit(Method method) {
        method.getName().accept(this);
        method.getBody().accept(this);
        return null;
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
        return null;
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
        st.add("index", int_literal_index).add("class_index", ClassSymbol.INT.getClassIndex()).add("value", literal);

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
        st.add("index", str_literal_index).add("class_index", ClassSymbol.STRING.getClassIndex())
                .add("dim", dim).add("len_obj", len_constant).add("str", "\"" + literal + "\"");

        str_literal_index++;
        strConstants.add("e", st);
    }

    void generate_bool_constant(String literal) {
        bool_constants.put(literal, "bool_const" + bool_literal_index);

        int value = 0;

        if (literal.compareTo("true") == 0)
            value = 1;

        ST st = templates.getInstanceOf("bool_const");
        st.add("index", bool_literal_index).add("class_index", ClassSymbol.BOOL.getClassIndex()).add("value", value);
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
        return null;
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
        return null;
    }

    @Override
    public ST visit(ExplicitDispatch explicitDispatch) {
        return null;
    }

    @Override
    public ST visit(Assign assign) {
        return null;
    }
}
