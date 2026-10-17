package dev.ioana.lessons_of_wellbeing.host;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRepository extends JpaRepository<HostEntity, Long> {
}
