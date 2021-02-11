package geometry;


public class Line2D implements Cloneable {

    public Point p1, p2;

    @Override
    public Line2D clone() {
        try {
            Line2D clone = (Line2D) super.clone();
            clone.p1 = p1.clone();
            clone.p2 = p2.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Line2D(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String toString() {
        return "(" + p1 + ") " + "(" + p2 + ")";
    }

}
