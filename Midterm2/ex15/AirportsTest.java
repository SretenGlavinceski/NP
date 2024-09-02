package Midterm2.ex15;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}

class Flight {
    String from;
    String to;
    int time;
    int duration;

    public Flight(String from, String to, int time, int duration) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    public LocalTime flightLiftOff() {
        return LocalTime.of(time / 60, time - (time / 60) * 60);
    }

    public LocalTime flightLanding() {
        int totalTime = time + duration;

        int days = (totalTime / 60) / 24;
        int hours = (totalTime - days * 24 * 60) / 60;
        int minutes = (totalTime - days * 24 * 60 - hours * 60);

        return LocalTime.of(hours, minutes);
    }

    public String flightDurationString() {

        int hours = duration / 60;
        int minutes = duration - (hours * 60);

        if (duration + time > 24 * 60)
            return String.format("+1d %dh%02dm", hours, minutes);

        return String.format("%dh%02dm", hours, minutes);
    }

    @Override
    public String toString() {
        return String.format("%s-%s %s-%s %s",
                from, to, flightLiftOff(), flightLanding(), flightDurationString());
    }
}

class SingleAirport {
    String name;
    String country;
    String code;
    int passengers;
    Set<Flight> flightFromThisAirport;
    Set<Flight> flightsToThisAirport;

    public SingleAirport(String name, String country, String code, int passengers) {
        this.name = name;
        this.country = country;
        this.code = code;
        this.passengers = passengers;
        flightFromThisAirport = new TreeSet<>(Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime));
        flightsToThisAirport = new TreeSet<>(Comparator.comparing(Flight::getTime).thenComparing(Flight::getFrom));
    }

    public void addOutgoingFlight(Flight flight) {
        flightFromThisAirport.add(flight);
    }

    public void addIncomingFlight(Flight flight) {
        flightsToThisAirport.add(flight);
    }

    public Set<Flight> getFlightFromThisAirport() {
        return flightFromThisAirport;
    }

    public String printFlights(boolean isOutgoing) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        if (isOutgoing) {
            for (Flight flight : flightFromThisAirport) {
                sb.append(i).append(". ").append(flight.toString()).append("\n");
                i++;
            }
            return String.format("%s (%s)\n%s\n%d\n%s",
                    name,
                    code,
                    country,
                    passengers,
                    sb.substring(0, sb.toString().length() - 1));
        }

        return String.format("%s",
                flightsToThisAirport.stream().map(Flight::toString).collect(Collectors.joining("\n")));
    }
}

class Airports {
    // code -> airport
    Map<String, SingleAirport> allAirports;

    public Airports() {
        this.allAirports = new HashMap<>();
    }

    public void addAirport(String name, String country, String code, int passengers) {
        allAirports.putIfAbsent(code, new SingleAirport(name, country, code, passengers));
    }

    public void addFlights(String from, String to, int time, int duration) {
        Flight flight = new Flight(from, to, time, duration);
        allAirports.get(from).addOutgoingFlight(flight);
        allAirports.get(to).addIncomingFlight(flight);
    }

    public void showFlightsFromAirport(String code) {
        System.out.println(allAirports.get(code).printFlights(true));
    }

    public void showDirectFlightsFromTo(String from, String to) {

        if (allAirports.get(from)
                .getFlightFromThisAirport()
                .stream()
                .noneMatch(flight -> flight.to.equals(to))) {
            System.out.printf("No flights from %s to %s%n", from, to);
        }

        allAirports.get(from)
                .getFlightFromThisAirport()
                .stream()
                .filter(i -> i.to.equals(to))
                .forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to) {
        System.out.println(allAirports.get(to).printFlights(false));
    }
}


