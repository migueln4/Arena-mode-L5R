import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Arena {

    private Jugador[] jugadores = new Jugador[2];
    private static final Scanner LEER_CONSOLA = new Scanner(System.in);
    private List<String> clanes;
    private List<Card> todasCartas;

    public void iniciar(Map<String,Card> todasCartas) throws Exception {
        this.todasCartas = new ArrayList<>(todasCartas.values());
        inicializarJugadores();
        inicializarClanes();
        elegirClan();
        elegirRol();
    }

    private void elegirRol() {
        List<Card> roles = todasCartas.stream().filter(carta -> carta.getDeck().equals("role")).collect(Collectors.toList());
        for(int i = 0; i<2; i++) {
            System.out.println("Elige tu rol, jugador "+(1+i));
            imprimirRoles(roles);

        }


    }

    private void imprimirRoles(List<Card> roles) {
        int index = 1;
        for (Card carta : roles) {
            System.out.println(index+") "+carta);
        }
    }

    private void inicializarJugadores() {
        jugadores[0] = new Jugador();
        jugadores[0].setMazo(new ArrayList<>());
        jugadores[1] = new Jugador();
        jugadores[1].setMazo(new ArrayList<>());
    }

    private void inicializarClanes() {
        clanes.add("CRAB");
        clanes.add("CRANE");
        clanes.add("DRAGON");
        clanes.add("LION");
        clanes.add("PHOENIX");
        clanes.add("SCORPION");
        clanes.add("UNICORN");
    }

    private void elegirClan() throws Exception {
        List<String> clanesPrincipales = clanes;
        List<String> clanesSecundarios = clanes;
        for(int i = 0; i<2; i++) {
            System.out.println("Elige el clan que quieres, jugador "+(i+1));
            int index = 1;
            for(String clan : clanes) {
                System.out.println(index+") "+clan);
            }
            int queClan = leerEntero();
            jugadores[i].setClan(seleccionarClan(queClan));
            clanesPrincipales.remove(queClan-1);
        }
        for(int i = 0; i<2; i++) {
            System.out.println("Elige el clan secundario que quieras, jugador "+(1+i));
            int index = 1;
            for(String clan : clanes) {
                System.out.println(index+") "+clan);
            }
            int queClan = leerEntero();
            jugadores[i].setClan(seleccionarClan(queClan));
            clanesSecundarios.remove(queClan-1);
        }
    }

    private String seleccionarClan (int n) throws Exception {
        switch(n) {
            case 1:
                return "crab";
            case 2:
                return "crane";
            case 3:
                return "dragon";
            case 4:
                return "lion";
            case 5:
                return "phoenix";
            case 6:
                return "scorpion";
            case 7:
                return "unicorn";
            default:
                throw new Exception();
        }
    }

    private int leerEntero() {
        return LEER_CONSOLA.nextInt();
    }
}
