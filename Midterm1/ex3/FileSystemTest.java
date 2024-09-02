package Midterm1.ex3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileSystemTest {

    public static Folder readFolder(Scanner sc) {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < totalFiles; i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String[] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args) {

        //file reading from input

        Scanner sc = new Scanner(System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());

    }
}

interface IFile {
    String getFileName();

    long getFileSize();

    String getFileInfo(String indent);

    void sortBySize();

    long findLargestFile();
}

abstract class BasicFile implements IFile {
    String name;

    public BasicFile(String name) {
        this.name = name;
    }

    @Override
    public String getFileName() {
        return name;
    }
}

class File extends BasicFile {
    long size;

    public File(String name, long size) {
        super(name);
        this.size = size;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(String indent) {
        return String.format("%sFile name: %10s File size: %10d\n", indent, name, getFileSize());
    }

    @Override
    public void sortBySize() {
    }

    @Override
    public long findLargestFile() {
        return size;
    }
}

class FileNameExistsException extends Exception {
    public FileNameExistsException(String s) {
        super(s);
    }
}

class Folder extends BasicFile {

    List<IFile> files;

    public Folder(String name) {
        super(name);
        files = new ArrayList<>();
    }

    void addFile(IFile file) throws FileNameExistsException {
        if (files.stream().anyMatch(iFile -> iFile.getFileName().equals(file.getFileName())))
            throw new FileNameExistsException(String.format("There is already a file named %s in the folder %s", file.getFileName(), name));
        files.add(file);
    }

    @Override
    public long getFileSize() {
        return files.stream().mapToLong(IFile::getFileSize).sum();
    }

    @Override
    public String getFileInfo(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sFolder name: %10s Folder size: %10d\n", indent, name, getFileSize()));
        sb.append(files.stream().map(i -> i.getFileInfo(indent + "    "))
                .collect(Collectors.joining()));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        files = files.stream().sorted(Comparator.comparing(IFile::getFileSize))
                .collect(Collectors.toList());
        files.forEach(IFile::sortBySize);
    }

    @Override
    public long findLargestFile() {
        return files.stream().mapToLong(IFile::findLargestFile).max().orElse(0);
    }
}

class FileSystem {
    Folder rootDirectory;

    public FileSystem() {
        rootDirectory = new Folder("root");
    }

    void addFile(IFile file) throws FileNameExistsException {
        rootDirectory.addFile(file);
    }

    long findLargestFile() {
        return rootDirectory.findLargestFile();
    }

    void sortBySize() {
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo("");
    }
}