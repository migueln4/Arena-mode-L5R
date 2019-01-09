import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jugador {

    private List<Card> mazo;
    private String roleRestrictionElement;
    private String roleRestriction;
    private String clan;
    private String clanSecundario;

}
