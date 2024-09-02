package Midterm2.ex20;

import java.io.*;
import java.util.*;

public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}

class Day {
    private int dayOfYear;
    List<Double> temperaturesCelsius;
    List<Double> temperaturesFahrenheit;

    public Day(String line) {
        this.temperaturesCelsius = new ArrayList<>();
        this.temperaturesFahrenheit = new ArrayList<>();

        addMeasurements(line);
    }

    private void addMeasurements (String line) {
        String [] parts = line.split("\\s+");
        this.dayOfYear = Integer.parseInt(parts[0]);

        Arrays.stream(parts).skip(1).forEach(temp -> {
            Double value = Double.parseDouble(temp.substring(0, temp.length() - 1));
            if (temp.contains("C"))
                temperaturesCelsius.add(value);
            else
                temperaturesFahrenheit.add(value);
        });
    }
    private DoubleSummaryStatistics getStatistics (char scale) {

        List<Double> allTemps;
        if (scale == 'C') {
            allTemps = new ArrayList<>(temperaturesCelsius);
            temperaturesFahrenheit.forEach(t -> allTemps.add(((t - 32.0) * 5.0) / 9.0));
        } else {
            allTemps = new ArrayList<>(temperaturesFahrenheit);
            temperaturesCelsius.forEach(t -> allTemps.add(((t * 9.0) / 5.0) + 32.0));
        }


        return allTemps.stream().mapToDouble(i -> i).summaryStatistics();

    }

    public String display (char scale) {
        DoubleSummaryStatistics ds = getStatistics(scale);

        return String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",
                    dayOfYear,
                    ds.getCount(),
                    ds.getMin(),
                    scale,
                    ds.getMax(),
                    scale,
                    ds.getAverage(),
                    scale);
    }
}

class DailyTemperatures {

    Map<Integer, Day> dailyTemperatures;

    DailyTemperatures() {
        this.dailyTemperatures = new TreeMap<>();
    }
    void readTemperatures(InputStream inputStream) {
        new BufferedReader(new InputStreamReader(inputStream))
                .lines().forEach(line -> {
                    String [] parts = line.split("\\s+");
                    dailyTemperatures.put(Integer.parseInt(parts[0]), new Day(line));
                });
    }
    void writeDailyStats(OutputStream outputStream, char scale){
        PrintWriter pw = new PrintWriter(outputStream);

        dailyTemperatures.values().forEach(temp -> pw.println(temp.display(scale)));

        pw.flush();
    }
}


