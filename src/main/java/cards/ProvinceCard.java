package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceCard extends Card {

    private String element;

    @Override
    public String toString() {
        return "[Name: "+super.getName()+
                ", Clan: "+super.getClan()+
                ", Element: "+this.element+
                ", Restrictions: "+super.getElementLimit()+"/"+super.getRoleLimit()+
                ", ID: "+super.getId()+"]";
    }

}
