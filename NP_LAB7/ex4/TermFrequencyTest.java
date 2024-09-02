package NP_LAB7.ex4;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[] { "во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја" };
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}

class TermFrequency {
    Set<String> uniqueWords;
    Map<String, Integer> mapByFrequency;
    TermFrequency(InputStream inputStream, String[] stopWords) {
        uniqueWords = new HashSet<>();
        mapByFrequency = new HashMap<>();
        new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .forEach(line -> {
                    String [] parts = line.split("\\s+");

                    for (String word : parts) {
                        word = word.replaceAll("[.,]", "").toLowerCase();
                        if (Arrays.asList(stopWords).contains(word) || word.isEmpty())
                            continue;

                        uniqueWords.add(word);
                        mapByFrequency.putIfAbsent(word, 0);
                        mapByFrequency.computeIfPresent(word, (k,v) -> v + 1);
                    }
                });
    }

    int countTotal() {
        return mapByFrequency.values().stream().mapToInt(i -> i).sum();
    }

    int countDistinct() {
        return uniqueWords.size();
    }

    List<String> mostOften(int k) {
        return mapByFrequency.keySet()
                .stream()
                .sorted(Comparator.comparing(w -> mapByFrequency.get(w)).reversed()
                        .thenComparing(Object::toString))
                .limit(k)
                .collect(Collectors.toList());
    }
}
