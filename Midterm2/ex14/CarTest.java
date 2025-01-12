package Midterm2.ex14;

import java.util.*;
import java.util.stream.Collectors;

public class CarTest {
    public static void main(String[] args) {
        CarCollection carCollection = new CarCollection();
        String manufacturer = fillCollection(carCollection);
        carCollection.sortByPrice(true);
        System.out.println("=== Sorted By Price ASC ===");
        print(carCollection.getList());
        carCollection.sortByPrice(false);
        System.out.println("=== Sorted By Price DESC ===");
        print(carCollection.getList());
        System.out.printf("=== Filtered By Manufacturer: %s ===\n", manufacturer);
        List<Car> result = carCollection.filterByManufacturer(manufacturer);
        print(result);
    }

    static void print(List<Car> cars) {
        for (Car c : cars) {
            System.out.println(c);
        }
    }

    static String fillCollection(CarCollection cc) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            if (parts.length < 4) return parts[0];
            Car car = new Car(parts[0], parts[1], Integer.parseInt(parts[2]),
                    Float.parseFloat(parts[3]));
            cc.addCar(car);
        }
        scanner.close();
        return "";
    }
}


class Car {
    String manufacturer;
    String model;
    int price;
    float power;

    public Car(String manufacturer, String model, int price, float power) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.price = price;
        this.power = power;
    }

    public int getPrice() {
        return price;
    }

    public float getPower() {
        return power;
    }

    public String getModel() {
        return model;
    }
    //Renault Clio (96KW) 12100

    @Override
    public String toString() {
        return String.format("%s %s (%.0fKW) %d", manufacturer, model, power, price);
    }
}

class CarCollection {
    static Comparator<Car> ascendingTrue = Comparator.comparing(Car::getPrice).thenComparing(Car::getPower);
    static Comparator<Car> ascendingFalse = Comparator.comparing(Car::getPrice).thenComparing(Car::getPower).reversed();
    Map<String, Set<Car>> carsByManufacturer;
    List<Car> allCars;

    public CarCollection() {
        this.carsByManufacturer = new TreeMap<>();
        allCars = new ArrayList<>();
    }

    public void addCar(Car car) {
        carsByManufacturer.putIfAbsent(car.manufacturer.toLowerCase(), new TreeSet<>(Comparator.comparing(Car::getModel)));
        carsByManufacturer.get(car.manufacturer.toLowerCase()).add(car);
        allCars.add(car);
    }

    public void sortByPrice(boolean ascending) {

        allCars = allCars.stream()
                .sorted(ascending ? ascendingTrue : ascendingFalse)
                .collect(Collectors.toList());

    }

    public List<Car> filterByManufacturer(String manufacturer) {
        return new ArrayList<>(carsByManufacturer.get(manufacturer.toLowerCase()));
    }

    public List<Car> getList() {
        return allCars;
    }
}