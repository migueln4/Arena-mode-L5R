package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConflictCard extends Card implements Cloneable {

    private Boolean character;
    private Integer influence;

    @Override
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "[Name: " + super.getName() +
                ", Clan: " + super.getClan() +
                ", ID: " + super.getId() +
                ", Limit: " + super.getDeckLimit() +
                ", Character: " + this.character +
                ", Influence: " + this.influence + "]" +
                "---> " + super.getQuantity() + " copies";
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        else if(!(o instanceof ConflictCard))
            return false;
        ConflictCard card = (ConflictCard) o;
        return card.getIdFiveRingsDB().equals(this.getIdFiveRingsDB());
    }

}
