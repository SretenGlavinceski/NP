package Midterm2.ex22;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;


public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
//        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            LocalDateTime date = LocalDateTime.parse(parts[2], dtf);

            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        String nl = scanner.nextLine();
        LocalDateTime date = LocalDateTime.parse(nl, dtf);
//        LocalDateTime date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}

class Event {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyy HH:mm");
    String name;
    String location;
    LocalDateTime date;

    public Event(String name, String location, LocalDateTime date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("%s at %s, %s",
                date.format(formatter),
                location,
                name);
    }
}

class WrongDateException extends Exception {
    public WrongDateException(String s) {
        super(s);
    }
}

class EventCalendar {
    private static final DateTimeFormatter exceptionFormatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss 'UTC' yyyy");
    int yearCalendar;
    Map<Integer, Set<Event>> eventsByDate;
    Map<Integer, Integer> monthlyEvents;

    public EventCalendar(int year) {
        this.eventsByDate = new HashMap<>();
        this.yearCalendar = year;
        this.monthlyEvents = new HashMap<>();
        IntStream.range(1, 13).forEach(i -> monthlyEvents.put(i, 0));
    }

    public void addEvent(String name, String location, LocalDateTime date) throws WrongDateException {
        if (date.getYear() != yearCalendar)
            throw new WrongDateException(String.format("Wrong date: %s", date.format(exceptionFormatter)));

        eventsByDate.putIfAbsent(date.getDayOfYear(), new TreeSet<>(
                Comparator.comparing(Event::getDate)
                .thenComparing(Event::getName)));

        eventsByDate.get(date.getDayOfYear()).add(new Event(name, location, date));

        monthlyEvents.put(date.getMonth().getValue(), monthlyEvents.get(date.getMonth().getValue()) + 1);
    }

    public void listEvents(LocalDateTime date) {
        if (!eventsByDate.containsKey(date.getDayOfYear()))
            System.out.println("No events on this day!");
        else
            eventsByDate.get(date.getDayOfYear()).forEach(System.out::println);
    }

    public void listByMonth() {
        monthlyEvents.forEach((key, value) -> System.out.println(key + " : " + value));
    }
}

