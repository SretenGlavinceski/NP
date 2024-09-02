package Midterm2.ex27;

import java.util.*;
import java.util.stream.Collectors;

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

class Names {
    Map<String, Integer> nameFrequency;

    public Names() {
        this.nameFrequency = new TreeMap<>();
    }

    public void addName(String name) {
        if (nameFrequency.containsKey(name)) {
            nameFrequency.put(name, nameFrequency.get(name) + 1);
        } else {
            nameFrequency.put(name, 1);
        }
    }

    private int uniqueLetters (String s) {
        Set<Character> characters = new HashSet<>();

        for (char ch : s.toCharArray())
            characters.add(Character.toLowerCase(ch));

        return characters.size();
    }

    public void printN(int n) {
        nameFrequency.entrySet().stream().filter(i -> i.getValue() >= n)
                .forEach(i -> System.out.printf("%s (%d) %d%n",
                        i.getKey(),
                        i.getValue(),
                        uniqueLetters(i.getKey())));
    }

    public String findName(int len, int x) {
        List<String> names = nameFrequency
                .keySet()
                .stream()
                .filter(i -> i.length() < len)
                .collect(Collectors.toList());

        while (x > names.size()) {
            x = x % names.size();
        }

        return names.get(x);
    }
}






























