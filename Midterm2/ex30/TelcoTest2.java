package Midterm2.ex30;//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}

class Call {
    String dialer;
    String receiver;
    long timestamp;
    long totalCallDuration;
    long pausedCallDuration;
    long callEnd;
    CallState state;
    public Call(String dialer, String receiver, long timestamp) {
        this.dialer = dialer;
        this.receiver = receiver;
        this.timestamp = timestamp;
        totalCallDuration = 0;
        pausedCallDuration = 0;
        state = new AnswerState(this);
    }

    long getCurrentTotal() {
        return timestamp + totalCallDuration + pausedCallDuration;
    }

    void answer(long time) {
        state.answer(time);
    }
    void hold(long time) {
        state = new HoldState(this);
        state.hold(time);
    }
    void resume(long time) {
        state = new ResumeState(this);
        state.resume(time);
    }
    void end(long time) {
        state.end(time);
        state = new EndState(this);
        callEnd = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long callEnd() {
        return callEnd;
    }

    public long getTotalCallDuration() {
        return totalCallDuration;
    }
}

interface IState {
    void answer(long timestamp);
    void hold(long timestamp);
    void resume(long timestamp);
    void end(long timestamp);
}

abstract class CallState implements IState {
    Call call;

    public CallState(Call call) {
        this.call = call;
    }
}

class AnswerState extends CallState{
    boolean answeredCall;

    public AnswerState(Call call) {
        super(call);
        answeredCall = false;
    }

    @Override
    public void answer(long timestamp) {
        call.timestamp = timestamp;
        answeredCall = true;
    }

    @Override
    public void hold(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void resume(long timestamp) {
        throw new RuntimeException();

    }

    @Override
    public void end(long timestamp) {
        if (answeredCall)
            call.totalCallDuration += timestamp - call.getCurrentTotal();
    }
}

class HoldState extends CallState{

    public HoldState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void hold(long timestamp) {
        call.totalCallDuration += timestamp - call.getCurrentTotal();
    }

    @Override
    public void resume(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void end(long timestamp) {
        call.pausedCallDuration += timestamp - call.getCurrentTotal();
    }
}

class ResumeState extends CallState{

    public ResumeState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void hold(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void resume(long timestamp) {
        call.pausedCallDuration += timestamp - call.getCurrentTotal();
    }

    @Override
    public void end(long timestamp) {
        call.totalCallDuration += timestamp - call.getCurrentTotal();
    }
}

class EndState extends CallState{

    public EndState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void hold(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void resume(long timestamp) {
        throw new RuntimeException();
    }

    @Override
    public void end(long timestamp) {
        throw new RuntimeException();
    }
}

class TelcoApp {
    Map<String, Call> callsByUUID;
    Map<String, List<Call>> callsByNumber;

    static final Comparator<Call> TOTAL_CALL_DURATION =
            Comparator.comparing(Call::getTotalCallDuration).thenComparing(Call::callEnd).reversed();
    static final Comparator<Call> BY_TIMESTAMP_COMPARATOR = Comparator.comparing(Call::getTimestamp);

    static final Comparator<Map.Entry<String, Long>> BY_TOTAL_CALL_COMPARATOR =
            Map.Entry.comparingByValue(Comparator.reverseOrder());
    public TelcoApp() {
        this.callsByUUID = new HashMap<>();
        this.callsByNumber = new HashMap<>();
    }

    void addCall (String uuid, String dialer, String receiver, long timestamp) {
        Call call = new Call(dialer, receiver, timestamp);

        callsByUUID.put(uuid, call);

        callsByNumber.putIfAbsent(dialer, new ArrayList<>());
        callsByNumber.get(dialer).add(call);

        callsByNumber.putIfAbsent(receiver, new ArrayList<>());
        callsByNumber.get(receiver).add(call);
    }

    void updateCall (String uuid, long timestamp, String action) {
        Call call = callsByUUID.get(uuid);

        switch (action) {
            case "ANSWER" -> call.answer(timestamp);
            case "END" -> call.end(timestamp);
            case "HOLD" -> call.hold(timestamp);
            case "RESUME" -> call.resume(timestamp);
            default -> throw new RuntimeException();
        }
    }

    String printCallerInfo(Call call, String phoneNumber) {
        boolean isDialer = call.dialer.equals(phoneNumber);
        boolean isMissed = call.getTotalCallDuration() == 0;

        return String.format("%s %s %d %s %s",
                isDialer ? "D" : "R",
                isDialer ? call.receiver : call.dialer,
                isMissed ? call.callEnd() : call.getTimestamp(),
                isMissed ? "MISSED CALL" : String.valueOf(call.callEnd()),
                DurationConverter.convert(call.getTotalCallDuration()));
    }

    void printChronologicalReport(String phoneNumber) {
        callsByNumber.get(phoneNumber)
                .stream()
                .sorted(BY_TIMESTAMP_COMPARATOR)
                .forEach(call -> {
                    System.out.println(printCallerInfo(call, phoneNumber));
                });
    }

    void printReportByDuration(String phoneNumber) {
        callsByNumber.get(phoneNumber)
                .stream()
                .sorted(TOTAL_CALL_DURATION)
                .forEach(call -> {
                    System.out.println(printCallerInfo(call, phoneNumber));
                });
    }

    void printCallsDuration() {
        Map<String, Long> callsDuration = callsByUUID.values().stream()
                .collect(Collectors.groupingBy(
                        call -> String.format("%s <-> %s", call.dialer, call.receiver),
                        Collectors.summingLong(Call::getTotalCallDuration)
                ));

        callsDuration.entrySet()
                .stream()
                .sorted(BY_TOTAL_CALL_COMPARATOR)
                .forEach(entry -> System.out.printf("%s : %s\n",
                        entry.getKey(),
                        DurationConverter.convert(entry.getValue())));
    }
}


public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }

    }
}
