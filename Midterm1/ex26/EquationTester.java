package Midterm1.ex26;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


class Line {
    Double coeficient;
    Double x;
    Double intercept;

    public Line(Double coeficient, Double x, Double intercept) {
        this.coeficient = coeficient;
        this.x = x;
        this.intercept = intercept;
    }

    public static Line createLine(String line) {
        String[] parts = line.split("\\s+");
        return new Line(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    public double calculateLine() {
        return coeficient * x + intercept;
    }

    @Override
    public String toString() {
        return String.format("%.2f * %.2f + %.2f", coeficient, x, intercept);
    }
}

class Equation<IN, OUT> {
    Supplier<IN> supplier;
    Function<IN, OUT> function;

    public Equation(Supplier<IN> supplier, Function<IN, OUT> function) {
        this.supplier = supplier;
        this.function = function;
    }

    Optional<OUT> calculate() {
        return Optional.of(function.apply(supplier.get()));
    }
}

class EquationProcessor {
    static <IN, OUT> void process(List<IN> inputs, List<Equation<IN, OUT>> equations) {
        for (IN input : inputs)
            System.out.println("Input: " + input);

        for (Equation<IN, OUT> equation : equations) {
            System.out.println("Result: " + equation.calculate().get());
        }
    }
}

public class EquationTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // Testing with Integer, Integer
            List<Equation<Integer, Integer>> equations1 = new ArrayList<>();
            List<Integer> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(Integer.parseInt(sc.nextLine()));
            }

            // TODO: Add an equation where you get the 3rd integer from the inputs list,
            //  and the result is the sum of that number and the number 1000.

            Supplier<Integer> supplier1 = () -> inputs.get(2);

            Function<Integer, Integer> function1 = integer -> integer + 1000;

            equations1.add(new Equation<>(supplier1, function1));

            // TODO: Add an equation where you get the 4th integer from the inputs list,
            //  and the result is the maximum of that number and the number 100.

            Supplier<Integer> supplier2 = () -> inputs.get(3);

            Function<Integer, Integer> function2 = integer -> Math.max(integer, 100);

            equations1.add(new Equation<>(supplier2, function2));

            EquationProcessor.process(inputs, equations1);

        } else { // Testing with Line, Integer
            List<Equation<Line, Double>> equations2 = new ArrayList<>();
            List<Line> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(Line.createLine(sc.nextLine()));
            }

            //TODO Add an equation where you get the 2nd line,
            // and the result is the value of y in the line equation.

            Supplier<Line> supplier3 = () -> inputs.get(1);

            Function<Line, Double> function3 = Line::calculateLine;

            equations2.add(new Equation<>(supplier3, function3));


            //TODO Add an equation where you get the 1st line,
            // and the result is the sum of all y values for all
            // lines that have a greater y value than that equation.

            Supplier<Line> supplier4 = () -> inputs.get(0);

            Function<Line, Double> function4 = line -> inputs.stream().mapToDouble(Line::calculateLine)
                    .filter(line1 -> line1 > line.calculateLine()).sum();

            equations2.add(new Equation<>(supplier4, function4));

            EquationProcessor.process(inputs, equations2);
        }
    }
}
