package Midterm2.ex9;

import java.util.*;

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}

class SeatTakenException extends Exception {

}

class SeatNotAllowedException extends Exception {

}

class Sector {
    private final String code;
    private final int capacity;
    private Map<Integer, Integer> seats;

    public Sector(String code, int capacity) {
        this.code = code;
        this.capacity = capacity;
        seats = new HashMap<>();
    }

    public void seatInfo(int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        if (seats.containsKey(seat))
            throw new SeatTakenException();

        if ((type == 1 && seats.containsValue(2)) || (type == 2 && seats.containsValue(1)))
            throw new SeatNotAllowedException();

        seats.put(seat, type);
    }

    public String getCode() {
        return code;
    }
    public int emptySeats() {
        return capacity - seats.size();
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%",
                code,
                emptySeats(),
                capacity,
                ((double) (capacity - emptySeats()) / capacity) * 100.0);
    }
}

class Stadium {
    private Map<String, Sector> sectors;
    private final String name;
    Stadium(String name) {
        this.name = name;
    }

    public void createSectors(String[] sectorNames, int[] sizes) {
        sectors = new HashMap<>();
        for (int i = 0; i < sectorNames.length; i++)
            sectors.put(sectorNames[i], new Sector(sectorNames[i], sizes[i]));
    }

    public void buyTicket(String sectorName, int seat, int type) throws SeatTakenException, SeatNotAllowedException {
        sectors.get(sectorName).seatInfo(seat, type);
    }

    public void showSectors() {
        sectors.values().stream()
                .sorted(Comparator.comparing(Sector::emptySeats).reversed().thenComparing(Sector::getCode))
                .forEach(System.out::println);
    }
}
