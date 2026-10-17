package dev.ioana.lessons_of_wellbeing.booking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    boolean existsByUserIdAndClassEntityId(Long userId, Long classId);

    long countByStatus(ReservationStatus status);

    @EntityGraph(attributePaths = { "user", "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    List<BookingEntity> findAllByOrderByClassEntityStartDateTimeDesc();

    @EntityGraph(attributePaths = { "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    List<BookingEntity> findByUserIdOrderByClassEntityStartDateTimeDesc(Long userId);

    @EntityGraph(attributePaths = { "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    Optional<BookingEntity> findByIdAndUserId(Long id, Long userId);
}
