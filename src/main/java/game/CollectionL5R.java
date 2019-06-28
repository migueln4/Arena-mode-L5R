package game;

import cards.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import constants.Constants;
import interfaces.ThreeParametersLambda;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Data
class CollectionL5R {

    private final String FIVERINGSDB = "https://api.fiveringsdb.com/cards";
    private final Integer TIMEOUT = 3000;

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
        StringBuilder strb = new StringBuilder();
        strb.append(getPackName.apply(name));
        strb.append(" - #");
        strb.append(number);
        return strb.toString();
    };
    private Function<JsonCard, DynastyCard> jsonCardToDynastyCard = jsonCard -> {
        DynastyCard newCard = new DynastyCard();
        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        setNameCard(packCards, newCard);

        newCard.setIdFiveRingsDB(jsonCard.getId());
        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());
        newCard.setIsRestricted(jsonCard.getIs_restricted());
        newCard.setUnicity(jsonCard.getUnicity());
        newCard.setName_extra(jsonCard.getName_extra());


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

        newCard.setIdFiveRingsDB(jsonCard.getId());
        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());
        newCard.setIsRestricted(jsonCard.getIs_restricted());
        newCard.setUnicity(jsonCard.getUnicity());
        newCard.setName_extra(jsonCard.getName_extra());


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

        newCard.setIdFiveRingsDB(jsonCard.getId());
        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());
        newCard.setIsRestricted(jsonCard.getIs_restricted());
        newCard.setUnicity(jsonCard.getUnicity());
        newCard.setName_extra(jsonCard.getName_extra());


        newCard.setInfluence(jsonCard.getInfluence_pool());

        return newCard;
    };
    private Function<JsonCard, RoleCard> jsonCardToRoleCard = jsonCard -> {
        RoleCard newCard = new RoleCard();

        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        setNameCard(packCards, newCard);

        newCard.setIdFiveRingsDB(jsonCard.getId());

        String name = jsonCard.getName();
        newCard.setName(name);
        newCard.setIsRestricted(jsonCard.getIs_restricted());
        newCard.setUnicity(jsonCard.getUnicity());
        newCard.setName_extra(jsonCard.getName_extra());


        JsonArray traits = jsonCard.getTraits();

        if (name.contains("Support")) {
            newCard.setClan(traits.get(0).toString().substring(1, traits.get(0).toString().length() - 1));
            newCard.setRole(Constants.SUPPORT);
        } else {
            newCard.setRole(traits.get(0).toString().substring(1, traits.get(0).toString().length() - 1));
            newCard.setElement(traits.get(1).toString().substring(1, traits.get(1).toString().length() - 1));
            newCard.setClan(Constants.NEUTRAL);
        }

        return newCard;
    };
    private Function<JsonCard, ConflictCard> jsonCardToConflictCard = jsonCard -> {
        ConflictCard newCard = new ConflictCard();
        Type type = new TypeToken<List<JsonPackCards>>() {
        }.getType();
        List<JsonPackCards> packCards = gson.fromJson(jsonCard.getPack_cards(), type);

        setNameCard(packCards, newCard);

        newCard.setIdFiveRingsDB(jsonCard.getId());
        newCard.setDeckLimit(jsonCard.getDeck_limit());
        newCard.setName(jsonCard.getName());
        newCard.setClan(jsonCard.getClan());
        newCard.setIsRestricted(jsonCard.getIs_restricted());
        newCard.setUnicity(jsonCard.getUnicity());
        newCard.setName_extra(jsonCard.getName_extra());

        JsonArray allowedClans = jsonCard.getAllowed_clans();
        List<String> allowedClansList = new ArrayList<>();
        if (allowedClans != null)
            allowedClans.forEach(clan -> allowedClansList.add(clan.getAsString()));

        newCard.setAllowed_clans(allowedClansList);

        if (jsonCard.getRole_restriction() != null)
            elementAndRoleRestrictions(newCard, jsonCard.getRole_restriction());

        if (Constants.CHARACTER.equalsIgnoreCase(jsonCard.getType()))
            newCard.setCharacter(Boolean.TRUE);
        else
            newCard.setCharacter(Boolean.FALSE);


        if (jsonCard.getInfluence_cost() != null)
            newCard.setInfluence(jsonCard.getInfluence_cost());


        return newCard;
    };
    private Function<String, File> getCardFileReader = filename -> {
        ClassLoader cl = getClass().getClassLoader();
        return new File(Objects.requireNonNull(cl.getResource(filename)).getFile());
    };

    CollectionL5R() {
        this.conflictCardList = new ArrayList<>();
        this.dynastyCardList = new ArrayList<>();
        this.provinceCardList = new ArrayList<>();
        this.roleCardList = new ArrayList<>();
        this.strongholdCardList = new ArrayList<>();
        this.parser = new JsonParser();
        this.gson = new Gson();
        readURL();
        deleteExceptions();
    }

    private void readURL() {
        try {
            URL url = new URL(FIVERINGSDB);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            try {
                if (connection.getResponseCode() != 200)
                    readFile();
            } catch (SocketTimeoutException e) {
                readFile();
            }
            System.out.println("Connection: OK");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            readFile(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile(StringBuilder content) {
        this.obj = parser.parse(content.toString());
        this.jsonObject = (JsonObject) obj;
        if (!this.jsonObject.getAsJsonPrimitive("success").getAsBoolean())
            readFile();
        this.records = jsonObject.getAsJsonArray("records");
        Type type = new TypeToken<List<JsonCard>>() {
        }.getType();
        this.allCards = gson.fromJson(records, type);
    }

    private void readFile() {
        System.out.println("FAIL Connection --> reading file...");
        this.file = getCardFileReader.apply("allcards.json");
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

    private void deleteExceptions() {
        List<JsonCard> newList = new ArrayList<>(this.allCards);
        newList.stream().
                filter(Objects::nonNull)
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
                                    || "\"the-children-of-heaven\"".equals(packCards.get(0).getPack().get("id").toString())
                                    || "\"a-champion-s-foresight\"".equals(packCards.get(0).getPack().get("id").toString());
                        }
                ).forEach(card -> this.allCards.remove(card));
        System.out.println("Total number of cards " + this.allCards.size());

    }

    void initializeConflictCardList() {
        allCards.stream().filter(card -> card.getSide().equalsIgnoreCase(Constants.CONFLICT)).
                forEach(card -> conflictCardList.add(jsonCardToConflictCard.apply(card)));
        System.out.println("Conflict Card List: OK (" + this.conflictCardList.size() + " cards)");
    }

    void initializeDynastyCardList() {
        allCards.stream().filter(card -> card.getSide().equalsIgnoreCase(Constants.DYNASTY)).
                forEach(card -> dynastyCardList.add(jsonCardToDynastyCard.apply(card)));
        System.out.println("Dynasty Card List: OK (" + this.dynastyCardList.size() + " cards)");
    }

    void initializeProvinceCardList() {
        allCards.stream().filter(card -> card.getType().equalsIgnoreCase(Constants.PROVINCE)).
                forEach(card -> provinceCardList.add(jsonCardToProvinceCard.apply(card)));
        System.out.println("Province Card List: OK (" + this.provinceCardList.size() + " cards)");
    }

    void initializeRoleCardList() {
        allCards.stream().filter(card -> card.getSide().equalsIgnoreCase(Constants.ROLE)).
                forEach(card -> roleCardList.add(jsonCardToRoleCard.apply(card)));
        System.out.println("Role Card List: OK (" + this.roleCardList.size() + " cards)");
    }

    void initializeStrongholdCardList() {
        allCards.stream().filter(card -> card.getType().equalsIgnoreCase(Constants.STRONGHOLD)).
                forEach(card -> strongholdCardList.add(jsonCardToStrongholdCard.apply(card)));
        System.out.println("Stronghold Card List: OK (" + this.strongholdCardList.size() + " cards)");
    }

    private void setNameCard(List<JsonPackCards> packCards, Card newCard) {
        String nameCard = "";
        int pos = -1;
        for (JsonPackCards jsonPackCards : packCards) {
            pos++;
            String namePack = jsonPackCards.getPack().get("id").toString();
            if (!"\"gen-con-2017\"".equals(namePack)
                    && !"\"2018-season-one-stronghold-kit\"".equals(namePack)
                    && !"\"battle-for-the-stronghold-kit\"".equals(namePack)) {
                nameCard = namePack;
                break;
            }
        }

        nameCard = nameCard.substring(1, nameCard.length() - 1);

        if ("core".equals(nameCard))
            newCard.setQuantity((packCards.get(pos).getQuantity()) * 3);
        else
            newCard.setQuantity(packCards.get(pos).getQuantity());

        newCard.setId(getCardName.apply(nameCard, packCards.get(pos).getPosition()));
    }

    private void elementAndRoleRestrictions(Card newCard, String role) {
        switch (role) {
            case Constants.KEEPER:
                newCard.setRoleLimit(Constants.KEEPER);
                break;
            case Constants.SEEKER:
                newCard.setRoleLimit(Constants.SEEKER);
                break;
            case Constants.AIR:
                newCard.setElementLimit(Constants.AIR);
                break;
            case Constants.WATER:
                newCard.setElementLimit(Constants.WATER);
                break;
            case Constants.FIRE:
                newCard.setElementLimit(Constants.FIRE);
                break;
            case Constants.VOID:
                newCard.setElementLimit(Constants.VOID);
                break;
            case Constants.EARTH:
                newCard.setElementLimit(Constants.EARTH);
                break;
            default:
                break;
        }
    }

}
