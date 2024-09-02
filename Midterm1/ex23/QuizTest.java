package Midterm1.ex23;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class QuizTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Quiz quiz = new Quiz();

        int questions = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < questions; i++) {
            quiz.addQuestion(sc.nextLine());
        }

        List<String> answers = new ArrayList<>();

        int answersCount = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < answersCount; i++) {
            answers.add(sc.nextLine());
        }

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) {
            quiz.printQuiz(System.out);
        } else if (testCase == 2) {
            try {
                quiz.answerQuiz(answers, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}

enum TypeQuestion {
    TF, // true/false
    MC // multiple choice
}

class InvalidOperationException extends Exception {
    public InvalidOperationException(String s) {
        super(s);
    }
}

abstract class Question {
    private static final String ALLOWED_TYPE = "ABCDE";
    TypeQuestion type;
    String text;
    int points;
    String answer;

    public Question(TypeQuestion type, String text, int points, String answer) {
        this.type = type;
        this.text = text;
        this.points = points;
        this.answer = answer;
    }

    public static Question makeQuestion(String type, String text, String points, String answer) throws InvalidOperationException {
        if (type.equals("TF"))
            return new QuestionTF(text, Integer.parseInt(points), answer);
        if (!ALLOWED_TYPE.contains(answer))
            throw new InvalidOperationException(String.format("%s is not allowed option for this question", answer));
        return new QuestionMC(text, Integer.parseInt(points), answer);
    }

    public int getPoints() {
        return points;
    }

    abstract public double answerQuestion(String a);
}

class QuestionTF extends Question {

    public QuestionTF(String text, int points, String answer) {
        super(TypeQuestion.TF, text, points, answer);
    }

        @Override
    public String toString() {
        return String.format("True/False Question: %s Points: %d Answer: %s",
                text, points, answer);
    }

    @Override
    public double answerQuestion(String a) {
        if (a.equals(answer))
            return points;
        return 0;
    }
}

class QuestionMC extends Question {

    public QuestionMC(String text, int points, String answer) {
        super(TypeQuestion.MC, text, points, answer);
    }

    @Override
    public String toString() {
        return String.format("Multiple Choice Question: %s Points %d Answer: %s",
                text, points, answer);
    }

    @Override
    public double answerQuestion(String a) {
        if (a.equals(answer))
            return points;
        return -(points * 0.2);
    }
}

class Quiz {
    List<Question> questions;

    public Quiz() {
        questions = new ArrayList<>();
    }

    void addQuestion(String questionData) {
        String[] parts = questionData.split(";");
        try {
            questions.add(Question.makeQuestion(parts[0], parts[1], parts[2], parts[3]));
        } catch (InvalidOperationException e) {
            System.out.println(e.getMessage());
        }
    }

    void printQuiz(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        questions.stream().sorted(Comparator.comparing(Question::getPoints).reversed())
                .forEach(pw::println);
        pw.flush();
    }

    void answerQuiz(List<String> answers, OutputStream os) throws InvalidOperationException {
        PrintWriter pw = new PrintWriter(os);
        if (questions.size() != answers.size())
            throw new InvalidOperationException("Answers and questions must be of same length!\n");

        double total = 0.0;

        for (int i = 0; i < questions.size(); i++) {
            pw.println(String.format("%d. %.2f", i + 1, questions.get(i).answerQuestion(answers.get(i))));
            total += questions.get(i).answerQuestion(answers.get(i));
        }
        pw.println(String.format("Total points: %.2f", total));

        pw.flush();
    }
}
