package util;

public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(x: " + x + " y: " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Position p) {
            return (x == p.x && y == p.y);
        }
        else throw new IllegalArgumentException("Argument must be of class Position");
    }
}
