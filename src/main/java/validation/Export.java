package validation;

import cards.Deck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

import java.io.FileWriter;

@Data
public class Export {

    private FileWriter fw;
    private Gson gson;

    public Export() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void exportPlayers(Deck player1, Deck player2) {
        StringBuilder srcPlayer1 = new StringBuilder(player1.getNamePlayer());
        StringBuilder srcPlayer2 = new StringBuilder(player2.getNamePlayer());
        srcPlayer1.append("_player1.json");
        srcPlayer2.append("_player2.json");
        try {
            this.fw = new FileWriter(srcPlayer1.toString());
            String player1Json = this.gson.toJson(player1);
            this.fw.write(player1Json);
            this.fw.flush();
            this.fw = new FileWriter(srcPlayer2.toString());
            String player2Json = this.gson.toJson(player2);
            this.fw.write(player2Json);
            this.fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            summary(player1, player2);
        }
    }

    private void summary(Deck player1, Deck player2) {
        StringBuilder strb = new StringBuilder("{\n\t\"decks\": [\n\t\t{\n");
        try {
            this.fw = new FileWriter("summary.json");
            strb.append(doSummaryPlayer(player1)).append("\n\t\t},\n{\n");
            strb.append(doSummaryPlayer(player2)).append("\n\t\t}\n]\n}");
            this.fw.write(strb.toString());
            this.fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String doSummaryPlayer(Deck player) {
        StringBuilder strb = new StringBuilder("\t\t\t");
        strb.append("\"").append(player.getStronghold().getId()).append("\": ").append(player.getStronghold().getQuantity()).append(",\n");
        strb.append("\t\t\t\"" + player.getRoleCard().getId() + "\": " + player.getRoleCard().getQuantity() + ",\n");
        player.getProvinces().forEach(card -> strb.append("\t\t\t\"" + card.getId()).append("\": ").append(card.getQuantity()).append(",\n"));
        player.getDynastyCardDeck().forEach(card -> strb.append("\t\t\t\"" + card.getId()).append("\": ").append(card.getQuantity()).append(",\n"));
        player.getConflictCardDeck().forEach(card -> strb.append("\t\t\t\"" + card.getId()).append("\": ").append(card.getQuantity()).append(",\n"));
        strb.setLength(strb.length() - 2);
        return strb.toString();
    }
}
