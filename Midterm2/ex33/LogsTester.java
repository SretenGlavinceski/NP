package Midterm2.ex33;

import java.util.*;
import java.util.stream.Collectors;

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k,v)-> System.out.printf("%d -> %d%n", k,v));
            } else if (line.startsWith("displayLogs")){
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                collector.displayLogs(service, microservice, order);
            }
        }
    }
}

enum TypeLog {
    INFO, WARN, ERROR
}

enum TypeComparator {
    NEWEST_FIRST,
    OLDEST_FIRST,
    MOST_SEVERE_FIRST,
    LEAST_SEVERE_FIRST
}

class LogFactory {
    public static Log createLog (String s) {
        String [] parts = s.split("\\s+");
        String typeOfLog = parts[2];
        String message = Arrays.stream(parts, 3, parts.length - 1).collect(Collectors.joining(" "));
        long timestamp = Long.parseLong(parts[parts.length - 1]);

        if (typeOfLog.equals(TypeLog.INFO.toString()))
            return new InfoLog(parts[0], parts[1], typeOfLog, message, timestamp);
        else if (typeOfLog.equals(TypeLog.WARN.toString()))
            return new WarningLog(parts[0], parts[1], typeOfLog, message, timestamp);
        else if (typeOfLog.equals(TypeLog.ERROR.toString()))
            return new ErrorLog(parts[0], parts[1], typeOfLog, message, timestamp);
        else
            throw new RuntimeException();
    }
}

class ComparatorFactory {
    public static Comparator<Log> generateComparator (String s) {
        if (s.equals(TypeComparator.NEWEST_FIRST.toString()))
            return Comparator.comparing(Log::getTimestamp).reversed();
        else if (s.equals(TypeComparator.OLDEST_FIRST.toString()))
            return Comparator.comparing(Log::getTimestamp);
        else if (s.equals(TypeComparator.MOST_SEVERE_FIRST.toString()))
            return Comparator.comparing(Log::severity).thenComparing(Log::getTimestamp).reversed();
        else
            return Comparator.comparing(Log::severity);
    }
}

abstract class Log {
    String service;
    String microService;
    String typeOfLog;
    String message;
    long timestamp;

    public Log(String service, String microService, String typeOfLog, String message, long timestamp) {
        this.service = service;
        this.microService = microService;
        this.typeOfLog = typeOfLog;
        this.message = message;
        this.timestamp = timestamp;
    }

    abstract public int severity();

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [%s] %s %d T:%d",
                service,
                microService,
                typeOfLog,
                message,
                timestamp,
                timestamp);
    }
}

class InfoLog extends Log {
    public InfoLog(String service, String microService, String typeOfLog, String message, long timestamp) {
        super(service, microService, typeOfLog, message, timestamp);
    }

    @Override
    public int severity() {
        return 0;
    }
}

class WarningLog extends Log {
    private final static String ERROR_MESSAGE = "might cause error";

    public WarningLog(String service, String microService, String typeOfLog, String message, long timestamp) {
        super(service, microService, typeOfLog, message, timestamp);
    }

    @Override
    public int severity() {
        return message.contains(ERROR_MESSAGE) ? 2 : 1;
    }
}

class ErrorLog extends Log {

    private final static String FATAL_MESSAGE = "fatal";

    private final static String EXCEPTION_MESSAGE = "exception";

    public ErrorLog(String service, String microService, String typeOfLog, String message, long timestamp) {
        super(service, microService, typeOfLog, message, timestamp);
    }

    @Override
    public int severity() {
        int total = 3;
        if (message.contains(FATAL_MESSAGE))
            total += 2;
        if (message.contains(EXCEPTION_MESSAGE))
            total += 3;
        return total;
    }
}

class MicroServices {
    String microServiceName;
    List<Log> logs;

    public MicroServices(String microServiceName) {
        this.microServiceName = microServiceName;
        logs = new ArrayList<>();
    }

    public void addLogs (Log log) {
        logs.add(log);
    }

    public int numberOfLogs () {
        return logs.size();
    }

    public List<Log> getLogs() {
        return logs;
    }
}

class Service {
    String serviceName;
    Map<String, MicroServices> microServicesByName;

    public Service(String serviceName) {
        this.serviceName = serviceName;
        microServicesByName = new HashMap<>();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void addingComponents (String microServiceName, Log log) {
        microServicesByName.putIfAbsent(microServiceName, new MicroServices(microServiceName));
        microServicesByName.get(microServiceName).addLogs(log);
    }

    public MicroServices getMicroservice (String s) {
        return microServicesByName.get(s);
    }

    public List<Log> allLogsService() {
        return microServicesByName.values()
                .stream()
                .map(MicroServices::getLogs)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public int microServiceCount () {
        return microServicesByName.values().size();
    }

    double averageSeverityAllLogs() {
        return allLogsService().stream()
                .mapToInt(Log::severity)
                .average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("Service name: %s Count of microservices: %d Total logs in service: %d Average severity for all logs: %.2f Average number of logs per microservice: %.2f",
                serviceName,
                microServiceCount(),
                microServicesByName.values().stream().mapToInt(MicroServices::numberOfLogs).sum(),
                averageSeverityAllLogs(),
                microServicesByName.values().stream()
                        .mapToInt(MicroServices::numberOfLogs)
                        .average().orElse(0.0));
    }
}

class LogCollector {
    Map<String, Service> serviceByName;
    public LogCollector() {
        this.serviceByName = new HashMap<>();
    }

    void addLog (String log) {
        String [] parts = log.split("\\s+");
        String service = parts[0];
        String microService = parts[1];

        Service newService;

        if (serviceByName.containsKey(service)) {
            newService = serviceByName.get(service);
        } else {
            newService = new Service(service);
        }

        Log newLog = LogFactory.createLog(log);
        newService.addingComponents(microService, newLog);

        serviceByName.putIfAbsent(newService.getServiceName(), newService);
    }

    void printServicesBySeverity() {
        serviceByName
                .values()
                .stream()
                .sorted(Comparator.comparing(Service::averageSeverityAllLogs).reversed())
                .forEach(System.out::println);
    }

    Map<Integer, Integer> getSeverityDistribution (String service, String microservice) {
        if (microservice != null)
            return serviceByName.get(service).getMicroservice(microservice).getLogs()
                    .stream().collect(Collectors.groupingBy(
                            Log::severity,
                            Collectors.summingInt(i -> 1)
                    ));

        return serviceByName.get(service).allLogsService()
                .stream().collect(Collectors.groupingBy(
                        Log::severity,
                        Collectors.summingInt(i -> 1)
                ));
    }

    void displayLogs(String service, String microservice, String order) {
        List<Log> logsSorted = new ArrayList<>();

        if (microservice != null) {
            logsSorted.addAll(serviceByName.get(service).getMicroservice(microservice).getLogs());
            System.out.printf("displayLogs %s %s %s%n",
                    service,
                    microservice,
                    order);
        } else {
            logsSorted.addAll(serviceByName.get(service).allLogsService());
            System.out.printf("displayLogs %s %s%n",
                    service,
                    order);
        }

        logsSorted.stream().sorted(ComparatorFactory.generateComparator(order)).forEach(System.out::println);
    }
}