package voronoi;

import geometry.DividingChain;
import geometry.Line2D;
import geometry.Point;
import geometry.Utils;
import gui.Canvas;

import java.awt.*;
import java.util.*;
import java.util.List;

import static voronoi.ConvexHull.mergeHulls;

class Diagram {
    private ConvexHull hull;
    private HashMap<Point, CellIterator> cell = new HashMap<>();
    DividingChain chain = null;
    private static boolean traceRay = false;
    Point leftmost;
    Point rightmost;
    double midpoint;

//    @Override
//    public Object clone() {
//        try {
//            return super.clone();
//        } catch (CloneNotSupportedException e) {
//            HashMap<Point, CellIterator> cell = new HashMap<>();
//            Set<Map.Entry<Point, CellIterator>> entries = this.cell.entrySet();
//            Map.Entry<Point, CellIterator> entry = entries.iterator().next();
//            Queue<Pair<Point, CellIterator>> queue = new LinkedList<>();
//            queue.add(new Pair<>(entry.getKey(), entry.getValue()));
//            Set<Point> used = new HashSet<>();
//            while(!queue.isEmpty()) {
//                Point curPoint = queue.peek().getValue0();
//                CellIterator curCellIterator = queue.peek().getValue1();
//                queue.poll();
//                used.add(curPoint);
//                DirectedEdge firstEdge = curCellIterator.getEdge();
//            }
//            for (Map.Entry<Point, CellIterator> mapEntry : entries) {
//                cell.put(mapEntry.getKey().copy(), mapEntry.getValue().copy());
//            }
//            return new Diagram(hull.clone(), cell);
//        }
//    }


    boolean contains(Point p) {
        for (Point point : getVertices()) {
            if (point.equals(p)) {
                return true;
            }
        }
        return false;
    }

    public List<Point> getVertices() {
        Set<Map.Entry<Point, CellIterator>> entries = cell.entrySet();
        List<Point> points = new LinkedList<>();
        for (Map.Entry<Point, CellIterator> e : entries) {
            points.add(e.getKey().clone());
        }
        return points;
    }

    public Diagram copyDiagram() {
        List<Point> points = getVertices();
        return new Diagram(points);
    }

    public CellIterator getSite(Point p) {
        return cell.get(p);
    }

    Diagram(Diagram d1, Diagram d2) {
        this.hull = mergeHulls(d1.hull, d2.hull);
        this.cell.putAll(d1.cell);
        this.cell.putAll(d2.cell);
        this.leftmost = d1.leftmost;
        this.rightmost = d2.rightmost;
        this.midpoint = (d1.rightmost.x + d2.leftmost.x)/2;
    }

    int size() {
        return cell.size();
    }

    Diagram(Point p0) {
        hull = new ConvexHull(Arrays.asList(p0));
        cell.put(p0, new CellIterator(DirectedEdge.getEmptyCell(), true));
        leftmost = p0;
        rightmost = p0;
        midpoint = p0.x;
    }

    Diagram(Point p1, Point p2) {
        hull = new ConvexHull(Arrays.asList(p1, p2));
        Edge e = new Edge(p1, p2);
        DirectedEdge[] pair = DirectedEdge.getJoinedEdges(e);
        CellIterator itr1 = new CellIterator(DirectedEdge.getEmptyCell(), false);
        CellIterator itr2 = new CellIterator(DirectedEdge.getEmptyCell(), true);
        itr1.insert(pair[0]);
        itr2.insert(pair[1]);
        cell.put(p1, itr1);
        cell.put(p2, itr2);
        if (p1.x < p2.x) {
            leftmost = p1;
            rightmost = p2;
        } else{
            leftmost = p2;
            rightmost = p1;
        }
        midpoint = (leftmost.x + rightmost.x)/2;
    }

    Diagram(List<Point> s) {
        s.sort(Utils::comparePointXY);
        Diagram d = Recursive(s, 0, s.size(), s.size());
        this.hull = d.hull;
        this.cell = d.cell;
        this.chain = d.chain;
        leftmost = s.get(0);
        rightmost = s.get(s.size() - 1);
        if (s.size() == 1) {
            midpoint = s.get(0).x;
        } else {
            midpoint = (s.get(s.size() / 2 - 1).x + s.get(s.size() / 2).x) / 2;
        }
    }

    private Diagram Recursive(List<Point> s, int i1, int i2, int sz) {
        int n = i2 - i1;
        if (n == 1) return new Diagram(s.get(i1));
        if (n == 2) return new Diagram(s.get(i1), s.get(i1 + 1));
        Diagram d1 = Recursive(s, i1, i1 + n / 2, sz);
        Diagram d2 = Recursive(s, i1 + n / 2, i2, sz);
        if (n == sz) traceRay = true;
        return mergeDiagrams(d1, d2);
    }

    static Diagram mergeDiagrams(Diagram dd1, Diagram dd2) {
        List<Line2D> tr;
        Diagram d1 = dd1.copyDiagram();
        Diagram d2 = dd2.copyDiagram();
        Diagram.traceRay = false;
        tr = new ArrayList<>();
        Diagram d = new Diagram(d1, d2);
        Point[] pivot = new Point[]{d.hull.pivot[0].p1, d.hull.pivot[0].p2};
        CellIterator[] itr = new CellIterator[]{d1.cell.get(pivot[0]).init(true), d2.cell.get(pivot[1]).init(false)};
        Edge ray = null;
        for (int i = 0; i < 500000; i++) {
            ray = new Edge(pivot[0], pivot[1], ray);
            DirectedEdge[] pair = DirectedEdge.getJoinedEdges(ray);
            DirectedEdge[] edges = new DirectedEdge[]{itr[0].cropCell(ray, pair[0]), itr[1].cropCell(ray, pair[1])};
            if (pivot[0] == d.hull.pivot[1].p1 && pivot[1] == d.hull.pivot[1].p2) {
                tr.add(new Line2D(ray.o1, ray.o2));
                d.chain = new DividingChain(tr);
                return d;
            }
            Point[] intPoint = new Point[]{ray.getIntersection(edges[0]), ray.getIntersection(edges[1])};
            if (intPoint[0] == null && intPoint[1] == null) throw new RuntimeException("No intersection");
            int l = ray.getDistToOrigin(intPoint[0]) < ray.getDistToOrigin(intPoint[1]) ? 0 : 1;
            ray.o2 = intPoint[l];
            if (Utils.comparePointXY(ray.o1, ray.o2) == 0) {
                itr[0].removeLast();
                itr[1].removeLast();
            }
            tr.add(new Line2D(ray.o1, ray.o2));
            if (edges[l].e == null) {
                d.chain = new DividingChain(tr);
                return d;
            }
            if (l == 1 ^ edges[l].fwd) edges[l].e.o1 = ray.o2;
            else edges[l].e.o2 = ray.o2;
            pivot[l] = edges[l].e.getOpposite(pivot[l]);
            itr[l] = d.cell.get(pivot[l]).setDirAndResetToEdge(l == 0, edges[l].opposite);
        }
        throw new RuntimeException("Merging overflow");
    }
    void draw(Canvas pap, boolean drawTraceRay, boolean drawOpenedCells, String canvasHeader) {
        draw(pap, drawTraceRay, drawOpenedCells, canvasHeader, null);
    }
    void draw(Canvas pap, boolean drawTraceRay, boolean drawOpenedCells, String canvasHeader, Point p) {
        synchronized (pap.figures) {
            pap.clear();
            for (Map.Entry<Point, CellIterator> siteCell : cell.entrySet()) {
                CellIterator itr = siteCell.getValue();
                DirectedEdge n0 = itr.getAnyOpenedEdge();
                if (!drawOpenedCells && n0.inf != 0) continue;
                Point p0 = siteCell.getKey().clone();
                if (p0.equals(p)) {
                    pap.addPoint(p0, Color.RED, 10);
                } else {
                    pap.addPoint(p0, Color.BLACK, 4);
                }
                DirectedEdge n = n0;
                int c = 0;
                do {
                    if (c++ == 1000) throw new RuntimeException("Drawing overflow");
                    if (n.inf == 0) pap.addLine(n.e.toShiftedLine(p0, 1), n.fwd ? Color.BLUE : Color.BLUE, false);
                    n = n.next;
                } while (n != n0);
            }
            if (drawTraceRay && chain != null) for (Line2D l : chain.getChain()) pap.addLine(l, Color.RED, true);
            pap.addText(canvasHeader,5,15,Color.BLACK);
        }
        pap.repaint();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int pn = 0;
        for (Map.Entry<Point, CellIterator> siteCell : cell.entrySet()) {
            s.append(++pn).append(". ").append(siteCell.getKey()).append(siteCell.getValue());
        }
        return s.toString();
    }

}