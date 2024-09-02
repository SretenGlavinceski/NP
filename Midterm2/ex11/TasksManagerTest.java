package Midterm2.ex11;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}

interface ITask {
    int getPriority();

    String getCategory();

    LocalDateTime getDeadLine();
}

// [категорија][име_на_задача],[oпис],[рок_за_задачата],[приоритет]
class SimpleTask implements ITask {
    String name;
    String description;
    String category;

    public SimpleTask(String category, String name, String description) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public LocalDateTime getDeadLine() {
        return LocalDateTime.MAX;
    }
    //Task{name='NP', description='lab 1 po NP'}

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

abstract class TaskDecorator implements ITask {
    ITask task;

    public TaskDecorator(ITask task) {
        this.task = task;
    }
}

class PriorityTask extends TaskDecorator {
    int priority;

    public PriorityTask(ITask task, int priority) {
        super(task);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getCategory() {
        return task.getCategory();
    }

    @Override
    public LocalDateTime getDeadLine() {
        return task.getDeadLine();
    }

    //Task{name='NP', description='solve all exercises', priority=3}

    @Override
    public String toString() {
        return task.toString().substring(0, task.toString().length() - 1) + ", priority=" + priority + "}";
    }
}

class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(String s) {
        super(s);
    }
}

class TimeTaskDecorator extends TaskDecorator {
    LocalDateTime deadLine;
    static final LocalDateTime invalidDateException = LocalDateTime.of(LocalDate.of(2020, 6, 2), LocalTime.of(0,0));
    public TimeTaskDecorator(ITask task, LocalDateTime deadLine) throws DeadlineNotValidException {
        super(task);
        if (deadLine.isBefore(invalidDateException))
            throw new DeadlineNotValidException(String.format("The deadline %s has already passed", deadLine));
        this.deadLine = deadLine;
    }

    @Override
    public int getPriority() {
        return task.getPriority();
    }

    @Override
    public String getCategory() {
        return task.getCategory();
    }

    @Override
    public LocalDateTime getDeadLine() {
        return deadLine;
    }

    @Override
    public String toString() {
        return task.toString().substring(0, task.toString().length() - 1) + ", deadline=" + deadLine.toString() + "}";
    }
}

class TaskFactory {
    public static ITask createTask(String line) throws DeadlineNotValidException {
        String[] parts = line.split(",");
        ITask base = new SimpleTask(parts[0], parts[1], parts[2]);

        if (parts.length == 3) {
            return base;
        } else if (parts.length == 4) {
            try {
                int priority = Integer.parseInt(parts[3]);
                return new PriorityTask(base, priority);
            } catch (Exception e) {
                LocalDateTime date = LocalDateTime.parse(parts[3]);
                return new TimeTaskDecorator(base, date);
            }
        } else  {
            int priority = Integer.parseInt(parts[4]);
            LocalDateTime date = LocalDateTime.parse(parts[3]);
            return new PriorityTask(new TimeTaskDecorator(base, date), priority);
        }
    }
}

class TaskManager {
    Map<String, List<ITask>> tasksByCategory;

    public TaskManager() {
        this.tasksByCategory = new TreeMap<>();
    }

    public void readTasks(InputStream inputStream) {
        tasksByCategory = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .map(line -> {
                    try {
                        return TaskFactory.createTask(line);
                    } catch (DeadlineNotValidException e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                    ITask::getCategory,
                    TreeMap::new,
                    Collectors.toList()
        ));
    }

    public void printTasks(OutputStream os, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(os);

        Comparator<ITask> simpleComparator = Comparator.comparing(task -> Duration.between(LocalDateTime.now(), task.getDeadLine()));
        Comparator<ITask> priorityComparator = Comparator.comparing(ITask::getPriority).thenComparing(task -> Duration.between(LocalDateTime.now(), task.getDeadLine()));

        if (includeCategory) {
            tasksByCategory.forEach((key, value) ->
                    pw.println(String.format("%s\n%s",
                    key.toUpperCase(),
                    value.stream()
                            .sorted(includePriority ? priorityComparator : simpleComparator)
                            .map(Object::toString).collect(Collectors.joining("\n")))));
        } else
            tasksByCategory.values().stream()
                    .flatMap(Collection::stream)
                    .sorted(includePriority ? priorityComparator : simpleComparator)
                    .map(Object::toString).forEach(pw::println);

        pw.flush();
    }
}
