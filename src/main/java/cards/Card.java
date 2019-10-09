package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private String id; // nombre del pack + número
    private String name;
    private Integer deckLimit;
    private String clan; //lion, scorpion, phoenix, crab, crane, unicorn, dragon, NULL
    private String roleLimit; //keeper, seeker, NULL
    private String elementLimit; //air, void, fire, earth, water, NULL
    private Integer quantity;
    private String idFiveRingsDB;
    private Boolean isRestricted;
    private Boolean unicity;
    private String name_extra;
    private List<String> traits;
}
