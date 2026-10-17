package dev.ioana.lessons_of_wellbeing.booking;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    boolean existsByUserIdAndClassEntityId(Long userId, Long classId);

    @EntityGraph(attributePaths = { "classEntity", "classEntity.category", "classEntity.guide", "classEntity.hosts" })
    List<BookingEntity> findByUserIdOrderByClassEntityStartDateTimeDesc(Long userId);
}
