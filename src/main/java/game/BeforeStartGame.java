package game;

import lombok.NoArgsConstructor;
import utils.Utils;

@NoArgsConstructor
public class BeforeStartGame {



    public static Boolean allowRestrictedRoles;
    public static Boolean allowRestrictedCards;


    public static void allowRestrictedRules() {
        System.out.println("Do you want to play with official Roles?\n(1 = YES / 2 = NO)");
        if (Utils.readInteger(1, 2) == 1) {
            allowRestrictedRoles = Boolean.TRUE;
        } else {
            allowRestrictedRoles = Boolean.FALSE;
        }

        System.out.println("Do you want to play with restricted card rules?\n(1 = YES / 2 = NO)");
        if (Utils.readInteger(1, 2) == 1) {
            allowRestrictedCards = Boolean.TRUE;
        } else {
            allowRestrictedCards = Boolean.FALSE;
        }
    }


}
