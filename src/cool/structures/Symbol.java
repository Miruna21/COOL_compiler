package cool.structures;

public abstract class Symbol {
    protected String name;
    protected int offset;
    
    public Symbol(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
