package geometry;

import org.junit.Before;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DividingChainTest {
    private List<Line2D> chain = new LinkedList<>();

    @Before
    public void setup() {
        chain = new LinkedList<>();
    }

    @org.junit.jupiter.api.Test
    void getRelativePosition_OnChain() {
        createChain(new LinkedList<>(Arrays.asList(new Point(5, 5), new Point(1, 3))));
        DividingChain dividingChain = new DividingChain(chain);
        assertEquals(dividingChain.getRelativePosition(new Point(3, 4)), DividingChain.Position.ON_CHAIN);
    }
    @org.junit.jupiter.api.Test
    void getRelativePosition_Left() {
        createChain(new LinkedList<>(Arrays.asList(new Point(5, 5), new Point(1, 3))));
        DividingChain dividingChain = new DividingChain(chain);
        assertEquals(dividingChain.getRelativePosition(new Point(2, 4)), DividingChain.Position.LEFT);
    }
    @org.junit.jupiter.api.Test
    void getRelativePosition_Right() {
        createChain(new LinkedList<>(Arrays.asList(new Point(5, 5), new Point(1, 3))));
        DividingChain dividingChain = new DividingChain(chain);
        assertEquals(dividingChain.getRelativePosition(new Point(7, 4)), DividingChain.Position.RIGHT);
    }
    @org.junit.jupiter.api.Test
    void getRelativePosition_complex() {
        createChain(new LinkedList<>(Arrays.asList( new Point(9, 19), new Point(7, 17), new Point(9, 15),
                new Point(8, 13),new Point(8, 11), new Point(9, 9), new Point(1, 6),
                new Point(0, 3), new Point(1, 0))));
        DividingChain dividingChain = new DividingChain(chain);
        assertEquals(dividingChain.getRelativePosition(new Point(8, 9)), DividingChain.Position.LEFT);
        assertEquals(dividingChain.getRelativePosition(new Point(-2, 10)), DividingChain.Position.LEFT);
        assertEquals(dividingChain.getRelativePosition(new Point(4, 4)), DividingChain.Position.RIGHT);
        assertEquals(dividingChain.getRelativePosition(new Point(11, 1)), DividingChain.Position.RIGHT);
        assertEquals(dividingChain.getRelativePosition(new Point(8, 16)), DividingChain.Position.ON_CHAIN);
    }
    @org.junit.jupiter.api.Test
    void getRelativePosition_bent() {
        createChain(new LinkedList<>(Arrays.asList(new Point(5, 25), new Point(7, 23), new Point(10, 22),
                new Point(13, 20), new Point(11, 7), new Point(8, 15), new Point(5, 13),
                new Point(5, 10), new Point(6, 8), new Point(2, 6), new Point(0, 3),
                new Point(4, 0))));
        DividingChain dividingChain = new DividingChain(chain);

        assertEquals(dividingChain.getRelativePosition(new Point(4.8,  11.3)), DividingChain.Position.LEFT);
        assertEquals(dividingChain.getRelativePosition(new Point(12,  19)), DividingChain.Position.LEFT);
        assertEquals(dividingChain.getRelativePosition(new Point(4.8,  11.3)), DividingChain.Position.LEFT);
        assertEquals(dividingChain.getRelativePosition(new Point(8,  2)), DividingChain.Position.RIGHT);
    }
    @org.junit.jupiter.api.Test
    void getRelativePosition_notMonotone() {
        createChain(new LinkedList<>(Arrays.asList(new Point(0, 0), new Point(2, 3),
                new Point(5, 4), new Point(7, 1))));

        DividingChain dividingChain = new DividingChain(chain);

        assertEquals(dividingChain.getRelativePosition(new Point(0,  2)), DividingChain.Position.LEFT);
        assertEquals(dividingChain.getRelativePosition(new Point(4,  2)), DividingChain.Position.RIGHT);
    }
    void createChain(List<Point> points) {
        for (int i = 0; i < points.size() - 1; ++i) {
            chain.add(new Line2D(points.get(i), points.get(i + 1)));
        }
    }
}