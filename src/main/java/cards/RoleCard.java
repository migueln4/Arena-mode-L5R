package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCard extends Card implements Cloneable {

    private String role;
    private String element;
    private String roleClan;

    @Override
    public String toString() {
        return "[Name: " + super.getName() +
                ", Role: " + this.role +
                ", Element: " + this.element +
                ", Clan: " + this.roleClan +
                ", ID: " + super.getId() + "]-->" + this.getQuantity() + " copies.";
    }

    @Override
    public Object clone() throws
            CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        else if (!(o instanceof RoleCard))
            return false;
        RoleCard card = (RoleCard) o;
        return card.getIdFiveRingsDB().equals(this.getIdFiveRingsDB());
    }

}
