package Midterm2.ex36;

import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

class DeliveryPerson {
    String id;
    String name;
    Location currentLocation;
    int orders = 0;
    double moneyMade = 0;

    public DeliveryPerson(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void registerOrder (double moneyForDelivery) {
        orders++;
        this.moneyMade += moneyForDelivery;
    }

    public int getOrders() {
        return orders;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getMoneyMade() {
        return moneyMade;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id,
                name,
                orders,
                moneyMade,
                (moneyMade / orders) > 0 ? moneyMade / orders : 0.0);
    }
}

class Restaurant {
    String id;
    String name;
    Location location;
    double totalEarned = 0.0;
    int totalOrders = 0;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void orderRegister (double cost) {
        totalEarned += cost;
        totalOrders++;
    }

    public double getTotalEarned() {
        return totalEarned;
    }

    public double averageAmountEarned () {
        return (getTotalEarned() / totalOrders) > 0 ? getTotalEarned() / totalOrders : 0.0;
    }
    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id,
                name,
                totalOrders,
                getTotalEarned(),
                averageAmountEarned());
    }
}

class User {
    String id;
    String name;
    Map<String, Location> userLocations;
    double amountSpent = 0.0;
    int totalOrders = 0;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        userLocations = new HashMap<>();
    }

    public void addLocation (String addressName, Location location) {
        userLocations.putIfAbsent(addressName, location);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Location> getUserLocations() {
        return userLocations;
    }

    public void orderedDelivery (double cost) {
        amountSpent += cost;
        totalOrders++;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public double averageAmountSpent () {
        return getAmountSpent() / totalOrders > 0 ? getAmountSpent() / totalOrders : 0.0;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id,
                name,
                totalOrders,
                getAmountSpent(),
                averageAmountSpent());
    }
}

class DeliveryApp {
    String name;
    List<DeliveryPerson> deliveryPeople;
    Map<String, User> usersByID;
    Map<String, Restaurant> restaurantByID;

    DeliveryApp (String name) {
        this.name = name;
        deliveryPeople = new ArrayList<>();
        usersByID = new HashMap<>();
        restaurantByID = new HashMap<>();
    }

    void registerDeliveryPerson (String id, String name, Location currentLocation) {
        deliveryPeople.add(new DeliveryPerson(id, name, currentLocation));
    }

    void addRestaurant (String id, String name, Location location) {
        restaurantByID.putIfAbsent(id, new Restaurant(id, name, location));
    }

    void addUser (String id, String name) {
        usersByID.put(id, new User(id, name));
    }

    void addAddress (String id, String addressName, Location location) {
        usersByID.get(id).addLocation(addressName, location);
    }

    void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        DeliveryPerson closestPerson = deliveryPeople.get(0);
        User user = usersByID.get(userId);
        Restaurant restaurant = restaurantByID.get(restaurantId);

        Location locationRestaurant = restaurant.getLocation();

        int diff = locationRestaurant.distance(closestPerson.getCurrentLocation());
        for (DeliveryPerson person : deliveryPeople) {
            if (locationRestaurant.distance(person.getCurrentLocation()) == diff) {
                if (person.getOrders() < closestPerson.getOrders()) {
                    diff = locationRestaurant.distance(person.getCurrentLocation());
                    closestPerson = person;
                }
            } else if (locationRestaurant.distance(person.getCurrentLocation()) < diff) {
                diff = locationRestaurant.distance(person.getCurrentLocation());
                closestPerson = person;
            }
        }

        closestPerson.setCurrentLocation(user.getUserLocations().get(userAddressName));

        closestPerson.registerOrder((double) (90 + (diff / 10) * 10));
        user.orderedDelivery(cost);
        restaurant.orderRegister(cost);
    }

    void printUsers() {
        usersByID
                .values()
                .stream()
                .sorted(Comparator.comparing(User::getAmountSpent).thenComparing(User::getId).reversed())
                .forEach(System.out::println);
    }

    void printRestaurants() {
        restaurantByID
                .values()
                .stream()
                .sorted(Comparator.comparing(Restaurant::averageAmountEarned).thenComparing(Restaurant::getId).reversed())
                .forEach(System.out::println);
    }

    void printDeliveryPeople() {
        deliveryPeople
                .stream()
                .sorted(Comparator.comparing(DeliveryPerson::getMoneyMade).thenComparing(DeliveryPerson::getId).reversed())
                .forEach(System.out::println);
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}
