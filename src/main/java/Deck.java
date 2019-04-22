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

    final Integer MAX_CONFLICT_CARDS = 45;
    final Integer MIN_CONFLICT_CARDS = 40;
    final Integer MAX_CONFLICT_CHARACTERS = 10;
    final Integer MAX_PROVINCE_CARDS = 5;
    final Integer MAX_STRONGHOLD_CARDS = 1;

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

    private Integer[] limitProvince;

    public Deck(String namePlayer) {
        this.namePlayer = namePlayer;
        this.influence = 0;
        this.limitProvince = new Integer[]{1,1,1,1,1};
        this.provinces = new ArrayList<>();
    }

}
