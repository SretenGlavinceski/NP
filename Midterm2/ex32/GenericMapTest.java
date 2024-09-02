package Midterm2.ex32;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class GenericMapTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { //Mergeable integers
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging
            // of two Integer objects into a new Integer object which is their sum
            MergeStrategy<Integer> mergeStrategy = Integer::sum;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 2) { // Mergeable strings
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging
            // of two String objects into a new String object which is their concatenation
            MergeStrategy<String> mergeStrategy = (s1, s2) -> s1 + s2;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 3) {
            Map<Integer, Integer> mapLeft = new HashMap<>();
            Map<Integer, Integer> mapRight = new HashMap<>();
            readIntMap(sc, mapLeft);
            readIntMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of
            // two Integer objects into a new Integer object which will be the max of the two objects
            MergeStrategy<Integer> mergeStrategy = Math::max;

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        } else if (testCase == 4) {
            Map<String, String> mapLeft = new HashMap<>();
            Map<String, String> mapRight = new HashMap<>();
            readStrMap(sc, mapLeft);
            readStrMap(sc, mapRight);

            //TODO Create an object of type MergeStrategy that will enable merging of
            // two String objects into a new String object which will mask the occurrences
            // of the second string in the first string

            MergeStrategy<String> mergeStrategy = (r1, r2) -> {
                return r1.replaceAll(r2, IntStream.range(0, r2.length()).mapToObj(i -> "*").collect(Collectors.joining("")));
            };

            printMap(MapOps.merge(mapLeft, mapRight, mergeStrategy));
        }
    }

    private static void readIntMap(Scanner sc, Map<Integer, Integer> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            int k = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            map.put(k, v);
        }
    }

    private static void readStrMap(Scanner sc, Map<String, String> map) {
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            String input = sc.nextLine();
            String[] parts = input.split("\\s+");
            map.put(parts[0], parts[1]);
        }
    }

    private static void printMap(Map<?, ?> map) {
        map.forEach((k, v) -> System.out.printf("%s -> %s%n", k.toString(), v.toString()));
    }
}

interface MergeStrategy <R> {
    R strategy(R r1, R r2);
}

class MapOps {
    public static<K extends Comparable<K>, V extends Comparable<V>> Map<K, V> merge (Map<K, V> firstMap, Map<K, V> secondMap, MergeStrategy<V> mergeStrategy) {
        Map<K, V> result = new TreeMap<>();

        firstMap.entrySet()
                .stream()
                .filter(entry -> !secondMap.containsKey(entry.getKey()))
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        secondMap.entrySet()
                .stream()
                .filter(entry -> !firstMap.containsKey(entry.getKey()))
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        firstMap.entrySet()
                .stream()
                .filter(entry -> secondMap.containsKey(entry.getKey()))
                .forEach(entry -> result.put(entry.getKey(), mergeStrategy.strategy(entry.getValue(), secondMap.get(entry.getKey()))));

        return result;
    }
}


