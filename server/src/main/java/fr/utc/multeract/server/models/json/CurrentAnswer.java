package fr.utc.multeract.server.models.json;

public class CurrentAnswer {
    private Object value;
    private long at;

    public void setValue(Object value) {
        this.at = System.currentTimeMillis();
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public long at() {
        return at;
    }

    public void setAt(long at) {
        this.at = at;
    }
}
