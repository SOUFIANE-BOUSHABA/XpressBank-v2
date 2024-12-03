package org.example.version2_xpresbank.Controller;

import org.example.version2_xpresbank.DTO.RegisterUserDTO;
import org.example.version2_xpresbank.Service.AuthService;
import org.example.version2_xpresbank.VM.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService userService;

    @Autowired
    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserVM register(@RequestBody RegisterUserDTO registerUserDTO) {
        return userService.register(registerUserDTO);
    }

    @PostMapping("/login")
    public  Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        return userService.login(username, password);
    }

    @GetMapping("/user")
    public UserVM getLoggedInUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        return userService.getLoggedInUser(token);
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
