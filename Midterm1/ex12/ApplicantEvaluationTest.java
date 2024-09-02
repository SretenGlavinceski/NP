package Midterm1.ex12;

import java.util.Scanner;
import java.util.function.Predicate;

/**
 * I partial exam 2016
 */
public class ApplicantEvaluationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        int creditScore = scanner.nextInt();
        int employmentYears = scanner.nextInt();
        boolean hasCriminalRecord = scanner.nextBoolean();
        int choice = scanner.nextInt();
        Applicant applicant = new Applicant(name, creditScore, employmentYears, hasCriminalRecord);
        Evaluator.TYPE type = Evaluator.TYPE.values()[choice];
        Evaluator evaluator = null;
        try {
            evaluator = EvaluatorBuilder.build(type);
            System.out.println("Applicant");
            System.out.println(applicant);
            System.out.println("Evaluation type: " + type.name());
            if (evaluator.evaluate(applicant)) {
                System.out.println("Applicant is ACCEPTED");
            } else {
                System.out.println("Applicant is REJECTED");
            }
        } catch (InvalidEvaluation invalidEvaluation) {
            System.out.println("Invalid evaluation");
        }
    }
}

class Applicant {
    private String name;

    private int creditScore;
    private int employmentYears;
    private boolean hasCriminalRecord;

    public Applicant(String name, int creditScore, int employmentYears, boolean hasCriminalRecord) {
        this.name = name;
        this.creditScore = creditScore;
        this.employmentYears = employmentYears;
        this.hasCriminalRecord = hasCriminalRecord;
    }

    public String getName() {
        return name;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getEmploymentYears() {
        return employmentYears;
    }

    public boolean hasCriminalRecord() {
        return hasCriminalRecord;
    }

    @Override
    public String toString() {
        return String.format("Name: %s\nCredit score: %d\nExperience: %d\nCriminal record: %s\n",
                name, creditScore, employmentYears, hasCriminalRecord ? "Yes" : "No");
    }
}

interface Evaluator {
    enum TYPE {
        NO_CRIMINAL_RECORD,
        MORE_EXPERIENCE,
        MORE_CREDIT_SCORE,
        NO_CRIMINAL_RECORD_AND_MORE_EXPERIENCE,
        MORE_EXPERIENCE_AND_MORE_CREDIT_SCORE,
        NO_CRIMINAL_RECORD_AND_MORE_CREDIT_SCORE,
        INVALID // should throw exception
    }

    boolean evaluate(Applicant applicant);
}

class EvaluatorBuilder {
    public static Evaluator build(Evaluator.TYPE type) throws InvalidEvaluation {

        Predicate<Applicant> criminalRecord = applicant -> !applicant.hasCriminalRecord();
        Predicate<Applicant> experience = applicant -> applicant.getEmploymentYears() >= 10;
        Predicate<Applicant> creditScore = applicant -> applicant.getCreditScore() >= 500;

        if (type.equals(Evaluator.TYPE.NO_CRIMINAL_RECORD))
            return criminalRecord::test;

        else if (type.equals(Evaluator.TYPE.MORE_EXPERIENCE))
            return experience::test;

        else if (type.equals(Evaluator.TYPE.MORE_CREDIT_SCORE))
            return creditScore::test;

        else if (type.equals(Evaluator.TYPE.NO_CRIMINAL_RECORD_AND_MORE_EXPERIENCE))
            return applicant -> criminalRecord.test(applicant) && experience.test(applicant);

        else if (type.equals(Evaluator.TYPE.MORE_EXPERIENCE_AND_MORE_CREDIT_SCORE))
            return applicant -> experience.test(applicant) && creditScore.test(applicant);

        else if (type.equals(Evaluator.TYPE.NO_CRIMINAL_RECORD_AND_MORE_CREDIT_SCORE))
            return applicant -> criminalRecord.test(applicant) && creditScore.test(applicant);

        else
            throw new InvalidEvaluation();

    }
}

class InvalidEvaluation extends Exception {
    public InvalidEvaluation() {
    }
}






