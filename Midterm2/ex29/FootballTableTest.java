package Midterm2.ex29;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Partial exam II 2016/2017
 */
public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

class Team {
    String name;
    int points;
    int wins;
    int draws;
    int losses;
    int goalsScored;
    int goalsTaken;


    public Team(String name) {
        this.name = name;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsScored = 0;
        this.goalsTaken = 0;
    }

    public void addPoints(int points, int scored, int taken) {
        if (points == 3)
            wins++;
        else if (points == 1)
            draws++;
        else
            losses++;
        this.points += points;
        this.goalsScored += scored;
        this.goalsTaken += taken;
    }

    public int getPoints() {
        return points;
    }

    public int goalDifference () {
        return goalsScored - goalsTaken;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5d%5d%5d%5d%5d",
                name,
                wins + draws + losses,
                wins,
                draws,
                losses,
                getPoints());
    }
}

class Game {
    static void calculatePoints (Team homeTeam, Team awayTeam, int homeGoals, int awayGoals) {
        if (homeGoals > awayGoals) {
            homeTeam.addPoints(3, homeGoals, awayGoals);
            awayTeam.addPoints(0, awayGoals, homeGoals);
        } else if (homeGoals < awayGoals) {
            homeTeam.addPoints(0, homeGoals, awayGoals);
            awayTeam.addPoints(3, awayGoals, homeGoals);
        } else {
            homeTeam.addPoints(1, homeGoals, awayGoals);
            awayTeam.addPoints(1, awayGoals, homeGoals);
        }
    }
}

class FootballTable {
    Map<String, Team> teams;

    public FootballTable() {
        this.teams = new HashMap<>();
    }

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teams.putIfAbsent(homeTeam, new Team(homeTeam));
        teams.putIfAbsent(awayTeam, new Team(awayTeam));
        Game.calculatePoints(teams.get(homeTeam), teams.get(awayTeam), homeGoals, awayGoals);
    }

    public void printTable() {
        Comparator<Team> comparator = Comparator
                .comparing(Team::getPoints)
                .thenComparing(Team::goalDifference).reversed().thenComparing(Team::getName);

        List<Team> sortedTeams = teams.values()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        for (int i = 0; i < sortedTeams.size(); i++) {
            System.out.printf("%2d. %s%n",
                    i + 1, sortedTeams.get(i));
        }
    }
}

