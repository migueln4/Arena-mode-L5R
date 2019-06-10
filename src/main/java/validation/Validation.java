package validation;

import cards.Deck;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@NoArgsConstructor
@Data
public class Validation {

    private final String VALIDATE_URL = "https://api.fiveringsdb.com/deck-validation/standard";

    public void validateDecks(Deck player1, Deck player2) {
        Deck[] decks = new Deck[]{player1, player2};
        for (Deck player : decks) {
            String bodyplayer = createJsonToValidate(player);
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

                System.out.println("Connection: " + player.getNamePlayer() + ": " + connection.getResponseCode());

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
                System.out.println("Validating " + player.getNamePlayer() + "'s deck...\n\t-> " + validationCode(jsonObject.get("status").getAsInt()));
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

    private String validationCode(int code) {
        switch (code) {
            case 0:
                return "Deck is valid";
            case 1:
                return "Deck has too many copies of one or more cards";
            case 2:
                return "Deck has too few Strongholds";
            case 3:
                return "Deck has too many Strongholds";
            case 4:
                return "Deck has too may Roles";
            case 5:
                return "Deck has too few Dynasty cards";
            case 6:
                return "Deck has too many Dynasty cards";
            case 7:
                return "Deck has off-clan Dynasty cards";
            case 8:
                return "Deck has too few Conflict cards";
            case 9:
                return "Deck has too many Conflict cards";
            case 10:
                return "Deck does not have enough influence for its off-clan Conflict cards";
            case 11:
                return "Deck has more than one off-clan in its Conflict deck";
            case 12:
                return "Deck has too many Character cards in its Conflict deck";
            case 13:
                return "Deck has too few Provinces";
            case 14:
                return "Deck has too many Provinces";
            case 15:
                return "Deck has too many Provinces of one Element";
            case 16:
                return "Deck has an off-clan Province";
            case 17:
                return "Deck has an off-clan Conflict card with no influence cost";
            default:
                return "Unknown code";
        }
    }

}
