package Midterm2.ex12;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class CosineSimilarityCalculator {
    public static double cosineSimilarity (Collection<Integer> c1, Collection<Integer> c2) {
        int [] array1;
        int [] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1=0, down2=0;

        for (int i=0;i<c1.size();i++) {
            up+=(array1[i] * array2[i]);
        }

        for (int i=0;i<c1.size();i++) {
            down1+=(array1[i]*array1[i]);
        }

        for (int i=0;i<c1.size();i++) {
            down2+=(array2[i]*array2[i]);
        }

        return up/(Math.sqrt(down1)*Math.sqrt(down2));
    }
}

public class TextProcessorTest {

    public static void main(String[] args) {
        TextProcessor textProcessor = new TextProcessor();

        textProcessor.readText(System.in);

        System.out.println("===PRINT VECTORS===");
        textProcessor.printTextsVectors(System.out);

        System.out.println("PRINT FIRST 20 WORDS SORTED ASCENDING BY FREQUENCY ");
        textProcessor.printCorpus(System.out,  20, true);

        System.out.println("PRINT FIRST 20 WORDS SORTED DESCENDING BY FREQUENCY");
        textProcessor.printCorpus(System.out, 20, false);

        System.out.println("===MOST SIMILAR TEXTS===");
        textProcessor.mostSimilarTexts(System.out);
    }
}

class TextProcessor {
    private static final String REGEX_FILTER = "[^A-Za-z\\s+]";
    Set<String> allUniqueWords;
    Map<String, Integer> wordsByFrequency;
    Map<String, List<Integer>> texts;
    TextProcessor() {
        allUniqueWords = new TreeSet<>();
        wordsByFrequency = new TreeMap<>();
        texts = new LinkedHashMap<>();
    }

    void readText (InputStream is) {
        new BufferedReader(new InputStreamReader(is))
                .lines()
                .forEach(line -> {
                    texts.put(line, null);
                    List<String> words = extractWordsFromText(line);

                    allUniqueWords.addAll(words);

                    words.forEach(word -> {
                        wordsByFrequency.putIfAbsent(word, 0);
                        wordsByFrequency.computeIfPresent(word, (k, v) -> v + 1);
                    });
                });
    }

    private int countWordAppearances (Set<String> list, String word) {
        return (int) list.stream().filter(i -> i.equalsIgnoreCase(word)).count();
    }

    private List<String> extractWordsFromText(String line) {
        line = line.replaceAll(REGEX_FILTER, "");
        return Arrays.stream(line.split("\\s+"))
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private List<Integer> wordFrequencyEvaluate(String s) {
        List<Integer> result = new ArrayList<>();
        List<String> words = extractWordsFromText(s);
        allUniqueWords.forEach(word -> result.add(countWordAppearances(new HashSet<>(words), word)));
        return result;
    }

    void printTextsVectors (OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        texts.keySet().forEach(key -> texts.put(key, wordFrequencyEvaluate(key)));

        texts.values().stream().filter(Objects::nonNull).forEach(vector -> {
            String result = vector.stream().map(Object::toString).collect(Collectors.joining(", "));
            pw.printf("[%s]\n", result);
        });
        pw.flush();
    }

    void printCorpus(OutputStream os, int n, boolean ascending) {
        PrintWriter pw = new PrintWriter(os);

        Comparator<Map.Entry<String, Integer>> comparator = Map.Entry.comparingByValue();

        wordsByFrequency.entrySet()
                        .stream().sorted(ascending ? comparator : comparator.reversed())
                        .limit(n)
                        .forEach(entry -> pw.printf("%s : %d\n", entry.getKey(), entry.getValue()));

        pw.flush();
    }

    public void mostSimilarTexts (OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        String first = "";
        String second = "";
        double bestSimilarity = 0;

        for (String stringFirst : texts.keySet()) {
            for (String stringSecond : texts.keySet()) {
                if (!stringFirst.equals(stringSecond)) {
                    double temp = CosineSimilarityCalculator.cosineSimilarity(texts.get(stringFirst), texts.get(stringSecond));
                    if (temp > bestSimilarity) {
                        bestSimilarity = temp;
                        first = stringFirst;
                        second = stringSecond;
                    }
                }
            }
        }

        pw.println(first);
        pw.println(second);
        pw.println(bestSimilarity);

        pw.flush();
    }
}