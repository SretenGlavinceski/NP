package Midterm2.ex26;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;


public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

class Student {
    String code;
    String study;
    List<Integer> grades;

    public Student(String s) {
        String[] parts = s.split("\\s+");
        this.code = parts[0];
        this.study = parts[1];
        this.grades = new ArrayList<>();

        for (int i = 2; i < parts.length; i++) {
            grades.add(Integer.parseInt(parts[i]));
        }
    }

    public String getCode() {
        return code;
    }

    public String getStudy() {
        return study;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    public double getAverage() {
        return grades.stream().mapToInt(i -> i).average().orElse(5.0);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", code, getAverage());
    }
}

class UniProgram {
    List<Student> students;
    String name;

    public UniProgram(String name, List<Student> students) {
        this.students = students;
        this.name = name;
    }

    public int getStat(int n) {
        return (int) students
                .stream()
                .map(Student::getGrades)
                .flatMap(Collection::stream)
                .filter(i -> i == n)
                .count();
    }

    public String starsDisplay (int n) {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, n % 10 == 0 ? n / 10 : (n / 10) + 1).forEach(i -> sb.append("*"));
        return sb.toString();
    }

    public long getTENS () {
        return getStat(10);
    }

    @Override
    public String toString() {
        return String.format("%s\n" +
                " 6 | %s(%d)\n" +
                " 7 | %s(%d)\n" +
                " 8 | %s(%d)\n" +
                " 9 | %s(%d)\n" +
                "10 | %s(%d)",
                name,
                starsDisplay(getStat(6)), getStat(6),
                starsDisplay(getStat(7)), getStat(7),
                starsDisplay(getStat(8)), getStat(8),
                starsDisplay(getStat(9)), getStat(9),
                starsDisplay(getStat(10)), getStat(10));
    }
}

class StudentRecords {
    private static final Comparator<Student> studentComparator =
            Comparator.comparing(Student::getAverage).reversed().thenComparing(Student::getCode);
    private static final Comparator<UniProgram> typeOfStudyComparator =
            Comparator.comparing(UniProgram::getTENS).reversed();
    Map<String, Set<Student>> studies;
    Set<UniProgram> typeOfStudy;

    StudentRecords() {
        this.studies = new TreeMap<>();
        this.typeOfStudy = new TreeSet<>(typeOfStudyComparator);
    }

    int readRecords(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        List<Student> students = new ArrayList<>();
        br.lines().forEach(line -> students.add(new Student(line)));

        for (Student student : students) {
            studies.putIfAbsent(student.getStudy(), new TreeSet<>(studentComparator));
            studies.get(student.getStudy()).add(student);
        }

        for (String s : studies.keySet())
            typeOfStudy.add(new UniProgram(s, new ArrayList<>(studies.get(s))));

        return students.size();
    }

    void writeTable(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        for (String study : studies.keySet()) {
            pw.println(study);
            studies.get(study).forEach(pw::println);
        }

        pw.flush();
    }

    void writeDistribution(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        typeOfStudy.forEach(pw::println);
        pw.flush();
    }
}
