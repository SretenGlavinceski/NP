package Midterm2.ex38;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

public class QuizProcessorTest {
    public static void main(String[] args) {
        QuizProcessor.processAnswers(System.in).forEach((k, v) -> System.out.printf("%s -> %.2f%n", k, v));
    }
}

class InvalidQuizAnswersException extends Exception {
    public InvalidQuizAnswersException() {
        super("A quiz must have same number of correct and selected answers");
    }
}

class Student {
    String id;
    List<String> correctAnswers;
    List<String> givenAnswers;

    public Student(String s) throws InvalidQuizAnswersException {
        correctAnswers = new ArrayList<>();
        givenAnswers = new ArrayList<>();

        String [] parts = s.split(";");
        this.id = parts[0];

        String [] correct = parts[1].split(", ");
        correctAnswers.addAll(Arrays.asList(correct));

        String [] given = parts[2].split(", ");
        givenAnswers.addAll(Arrays.asList(given));

        if (correctAnswers.size() != givenAnswers.size())
            throw new InvalidQuizAnswersException();
    }

    double calculatePoints () {
        double temp = 0;
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (correctAnswers.get(i).equals(givenAnswers.get(i)))
                temp += 1;
            else
                temp += -0.25;
        }

        return temp;
    }

    public String getId() {
        return id;
    }
}

class QuizProcessor {
    static Map<String, Double> processAnswers(InputStream is) {
        Map<String, Double> studentsByPoints = new LinkedHashMap<>();

        new BufferedReader(new InputStreamReader(is))
                .lines().forEach(line -> {
                    try {
                        Student student = new Student(line);
                        studentsByPoints.put(student.getId(), student.calculatePoints());
                    } catch (InvalidQuizAnswersException e) {
                        System.out.println(e.getMessage());
                    }
                });

        return studentsByPoints;
    }
}