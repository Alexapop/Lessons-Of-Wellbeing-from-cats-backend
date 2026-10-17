package dev.ioana.lessons_of_wellbeing.booking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    boolean existsByUserIdAndClassEntityId(Long userId, Long classId);

    long countByStatus(ReservationStatus status);

    // Bulk status change for every booking of one session, e.g. CONFIRMED -> CANCELLED.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update BookingEntity b set b.status = :newStatus where b.classEntity.id = :classId and b.status = :currentStatus")
    int updateStatusForClass(
        @Param("classId") Long classId,
        @Param("currentStatus") ReservationStatus currentStatus,
        @Param("newStatus") ReservationStatus newStatus
    );

    @EntityGraph(attributePaths = { "user", "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    List<BookingEntity> findAllByOrderByClassEntityStartDateTimeDesc();

    @EntityGraph(attributePaths = { "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    List<BookingEntity> findByUserIdOrderByClassEntityStartDateTimeDesc(Long userId);

    @EntityGraph(attributePaths = { "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    Optional<BookingEntity> findByIdAndUserId(Long id, Long userId);
}
