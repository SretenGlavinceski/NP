package Midterm1.ex14;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

class Measurement {
    float temperature;
    float wind;
    float humidity;
    float visibility;
    Date date;

    public Measurement(float temperature, float wind, float humidity, float visibility, Date date) {
        this.temperature = temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.visibility = visibility;
        this.date = date;
    }

    public long compareInDays(Measurement measurement) {
        return Duration.between(date.toInstant(), measurement.date.toInstant()).toDays();
    }

    public long compareInMinutes(Measurement measurement) {
        return Duration.between(date.toInstant(), measurement.date.toInstant()).toMinutes();
    }

    @Override
    public String toString() {
        String dateToPrint = date.toString().replace("UTC", "GMT");
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s",
                temperature,
                wind,
                humidity,
                visibility,
                dateToPrint);
    }
}

class WeatherStation {
    int days;
    List<Measurement> measurements;

    WeatherStation(int days) {
        measurements = new ArrayList<>();
        this.days = days;
    }

    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date) {
        Measurement measurement = new Measurement(temperature, wind, humidity, visibility, date);

        measurements = measurements.stream().filter(i -> i.compareInDays(measurement) < days)
                .collect(Collectors.toList());

        if (measurements.stream().anyMatch(i -> i.compareInMinutes(measurement) < 2.5))
            return;

        measurements.add(new Measurement(temperature, wind, humidity, visibility, date));
    }

    public int total() {
        return measurements.size();
    }

    public void status(Date from, Date to) {

        List<Measurement> filteredList =
                measurements.stream()
                        .filter(i -> i.date.compareTo(from) >= 0 && i.date.compareTo(to) <= 0).toList();

        if (filteredList.isEmpty())
            throw new RuntimeException();

        filteredList.forEach(System.out::println);

        System.out.printf("Average temperature: %.2f\n%n",
                filteredList.stream().mapToDouble(i -> i.temperature)
                        .average().orElse(0));
    }
}