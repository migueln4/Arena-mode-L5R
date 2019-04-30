import cards.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    private String namePlayer;

    private String clan;
    private String splash;
    private RoleCard roleCard;
    private String role;
    private String element;
    private Integer influence;
    private Integer numberCharacters;

    private StrongholdCard stronghold;

    private List<ProvinceCard> provinces;

    private List<ConflictCard> conflictCardDeck;
    private List<DynastyCard> dynastyCardDeck;
    private Integer numberConflictCards;
    private Integer numberDynastyCards;

    private Integer[] limitProvince;

    private Boolean containsRestrictedCards;

    public Deck(String namePlayer) {
        this.numberCharacters = 0;
        this.numberConflictCards = 0;
        this.numberDynastyCards = 0;
        this.namePlayer = namePlayer;
        this.influence = 0;
        this.limitProvince = new Integer[]{1,1,1,1,1};
        this.provinces = new ArrayList<>();
        this.conflictCardDeck = new ArrayList<>();
        this.dynastyCardDeck = new ArrayList<>();
        this.containsRestrictedCards = Boolean.FALSE;
    }

}
