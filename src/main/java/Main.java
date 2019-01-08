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
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        Main main = new Main();
        Map<String,Card> cards = main.readCardData();
        cards.entrySet().stream().forEach(main.printit);
        System.out.println("TAMAÑO: "+cards.size());

    }

    /**
     * Esto imrpime todas las cartas
     */

    private static Consumer<Map.Entry<String, Card>> printit = cartas -> {
        final Card card = cartas.getValue();
        System.out.println("Detalles ");
        System.out.println("========================================");
        System.out.println("Customer Key is: " + cartas.getKey());
        System.out.println("ID: " + card.getId());
        System.out.println("Nombre: " + card.getName());
        System.out.println("Clan: "+ card.getClan());
        System.out.println("Deck: "+card.getDeck());
        final PackCards packCards = card.getPackCards();
        System.out.println("Número: "+packCards.getNumber());
        System.out.println("Cantidad: "+packCards.getQuantity());
        final Pack pack = packCards.getPack();
        System.out.println("Pack: "+pack.getId());
        System.out.println();
    };

    /**
     * Lee todas las cartas del archivo y crea un mapa.
     * @return
     */
    public Map<String, Card> readCardData() {
        Map<String, Card> cards = null;

        File customer = getCardFileReader.apply("todascartas.json");
        JSONParser parser = new JSONParser();
        try (Reader is = new FileReader(customer)) {
            JSONArray jsonArray = (JSONArray) parser.parse(is);

            cards = (Map<String, Card>) jsonArray.stream().collect(
                    Collectors.toMap(key_card, value_requestCard));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return cards;
    }

    /**
     * Obtiene un archivo con un nombre específico.
     */
    public Function<String, File> getCardFileReader = filename -> {
        ClassLoader cl = getClass().getClassLoader();
        File file = new File(cl.getResource(filename).getFile());
        return file;
    };

    /**
     * Read the JSON entry and return Id
     */
    private Function<JSONObject, String> key_card = c -> (String) ((JSONObject) c)
            .get("id");

    /**
     * Read the JSON entry and return the request Customer
     */
    @SuppressWarnings("unchecked")
    private Function<JSONObject, Card> value_requestCard = json -> {
        Card card = new Card();
        card.setId((String) json.get("id"));
        card.setClan((String) json.get("clan"));
        card.setName((String) json.get("name"));
        card.setDeck((String) json.get("side"));


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
