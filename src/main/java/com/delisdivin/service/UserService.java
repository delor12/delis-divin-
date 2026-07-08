package com.delisdivin.service;

import com.delisdivin.dto.*;
import java.util.List;

public interface UserService {
    AuthResponse login(LoginRequest loginRequest);
    UserDTO register(RegisterRequest registerRequest);
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    List<UserDTO> getUsersByRestaurant(Long restaurantId);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
