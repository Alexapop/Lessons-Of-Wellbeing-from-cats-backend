package dev.ioana.lessons_of_wellbeing.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDTORequest(
    @NotBlank @Size(max = 255) String firstName,
    @NotBlank @Size(max = 255) String lastName,
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(max = 20)
    @Pattern(regexp = "\\+?[0-9\\s().-]+") String phoneNumber,
    @NotBlank @Size(max = 255) String address,
    @NotBlank @Size(min = 8, max = 72) String password
) {}
