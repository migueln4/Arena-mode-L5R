import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card {

    private String id;
    private String clan;
    private String name;
    private String deck;
    private PackCards packCards;

}
