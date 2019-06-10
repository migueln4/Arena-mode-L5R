package restrictions;

import constants.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RestrictedRoles {

    //Last update: 02/05/2019

    private Map<String,List<String>> restrictedRolesLists;
    private Integer rolesPerClan;

    public RestrictedRoles() {
        this.restrictedRolesLists = new HashMap<>(7);

        List<String> crabRestrictedRoles = new ArrayList<>();
        crabRestrictedRoles.add("Keeper of Water");
        crabRestrictedRoles.add("Seeker of Void");
        this.restrictedRolesLists.put(Constants.CRAB,crabRestrictedRoles);

        List<String> craneRestrictedRoles = new ArrayList<>();
        craneRestrictedRoles.add("Seeker of Fire");
        craneRestrictedRoles.add("Seeker of Void");
        this.restrictedRolesLists.put(Constants.CRANE,craneRestrictedRoles);

        List<String> dragonRestrictedRoles = new ArrayList<>();
        dragonRestrictedRoles.add("Seeker of Void");
        dragonRestrictedRoles.add("Keeper of Water");
        this.restrictedRolesLists.put(Constants.DRAGON,dragonRestrictedRoles);

        List<String> lionRestrictedRoles = new ArrayList<>();
        lionRestrictedRoles.add("Keeper of Earth");
        lionRestrictedRoles.add("Seeker of Air");
        this.restrictedRolesLists.put(Constants.LION,lionRestrictedRoles);

        List<String> phoenixRestrictedRoles = new ArrayList<>();
        phoenixRestrictedRoles.add("Keeper of Air");
        phoenixRestrictedRoles.add("Seeker of Void");
        this.restrictedRolesLists.put(Constants.PHOENIX,phoenixRestrictedRoles);

        List<String> scorpionRestrictedRoles = new ArrayList<>();
        scorpionRestrictedRoles.add("Seeker of Air");
        scorpionRestrictedRoles.add("Keeper of Air");
        this.restrictedRolesLists.put(Constants.SCORPION,scorpionRestrictedRoles);

        List<String> unicornRestrictedRoles = new ArrayList<>();
        unicornRestrictedRoles.add("Seeker of Water");
        unicornRestrictedRoles.add("Keeper of Water");
        this.restrictedRolesLists.put(Constants.UNICORN,unicornRestrictedRoles);

        this.rolesPerClan = unicornRestrictedRoles.size();

    }
}
