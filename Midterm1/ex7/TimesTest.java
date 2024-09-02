package Midterm1.ex7;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}

class UnsupportedFormatException extends Exception {
    public UnsupportedFormatException(String s) {
        super(s);
    }
}

class InvalidTimeException extends Exception {
    public InvalidTimeException(String s) {
        super(s);
    }
}

class Time {
    LocalTime time;

    public Time(String s) throws InvalidTimeException {
        s = s.replace(".", ":");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (s.length() == 4)
            s = "0" + s;
        time = LocalTime.parse(s, formatter);
        if (time == null)
            throw new InvalidTimeException(s);

    }

    public String regularTimeFormat() {
        String printTime;
        if (time.toString().startsWith("0"))
            printTime = time.toString().substring(1);
        else printTime = time.toString();
        return String.format("%5s", printTime);
    }

    public String AMPMtimeFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        String printTime = time.format(formatter);

        if (printTime.startsWith("0"))
            printTime = printTime.substring(1);

        return String.format("%8s", printTime);
    }

    public LocalTime getTime() {
        return time;
    }
}

class TimeTable {
    List<Time> times;

    TimeTable() {
        times = new ArrayList<>();
    }

    void readTimes(InputStream inputStream) throws UnsupportedFormatException, InvalidTimeException {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (!line.contains(":") && !line.contains("."))
                throw new UnsupportedFormatException(line);

            times.add(new Time(line));
        }
        scanner.close();
    }

    void writeTimes(OutputStream outputStream, TimeFormat format) {
        PrintWriter pw = new PrintWriter(outputStream);

        if (format.equals(TimeFormat.FORMAT_24))
            times.stream().sorted(Comparator.comparing(Time::getTime)).forEach(time -> pw.println(time.regularTimeFormat()));
        else
            times.stream().sorted(Comparator.comparing(Time::getTime)).forEach(time -> pw.println(time.AMPMtimeFormat()));

        pw.flush();
    }
}