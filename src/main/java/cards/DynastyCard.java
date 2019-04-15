package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynastyCard extends Card {

    private String element;

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder(super.toString());
        strb.append("\n");
        strb.append("ConflictCard(element="+this.element+")");
        return strb.toString();
    }

}
