package lab6.ex2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SuperStringTest {
    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) {
            SuperString s = new SuperString();
            while (true) {
                int command = jin.nextInt();
                if (command == 0) {//append(String s)
                    s.append(jin.next());
                }
                if (command == 1) {//insert(String s)
                    s.insert(jin.next());
                }
                if (command == 2) {//contains(String s)
                    System.out.println(s.contains(jin.next()));
                }
                if (command == 3) {//reverse()
                    s.reverse();
                }
                if (command == 4) {//toString()
                    System.out.print(s);
                }
                if (command == 5) {//removeLast(int k)
                    s.removeLast(jin.nextInt());
                }
                if (command == 6) {//end
                    break;
                }
            }
        }
    }
}

class SuperString {
    private LinkedList<String> list;
    private Stack<String> stack;

    public SuperString() {
        this.list = new LinkedList<>();
        this.stack = new Stack<>();
    }

    public void append(String s) {
        list.addLast(s);
        stack.push(s);
    }

    public void insert(String s) {
        list.addFirst(s);
        stack.push(s);
    }

    public boolean contains(String s) {
        return toString().contains(s);
    }

    public void reverse() {
        Collections.reverse(list);

        list = list.stream().map(i -> new StringBuilder(i).reverse().toString()).collect(Collectors.toCollection(LinkedList::new));
    }

    public void removeLast(int k) {
        for (int i = 0; i < k; i++) {
            StringBuilder sb = new StringBuilder(stack.pop());

            list.remove(sb.toString());
            list.remove(sb.reverse().toString());
        }
    }

    @Override
    public String toString() {
        return String.join("", list);
    }
}

//class SuperString {
//    LinkedList<String> strings;
//    Stack<String> stack;
//    SuperString() {
//        strings = new LinkedList<>();
//        stack = new Stack<>();
//    }
//
//    void append(String s) {
//        strings.addLast(s);
//        stack.push(s);
//    }
//
//    void insert(String s) {
//        strings.addFirst(s);
//        stack.push(s);
//    }
//
//    boolean contains(String s) {
//        return toString().contains(s);
//    }
//
//    void reverse() {
//        Collections.reverse(strings);
////        strings.replaceAll(str -> new StringBuilder(str).reverse().toString());
//        strings = strings.stream().map(i -> new StringBuilder(i).reverse().toString()).collect(Collectors.toCollection(LinkedList::new));
//    }
//
////    void removeLast(int k) {
//////        IntStream.range(0, k).forEach(i -> strings.removeLast());
//////        lastAddedOrder.stream().skip(lastAddedOrder.size() - k).  forEach(i -> strings.remove(i));
////        lastAddedOrder.stream().sorted(Comparator.reverseOrder()).limit(k).forEach(i -> {
////            strings.remove(i);
////            strings.remove(new StringBuilder(i).reverse().toString());
////        });
////    }
//
//    public void removeLast(int k) {
//        for (int i = 0; i < k; i++) {
//            StringBuilder sb = new StringBuilder(stack.pop());
//
//            strings.remove(sb.toString());
//            stack.remove(sb.reverse().toString());
//        }
//    }
//
//    @Override
//    public String toString() {
//        return String.join("", strings);
//    }
//}
