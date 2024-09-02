package Midterm1.ex21;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}

class Racer {
    String name;
    List<LocalTime> times;

    public Racer(String s) {
        times = new ArrayList<>();
        String[] parts = s.split("\\s+");
        name = parts[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

        for (int i = 1; i < parts.length; i++)
            times.add(LocalTime.parse("00:0" + parts[i], formatter));
    }

    LocalTime bestTime() {
        return times.stream().min(LocalTime::compareTo).orElse(null);
    }
}

class F1Race {
    List<Racer> racers;

    public F1Race() {
        racers = new ArrayList<>();
    }

    void readResults(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(l -> racers.add(new Racer(l)));
    }

    void printSorted(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        racers = racers.stream()
                .sorted(Comparator.comparing(Racer::bestTime)).collect(Collectors.toList());

        for (int i = 0; i < racers.size(); i++)
            pw.println(String.format("%d. %-10s%10s", i + 1,
                    racers.get(i).name,
                    racers.get(i).bestTime().toString().substring(4).replace(".", ":")));

        pw.flush();
    }
}