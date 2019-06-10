package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceCard extends Card implements Cloneable {

    private String element;

    @Override
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "[Name: " + super.getName() +
                ", Clan: " + super.getClan() +
                ", Element: " + this.element +
                ", Restrictions: " + super.getElementLimit() + "/" + super.getRoleLimit() +
                ", Restricted: " + this.getIsRestricted() +
                ", ID: " + super.getId() + "] ---> " + this.getQuantity() + " copies.";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        else if (!(o instanceof ProvinceCard))
            return false;
        ProvinceCard card = (ProvinceCard) o;
        return card.getIdFiveRingsDB().equals(this.getIdFiveRingsDB());
    }

}
