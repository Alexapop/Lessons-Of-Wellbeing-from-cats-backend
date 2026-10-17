package dev.ioana.lessons_of_wellbeing.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.ioana.lessons_of_wellbeing.enums.CategoryName;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(CategoryName name);
}
