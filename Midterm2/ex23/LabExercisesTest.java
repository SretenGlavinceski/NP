package Midterm2.ex23;

import org.w3c.dom.stylesheets.LinkStyle;

import java.util.*;
import java.util.stream.Collectors;

public class LabExercisesTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LabExercises labExercises = new LabExercises();
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            String index = parts[0];
            List<Integer> points = Arrays.stream(parts).skip(1)
                    .mapToInt(Integer::parseInt)
                    .boxed()
                    .collect(Collectors.toList());

            labExercises.addStudent(new Student(index, points));
        }

        System.out.println("===printByAveragePoints (ascending)===");
        labExercises.printByAveragePoints(true, 100);
        System.out.println("===printByAveragePoints (descending)===");
        labExercises.printByAveragePoints(false, 100);
        System.out.println("===failed students===");
        labExercises.failedStudents().forEach(System.out::println);
        System.out.println("===statistics by year");
        labExercises.getStatisticsByYear().entrySet().stream()
                .map(entry -> String.format("%d : %.2f", entry.getKey(), entry.getValue()))
                .forEach(System.out::println);

    }
}

class Student {
    private String index;
    private List<Integer> points;
    private int yearStudying;

    public Student(String index, List<Integer> points) {
        this.index = index;
        this.points = points;
        this.yearStudying = (20 - Integer.parseInt(index.substring(0, 2)));
    }

    public double totalPoints() {
        return (double) points.stream().mapToInt(i -> i).sum() / 10.0;
    }

    public String getIndex() {
        return index;
    }

    public int labsMade () {
        return points.size();
    }

    public int getYearStudying() {
        return yearStudying;
    }

    @Override
    public String toString() {
        return String.format("%s %s %.2f",
                index,
                labsMade() >= 8 ? "YES" : "NO",
                totalPoints());
    }
}

class LabExercises {

    Set<Student> studentsNotValidLabs;
    List<Student> studentsValid;
    Map<Integer, List<Student>> studentsByYear;

    public LabExercises() {
        this.studentsNotValidLabs = new TreeSet<>(Comparator.comparing(Student::getIndex).thenComparing(Student::totalPoints));
        this.studentsValid = new ArrayList<>();
        this.studentsByYear = new HashMap<>();
    }

    public void addStudent (Student student) {
        if (student.labsMade() >= 8) {
            studentsByYear.putIfAbsent(student.getYearStudying(), new ArrayList<>());
            studentsByYear.get(student.getYearStudying()).add(student);
        }
        else
            studentsNotValidLabs.add(student);

        studentsValid.add(student);
    }

    public void printByAveragePoints (boolean ascending, int n) {

        Comparator<Student> comparatorAscending = Comparator.comparing(Student::totalPoints)
                .thenComparing(Student::getIndex);

        studentsValid.stream()
                .sorted(ascending ? comparatorAscending : comparatorAscending.reversed())
                .limit(n)
                .forEach(System.out::println);
    }

    public List<Student> failedStudents () {
        return studentsNotValidLabs.stream().collect(Collectors.toList());
    }

    public Map<Integer,Double> getStatisticsByYear() {
        return studentsByYear
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .mapToDouble(Student::totalPoints)
                                .average().orElse(0.0)
                ));
    }
}