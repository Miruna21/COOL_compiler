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
    ST allTables;

    ST int_constants_st = templates.getInstanceOf("sequence");
    ST str_constants_st = templates.getInstanceOf("sequence");
    ST bool_constants_st = templates.getInstanceOf("sequence");

    Map<Integer, String> int_constants = new LinkedHashMap<>();
    Map<String, String> str_constants = new LinkedHashMap<>();
    Map<String, String> bool_constants = new LinkedHashMap<>();
    Integer int_literal_index = 0;
    Integer str_literal_index = 0;
    Integer bool_literal_index = 0;

    Integer classIndex = 0;

    int PROTOTYPE_LENGTH = 12;

    private void dfs(ClassSymbol node, int offset) {
        for (IdSymbol attr : node.attributes.values()) {
            if (attr.name.equals("self")) {
                continue;
            }
            TypeSymbol type = attr.getType();
            type.setOffset(offset + PROTOTYPE_LENGTH);
            offset += 4;
        }

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

    private void initCodeSections() {
        generateClassesRepresentations();
    }

    @Override
    public ST visit(Program program) {
        dataSection = templates.getInstanceOf("sequenceSpaced");
        textSection = templates.getInstanceOf("sequenceSpaced");
        allObjects = templates.getInstanceOf("sequenceSpaced");
        allTables = templates.getInstanceOf("sequenceSpaced");

        initCodeSections();

        dataSection = dataSection.add("e", allObjects);
        dataSection = dataSection.add("e", allTables);

        for (ASTNode e : program.getClasses())
            textSection.add("e", e.accept(this));

        dataSection.add("e", str_constants_st).add("e", int_constants_st).add("e", bool_constants);

        var programST = templates.getInstanceOf("program");
        programST.add("data", dataSection);
        programST.add("text", textSection);

        return programST;
    }

    public ST createAttributes(ClassSymbol classSymbol) {
        ST atrributes;
        if (classSymbol.getParent() == SymbolTable.globals) {
            atrributes = templates.getInstanceOf("sequence");
        } else {
            atrributes = createAttributes((ClassSymbol) classSymbol.getParent());
        }

        for (IdSymbol attr : classSymbol.attributes.values()) {
            if (attr.name.equals("self")) {
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
                value = get_or_generate_bool("False");
            } else {
                value = "0";
            }
            attrST.add("value", value);
            atrributes.add("e", attrST);
        }

        return atrributes;
    }

    public ST createClassST(ClassSymbol classSymbol) {
        ST attributes = createAttributes(classSymbol);
        ST classProt =  templates.getInstanceOf("protObj");

        classProt.add("class", classSymbol.name).add("index", classSymbol.classIndex).add("dim", 12)
                .add("disp_ptr", classSymbol.name + "_disptable").add("attributes", attributes);

        return classProt;
    }

    public ST createMethods(ClassSymbol classSymbol) {
        ST methods;
        if (classSymbol.getParent() == SymbolTable.globals) {
            methods = templates.getInstanceOf("sequence");
        } else {
            methods = createMethods((ClassSymbol) classSymbol.getParent());
        }

        for (MethodSymbol method : classSymbol.methods.values()) {
            String methodName = method.getName();
            ST methodST = templates.getInstanceOf("word");
            methodST.add("value", classSymbol.name + "." + methodName);
            methods.add("e", methodST);
        }

        return methods;
    }

    @Override
    public ST visit(Class_ class_) {
        ClassSymbol currentClass = (ClassSymbol) class_.getType().getSymbol();
        allObjects.add("e", createClassST(currentClass));

        ST methods = createMethods(currentClass);

        ST dispTable = templates.getInstanceOf("dispTab");
        dispTable.add("class", currentClass.name).add("methods", methods);

        allTables.add("e", dispTable);

        return null;
    }

    @Override
    public ST visit(Attribute attribute) {

        attribute.getName().accept(this);
        attribute.getInit().accept(this);
        return null;
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
        int_constants.put(literal, "int_const" + int_literal_index++);

        ST st = templates.getInstanceOf("int_const");
        st.add("index", int_literal_index).add("value", literal);

        int_literal_index++;
        int_constants_st.add("e", st);
    }

    void generate_str_constant(String literal) {
        str_constants.put(literal, "str_const" + str_literal_index++);

        ST st = templates.getInstanceOf("str_const");
        int len = literal.length();
        int dim = (int) (3 + Math.ceil((len + 1)/ 4.0f));

        if (!int_constants.containsKey(len))
            generate_int_constant(len);

        String len_constant = int_constants.get(len);
        st.add("index", str_literal_index).add("dim", dim).add("len_obj", len_constant)
                .add("str", literal);

        str_literal_index++;
        str_constants_st.add("e", st);
    }

    void generate_bool_constant(String literal) {
        bool_constants.put(literal, "bool_const" + bool_literal_index);

        int value = 0;

        if (literal.compareTo("true") == 0)
            value = 1;

        ST st = templates.getInstanceOf("bool_const");
        st.add("index", bool_literal_index).add("value", value);
        bool_literal_index++;

        bool_constants_st.add("e", st);
    }

    @Override
    public ST visit(Int int_) {
        Integer literal = Integer.parseInt(int_.getToken().getText());

        if (!int_constants.containsKey(literal))
            generate_int_constant(literal);

        String int_constant = int_constants.get(literal);
        ST st = templates.getInstanceOf("literal");
        st.add("value", int_constant);

        return st;
    }

    @Override
    public ST visit(String_ string) {
        String literal = string.getToken().getText();

        if (!str_constants.containsKey(literal))
            generate_str_constant(literal);

        String str_constant = str_constants.get(literal);
        ST st = templates.getInstanceOf("literal");
        st.add("value", str_constant);

        return st;
    }

    @Override
    public ST visit(Bool bool) {
        String literal = bool.getToken().getText();

        if (!bool_constants.containsKey(literal))
            generate_bool_constant(literal);

        String bool_constant = bool_constants.get(literal);
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
