package Midterm2.ex18;

import java.util.*;
import java.util.stream.Collectors;

/**
 * January 2016 Exam problem 2
 */
public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}

class Cluster <T extends Point2D> {

    Map<Long, T> pointsById;

    public Cluster() {
        this.pointsById = new HashMap<>();
    }

    void addItem(T element) {
        pointsById.put(element.getId(), element);
    }

    void near(long id, int top) {
        Point2D point2D = pointsById.get(id);
        List<Point2D> points = point2D.closestPoints(
                new ArrayList<>(pointsById.values().stream().filter(i -> !i.equals(point2D)).collect(Collectors.toList())));

        for (int i = 0; i < top; i++) {
            System.out.printf("%d. %d -> %.3f%n", i+ 1, points.get(i).getId(), point2D.distance(points.get(i)));
        }
    }
}

class Point2D {
    long id;
    float x;
    float y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    double distance (Point2D point) {
        return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
    }

    List<Point2D> closestPoints(List<Point2D> points) {
        Comparator<Point2D> comparator = Comparator.comparing(i -> i.distance(this));
        return points.stream().sorted(comparator).collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

}
