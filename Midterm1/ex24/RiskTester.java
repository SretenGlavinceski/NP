package Midterm1.ex24;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskTester {
    public static void main(String[] args) {

        Risk risk = new Risk();

        System.out.println(risk.processAttacksData(System.in));

    }
}

class Attack {
    ArrayList<Integer> attacker;
    ArrayList<Integer> defender;

    public Attack(String s) {
        attacker = new ArrayList<>();
        defender = new ArrayList<>();

        String [] parts = s.split(";");

        String [] partsAttack = parts[0].split("\\s+");
        String [] partsDefend = parts[1].split("\\s+");

        for (int i = 0; i < partsAttack.length; i++) {
            attacker.add(Integer.parseInt(partsAttack[i]));
            defender.add(Integer.parseInt(partsDefend[i]));
        }

    }

    boolean successAttacks() {
        attacker = attacker.stream().sorted().collect(Collectors.toCollection(ArrayList::new));
        defender = defender.stream().sorted().collect(Collectors.toCollection(ArrayList::new));

        return IntStream.range(0, attacker.size())
                .filter(i -> attacker.get(i) > defender.get(i)).count() == attacker.size();
    }
}

class Risk {
    List<Attack> attacks;
    int processAttacksData (InputStream is) {

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        attacks = br.lines().map(Attack::new).collect(Collectors.toCollection(ArrayList::new));

        return (int) attacks.stream().filter(Attack::successAttacks).count();
    }
}