package geometry;

public class LineCommon implements Cloneable {
    @Override
    public LineCommon clone() {
        try {
            LineCommon lc = (LineCommon) super.clone();
            lc.C = this.C;
            lc.A = this.A;
            lc.B = this.B;
            return lc;
        } catch (CloneNotSupportedException e) {
            LineCommon line = new LineCommon(this.A, this.B);
            line.C = this.C;
            return line;
        }
    }

    protected double  A, B, C;

    public LineCommon(double a, double b) {
        A = a;
        B = b;
    }

}
