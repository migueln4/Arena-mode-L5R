import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deck {

    static final Integer MIN_CARDS = 40;
    static final Integer MAX_CARDS = 45;

    private Card role;
    private String clan;
    private String splash;
    private Integer influence;

    private Card stronghold;
    private List<Card> provinces;
    private List<Card> conflict;
    private Integer conflict_characters;
    private List<Card> Dynasty;

}