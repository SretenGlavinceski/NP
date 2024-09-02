package Midterm1.ex13;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}

class Component {
    List<Component> components;
    String color;
    int weight;

    public Component(String color, int weight) {
        components = new ArrayList<>();
        this.color = color;
        this.weight = weight;
    }

    void addComponent(Component component) {
        components.add(component);
        components = components.stream()
                .sorted(Comparator.comparing(Component::getWeight).thenComparing(Component::getColor))
                .collect(Collectors.toList());
    }

    public void setColor(String color) {
        this.color = color;
    }

    void changeColorForComponents(int weight1, String color1) {
        if (weight < weight1)
            setColor(color1);
        components.forEach(component -> component.changeColorForComponents(weight1, color1));
    }

    public String display (String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s%d:%s\n", indent, weight, color));
        sb.append(components.stream().map(component -> component.display(indent + "---"))
                .collect(Collectors.joining()));
        return sb.toString();
    }

    @Override
    public String toString() {
        return components.stream().map(i -> i.display("---"))
                .collect(Collectors.joining());
    }

    public int getWeight() {
        return weight;
    }
    public String getColor() {
        return color;
    }
}

class InvalidPositionException extends Exception {
    public InvalidPositionException(String s) {
        super(s);
    }
}

class Window {
    String name;
    List<Component> components;

    public Window(String name) {
        components = new ArrayList<>();
        this.name = name;
    }

    void addComponent(int position, Component component) throws InvalidPositionException {
        if (position <= components.size())
            throw new InvalidPositionException(String.format("Invalid position %d, already taken!", position));
        components.add(component);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("WINDOW %s\n", name));
        for (int i = 0; i < components.size(); i++) {
            sb.append(String.format("%d:%d:%s\n", i + 1, components.get(i).weight, components.get(i).color));
            sb.append(components.get(i).toString());
        }

        return sb.toString();
    }

    void changeColor(int weight, String color) {
        components.stream().filter(component -> component.weight < weight)
                .forEach(component -> component.setColor(color));
        components.forEach(component -> component.changeColorForComponents(weight, color));
    }

    void swichComponents(int pos1, int pos2) {
        Component component2 = components.remove(pos2 - 1);
        Component component1 = components.remove(pos1 - 1);
        components.add(pos1 - 1, component2);
        components.add(pos2 - 1, component1);
    }
}