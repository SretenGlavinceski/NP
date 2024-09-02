package Midterm2.ex35;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}


class Payments {
    String description;
    int price;

    public Payments(String s) {
        String[] parts = s.split(";");

        this.description = parts[1];
        this.price = Integer.parseInt(parts[2]);
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s %d", description, price);
    }

}

class OnlinePayments {
    Map<String, List<Payments>> studentPayments;

    public OnlinePayments() {
        this.studentPayments = new HashMap<>();
    }

    void readItems(InputStream is) {
        new BufferedReader(new InputStreamReader(is))
                .lines()
                .forEach(line -> {
                    String[] parts = line.split(";");
                    String id = parts[0];

                    studentPayments.putIfAbsent(id, new ArrayList<>());
                    studentPayments.get(id).add(new Payments(line));
                });
    }

    void printStudentReport(String index, OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        if (!studentPayments.containsKey(index)) {
            pw.printf("Student %s not found!\n", index);
            pw.flush();
            return;
        }
        Comparator<Payments> comparator = Comparator.comparing(Payments::getPrice).reversed();
        List<Payments> payments = studentPayments.get(index).stream().sorted(comparator).collect(Collectors.toList());

        long paymentSum = payments.stream().mapToInt(Payments::getPrice).sum();
        long provision = Math.round(paymentSum * 0.0114);

        provision = Math.max(Math.min(300, provision), 3);

        pw.printf("Student: %s Net: %d Fee: %d Total: %d\nItems:\n",
                index,
                paymentSum,
                provision,
                paymentSum + provision
        );

        IntStream.range(0, payments.size()).forEach(i -> pw.printf("%d. %s\n", i + 1, payments.get(i)));

        pw.flush();
    }
}