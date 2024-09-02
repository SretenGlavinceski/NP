package Midterm1.ex6;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}

enum Color {
    RED, GREEN, BLUE
}

enum TypeShape { // moze i bez tip na shape...
    CIRCLE,
    RECTANGLE
}

interface Scalable {
    void scale(float scaleFactor);
}

interface Stackable {
    float weight();
}

abstract class Shape implements Scalable, Stackable {
    TypeShape typeShape;
    Color color;
    String id;

    public Shape(TypeShape typeShape, String id, Color color) {
        this.id = id;
        this.typeShape = typeShape;
        this.color = color;
    }

    @Override
    abstract public void scale(float scaleFactor);

    @Override
    abstract public float weight();
}

class Circle extends Shape {
    float radius;

    public Circle(TypeShape typeShape, String id, Color color, float radius) {
        super(typeShape, id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        radius *= scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (radius * radius * Math.PI);
    }
    @Override
    public String toString() {
        return String.format("C: %-5s%-10s%10.2f", id, color, weight());
    }
}

class Rectangle extends Shape {
    float width;
    float height;

    public Rectangle(TypeShape typeShape, String id, Color color, float width, float height) {
        super(typeShape, id, color);
        this.width = width;
        this.height = height;
    }
    @Override
    public void scale(float scaleFactor) {
        width *= scaleFactor;
        height *= scaleFactor;
    }

    @Override
    public float weight() {
        return width * height;
    }
    @Override
    public String toString() {
        return String.format("R: %-5s%-10s%10.2f", id, color, weight());
    }
}

class Canvas {
    List<Shape> shapes;

    public Canvas() {
        shapes = new ArrayList<>();
    }

    void add(String id, Color color, float radius) {
        shapes.add(new Circle(TypeShape.CIRCLE ,id, color, radius));
    }
    void add(String id, Color color, float width, float height) {
        shapes.add(new Rectangle(TypeShape.RECTANGLE ,id, color, width, height));
    }

    void scale(String id, float scaleFactor) {
        shapes.stream().filter(i -> i.id.equals(id)).forEach(i -> i.scale(scaleFactor));
    }

    @Override
    public String toString() {
        return shapes.stream().sorted(Comparator.comparing(Shape::weight)
                .reversed()).map(Object::toString).collect(Collectors.joining("\n")) + "\n";
    }
}