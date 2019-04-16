import cards.RoleCard;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
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

        this.player1 = new Deck();
        this.player2 = new Deck();
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
        selectingRole();
        //player1.setSplash(selectSplash(player1.getClan()));
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

    private void selectingRole() {
        int indexMainClan = checkMainClan(player1);
        System.out.println("LOG --- Se va a excluir el número "+indexMainClan);
        int[] arrayNumbers;
        if(indexMainClan > -1)
            arrayNumbers = createArrayNumbers(this.collectionL5R.getRoleCardList().size()-2,indexMainClan);
        else
            arrayNumbers = createArrayNumbers(this.collectionL5R.getRoleCardList().size()-1);
        for(int i : arrayNumbers)
            System.out.println(i);
    }


    private int[] createArrayNumbers (int size, int exclude) {
        int[] result = new int[size-1];
        int aux = 0;
        for(int i=0; i<result.length; i++) {
            if(i == exclude)
                continue;
            else
                result[aux] = i;
            aux++;
        }
        return result;
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



    private String selectSplash(String primaryClan) {
        ArrayList<String> selectingSplash = randomClan(primaryClan);
        for(int i= 0; i<selectingSplash.size();i++)
            System.out.println("\t"+(i+1)+". "+selectingSplash.get(i));
        int clanNumber = leerEntero();
        return selectingSplash.get(clanNumber-1);
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
