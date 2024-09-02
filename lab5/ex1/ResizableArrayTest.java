package lab5.ex1;

import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

public class ResizableArrayTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();
        if ( test == 0 ) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while ( jin.hasNextInt() ) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if ( test == 1 ) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for ( int i = 0 ; i < 4 ; ++i ) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if ( test == 2 ) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while ( jin.hasNextInt() ) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if ( a.sum() > 100 )
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if ( test == 3 ) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for ( int w = 0 ; w < 500 ; ++w ) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k =  2000;
                int t =  1000;
                for ( int i = 0 ; i < k ; ++i ) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for ( int i = 0 ; i < t ; ++i ) {
                    a.removeElement(k-i-1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

}

class ResizableArray <T> {
    private T [] array;
    @SuppressWarnings("unchecked")
    ResizableArray() {
        this.array = (T [])new Object[0];
    }

    void addElement(T element) {
        T [] elements = Arrays.copyOf(array, array.length + 1);
        elements[elements.length - 1] = element;
        array = elements;
    }
    @SuppressWarnings("unchecked")
    boolean removeElement(T element) {
        if (!contains(element))
            return false;

        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                index = i;
                break;
            }
        }

        array[index] = array[array.length - 1];

        T [] elements;
        elements = Arrays.copyOf(array, array.length - 1);
        this.array = elements;
        return true;
    }

    boolean contains(T element) {
        return Arrays.asList(array).contains(element);
    }

    Object[] toArray() {
        return Arrays.stream(array).toArray();
    }

    boolean isEmpty() {
        return array.length == 0;
    }

    int count() {
        return array.length;
    }

    T elementAt(int idx) {
        if (idx < 0 || idx > count())
            throw new ArrayIndexOutOfBoundsException();
        return array[idx];
    }
    @SuppressWarnings("unchecked")
    static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src) {
        T [] elements = (T[])Arrays.copyOf(src.toArray(), src.count());
        for (T element : elements) {
            dest.addElement(element);
        }
    }
}

class IntegerArray extends ResizableArray<Integer> {
    double sum() {
        return Arrays.stream(this.toArray()).mapToInt(i -> (int) i).sum();
    }

    double mean() {
        return Arrays.stream(this.toArray()).mapToInt(i -> (int) i).average().orElse(0.0);
    }

    int countNonZero() {
        return (int) Arrays.stream(this.toArray()).mapToInt(i -> (int) i).filter(i -> i != 0).count();
    }

    IntegerArray distinct() {
        IntegerArray integerArray = new IntegerArray();
        for (int i = 0; i < count(); i++) {
            if (!integerArray.contains(elementAt(i)))
                integerArray.addElement(elementAt(i));
        }

        return integerArray;
    }

    IntegerArray increment(int offset) {
        IntegerArray integerArray = new IntegerArray();
        for (int i = 0; i < count(); i++) {
            integerArray.addElement(elementAt(i) + offset);
        }

        return integerArray;
    }

}