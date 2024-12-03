package org.example.version2_xpresbank.Utils;

import org.example.version2_xpresbank.Entity.User;

public class AuthUtils {

    public static boolean hasRole(User user, String roleName) {
        return user.getRole().getName().name().equalsIgnoreCase(roleName);
    }

    public static boolean hasPermission(User user, String permissionName) {
        return user.getRole().getPermissions().stream()
                .anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName));
    }
}
