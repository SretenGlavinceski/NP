//package Midterm2.ex6;
//
//
//import java.io.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class PayrollSystemTest {
//
//    public static void main(String[] args) {
//
//        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
//        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
//        for (int i = 1; i <= 10; i++) {
//            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
//            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
//        }
//
//        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
//
//        System.out.println("READING OF THE EMPLOYEES DATA");
//        payrollSystem.readEmployees(System.in);
//
//        System.out.println("PRINTING EMPLOYEES BY LEVEL");
//        Set<String> levels = new LinkedHashSet<>();
//        for (int i = 5; i <= 10; i++) {
//            levels.add("level" + i);
//        }
//        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
//        result.forEach((level, employees) -> {
//            System.out.println("LEVEL: " + level);
//            System.out.println("Employees: ");
//            employees.forEach(System.out::println);
//            System.out.println("------------");
//        });
//    }
//}
//
//interface IEmployee {
//    double totalSalary();
//}
//
//abstract class Employee implements IEmployee {
//    String id;
//    String level;
//    double paycheckByLevel;
//
//    public Employee(String id, String level, double paycheckByLevel) {
//        this.id = id;
//        this.level = level;
//        this.paycheckByLevel = paycheckByLevel;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public String getLevel() {
//        return level;
//    }
//
//}
//
//class HourlyEmployee extends Employee {
//    double hoursWork;
//    static final int BASE_HOURS = 40;
//    static final double COEF_BONUS_HOURS = 1.5;
//
//    public HourlyEmployee(String id, String level, double paycheckByLevel, double hoursWork) {
//        super(id, level, paycheckByLevel);
//        this.hoursWork = hoursWork;
//    }
//
//
//    @Override
//    public double totalSalary() {
//        return hoursWork > 40 ?
//                BASE_HOURS * paycheckByLevel + (hoursWork - BASE_HOURS) * (COEF_BONUS_HOURS * paycheckByLevel) :
//                hoursWork * paycheckByLevel;
//    }
//
//    //Employee ID: 157f3d Level: level10 Salary: 2390.72 Regular hours: 40.00 Overtime hours: 23.14
//    @Override
//    public String toString() {
//        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
//                id,
//                level,
//                totalSalary(),
//                hoursWork > BASE_HOURS ? 40.0 : hoursWork,
//                Math.max(0.0, hoursWork - BASE_HOURS));
//    }
//}
//
//class FreelanceEmployee extends Employee {
//    List<Integer> ticketPoints;
//
//    public FreelanceEmployee(String id, String level, double paycheckByLevel, List<Integer> ticketPoints) {
//        super(id, level, paycheckByLevel);
//        this.ticketPoints = ticketPoints;
//    }
//
//
//    @Override
//    public double totalSalary() {
//        return ticketPoints.stream().mapToInt(i -> i).sum() * paycheckByLevel;
//    }
//
//    //Employee ID: 952c98 Level: level8 Salary: 400.00 Tickets count: 4 Tickets points: 16
//    @Override
//    public String toString() {
//        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
//                id,
//                level,
//                totalSalary(),
//                ticketPoints.size(),
//                ticketPoints.stream().mapToInt(i -> i).sum());
//    }
//}
//
//class EmployeeFactory {
//    static final String HOURLY_EMPLOYEE = "H";
//    static final String FREELANCE_EMPLOYEE = "F";
//
//    static Employee generateEmployee(String s, Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
//        String[] parts = s.split(";");
//        String typeEmployee = parts[0];
//        if (typeEmployee.equals(HOURLY_EMPLOYEE)) {
//            return new HourlyEmployee(parts[1], parts[2], hourlyRateByLevel.get(parts[2]), Double.parseDouble(parts[3]));
//        } else if (typeEmployee.equals(FREELANCE_EMPLOYEE)) {
//            List<Integer> points = Arrays.stream(parts)
//                    .skip(3)
//                    .map(Integer::parseInt)
//                    .collect(Collectors.toList());
//            return new FreelanceEmployee(parts[1], parts[2], ticketRateByLevel.get(parts[2]), points);
//        } else
//            throw new RuntimeException();
//    }
//}
//
//class PayrollSystem {
//    Map<String, Double> hourlyRateByLevel;
//    Map<String, Double> ticketRateByLevel;
//    Map<String, Set<Employee>> employeesByLevel;
//
//    PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
//        this.hourlyRateByLevel = hourlyRateByLevel;
//        this.ticketRateByLevel = ticketRateByLevel;
//        employeesByLevel = new TreeMap<>();
//    }
//
//    void readEmployees(InputStream is) {
//        new BufferedReader(new InputStreamReader(is))
//                .lines()
//                .forEach(line -> {
//                    Employee employee = EmployeeFactory.generateEmployee(
//                            line,
//                            hourlyRateByLevel,
//                            ticketRateByLevel
//                    );
//
//                    employeesByLevel.putIfAbsent(
//                            employee.getLevel(),
//                            new TreeSet<>(Comparator.comparing(IEmployee::totalSalary).reversed())
//                    );
//
//                    employeesByLevel.get(employee.getLevel()).add(employee);
//                });
//    }
//
//    Map<String, Set<Employee>> printEmployeesByLevels(OutputStream os, Set<String> levels) {
//        List<String> strings = employeesByLevel.keySet()
//                .stream()
//                .collect(Collectors.toList());
//
//        strings.stream().filter(i -> !levels.contains(i)).forEach(i -> employeesByLevel.remove(i));
//
//        return employeesByLevel;
//    }
//}