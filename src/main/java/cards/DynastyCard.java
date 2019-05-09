package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynastyCard extends Card implements Cloneable {

    private String element;

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
                ", ID: "+super.getId()+
                ", Limit: "+super.getDeckLimit()+"]"+
                "---> "+super.getQuantity()+" copies";
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        else if(!(o instanceof DynastyCard))
            return false;
        DynastyCard card = (DynastyCard) o;
        return card.getIdFiveRingsDB().equals(this.getIdFiveRingsDB());
    }
}
