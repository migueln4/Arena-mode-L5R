package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    private String namePlayer;

    private String clan;
    private String splash;
    private String role;
    private String element;
    private Integer influence;
    private Integer numberCharacters;

    private RoleCard roleCard;
    private StrongholdCard stronghold;
    private List<ProvinceCard> provinces;
    private List<ConflictCard> conflictCardDeck;
    private List<DynastyCard> dynastyCardDeck;

    private Integer numberConflictCards;
    private Integer numberDynastyCards;

    private Integer[] limitProvince;

    private Boolean containsRestrictedCards;

    private Map<String,Integer> traits;

    public Deck(String namePlayer) {
        this.numberCharacters = 0;
        this.numberConflictCards = 0;
        this.numberDynastyCards = 0;
        this.namePlayer = namePlayer;
        this.influence = 0;
        this.limitProvince = new Integer[]{1, 1, 1, 1, 1};
        this.provinces = new ArrayList<>();
        this.conflictCardDeck = new ArrayList<>();
        this.dynastyCardDeck = new ArrayList<>();
        this.containsRestrictedCards = Boolean.FALSE;
        this.traits = new HashMap<>();
    }

}
