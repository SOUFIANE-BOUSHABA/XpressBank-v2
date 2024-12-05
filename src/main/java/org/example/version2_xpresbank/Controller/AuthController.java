package org.example.version2_xpresbank.Controller;

import org.example.version2_xpresbank.DTO.RegisterUserDTO;
import org.example.version2_xpresbank.Service.AuthService;
import org.example.version2_xpresbank.Service.JwtService;
import org.example.version2_xpresbank.VM.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthService userService , AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public UserVM register(@RequestBody RegisterUserDTO registerUserDTO) {
        return userService.register(registerUserDTO);
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        List<String> roles = userService.getUserRoles(username);
        String token = jwtService.generateToken(username, roles);

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/user")
    public ResponseEntity<UserVM> getLoggedInUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        String username = jwtService.extractUsername(token);

        UserVM userVM = userService.getUserByUsername(username);

        return ResponseEntity.ok(userVM);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        userService.logout(token);
        return "User logged out successfully";
    }

    @PostMapping("/restricted-action")
    public ResponseEntity<String> testAction(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        userService.testPermission(token);
        return ResponseEntity.ok("Action  successfully.");
    }
}
