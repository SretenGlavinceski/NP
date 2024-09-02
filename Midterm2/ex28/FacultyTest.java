package Midterm2.ex28;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class OperationNotAllowedException extends Exception {
    public OperationNotAllowedException(String s) {
        super(s);
    }
}

class Course {
    String courseName;
    int term;
    Map<String, Integer> gradesByStudents = new HashMap<>();
    int studentsEnrolled = 0;

    public Course(String courseName, int term) {
        this.term = term;
        this.courseName = courseName;
    }

    public void addStudentGrade (String studentID, int grade) {
        gradesByStudents.put(studentID, grade);
        studentsEnrolled++;
    }

    public int getGradeForStudent (String studentId) {
        return gradesByStudents.get(studentId);
    }

    public double averageGradeForCourse() {
        return gradesByStudents.values().stream().mapToInt(i -> i).average().orElse(5.0);
    }

    public String getCourseName() {
        return courseName;
    }

    public int getStudentsEnrolled() {
        return studentsEnrolled;
    }

    @Override
    public String toString() {
        return String.format("%s %d %.2f",
                courseName,
                studentsEnrolled,
                averageGradeForCourse());
    }
}

class StudentFactory {
    static final int THREE_YEAR_STUDIES = 3;
    static final int FOUR_YEAR_STUDIES = 4;
    static Student createStudent (String studentId, int yearOfStudies) {
        if (yearOfStudies == THREE_YEAR_STUDIES)
            return new ThreeYearStudiesStudent(studentId);
        else if (yearOfStudies == FOUR_YEAR_STUDIES)
            return new FourYearStudiesStudent(studentId);
        else throw new RuntimeException();
    }
}

abstract class Student {
    String studentId;
    Map<Integer, List<Course>> coursesByTerms;
    int yearOfStudies;
    Set<String> courses;

    public Student(String studentId) {
        this.studentId = studentId;
        this.coursesByTerms = new HashMap<>();
        this.courses = new TreeSet<>();
    }

    public void addCourseToTerm (int term, Course course, int grade) throws OperationNotAllowedException {
        if (term > yearOfStudies * 2)
            throw new OperationNotAllowedException(String.format("Term %d is not possible for student with ID %s", term, studentId));

        if (coursesByTerms.get(term).size() == 3)
            throw new OperationNotAllowedException(String.format("Student %s already has 3 grades in term %d", studentId, term));

        coursesByTerms.get(term).add(course);
        courses.add(course.getCourseName());
        course.addStudentGrade(this.studentId, grade);
    }

    public String getStudentId() {
        return studentId;
    }

    public int numberOfGrades () {
        return (int) coursesByTerms
                .values()
                .stream()
                .mapToLong(Collection::size)
                .sum();
    }

    public int getYearOfStudies() {
        return yearOfStudies;
    }

    public double averageGrade() {
        return coursesByTerms
                .values()
                .stream()
                .flatMap(Collection::stream)
                .mapToInt(i -> i.getGradeForStudent(this.studentId))
                .average().orElse(5.0);
    }

    public String graduatedLog() {
        return String.format("Student with ID %s graduated with average grade %.2f in %d years.",
                studentId,
                averageGrade(),
                yearOfStudies);
    }

    public double averageGradeForTerm (int term) {
        return coursesByTerms
                .get(term).stream()
                .mapToInt(course -> course.getGradeForStudent(this.studentId))
                .average().orElse(5.0);
    }

    public String detailedInfo () {
        return String.format("Student: %s\n%sAverage grade: %.2f\nCourses attended: %s",
                studentId,
                coursesByTerms.entrySet()
                        .stream()
                        .map(entry -> String.format("Term %d\nCourses: %d\nAverage grade for term: %.2f\n",
                                entry.getKey(),
                                entry.getValue().size(),
                                averageGradeForTerm(entry.getKey())))
                        .collect(Collectors.joining("")),
                averageGrade(),
                String.join(",", courses));
    }

    public String printStudentSuccess () {
        return String.format("Student: %s Courses passed: %d Average grade: %.2f",
                studentId,
                numberOfGrades(),
                averageGrade());
    }
}

class ThreeYearStudiesStudent extends Student {

    public ThreeYearStudiesStudent(String studentId) {
        super(studentId);
        this.yearOfStudies = 3;
        this.coursesByTerms = new HashMap<>();
        IntStream.range(1, 7).forEach(i -> coursesByTerms.put(i, new ArrayList<>()));
    }
}


class FourYearStudiesStudent extends Student {

    public FourYearStudiesStudent(String studentId) {
        super(studentId);
        this.yearOfStudies = 4;
        this.coursesByTerms = new HashMap<>();
        IntStream.range(1, 9).forEach(i -> coursesByTerms.put(i, new ArrayList<>()));

    }
}

class Faculty {
    Map<String, Student> studentByID;
    Map<String, Course> courses;
    List<String> graduatedStudentsLog;
    static final Comparator<Student> BEST_STUDENTS_COMPARATOR = Comparator.comparing(Student::numberOfGrades)
            .thenComparing(Student::averageGrade)
            .thenComparing(Student::getStudentId).reversed();

    static final Comparator<Course> COURSES_COMPARATOR = Comparator.comparing(Course::getStudentsEnrolled)
            .thenComparing(Course::averageGradeForCourse)
            .thenComparing(Course::getCourseName);
    public Faculty() {
        this.studentByID = new HashMap<>();
        this.courses = new HashMap<>();
        this.graduatedStudentsLog = new ArrayList<>();
    }

    void addStudent(String id, int yearsOfStudies) {
        studentByID.put(id, StudentFactory.createStudent(id, yearsOfStudies));
    }

    void addGradeToStudent(String studentId, int term, String courseName, int grade) throws OperationNotAllowedException {
        Student student = studentByID.get(studentId);
        Course course;
        if (courses.containsKey(courseName))
            course = courses.get(courseName);
        else {
            course = new Course(courseName, term);
            courses.put(courseName, course);
        }

        student.addCourseToTerm(term, course, grade);

        if (student.numberOfGrades() == (student.getYearOfStudies() * 6)) {
            graduatedStudentsLog.add(student.graduatedLog());
            studentByID.remove(studentId);
        }
    }

    String getFacultyLogs() {
        return String.join("\n", graduatedStudentsLog);
    }

    String getDetailedReportForStudent(String id) {
        return studentByID.get(id).detailedInfo();
    }

    void printFirstNStudents(int n) {
        Set<Student> bestStudents = new TreeSet<>(BEST_STUDENTS_COMPARATOR);
        bestStudents.addAll(studentByID.values());

        System.out.println(
                bestStudents
                .stream()
                .limit(n)
                .map(Student::printStudentSuccess)
                .collect(Collectors.joining("\n")));
    }

    void printCourses() {
        Set<Course> coursesSorted = new TreeSet<>(COURSES_COMPARATOR);
        coursesSorted.addAll(courses.values());
        coursesSorted.forEach(System.out::println);
    }
}

public class FacultyTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = sc.nextInt();

        if (testCase == 1) {
            System.out.println("TESTING addStudent AND printFirstNStudents");
            Faculty faculty = new Faculty();
            for (int i = 0; i < 10; i++) {
                faculty.addStudent("student" + i, (i % 2 == 0) ? 3 : 4);
            }
            faculty.printFirstNStudents(10);

        } else if (testCase == 2) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            try {
                faculty.addGradeToStudent("123", 7, "NP", 10);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
            try {
                faculty.addGradeToStudent("1234", 9, "NP", 8);
            } catch (OperationNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        } else if (testCase == 3) {
            System.out.println("TESTING addGrade and exception");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("123", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (int i = 0; i < 4; i++) {
                try {
                    faculty.addGradeToStudent("1234", 1, "course" + i, 10);
                } catch (OperationNotAllowedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else if (testCase == 4) {
            System.out.println("Testing addGrade for graduation");
            Faculty faculty = new Faculty();
            faculty.addStudent("123", 3);
            faculty.addStudent("1234", 4);
            int counter = 1;
            for (int i = 1; i <= 6; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("123", i, "course" + counter, (i % 2 == 0) ? 7 : 8);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            counter = 1;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 3; j++) {
                    try {
                        faculty.addGradeToStudent("1234", i, "course" + counter, (j % 2 == 0) ? 7 : 10);
                    } catch (OperationNotAllowedException e) {
                        System.out.println(e.getMessage());
                    }
                    ++counter;
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("PRINT STUDENTS (there shouldn't be anything after this line!");
            faculty.printFirstNStudents(2);
        } else if (testCase == 5 || testCase == 6 || testCase == 7) {
            System.out.println("Testing addGrade and printFirstNStudents (not graduated student)");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), i % 5 + 6);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            if (testCase == 5)
                faculty.printFirstNStudents(10);
            else if (testCase == 6)
                faculty.printFirstNStudents(3);
            else
                faculty.printFirstNStudents(20);
        } else if (testCase == 8 || testCase == 9) {
            System.out.println("TESTING DETAILED REPORT");
            Faculty faculty = new Faculty();
            faculty.addStudent("student1", ((testCase == 8) ? 3 : 4));
            int grade = 6;
            int counterCounter = 1;
            for (int i = 1; i < ((testCase == 8) ? 6 : 8); i++) {
                for (int j = 1; j < 3; j++) {
                    try {
                        faculty.addGradeToStudent("student1", i, "course" + counterCounter, grade);
                    } catch (OperationNotAllowedException e) {
                        e.printStackTrace();
                    }
                    grade++;
                    if (grade == 10)
                        grade = 5;
                    ++counterCounter;
                }
            }
            System.out.println(faculty.getDetailedReportForStudent("student1"));
        } else if (testCase==10) {
            System.out.println("TESTING PRINT COURSES");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j < ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 3 : 2); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            faculty.printCourses();
        } else if (testCase==11) {
            System.out.println("INTEGRATION TEST");
            Faculty faculty = new Faculty();
            for (int i = 1; i <= 10; i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= ((j % 2 == 1) ? 2 : 3); k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }

            }

            for (int i=11;i<15;i++) {
                faculty.addStudent("student" + i, ((i % 2) == 1 ? 3 : 4));
                int courseCounter = 1;
                for (int j = 1; j <= ((i % 2 == 1) ? 6 : 8); j++) {
                    for (int k = 1; k <= 3; k++) {
                        int grade = sc.nextInt();
                        try {
                            faculty.addGradeToStudent("student" + i, j, ("course" + courseCounter), grade);
                        } catch (OperationNotAllowedException e) {
                            System.out.println(e.getMessage());
                        }
                        ++courseCounter;
                    }
                }
            }
            System.out.println("LOGS");
            System.out.println(faculty.getFacultyLogs());
            System.out.println("DETAILED REPORT FOR STUDENT");
            System.out.println(faculty.getDetailedReportForStudent("student2"));
            try {
                System.out.println(faculty.getDetailedReportForStudent("student11"));
                System.out.println("The graduated students should be deleted!!!");
            } catch (NullPointerException e) {
                System.out.println("The graduated students are really deleted");
            }
            System.out.println("FIRST N STUDENTS");
            faculty.printFirstNStudents(10);
            System.out.println("COURSES");
            faculty.printCourses();
        }
    }
}
