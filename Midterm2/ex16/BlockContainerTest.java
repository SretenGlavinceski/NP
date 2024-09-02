package Midterm2.ex16;

import java.util.*;
import java.util.stream.Collectors;

public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<Integer>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for(int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<String>(size);
        String lastString = null;
        for(int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}


class BlockContainer<T extends Comparable<T>> {
    List<Set<T>> blocks;
    int size;
    public BlockContainer(int n) {
        this.size = n;
        blocks = new ArrayList<>();
    }

    public void add(T a) {
        if (blocks.isEmpty()) {
            blocks.add(new TreeSet<>(Comparator.naturalOrder()));
        }

        if (blocks.get(blocks.size() - 1).size() == size) {
            blocks.add(new TreeSet<>());
        }

        blocks.get(blocks.size() - 1).add(a);
    }
    public boolean remove (T a) {
        boolean result = false;
        if (!blocks.isEmpty()) {
            Set<T> set = blocks.get(blocks.size() - 1);
            result = set.remove(a);

            if (set.isEmpty()) {
                blocks.remove(blocks.size() - 1);
            }
        }
        return result;
    }
    public void sort() {
        Set<T> sortedElements = blocks.stream().flatMap(Collection::stream).collect(Collectors.toCollection(TreeSet::new));
        blocks = new ArrayList<>();
        sortedElements.forEach(this::add);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Set<T> set : blocks) {
            sb.append("[").append(set.stream().map(Object::toString).collect(Collectors.joining(", "))).append("]");
            sb.append(",");
        }

        return sb.substring(0, sb.length() - 1);
    }
}


