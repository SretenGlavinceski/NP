package Midterm2.ex2;

import java.util.*;
import java.util.stream.Collectors;

public class BooksTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        BookCollection booksCollection = new BookCollection();
        Set<String> categories = fillCollection(scanner, booksCollection);
        System.out.println("=== PRINT BY CATEGORY ===");
        for (String category : categories) {
            System.out.println("CATEGORY: " + category);
            booksCollection.printByCategory(category);
        }
        System.out.println("=== TOP N BY PRICE ===");
        print(booksCollection.getCheapestN(n));
    }

    static void print(List<Book> books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    static TreeSet<String> fillCollection(Scanner scanner,
                                          BookCollection collection) {
        TreeSet<String> categories = new TreeSet<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Book book = new Book(parts[0], parts[1], Float.parseFloat(parts[2]));
            collection.addBook(book);
            categories.add(parts[1]);
        }
        return categories;
    }
}

class Book {
    String title;
    String category;
    float price;

    public Book(String title, String category, float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %.2f", title, category, price);
    }
}

class BookCollection {
    static final Comparator<Book> CHEAPEST_BOOKS_COMPARATOR =
            Comparator.comparing(Book::getPrice).thenComparing(Book::getTitle);

    static final Comparator<Book> SORTED_BY_TITLE_COMPARATOR =
            Comparator.comparing(Book::getTitle).thenComparing(Book::getPrice);
    Map<String, Set<Book>> booksByCategory;
    Set<Book> cheapestBooks;
    public BookCollection() {
        this.booksByCategory = new HashMap<>();
        cheapestBooks = new TreeSet<>(CHEAPEST_BOOKS_COMPARATOR);
    }

    public void addBook(Book book) {
        booksByCategory.putIfAbsent(book.getCategory(), new TreeSet<>(SORTED_BY_TITLE_COMPARATOR));
        booksByCategory.get(book.getCategory()).add(book);
        cheapestBooks.add(book);
    }

    public void printByCategory(String category) {
        booksByCategory.get(category).forEach(System.out::println);
    }

    public List<Book> getCheapestN(int n) {
        return cheapestBooks.stream().limit(n).collect(Collectors.toList());
    }
}