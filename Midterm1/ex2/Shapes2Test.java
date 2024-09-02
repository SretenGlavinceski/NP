package Midterm1.ex2;


import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}

enum TypeShape {
    CIRCLE,
    SQUARE
}

abstract class Shape {
    TypeShape type;
    int side;

    public Shape(int side) {
        this.side = side;
    }

    public static Shape createShape(String type, int side) {
        if (type.equals("C"))
            return new Circle(side);
        return new Square(side);
    }

    abstract public double area();

    public TypeShape getType() {
        return type;
    }
}

class Circle extends Shape {

    public Circle(int side) {
        super(side);
        type = TypeShape.CIRCLE;
    }

    @Override
    public double area() {
        return Math.PI * side * side;
    }
}

class Square extends Shape {

    public Square(int side) {
        super(side);
        type = TypeShape.SQUARE;
    }

    @Override
    public double area() {
        return side * side;
    }
}

class IrregularCanvasException extends Exception {
    public IrregularCanvasException(String s) {
        super(s);
    }
}

class Canvas {
    String id;
    List<Shape> shapes;

    public Canvas(String s, double maxArea) throws IrregularCanvasException {
        shapes = new ArrayList<>();
        String[] parts = s.split("\\s+");
        id = parts[0];
        for (int i = 1; i < parts.length; i += 2) {
            Shape shapeToAdd = Shape.createShape(parts[i], Integer.parseInt(parts[i + 1]));
            if (shapeToAdd.area() > maxArea)
                throw new IrregularCanvasException(
                        String.format("Canvas %s has a shape with area larger than %.2f", id, maxArea));
            shapes.add(shapeToAdd);
        }
    }
    double totalArea() {
        return shapes.stream().mapToDouble(Shape::area).sum();
    }

//ID total_shapes total_circles total_squares min_area max_area average_area.

    @Override
    public String toString() {
        DoubleSummaryStatistics dss = shapes.stream().mapToDouble(Shape::area).summaryStatistics();
        return String.format("%s %d %d %d %.2f %.2f %.2f",
                id,
                shapes.size(),
                shapes.stream().filter(i -> i.getType().equals(TypeShape.CIRCLE)).count(),
                shapes.stream().filter(i -> i.getType().equals(TypeShape.SQUARE)).count(),
                dss.getMin(),
                dss.getMax(),
                dss.getAverage());
    }
}

class ShapesApplication {
    List<Canvas> canvases;
    double maxArea;

    public ShapesApplication(double maxArea) {
        canvases = new ArrayList<>();
        this.maxArea = maxArea;
    }

    void readCanvases(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(line -> {
            try {
                canvases.add(new Canvas(line, maxArea));
            } catch (IrregularCanvasException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    void printCanvases (OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        canvases.stream().sorted(Comparator.comparing(Canvas::totalArea).reversed())
                .forEach(pw::println);

        pw.flush();
    }
}