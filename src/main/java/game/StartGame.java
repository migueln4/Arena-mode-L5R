package game;

import cards.*;
import constants.Constants;
import lombok.NoArgsConstructor;
import restrictions.RestrictedCards;
import restrictions.RestrictedRoles;
import validation.Export;
import validation.Validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

@NoArgsConstructor
public class StartGame {

    private static final Scanner READ_CONSOLE = new Scanner(System.in);

    private final String[] CLANS = Constants.CLANS;
    private final String[] ELEMENTS = Constants.ELEMENTS;
    private final Integer NUMBER_OPTIONS = Constants.NUMBER_OPTIONS;

    private final Integer MAX_CONFLICT_CARDS = Constants.MAX_CONFLICT_CARDS;
    private final Integer MIN_CONFLICT_CARDS = Constants.MIN_CONFLICT_CARDS;
    private final Integer MAX_CONFLICT_CHARACTERS = Constants.MAX_CONFLICT_CHARACTERS;
    private final Integer MAX_PROVINCE_CARDS = Constants.MAX_PROVINCE_CARDS;
    private final Integer MAX_CARD_COPIES = Constants.MAX_CARD_COPIES;

    private Deck player1;
    private Deck player2;

    private CollectionL5R collectionL5R;
    private RestrictedCards restrictedCards;
    private RestrictedRoles restrictedRoles;

    private Boolean allowRestrictedCards;
    private Boolean allowRestrictedRoles;

    public void start() throws CloneNotSupportedException {
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
        Validation validate = new Validation();
        validate.validateDecks(this.player1, this.player2);
        Export export = new Export();
        export.exportPlayers(this.player1, this.player2);
    }

    private void playerTurn() throws CloneNotSupportedException {
        allowRestrictedRules();
        selectingClan();
        selectingRole();
        selectSplash();
        selectStronghold();
        selectProvinces();
        selectDynastyDeck();
        selectConflictDeck();
    }

    private void allowRestrictedRules() {
        System.out.println("Do you want to play with official Roles?\n(1 = YES / 2 = NO)");
        if (readInteger(1, 2) == 1) {
            this.allowRestrictedRoles = Boolean.TRUE;
        } else {
            this.allowRestrictedRoles = Boolean.FALSE;
        }

        System.out.println("Do you want to play with restricted card rules?\n(1 = YES / 2 = NO)");
        if (readInteger(1, 2) == 1) {
            this.allowRestrictedCards = Boolean.TRUE;
        } else {
            this.allowRestrictedCards = Boolean.FALSE;
        }
    }

    private void selectConflictDeck() throws CloneNotSupportedException {
        boolean flagPlayer1 = this.player1.getNumberConflictCards() < MIN_CONFLICT_CARDS;
        boolean flagPlayer2 = this.player2.getNumberConflictCards() < MIN_CONFLICT_CARDS;
        while (flagPlayer1 || flagPlayer2) {
            if (flagPlayer1) {
                selectConflictCard(this.player1);
                if (this.player1.getNumberConflictCards() >= MIN_CONFLICT_CARDS
                        && this.player1.getNumberConflictCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Player " + this.player1.getNamePlayer() + ", your Dynasty deck has enough cards. You have "
                            + player1.getNumberConflictCards() + " cards. \n\tDo you continue adding cards?\n\t(1 = YES / 2 = NO)");
                    if (readInteger(1, 2) == 1) {
                        flagPlayer1 = false;
                    }
                } else {
                    flagPlayer1 = this.player1.getNumberConflictCards() < MAX_CONFLICT_CARDS;
                }
            }
            if (flagPlayer2) {
                selectConflictCard(this.player2);
                if (this.player2.getNumberConflictCards() >= MIN_CONFLICT_CARDS
                        && this.player2.getNumberConflictCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Player " + this.player2.getNamePlayer() + ", your Conflict deck has enough cards. You have "
                            + this.player2.getNumberConflictCards() + " cards. \n\tDo you want to finish?\n\t(1 = YES / 2 = NO)");
                    if (readInteger(1, 2) == 1) {
                        flagPlayer2 = false;
                    }
                } else {
                    flagPlayer2 = this.player2.getNumberConflictCards() < MAX_CONFLICT_CARDS;
                }
            }
        }
    }

    private void selectConflictCard(Deck player) throws CloneNotSupportedException {
        System.out.println("Player " + player.getNamePlayer() + ", choose your card number " + (player.getNumberConflictCards() + 1) +
                "\n (You have " + player.getNumberConflictCards() + " cards in your Conflict deck, " + player.getNumberCharacters() +
                " of them are characters.\n(influence remaining: " + player.getInfluence() + " points)");
        List<ConflictCard> cardsAvailables = new ArrayList<>();
        makeConflictOptions(cardsAvailables, player);
        int option = 0;
        for (int i = 0; i < cardsAvailables.size(); i++) {
            System.out.println((i + 1) + ")" + cardsAvailables.get(i));
            option = i + 1;
        }
        option = readInteger(1, option);
        ConflictCard cardSelected = (ConflictCard) cardsAvailables.get(option - 1).clone();
        int addQuantity = 1;
        if (cardSelected.getDeckLimit() != 1
                && cardSelected.getQuantity() > 1
                && player.getNumberConflictCards() + 1 < MAX_CONFLICT_CARDS
                && oneMoreCharacter(player, cardSelected, 1)
                && oneMoreSplashCard(player, cardSelected, 1)) {
            System.out.println("You have " + player.getNumberConflictCards() + " cards in your Conflict deck. How many copies do you want of this card?");
            for (int i = 1; i <= cardSelected.getQuantity()
                    && MAX_CONFLICT_CARDS >= player.getNumberConflictCards() + i
                    && i <= MAX_CARD_COPIES
                    && i <= cardSelected.getDeckLimit()
                    && oneMoreCharacter(player, cardSelected, i)
                    && oneMoreSplashCard(player, cardSelected, i); i++) {
                System.out.print(i + ", ");
                addQuantity = i;
            }
            addQuantity = readInteger(1, addQuantity);
        }
        ConflictCard cardToAdd = (ConflictCard) cardSelected.clone();
        cardToAdd.setQuantity(addQuantity);
        player.getConflictCardDeck().add(cardToAdd);
        player.setNumberConflictCards(player.getNumberConflictCards() + addQuantity);
        if (isRestrictedCard(cardSelected, Constants.CONFLICT))
            player.setContainsRestrictedCards(Boolean.TRUE);
        if (cardToAdd.getCharacter())
            player.setNumberCharacters(player.getNumberCharacters() + addQuantity);
        if (cardToAdd.getClan().equals(player.getSplash()))
            player.setInfluence(player.getInfluence() - (cardToAdd.getInfluence() * addQuantity));
        int quantity = cardSelected.getQuantity() - addQuantity;
        int index = this.collectionL5R.getConflictCardList().indexOf(cardSelected);
        if (quantity < 1)
            this.collectionL5R.getConflictCardList().remove(cardSelected);
        else
            this.collectionL5R.getConflictCardList().get(index).setQuantity(quantity);
    }

    private boolean oneMoreSplashCard(Deck player, ConflictCard card, int i) {
        if (card.getClan() == null
                || card.getClan().equals(Constants.NEUTRAL)
                || card.getClan().equals(player.getClan())) {
            return true;
        } else {
            return card.getInfluence() * i <= player.getInfluence();
        }
    }

    private boolean oneMoreCharacter(Deck player, ConflictCard card, int i) {
        if (!card.getCharacter()) return true;
        else {
            return player.getNumberCharacters() + i < MAX_CONFLICT_CHARACTERS;
        }
    }

    private void makeConflictOptions(List<ConflictCard> cardsAvailables, Deck player) {
        List<ConflictCard> selection = this.collectionL5R.getConflictCardList().stream()
                .filter(card ->
                        !conflictCardIsPresent(player, card)
                                && characterConditions(player, card)
                                && isAllowedCard(player, card, Constants.CONFLICT)
                                && (card.getClan().equals(player.getClan())
                                    || isAllowedClan(player, card))
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

    private boolean isAllowedClan(Deck player, ConflictCard card) {
        if(card.getClan().equals(Constants.NEUTRAL)) {
            return isAllowedClan(player.getClan(),card.getAllowed_clans());
        } else {
            return splashConditions(player,card);
        }
    }

    private boolean isAllowedClan(String clanToEvaluate, List<String> allowedClans) {
        for(String clan : allowedClans)
            if(clan.equals(clanToEvaluate))
                return true;
        return false;
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
        return player.getSplash() != null
                && !player.getSplash().equals("null")
                && isAllowedClan(player.getClan(),card.getAllowed_clans())
                && card.getClan().equals(player.getSplash())
                && card.getInfluence() != null
                && !card.getInfluence().equals("null")
                && card.getInfluence() >= 1
                && player.getInfluence() >= 1
                && card.getInfluence() <= player.getInfluence();
    }

    private boolean characterConditions(Deck player, ConflictCard card) {
        if (!card.getCharacter()) {
            return true;
        } else return !Objects.equals(player.getNumberCharacters(), MAX_CONFLICT_CHARACTERS);
    }

    private void selectDynastyDeck() throws CloneNotSupportedException {
        boolean flagPlayer1 = this.player1.getNumberDynastyCards() < MIN_CONFLICT_CARDS;
        boolean flagPlayer2 = this.player2.getNumberDynastyCards() < MIN_CONFLICT_CARDS;
        while (flagPlayer1 || flagPlayer2) {
            if (flagPlayer1) {
                selectDynastyCard(this.player1);
                if (this.player1.getNumberDynastyCards() >= MIN_CONFLICT_CARDS
                        && this.player1.getNumberDynastyCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Player " + this.player1.getNamePlayer() + ", you have enough cards in your Dynasty deck (you have " +
                            this.player1.getNumberDynastyCards() + " cards). \n\tDo you want to finish?\n\t(1 = YES / 2 = NO)");
                    if (readInteger(1, 2) == 1) {
                        flagPlayer1 = false;
                    }
                } else {
                    flagPlayer1 =
                            this.player1.getNumberDynastyCards() < MAX_CONFLICT_CARDS;
                }
            }
            if (flagPlayer2) {
                selectDynastyCard(this.player2);
                if (this.player2.getNumberDynastyCards() >= MIN_CONFLICT_CARDS
                        && this.player2.getNumberDynastyCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Player " + this.player2.getNamePlayer() + ", you have enough cards in your Dynasty deck (you have " +
                            this.player2.getNumberDynastyCards() + " cards). \n\tDo you want to finish?\n\t(1 = YES / 2 = NO)");
                    if (readInteger(1, 2) == 1) {
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
        System.out.println("Player " + player.getNamePlayer() + ", choose your card number " + (player.getNumberDynastyCards() + 1) +
                "\n(you have " + player.getNumberDynastyCards() + " cards in your Dynasty deck)");
        List<DynastyCard> cardsAvailables = new ArrayList<>();
        makeDynastyOptions(cardsAvailables, player);
        int option = 0;
        for (int i = 0; i < cardsAvailables.size(); i++) {
            System.out.println((i + 1) + ") " + cardsAvailables.get(i));
            option = i + 1;
        }
        option = readInteger(1, option);
        DynastyCard cardSelected = (DynastyCard) cardsAvailables.get(option - 1).clone();
        int addQuantity = 1;
        if (cardSelected.getDeckLimit() != 1
                && cardSelected.getQuantity() > 1
                && player.getNumberDynastyCards() + 1 < MAX_CONFLICT_CARDS) {
            System.out.println("You have " + player.getNumberDynastyCards() + " cards in your Dynasy deck. How many copies do you want of this card?");
            for (int i = 1; i <= cardSelected.getQuantity()
                    && MAX_CONFLICT_CARDS >= player.getNumberDynastyCards() + i
                    && i <= cardSelected.getDeckLimit()
                    && i <= MAX_CARD_COPIES; i++) {
                System.out.print(i + ", ");
                addQuantity = i;
            }
            addQuantity = readInteger(1, addQuantity);
        }
        DynastyCard cardToAdd = (DynastyCard) cardSelected.clone();
        cardToAdd.setQuantity(addQuantity);
        player.getDynastyCardDeck().add(cardToAdd);
        player.setNumberDynastyCards(player.getNumberDynastyCards() + addQuantity);
        if (isRestrictedCard(cardSelected, Constants.DYNASTY))
            player.setContainsRestrictedCards(Boolean.TRUE);
        int quantity = cardSelected.getQuantity() - addQuantity;
        int index = this.collectionL5R.getDynastyCardList().indexOf(cardSelected);
        if (quantity < 1) {
            this.collectionL5R.getDynastyCardList().remove(cardSelected);
        } else {
            this.collectionL5R.getDynastyCardList().get(index).setQuantity(quantity);
        }
    }

    private void makeDynastyOptions(List<DynastyCard> cardsAvailables, Deck player) {
        List<DynastyCard> selection =
                this.collectionL5R.getDynastyCardList().stream()
                        .filter(card -> !dynastyCardIsPresent(player, card)
                                && isAllowedCard(player, card, Constants.DYNASTY)
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
                return true;
            }
        }
        return false;
    }

    private void selectProvinces() throws CloneNotSupportedException {
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
        System.out.println("Player " + player.getNamePlayer() + ", choose your Province number " + (player.getProvinces().size() + 1));
        List<String> elementsAvailables = new ArrayList<>();
        List<ProvinceCard> provincesAvailables = new ArrayList<>();
        for (int i = 0; i < ELEMENTS.length; i++) {
            if (player.getLimitProvince()[i] > 0) {
                elementsAvailables.add(ELEMENTS[i]);
            }
        }
        makeProvinceOptions(elementsAvailables, provincesAvailables, player);
        int option = 0;
        for (int i = 0; i < provincesAvailables.size(); i++) {
            System.out.println((i + 1) + ") " + provincesAvailables.get(i));
            option = 1 + i;
        }
        option = readInteger(1, option);
        ProvinceCard selectedCard = (ProvinceCard) provincesAvailables.get(option - 1).clone();
        selectedCard.setQuantity(1);
        player.getProvinces().add(selectedCard);
        if (isRestrictedCard(selectedCard, Constants.PROVINCE))
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
            this.collectionL5R.getProvinceCardList().get(index).setQuantity(quantity);
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
                        .filter(card ->
                                (elementsAvailables.contains(card.getElement())
                                    || card.getElement().equals(Constants.ALL_ELEMENTS))
                                && !pronvinceIsPresent(player, card)
                                && isAllowedCard(player, card, Constants.PROVINCE)
                                && ((card.getClan() == null)
                                    || card.getClan().equals(player.getClan())
                                    || card.getClan().equals(Constants.NEUTRAL))
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
        for (Deck player : players) {
            ArrayList<StrongholdCard> strongholdValidList = new ArrayList<>();
            this.collectionL5R.getStrongholdCardList().stream()
                    .filter(card -> player.getClan().equals(card.getClan()))
                    .forEach(strongholdValidList::add);
            System.out.println("Player " + player.getNamePlayer() + ", choose your Stronghold " + player.getClan());
            int option = 0;
            for (int i = 0; i < strongholdValidList.size(); i++) {
                System.out.println((i + 1) + ") " + strongholdValidList.get(i));
                option = i + 1;
            }
            option = readInteger(1, option);
            StrongholdCard selectedCard = (StrongholdCard) strongholdValidList.get(option - 1).clone();
            player.setStronghold(selectedCard);
            player.setInfluence(player.getInfluence() + selectedCard.getInfluence());
            player.getStronghold().setQuantity(1);
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
        for (Deck player : players) {
            if (player.getSplash() == null) {
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
                System.out.println("Player " + player.getNamePlayer() + ", choose your splash clan");
                int option = 0;
                for (int i = 0; i < NUMBER_OPTIONS; i++) {
                    System.out.println((i + 1) + ") " + CLANS[arrayOptions[i]]);
                    option = i + 1;
                }
                option = readInteger(1, option);
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
        System.out.println("Player " + n + ", choose a clan");
        ArrayList<String> selectingClan = randomClan();
        for (int i = 0; i < selectingClan.size(); i++)
            System.out.println("\t" + (i + 1) + ". " + selectingClan.get(i));
        int clanNumber = readInteger(1, NUMBER_OPTIONS);
        return selectingClan.get(clanNumber - 1);
    }

    private ArrayList<String> randomClan() {
        ArrayList<String> selectingClan = new ArrayList<>();
        String[] clansRemaining = new String[this.CLANS.length];
        System.arraycopy(this.CLANS, 0, clansRemaining, 0, this.CLANS.length);
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
        List<RoleCard> options = this.collectionL5R.getRoleCardList().stream()
                .filter(card -> this.restrictedRoles.getRestrictedRolesLists().get(player.getClan()).contains(card.getName())).collect(Collectors.toCollection(ArrayList::new));
        System.out.println("Player " + player.getNamePlayer() + ", choose a card: ");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ") " + options.get(i).toString());
        }
        RoleCard cardSelected = options.get(readInteger(1, options.size()) - 1);
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
                int option = 0;
                for (int i = 0; i < cardsAvailables.size(); i++) {
                    System.out.println((i + 1) + ") " + cardsAvailables.get(i));
                    option = i + 1;
                }
                option = readInteger(1, option);
                RoleCard roleCardSelected = (RoleCard) cardsAvailables.get(option - 1).clone();
                player.setRoleCard(roleCardSelected);
                player.setRole(roleCardSelected.getRole());
                player.getRoleCard().setQuantity(1);
                if (roleCardSelected.getName().toLowerCase().contains(Constants.SUPPORT)) {
                    player.setSplash(roleCardSelected.getClan());
                    player.setInfluence(player.getInfluence() + 8);
                } else {
                    player.setElement(roleCardSelected.getElement());
                    if (roleCardSelected.getRole().toLowerCase().equals(Constants.KEEPER))
                        player.setInfluence(player.getInfluence() + 3);
                    else if (roleCardSelected.getRole().toLowerCase().equals(Constants.SEEKER)) {
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

    private int readInteger(int min, int max) {
        int n = READ_CONSOLE.nextInt();
        while (n < min || n > max) {
            System.out.println("You must to choose a number between " + min + " and " + max + ".");
            n = READ_CONSOLE.nextInt();
        }
        return n;
    }


}