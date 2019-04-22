package cards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynastyCard extends Card implements Cloneable {

    private String element;

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof DynastyCard)) return false;
        DynastyCard other = (DynastyCard) o;
        return (this.getId().equals(other.getId()));
    }

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
                ", ID: "+super.getId()+"]"+
                "---> "+super.getQuantity()+" copies";
    }

}
