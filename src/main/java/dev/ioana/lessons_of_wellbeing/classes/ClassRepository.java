package dev.ioana.lessons_of_wellbeing.classes;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    @EntityGraph(attributePaths = { "category", "guide", "hosts" })
    List<ClassEntity> findByStartDateTimeAfter(LocalDateTime now, Sort sort);
}
