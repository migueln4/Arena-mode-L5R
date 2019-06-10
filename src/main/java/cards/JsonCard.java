package cards;

import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonCard {
    private String clan;
    private Integer cost;
    private Integer deck_limit;
    private Integer glory;
    private String id;
    private Integer military;
    private String name;
    private String name_canonical;
    private JsonArray pack_cards;
    private Integer political;
    private String side;
    private String text;
    private String text_canonical;
    private JsonArray traits;
    private String character;
    private Boolean unicity;
    private String type;
    private Integer influence_cost;
    private String role_restriction;
    private String element;
    private Integer influence_pool;
    private JsonArray allowed_clans;
    private Boolean is_restricted;
}
