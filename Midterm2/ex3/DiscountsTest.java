package Midterm2.ex3;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Discounts
 */
public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}

class Product {
    int discountPrice;
    int originalPrice;

    public Product(int discountPrice, int originalPrice) {
        this.discountPrice = discountPrice;
        this.originalPrice = originalPrice;
    }

    int discountPercentage() {
        return (int)(100 - (100.0 / originalPrice) * discountPrice);
    }
    int totalDiscountProduct() {
        return originalPrice - discountPrice;
    }

    @Override
    public String toString() {
        return String.format("%2d%% %d/%d",
                discountPercentage(),
                discountPrice,
                originalPrice);
    }
}

class Store {
    String name;
    List<Product> products;
    public Store(String s) {
        products = new ArrayList<>();
        this.name = s.split("\\s+")[0];
        readProducts(s);
    }

    private void readProducts (String s) {
        String [] parts = s.split("\\s+");
        Arrays.stream(parts).skip(1)
                .forEach(product -> {
                    String [] split = product.split(":");
                    int discountPrice = Integer.parseInt(split[0]);
                    int originalPrice = Integer.parseInt(split[1]);
                    products.add(new Product(discountPrice, originalPrice));
                });
    }

    public int getTotalDiscount() {
        return products.stream().mapToInt(Product::totalDiscountProduct).sum();
    }

    public double averageDiscount() {
        return products.stream().mapToInt(Product::discountPercentage).average().orElse(0.0);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s\nAverage discount: %.1f%%\nTotal discount: %d\n%s",
                name,
                averageDiscount(),
                getTotalDiscount(),
                products.stream()
                        .sorted(Comparator.comparing(Product::discountPercentage).thenComparing(Product::totalDiscountProduct).reversed())
                        .map(Product::toString)
                        .collect(Collectors.joining("\n")));
    }
}

class Discounts {
    List<Store> stores;
    static final int LIMIT_DISPLAY_PRODUCTS = 3;
    static final Comparator<Store> AVERAGE_DISCOUNT_COMPARATOR =
            Comparator.comparing(Store::averageDiscount).reversed().thenComparing(Store::getName);

    static final Comparator<Store> LOWEST_TOTAL_DISCOUNT_COMPARATOR =
            Comparator.comparing(Store::getTotalDiscount).thenComparing(Store::getName);
    public Discounts() {
        this.stores = new ArrayList<>();
    }

    public int readStores(InputStream inputStream) {
        new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .forEach(line -> {
                    stores.add(new Store(line));
                });

        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream().sorted(AVERAGE_DISCOUNT_COMPARATOR).limit(LIMIT_DISPLAY_PRODUCTS).collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream().sorted(LOWEST_TOTAL_DISCOUNT_COMPARATOR).limit(LIMIT_DISPLAY_PRODUCTS).collect(Collectors.toList());
    }
}