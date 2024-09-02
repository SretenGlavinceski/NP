package Midterm2.ex25;

import java.time.LocalDateTime;
import java.util.*;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}

class ComparatorFactory {
    static Comparator<Product> generateComparator(COMPARATOR_TYPE type) {
        if (type.equals(COMPARATOR_TYPE.NEWEST_FIRST))
            return Comparator.comparing(Product::getCreatedAt).reversed();
        else if (type.equals(COMPARATOR_TYPE.OLDEST_FIRST))
            return Comparator.comparing(Product::getCreatedAt);
        else if (type.equals(COMPARATOR_TYPE.LOWEST_PRICE_FIRST))
            return Comparator.comparing(Product::getPrice);
        else if (type.equals(COMPARATOR_TYPE.HIGHEST_PRICE_FIRST))
            return Comparator.comparing(Product::getPrice).reversed();
        else if (type.equals(COMPARATOR_TYPE.MOST_SOLD_FIRST))
            return Comparator.comparing(Product::getQuantitySold).reversed();
        else if (type.equals(COMPARATOR_TYPE.LEAST_SOLD_FIRST))
            return Comparator.comparing(Product::getQuantitySold);
        else throw new RuntimeException();
    }
}


class Product {
    String category;
    String id;
    String name;
    LocalDateTime createdAt;
    double price;
    int quantitySold;

    public Product(String category, String id, String name, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", price=" + price +
                ", quantitySold=" + quantitySold +
                '}';
    }
}


class OnlineShop {
    Map<String, Product> productsByID;
    Map<String, List<Product>> productsByCategory;

    OnlineShop() {
        productsByID = new HashMap<>();
        productsByCategory = new HashMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price){
        Product product = new Product(category, id, name, createdAt, price);
        productsByID.put(id, product);

        productsByCategory.putIfAbsent(category, new ArrayList<>());
        productsByCategory.get(category).add(product);
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException{
        if (!productsByID.containsKey(id))
            throw new ProductNotFoundException(String.format("Product with id %s does not exist in the online shop!", id));
        productsByID.get(id).setQuantitySold(quantity);
        return productsByID.get(id).getPrice() * quantity;
    }

    List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<List<Product>> result = new ArrayList<>();
        List<Product> productList;

        if (category == null)
            productList = new ArrayList<>(productsByID.values());
        else
            productList = productsByCategory.get(category);

        productList.sort(ComparatorFactory.generateComparator(comparatorType));

        for (int i = 0; i < productList.size(); i += pageSize) {
            int endIndex = Math.min(pageSize + i, productList.size());
            result.add(productList.subList(i, endIndex));
        }

        return result;
    }

}

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category=null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}

