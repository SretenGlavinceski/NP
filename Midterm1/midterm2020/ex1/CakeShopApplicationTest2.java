package Midterm1.midterm2020.ex1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CakeShopApplicationTest2 {

    public static void main(String[] args) {
        CakeShopApplication cakeShopApplication = new CakeShopApplication(4);

        System.out.println("--- READING FROM INPUT STREAM ---");
        cakeShopApplication.readCakeOrders(System.in);

        System.out.println("--- PRINTING TO OUTPUT STREAM ---");
        cakeShopApplication.printAllOrders(System.out);
    }
}

abstract class Item {
    String name;
    int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public static Item takeItem(String name, int price) {
        if (name.startsWith("C"))
            return new CakeItem(name, price);
        return new PieItem(name, price);
    }

    abstract public int getPrice();
    abstract public String getType();
}

class CakeItem extends Item {

    public CakeItem(String name, int price) {
        super(name, price);
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public String getType() {
        return "C";
    }
}

class PieItem extends Item {

    public PieItem(String name, int price) {
        super(name, price);
    }

    @Override
    public int getPrice() {
        return price + 50;
    }

    @Override
    public String getType() {
        return "P";
    }
}

class InvalidOrderException extends Exception {
    public InvalidOrderException(String s) {
        super(s);
    }
}

class Order implements Comparable<Order> {
    List<Item> items;
    String id;
    public Order(String s, int minBeforeException) throws InvalidOrderException {
        items = new ArrayList<>();
        String [] parts = s.split("\\s+");
        if (parts.length - 1 < minBeforeException)
            throw new InvalidOrderException(String.format("The order with id %s has less items than the minimum allowed.", parts[0]));
        id = parts[0];

        for(int i = 1; i < parts.length; i+=2)
            items.add(Item.takeItem(parts[i], Integer.parseInt(parts[i + 1])));

    }

    public int fullPriceItems() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    @Override
    public int compareTo(Order o) {
        return Integer.compare(fullPriceItems(), o.fullPriceItems());
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %d",
                id,
                items.size(),
                items.stream().filter(i -> i.getType().equals("P")).count(),
                items.stream().filter(i -> i.getType().equals("C")).count(),
                fullPriceItems());
    }
}

class CakeShopApplication {
    int minProducts;
    List<Order> orders;

    public CakeShopApplication(int minProducts) {
        this.minProducts = minProducts;
        orders = new ArrayList<>();
    }

    void readCakeOrders(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        br.lines().forEach(line -> {
            try {
                orders.add(new Order(line, minProducts));
            } catch (InvalidOrderException e) {
                System.out.println(e.getMessage());
            }
        });
    }
    void printAllOrders(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        orders = orders.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        orders.forEach(pw::println);

        pw.flush();

    }
}
