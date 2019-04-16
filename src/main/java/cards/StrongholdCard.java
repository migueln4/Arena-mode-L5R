package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrongholdCard extends Card {

    //Se sabe por "type": "stronghold"
    private Integer influence;

    @Override
    public String toString() {
        return "[Name: "+super.getName()+
                ", Clan: "+super.getClan()+
                ", Influence: +"+this.influence+
                ", ID: "+super.getId()+"]";
    }

}
