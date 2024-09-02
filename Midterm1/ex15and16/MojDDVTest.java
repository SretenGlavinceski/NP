package Midterm1.ex15and16;

import java.io.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);

    }
}


class Item {
    String type;
    int price;

    public Item(String price, String type) {
        this.price = Integer.parseInt(price);
        this.type = type;
    }

    public int getPrice() {
        return price;
    }

    public double taxReturn() {
        if (type.equals("A"))
            return price * 0.18 * 0.15;
        else if (type.equals("B"))
            return price * 0.05 * 0.15;
        else
            return 0;
    }
}

class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(String s) {
        super(s);
    }
}

class Receipt {
    private static final int MAX_ALLOWED = 30000;
    String id;
    List<Item> items;

    public Receipt(String s) throws AmountNotAllowedException {
        items = new ArrayList<>();
        String[] parts = s.split("\\s+");
        id = parts[0];
        int sum = 0;
        for (int i = 1; i < parts.length; i+=2) {
            sum += Integer.parseInt(parts[i]);
            items.add(new Item(parts[i], parts[i + 1]));
        }
        if (sum > MAX_ALLOWED)
            throw new AmountNotAllowedException(String.format("Receipt with amount %d is not allowed to be scanned", sum));
    }

    int totalSum() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    double totalTaxReturn() {
        return items.stream().mapToDouble(Item::taxReturn).sum();
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f",
                id,
                totalSum(),
                totalTaxReturn());
    }
}

class MojDDV {
    List<Receipt> receipts;

    public MojDDV() {
        receipts = new ArrayList<>();
    }

    void readRecords(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(line -> {
            try {
                receipts.add(new Receipt(line));
            } catch (AmountNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    void printTaxReturns (OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        receipts.forEach(pw::println);
        pw.flush();
    }

    void printStatistics (OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        DoubleSummaryStatistics dss = receipts.stream()
                .mapToDouble(Receipt::totalTaxReturn).summaryStatistics();

        pw.println(String.format("min:\t%.3f\nmax:\t%.3f\nsum:\t%.3f\ncount:\t%d\navg:\t%.3f",
                dss.getMin(), dss.getMax(), dss.getSum(), dss.getCount(), dss.getAverage()));
        pw.flush();
    }
}