package com.delisdivin.service.impl;

import com.delisdivin.dto.*;
import com.delisdivin.entity.AppUser;
import com.delisdivin.entity.Restaurant;
import com.delisdivin.exception.BadRequestException;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.AppUserRepository;
import com.delisdivin.repository.RestaurantRepository;
import com.delisdivin.security.JwtUtils;
import com.delisdivin.security.UserDetailsImpl;
import com.delisdivin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AppMapper mapper;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        String jwt = jwtUtils.generateToken(userDetails);
        String refresh = jwtUtils.generateRefreshToken(userDetails);

        AppUser user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userDetails.getId()));

        return new AuthResponse(jwt, refresh, mapper.toDto(user));
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username " + req.getUsername() + " is already taken.");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email " + req.getEmail() + " is already registered.");
        }

        AppUser user = new AppUser();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());

        if (req.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(req.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + req.getRestaurantId()));
            user.setRestaurant(restaurant);
        }

        user.setActive(true);
        AppUser saved = userRepository.save(user);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRestaurant(Long restaurantId) {
        return userRepository.findByRestaurantId(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setActive(dto.isActive());
        
        if (dto.getRestaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + dto.getRestaurantId()));
            user.setRestaurant(restaurant);
        } else {
            user.setRestaurant(null);
        }

        AppUser updated = userRepository.save(user);
        return mapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }
}
