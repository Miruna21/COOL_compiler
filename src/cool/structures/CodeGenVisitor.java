package cool.structures;

import cool.ast.ASTVisitor;
import cool.ast.nodes.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

public class CodeGenVisitor implements ASTVisitor<ST> {
    //for debugging
    //    static STGroupFile templates = new STGroupFile("src/cool/structures/cgen.stg");
    //for checker script
    static STGroupFile templates = new STGroupFile("cool/structures/cgen.stg");

    ST dataSection;
    ST textSection;
    ST allObjects;
    ST allTables;

    Integer classIndex = 0;

    private void dfs(ClassSymbol node) {
        for (ClassSymbol child : node.getChildren()) {
            child.setClassIndex(classIndex);
            classIndex++;
            dfs(child);
        }
    }

    private void generateClassesRepresentations() {
        ClassSymbol root = ClassSymbol.OBJECT;
        root.setClassIndex(classIndex);

        classIndex++;
        dfs(root);
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

        initCodeSections(); // code default in stg pentru Object, IO, Int, String, Bool

        dataSection = dataSection.add("e", allObjects);
        dataSection = dataSection.add("e", allTables);

        for (ASTNode e : program.getClasses())
            textSection.add("e", e.accept(this));

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
//            TypeSymbol type = attr.getType();
            atrributes.add("e", attr.name);
            // aici am nevoie de miru's map
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
            methods.add("e", classSymbol.name + "." + methodName);
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
        return null;
    }

    @Override
    public ST visit(Method method) {
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

    @Override
    public ST visit(Int int_) {
        return null;
    }

    @Override
    public ST visit(String_ string) {
        return null;
    }

    @Override
    public ST visit(Bool bool) {
        return null;
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
