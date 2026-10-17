package dev.ioana.lessons_of_wellbeing.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ioana.lessons_of_wellbeing.enums.Role;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    long countByRole(Role role);
}
