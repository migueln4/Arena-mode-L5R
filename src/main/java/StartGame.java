import cards.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@NoArgsConstructor
public class StartGame {

    private static final Scanner LEER_CONSOLA = new Scanner(System.in);

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

    public void start() throws CloneNotSupportedException {

        this.player1 = new Deck("UNO");
        this.player2 = new Deck("DOS");
        this.collectionL5R = new CollectionL5R();
        this.collectionL5R.readFile();
        this.collectionL5R.deleteExceptions();
        this.collectionL5R.initializeConflictCardList();
        this.collectionL5R.initializeDynastyCardList();
        this.collectionL5R.initializeProvinceCardList();
        this.collectionL5R.initializeRoleCardList();
        this.collectionL5R.initializeStrongholdCardList();
        playerTurn();
    }

    private void playerTurn() throws CloneNotSupportedException {
        selectingClan();
        selectingRole(player1);
        selectingRole(player2);
        if (player1.getSplash() == null)
            selectSplash(player1);
        if (player2.getSplash() == null)
            selectSplash(player2);
        selectStronghold(player1);
        selectStronghold(player2);
        selectProvinces(player1, player2);
        selectDynastyDeck(player1, player2);
        selectConflictDeck(player1,player2);


        System.out.println("Resumen de los jugadores: ");
        System.out.println("===========================");

        System.out.println(player1.getNamePlayer() + "\n\tClan: " + player1.getClan() + "\n\tRol: " + player1.getRoleCard() +
                "\n\tSplash: " + player1.getSplash() + "Stronghold: " + player1.getStronghold());
        System.out.println("Provinces");
        for (ProvinceCard card : player1.getProvinces())
            System.out.println(card.toString());
        System.out.println("Número de cartas en el mazo de Dinastía: "+player1.getNumberDynastyCards());
        System.out.println("Número de cartas en el mazo de Conflicto: "+player1.getNumberConflictCards());
        System.out.println("Personajes de conflicto: "+player1.getNumberCharacters());
        System.out.println("Influencia restante: "+player1.getInfluence());

        System.out.println("===========================");

        System.out.println(player2.getNamePlayer() + "\n\tClan: " + player2.getClan() + "\n\tRol: " + player2.getRoleCard() +
                "\n\tSplash: " + player2.getSplash() + "Stronghold: " + player2.getStronghold());
        System.out.println("Provinces");
        for (ProvinceCard card : player2.getProvinces())
            System.out.println(card.toString());
        System.out.println("Número de cartas en el mazo de Dinastía: "+player2.getNumberDynastyCards());
        System.out.println("Número de cartas en el mazo de Conflicto: "+player2.getNumberConflictCards());
        System.out.println("Personajes de conflicto: "+player2.getNumberCharacters());
        System.out.println("Influencia restante: "+player2.getInfluence());
    }

    private void selectConflictDeck(Deck player1, Deck player2) throws CloneNotSupportedException {
        boolean flagPlayer1 = player1.getNumberConflictCards() < MIN_CONFLICT_CARDS;
        boolean flagPlayer2 = player2.getNumberConflictCards() < MIN_CONFLICT_CARDS;
        while(flagPlayer1 || flagPlayer2) {
            if(flagPlayer1) {
                selectConflictCard(player1);
                if(player1.getNumberConflictCards() >= MIN_CONFLICT_CARDS
                    && player1.getNumberConflictCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Jugador "+player1.getNamePlayer()+ " " +
                            "has llegado al mínimo del mazo de Conflicto. Tienes "+player1.getNumberConflictCards()+
                            " cartas. \n\t¿Quieres dejar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if(leerEntero() == 1) {
                        flagPlayer1 = false;
                    }
                } else {
                    flagPlayer1 = player1.getNumberConflictCards() < MAX_CONFLICT_CARDS;
                }
            }
            if(flagPlayer2) {
                selectConflictCard(player1);
                if(player2.getNumberConflictCards() >= MIN_CONFLICT_CARDS
                        && player2.getNumberConflictCards() < MAX_CONFLICT_CARDS) {
                    System.out.println("Jugador "+player2.getNamePlayer()+ " " +
                            "has llegado al mínimo del mazo de Conflicto. Tienes "+player2.getNumberConflictCards()+
                            " cartas. \n\t¿Quieres dejar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if(leerEntero() == 1) {
                        flagPlayer2 = false;
                    }
                } else {
                    flagPlayer2 = player2.getNumberConflictCards() < MAX_CONFLICT_CARDS;
                }
            }
        }
    }

    private void selectConflictCard(Deck player) throws CloneNotSupportedException {
        System.out.println("Jugador "+player.getNamePlayer()+ " elige tu carta"+
                " número "+(player.getNumberConflictCards()+1)+"\n (tienes "+
                player.getNumberConflictCards()+" cartas en tu mazo de Conflicto, "+
                player.getNumberCharacters()+" de ellas, personajes.\n(influencia restante: "+
                player.getInfluence()+" puntos)");
        List<ConflictCard> cardsAvailables = new ArrayList<>();
        makeConflictOptions(cardsAvailables,player);
        for(int i = 0; i<cardsAvailables.size(); i++) {
            System.out.println((i+1)+")"+cardsAvailables.get(i));
        }
        int option = leerEntero();
        ConflictCard cardSelected = cardsAvailables.get(option-1);
        int addQuantity = 1;
        if(cardSelected.getDeckLimit() != 1
           && cardSelected.getQuantity() > 1
           && player.getNumberConflictCards()+1 < MAX_CONFLICT_CARDS
           && oneMoreCharacter(player,cardSelected,1)
           && oneMoreSplashCard(player,cardSelected,1)) {
            System.out.println("Tienes "+player.getNumberConflictCards() + " cartas en tu mazo "+
                    "de Conflicto. ¿Cuántas copias quieres de esta carta?");
            for (int i=1; i<=cardSelected.getQuantity()
                            && MAX_CONFLICT_CARDS >= player.getNumberConflictCards()+i
                            && i <= MAX_CARD_COPIES
                            && i <= cardSelected.getDeckLimit()
                            && oneMoreCharacter(player,cardSelected,i)
                            && oneMoreSplashCard(player,cardSelected,i); i++) {
                System.out.print(i+", ");
            }
            addQuantity = leerEntero();
        }
        ConflictCard cardToAdd = (ConflictCard) cardSelected.clone();
        player.getConflictCardDeck().add(cardToAdd);
        player.setNumberConflictCards(player.getNumberConflictCards()+addQuantity);
        if(cardToAdd.getCharacter())
            player.setNumberCharacters(player.getNumberCharacters()+addQuantity);
        if(cardToAdd.getClan().equals(player.getSplash()))
            player.setInfluence(player.getInfluence()-(cardToAdd.getInfluence()*addQuantity));
        int quantity = cardSelected.getQuantity() - addQuantity;
        int index = this.collectionL5R.getConflictCardList().indexOf(cardSelected);
        if (quantity < 1) {
            this.collectionL5R.getConflictCardList().remove(cardSelected);
        } else {
            this.collectionL5R.getConflictCardList().get(index).setQuantity(Integer.valueOf(quantity));
        }
    }

    private boolean oneMoreSplashCard(Deck player, ConflictCard card, int i) {
        if(card.getClan() == null
            || card.getClan().equals("neutral")
            || card.getClan().equals(player.getClan())) {
            return true;
        } else {
            if(card.getInfluence()*i <= player.getInfluence()) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean oneMoreCharacter(Deck player, ConflictCard card, int i) {
        if(!card.getCharacter()) {
            return true;
        } else {
            if(player.getNumberCharacters()+i < MAX_CONFLICT_CHARACTERS) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void makeConflictOptions(List<ConflictCard> cardsAvailables, Deck player) {
        List<ConflictCard> selection = this.collectionL5R.getConflictCardList().stream()
                .filter(card -> !conflictCardIsPresent(player,card)
                            && characterConditions(player,card)
                            && (card.getClan().equals("neutral")
                                || card.getClan().equals(player.getClan())
                                || splashConditions(player,card))
                            && (card.getRoleLimit() == null
                                || card.getRoleLimit().equals("null")
                                || card.getRoleLimit().equals(player.getRoleCard().getRole()))
                            && (card.getElementLimit() == null
                                || card.getElementLimit().equals("null")
                                || card.getElementLimit().equals(player.getElement())))
                .collect(Collectors.toList());
        int rndArray[] = createArrayNumbers(selection.size());
        for(int i = 0; i<NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (rndArray.length - 1 - i));
            cardsAvailables.add(selection.get(rndArray[rnd]));
            rndArray[rnd] = rndArray[rndArray.length - 1 - i];
        }
    }

    private boolean splashConditions(Deck player, ConflictCard card) {
        if(player.getSplash() == null
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

    private boolean conflictCardIsPresent(Deck player, ConflictCard card) {
        for(ConflictCard conflictCard : player.getConflictCardDeck()) {
            if(card.equals(conflictCard)) {
                return true;
            }
        }
        return false;
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
                            "has llegado al mínimo del mazo (tienes "+player1.getNumberDynastyCards()+ "). " +
                            "¿Quieres terminar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if (leerEntero() == 1) {
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
                            "has llegado al mínimo del mazo (tienes "+player2.getNumberDynastyCards()+" ). " +
                            "¿Quieres " +
                            "terminar de añadir cartas?\n\t(1 = Sí / 2 = No)");
                    if (leerEntero() == 1) {
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
        System.out.println("Jugador " + player.getNamePlayer() + " elige tu carta" +
                " número " + (player.getNumberDynastyCards() + 1)+"\n(tienes "+player.getNumberDynastyCards()+" cartas en tu mazo de Dinastía)");
        List<DynastyCard> cardsAvailables = new ArrayList<>();
        makeDynastyOptions(cardsAvailables, player);
        for (int i = 0; i < cardsAvailables.size(); i++) {
            System.out.println((i + 1) + ") " + cardsAvailables.get(i));
        }
        int option = leerEntero();
        DynastyCard cardSelected = cardsAvailables.get(option - 1);
        int addQuantity = 1;
        if (cardSelected.getDeckLimit() != 1
                && cardSelected.getQuantity() > 1
                && player.getNumberDynastyCards()+1 < MAX_CONFLICT_CARDS) {
            System.out.println("Tienes " + player.getNumberDynastyCards() +
                    " cartas en tu mazo de Dinastía. ¿Cuántas copias quieres " +
                    "de esta carta?");
            for (int i = 1; i <= cardSelected.getQuantity()
                    && MAX_CONFLICT_CARDS >= player.getNumberDynastyCards() + i
                    && i <= cardSelected.getDeckLimit()
                    && i <= MAX_CARD_COPIES; i++)
                System.out.print(i + ", ");
            addQuantity = leerEntero();
        }
        DynastyCard cardToAdd = (DynastyCard) cardSelected.clone();
        cardToAdd.setQuantity(addQuantity);
        player.getDynastyCardDeck().add(cardToAdd);
        player.setNumberDynastyCards(player.getNumberDynastyCards() + addQuantity);
        int quantity = cardSelected.getQuantity() - addQuantity;
        int index =
                this.collectionL5R.getDynastyCardList().indexOf(cardSelected);
        if (quantity < 1) {
            this.collectionL5R.getDynastyCardList().remove(cardSelected);
        } else {
            this.collectionL5R.getDynastyCardList().get(index).setQuantity(Integer.valueOf(quantity));
        }
    }

    private void makeDynastyOptions(List<DynastyCard> cardsAvailables,
                                    Deck player) {
        List<DynastyCard> selection =
                this.collectionL5R.getDynastyCardList().stream()
                        .filter(card -> !dynastyCardIsPresent(player, card)
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
            if (card.equals(dynastyCard)) {
                return true;
            }
        }
        return false;
    }

    private void selectProvinces(Deck player1, Deck player2) {
        while (player1.getProvinces().size() < MAX_PROVINCE_CARDS && player2.getProvinces().size() < MAX_PROVINCE_CARDS) {
            if (player1.getProvinces().size() < MAX_PROVINCE_CARDS) {
                playerSelectingProvince(player1);
            }
            if (player2.getProvinces().size() < MAX_PROVINCE_CARDS) {
                playerSelectingProvince(player2);
            }
        }
    }

    private void playerSelectingProvince(Deck player) {
        System.out.println("Jugador " + player.getNamePlayer() + " elige tu provincia número " + (player.getProvinces().size() + 1));
        List<String> elementsAvailables = new ArrayList<>();
        List<ProvinceCard> provincesAvailables = new ArrayList<>();
        for (int i = 0; i < ELEMENTS.length; i++) {
            if (player.getLimitProvince()[i] > 0) {
                elementsAvailables.add(ELEMENTS[i]);
            }
        }
        makeProvinceOptions(elementsAvailables, provincesAvailables,
                player);
        for (int i = 0; i < provincesAvailables.size(); i++) {
            System.out.println((i + 1) + ") " + provincesAvailables.get(i));
        }
        int option = leerEntero();
        ProvinceCard selectedCard = provincesAvailables.get(option - 1);
        player.getProvinces().add(selectedCard);
        for (int i = 0; i < player.getLimitProvince().length; i++) {
            if (ELEMENTS[i].equals(selectedCard.getElement())) {
                player.getLimitProvince()[i] = player.getLimitProvince()[i] - 1;
            }
        }
        int quantity = selectedCard.getQuantity() - 1;
        int index =
                this.collectionL5R.getProvinceCardList().indexOf(selectedCard);
        if (quantity < 1) {
            this.collectionL5R.getProvinceCardList().remove(selectedCard);
        } else {
            this.collectionL5R.getProvinceCardList().get(index).setQuantity(Integer.valueOf(quantity));
        }
    }

    private void makeProvinceOptions(List<String> elementsAvailables,
                                     List<ProvinceCard> provincesAvailables,
                                     Deck player) {
        List<ProvinceCard> selection =
                this.collectionL5R.getProvinceCardList().stream()
                        .filter(card -> elementsAvailables.contains(card.getElement())
                                && !pronvinceIsPresent(player, card)
                                && (card.getClan() == null
                                || card.getClan().equals(player.getClan())
                                || card.getClan().equals("neutral"))
                                && (card.getRoleLimit() == null
                                || card.getRoleLimit().equals(player.getRoleCard().getRole())
                                || card.getRoleLimit().equals("null"))
                                && (card.getElementLimit() == null
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

    private boolean pronvinceIsPresent(Deck player, ProvinceCard province) {
        for (ProvinceCard card : player.getProvinces()) {
            if (card.equals(province)) {
                return true;
            }
        }
        return false;
    }

    private void selectStronghold(Deck player) {
        List<StrongholdCard> strongholdValidList = new ArrayList<>();
        this.collectionL5R.getStrongholdCardList().stream()
                .filter(card -> player.getClan().equals(card.getClan()))
                .forEach(card -> strongholdValidList.add(card));
        System.out.println("Jugador " + player.getNamePlayer() + " elige un Stronghold para tu clan " + player.getClan());
        for (int i = 0; i < strongholdValidList.size(); i++) {
            System.out.println((i + 1) + ") " + strongholdValidList.get(i));
        }
        int option = leerEntero();
        StrongholdCard selectedCard = strongholdValidList.get(option - 1);
        player.setStronghold(selectedCard);
        player.setInfluence(player.getInfluence() + selectedCard.getInfluence());
        int indexCard = this.collectionL5R.getStrongholdCardList().indexOf(selectedCard);
        int quantity = this.collectionL5R.getStrongholdCardList().get(indexCard).getQuantity();
        this.collectionL5R.getStrongholdCardList().get(indexCard).setQuantity(--quantity);
        if (quantity == 0)
            this.collectionL5R.getStrongholdCardList().remove(selectedCard);
    }

    private void selectSplash(Deck player) {
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
        int option = leerEntero();
        player.setSplash(CLANS[arrayOptions[option - 1]]);
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
        int clanNumber = leerEntero();
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

    private void selectingRole(Deck player) {
        int indexMainClan = checkMainClan(player);
        int[] arrayNumbers;
        if (indexMainClan > -1)
            arrayNumbers = createArrayNumbers(this.collectionL5R.getRoleCardList().size() - 2, indexMainClan);
        else
            arrayNumbers = createArrayNumbers(this.collectionL5R.getRoleCardList().size() - 1);
        int[] arrayOptions = new int[NUMBER_OPTIONS];
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (arrayNumbers.length - 1 - i));
            arrayOptions[i] = arrayNumbers[rnd];
            int aux = arrayNumbers[arrayNumbers.length - 1 - i];
            arrayNumbers[rnd] = aux;
        }
        System.out.println("Jugador " + player.getNamePlayer() + " selecciona una carta de rol.");
        for (int i = 0; i < arrayOptions.length; i++) {
            System.out.println((i + 1) + ") " + this.collectionL5R.getRoleCardList().get(arrayOptions[i]));
        }
        int roleOption = leerEntero();
        RoleCard roleCardSelected = this.collectionL5R.getRoleCardList().get(arrayOptions[roleOption - 1]);
        player.setRoleCard(roleCardSelected);
        if (roleCardSelected.getName().contains("Support")) {
            player.setSplash(roleCardSelected.getClan());
            player.setInfluence(player.getInfluence() + 8);
        } else {
            player.setRole(roleCardSelected.getRole());
            player.setElement(roleCardSelected.getElement());
            if (roleCardSelected.getRole().equals("keeper"))
                player.setInfluence(player.getInfluence() + 3);
            else if (roleCardSelected.getRole().equals("seeker")) {
                for (int i = 0; i < ELEMENTS.length; i++) {
                    if (roleCardSelected.getElement().equals(ELEMENTS[i])) {
                        player.getLimitProvince()[i] =
                                player.getLimitProvince()[i] + 1;
                    }
                }
            }
        }
        int cardSelected = this.collectionL5R.getRoleCardList().indexOf(roleCardSelected);
        int quantityCard = this.collectionL5R.getRoleCardList().get(cardSelected).getQuantity();
        this.collectionL5R.getRoleCardList().get(cardSelected).setQuantity(--quantityCard);
        if (quantityCard == 0)
            this.collectionL5R.getRoleCardList().remove(roleCardSelected);
    }

    private int[] createArrayNumbers(int size) {
        int[] result = new int[size];
        for (int i = 0; i < size; i++)
            result[i] = i;
        return result;
    }

    private int checkMainClan(Deck player) {
        for (RoleCard card : this.collectionL5R.getRoleCardList()) {
            if (player.getClan().equals(card.getClan())) {
                return this.collectionL5R.getRoleCardList().indexOf(card);
            }
        }
        return -1;
    }

    private String[] copyArray(String[] array) {
        String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    private String[] copyArray(String[] array, String clan) {
        String[] result = new String[array.length - 1];
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (!clan.equals(array[i])) {
                result[j] = array[i];
                j++;
            }
        }
        return result;
    }

    private ArrayList<String> randomClan(String clan) {
        ArrayList<String> selectingClan = new ArrayList<>();
        String[] clansRemaining = copyArray(CLANS, clan);
        for (int i = 0; i < NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random() * (clansRemaining.length - 1 - i));
            selectingClan.add(clansRemaining[rnd]);
            String aux = clansRemaining[rnd];
            clansRemaining[rnd] = clansRemaining[clansRemaining.length - 1 - i];
            clansRemaining[clansRemaining.length - 1 - i] = aux;
        }
        return selectingClan;
    }

    private int leerEntero() {
        return LEER_CONSOLA.nextInt();
    }


}
