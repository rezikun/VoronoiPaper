package geometry;

import java.util.LinkedList;
import java.util.List;

public class DividingChain {
    private final List<Line2D> chain;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        if (o.getClass() == getClass()) {
            DividingChain dc = (DividingChain) o;
            if (chain.size() != dc.chain.size()) return false;
            for (int i = 0; i < chain.size() - 1; ++i) {
                if (!chain.get(i).p1.equals(dc.chain.get(i).p1) ||
                !chain.get(i).p2.equals((dc.chain.get(i).p2))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public DividingChain(List<Line2D> chain) {
        this.chain = chain;
    }

    public enum Position {
        LEFT,
        RIGHT,
        ON_CHAIN;
    }

    public List<Line2D> getChain() {
        return chain;
    }

    public Position getRelativePosition(Point p) {
        Line2D segment = locatePoint(p);
        double a = segment.p2.y - segment.p1.y;
        double b = -segment.p2.x + segment.p1.x;
        double c = segment.p1.x * a + segment.p1.y * b;
        if (b == 0) {
            if (p.x < c/a) {
                return Position.LEFT;
            } else if (p.x > c/a) {
                return Position.RIGHT;
            } else {
                return Position.ON_CHAIN;
            }
        }
        if (a == 0) {
            if (segment.p1.x > p.x) {
                return Position.LEFT;
            } else {
                return Position.RIGHT;
            }
        }
        double k = -a/b;
        double bb = c/b;
        if (p.y < k * p.x + bb) {
            if (k < 0) {
                return Position.LEFT;
            } else {
                return Position.RIGHT;
            }
        } else if (p.y == k*p.x + bb) {
            return Position.ON_CHAIN;
        } else {
            if (k < 0) {
                return Position.RIGHT;
            } else {
                return Position.LEFT;
            }
        }
    }

    private Line2D locatePoint(Point p) {
        if (chain.size() == 0) {
            System.out.print("ass");
        }
        if (chain.get(0).p1.y < p.y) {
            return chain.get(0);
        }
        for (Line2D segment : chain) {
            // horizontal line
            if (segment.p1.y == segment.p2.y && segment.p1.y == p.y) {
                return segment;
            }
            if (segment.p1.y > p.y && segment.p2.y <= p.y) {
                return segment;
            }
        }
        if (chain.get(chain.size() - 1).p2.y > p.y) {
            return  chain.get(chain.size() - 1);
        }
        throw new RuntimeException("Couldn't find segment for point :" + p + " and dividing chain: " + chain.toString());
    }
}
