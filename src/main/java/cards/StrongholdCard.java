package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrongholdCard extends Card implements Cloneable {

    private Integer influence;

    @Override
    public Object clone() throws
            CloneNotSupportedException
    {
        return super.clone();
    }

    @Override
    public String toString() {
        return "[Name: "+super.getName()+
                ", Clan: "+super.getClan()+
                ", Influence: +"+this.influence+
                ", ID: "+super.getId()+"] ---> "+this.getQuantity()+" copies";
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        else if(!(o instanceof StrongholdCard))
            return false;
        StrongholdCard card = (StrongholdCard) o;
        return card.getIdFiveRingsDB().equals(this.getIdFiveRingsDB());
    }

}
