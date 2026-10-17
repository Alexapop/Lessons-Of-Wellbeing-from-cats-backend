package dev.ioana.lessons_of_wellbeing.classes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Deleted sessions stay in the table for the reservation history, so these queries skip them.
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    @EntityGraph(attributePaths = { "category", "guide", "hosts" })
    @Query("select c from ClassEntity c where c.deleted = false and c.startDateTime > :now")
    List<ClassEntity> findByStartDateTimeAfter(@Param("now") LocalDateTime now, Sort sort);

    @Query("select count(c) from ClassEntity c where c.deleted = false and c.startDateTime > :now")
    long countByStartDateTimeAfter(@Param("now") LocalDateTime now);

    @Query("select count(c) from ClassEntity c where c.deleted = false")
    long countActive();

    @EntityGraph(attributePaths = { "category", "guide", "hosts" })
    @Query("select c from ClassEntity c where c.id = :id and c.deleted = false")
    Optional<ClassEntity> findWithDetailsById(@Param("id") Long id);
}
