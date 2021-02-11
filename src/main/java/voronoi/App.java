package voronoi;

import geometry.Point;
import geometry.Utils;
import gui.Canvas;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

public class App {
    private static void waitInput() {
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
    }
    private static void recursivelyDraw(DynamicVoronoiDiagram.Node node, Canvas paper, int level, String position) {
        if (node == null) return;
        node.diagram.draw(paper, false, true, String.format("LEVEL: %d %s", level, position));
        waitInput();
        recursivelyDraw(node.left, paper, level + 1, "left");
        recursivelyDraw(node.right, paper, level + 1, "right");
    }

    private static void drawTree(DynamicVoronoiDiagram dd, Canvas paper) {
        assert dd.root != null;
        dd.root.diagram.draw(paper, false, true,"ROOT");
        recursivelyDraw(dd.root.left, paper, 1, "left");
        recursivelyDraw(dd.root.right, paper, 1, "right");
    }

    public static void main(String[] args) throws InterruptedException {

        Canvas paper = new Canvas(1024, 800, 120, false);
        List<Point> pts = SceneProducer.getScene(6);
        DynamicVoronoiDiagram dynamicDiagram = new DynamicVoronoiDiagram();
        List<Long> insertionTimes = new ArrayList<>();
        List<Long> insertionTimesControl = new ArrayList<>();

        // The following commented code is for performance measurement.

//        for (Point p : pts) {
//            long start = nanoTime();
//            dynamicDiagram.insert(p);
//            long finish = nanoTime();
//            insertionTimes.add(finish - start);
//
//            var points = dynamicDiagram.root.diagram.getVertices();
//            long startControl = nanoTime();
//            Diagram staticDiagram = new Diagram(points);
//            long finishControl = nanoTime();
//            insertionTimesControl.add(finishControl - startControl);
//        }
//        System.out.println("Dynamic times: " + insertionTimes);
//        System.out.println("Static times: " + insertionTimesControl);
//        try {
//            FileWriter dynamicTimesWriter = new FileWriter("dynamic_times2.csv");
//            dynamicTimesWriter.write(insertionTimes.toString());
//            dynamicTimesWriter.close();
//            FileWriter staticTimesWriter = new FileWriter("static_times2.csv");
//            staticTimesWriter.write(insertionTimesControl.toString());
//            staticTimesWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        for (int i = 0; i < pts.size(); ++i) {
//            System.out.println("Diff at " + i + ": "  + (insertionTimes.get(i) - insertionTimesControl.get(i)));
//        }

        double angle = 0;
        int errorCounter = 0;
        int frameCounter = 0;
        double calcT = 0;
        Point forException = new Point(0, 0);
        int pointCounter = 0;
        Scanner scanner = new Scanner(System.in);
        while (!paper.closed) {
            try {
                long t0 = System.nanoTime();
                long t1 = System.nanoTime();
                calcT = calcT * 0.98 + (t1 - t0) * 0.02;
                for (Point p : pts) {
                    ++pointCounter;
                    forException = p;
                    dynamicDiagram.insert(p);
                    System.out.println("Break counter: " + dynamicDiagram.breakCounted);
                    System.out.println("BAD counter: " + dynamicDiagram.badCounter);
                    dynamicDiagram.root.diagram.draw(paper, false, true,"REAL", p);
                    // Time between each point is inserted in ms.
                    sleep(1000);

                    // Uncomment if you want to draw points one by one after any input in terminal. If input is "tree"
                    // will draw each node of the current voronoi tree in prefix order.

//                    String line = scanner.nextLine();
//                    if (line.equals("tree")) {
//                        drawTree(dynamicDiagram, paper);
//                    }
                }
                for (Point p : pts) {
                    dynamicDiagram.root.diagram.draw(paper, false, true,"REAL");
                    dynamicDiagram.delete(p);
                    sleep(100);
                }
            } catch (Exception e) {
                // Time between each point is deleted in ms.
                sleep(1000);

                System.out.println(forException + ": " + pointCounter);
                System.out.println(++errorCounter + ": " + e);
                e.printStackTrace();
                throw e;
            }
            angle = angle + 0.02 * Math.sin(-0.2 + 0.005 * frameCounter);
            paper.setZ(200 + 100 * Math.cos(0.003 * frameCounter));
            paper.setZ(50);
        }
    }

}
