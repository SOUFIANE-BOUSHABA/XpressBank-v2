package org.example.version2_xpresbank.Utils;

import org.example.version2_xpresbank.Entity.User;
import org.example.version2_xpresbank.Service.AuthService;
import org.springframework.stereotype.Component;

@Component
public class PermissionUtils {

    private final AuthService authService;

    public PermissionUtils(AuthService authService) {
        this.authService = authService;
    }

    public void checkAdminPermission(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        User user = authService.getUserFromSession(token);
        if (!AuthUtils.hasRole(user, "ADMIN")) {
            throw new SecurityException("Unauthorized. Only ADMIN users");
        }
    }

    public void checkUserPermission(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        User user = authService.getUserFromSession(token);
        if (!AuthUtils.hasRole(user, "USER")) {
            throw new SecurityException("Unauthorized. Only USER users ");
        }
    }

    public void CheckEmployPermission(String authorizationHeader) {
        String token = extractToken(authorizationHeader);
        User user = authService.getUserFromSession(token);
        if (!AuthUtils.hasRole(user, "EMPLOYEE")) {
            throw new SecurityException("Unauthorized. Only EMPLOYEE users ");
        }
    }

    public boolean isAdminOrEmployee(User user) {
        return AuthUtils.hasRole(user, "ADMIN") || AuthUtils.hasRole(user, "EMPLOYEE");
    }

    private String extractToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
