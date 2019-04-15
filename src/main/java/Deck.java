import cards.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    private String clan;
    private String splash;
    private RoleCard role;
    private Integer influence;

    private StrongholdCard stronghold;

    private List<ProvinceCard> provinces;

    private List<ConflictCard> conflictCardDeck;
    private List<DynastyCard> dynastyCardDeck;

}
