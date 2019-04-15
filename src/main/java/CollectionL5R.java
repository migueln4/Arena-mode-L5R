import cards.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import interfaces.ThreeParametersLambda;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.util.function.Function;

public class CollectionL5R {

    //https://api.fiveringsdb.com/cards GET

    private List<ConflictCard> conflictCardList;
    private List<DynastyCard> dynastyCardList;
    private List<ProvinceCard> provinceCardList;
    private List<RoleCard> roleCardList;
    private List<StrongholdCard> strongStrongholdCardList;

    private JsonParser parser;

    private Gson gson;

    private File file;
    private Object obj;

    private JsonObject jsonObject;
    private JsonArray records;

    private Type type;
    private List<JsonCard> allCards;

    @SuppressWarnings("unchecked")
    public void readFile() {
        this.conflictCardList = new ArrayList<>();
        this.dynastyCardList = new ArrayList<>();
        this.provinceCardList = new ArrayList<>();
        this.roleCardList = new ArrayList<>();
        this.strongStrongholdCardList = new ArrayList<>();
        this.parser = new JsonParser();
        this.gson = new Gson();
        this.file = getCardFileReader.apply("todascartas.json");
        try {
            this.obj = parser.parse(new FileReader(file));
            this.jsonObject = (JsonObject) obj;
            this.records = jsonObject.getAsJsonArray("records");
            this.type = new TypeToken<List<JsonCard>>() {}.getType();
            this.allCards = gson.fromJson(records,type);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeConflictCardList() {
            allCards.stream().filter(card -> card.getSide().equals("conflict")).
                    forEach(card -> conflictCardList.add(jsonCardToConflictCard.apply(card)));
            System.out.println(conflictCardList.get(0).toString());
        System.out.println("Hasta aquí, todo OK");
    }

    public void initializeDynastyCardList() {
        System.out.println("Vengo a la dinastía");
            allCards.stream().filter(card -> card.getSide().equals("dynasty")).
                    forEach(card -> dynastyCardList.add(jsonCardToDynastyCard.apply(card)));
        System.out.println(dynastyCardList.get(0).toString());
        System.out.println(dynastyCardList.size());
    }


    private ThreeParametersLambda<String,String,String> getCardName = (name,number) -> {
        StringBuilder strb = new StringBuilder("[");
        strb.append(name.substring(1,name.length()-1));
        strb.append(" - #");
        strb.append(number);
        strb.append("]");

        return strb.toString();
    };

    private Function<JsonCard, DynastyCard> jsonCardToDynastyCard = jsonCard -> {
        DynastyCard newCard = new DynastyCard();
        Type type = new TypeToken<List<JsonPackCards>>() {}.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(),type);

        String nameCard = packCards.get(0).getPack().get("id").toString();

        newCard.setId(getCardName.apply(nameCard,packCards.get(0).getPosition()));

        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());

        JsonArray traits = jsonCard.getTraits();
        elementAndRoleRestrictions(newCard,traits);

        newCard.setElement(newCard.getElementLimit());

        return newCard;
    };

    private void elementAndRoleRestrictions(Card newCard, JsonArray traits) {
        for(JsonElement trait : traits) {
            switch (trait.getAsString()) {
                case "keeper":
                    newCard.setRoleLimit("keeper");
                    break;
                case "seeker":
                    newCard.setRoleLimit("seeker");
                    break;
                case "air":
                    newCard.setElementLimit("air");
                    break;
                case "water":
                    newCard.setElementLimit("water");
                    break;
                case "fire":
                    newCard.setElementLimit("fire");
                    break;
                case "void":
                    newCard.setElementLimit("void");
                    break;
                case "earth":
                    newCard.setElementLimit("earth");
                    break;
                default:
                    break;
            }
        }
    }

    private Function<JsonCard,ConflictCard> jsonCardToConflictCard = jsonCard -> {
        ConflictCard newCard = new ConflictCard();
        Type type = new TypeToken<List<JsonPackCards>>() {}.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(),type);

        String nameCard = packCards.get(0).getPack().get("id").toString();

        newCard.setId(getCardName.apply(nameCard,packCards.get(0).getPosition()));
        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());

        newCard.setClan(jsonCard.getClan());

        JsonArray traits = jsonCard.getTraits();

        elementAndRoleRestrictions(newCard,traits);

            if("character".equals(jsonCard.getType()))
                newCard.setCharacter(Boolean.TRUE);
            else
                newCard.setCharacter(Boolean.FALSE);

            if(jsonCard.getInfluence_cost() != null)
                newCard.setInfluence(jsonCard.getInfluence_cost());


        return newCard;
    };



    /**
     * Obtiene un archivo con un nombre específico.
     */
    private Function<String, File> getCardFileReader = filename -> {
        ClassLoader cl = getClass().getClassLoader();
        File file = new File(cl.getResource(filename).getFile());
        return file;
    };

}
