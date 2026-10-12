package dev.ioana.lessons_of_wellbeing.classes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.mappers.ClassMapper;

@ExtendWith(MockitoExtension.class)
class ClassServiceTest {
    @Mock
    private ClassRepository classRepository;

    @Mock
    private ClassMapper classMapper;

    @InjectMocks
    private ClassService classService;

    @Test
    void getUpcomingClasses_returnsMappedClassesInRepositoryOrder() {
        ClassEntity firstClass = new ClassEntity();
        ClassEntity secondClass = new ClassEntity();
        ClassDTOResponse firstResponse = createResponse(1L);
        ClassDTOResponse secondResponse = createResponse(2L);

        when(classRepository.findByStartDateTimeAfter(any(LocalDateTime.class), any(Sort.class)))
            .thenReturn(List.of(firstClass, secondClass));
        when(classMapper.toResponse(firstClass)).thenReturn(firstResponse);
        when(classMapper.toResponse(secondClass)).thenReturn(secondResponse);

        List<ClassDTOResponse> result = classService.getUpcomingClasses();

        assertThat(result).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void getUpcomingClasses_searchesFromNowInMadridSortedByStartDateTime() {
        LocalDateTime before = LocalDateTime.now(ZoneId.of("Europe/Madrid"));
        ArgumentCaptor<LocalDateTime> nowCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        classService.getUpcomingClasses();

        LocalDateTime after = LocalDateTime.now(ZoneId.of("Europe/Madrid"));
        verify(classRepository).findByStartDateTimeAfter(nowCaptor.capture(), eq(Sort.by("startDateTime")));
        assertThat(nowCaptor.getValue()).isBetween(before, after);
    }

    @Test
    void getUpcomingClasses_returnsEmptyListWhenThereAreNoUpcomingClasses() {
        when(classRepository.findByStartDateTimeAfter(any(LocalDateTime.class), any(Sort.class)))
            .thenReturn(List.of());

        List<ClassDTOResponse> result = classService.getUpcomingClasses();

        assertThat(result).isEmpty();
        verify(classMapper, never()).toResponse(any());
    }

    private ClassDTOResponse createResponse(Long id) {
        return new ClassDTOResponse(id, "Title " + id, null, null, null, null, 60, null, null, List.of());
    }
}
