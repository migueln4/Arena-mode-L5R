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

    void exportPlayers(Deck player1, Deck player2) {
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
        }
    }

}
