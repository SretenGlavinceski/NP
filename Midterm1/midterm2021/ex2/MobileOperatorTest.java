package Midterm1.midterm2021.ex2;

import java.io.*;
import java.util.*;

public class MobileOperatorTest {
    public static void main(String[] args) {
        MobileOperator mobileOperator = new MobileOperator();
        System.out.println("---- READING OF THE SALES REPORTS ----");
        mobileOperator.readSalesRepData(System.in);
        System.out.println("---- PRINTING FINAL REPORTS FOR SALES REPRESENTATIVES ----");
        mobileOperator.printSalesReport(System.out);
    }
}

class MobilePackage {
    String type;
    int count_minutes;
    int count_of_SMS;
    double count_of_data_in_GB;

    public MobilePackage(String type, String count_minutes, String count_of_SMS, String count_of_data_in_GB) {
        this.type = type;
        this.count_minutes = Integer.parseInt(count_minutes);
        this.count_of_SMS = Integer.parseInt(count_of_SMS);
        this.count_of_data_in_GB = Double.parseDouble(count_of_data_in_GB);
    }

    public static MobilePackage addOperator(String package_type, String count_minutes,
                               String count_of_SMS, String count_of_data_in_GB) {
        if (package_type.equals("S")){
            return new PackageS(package_type, count_minutes, count_of_SMS, count_of_data_in_GB);
        }
        return new PackageM(package_type, count_minutes, count_of_SMS, count_of_data_in_GB);
    }

    int calculateTotalPrice() {return 0;}

    public String getType() {
        return type;
    }
}

class PackageS extends MobilePackage {
    private static final int BASIC_SMS = 50;
    private static final int BASIC_MIN = 100;
    private static final double BASIC_NET = 5;
    private static final int PRICE_BASIC = 500;

    public PackageS(String type, String count_minutes, String count_of_SMS, String count_of_data_in_GB) {
        super(type, count_minutes, count_of_SMS, count_of_data_in_GB);
    }

    @Override
    int calculateTotalPrice() {
        int sum = PRICE_BASIC;
        if (count_minutes > BASIC_MIN)
            sum += (count_minutes - BASIC_MIN) * 5;
        if (count_of_SMS > BASIC_SMS)
            sum += (count_of_SMS - BASIC_SMS) * 6;
        if (count_of_data_in_GB > BASIC_NET)
            sum += (int) ((count_of_data_in_GB - BASIC_NET) * 25);
        return sum;
    }
}

class PackageM extends MobilePackage {
    private static final int BASIC_SMS = 60;
    private static final int BASIC_MIN = 150;
    private static final double BASIC_NET = 10;
    private static final int PRICE_BASIC = 750;

    public PackageM(String type, String count_minutes, String count_of_SMS, String count_of_data_in_GB) {
        super(type, count_minutes, count_of_SMS, count_of_data_in_GB);
    }

    @Override
    int calculateTotalPrice() {
        int sum = PRICE_BASIC;
        if (count_minutes > BASIC_MIN)
            sum += (count_minutes - BASIC_MIN) * 4;
        if (count_of_SMS > BASIC_SMS)
            sum += (count_of_SMS - BASIC_SMS) * 4;
        if (count_of_data_in_GB > BASIC_NET)
            sum += (int) ((count_of_data_in_GB - BASIC_NET) * 20);
        return sum;
    }
}

class InvalidIdException extends Exception {
    public InvalidIdException(String s) {
        super(s);
    }
}

class Client {
    private static final int MAX_ID = 7;

    String customerID;
    MobilePackage mobilePackage;

    public Client(String customerID, String package_type, String count_of_minutes, String count_of_SMS,
                  String count_of_data_in_GB) throws InvalidIdException {
        if (customerID.length() != MAX_ID)
            throw new InvalidIdException(String.format("%s is not a valid client ID", customerID));
        this.customerID = customerID;
        mobilePackage = MobilePackage.addOperator(package_type, count_of_minutes, count_of_SMS, count_of_data_in_GB);
    }
}

class SalesPerson implements Comparable<SalesPerson> {
    private static final int MAX_ID = 3;
    String salesID;
    List<Client> clients;

    private static final double PROVISION_S = 0.07;
    private static final double PROVISION_M = 0.04;

    public SalesPerson(String s) throws InvalidIdException {
        clients = new ArrayList<>();
        String [] parts = s.split("\\s+");
        if (parts[0].length() != MAX_ID)
            throw new InvalidIdException(String.format("%s is not a valid sales rep ID", parts[0]));
        salesID = parts[0];
        for (int i = 1; i < parts.length; i+=5) {
            clients.add(new Client(parts[i], parts[i + 1], parts[i + 2], parts[i + 3], parts[i + 4]));
        }
    }

    double totalProvisionSalesPerson() {
        double total = 0.0;
        total += (clients.stream().map(client -> client.mobilePackage)
                .filter(mobilePackage -> mobilePackage.getType().equals("S"))
                .mapToInt(MobilePackage::calculateTotalPrice).sum()) * PROVISION_S;

        total += (clients.stream().map(client -> client.mobilePackage)
                .filter(mobilePackage -> mobilePackage.getType().equals("M"))
                .mapToInt(MobilePackage::calculateTotalPrice).sum()) * PROVISION_M;
        return total;
    }

    @Override
    public String toString() {
        IntSummaryStatistics dss = clients.stream().map(i -> i.mobilePackage)
                .mapToInt(MobilePackage::calculateTotalPrice).summaryStatistics();
        //ID number_of_bills min_bill average_bill max_bill total_commission
        return String.format("%s Count: %d Min: %d Average: %.2f Max: %d Commission: %.2f",
                  salesID,
                  clients.size(),
                  dss.getMin(),
                  dss.getAverage(),
                  dss.getMax(),
                  totalProvisionSalesPerson());
    }

    @Override
    public int compareTo(SalesPerson o) {
        return Double.compare(totalProvisionSalesPerson(), o.totalProvisionSalesPerson());
    }
}

class MobileOperator {
    List<SalesPerson> salesPeople;
    MobileOperator(){
        salesPeople = new ArrayList<>();
    }

    void readSalesRepData (InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> {
            try {
                salesPeople.add(new SalesPerson(line));
            } catch (InvalidIdException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    void printSalesReport (OutputStream os) {
        PrintWriter pw = new PrintWriter(os);

        salesPeople.stream().sorted(Comparator.reverseOrder()).forEach(pw::println);

        pw.flush();
    }
}


// test case 1

// input
//123 5656568 S 123 50 66
//222 3049587 M 500 120 300
//111 5647481 S 150 200 300
//910 1029384 M 10 5 2
//12 1234567 M 21 13 15
//010 7584930 S 1200 5000 10000


// output


//12 is not a valid sales rep ID

//010 Count: 1 Min: 285575.00 Average: 285575.00 Max: 285575.00 Commission: 19990.25
//111 Count: 1 Min: 9025.00 Average: 9025.00 Max: 9025.00 Commission: 631.75
//222 Count: 1 Min: 8190.00 Average: 8190.00 Max: 8190.00 Commission: 327.60
//123 Count: 1 Min: 2140.00 Average: 2140.00 Max: 2140.00 Commission: 149.80
//910 Count: 1 Min: 750.00 Average: 750.00 Max: 750.00 Commission: 30.00