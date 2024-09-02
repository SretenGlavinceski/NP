package Midterm2.ex21;

import java.util.*;
import java.util.stream.Collectors;

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

class DuplicateNumberException extends Exception {
    public DuplicateNumberException(String s) {
        super(s);
    }
}

class PhoneBook {
    List<String> numbers;
    Map<String, Set<String>> contactsByName;

    public PhoneBook() {
        this.contactsByName = new TreeMap<>();
        this.numbers = new ArrayList<>();
    }

    void addContact(String name, String number) throws DuplicateNumberException {
        if (numbers.contains(number))
            throw new DuplicateNumberException(String.format("Duplicate number: %s", number));

        contactsByName.putIfAbsent(name, new TreeSet<>());
        contactsByName.get(name).add(name + " " + number);
    }

    void contactsByNumber(String number) {
        List<String> list = contactsByName.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(i -> i.contains(number))
                .toList();

        if (list.isEmpty())
            System.out.println("NOT FOUND");
        else
            list.forEach(System.out::println);
    }

    void contactsByName(String name) {
        if (!contactsByName.containsKey(name))
            System.out.println("NOT FOUND");
        else
            contactsByName.get(name).forEach(System.out::println);
    }
}
