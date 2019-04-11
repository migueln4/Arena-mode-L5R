import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class CollectionL5R {

    private List<RoleCard> roleCards;
    private Map<String,Card> allCards;

    public CollectionL5R() {
        allCards = readCardData();
        roleCards = takeRoleCards.apply("role");
    }



    private Map<String, Card> readCardData() {
        Map<String, Card> cards = null;

        File archivo = getCardFileReader.apply("todascartas.json");
        JSONParser parser = new JSONParser();
        try (Reader is = new FileReader(archivo)) {
            JSONArray jsonArray = (JSONArray) parser.parse(is);

            cards = (Map<String, Card>) jsonArray.stream().collect(
                    Collectors.toMap(key_card, value_requestCard));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return cards;
    }

    //Cuando se llama, se hace: nombrefuncion(dato)
    //Esto se construye: private Function<TipoQueRecibe,TipoQueDevuelve> nombre = variablequerecibe -> {...}
    private Function<String, File> getCardFileReader = filename -> {
        ClassLoader cl = getClass().getClassLoader();
        File file = new File(cl.getResource(filename).getFile());
        return file;
    };

    private Function<String,List<RoleCard>> takeRoleCards = typeCard -> {
      List<RoleCard> result = new ArrayList<>();
      allCards.values().stream().filter(card -> card.getType().equals(typeCard)).forEach(card -> result.add(cardToRoleCard(card)));
      return result;
    };

    //TODO --> cardToRoleCard

    private Function<JSONObject, String> key_card = c -> (String) ((JSONObject) c)
            .get("id");


    private Function<JSONObject, Card> value_requestCard = json -> {
        Card card = new Card();
        card.setId((String) json.get("id"));
        card.setClan((String) json.get("clan"));
        card.setName((String) json.get("name"));
        card.setDeck((String) json.get("side"));
        card.setRoleRestriction((String) json.get("role_restriction"));
        card.setLimit((Long) json.get("deck_limit"));
        card.setType((String) json.get("type"));

        ArrayList<PackCards> packCardsList = new ArrayList<>();
        JSONArray packCardsJsonArray = (JSONArray) json.get("pack_cards");
        Iterator<JSONObject> itr = packCardsJsonArray.iterator();
        PackCards packCardsObj = null;
        while(itr.hasNext()) {
            final JSONObject packCards = itr.next();

            final JSONObject pack = (JSONObject) packCards.get("pack");

            packCardsList.add(new PackCards(
                    (String)packCards.get("position"),
                    (Long)packCards.get("quantity"),
                    new Pack((String)pack.get("id"))
            ));

            packCardsObj = packCardsList.get(0);
        }

        card.setPackCards(packCardsObj);

        return card;
    };

}
