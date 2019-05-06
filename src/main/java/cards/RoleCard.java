package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCard extends Card {

    private String role;
    private String element;
    private String roleClan;

    @Override
    public String toString() {
        return "[Name: "+super.getName()+
                ", Role: "+this.role+
                ", Element: "+this.element+
                ", Clan: "+this.roleClan+
                ", ID: "+super.getId()+"]";
    }
}
