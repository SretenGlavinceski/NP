package Midterm1.ex22;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class Student {
    String id;
    List<Integer> grades;

    public Student(String id, List<Integer> grades) {
        this.id = id;
        this.grades = grades;
    }

    public static Student create(String line) {
        String[] parts = line.split("\\s+");
        String id = parts[0];
        List<Integer> grades = Arrays.stream(parts).skip(1).map(Integer::parseInt).collect(Collectors.toList());
        return new Student(id, grades);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", grades=" + grades +
                '}';
    }
}

class Rule <IN, OUT> {
    Predicate<IN> predicate;
    Function<IN, OUT> function;

    public Rule(Predicate<IN> predicate, Function<IN, OUT> function) {
        this.predicate = predicate;
        this.function = function;
    }

    Optional<OUT> apply(IN input) {
        if (predicate.test(input))
            return Optional.of(function.apply(input));
        return Optional.empty();
    }
}

class RuleProcessor {
    static <IN, OUT> void process(List<IN> inputs, List<Rule<IN, OUT>> rules) {
        for (IN input : inputs) {
            System.out.println("Input: " + input);
            for (Rule<IN, OUT> rule : rules) {
                Optional<OUT> optional = rule.apply(input);
                if (optional.isPresent())
                    System.out.println("Result: " + optional.get());
                else
                    System.out.println("Condition not met");
            }
        }
    }
}

public class RuleTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { //Test for String,Integer
            List<Rule<String, Integer>> rules = new ArrayList<>();

            /*
            TODO: Add a rule where if the string contains the string "NP",
              the result would be index of the first occurrence of the string "NP"
            * */

            Predicate<String> predicate1 = s -> s.contains("NP");

            Function<String, Integer> function1 = s -> s.indexOf("NP");

            rules.add(new Rule<>(predicate1, function1));

            /*
            TODO: Add a rule where if the string starts with the string "NP",
              the result would be length of the string
            * */


            Predicate<String> predicate2 = s -> s.startsWith("NP");

            Function<String, Integer> function2 = String::length;

            rules.add(new Rule<>(predicate2, function2));


            List<String> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(sc.nextLine());
            }

            RuleProcessor.process(inputs, rules);


        } else { //Test for Student, Double
            List<Rule<Student, Double>> rules = new ArrayList<>();

            //TODO Add a rule where if the student has at least 3 grades,
            // the result would be the max grade of the student

            Predicate<Student> predicate3 = student -> student.grades.size() >= 3;

            Function<Student, Double> function3 = student -> student.grades
                    .stream().mapToDouble(i->i).max().orElse(5.0);

            rules.add(new Rule<>(predicate3, function3));

            //TODO Add a rule where if the student has an ID that starts with 20,
            // the result would be the average grade of the student
            //If the student doesn't have any grades, the average is 5.0

            Predicate<Student> predicate4 = student -> student.id.startsWith("20");

            Function<Student, Double> function4 = student -> student.grades
                    .stream().mapToDouble(i->i).average().orElse(5.0);

            rules.add(new Rule<>(predicate4, function4));


            List<Student> students = new ArrayList<>();
            while (sc.hasNext()){
                students.add(Student.create(sc.nextLine()));
            }

            RuleProcessor.process(students, rules);
        }
    }
}
