package Midterm1.midterm2021.ex3;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Scanner;
import java.util.stream.Collectors;

public class NumberProcessorTest<T extends Number> {

    public static void main(String[] args) {

        ArrayList<Integer> integerArrayList = new ArrayList<>();
        ArrayList<Double> doubleArrayList = new ArrayList<>();

        int countOfIntegers;
        Scanner sc = new Scanner(System.in);
        countOfIntegers = sc.nextInt();
        while (countOfIntegers > 0) {
            integerArrayList.add(sc.nextInt());
            --countOfIntegers;
        }

        int countOfDoubles;
        countOfDoubles = sc.nextInt();
        while (countOfDoubles > 0) {
            doubleArrayList.add(sc.nextDouble());
            --countOfDoubles;
        }

        Numbers<Integer> integerNumbers = new Numbers<>(integerArrayList);

        //TODO first processor


        NumberProcessor<Integer, Long> firstProcessor =
                numbers -> numbers.stream().filter(i -> i < 0).count();

        System.out.println("RESULTS FROM THE FIRST NUMBER PROCESSOR");
        integerNumbers.process(firstProcessor);

        //TODO second processor

        NumberProcessor<Integer, String> secondProcessor = numbers -> {
            DoubleSummaryStatistics dss = numbers.stream().mapToDouble(i -> i).summaryStatistics();
            return String.format("Count: %d Min: %.2f Average: %.2f Max: %.2f",
                    numbers.size(),
                    dss.getMin(),
                    dss.getAverage(),
                    dss.getMax());
        };

        System.out.println("RESULTS FROM THE SECOND NUMBER PROCESSOR");
        integerNumbers.process(secondProcessor);

        Numbers<Double> doubleNumbers = new Numbers<>(doubleArrayList);

        //TODO third processor

        NumberProcessor<Double, ArrayList<Double>> thirdProcessor =
                numbers -> numbers.stream().sorted().collect(Collectors.toCollection(ArrayList::new));


        System.out.println("RESULTS FROM THE THIRD NUMBER PROCESSOR");
        doubleNumbers.process(thirdProcessor);

        //TODO fourth processor

        NumberProcessor<Double, Double> fourthProcessor = new NumberProcessor<Double, Double>() {
            @Override
            public Double compute(ArrayList<Double> numbers) {
                numbers = thirdProcessor.compute(numbers);

                if (numbers.size() % 2 != 0)
                    return numbers.get(numbers.size() / 2);
                else
                    return (numbers.get(numbers.size() / 2) + numbers.get(numbers.size() / 2 - 1)) / 2.0;
            }
        };

        System.out.println("RESULTS FROM THE FOURTH NUMBER PROCESSOR");
        doubleNumbers.process(fourthProcessor);

    }

}

interface NumberProcessor<T extends Number, R> {
    R compute(ArrayList<T> numbers);
}

class Numbers<T extends Number> {
    ArrayList<T> numbers;

    public Numbers(ArrayList<T> numbers) {
        this.numbers = numbers;
    }

    <R> void process(NumberProcessor<T, R> processor) {
        System.out.println(processor.compute(numbers));
    }
}
// test case
// input
//        5
//        1
//        2
//        3
//        4
//        -10
//        5
//        23.5
//        -15.6
//        -10.1
//        77.7
//        88.1

// output
//        RESULTS FROM THE FIRST NUMBER PROCESSOR
//        1
//        RESULTS FROM THE SECOND NUMBER PROCESSOR
//        Count: 5 Min: -10.00 Average: 0.00 Max: 4.00
//        RESULTS FROM THE THIRD NUMBER PROCESSOR
//        [-15.6, -10.1, 23.5, 77.7, 88.1]
//        RESULTS FROM THE FOURTH NUMBER PROCESSOR
//        23.5
