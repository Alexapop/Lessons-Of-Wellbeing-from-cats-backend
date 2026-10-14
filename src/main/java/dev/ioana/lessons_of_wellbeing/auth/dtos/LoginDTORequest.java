package dev.ioana.lessons_of_wellbeing.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTORequest(
    @NotBlank @Email @Size(max = 255) String email,
    @NotBlank @Size(max = 72) String password
) {}
