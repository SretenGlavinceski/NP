package Midterm2.ex7;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PayrollSystemTest2 {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 11 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5.5 + i * 2.5);
        }

        Scanner sc = new Scanner(System.in);

        int employeesCount = Integer.parseInt(sc.nextLine());

        PayrollSystem ps = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);
        Employee emp = null;
        for (int i = 0; i < employeesCount; i++) {
            try {
                emp = ps.createEmployee(sc.nextLine());
            } catch (BonusNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        int testCase = Integer.parseInt(sc.nextLine());

        switch (testCase) {
            case 1: //Testing createEmployee
                if (emp != null)
                    System.out.println(emp);
                break;
            case 2: //Testing getOvertimeSalaryForLevels()
                ps.getOvertimeSalaryForLevels().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Overtime salary: %.2f\n", level, overtimeSalary);
                });
                break;
            case 3: //Testing printStatisticsForOvertimeSalary()
                ps.printStatisticsForOvertimeSalary();
                break;
            case 4: //Testing ticketsDoneByLevel
                ps.ticketsDoneByLevel().forEach((level, overtimeSalary) -> {
                    System.out.printf("Level: %s Tickets by level: %d\n", level, overtimeSalary);
                });
                break;
            case 5: //Testing getFirstNEmployeesByBonus (int n)
                ps.getFirstNEmployeesByBonus(Integer.parseInt(sc.nextLine())).forEach(System.out::println);
                break;
        }

    }
}

enum TYPE_EMPLOYEE {
    H, // hourly
    F // freelance
}

class BonusNotAllowedException extends Exception {
    public BonusNotAllowedException(String s) {
        super(s);
    }
}

abstract class Employee {
    private static final double MAX_FIXED_BONUS = 1000.0;
    private static final double MAX_PERCENTAGE_BONUS = 20.0;
    private final String ID;
    private final String level;
    protected double totalSalary;
    TYPE_EMPLOYEE typeEmployee;
    private double bonus;

    public Employee(String ID, String level) {
        this.ID = ID;
        this.level = level;
        totalSalary = 0;
        this.bonus = -1.0;
    }

    static public Employee generateEmployee(String line) {
        String[] parts = line.split(";");
        String type = parts[0];
        String ID = parts[1];
        String level = parts[2];

        if (type.equals(TYPE_EMPLOYEE.H.toString()))
            return new HourlyEmployee(ID, level, Double.parseDouble(parts[3]));

        int summarize = Arrays.stream(parts).skip(3).mapToInt(Integer::parseInt).sum();
        return new FreelanceEmployee(ID, level, summarize, parts.length - 3);
    }

    public void setBonus(String bonus) throws BonusNotAllowedException {
        double value;
        if (bonus.contains("%")) {
            value = Double.parseDouble(bonus.substring(0, bonus.length() - 1));
            if (value > MAX_PERCENTAGE_BONUS)
                throw new BonusNotAllowedException(String.format("Bonus of %.2f%% is not allowed", value));
            this.bonus = totalSalary * ((value) / 100.0);
        } else {
            value = Double.parseDouble(bonus);
            if (value > MAX_FIXED_BONUS)
                throw new BonusNotAllowedException(String.format("Bonus of %d$ is not allowed", (int) value));
            this.bonus = value;
        }
    }

    abstract public void setTotalSalary(double totalSalary);

    abstract public double getOvertimeSalary();

    abstract public int getTicketPoints();

    public String getLevel() {
        return level;
    }

    public String getID() {
        return ID;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public double getBonus() {
        return Math.max(bonus, 0);
    }

    public String addBonus() {
        if (getBonus() <= 0.0)
            return "";
        return String.format(" Bonus: %.2f", getBonus());
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f ",
                getID(),
                getLevel(),
                getTotalSalary() + Math.max(getBonus(), 0));
    }
}

class HourlyEmployee extends Employee {

    private static final double BASIC_HOURS = 40.0;
    private static final double OVERTIME_RATE = 1.5;
    private final double totalHours;
    private double overtimePay;

    public HourlyEmployee(String ID, String level, double totalHours) {
        super(ID, level);
        this.totalHours = totalHours;
        this.typeEmployee = TYPE_EMPLOYEE.H;
    }

    @Override
    public void setTotalSalary(double salaryRate) {
        this.overtimePay = (salaryRate * OVERTIME_RATE) * (totalHours - BASIC_HOURS);
        if (totalHours > BASIC_HOURS)
            totalSalary = BASIC_HOURS * salaryRate + overtimePay;
        else
            totalSalary = Math.min(BASIC_HOURS, totalHours) * salaryRate;
    }

    @Override
    public double getOvertimeSalary() {
        return Math.max(0, overtimePay);
    }

    @Override
    public int getTicketPoints() {
        return 0;
    }


    @Override
    public String toString() {
        return super.toString() + String.format("Regular hours: %.2f Overtime hours: %.2f",
                Math.min(BASIC_HOURS, totalHours),
                Math.max(0.0, totalHours - BASIC_HOURS)) + addBonus();

    }
}

class FreelanceEmployee extends Employee {
    private final int ticketPoints;
    private final int ticketCount;

    public FreelanceEmployee(String ID, String level, int ticketPoints, int count) {
        super(ID, level);
        this.ticketPoints = ticketPoints;
        this.typeEmployee = TYPE_EMPLOYEE.F;
        this.ticketCount = count;
    }

    @Override
    public void setTotalSalary(double salaryRate) {
        totalSalary = salaryRate * ticketPoints;
    }

    @Override
    public double getOvertimeSalary() {
        return -1;
    }

    public int getTicketPoints() {
        return ticketCount;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Tickets count: %d Tickets points: %d",
                ticketCount,
                ticketPoints) + addBonus();

    }
}

class PayrollSystem {
    List<Employee> employees;
    Map<String, Double> hourlyRateByLevel;
    Map<String, Double> ticketRateByLevel;

    PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.employees = new ArrayList<>();
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    private void setSalaryForTypeOfEmployee(Employee employee) {
        if (employee.typeEmployee.equals(TYPE_EMPLOYEE.F))
            employee.setTotalSalary(ticketRateByLevel.get(employee.getLevel()));
        else
            employee.setTotalSalary(hourlyRateByLevel.get(employee.getLevel()));

    }

    public Employee createEmployee(String string) throws BonusNotAllowedException {
        String[] parts = string.split("\\s+");
        Employee employee = Employee.generateEmployee(parts[0]);

        setSalaryForTypeOfEmployee(employee);

        if (parts.length > 1)
            employee.setBonus(parts[1]);

        employees.add(employee);
        return employee;
    }

    Map<String, Double> getOvertimeSalaryForLevels() {

        Map<String, Double> map = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        Collectors.summingDouble(Employee::getOvertimeSalary)
                ));

        map.entrySet().removeIf(entry -> entry.getValue() == -1);

        return map;
    }

    void printStatisticsForOvertimeSalary() {

        DoubleSummaryStatistics ds = employees
                .stream()
                .filter(i -> i.getOvertimeSalary() != -1)
                .mapToDouble(Employee::getOvertimeSalary)
                .summaryStatistics();

        //Statistics for overtime salary: Min: 285.98 Average: 774.49 Max: 1250.04 Sum: 3097.94

        System.out.printf("Statistics for overtime salary: Min: %.2f Average: %.2f Max: %.2f Sum: %.2f%n",
                ds.getMin(),
                ds.getAverage(),
                ds.getMax(),
                ds.getSum()
        );
    }

    Map<String, Integer> ticketsDoneByLevel() {

        return employees.stream()
                .filter(i -> i.typeEmployee.equals(TYPE_EMPLOYEE.F))
                .collect(Collectors.groupingBy(
                        Employee::getLevel,
                        Collectors.summingInt(Employee::getTicketPoints)
                ));
    }

    Collection<Employee> getFirstNEmployeesByBonus(int n) {
        return employees.stream()
                .sorted(Comparator.comparing(Employee::getBonus).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

}