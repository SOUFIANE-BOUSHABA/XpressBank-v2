package org.example.version2_xpresbank.Service;

import jakarta.transaction.Transactional;
import org.example.version2_xpresbank.DTO.RegisterUserDTO;
import org.example.version2_xpresbank.Entity.Enums.RoleType;
import org.example.version2_xpresbank.Entity.Role;
import org.example.version2_xpresbank.Entity.User;
import org.example.version2_xpresbank.Exception.UserNotFoundException;
import org.example.version2_xpresbank.Mapper.UserMapper;
import org.example.version2_xpresbank.Repository.RoleRepository;
import org.example.version2_xpresbank.Repository.UserRepository;
import org.example.version2_xpresbank.Utils.AuthUtils;
import org.example.version2_xpresbank.VM.UserVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final Map<String, User> sessions = new HashMap<>();

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserVM register(RegisterUserDTO registerUserDTO) {
        Role role = roleRepository.findByName(RoleType.valueOf(registerUserDTO.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + registerUserDTO.getRole()));

        User newUser = userMapper.fromRegisterUserDTO(registerUserDTO);
        newUser.setRole(role);

        User savedUser = userRepository.save(newUser);
        return userMapper.toUserVM(savedUser, "Registration successful");
    }

    public  Map<String, String> login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            sessions.put(token, user.get());

            Map<String, String> loginResponse = new HashMap<>();
            loginResponse.put("token", token);
            loginResponse.put("role", user.get().getRole().getName().toString());
            loginResponse.put("username", user.get().getUsername());

            return loginResponse;
        } else {
            throw new UserNotFoundException("Invalid username or password");
        }
    }



    public UserVM getLoggedInUser(String token) {
        User user = sessions.get(token);
        if (user == null) {
            throw new UserNotFoundException("User not logged in or token is invalid");
        }
        return userMapper.toUserVM(user, "User retrieved successfully");
    }



    public void logout(String token) {
        if (!sessions.containsKey(token)) {
            throw new UserNotFoundException("Invalid session token provided for logout");
        }
        sessions.remove(token);
    }




    public User getUserFromSession(String token) {
        User user = sessions.get(token);
        if (user == null) {
            throw new UserNotFoundException("User not logged in or token is invalid");
        }
        return user;
    }

    @Transactional
    public void testPermission(String token) {
        User user = getUserFromSession(token);
        if (!AuthUtils.hasPermission(user, "USER_CREATE")) {
            throw new UserNotFoundException("You do not have permission to perform this action.");
        }
    }



}
