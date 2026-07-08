package com.delisdivin.dto;

import com.delisdivin.entity.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private Long restaurantId;
    private String restaurantName;
    private boolean active;
}
