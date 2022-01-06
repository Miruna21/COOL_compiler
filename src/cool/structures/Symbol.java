package cool.structures;

public abstract class Symbol {
    protected String name;
    protected int index;
    protected int group;  // 0 type, 1 attribute, 2 method, 3 formal, 4 local, 5 case

    final static int TYPE_GROUP = 0;
    final static int ATTRIBUTE_GROUP = 1;
    final static int METHOD_GROUP = 2;
    final static int FORMAL_GROUP = 3;
    final static int LOCAL_GROUP = 4;
    final static int CASE_GROUP = 5;
    
    public Symbol(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
