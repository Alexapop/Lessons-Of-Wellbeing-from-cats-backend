package dev.ioana.lessons_of_wellbeing.guide;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<GuideEntity, Long> {
}
