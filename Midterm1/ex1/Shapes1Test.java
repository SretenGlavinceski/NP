package Midterm1.ex1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}

class Squares {
    int side;

    public Squares(int side) {
        this.side = side;
    }

    public int area() {
        return side * 4;
    }
}

class Canvas {
    String id;
    List<Squares> squares;

    public Canvas(String s) {
        squares = new ArrayList<>();
        String [] parts = s.split("\\s+");
        id = parts[0];

        for (int i = 1; i < parts.length; i++)
            squares.add(new Squares(Integer.parseInt(parts[i])));
    }

    public int sumOfParameters() {
        return squares.stream().mapToInt(Squares::area).sum();
    }

    @Override
    public String toString() {
        return String.format("%s %d %d", id, squares.size(), sumOfParameters());
    }
    int getNumberOfSquares() {
        return squares.size();
    }
}

class ShapesApplication {
    List<Canvas> canvases;

    public ShapesApplication() {
        canvases = new ArrayList<>();
    }

    int readCanvases (InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(line -> canvases.add(new Canvas(line)));
        return canvases.stream().mapToInt(Canvas::getNumberOfSquares).sum();
    }
    void printLargestCanvasTo (OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        pw.println(canvases.stream().max(Comparator.comparing(Canvas::sumOfParameters)).get());
        pw.flush();
    }
}