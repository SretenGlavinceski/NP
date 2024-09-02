package Midterm2.ex1;

import java.util.*;

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticipant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}

class Contestant {
    String code;
    String name;
    int age;

    public Contestant(String code, String name, int age) {
        this.code = code;
        this.name = name;
        this.age = age;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contestant that = (Contestant) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", code, name, age);
    }
}

class Audition {
    Map<String, Set<Contestant>> contestantsByCity;
    private static final Comparator<Contestant> CONTESTANT_COMPARATOR =
            Comparator.comparing(Contestant::getName)
            .thenComparing(Contestant::getAge)
            .thenComparing(Contestant::getCode);

    public Audition() {
        this.contestantsByCity = new HashMap<>();
    }

    void addParticipant(String city, String code, String name, int age) {
        contestantsByCity.putIfAbsent(city, new HashSet<>());
        contestantsByCity.get(city).add(new Contestant(code, name, age));
    }

    void listByCity(String city) {
        contestantsByCity.get(city)
                .stream()
                .sorted(CONTESTANT_COMPARATOR)
                .forEach(System.out::println);
    }
}