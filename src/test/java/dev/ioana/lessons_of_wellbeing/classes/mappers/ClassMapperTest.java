package dev.ioana.lessons_of_wellbeing.classes.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import dev.ioana.lessons_of_wellbeing.category.CategoryEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import dev.ioana.lessons_of_wellbeing.guide.GuideEntity;
import dev.ioana.lessons_of_wellbeing.host.HostEntity;

class ClassMapperTest {
    private final ClassMapper classMapper = new ClassMapper();

    @Test
    void toResponse_mapsAllFieldsOfTheClass() {
        ClassEntity classEntity = createClass();

        ClassDTOResponse response = classMapper.toResponse(classEntity);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("Sunset Yin Yoga & Purr Sound Bath");
        assertThat(response.description()).isEqualTo("Long-held Yin stretches.");
        assertThat(response.level()).isEqualTo(ClassLevel.GENTLE_RESTORATIVE);
        assertThat(response.location()).isEqualTo("The Velvet Sun Lounge");
        assertThat(response.startDateTime()).isEqualTo(LocalDateTime.of(2026, 10, 12, 18, 30));
        assertThat(response.durationMinutes()).isEqualTo(75);
        assertThat(response.category()).isEqualTo(new ClassDTOResponse.CategoryResponse(1L, CategoryName.YOGA));
        assertThat(response.guide()).isEqualTo(new ClassDTOResponse.GuideResponse(5L, "Elena"));
    }

    @Test
    void toResponse_returnsHostsOrderedById() {
        ClassEntity classEntity = createClass();
        classEntity.getHosts().add(createHost(2L, "Thor"));
        classEntity.getHosts().add(createHost(1L, "Mia"));

        ClassDTOResponse response = classMapper.toResponse(classEntity);

        assertThat(response.hosts()).containsExactly(
            new ClassDTOResponse.HostResponse(1L, "Mia"),
            new ClassDTOResponse.HostResponse(2L, "Thor")
        );
    }

    @Test
    void toResponse_returnsEmptyHostListWhenClassHasNoHosts() {
        ClassDTOResponse response = classMapper.toResponse(createClass());

        assertThat(response.hosts()).isEmpty();
    }

    private ClassEntity createClass() {
        CategoryEntity category = new CategoryEntity();
        ReflectionTestUtils.setField(category, "id", 1L);
        category.setName(CategoryName.YOGA);

        GuideEntity guide = new GuideEntity();
        ReflectionTestUtils.setField(guide, "id", 5L);
        guide.setName("Elena");

        ClassEntity classEntity = new ClassEntity();
        ReflectionTestUtils.setField(classEntity, "id", 10L);
        classEntity.setTitle("Sunset Yin Yoga & Purr Sound Bath");
        classEntity.setDescription("Long-held Yin stretches.");
        classEntity.setLevel(ClassLevel.GENTLE_RESTORATIVE);
        classEntity.setLocation("The Velvet Sun Lounge");
        classEntity.setStartDateTime(LocalDateTime.of(2026, 10, 12, 18, 30));
        classEntity.setDurationMinutes(75);
        classEntity.setCategory(category);
        classEntity.setGuide(guide);
        return classEntity;
    }

    private HostEntity createHost(Long id, String name) {
        HostEntity host = new HostEntity();
        ReflectionTestUtils.setField(host, "id", id);
        host.setName(name);
        return host;
    }
}
