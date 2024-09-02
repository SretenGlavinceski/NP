package Midterm1.ex19;

import java.io.InputStream;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}

class PartsSubtitles {
    private static final String formatTime = " --> ";
    String id;
    LocalTime start;
    LocalTime end;
    String text;

    public PartsSubtitles(String id, String date, String text) {
        String [] parts = date.split(formatTime);
        String startDate = parts[0];
        String endDate = parts[1];
        this.id = id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");
        start = LocalTime.parse(startDate, formatter);
        end = LocalTime.parse(endDate, formatter);
        this.text = text;
    }

    public void shiftDates(int ms) {
        start = start.plus(ms, ChronoUnit.MILLIS);
        end = end.plus(ms, ChronoUnit.MILLIS);
    }

    public String fullDate() {
        String s1 = start.format(DateTimeFormatter.ofPattern("HH:mm:ss,SSS"));
        String s2 = end.format(DateTimeFormatter.ofPattern("HH:mm:ss,SSS"));
        return String.format("%s%s%s", s1, formatTime, s2);
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s", id, fullDate(), text);
    }
}

class Subtitles {
    List<PartsSubtitles> subtitles;

    public Subtitles() {
        subtitles = new ArrayList<>();
    }

    int loadSubtitles(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String part1 = scanner.nextLine();
            String part2 = scanner.nextLine();
            String part3 = scanner.nextLine();
            StringBuilder sb = new StringBuilder();
            sb.append(part3).append("\n");
            while (scanner.hasNextLine()) {
                String part4 = scanner.nextLine();
                if (part4.isEmpty()) {
                    break;
                }
                sb.append(part4).append("\n");
            }
            subtitles.add(new PartsSubtitles(part1, part2, sb.toString()));
        }
        return subtitles.size();
    }

    void print() {
        subtitles.forEach(System.out::println);
    }

    void shift(int ms) {
        subtitles.forEach(i -> i.shiftDates(ms));
    }
}