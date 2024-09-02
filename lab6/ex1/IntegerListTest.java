package lab6.ex1;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class IntegerListTest {

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) { //test standard methods
            int subtest = jin.nextInt();
            if ( subtest == 0 ) {
                IntegerList list = new IntegerList();
                while ( true ) {
                    int num = jin.nextInt();
                    if ( num == 0 ) {
                        list.add(jin.nextInt(), jin.nextInt());
                    }
                    if ( num == 1 ) {
                        list.remove(jin.nextInt());
                    }
                    if ( num == 2 ) {
                        print(list);
                    }
                    if ( num == 3 ) {
                        break;
                    }
                }
            }
            if ( subtest == 1 ) {
                int n = jin.nextInt();
                Integer[] a = new Integer[n];
                for ( int i = 0 ; i < n ; ++i ) {
                    a[i] = jin.nextInt();
                }
                IntegerList list = new IntegerList(a);
                print(list);
            }
        }
        if ( k == 1 ) { //test count,remove duplicates, addValue
            int n = jin.nextInt();
            Integer[] a = new Integer[n];
            for ( int i = 0 ; i < n ; ++i ) {
                a[i] = jin.nextInt();
            }
            IntegerList list = new IntegerList(a);
            while ( true ) {
                int num = jin.nextInt();
                if ( num == 0 ) { //count
                    System.out.println(list.count(jin.nextInt()));
                }
                if ( num == 1 ) {
                    list.removeDuplicates();
                }
                if ( num == 2 ) {
                    print(list.addValue(jin.nextInt()));
                }
                if ( num == 3 ) {
                    list.add(jin.nextInt(), jin.nextInt());
                }
                if ( num == 4 ) {
                    print(list);
                }
                if ( num == 5 ) {
                    break;
                }
            }
        }
        if ( k == 2 ) { //test shiftRight, shiftLeft, sumFirst , sumLast
            int n = jin.nextInt();
            Integer[] a = new Integer[n];
            for ( int i = 0 ; i < n ; ++i ) {
                a[i] = jin.nextInt();
            }
            IntegerList list = new IntegerList(a);
            while ( true ) {
                int num = jin.nextInt();
                if ( num == 0 ) { //count
                    list.shiftLeft(jin.nextInt(), jin.nextInt());
                }
                if ( num == 1 ) {
                    list.shiftRight(jin.nextInt(), jin.nextInt());
                }
                if ( num == 2 ) {
                    System.out.println(list.sumFirst(jin.nextInt()));
                }
                if ( num == 3 ) {
                    System.out.println(list.sumLast(jin.nextInt()));
                }
                if ( num == 4 ) {
                    print(list);
                }
                if ( num == 5 ) {
                    break;
                }
            }
        }
    }

    public static void print(IntegerList il) {
        if ( il.size() == 0 ) System.out.print("EMPTY");
        for ( int i = 0 ; i < il.size() ; ++i ) {
            if ( i > 0 ) System.out.print(" ");
            System.out.print(il.get(i));
        }
        System.out.println();
    }

}

class IntegerList {
    List<Integer> list;
    IntegerList() {
        list = new ArrayList<>();
    }
    IntegerList(Integer [] numbers) {
        list = new ArrayList<>();
        list.addAll(Arrays.asList(numbers));
    }

    void add(int el, int idx) {
        if (idx > list.size())
            IntStream.range(0, idx - list.size()).forEach(i -> list.add(0));
        list.add(idx, el);
    }

    int remove(int idx) {
        if (idx > list.size())
            throw new ArrayIndexOutOfBoundsException();
        return list.remove(idx);
    }

    void set(int el, int idx) {
        if (idx > list.size()) throw new ArrayIndexOutOfBoundsException();
        list.add(idx, el);
    }

    int get(int idx) {
        if (idx > list.size()) throw new ArrayIndexOutOfBoundsException();
        return list.get(idx);
    }
    int size() {
        return list.size();
    }

    int count(int el) {
        return (int) list.stream().filter(i -> i == el).count();
    }

    void removeDuplicates() {
        Collections.reverse(list);
        Set<Integer> set = new LinkedHashSet<>(list);
        list = new LinkedList<>(set);
        Collections.reverse(list);
    }

    int sumFirst(int k) {
        return list.stream().limit(k).mapToInt(i -> i).sum();
    }

    int sumLast(int k) {
        return list.stream().skip(list.size() - k).mapToInt(i -> i).sum();
    }

    void shiftRight(int idx, int k) {
        list.add((idx + k) % size(), list.remove(idx));
    }

    void shiftLeft(int idx , int k) {
        if (idx - k < 0)
            list.add(size() - (k - idx) % size(), list.remove(idx));
        else
            list.add(idx - k, list.remove(idx));
    }

    IntegerList addValue(int value) {
        Integer [] array = new Integer[list.size()];
        IntStream.range(0, list.size()).forEach(i -> array[i] = list.get(i) + value);
        return new IntegerList(array);
    }
}