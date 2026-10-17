package dev.ioana.lessons_of_wellbeing.user.dtos;

import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;

public record UserDTOResponse(
    Long id, String firstName, String lastName, String email, String phoneNumber, String address, Role role
) {
    public static UserDTOResponse from(UserEntity user) {
        return new UserDTOResponse(user.getId(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getPhoneNumber(), user.getAddress(), user.getRole());
    }
}
