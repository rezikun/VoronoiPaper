package voronoi;

import geometry.Line2D;
import geometry.Point;

import java.util.ArrayList;
import java.util.List;

import static geometry.Utils.*;

class ConvexHull implements Cloneable {
    private ArrayList<Point>[] halves;
    Line2D[] pivot = new Line2D[2];

    @Override
    public ConvexHull clone() {
        try {
            ConvexHull clone = (ConvexHull) super.clone();
            ArrayList[] halvesCopy = new ArrayList[halves.length];
            for (int i = 0; i < halves.length; ++i) {
                ArrayList<Point> listCopy = new ArrayList<>();
                for (Point p : halves[i]) {
                    listCopy.add(p.clone());
                }
                halvesCopy[i] = listCopy;
            }
            clone.halves = halvesCopy;

            Line2D[] pivotCopy = new Line2D[pivot.length];
            for (int i = 0; i < pivot.length; ++i) {
                pivotCopy[i] = pivot[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    ConvexHull(List<Point> s) {
        halves = new ArrayList[]{new ArrayList(), new ArrayList()};
        s.forEach(this::putPoint);
    }

    static ConvexHull mergeHulls(ConvexHull ch, ConvexHull ch2) {
        int[] m = new int[]{ch.halves[0].size(), ch.halves[1].size()};
        for (int i = 0; i < 2; i++) {
            for (Point p : ch2.halves[i]) ch.putPointHalf(p, m, i);
            if (m[i] > 0 && m[i] < ch.halves[i].size())
                ch.pivot[i] = new Line2D(ch.halves[i].get(m[i] - 1), ch.halves[i].get(m[i]));
        }
        return ch;
    }

    private static boolean cmpArea(double d, int half) {
        return half == 0 ? cmpGZ(d) : cmpLZ(d);
    }

    private void putPointHalf(Point p, int[] m, int i) {
        while (halves[i].size() > 1 && cmpArea(getArea(halves[i].get(halves[i].size() - 2), halves[i].get(halves[i].size() - 1), p), i))
            halves[i].remove(halves[i].size() - 1);
        if (m != null && halves[i].size() < m[i]) m[i] = halves[i].size();
        halves[i].add(p);
    }

    private void putPoint(Point p) {
        for (int i = 0; i < 2; i++) putPointHalf(p, null, i);
    }

}
