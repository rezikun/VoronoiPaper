package voronoi;

import geometry.Point;

import java.util.Collections;
import java.util.List;

public class DynamicVoronoiDiagram {
    private static final double REBALANCING_THRESHOLD = 0.5;
    int breakCounted = 0;
    int badCounter = 0;
    class Node {
        Node left;
        Node right;
        Node parent;
        Diagram diagram;
        int height = 0;
        public  Node(Node p, Diagram d) {
            parent = p;
            diagram = d;
        }
        public Node(Diagram d) {
            diagram = d;
        }
    }

    public Node root;

    public DynamicVoronoiDiagram() {
        root = null;
    }

    public void insert(Point p) {

        if (root == null) {
            root = new Node(new Diagram(p));
            return;
        }
        if (root.diagram.contains(p)) {
            return;
        }
        Node cur = descend(root, p);
        if (cur.diagram.size() == 3) {
            List<Point> vertices = cur.diagram.getVertices();
            vertices.add(p);
            Collections.sort(vertices);
            divideNode(vertices, cur);
            mergeUp(cur);
        }
        else {
            mergeUp(cur, p);
        }
        rebalance(root);
    }
    private void insert(Point p, Node node) {
        Node cur = descend(node, p);
        if (cur.diagram.size() == 3) {
            List<Point> vertices = cur.diagram.getVertices();
            vertices.add(p);
            Collections.sort(vertices);
            divideNode(vertices, cur);
            mergeUp(cur);
        }
        else {
            mergeUp(cur, p);
        }
    }
    void delete(Point p) {
        if (root == null || !root.diagram.contains(p)) {
            return;
        }
        if (root.diagram.size() == 1) {
            root = null;
            return;
        }
        Node cur = descend(root, p);
        if (cur.diagram.size() == 2) {
            List<Point> vertices = cur.diagram.getVertices();
            vertices.remove(p);
            Point savedPoint = vertices.get(0);
            cur.diagram = null;
            if (cur.parent == null) {
                cur.diagram = new Diagram(savedPoint);
                cur.left = null;
                cur.right = null;
                return;
            }
            cur = cur.parent;
            if (cur.left.diagram != null) {
                cur.diagram = cur.left.diagram;
                cur.right = cur.left.right;
                if (cur.right != null) {
                    cur.right.parent = cur;
                }
                cur.left = cur.left.left;
                if (cur.left != null) {
                    cur.left.parent = cur;
                }
            } else {
                cur.diagram = cur.right.diagram;
                cur.left = cur.right.left;
                if (cur.left != null) {
                    cur.left.parent = cur;
                }
                cur.right = cur.right.right;
                if (cur.right != null) {
                    cur.right.parent = cur;
                }
            }
            insert(savedPoint, cur);
            rebalance(root);
            return;
        }
        List<Point> vertices = cur.diagram.getVertices();
        vertices.remove(p);
        cur.diagram = new Diagram(vertices);
        if (cur.parent == null) {
            rebalance(root);
            return;
        }
        mergeUp(cur.parent);
        rebalance(root);
    }

    private void mergeUp(Node cur, Point p) {
        int leftHeight = cur.left != null ? cur.left.height : 0;
        int rightHeight = cur.right != null ? cur.right.height : 0;
        cur.height = Math.max(leftHeight, rightHeight) + 1;
        Diagram mergeResult = Diagram.mergeDiagrams(cur.diagram, new Diagram(p));

        while(true) {
            cur.diagram = mergeResult;
            if (cur.parent == null) break;
            cur = cur.parent;
            if (cur.left.diagram == null || cur.right.diagram == null) {
                System.out.println("NOPE");
            }
            mergeResult = Diagram.mergeDiagrams(cur.left.diagram, cur.right.diagram);
            if (cur.diagram.chain.equals(mergeResult.chain)){
                breakCounted++;
                break;
            }
        }
    }

    private void mergeUp(Node cur) {
        if (cur.left == null || cur.right == null) {
            cur = cur.parent;
        }
        cur.height = Math.max(cur.left.height, cur.right.height) + 1;
        Diagram mergeResult = Diagram.mergeDiagrams(cur.left.diagram, cur.right.diagram);
        while(true) {
            cur.diagram = mergeResult;
            if (cur.parent == null) break;
            cur = cur.parent;
            if (cur.left.diagram == null || cur.right.diagram == null) {
                System.out.println("NOPE");
            }
            mergeResult = Diagram.mergeDiagrams(cur.left.diagram, cur.right.diagram);
            if (cur.diagram.chain.equals(mergeResult.chain)){
                breakCounted++;
                break;
            }
        }
    }

    private void divideNode(List<Point> vertices, Node cur) {
        if (vertices.size() != 4) {
            throw new RuntimeException("WHY");
        }
        Diagram leftDiagram = new Diagram(vertices.get(0), vertices.get(1));
        Diagram rightDiagram = new Diagram(vertices.get(2), vertices.get(3));
        cur.left = new Node(cur, leftDiagram);
        cur.right = new Node(cur, rightDiagram);
    }

    private static Node descend(Node node, Point p) {
        Node cur = node;
        while (cur.diagram.size() > 3) {
            if (p.x < cur.diagram.midpoint) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
//            switch (cur.chain.getRelativePosition(p)) {
//                case LEFT:
//                    cur = cur.left;
//                    break;
//                case ON_CHAIN:
//                    if (cur.left.diagram.size() <= cur.right.diagram.size()) {
//                        cur = cur.left;
//                    } else {
//                        cur = cur.right;
//                    }
//                    break;
//                case RIGHT:
//                    cur = cur.right;
//                    break;
//            }
        }
        return cur;
    }



    public CellIterator getSite(Point p) {
        return root.diagram.getSite(p);
    }

    private void rebalance(Node node) {
        if (node.left == null || node.right == null) return;
        if (node.left.diagram.rightmost.x > node.right.diagram.leftmost.x) {
            badCounter++;
            rebalanceRec(node, node.diagram.getVertices());
            return;
        }
        if ((double)Math.abs(node.left.diagram.size() - node.right.diagram.size())/node.diagram.size() > REBALANCING_THRESHOLD) {
            rebalanceRec(node, node.diagram.getVertices());
            return;
        }
        rebalance(node.left);
        rebalance(node.right);
    }

    private void rebalanceRec(Node node, List<Point> vertices) {
        if (vertices.size() <= 3) return;
        node.diagram = new Diagram(vertices);
        List<Point> leftVertices = vertices.subList(0, vertices.size()/2);
        List<Point> rightVertices = vertices.subList(vertices.size()/2, vertices.size());
        if (node.left == null || node.right == null) {
            node.left = new Node(new Diagram(leftVertices));
            node.right = new Node(new Diagram(rightVertices));
        }
        node.left.parent = node;
        node.left.diagram = new Diagram(leftVertices);
        node.right.parent = node;
        node.right.diagram = new Diagram(rightVertices);
        rebalanceRec(node.left, leftVertices);
        rebalanceRec(node.right, rightVertices);
    }
}
