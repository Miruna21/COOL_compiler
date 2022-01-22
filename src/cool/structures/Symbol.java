package cool.structures;

public abstract class Symbol {
    protected String name;
    protected int index;
    protected int group;  // 0 attribute, 1 formal, 2 local, 3 case, 4 method, 5 type

    final static int ATTRIBUTE_GROUP = 0;
    final static int FORMAL_GROUP = 1;
    final static int LOCAL_GROUP = 2;
    final static int CASE_GROUP = 3;
    
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
