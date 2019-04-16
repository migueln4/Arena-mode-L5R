import cards.RoleCard;
import cards.StrongholdCard;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor
public class StartGame {

    private static final Scanner LEER_CONSOLA = new Scanner(System.in);

    final String[] CLANS = {"lion","scorpion","phoenix","crane","crab","unicorn","dragon"};
    final String[] ELEMENTS = {"void","air","water","fire","earth"};
    final int NUMBER_OPTIONS = 3;

    private Deck player1;
    private Deck player2;

    private CollectionL5R collectionL5R;

    public void start() {

        this.player1 = new Deck("UNO");
        this.player2 = new Deck("DOS");
        this.collectionL5R = new CollectionL5R();
        this.collectionL5R.readFile();
        this.collectionL5R.initializeConflictCardList();
        this.collectionL5R.initializeDynastyCardList();
        this.collectionL5R.initializeProvinceCardList();
        this.collectionL5R.initializeRoleCardList();
        this.collectionL5R.initializeStrongholdCardList();
        playerTurn();
    }

    private void playerTurn () {
        selectingClan();
        selectingRole(player1);
        selectingRole(player2);
        if(player1.getSplash() == null)
            selectSplash(player1);
        if(player2.getSplash() == null)
            selectSplash(player2);
        selectStronghold(player1);
        selectStronghold(player2);
        System.out.println("Resumen de los jugadores: ");
        System.out.println(player1.getNamePlayer()+"\n\tClan: "+player1.getClan()+"\n\tRol: "+player1.getRoleCard()+
                "\n\tSplash: "+player1.getSplash()+"Stronghold: "+player1.getStronghold());
        System.out.println(player2.getNamePlayer()+"\n\tClan: "+player2.getClan()+"\n\tRol: "+player2.getRoleCard()+
                "\n\tSplash: "+player2.getSplash()+"Stronghold: "+player2.getStronghold());
    }

    private void selectStronghold(Deck player) {
        List<StrongholdCard> strongholdValidList = new ArrayList<>();
        this.collectionL5R.getStrongholdCardList().stream()
                .filter(card -> player.getClan().equals(card.getClan()))
                .forEach(card -> strongholdValidList.add(card));
        System.out.println("Jugador "+player.getNamePlayer()+" elige un Stronghold para tu clan "+player.getClan());
        for(int i=0; i<strongholdValidList.size(); i++) {
            System.out.println((i+1)+") "+strongholdValidList.get(i));
        }
        int option = leerEntero();
        StrongholdCard selectedCard = strongholdValidList.get(option-1);
        player.setStronghold(selectedCard);
        player.setInfluence(player.getInfluence()+selectedCard.getInfluence());
        int indexCard = this.collectionL5R.getStrongholdCardList().indexOf(selectedCard);
        int quantity = this.collectionL5R.getStrongholdCardList().get(indexCard).getQuantity();
        this.collectionL5R.getStrongholdCardList().get(indexCard).setQuantity(--quantity);
        if(quantity == 0)
            this.collectionL5R.getStrongholdCardList().remove(selectedCard);
    }

    private void selectSplash(Deck player) {
        String mainClan = player.getClan();
        int mainClanIndex = -1;
        for(int i = 0; i<CLANS.length; i++) {
            if(CLANS[i].equals(mainClan))
                mainClanIndex = i;
        }
        int[] arrayClans = createArrayNumbers(CLANS.length,mainClanIndex);
        int[] arrayOptions = new int[NUMBER_OPTIONS];
        for(int i = 0; i<NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random()*(arrayClans.length-1-i));
            arrayOptions[i] = arrayClans[rnd];
            arrayClans[rnd] = arrayClans.length-1-i;
        }
        System.out.println("Jugador "+player.getNamePlayer()+" elige cuál es tu clan secundario");
        for(int i = 0; i<NUMBER_OPTIONS; i++)
            System.out.println((i+1)+") "+CLANS[arrayOptions[i]]);
        int option = leerEntero();
        player.setSplash(CLANS[arrayOptions[option-1]]);
    }

    private int[] createArrayNumbers (int size, int exclude) {
        int[] result = new int[size-1];
        int aux = 0;
        for(int i=0; aux<result.length; i++) {
            if(i == exclude)
                continue;
            else
                result[aux] = i;
            aux++;
        }
        return result;
    }

    private void selectingClan() {
        player1.setClan(selectClan(1));
        System.out.println("LOG --- El jugador 1 tiene el clan "+player1.getClan());
        player2.setClan(selectClan(2));
    }

    private String selectClan(int n) {
        System.out.println("Jugador "+n+", selecciona el clan que quieras");
        ArrayList<String> selectingClan = randomClan();
        for (int i = 0; i<selectingClan.size(); i++)
            System.out.println("\t"+(i+1)+". "+selectingClan.get(i));
        int clanNumber = leerEntero();
        return selectingClan.get(clanNumber-1);
    }

    private ArrayList<String> randomClan() {
        ArrayList<String> selectingClan = new ArrayList<>();
        String[] clansRemaining = copyArray(CLANS);
        for (int i = 0; i<NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random()*(clansRemaining.length-1-i));
            selectingClan.add(clansRemaining[rnd]);
            String aux = clansRemaining[rnd];
            clansRemaining[rnd] = clansRemaining[clansRemaining.length-1-i];
            clansRemaining[clansRemaining.length-1-i] = aux;
        }
        return selectingClan;
    }

    private void selectingRole(Deck player) {
        int indexMainClan = checkMainClan(player);
        System.out.println("LOG --- Se va a excluir el número "+indexMainClan);
        int[] arrayNumbers;
        if(indexMainClan > -1)
            arrayNumbers = createArrayNumbers(this.collectionL5R.getRoleCardList().size()-2,indexMainClan);
        else
            arrayNumbers = createArrayNumbers(this.collectionL5R.getRoleCardList().size()-1);
        int[] arrayOptions = new int[NUMBER_OPTIONS];
        System.out.println("LOG --- El tamaño del arrayNumbers es "+arrayNumbers.length);
        for(int i = 0; i<NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random()*(arrayNumbers.length-1-i));
            arrayOptions[i] = arrayNumbers[rnd];
            int aux = arrayNumbers[arrayNumbers.length-1-i];
            arrayNumbers[rnd] = aux;
        }
        System.out.println("Jugador "+player.getNamePlayer()+" selecciona una carta de rol.");
        for(int i = 0; i<arrayOptions.length;i++) {
            System.out.println((i+1)+") "+this.collectionL5R.getRoleCardList().get(arrayOptions[i]));
        }
        int roleOption = leerEntero();
        RoleCard roleCardSelected = this.collectionL5R.getRoleCardList().get(arrayOptions[roleOption-1]);
        player.setRoleCard(roleCardSelected);
        if(roleCardSelected.getName().contains("Support")) {
            player.setSplash(roleCardSelected.getClan());
            player.setInfluence(player.getInfluence()+8);
        }
        else {
            player.setRole(roleCardSelected.getRole());
            player.setElement(roleCardSelected.getElement());
            if(roleCardSelected.getName().contains("Keeper"))
                player.setInfluence(player.getInfluence()+3);
        }
        int cardSelected = this.collectionL5R.getRoleCardList().indexOf(roleCardSelected);
        int quantityCard = this.collectionL5R.getRoleCardList().get(cardSelected).getQuantity();
        this.collectionL5R.getRoleCardList().get(cardSelected).setQuantity(--quantityCard);
        if(quantityCard == 0)
            this.collectionL5R.getRoleCardList().remove(roleCardSelected);
        System.out.println("LOG --- El jugador "+player.getNamePlayer()+" ha elegido la carta "+player.getRoleCard().toString());

    }


    private int[] createArrayNumbers (int size) {
        int[] result = new int[size];
        for(int i=0; i<size; i++)
            result[i] = i;
        return result;
    }

    private int checkMainClan(Deck player) {
        for(RoleCard card : this.collectionL5R.getRoleCardList()) {
            System.out.println("LOG --- Clan de la carta: "+card.getClan());
            System.out.println("LOG --- Clan del jugador: "+player.getClan());
            if(player.getClan().equals(card.getClan())) {
                return this.collectionL5R.getRoleCardList().indexOf(card);
            }
        }
        return -1;
    }


    private String[] copyArray(String[] array) {
        String[] result = new String[array.length];
        for(int i=0;i<array.length;i++) {
            result[i] = array[i];
        }
        return result;
    }

    private String[] copyArray(String[] array, String clan) {
        String[] result = new String[array.length-1];
        int j = 0;
        for(int i=0; i<array.length;i++) {
            if(!clan.equals(array[i])) {
                result[j] = array[i];
                j++;
            }
        }
        return result;
    }

    private ArrayList<String> randomClan (String clan) {
        ArrayList<String> selectingClan = new ArrayList<>();
        String[] clansRemaining = copyArray(CLANS,clan);
        for (int i = 0; i<NUMBER_OPTIONS; i++) {
            int rnd = (int) (Math.random()*(clansRemaining.length-1-i));
            selectingClan.add(clansRemaining[rnd]);
            String aux = clansRemaining[rnd];
            clansRemaining[rnd] = clansRemaining[clansRemaining.length-1-i];
            clansRemaining[clansRemaining.length-1-i] = aux;
        }
        return selectingClan;
    }

    private int leerEntero() {
        return LEER_CONSOLA.nextInt();
    }


}
