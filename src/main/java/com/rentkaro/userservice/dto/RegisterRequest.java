package com.rentkaro.userservice.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 50, message = "First name must be between 3 to 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 50, message = "Last name must be between 3 to 50 characters")
        String lastName,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 to 50 characters")
        String userName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}
