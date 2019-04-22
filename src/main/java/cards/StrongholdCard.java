package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrongholdCard extends Card {

    private Integer influence;

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof StrongholdCard)) return false;
        StrongholdCard other = (StrongholdCard) o;
        return (this.getId().equals(other.getId()));
    }

    @Override
    public String toString() {
        return "[Name: "+super.getName()+
                ", Clan: "+super.getClan()+
                ", Influence: +"+this.influence+
                ", ID: "+super.getId()+"]";
    }

}
