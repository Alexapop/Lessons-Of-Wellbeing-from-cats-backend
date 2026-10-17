package dev.ioana.lessons_of_wellbeing.booking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    boolean existsByUserIdAndClassEntityId(Long userId, Long classId);
}
