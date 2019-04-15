package cards;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonPackCards {
    private String flavor;
    private String illustrator;
    private String image_url;
    private JsonObject pack;
    private String position;
    private Integer quantity;
}
