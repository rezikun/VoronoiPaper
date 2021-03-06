package voronoi;

import geometry.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SceneProducer {

    static final int scenesNum = 5;

    static List<Point> getScene(int scene) {
        List<Point> pts = new ArrayList<>();
        int i0 = new Random().nextInt(100000);
        //System.out.println("Seed: " + i0);
        Random r = new Random(i0);
        switch (scene) {

            //square grid
            case 1: {
                int n = 16;
                for (int i = 0; i < n * n; i++)
                    pts.add(new Point(5.0 * (-n / 2 + i % n) / n, 5.0 * (-n / 2 + i / n) / n));
                break;
            }

            //randomized square grid
            case 2: {
                int n = 50;
                for (int i = 0; i < n * n; i++)
                    if (r.nextInt(100) > 30)
                        pts.add(new Point(5.0 * (-n / 2 + i % n) / n, 5.0 * (-n / 2 + i / n) / n));
                break;
            }

            //whirlpool
            case 3: {
                int n = 800;
                double r0 = 2.6;
                double an = 0;
                for (int i = 0; i < n; i++) {
                    pts.add(new Point(r0 * Math.cos(an), r0 * Math.sin(an)));
                    an = an + 0.1 * r0;
                    r0 = r0 * 0.9985;
                }
                pts.add(new Point(0, 0));
                break;
            }

            // large randomized square grid
            case 4: {
                int n = 100;
                for (int i = 0; i < n * n; i++)
                    if (r.nextInt(100) > 50)
                        pts.add(new Point(5.0 * (-n / 2 + i % n) / n, 5.0 * (-n / 2 + i / n) / n));
                break;
            }

            case 5: {
                pts.add(new Point(1.0, 1.0));
                pts.add(new Point(1.5, 1.1));
                pts.add(new Point(2.0, 1.4));
                pts.add(new Point(2.5, 2.5));
                break;
            }
            case 6: {
                pts.add(new Point(3.0, 2.0));
                pts.add(new Point(3.5, 2.1));
                pts.add(new Point(4.0, 2.4));
                pts.add(new Point(4.5, 3.5));
                pts.add(new Point(3.5, 3.5));
                pts.add(new Point(3.0, 3.0));
                break;
            }
            //random points
            default: {
                int n = 100;
                for (int i = 0; i < n; i++) pts.add(new Point(-2 + r.nextFloat() * 4, -2 + r.nextFloat() * 4));
                break;
            }

        }
        return pts;
    }

}
