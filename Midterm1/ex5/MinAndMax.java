package Midterm1.ex5;

import java.util.Scanner;


class MinMax <T extends Comparable<T>> {
    T min;
    T max;
    int countMin;
    int countMax;
    int countNonMinMax;

    public MinMax() {
        countNonMinMax = 0;
        countMin = 0;
        countMax = 0;
    }

    void update(T element) {
        if (countNonMinMax == 0) {
            min = element;
            max = element;
        }
        countNonMinMax++;

        if (element.compareTo(min) < 0) {
            countMin = 1;
            min = element;
        } else if(element.compareTo(min) == 0) {
            countMin++;
        }
        if (element.compareTo(max) > 0) {
            countMax = 1;
            max = element;
        } else if(element.compareTo(max) == 0) {
            countMax++;
        }


    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public String toString() {
        return min + " " + max + " " +  (countNonMinMax - countMax - countMin);
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}
