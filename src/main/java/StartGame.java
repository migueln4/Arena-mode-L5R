import cards.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.NoArgsConstructor;
import restrictions.RestrictedCards;
import restrictions.RestrictedRoles;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@NoArgsConstructor
public class StartGame {

    private static final Scanner READ_CONSOLE = new Scanner(System.in);
    private final String VALIDATE_URL = "https://api.fiveringsdb.com/deck-validation/standard";

    final String[] CLANS = {"lion", "scorpion", "phoenix", "crane", "crab", "unicorn", "dragon"};
    final String[] ELEMENTS = {"void", "air", "water", "fire", "earth"};
    final int NUMBER_OPTIONS = 3;

    final Integer MAX_CONFLICT_CARDS = 45;
    final Integer MIN_CONFLICT_CARDS = 40;
    final Integer MAX_CONFLICT_CHARACTERS = 10;
    final Integer MAX_PROVINCE_CARDS = 5;
    final Integer MAX_CARD_COPIES = 3;

    private Deck player1;
    private Deck player2;

    private CollectionL5R collectionL5R;
    private RestrictedCards restrictedCards;
    private RestrictedRoles restrictedRoles;

    private Boolean allowRestrictedCards;
    private Boolean allowRestrictedRoles;

    private Gson gson;

    public void start() throws CloneNotSupportedException {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.player1 = new Deck("UNO");
        this.player2 = new Deck("DOS");
        this.collectionL5R = new CollectionL5R();
        this.restrictedCards = new RestrictedCards();
        this.restrictedRoles = new RestrictedRoles();
        this.collectionL5R.initializeConflictCardList();
        this.collectionL5R.initializeDynastyCardList();
        this.collectionL5R.initializeProvinceCardList();
        this.collectionL5R.initializeRoleCardList();
        this.collectionL5R.initializeStrongholdCardList();
        playerTurn();
        validateDecks();
        exportPlayers();
    }

    private void validateDecks() {
        Deck[] decks = new Deck[]{this.player1, this.player2};
        for (Deck player : decks) {
            String bodyplayer = createJsonToValidate(player);
            System.out.println("El jugador " + player.getNamePlayer() + " tiene este mazo");
            System.out.println(bodyplayer);
            try {
                URL url = new URL(VALIDATE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");

                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                os.write(bodyplayer.getBytes());
                os.flush();
                os.close();

                System.out.println("Código de respuesta para el jugador " + player.getNamePlayer() + ": " + connection.getResponseCode());
                System.out.println("Mensaje de respuesta para el jugador " + player.getNamePlayer() + ": " + connection.getResponseMessage());

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                JsonParser parser = new JsonParser();
                Object obj = parser.parse(content.toString());
                JsonObject jsonObject = (JsonObject) obj;
                System.out.println("LOG ---- Esto es lo que me llega --> " + jsonObject.get("status"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String createJsonToValidate(Deck player) {
        StringBuilder data = new StringBuilder("{\n");
        data.append("\"").append(player.getStronghold().getIdFiveRingsDB()).append("\": ").append(1).append(",\n");
        data.append("\"").append(player.getRoleCard().getIdFiveRingsDB()).append("\": ").append(1).append(",\n");
        player.getProvinces().forEach(card -> data.append("\"").append(card.getIdFiveRingsDB()).append("\": ").append(1).append(",\n"));
        player.getConflictCardDeck().forEach(card -> data.append("\"").append(card.getIdFiveRingsDB()).append("\": ").append(card.getQuantity()).append(",\n"));
        player.getDynastyCardDeck().forEach(card -> data.append("\"").append(card.getIdFiveRingsDB()).append("\": ").append(card.getQuantity()).append(",\n"));
        data.setLength(data.length() - 2);
        data.append("\n}");
        return data.toString();
    }

    private void exportPlayers() {
        StringBuilder srcPlayer1 = new StringBuilder(this.player1.getNamePlayer());
        StringBuilder srcPlayer2 = new StringBuilder(this.player2.getNamePlayer());
        srcPlayer1.append("_player1.json");
        srcPlayer2.append("_player2.json");
        try {
            FileWriter fileWriter = new FileWriter(srcPlayer1.toString());
            String player1Json = this.gson.toJson(this.player1);
            fileWriter.write(player1Json);
            fileWriter.flush();
            fileWriter = new FileWriter(srcPlayer2.toString());
            String player2Json = this.gson.toJson(this.player2);
            fileWriter.write(player2Json);
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playerTurn() throws CloneNotSupportedException {
        allowRestrictedRules();
        selectingClan();
        selectingRole();
        selectSplash();
        selectStronghold();
        selectProvinces(player1, player2);
        selectDynastyDeck(player1, player2);
        selectConflictDeck(player1, player2);
    }

    private void allowRestrictedRules() {
        System.out.println("¿Queréis jugar con los roles oficiales para cada clan?\n(1 = SÍ / 2 = " +
                "NO)");
        if (readInteger() == 1) {
            this.allowRestrictedRoles = Boolean.TRUE;
        } else {
            this.allowRestrictedRoles = Boolean.FALSE;
        }

        System.out.println("¿Queréis jugar con las reglas de cartas restringidas?\n(1 = SÍ / 2 = " +
                "NO)");
        if (readInteger() == 1) {
            this.allowRestrictedCards = Boolean.TRUE;
        } else {
            this.allowRestrictedCards = Boolean.FALSE;
        }
    }

    private void selectConflictDeck(Deck player1, Deck player2) throws CloneNotSupportedException {
        boolean flagPlayer1 = player1.getNumberConflictCards() < MIN_CONFLICT_CARDS;
        boolean flagPlayer2 = player2.getNumberConflictCards() < MIN_CONFLICT_CARDS;
        while (flagPlayer1 || flagPlayer2) {
            if (flagPlayer1) {
                selectConflictCard(player1);
                if (player1.getNumberConflictCards() >= MIN_CONFLICT_CARDS
                        && player1.getNumberConflictCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Jugador " + player1.getNamePlayer() + " " +
                            "has llegado al mínimo del mazo de Conflicto. Tienes " + player1.getNumberConflictCards() +
                            " cartas. \n\t¿Quieres dejar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if (readInteger() == 1) {
                        flagPlayer1 = false;
                    }
                } else {
                    flagPlayer1 = player1.getNumberConflictCards() < MAX_CONFLICT_CARDS;
                }
            }
            if (flagPlayer2) {
                selectConflictCard(player2);
                if (player2.getNumberConflictCards() >= MIN_CONFLICT_CARDS
                        && player2.getNumberConflictCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Jugador " + player2.getNamePlayer() + " " +
                            "has llegado al mínimo del mazo de Conflicto. Tienes " + player2.getNumberConflictCards() +
                            " cartas. \n\t¿Quieres dejar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if (readInteger() == 1) {
                        flagPlayer2 = false;
                    }
                } else {
                    flagPlayer2 = player2.getNumberConflictCards() < MAX_CONFLICT_CARDS;
                }
            }
        }
    }

    private void selectConflictCard(Deck player) throws CloneNotSupportedException {
        System.out.println("Jugador " + player.getNamePlayer() + " elige tu carta" +
                " número " + (player.getNumberConflictCards() + 1) + "\n (tienes " +
                player.getNumberConflictCards() + " cartas en tu mazo de Conflicto, " +
                player.getNumberCharacters() + " de ellas, personajes.\n(influencia restante: " +
                player.getInfluence() + " puntos)");
        List<ConflictCard> cardsAvailables = new ArrayList<>();
        makeConflictOptions(cardsAvailables, player);
        for (int i = 0; i < cardsAvailables.size(); i++) {
            System.out.println((i + 1) + ")" + cardsAvailables.get(i));
        }
        int option = readInteger();
        ConflictCard cardSelected = (ConflictCard) cardsAvailables.get(option - 1).clone();
        int addQuantity = 1;
        if (cardSelected.getDeckLimit() != 1
                && cardSelected.getQuantity() > 1
                && player.getNumberConflictCards() + 1 < MAX_CONFLICT_CARDS
                && oneMoreCharacter(player, cardSelected, 1)
                && oneMoreSplashCard(player, cardSelected, 1)) {
            System.out.println("Tienes " + player.getNumberConflictCards() + " cartas en tu mazo " +
                    "de Conflicto. ¿Cuántas copias quieres de esta carta?");
            for (int i = 1; i <= cardSelected.getQuantity()
                    && MAX_CONFLICT_CARDS >= player.getNumberConflictCards() + i
                    && i <= MAX_CARD_COPIES
                    && i <= cardSelected.getDeckLimit()
                    && oneMoreCharacter(player, cardSelected, i)
                    && oneMoreSplashCard(player, cardSelected, i); i++) {
                System.out.print(i + ", ");
            }
            addQuantity = readInteger();
        }
        ConflictCard cardToAdd = (ConflictCard) cardSelected.clone();
        player.getConflictCardDeck().add(cardToAdd);
        player.setNumberConflictCards(player.getNumberConflictCards() + addQuantity);
        if (isRestrictedCard(cardSelected, "Conflict"))
            player.setContainsRestrictedCards(Boolean.TRUE);
        if (cardToAdd.getCharacter())
            player.setNumberCharacters(player.getNumberCharacters() + addQuantity);
        if (cardToAdd.getClan().equals(player.getSplash()))
            player.setInfluence(player.getInfluence() - (cardToAdd.getInfluence() * addQuantity));
        int quantity = cardSelected.getQuantity() - addQuantity;
        int index = this.collectionL5R.getConflictCardList().indexOf(cardSelected);
        if (quantity < 1) {
            this.collectionL5R.getConflictCardList().remove(cardSelected);
        } else {
            this.collectionL5R.getConflictCardList().get(index).setQuantity(quantity);
        }
    }

    private boolean oneMoreSplashCard(Deck player, ConflictCard card, int i) {
        if (card.getClan() == null
                || card.getClan().equals("neutral")
                || card.getClan().equals(player.getClan())) {
            return true;
        } else {
            if (card.getInfluence() * i <= player.getInfluence()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean oneMoreCharacter(Deck player, ConflictCard card, int i) {
        if (!card.getCharacter()) {
            return true;
        } else {
            if (player.getNumberCharacters() + i < MAX_CONFLICT_CHARACTERS) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void makeConflictOptions(List<ConflictCard> cardsAvailables, Deck player) {
        List<ConflictCard> selection = this.collectionL5R.getConflictCardList().stream()
                .filter(card ->
                        !conflictCardIsPresent(player, card)
                                && characterConditions(player, card)
                                && isAllowedCard(player, card, "Conflict")
                                && (card.getClan().equals("neutral")
                                || card.getClan().equals(player.getClan())
                                || splashConditions(player, card))
                                && (card.getRoleLimit() == null
                                || card.getRoleLimit().equals("null")
                                || card.getRoleLimit().equals(player.getRoleCard().getRole()))
                                && (card.getElementLimit() == null
                                || card.getElementLimit().equals("null")
                                || card.getElementLimit().equals(player.getElement()))
                )
                .collect(Collectors.toList());
        int rndArray[] = createArrayNumbers(selection.size());
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (rndArray.length - 1 - i));
            cardsAvailables.add(selection.get(rndArray[rnd]));
            rndArray[rnd] = rndArray[rndArray.length - 1 - i];
        }
    }

    private boolean conflictCardIsPresent(Deck player, ConflictCard card) {
        for (ConflictCard conflictCard : player.getConflictCardDeck()) {
            if (card.getIdFiveRingsDB().equals(conflictCard.getIdFiveRingsDB())) {
                return true;
            }
        }
        return false;
    }

    private boolean splashConditions(Deck player, ConflictCard card) {
        if (player.getSplash() == null
                || player.getSplash().equals("null")
                || !card.getClan().equals(player.getSplash())
                || card.getInfluence() == null
                || card.getInfluence().equals("null")
                || card.getInfluence() < 1
                || player.getInfluence() < 1
                || card.getInfluence() > player.getInfluence()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean characterConditions(Deck player, ConflictCard card) {
        if (!card.getCharacter()) {
            return true;
        } else {
            if (player.getNumberCharacters() == MAX_CONFLICT_CHARACTERS) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void selectDynastyDeck(Deck player1, Deck player2) throws CloneNotSupportedException {
        boolean flagPlayer1 = player1.getNumberDynastyCards() < MIN_CONFLICT_CARDS;
        boolean flagPlayer2 =
                player2.getNumberDynastyCards() < MIN_CONFLICT_CARDS;
        while (flagPlayer1 || flagPlayer2) {
            if (flagPlayer1) {
                selectDynastyCard(player1);
                if (player1.getNumberDynastyCards() >= MIN_CONFLICT_CARDS
                        && player1.getNumberDynastyCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Jugador " + player1.getNamePlayer() + " " +
                            "has llegado al mínimo del mazo (tienes " + player1.getNumberDynastyCards() + "). " +
                            "¿Quieres terminar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if (readInteger() == 1) {
                        flagPlayer1 = false;
                    }
                } else {
                    flagPlayer1 =
                            player1.getNumberDynastyCards() < MAX_CONFLICT_CARDS;
                }
            }
            if (flagPlayer2) {
                selectDynastyCard(player2);
                if (player2.getNumberDynastyCards() >= MIN_CONFLICT_CARDS
                        && player2.getNumberDynastyCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Jugador " + player2.getNamePlayer() + " " +
                            "has llegado al mínimo del mazo (tienes " + player2.getNumberDynastyCards() + " ). " +
                            "¿Quieres " +
                            "terminar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if (readInteger() == 1) {
                        flagPlayer2 = false;
                    }
                } else {
                    flagPlayer2 =
                            player2.getNumberDynastyCards() < MAX_CONFLICT_CARDS;
                }
            }
        }
    }

    private void selectDynastyCard(Deck player) throws CloneNotSupportedException {
        System.out.println("Jugador " + player.getNamePlayer() + " elige tu carta número " + (player.getNumberDynastyCards() + 1) +
                "\n(tienes " + player.getNumberDynastyCards() + " cartas en tu mazo de Dinastía)");
        List<DynastyCard> cardsAvailables = new ArrayList<>();
        makeDynastyOptions(cardsAvailables, player);
        for (int i = 0; i < cardsAvailables.size(); i++) {
            System.out.println((i + 1) + ") " + cardsAvailables.get(i));
        }
        int option = readInteger();
        DynastyCard cardSelected = (DynastyCard) cardsAvailables.get(option - 1).clone();
        int addQuantity = 1;
        if (cardSelected.getDeckLimit() != 1
                && cardSelected.getQuantity() > 1
                && player.getNumberDynastyCards() + 1 < MAX_CONFLICT_CARDS) {
            System.out.println("Tienes " + player.getNumberDynastyCards() +
                    " cartas en tu mazo de Dinastía. ¿Cuántas copias quieres " +
                    "de esta carta?");
            for (int i = 1; i <= cardSelected.getQuantity()
                    && MAX_CONFLICT_CARDS >= player.getNumberDynastyCards() + i
                    && i <= cardSelected.getDeckLimit()
                    && i <= MAX_CARD_COPIES; i++)
                System.out.print(i + ", ");
            addQuantity = readInteger();
        }
        DynastyCard cardToAdd = (DynastyCard) cardSelected.clone();
        cardToAdd.setQuantity(addQuantity);
        player.getDynastyCardDeck().add(cardToAdd);
        player.setNumberDynastyCards(player.getNumberDynastyCards() + addQuantity);
        if (isRestrictedCard(cardSelected, "Dynasty"))
            player.setContainsRestrictedCards(Boolean.TRUE);
        int quantity = cardSelected.getQuantity() - addQuantity;
        int index = this.collectionL5R.getDynastyCardList().indexOf(cardSelected);
        if (quantity < 1) {
            this.collectionL5R.getDynastyCardList().remove(cardSelected);
        } else {
            this.collectionL5R.getDynastyCardList().get(index).setQuantity(Integer.valueOf(quantity));
        }
    }

    private void makeDynastyOptions(List<DynastyCard> cardsAvailables, Deck player) {
        List<DynastyCard> selection =
                this.collectionL5R.getDynastyCardList().stream()
                        .filter(card -> !dynastyCardIsPresent(player, card)
                                && isAllowedCard(player, card, "Dynasty")
                                && (card.getClan().equals("neutral")
                                || card.getClan().equals(player.getClan()))
                                && (card.getRoleLimit() == null
                                || card.getRoleLimit().equals("null")
                                || card.getRoleLimit().equals(player.getRoleCard().getRole()))
                                && (card.getElementLimit() == null
                                || card.getElementLimit().equals("null")
                                || card.getElementLimit().equals(player.getElement())))
                        .collect(Collectors.toList());
        int rndArray[] = createArrayNumbers(selection.size());
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (rndArray.length - 1 - i));
            cardsAvailables.add(selection.get(rndArray[rnd]));
            rndArray[rnd] = rndArray[rndArray.length - 1 - i];
        }
    }

    private boolean dynastyCardIsPresent(Deck player, DynastyCard dynastyCard) {
        for (DynastyCard card : player.getDynastyCardDeck()) {
            if (card.getIdFiveRingsDB().equals(dynastyCard.getIdFiveRingsDB())) {
                System.out.println("LOG --- Entra en verdadero comparando:");
                System.out.println(card.getIdFiveRingsDB() + " vs. " + dynastyCard.getIdFiveRingsDB());
                return true;
            }
        }
        return false;
    }

    private void selectProvinces(Deck player1, Deck player2) throws CloneNotSupportedException {
        while (player1.getProvinces().size() < MAX_PROVINCE_CARDS && player2.getProvinces().size() < MAX_PROVINCE_CARDS) {
            if (player1.getProvinces().size() < MAX_PROVINCE_CARDS) {
                playerSelectingProvince(player1);
            }
            if (player2.getProvinces().size() < MAX_PROVINCE_CARDS) {
                playerSelectingProvince(player2);
            }
        }
    }

    private void playerSelectingProvince(Deck player) throws CloneNotSupportedException {
        System.out.println("Jugador " + player.getNamePlayer() + " elige tu provincia número " + (player.getProvinces().size() + 1));
        List<String> elementsAvailables = new ArrayList<>();
        List<ProvinceCard> provincesAvailables = new ArrayList<>();
        for (int i = 0; i < ELEMENTS.length; i++) {
            if (player.getLimitProvince()[i] > 0) {
                elementsAvailables.add(ELEMENTS[i]);
            }
        }
        makeProvinceOptions(elementsAvailables, provincesAvailables, player);
        for (int i = 0; i < provincesAvailables.size(); i++) {
            System.out.println((i + 1) + ") " + provincesAvailables.get(i));
        }
        int option = readInteger();
        ProvinceCard selectedCard = (ProvinceCard) provincesAvailables.get(option - 1).clone();
        player.getProvinces().add(selectedCard);
        if (isRestrictedCard(selectedCard, "Province"))
            player.setContainsRestrictedCards(Boolean.TRUE);
        for (int i = 0; i < player.getLimitProvince().length; i++) {
            if (ELEMENTS[i].equals(selectedCard.getElement())) {
                player.getLimitProvince()[i] = player.getLimitProvince()[i] - 1;
            }
        }
        int quantity = selectedCard.getQuantity() - 1;
        int index = this.collectionL5R.getProvinceCardList().indexOf(selectedCard);
        if (quantity < 1) {
            this.collectionL5R.getProvinceCardList().remove(selectedCard);
        } else {
            this.collectionL5R.getProvinceCardList().get(index).setQuantity(Integer.valueOf(quantity));
        }
    }

    private boolean isRestrictedCard(Card card, String type) {
        if (this.allowRestrictedCards) {
            return false;
        } else {
            for (String restrictedNameCard : this.restrictedCards.getRestrictedLists().get(type)) {
                if (restrictedNameCard.equals(card.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    private void makeProvinceOptions(List<String> elementsAvailables, List<ProvinceCard> provincesAvailables, Deck player) {
        List<ProvinceCard> selection =
                this.collectionL5R.getProvinceCardList().stream()
                        .filter(card -> elementsAvailables.contains(card.getElement())
                                && !pronvinceIsPresent(player, card)
                                && isAllowedCard(player, card, "Province")
                                && ((card.getClan() == null)
                                || card.getClan().equals(player.getClan())
                                || card.getClan().equals("neutral"))
                                && ((card.getRoleLimit() == null)
                                || card.getRoleLimit().equals(player.getRoleCard().getRole())
                                || card.getRoleLimit().equals("null"))
                                && ((card.getElementLimit() == null)
                                || card.getElementLimit().equals(player.getRoleCard().getElement())
                                || card.getElementLimit().equals("null")))
                        .collect(Collectors.toList());
        int rndArray[] = createArrayNumbers(selection.size());
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (rndArray.length - 1 - i));
            provincesAvailables.add(selection.get(rndArray[rnd]));
            rndArray[rnd] = rndArray[rndArray.length - 1 - i];
        }
    }

    private boolean isAllowedCard(Deck player, Card card, String type) {
        if (this.allowRestrictedCards) {
            for (String restrictedCardName : this.restrictedCards.getRestrictedLists().get(type)) {
                if (player.getContainsRestrictedCards() && restrictedCardName.equals(card.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean pronvinceIsPresent(Deck player, ProvinceCard province) {
        for (ProvinceCard card : player.getProvinces()) {
            if (card.getIdFiveRingsDB().equals(province.getIdFiveRingsDB())) {
                return true;
            }
        }
        return false;
    }

    private void selectStronghold() throws CloneNotSupportedException {
        List<Deck> players = new ArrayList<>();
        players.add(this.player1);
        players.add(this.player2);
        for(Deck player : players) {
            List<StrongholdCard> strongholdValidList = new ArrayList<>();
            this.collectionL5R.getStrongholdCardList().stream()
                    .filter(card -> player.getClan().equals(card.getClan()))
                    .forEach(card -> strongholdValidList.add(card));
            System.out.println("Jugador " + player.getNamePlayer() + " elige un Stronghold para tu clan " + player.getClan());
            for (int i = 0; i < strongholdValidList.size(); i++)
                System.out.println((i + 1) + ") " + strongholdValidList.get(i));
            int option = readInteger();
            StrongholdCard selectedCard = (StrongholdCard) strongholdValidList.get(option - 1).clone();
            player.setStronghold(selectedCard);
            player.setInfluence(player.getInfluence() + selectedCard.getInfluence());
            int indexCard = this.collectionL5R.getStrongholdCardList().indexOf(selectedCard);
            int quantity = this.collectionL5R.getStrongholdCardList().get(indexCard).getQuantity();
            this.collectionL5R.getStrongholdCardList().get(indexCard).setQuantity(--quantity);
            if (quantity == 0)
                this.collectionL5R.getStrongholdCardList().remove(indexCard);
        }
    }

    private void selectSplash() {
        List<Deck> players = new ArrayList<>();
        players.add(this.player1);
        players.add(this.player2);
        for(Deck player : players) {
            if(player.getSplash() == null) {
                String mainClan = player.getClan();
                int mainClanIndex = -1;
                for (int i = 0; i < CLANS.length; i++) {
                    if (CLANS[i].equals(mainClan))
                        mainClanIndex = i;
                }
                int[] arrayClans = createArrayNumbers(CLANS.length, mainClanIndex);
                int[] arrayOptions = new int[NUMBER_OPTIONS];
                for (int i = 0; i < NUMBER_OPTIONS; i++) {
                    int rnd = (int) (Math.random() * (arrayClans.length - 1 - i));
                    arrayOptions[i] = arrayClans[rnd];
                    arrayClans[rnd] = arrayClans.length - 1 - i;
                }
                System.out.println("Jugador " + player.getNamePlayer() + " elige cuál es tu clan secundario");
                for (int i = 0; i < NUMBER_OPTIONS; i++)
                    System.out.println((i + 1) + ") " + CLANS[arrayOptions[i]]);
                int option = readInteger();
                player.setSplash(CLANS[arrayOptions[option - 1]]);
            }
        }
    }

    private int[] createArrayNumbers(int size, int exclude) {
        int[] result = new int[size - 1];
        int aux = 0;
        for (int i = 0; aux < result.length; i++) {
            if (i == exclude)
                continue;
            else
                result[aux] = i;
            aux++;
        }
        return result;
    }

    private void selectingClan() {
        player1.setClan(selectClan(1));
        player2.setClan(selectClan(2));
    }

    private String selectClan(int n) {
        System.out.println("Jugador " + n + ", selecciona el clan que quieras");
        ArrayList<String> selectingClan = randomClan();
        for (int i = 0; i < selectingClan.size(); i++)
            System.out.println("\t" + (i + 1) + ". " + selectingClan.get(i));
        int clanNumber = readInteger();
        return selectingClan.get(clanNumber - 1);
    }

    private ArrayList<String> randomClan() {
        ArrayList<String> selectingClan = new ArrayList<>();
        String[] clansRemaining = copyArray(CLANS);
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (clansRemaining.length - 1 - i));
            selectingClan.add(clansRemaining[rnd]);
            String aux = clansRemaining[rnd];
            clansRemaining[rnd] = clansRemaining[clansRemaining.length - 1 - i];
            clansRemaining[clansRemaining.length - 1 - i] = aux;
        }
        return selectingClan;
    }

    private void selectRestrictedRole(Deck player) {
        List<RoleCard> options = new ArrayList<>(this.restrictedRoles.getRolesPerClan());
        this.collectionL5R.getRoleCardList().stream()
                .filter(card -> this.restrictedRoles.getRestrictedRolesLists().get(player.getClan()).contains(card.getName()))
                .forEach(card -> options.add(card));
        System.out.println("Jugador " + player.getNamePlayer() + " selecciona una carta: ");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ") " + options.get(i).toString());
        }
        RoleCard cardSelected = options.get(readInteger() - 1);
        player.setRoleCard(cardSelected);
        player.setRole(cardSelected.getRole());
        player.setElement(cardSelected.getElement());
    }

    private void selectingRole() throws CloneNotSupportedException {
        List<Deck> players = new ArrayList<>();
        players.add(this.player1);
        players.add(this.player2);
        for (Deck player : players) {
            if (allowRestrictedRoles) {
                selectRestrictedRole(player);
            } else {
                List<RoleCard> cardsAvailables = new ArrayList<>();
                makeRoleCardOptions(cardsAvailables, player);
                for (int i = 0; i < cardsAvailables.size(); i++) {
                    System.out.println((i + 1) + ") " + cardsAvailables.get(i));
                }
                int option = readInteger();
                RoleCard roleCardSelected = cardsAvailables.get(option-1);
                player.setRoleCard(roleCardSelected);
                player.setRole(roleCardSelected.getRole());
                if (roleCardSelected.getName().toLowerCase().contains("support")) {
                    player.setSplash(roleCardSelected.getClan());
                    player.setInfluence(player.getInfluence() + 8);
                } else {
                    player.setElement(roleCardSelected.getElement());
                    if (roleCardSelected.getRole().toLowerCase().equals("keeper"))
                        player.setInfluence(player.getInfluence() + 3);
                    else if (roleCardSelected.getRole().toLowerCase().equals("seeker")) {
                        for (int i = 0; i < ELEMENTS.length; i++) {
                            if (roleCardSelected.getElement().equals(ELEMENTS[i])) {
                                player.getLimitProvince()[i] = player.getLimitProvince()[i] + 1;
                            }
                        }
                    }
                }
                int cardSelected = this.collectionL5R.getRoleCardList().indexOf(roleCardSelected);
                int quantityCard = this.collectionL5R.getRoleCardList().get(cardSelected).getQuantity();
                this.collectionL5R.getRoleCardList().get(cardSelected).setQuantity(--quantityCard);
                if (quantityCard == 0)
                    this.collectionL5R.getRoleCardList().remove(cardSelected);
            }
        }
    }

    private void makeRoleCardOptions(List<RoleCard> rolesAvailables, Deck player) {
        List<RoleCard> selection =
                this.collectionL5R.getRoleCardList().stream()
                        .filter(card -> !card.getClan().equals(player.getClan()))
                        .collect(Collectors.toList());
        int rndArray[] = createArrayNumbers(selection.size());
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (rndArray.length - 1 - i));
            rolesAvailables.add(selection.get(rndArray[rnd]));
            rndArray[rnd] = rndArray[rndArray.length - 1 - i];
        }
    }

    private int[] createArrayNumbers(int size) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++)
            result[i] = i;
        return result;
    }

    private String[] copyArray(String[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    private int readInteger() {
        return READ_CONSOLE.nextInt();
    }


}
