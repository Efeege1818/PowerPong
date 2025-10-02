package de.hhn.it.devtools.apis.examples.fourconnect;

public class ToxicField {
    private final int row;
    private final int column;
    private int age = 0;

    public ToxicField(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public void incrementAge() {
        
    }

    public boolean isExpired() {
        return false;
    }

    
    public int getRow() { return row; }
    public int getColumn() { return column; }
    public int getAge() { return age; }
}
