package Midterm1.midterm2021.ex1;

import java.io.*;
import java.util.ArrayList;

public class LineProcessorTest {
    public static void main(String[] args) {
        LineProcessor lineProcessor = new LineProcessor();

        try {
            lineProcessor.readLines(System.in, System.out, 'a');
        } catch (IOException e) {
            System.out.println("NOT HEHE");
        }
    }
}
class LineProcessor {
    ArrayList<String> strings;

    String maxString(char c) {
        String s = strings.get(0);
        long max = strings.get(0).toLowerCase().chars().filter(i -> i == c).count();
        for (String string : strings) {
            long curr = string.toLowerCase().chars().filter(ch -> ch == c).count();
            if (curr >= max) {
                s = string;
            }
        }
        return s;
    }

    void readLines (InputStream is, OutputStream os, char c) throws IOException {
        strings = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        PrintWriter pw = new PrintWriter(os);

        br.lines().forEach(line -> strings.add(line));

        pw.println(maxString(c));

        pw.flush();
        br.close();
    }
}

