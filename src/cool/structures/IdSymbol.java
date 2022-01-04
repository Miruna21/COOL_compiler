package cool.structures;

public class IdSymbol extends Symbol {
    protected TypeSymbol type;
    protected int offset;

    public IdSymbol(String name) {
        super(name);
    }

    public TypeSymbol getType() {
        return type;
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
