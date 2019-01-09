import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor
public class CreateDecksL5R {

    private Map<String, Card> allCards;
    private Map<String, RoleCard> roleCards;

    public static void main(String[] args) {

        CreateDecksL5R l5r = new CreateDecksL5R();
        Map<String,Card> cards = l5r.readCardData();
        // Esto lo que hace es imprimir todas las cartas
        // cards.entrySet().stream().forEach(main.printit);
        System.out.println("TAMAÑO: "+cards.size());
        List<Card> listaCartas = new ArrayList<>(cards.values());
        // Esto lo que hace es borrar las cartas que no sean del clan que se indica
        // listaCartas.removeIf(carta -> !carta.getClan().equals("lion") && !carta.getClan().equals("neutral"));

        //Esto actualiza a tres cores en la colección
        listaCartas.stream().filter(carta -> carta.getPackCards().getPack().getId().equals("core")).forEach(carta -> carta.getPackCards().setQuantity(carta.getPackCards().getQuantity()*3));
        // Esto imprime los roles que hay en la colección
        // listaCartas.stream().filter(carta -> carta.getDeck().equals("role")).forEach(System.out::println);

        System.out.println("TAMAÑO LISTA "+listaCartas.size());
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
        String clan = (String) json.get("clan");
        if(clan.equals("lion"))
            System.out.println(clan);
        card.setId((String) json.get("id"));
        card.setClan((String) json.get("clan"));
        card.setName((String) json.get("name"));
        card.setDeck((String) json.get("side"));
        card.setRoleRestriction((String) json.get("role_restriction"));
        card.setLimit((Long) json.get("deck_limit"));

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
