import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.Scanner;

@NoArgsConstructor
public class StartGame {

    private static final Scanner LEER_CONSOLA = new Scanner(System.in);

    final String[] CLANS = {"lion","scorpion","phoenix","crane","crab","unicorn","dragon"};
    final int NUMBER_OPTIONS = 3;

    Deck player1;
    Deck player2;

    CollectionL5R collectionL5R;

    public void start() {

        //player1 = new Deck();
        //System.out.println("Jugador 1, selecciona el clan que quieras");
        //player1.setClan(selectClan());
        //player1.setSplash(selectSplash(player1.getClan()));
        collectionL5R = new CollectionL5R();
        collectionL5R.readFile();
        collectionL5R.initializeConflictCardList();
        collectionL5R.initializeDynastyCardList();
    }
/*
    private String selectSplash(String primaryClan) {
        ArrayList<String> selectingSplash = randomClan(primaryClan);
        for(int i= 0; i<selectingSplash.size();i++)
            System.out.println("\t"+(i+1)+". "+selectingSplash.get(i));
        int clanNumber = leerEntero();
        return selectingSplash.get(clanNumber-1);
    }

    private String selectClan() {
        ArrayList<String> selectingClan = randomClan();
        for (int i = 0; i<selectingClan.size(); i++)
            System.out.println("\t"+(i+1)+". "+selectingClan.get(i));
        int clanNumber = leerEntero();
        return selectingClan.get(clanNumber-1);
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

 */

}
