package Midterm1.ex27;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskTester {
    public static void main(String[] args) throws IOException {
        Risk risk = new Risk();
        risk.processAttacksData(System.in);
    }
}

class Solder {
    List<Integer> attacker;
    List<Integer> defender;

    public Solder(String s) {
        attacker = new ArrayList<>();
        defender = new ArrayList<>();

        String [] parts = s.split(";");

        String [] attackParts = parts[0].split("\\s+");
        String [] defenderParts = parts[1].split("\\s+");

        for (int i = 0; i < attackParts.length; i++) {
            attacker.add(Integer.parseInt(attackParts[i]));
            defender.add(Integer.parseInt(defenderParts[i]));
        }
    }

    public void displayBattle() {
        attacker = attacker.stream().sorted().collect(Collectors.toList());
        defender = defender.stream().sorted().collect(Collectors.toList());

        System.out.printf("%d %d\n",
                IntStream.range(0, attacker.size()).filter(i -> attacker.get(i) > defender.get(i)).count(),
                3 - IntStream.range(0, attacker.size()).filter(i -> attacker.get(i) > defender.get(i)).count());
    }
}

class Risk {
    List<Solder> solders;
    void processAttacksData (InputStream is) throws IOException {
        solders = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.lines().forEach(line -> solders.add(new Solder(line)));
        solders.forEach(Solder::displayBattle);

        br.close();
    }

}