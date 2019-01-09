import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Card {

    private String id;
    private String clan;
    private String name;
    private String deck;
    private String roleRestriction;
    private Long limit;
    private PackCards packCards;

    public String toString() {
        StringBuilder salida = new StringBuilder();
        salida.append("ID: "+getId()+"\n"+
                        "Clan: "+getClan()+"\n"+
                "Nombre: "+getName()+"\n"+
                "Mazo: "+getDeck()+"\n"+
                "Restricción de rol: "+getRoleRestriction()+"\n"+
                "Pack: "+getPackCards().getPack().getId()+"\n"+
                "Número: "+getPackCards().getNumber()+"\n"+
                "Cantidad: "+getPackCards().getQuantity()+"\n");
        return salida.toString();
    }

}
