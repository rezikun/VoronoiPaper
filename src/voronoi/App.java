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

import static java.lang.System.nanoTime;
import static java.lang.Thread.sleep;

public class App {

    public static void main(String[] args) throws InterruptedException {

        Canvas paper = new Canvas(1024, 800, 120, true);
        List<Point> pts = SceneProducer.getScene(7);
        DynamicVoronoiDiagram dynamicDiagram = new DynamicVoronoiDiagram();
        List<Long> insertionTimes = new ArrayList<>();
        List<Long> insertionTimesControl = new ArrayList<>();
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
                    dynamicDiagram.root.diagram.draw(paper, true, true,"REAL", p);
                    sleep(100);
                }
                for (Point p : pts) {
                    dynamicDiagram.root.diagram.draw(paper, true, true,"REAL");
                    dynamicDiagram.delete(p);
                    sleep(100);
                }
            } catch (Exception e) {
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
