package Midterm1.ex8;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch (NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}

enum TypeArchive {
    LOCKED,
    SPECIAL
}

abstract class Archive {
    int id;
    LocalDate date;
    TypeArchive typeArchive;
    static String currentMessage;

    public Archive(int id, LocalDate date) {
        this.id = id;
        this.date = date;
    }

    public static Archive createArchive(Archive item, LocalDate date) {
        currentMessage = String.format("Item %d archived at %s", item.id, date);
        if (item.getTypeArchive().equals(TypeArchive.LOCKED)) {
            LockedArchive archive = (LockedArchive) item;
            return new LockedArchive(archive.id, date, archive.dateToOpen);
        }
        SpecialArchive archive = (SpecialArchive) item;
        return new SpecialArchive(archive.id, date, archive.maxOpen);
    }
    public TypeArchive getTypeArchive() {
        return typeArchive;
    }
    static String getMessage() {
        return currentMessage;
    }
    public void openArchive() {
        currentMessage = String.format("Item %d opened at %s", id, date);
    }
}

class LockedArchive extends Archive {
    LocalDate dateToOpen;
    LockedArchive(int id, LocalDate date, LocalDate dateToOpen) {
        super(id, date);
        typeArchive = TypeArchive.LOCKED;
        this.dateToOpen = dateToOpen;
    }

    LockedArchive(int id, LocalDate dateToOpen) {
        super(id, null);
        this.id = id;
        this.dateToOpen = dateToOpen;
        typeArchive = TypeArchive.LOCKED;
    }

    @Override
    public void openArchive() {
        if (date.isBefore(dateToOpen))
            currentMessage = String.format("Item %d cannot be opened before %s", id, dateToOpen);
        else
            super.openArchive();
    }
}

class SpecialArchive extends Archive {
    int maxOpen;
    private final int MAX_TIMES_PERMISSION;

    SpecialArchive(int id, LocalDate date, int maxOpen) {
        super(id, date);
        typeArchive = TypeArchive.SPECIAL;
        this.maxOpen = maxOpen;
        MAX_TIMES_PERMISSION = maxOpen;
    }

    SpecialArchive(int id, int maxOpen) {
        super(id, null);
        typeArchive = TypeArchive.SPECIAL;
        this.maxOpen = maxOpen;
        MAX_TIMES_PERMISSION = maxOpen;
    }

    @Override
    public void openArchive() {
        if (maxOpen == 0)
            currentMessage = String.format("Item %d cannot be opened more than %d times", id, MAX_TIMES_PERMISSION);
        else
            super.openArchive();
        maxOpen--;
    }
}

class NonExistingItemException extends Exception {
    public NonExistingItemException(String s) {
        super(s);
    }
}

class ArchiveStore {

    List<String> messages;
    List<Archive> archives;

    public ArchiveStore() {
        archives = new ArrayList<>();
        messages = new ArrayList<>();
    }

    void archiveItem(Archive item, LocalDate date) {
        archives.add(Archive.createArchive(item, date));
        messages.add(Archive.getMessage());
    }

    void openItem(int id, LocalDate date) throws NonExistingItemException {
        if (archives.stream().noneMatch(archive -> archive.id == id))
            throw new NonExistingItemException(String.format("Item with id %d doesn't exist", id));
        archives.stream().filter(archive -> archive.id == id && archive.date.equals(date))
                .forEach(Archive::openArchive);
        messages.add(Archive.getMessage());
    }

    String getLog() {
        return String.join("\n", messages);
    }
}

