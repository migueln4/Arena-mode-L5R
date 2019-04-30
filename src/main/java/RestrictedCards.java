import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RestrictedCards {

    //Last update: 29/04/2019

    private Map<String,List<String>> restrictedLists;

    public RestrictedCards() {

        this.restrictedLists = new HashMap<>();

        List<String> restrictedConflictCards = new ArrayList<>();
        List<String> restrictedProvinceCards = new ArrayList<>();
        List<String> restrictedDynastyCards = new ArrayList<>();

        restrictedConflictCards.add("Rebuild");
        restrictedConflictCards.add("Mirumoto's Fury");
        restrictedConflictCards.add("For Greater Glory ");
        restrictedConflictCards.add("Forged Edict");
        restrictedConflictCards.add("Charge!");
        restrictedConflictCards.add("Pathfinder's Blade");
        restrictedConflictCards.add("Policy Debate");
        restrictedConflictCards.add("A Fate Worse than Death");
        restrictedConflictCards.add("Void Fist");

        restrictedDynastyCards.add("Isawa Tadaka");
        restrictedDynastyCards.add("Niten Master");
        restrictedDynastyCards.add("Young Rumormonger");
        restrictedDynastyCards.add("Guest of Honor");

        restrictedProvinceCards.add("Feast or Famine");

        this.restrictedLists.put("Conflict",restrictedConflictCards);
        this.restrictedLists.put("Dynasty",restrictedDynastyCards);
        this.restrictedLists.put("Province",restrictedProvinceCards);
    }

}
