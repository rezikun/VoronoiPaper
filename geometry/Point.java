package geometry;

import org.jetbrains.annotations.Contract;

public class Point implements Cloneable, Comparable<Point> {

    public double x, y;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        if (o.getClass() == getClass()) {
            Point p = (Point) o;
            return p.x == x && p.y == y;
        }
        return false;
    }

    public int compareTo(Point p) {
        if (x == p.x) {
            return Double.compare(y, p.y);
        }
        return Double.compare(x, p.x);
    }

    @Override
    public Point clone() {
        try {
            Point p = (Point) super.clone();
            p.x = this.x;
            p.y = this.y;
            return p;
        } catch (CloneNotSupportedException e) {
            return new Point(this.x, this.y);
        }
    }

    public Point(double x0, double y0) {
        x = x0;
        y = y0;
    }

    @Override
    public String toString() {
        return String.format("[%.2f, %.2f]", x, y);
    }
}
