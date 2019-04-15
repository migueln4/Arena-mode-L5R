package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrongholdCard extends Card {

    //Se sabe por "type": "stronghold"

    private String clan;
    private Integer influence;

}
