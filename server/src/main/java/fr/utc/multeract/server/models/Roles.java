package fr.utc.multeract.server.models;

import java.util.Arrays;

public class Roles {
    public static final String HISTORY_INSTANCE_SCREEN = "HISTORY_INSTANCE_SCREEN";
    public static final String HISTORY_INSTANCE_USER = "HISTORY_INSTANCE_USER";
    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    public static final String[] ALL_ROLES = {HISTORY_INSTANCE_SCREEN, HISTORY_INSTANCE_USER, USER, ADMIN};

    public static boolean isRole(String role) {
        for (String r : ALL_ROLES) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }

    //valueOf(String role) returns the enum constant of this type with the specified name.
    public static String valueOf(String role) {
        if(Arrays.stream(ALL_ROLES).anyMatch(role::equals))
            return role;
        return null;
    }
}
