package Midterm1.ex25;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            try {
                cart.addItem(sc.nextLine());
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}


enum TypeProduct {
    WS, // whole product
    PS // in grams product
}

abstract class Product {
    TypeProduct typeProduct;
    int id;
    String name;
    int productPrice;
    double quantity;
    double discount;

    public Product(TypeProduct typeProduct, int id, String name, int productPrice, double quantity) {
        this.typeProduct = typeProduct;
        this.id = id;
        this.name = name;
        this.productPrice = productPrice;
        this.quantity = quantity;
        discount = 0;
    }

    public static Product makeProduct(String type, String id, String name, String price, String quantity) {
        if (type.equals("WS"))
            return new ProductWS(Integer.parseInt(id), name, Integer.parseInt(price), Double.parseDouble(quantity));
        return new ProductPS(Integer.parseInt(id), name, Integer.parseInt(price), Double.parseDouble(quantity));
    }

    abstract public double productFullPrice();

    @Override
    public String toString() {
        return String.format("%s - %.2f", id, productFullPrice() - (productFullPrice() * discount));
    }

    void discount(double discount) {
        this.discount = 1 - discount;
    }

    public int getId() {
        return id;
    }
}

class ProductWS extends Product {

    public ProductWS(int id, String name, int productPrice, double quantity) {
        super(TypeProduct.WS, id, name, productPrice, quantity);
    }

    @Override
    public double productFullPrice() {
        return productPrice * quantity;
    }
}

class ProductPS extends Product {

    public ProductPS(int id, String name, int productPrice, double quantity) {
        super(TypeProduct.PS, id, name, productPrice, quantity);
    }

    @Override
    public double productFullPrice() {
        return (quantity / 1000.0) * productPrice;
    }
}

class InvalidOperationException extends Exception {
    public InvalidOperationException(String s) {
        super(s);
    }
}

class ShoppingCart {
    private static final double FIXED_DISCOUNT = 0.1;
    List<Product> products;

    public ShoppingCart() {
        products = new ArrayList<>();
    }

    void addItem(String itemData) throws InvalidOperationException {
        String[] parts = itemData.split(";");
        if (Double.parseDouble(parts[4]) == 0.0)
            throw new InvalidOperationException(String.format("The quantity of the product with id %s can not be %d", parts[1], Integer.parseInt(parts[4])));
        products.add(Product.makeProduct(parts[0], parts[1], parts[2], parts[3], parts[4]));
    }

    void printShoppingCart(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        products.stream()
                .sorted(Comparator.comparing(Product::productFullPrice).reversed())
                .forEach(pw::println);

        pw.flush();
    }

    void blackFridayOffer(List<Integer> discountItems, OutputStream os) throws InvalidOperationException {
        PrintWriter pw = new PrintWriter(os);

        if (discountItems.isEmpty())
            throw new InvalidOperationException("There are no products with discount.");

        products.stream()
                .filter(product -> discountItems.contains(product.getId()))
                .forEach(product -> {
                    product.discount(FIXED_DISCOUNT);
                    pw.println(product);
                });

        pw.flush();
    }
}