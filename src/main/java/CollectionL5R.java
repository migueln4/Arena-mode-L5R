import cards.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import interfaces.ThreeParametersLambda;
import lombok.Data;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.util.function.Function;

@Data
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
            this.type = new TypeToken<List<JsonCard>>() {
            }.getType();
            this.allCards = gson.fromJson(records, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeConflictCardList() {
        allCards.stream().filter(card -> card.getSide().equals("conflict")).
                forEach(card -> conflictCardList.add(jsonCardToConflictCard.apply(card)));
        System.out.println("Conflict Card List: OK");
    }

    public void initializeDynastyCardList() {
        allCards.stream().filter(card -> card.getSide().equals("dynasty")).
                forEach(card -> dynastyCardList.add(jsonCardToDynastyCard.apply(card)));
        System.out.println("Dynasty Card List: OK");

    }

    public void initializeProvinceCardList() { //OJO con la provincia Toshi Ranbo, que está inédita todavía
        allCards.stream().filter(card -> !card.getId().equals("toshi-ranbo") && card.getSide().equals("province")).
                forEach(card -> provinceCardList.add(jsonCardToProvinceCard.apply(card)));
        System.out.println("Province Card List: OK");
    }

    public void initializeRoleCardList() {
        allCards.stream().filter(card -> card.getSide().equals("role")).
                forEach(card -> roleCardList.add(jsonCardToRoleCard.apply(card)));
        System.out.println("Role Card List: OK");
    }

    public void initializeStrongholdCardList() {
        allCards.stream().filter(card -> card.getSide().equals("stronghold")).
                forEach(card -> strongStrongholdCardList.add(jsonCardToStrongholdCard.apply(card)));
        System.out.println("Stronghold Card List: OK");
    }

    private Function<String, String> getPackName = str -> {
        switch (str) {
            case "core":
                return "CORE";
            case "tears-of-amaterasu":
            case "for-honor-and-glory":
            case "into-the-forbidden-city":
            case "the-chrysanthemun-throne":
            case "fate-has-no-secrets":
            case "meditations-on-the-ephemeral":
                return "IMPERIAL CYCLE";
            case "breath-of-the-kami":
            case "tainted-lands":
            case "the-fires-within":
            case "the-ebb-and-flow":
            case "all-and-nothing":
            case "elements-unbound":
                return "ELEMENTAL CYCLE";
            case "warriors-of-the-wind":
                return "UNICORN CLAN PACK";
            case "masters-of-the-court":
                return "CRANE CLAN PACK";
            case "disciples-of-the-void":
                return "PHOENIX CLAN PACK";
            case "underhand-of-the-emperor":
                return "SCORPION CLAN PACK";
            case "children-of-the-empire":
                return "PREMIUM EXPANSIONS #1";
            default:
                return null;
        }
    };

    private ThreeParametersLambda<String, String, String> getCardName = (name, number) -> {
        StringBuilder strb = new StringBuilder("[");
        strb.append(name);
        strb.append(" (");
        strb.append(getPackName.apply(name));
        strb.append(")");
        strb.append(" - #");
        strb.append(number);
        strb.append("]");

        return strb.toString();
    };

    private Function<JsonCard, DynastyCard> jsonCardToDynastyCard = jsonCard -> {
        DynastyCard newCard = new DynastyCard();
        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        String nameCard = packCards.get(0).getPack().get("id").toString();
        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(0).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(0).getQuantity());

        newCard.setId(getCardName.apply(nameCard, packCards.get(0).getPosition()));

        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());

        if (jsonCard.getRole_restriction() != null)
            elementAndRoleRestrictions(newCard, jsonCard.getRole_restriction());

        newCard.setElement(newCard.getElementLimit());

        return newCard;
    };

    private Function<JsonCard, ProvinceCard> jsonCardToProvinceCard = jsonCard -> {
        ProvinceCard newCard = new ProvinceCard();
        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        String nameCard = packCards.get(0).getPack().get("id").toString();
        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(0).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(0).getQuantity());

        newCard.setId(getCardName.apply(nameCard, packCards.get(0).getPosition()));

        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());

        if (jsonCard.getRole_restriction() != null)
            elementAndRoleRestrictions(newCard, jsonCard.getRole_restriction());

        newCard.setElement(jsonCard.getElement());

        return newCard;
    };

    private Function<JsonCard, StrongholdCard> jsonCardToStrongholdCard = jsonCard -> {
      StrongholdCard newCard = new StrongholdCard();

        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        String nameCard = packCards.get(0).getPack().get("id").toString();
        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(0).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(0).getQuantity());

        newCard.setId(getCardName.apply(nameCard, packCards.get(0).getPosition()));

        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());

        newCard.setInfluence(jsonCard.getInfluence_pool());

      return newCard;
    };

    private Function<JsonCard, RoleCard> jsonCardToRoleCard = jsonCard -> {
        RoleCard newCard = new RoleCard();

        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        String nameCard = packCards.get(0).getPack().get("id").toString();
        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(0).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(0).getQuantity());

        newCard.setId(getCardName.apply(nameCard, packCards.get(0).getPosition()));

        String name = jsonCard.getName();
        newCard.setName(name);

        JsonArray traits = jsonCard.getTraits();

        if (name.contains("Support")) {
            newCard.setClan(traits.get(0).toString().substring(1, traits.get(0).toString().length() - 1));
        } else {
            newCard.setRole(traits.get(0).toString());
            newCard.setElement(traits.get(1).toString());
        }

        return newCard;
    };

    private void elementAndRoleRestrictions(Card newCard, String role) {
        switch (role) {
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

    private Function<JsonCard, ConflictCard> jsonCardToConflictCard = jsonCard -> {
        ConflictCard newCard = new ConflictCard();
        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        String nameCard = packCards.get(0).getPack().get("id").toString();
        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(0).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(0).getQuantity());

        newCard.setId(getCardName.apply(nameCard, packCards.get(0).getPosition()));
        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());

        if (jsonCard.getRole_restriction() != null)
            elementAndRoleRestrictions(newCard, jsonCard.getRole_restriction());

        if ("character".equals(jsonCard.getType()))
            newCard.setCharacter(Boolean.TRUE);
        else
            newCard.setCharacter(Boolean.FALSE);


        if (jsonCard.getInfluence_cost() != null)
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
