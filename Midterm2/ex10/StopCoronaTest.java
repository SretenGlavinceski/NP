package Midterm2.ex10;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;

public class StopCoronaTest {

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}

interface ILocation{
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}

class LocationCalculator {

    static final long TIME_LIMIT_SECONDS = 300;
    static final double DISTANCE_LIMIT_SECONDS = 2.0;
    static boolean closeLocations(ILocation location1, ILocation location2) {
        double distance = Math.sqrt(Math.pow(location1.getLatitude() - location2.getLatitude(), 2)
                + Math.pow(location1.getLongitude() - location2.getLongitude(), 2));

        long time = Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());

        return distance <= DISTANCE_LIMIT_SECONDS && time <= TIME_LIMIT_SECONDS;
    }
    static int directContact (User firstUser, User secondUser) {
        int counter = 0;
        for (ILocation locationFirst : firstUser.getLocationsUser()) {
            for (ILocation locationsSecond : secondUser.getLocationsUser()) {
                if (closeLocations(locationFirst, locationsSecond))
                    counter++;
            }
        }
        return counter;
    }
}

class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String s) {
        super(s);
    }
}

class User {
    String name;
    String id;
    List<ILocation> locationsUser;
    boolean isInfected = false;
    LocalDateTime timestamp;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        this.locationsUser = new ArrayList<>();
    }

    public void addLocations (List<ILocation> locations) {
        locationsUser.addAll(locations);
    }

    public void infected (LocalDateTime time) {
        isInfected = true;
        timestamp = time;
    }

    public List<ILocation> getLocationsUser() {
        return locationsUser;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", name, id, timestamp);
    }

    public String displayAsContact() {
        return String.format("%s %s***", name, id.substring(0, 4));
    }
}

class StopCoronaApp {
    Map<String, User> usersByID;
    public StopCoronaApp() {
        this.usersByID = new HashMap<>();
    }

    void addUser(String name, String id) throws UserAlreadyExistException {
        if (usersByID.containsKey(id))
            throw new UserAlreadyExistException(String.format("User with id %s already exists", id));
        usersByID.put(id, new User(name, id));
    }

    void addLocations (String id, List<ILocation> iLocations) {
        usersByID.get(id).addLocations(iLocations);
    }

    void detectNewCase (String id, LocalDateTime timestamp) {
        usersByID.get(id).infected(timestamp);
    }

    Map<User, Integer> getDirectContacts (User currentUser) {
        Map<User, Integer> result = new HashMap<>();

        for (User user : usersByID.values()) {
            int closeContacts = LocationCalculator.directContact(currentUser, user);
            if (closeContacts > 0 && !user.equals(currentUser))
                result.put(user, closeContacts);
        }

        return result;
    }

    Collection<User> getIndirectContacts (User currentUser) {
        return getDirectContacts(currentUser)
                .keySet()
                .stream()
                .map(i -> getDirectContacts(i).keySet())
                .flatMap(Collection::stream)
                .filter(i -> !i.equals(currentUser))
                .collect(Collectors.toSet());
    }

    void createReport () {
        List<User> users = usersByID.values()
                .stream()
                .filter(i -> i.isInfected)
                .sorted(Comparator.comparing(User::getTimestamp).thenComparing(User::getId))
                .collect(Collectors.toList());

        List<Integer> directContacts = new ArrayList<>();
        List<Integer> indirectContacts = new ArrayList<>();

        for (User user : users) {
            System.out.println(user);

            Map<User, Integer> directContactsUser = getDirectContacts(user);

            List<User> indirectContactsUser = getIndirectContacts(user)
                    .stream()
                    .filter(i -> !directContactsUser.containsKey(i))
                    .sorted(Comparator.comparing(User::getName).thenComparing(User::getId))
                    .collect(Collectors.toList());

            System.out.println("Direct contacts:");

            directContactsUser
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> System.out.printf("%s %d%n", entry.getKey().displayAsContact(), entry.getValue()));

            System.out.printf("Count of direct contacts: %d\n", directContactsUser.values().stream().mapToInt(i ->i).sum());

            System.out.println("Indirect contacts:");

            indirectContactsUser.forEach(i -> System.out.println(i.displayAsContact()));

            System.out.printf("Count of indirect contacts: %d\n", indirectContactsUser.size());

            directContacts.add(directContactsUser.values().stream().mapToInt(i ->i).sum());
            indirectContacts.add(indirectContactsUser.size());
        }

        System.out.printf("Average direct contacts: %.4f\n", directContacts.stream().mapToInt(i -> i).average().orElse(0.0));
        System.out.printf("Average indirect contacts: %.4f\n", indirectContacts.stream().mapToInt(i -> i).average().orElse(0.0));
    }
}


