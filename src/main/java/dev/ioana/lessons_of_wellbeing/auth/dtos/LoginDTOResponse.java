package dev.ioana.lessons_of_wellbeing.auth.dtos;

import dev.ioana.lessons_of_wellbeing.user.dtos.UserDTOResponse;

public record LoginDTOResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserDTOResponse user
) {}
