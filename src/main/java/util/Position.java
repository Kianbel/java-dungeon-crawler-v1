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

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public Position reverse() {
        return new Position(-x, -y);
    }

    public Position add(int x, int y) {
        return new Position(this.x+x, this.y+y);
    }

    public Position add(Position otherPos) {
        return new Position(x+otherPos.x, y+otherPos.y);
    }

    public double getDistanceTo(Position to) {
        int dx = to.x - x;
        int dy = to.y - y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public double getSquaredDistanceTo(Position to) {
        int dx = to.x - x;
        int dy = to.y - y;
        return dx*dx + dy*dy;
    }
}
