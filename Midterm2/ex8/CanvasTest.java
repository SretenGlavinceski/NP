package Midterm2.ex8;

import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class CanvasTest {

    public static void main(String[] args) throws IOException {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        try {
            canvas.readShapes(System.in);
        } catch (InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}

class InvalidIDException extends Exception {
    public InvalidIDException(String s) {
        super(s);
    }
}

class InvalidDimensionException extends Exception {
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

interface ShapesInfo {
    double areaShape();

    double parameterShape();

    void scaleSize(double scale);
}

class Circle implements ShapesInfo {
    private double radius;

    public Circle(double radius) throws InvalidDimensionException {
        if (radius == 0.0)
            throw new InvalidDimensionException();
        this.radius = radius;
    }

    @Override
    public double areaShape() {
        return Math.pow(radius, 2) * Math.PI;
    }

    @Override
    public double parameterShape() {
        return 2 * radius * Math.PI;
    }

    @Override
    public void scaleSize(double scale) {
        this.radius *= scale;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f",
                radius,
                areaShape(),
                parameterShape());
    }
}

class Square implements ShapesInfo {
    private double side;

    public Square(double side) throws InvalidDimensionException {
        if (side == 0.0)
            throw new InvalidDimensionException();
        this.side = side;
    }

    @Override
    public double areaShape() {
        return Math.pow(side, 2);
    }

    @Override
    public double parameterShape() {
        return 4 * side;
    }

    @Override
    public void scaleSize(double scale) {
        this.side *= scale;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f",
                side,
                areaShape(),
                parameterShape());
    }
}

class Rectangle implements ShapesInfo {
    private double sideA;
    private double sideB;

    public Rectangle(double sideA, double sideB) throws InvalidDimensionException {
        if (sideA == 0.0 || sideB == 0)
            throw new InvalidDimensionException();
        this.sideA = sideA;
        this.sideB = sideB;
    }

    @Override
    public double areaShape() {
        return sideA * sideB;
    }

    @Override
    public double parameterShape() {
        return 2 * sideA + 2 * sideB;
    }

    @Override
    public void scaleSize(double scale) {
        this.sideA *= scale;
        this.sideB *= scale;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f",
                sideA,
                sideB,
                areaShape(),
                parameterShape());
    }
}

class ShapeFactory {
    private static final String SHAPE_CIRCLE = "1";
    private static final String SHAPE_SQUARE = "2";
//    private static final String SHAPE_RECTANGLE = "3";

    public static ShapesInfo createShape(String line) throws InvalidDimensionException {
        String[] parts = line.split("\\s+");
        String typeShape = parts[0];

        if (typeShape.equals(SHAPE_CIRCLE))
            return new Circle(Double.parseDouble(parts[2]));
        else if (typeShape.equals(SHAPE_SQUARE))
            return new Square(Double.parseDouble(parts[2]));
        return new Rectangle(Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

}

class User {
    private static final String ID_ALLOWED_REGEX = "[a-zA-Z0-9]{1,6}";
    private final String ID;
    private int shapesAmount;
    private double totalAreaShapes;

    public User(String id) throws InvalidIDException {
        if (!id.matches(ID_ALLOWED_REGEX))
            throw new InvalidIDException(String.format("ID %s is not valid", id));
        this.ID = id;
        this.shapesAmount = 0;
        this.totalAreaShapes = 0.0;
    }

    public void addedShape(double area) {
        shapesAmount++;
        totalAreaShapes += area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(ID, user.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public String getID() {
        return ID;
    }

    public int getShapesAmount() {
        return shapesAmount;
    }

    public double getTotalAreaShapes() {
        return totalAreaShapes;
    }
}

class Canvas {
    private Map<User, Set<ShapesInfo>> shapes;
    private Set<ShapesInfo> shapesSorted;

    public void readShapes(InputStream is) throws InvalidDimensionException {
        shapes = new HashMap<>();
        shapesSorted = new TreeSet<>(Comparator.comparing(ShapesInfo::areaShape));

        Scanner scanner = new Scanner(is);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\s+");

            ShapesInfo shape = ShapeFactory.createShape(line);

            try {
                User user = new User(parts[1]);
                shapes.putIfAbsent(user, new TreeSet<>(Comparator.comparing(ShapesInfo::parameterShape)));
                shapes.get(user).add(shape);
                shapes.keySet().stream()
                        .filter(u -> u.equals(user))
                        .forEach(user1 -> user1.addedShape(shape.areaShape()));

                shapesSorted.add(shape);
            } catch (InvalidIDException e) {
                System.out.println(e.getMessage());
            }

        }

        // you cant throw exception further if using stream API
        // that's why this code didn't work

//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        br.lines().forEach(string -> {
//            try {
//                String[] parts = string.split("\\s+");
//                ShapesInfo shape = ShapeFactory.createShape(string);
//
//                User user = new User(parts[1]);
//                shapes.putIfAbsent(user, new TreeSet<>(Comparator.comparing(ShapesInfo::areaShape)));
//                shapes.get(user).add(shape);
//                shapes.keySet().stream()
//                        .filter(u -> u.equals(user))
//                        .forEach(user1 -> user1.addedShape(shape.areaShape()));
//
//                shapesSorted.add(shape);
//
//            } catch (InvalidIDException e) {
//                System.out.println(e.getMessage());
//            }
//        });

//        br.close();
    }

    public void scaleShapes(String userID, double coef) {
        shapes.keySet().stream()
                .filter(user -> user.getID().equals(userID))
                .map(user -> shapes.get(user))
                .flatMap(Collection::stream)
                .forEach(shapesInfo -> shapesInfo.scaleSize(coef));

    }

    public void printAllShapes(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        shapesSorted.forEach(pw::println);
        pw.flush();
    }

    public void printByUserId(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        List<User> usersSorted = shapes.keySet().stream()
                .sorted(Comparator.comparing(User::getShapesAmount).reversed().thenComparing(User::getTotalAreaShapes))
                .toList();

        for (User user : usersSorted) {
            pw.println("Shapes of user: " + user.getID());
            shapes.get(user).forEach(pw::println);
        }

        pw.flush();
    }

    public void statistics(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        DoubleSummaryStatistics ds = shapesSorted.stream()
                .mapToDouble(ShapesInfo::areaShape)
                .summaryStatistics();

        //PRINT STATISTICS
        //count: 5
        //sum: 852.06
        //min: 51.86
        //average: 170.41
        //max: 306.99

        pw.println(String.format("count: %d\nsum: %.2f\nmin: %.2f\naverage: %.2f\nmax: %.2f\n",
                ds.getCount(),
                ds.getSum(),
                ds.getMin(),
                ds.getAverage(),
                ds.getMax()));

        pw.flush();
    }
}
