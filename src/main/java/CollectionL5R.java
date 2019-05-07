import cards.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import interfaces.ThreeParametersLambda;
import lombok.Data;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.util.function.Function;

@Data
public class CollectionL5R {

    private final String FIVERINGSDB = "https://api.fiveringsdb.com/cards";

    private List<ConflictCard> conflictCardList;
    private List<DynastyCard> dynastyCardList;
    private List<ProvinceCard> provinceCardList;
    private List<RoleCard> roleCardList;
    private List<StrongholdCard> strongholdCardList;

    private JsonParser parser;

    private Gson gson;

    private File file;
    private Object obj;

    private JsonObject jsonObject;
    private JsonArray records;

    private List<JsonCard> allCards;

    public CollectionL5R() {
        this.conflictCardList = new ArrayList<>();
        this.dynastyCardList = new ArrayList<>();
        this.provinceCardList = new ArrayList<>();
        this.roleCardList = new ArrayList<>();
        this.strongholdCardList = new ArrayList<>();
        this.parser = new JsonParser();
        this.gson = new Gson();
        readURL();
    }

    private void readURL(){
        try {
            URL url = new URL(FIVERINGSDB);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if(connection.getResponseCode() != 200)
                readFile();
            System.out.println("Connection: OK");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            readFile(content);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void readFile(StringBuilder content) {
        this.obj = parser.parse(content.toString());
        this.jsonObject = (JsonObject) obj;
        this.records = jsonObject.getAsJsonArray("records");
        Type type = new TypeToken<List<JsonCard>>() {
        }.getType();
        this.allCards = gson.fromJson(records, type);
    }

    @SuppressWarnings("unchecked")
    public void readFile() {
        System.out.println("Connection OFF --> reading file...");
        this.file = getCardFileReader.apply("todascartas.json");
        try {
            this.obj = parser.parse(new FileReader(file));
            this.jsonObject = (JsonObject) obj;
            this.records = jsonObject.getAsJsonArray("records");
            Type type = new TypeToken<List<JsonCard>>() {
            }.getType();
            this.allCards = gson.fromJson(records, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteExceptions() {
        List<JsonCard> newList = new ArrayList<>(this.allCards);
        newList.stream().
                filter(obj -> obj != null)
                .filter(
                        card -> {
                            Type type = new TypeToken<List<JsonPackCards>>() {
                            }.getType();
                            List<JsonPackCards> packCards = gson.fromJson(card.getPack_cards(),
                                    type);
                            return packCards.size() == 0
                                    || "\"the-emperor-s-legion\"".equals(packCards.get(0).getPack().get("id").toString())
                                    || "\"bonds-of-blood\"".equals(packCards.get(0).getPack().get("id").toString())
                                    || "\"justice-for-satsume\"".equals(packCards.get(0).getPack().get("id").toString())
                                    || "\"for-the-empire\"".equals(packCards.get(0).getPack().get("id").toString())
                                    || "\"the-children-of-heaven\"".equals(packCards.get(0).getPack().get("id").toString());
                        }
                ).forEach(card -> this.allCards.remove(card));
        System.out.println("Total number of cards " + this.allCards.size());

    }

    public void initializeConflictCardList() {
        allCards.stream().filter(card -> card.getSide().equals("conflict")).
                forEach(card -> conflictCardList.add(jsonCardToConflictCard.apply(card)));
        System.out.println("Conflict Card List: OK (" + this.conflictCardList.size() + " cards)");
    }

    public void initializeDynastyCardList() {
        allCards.stream().filter(card -> card.getSide().equals("dynasty")).
                forEach(card -> dynastyCardList.add(jsonCardToDynastyCard.apply(card)));
        System.out.println("Dynasty Card List: OK (" + this.dynastyCardList.size() + " cards)");

    }

    public void initializeProvinceCardList() {
        allCards.stream().filter(card -> card.getType().equals("province")).
                forEach(card -> provinceCardList.add(jsonCardToProvinceCard.apply(card)));
        System.out.println("Province Card List: OK (" + this.provinceCardList.size() + " cards)");
    }

    public void initializeRoleCardList() {
        allCards.stream().filter(card -> card.getSide().equals("role")).
                forEach(card -> roleCardList.add(jsonCardToRoleCard.apply(card)));
        System.out.println("Role Card List: OK (" + this.roleCardList.size() + " cards)");
    }

    public void initializeStrongholdCardList() {
        allCards.stream().filter(card -> card.getType().equals("stronghold")).
                forEach(card -> strongholdCardList.add(jsonCardToStrongholdCard.apply(card)));
        System.out.println("Stronghold Card List: OK (" + this.strongholdCardList.size() + " cards)");
    }

    private Function<String, String> getPackName = str -> {
        switch (str) {
            case "core":
                return "CORE";
            case "tears-of-amaterasu":
            case "for-honor-and-glory":
            case "into-the-forbidden-city":
            case "the-chrysanthemum-throne":
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

    private void setNameCard(List<JsonPackCards> packCards, Card newCard) {
        String nameCard = "";
        int pos = -1;
        for (JsonPackCards jsonPackCards : packCards) {
            pos++;
            String namePack = jsonPackCards.getPack().get("id").toString();
            if ("\"gen-con-2017\"".equals(namePack)
                    || "\"2018-season-one-stronghold-kit\"".equals(namePack)
                    || "\"battle-for-the-stronghold-kit\"".equals(namePack)) {
                continue;
            } else {
                nameCard = namePack;
                break;
            }
        }

        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(pos).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(pos).getQuantity());

        newCard.setId(getCardName.apply(nameCard,
                packCards.get(pos).getPosition()));
    }

    private Function<JsonCard, DynastyCard> jsonCardToDynastyCard = jsonCard -> {
        DynastyCard newCard = new DynastyCard();
        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        setNameCard(packCards, newCard);

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

        setNameCard(packCards, newCard);

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

        setNameCard(packCards, newCard);


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

        setNameCard(packCards, newCard);


        String name = jsonCard.getName();
        newCard.setName(name);

        JsonArray traits = jsonCard.getTraits();

        if (name.contains("Support")) {
            newCard.setClan(traits.get(0).toString().substring(1, traits.get(0).toString().length() - 1));
            newCard.setRole("support");
        } else {
            newCard.setRole(traits.get(0).toString().substring(1, traits.get(0).toString().length() - 1));
            newCard.setElement(traits.get(1).toString().substring(1,
                    traits.get(1).toString().length() - 1));
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

        setNameCard(packCards, newCard);

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
