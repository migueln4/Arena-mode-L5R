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
    public boolean equals(Object o) {
        if(!(o instanceof ProvinceCard)) return false;
        ProvinceCard other = (ProvinceCard) o;
        return (this.getId().equals(other.getId()));
    }

    @Override
    public String toString() {
        return "[Name: "+super.getName()+
                ", Clan: "+super.getClan()+
                ", Element: "+this.element+
                ", Restrictions: "+super.getElementLimit()+"/"+super.getRoleLimit()+
                ", ID: "+super.getId()+"]";
    }

}
