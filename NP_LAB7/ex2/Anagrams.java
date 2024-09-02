package NP_LAB7.ex2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        Map<String, List<String>> anagrams = new LinkedHashMap<>();

        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(line -> {
                    char [] array = line.toCharArray();
                    Arrays.sort(array);
                    String sortedWord = new String(array);
                    anagrams.putIfAbsent(sortedWord, new ArrayList<>());
                    anagrams.get(sortedWord).add(line);
                });

        anagrams.values().forEach(list -> {
            list.forEach(i -> System.out.printf("%s ", i));
            System.out.println();
        });
    }
}
