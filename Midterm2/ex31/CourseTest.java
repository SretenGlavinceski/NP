package Midterm2.ex31;//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}

class UnwantedActivityTypeException extends Exception {
    public UnwantedActivityTypeException() {
    }
}

class Student {
    static final int MAX_MIDTERM_POINTS = 100;
    static final int MAX_LAB_POINTS = 10;
    String index;
    String name;
    int midtermFirst;
    int midtermSecond;
    int labPoints;

    public Student(String index, String name) {
        this.index = index;
        this.name = name;
        midtermFirst = 0;
        midtermSecond = 0;
        labPoints = 0;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public int getMidtermFirst() {
        return midtermFirst;
    }

    public int getMidtermSecond() {
        return midtermSecond;
    }

    public int getLabPoints() {
        return labPoints;
    }

    public double totalPoints() {
        return midtermFirst * 0.45 + midtermSecond * 0.45 + labPoints;
    }

    public int getGrade() {
        if (totalPoints() < 50.0)
            return 5;
        else if (totalPoints() < 60.0)
            return 6;
        else if (totalPoints() < 70.0)
            return 7;
        else if (totalPoints() < 80.0)
            return 8;
        else if (totalPoints() < 90.0)
            return 9;
        else if (totalPoints() <= 100)
            return 10;
        else
            throw new RuntimeException();
    }

    public void addPointsToActivity(String activity, int points) throws UnwantedActivityTypeException {
        if (points > MAX_MIDTERM_POINTS && (activity.equals("midterm1") || activity.equals("midterm2"))) {
            throw new UnwantedActivityTypeException();
        }

        if (points > MAX_LAB_POINTS && activity.equals("labs")) {
            throw new UnwantedActivityTypeException();
        }

        switch (activity) {
            case "midterm1" -> midtermFirst = points;
            case "midterm2" -> midtermSecond = points;
            case "labs" -> labPoints = points;
            default -> throw new UnwantedActivityTypeException();
        }

    }

    //ID: 151004 Name: NameAndLastName#3 First midterm: 97 Second midterm 88 Labs: 10 Summary points: 93.25 Grade: 10↩
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d",
                index,
                name,
                midtermFirst,
                midtermSecond,
                labPoints,
                totalPoints(),
                getGrade());
    }
}

class AdvancedProgrammingCourse {
    static final double PASSING_POINTS = 50.0;
    static final Comparator<Student> BEST_PASSED_STUDENTS = Comparator.comparing(Student::totalPoints).reversed();
    Map<String, Student> studentsByID;

    public AdvancedProgrammingCourse() {
        this.studentsByID = new HashMap<>();
    }

    public void addStudent(Student s) {
        studentsByID.put(s.getIndex(), s);
    }

    public void updateStudent(String idNumber, String activity, int points) {
        try {
            studentsByID.get(idNumber).addPointsToActivity(activity, points);
        } catch (UnwantedActivityTypeException e) {
            // TODO do nothing???
        }
    }

    public List<Student> getFirstNStudents(int n) {
        return studentsByID.values().stream()
                .sorted(BEST_PASSED_STUDENTS)
                .limit(n)
                .collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> result = new HashMap<>();
        IntStream.range(5, 11).forEach(i -> result.put(i, 0));

        studentsByID.values()
                .stream().mapToInt(Student::getGrade)
                .forEach(grade -> result.computeIfPresent(grade, (k, v) -> v + 1));

        return result;
    }

    public void printStatistics() {
        //Count: 1 Min: 79.10 Average: 79.10 Max: 79.10
        DoubleSummaryStatistics ds = studentsByID.values()
                .stream()
                .filter(i -> i.totalPoints() >= PASSING_POINTS)
                .mapToDouble(Student::totalPoints)
                .summaryStatistics();

        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f\n",
                ds.getCount(),
                ds.getMin(),
                ds.getAverage(),
                ds.getMax());
    }
}
