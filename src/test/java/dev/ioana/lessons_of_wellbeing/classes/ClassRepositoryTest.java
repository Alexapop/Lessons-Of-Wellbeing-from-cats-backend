package dev.ioana.lessons_of_wellbeing.classes;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import dev.ioana.lessons_of_wellbeing.category.CategoryEntity;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import dev.ioana.lessons_of_wellbeing.guide.GuideEntity;
import dev.ioana.lessons_of_wellbeing.host.HostEntity;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ClassRepositoryTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 12, 12, 0);

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByStartDateTimeAfter_returnsOnlyUpcomingClassesSortedByStartDateTime() {
        CategoryEntity category = persistCategory();
        GuideEntity guide = persistGuide();
        HostEntity mia = persistHost("Mia");

        persistClass("Past class", NOW.minusDays(1), category, guide, mia);
        persistClass("Later class", NOW.plusDays(3), category, guide, mia);
        persistClass("Sooner class", NOW.plusHours(2), category, guide, mia);
        entityManager.flush();
        entityManager.clear();

        List<ClassEntity> result = classRepository.findByStartDateTimeAfter(NOW, Sort.by("startDateTime"));

        assertThat(result)
            .extracting(classEntity -> classEntity.getTitle())
            .containsExactly("Sooner class", "Later class");
    }

    @Test
    void findByStartDateTimeAfter_loadsCategoryGuideAndHosts() {
        CategoryEntity category = persistCategory();
        GuideEntity guide = persistGuide();
        HostEntity mia = persistHost("Mia");
        HostEntity thor = persistHost("Thor");

        persistClass("Tea tasting", NOW.plusDays(1), category, guide, mia, thor);
        entityManager.flush();
        entityManager.clear();

        List<ClassEntity> result = classRepository.findByStartDateTimeAfter(NOW, Sort.by("startDateTime"));

        ClassEntity classEntity = result.get(0);
        assertThat(classEntity.getCategory().getName()).isEqualTo(CategoryName.KOMBUCHA_TEA_LOUNGE);
        assertThat(classEntity.getGuide().getName()).isEqualTo("Master Tea Artisan");
        assertThat(classEntity.getHosts())
            .extracting(host -> host.getName())
            .containsExactlyInAnyOrder("Mia", "Thor");
    }

    @Test
    void findByStartDateTimeAfter_returnsEmptyListWhenAllClassesArePast() {
        persistClass("Past class", NOW.minusHours(1), persistCategory(), persistGuide(), persistHost("Mia"));

        List<ClassEntity> result = classRepository.findByStartDateTimeAfter(NOW, Sort.by("startDateTime"));

        assertThat(result).isEmpty();
    }

    private CategoryEntity persistCategory() {
        CategoryEntity category = new CategoryEntity();
        category.setName(CategoryName.KOMBUCHA_TEA_LOUNGE);
        return entityManager.persist(category);
    }

    private GuideEntity persistGuide() {
        GuideEntity guide = new GuideEntity();
        guide.setName("Master Tea Artisan");
        return entityManager.persist(guide);
    }

    private HostEntity persistHost(String name) {
        HostEntity host = new HostEntity();
        host.setName(name);
        return entityManager.persist(host);
    }

    private void persistClass(
        String title,
        LocalDateTime startDateTime,
        CategoryEntity category,
        GuideEntity guide,
        HostEntity... hosts
    ) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setTitle(title);
        classEntity.setDescription("Description of " + title);
        classEntity.setLevel(ClassLevel.ALL_LEVELS);
        classEntity.setLocation("The Garden Tea & Kombucha Lounge");
        classEntity.setStartDateTime(startDateTime);
        classEntity.setDurationMinutes(90);
        classEntity.setCategory(category);
        classEntity.setGuide(guide);
        classEntity.getHosts().addAll(List.of(hosts));
        entityManager.persist(classEntity);
    }
}
