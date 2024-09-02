package NP_LAB7.ex1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Map;
import java.util.TreeMap;


public class ChatSystemTest {
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr.addUser(jin.next());
                if (k == 1) cr.removeUser(jin.next());
                if (k == 2) System.out.println(cr.hasUser(jin.next()));
            }
            n = jin.nextInt();
            if(n!=0) System.out.println("");
            System.out.println(cr.toString());
            if (n == 0) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr2.addUser(jin.next());
                if (k == 1) cr2.removeUser(jin.next());
                if (k == 2) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method mts[] = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String params[] = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs,(Object[])params);
                    }
                }
            }
        }
    }

}


class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String s) {
        super(s);
    }
}

class NoSuchUserException extends Exception {
    public NoSuchUserException(String s) {
        super(s);
    }
}

class ChatRoom {
    String chatRoomName;
    Set<String> users;

    ChatRoom() {
        this.chatRoomName = "ChatRoom1";
        this.users = new TreeSet<>();
    }

    ChatRoom(String name) {
        this.chatRoomName = name;
        this.users = new TreeSet<>();
    }
    void addUser(String username) {
        users.add(username);
    }
    void removeUser(String username) {
        users.removeIf(element -> element.equals(username));
    }

    boolean hasUser(String username) {
        return users.contains(username);
    }

    int numUsers() {
        return users.size();
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

//    @Override
//    public String toString() {
//        return String.format("%s\n%s\n",
//                chatRoomName,
//                numUsers() == 0 ? "EMPTY" : String.join("\n", users));
//    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(chatRoomName).append("\n");
        if(users.isEmpty()) {
            str.append("EMPTY").append("\n");
        } else {
            users.forEach(x -> str.append(x).append("\n"));
        }
        return str.toString();
    }
}

class ChatSystem {
    Map<String, ChatRoom> rooms;
    Set<String> allUsers;

    ChatSystem() {
        rooms = new TreeMap<>();
        allUsers = new HashSet<>();
    }

    public void addRoom(String roomName) {
        rooms.put(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        rooms.remove(roomName);
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if (!rooms.containsKey(roomName))
            throw new NoSuchRoomException("NO SUCH ROOM");

        return rooms.get(roomName);
    }

    public void register(String username) {
        rooms.values().stream().min(Comparator.comparing(ChatRoom::numUsers)
                .thenComparing(ChatRoom::getChatRoomName))
                .ifPresent(i -> i.addUser(username));

        allUsers.add(username);
    }

    public void registerAndJoin(String username, String roomName) {
        rooms.get(roomName).addUser(username);
        allUsers.add(username);
    }

    public void joinRoom(String username, String roomName) throws NoSuchRoomException, NoSuchUserException {
        if (!rooms.containsKey(roomName))
            throw new NoSuchRoomException("NO ROOM");
        if (!allUsers.contains(username))
            throw new NoSuchUserException("NO SUCH USER");
        rooms.get(roomName).addUser(username);
    }

    public void leaveRoom(String username, String roomName) throws NoSuchRoomException, NoSuchUserException {
        if (!rooms.containsKey(roomName))
            throw new NoSuchRoomException("NO ROOM");
        if (!allUsers.contains(username))
            throw new NoSuchUserException("NO SUCH USER");
        rooms.get(roomName).removeUser(username);
    }

    public void followFriend(String username, String friendUsername) throws NoSuchUserException {
        if (!allUsers.contains(username))
            throw new NoSuchUserException("NO SUCH USER");

        rooms.values().stream().filter(i -> i.hasUser(friendUsername)).forEach(i -> i.addUser(username));
    }
}