package lab3.ex2;// package Napredno_Programiranje.Lab3_2_napredno;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.*;



class InvalidFormatException extends Exception{
    InvalidFormatException(){}
}

class InvalidNameException extends Exception{
    String name;
    InvalidNameException(String name){
        super(name);
        this.name = name;
    }
}

class InvalidNumberException extends Exception{
    public InvalidNumberException() {
    }
}

class MaximumSizeExceddedException extends Exception{
    MaximumSizeExceddedException(){}
}




class Contact implements Comparable<Contact>, Serializable {
    private String name;
    ArrayList<String> phoneNumbers;
    Contact(String name, String...phonenumber) throws InvalidNameException, MaximumSizeExceddedException, InvalidNumberException {
        if (phonenumber.length > 5)
            throw new MaximumSizeExceddedException();
        if (name.length() < 4 || name.length() > 10 || !name.matches("[a-zA-Z0-9]{5,10}"))
            throw new InvalidNameException(name);
        if (Arrays.stream(phonenumber).allMatch(i -> i.matches("07[0125678][0-9]{6}"))) {
            phoneNumbers = new ArrayList<>();
            phoneNumbers.addAll(Arrays.asList(phonenumber));
        } else
            throw new InvalidNumberException();

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String [] getNumbers() {
        Collections.sort(phoneNumbers);
        return phoneNumbers.toArray(new String[0]);
    }

    public void addNumber(String phonenumber) throws InvalidNumberException {
        if (!phonenumber.matches("07[012345678][0-9]{6}"))
            throw new InvalidNumberException();
        phoneNumbers.add(phonenumber);
    }


    @Override
    public String toString() {
        if (phoneNumbers.isEmpty())   return name + "\n";
        Collections.sort(phoneNumbers);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(name)
                .append("\n")
                .append(phoneNumbers.size())
                .append("\n");
        phoneNumbers.stream()
                .takeWhile(numbers -> !numbers.equals(phoneNumbers.get(phoneNumbers.size() - 1)))
                .forEach(num -> stringBuilder.append(num).append("\n"));
        stringBuilder.append(phoneNumbers.get(phoneNumbers.size() - 1)).append("\n");
        return stringBuilder.toString();
    }

    public static Contact valueOf(String s) throws InvalidFormatException {
        try {
            return new Contact(s);
        } catch (Exception e) {
            throw new InvalidFormatException();
        }
    }

    public String showPhoneNumbers () {
        StringBuilder stringBuilder = new StringBuilder();
        phoneNumbers.forEach(i -> stringBuilder.append(i).append("\n"));
        return stringBuilder.toString();
    }

    @Override
    public int compareTo(Contact o) {
        return name.compareTo(o.name);
    }

    public boolean startsWithNumber (String num) {
        return phoneNumbers.stream().anyMatch(i -> i.startsWith(num));
    }

}

class PhoneBook {

    List<Contact> contacts;
    public PhoneBook() {
        contacts = new ArrayList<>();
    }

    public void addContact(Contact contact) throws MaximumSizeExceddedException, InvalidNameException {
        if (contacts.size() == 250)
            throw new MaximumSizeExceddedException();
        if (contacts.stream().anyMatch(i -> i.getName().equals(contact.getName())))
            throw new InvalidNameException(contact.getName());
        contacts.add(contact);
    }

    public Contact getContactForName(String name) {
        return contacts.stream().filter(i -> i.getName().equals(name)).findFirst().orElse(null);
    }

    public Contact[] getContactsForNumber(String number) {
        return  contacts.stream().filter(i -> i.startsWithNumber(number)).toArray(Contact[]::new);
    }

    public int numberOfContacts() {
        return contacts.size();
    }

    public Contact[] getContacts() {
        contacts.sort(Contact::compareTo);
        return contacts.toArray(Contact[]::new);
    }

    public boolean removeContact(String name) {
        return contacts.removeIf(contact -> contact.getName().equals(name));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        contacts.forEach(i -> stringBuilder
                .append(i.getName())
                .append("\n")
                .append(i.phoneNumbers.size())
                .append("\n")
                .append(i.showPhoneNumbers())
                .append("\n"));
        return stringBuilder.toString();
    }
    public static boolean saveAsTextFile(PhoneBook phonebook,String path) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            for (int i = 0; i < phonebook.contacts.size(); i++)
                writer.write(phonebook.contacts.get(i).toString());
            writer.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static PhoneBook loadFromTextFile(String path) {
        PhoneBook phoneBook = new PhoneBook();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                int n = Integer.parseInt(reader.readLine());
                String [] nums = new String[n];
                for (int i = 0; i < n; i++) {
                    nums[i] = reader.readLine();
                }
                phoneBook.addContact(new Contact(line, nums));
            }
        } catch (IOException | InvalidNameException | InvalidNumberException | MaximumSizeExceddedException e) {
            throw new RuntimeException(e);
        }
        return phoneBook;
    }
}

public class PhonebookTester {

    public static void main(String[] args) throws Exception {
        Scanner jin = new Scanner(System.in);
        String line = jin.nextLine();
        switch( line ) {
            case "test_contact":
                testContact(jin);
                break;
            case "test_phonebook_exceptions":
                testPhonebookExceptions(jin);
                break;
            case "test_usage":
                testUsage(jin);
                break;
        }
    }

    private static void testFile(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while ( jin.hasNextLine() )
            phonebook.addContact(new Contact(jin.nextLine(),jin.nextLine().split("\\s++")));
        String text_file = "phonebook.txt";
        PhoneBook.saveAsTextFile(phonebook,text_file);
        PhoneBook pb = PhoneBook.loadFromTextFile(text_file);
        if ( ! pb.equals(phonebook) ) System.out.println("Your file saving and loading doesn't seem to work right");
        else System.out.println("Your file saving and loading works great. Good job!");
    }

    private static void testUsage(Scanner jin) throws Exception {
        PhoneBook phonebook = new PhoneBook();
        while ( jin.hasNextLine() ) {
            String command = jin.nextLine();
            switch ( command ) {
                case "add":
                    phonebook.addContact(new Contact(jin.nextLine(),jin.nextLine().split("\\s++")));
                    break;
                case "remove":
                    phonebook.removeContact(jin.nextLine());
                    break;
                case "print":
                    System.out.println(phonebook.numberOfContacts());
                    System.out.println(Arrays.toString(phonebook.getContacts()));
                    System.out.println(phonebook);
                    break;
                case "get_name":
                    System.out.println(phonebook.getContactForName(jin.nextLine()));
                    break;
                case "get_number":
                    System.out.println(Arrays.toString(phonebook.getContactsForNumber(jin.nextLine())));
                    break;
            }
        }
    }

    private static void testPhonebookExceptions(Scanner jin) {
        PhoneBook phonebook = new PhoneBook();
        boolean exception_thrown = false;
        try {
            while ( jin.hasNextLine() ) {
                phonebook.addContact(new Contact(jin.nextLine()));
            }
        }
        catch ( InvalidNameException e ) {
            System.out.println(e.name);
            exception_thrown = true;
        }
        catch ( Exception e ) {}
        if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw InvalidNameException");
        /*
		exception_thrown = false;
		try {
		phonebook.addContact(new Contact(jin.nextLine()));
		} catch ( MaximumSizeExceddedException e ) {
			exception_thrown = true;
		}
		catch ( Exception e ) {}
		if ( ! exception_thrown ) System.out.println("Your addContact method doesn't throw MaximumSizeExcededException");
        */
    }

    private static void testContact(Scanner jin) throws Exception {
        boolean exception_thrown = true;
        String names_to_test[] = { "And\nrej","asd","AAAAAAAAAAAAAAAAAAAAAA","Ð�Ð½Ð´Ñ€ÐµÑ˜A123213","Andrej#","Andrej<3"};
        for ( String name : names_to_test ) {
            try {
                new Contact(name);
                exception_thrown = false;
            } catch (InvalidNameException e) {
                exception_thrown = true;
            }
            if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw an InvalidNameException");
        }
        String numbers_to_test[] = { "+071718028","number","078asdasdasd","070asdqwe","070a56798","07045678a","123456789","074456798","073456798","079456798" };
        for ( String number : numbers_to_test ) {
            try {
                new Contact("Andrej",number);
                exception_thrown = false;
            } catch (InvalidNumberException e) {
                exception_thrown = true;
            }
            if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw an InvalidNumberException");
        }
        String nums[] = new String[10];
        for ( int i = 0 ; i < nums.length ; ++i ) nums[i] = getRandomLegitNumber();
        try {
            new Contact("Andrej",nums);
            exception_thrown = false;
        } catch (MaximumSizeExceddedException e) {
            exception_thrown = true;
        }
        if ( ! exception_thrown ) System.out.println("Your Contact constructor doesn't throw a MaximumSizeExceddedException");
        Random rnd = new Random(5);
        Contact contact = new Contact("Andrej",getRandomLegitNumber(rnd),getRandomLegitNumber(rnd),getRandomLegitNumber(rnd));
        System.out.println(contact.getName());
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact);
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact);
        contact.addNumber(getRandomLegitNumber(rnd));
        System.out.println(Arrays.toString(contact.getNumbers()));
        System.out.println(contact);
    }

    static String[] legit_prefixes = {"070","071","072","075","076","077","078"};
    static Random rnd = new Random();

    private static String getRandomLegitNumber() {
        return getRandomLegitNumber(rnd);
    }

    private static String getRandomLegitNumber(Random rnd) {
        StringBuilder sb = new StringBuilder(legit_prefixes[rnd.nextInt(legit_prefixes.length)]);
        for ( int i = 3 ; i < 9 ; ++i )
            sb.append(rnd.nextInt(10));
        return sb.toString();
    }


}


