package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConflictCard extends Card {

    private Boolean character;
    private Integer influence;

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder(super.toString());
        strb.append("\n");
        strb.append("ConflictCard(character="+this.character.toString()+", influence="+this.influence.toString()+")");
        return strb.toString();
    }

}
