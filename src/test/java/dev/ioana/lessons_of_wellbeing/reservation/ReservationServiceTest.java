package dev.ioana.lessons_of_wellbeing.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.category.CategoryEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void validateReservation_rejectsKombuchaTeaSessionWithBlankNotes() {
        when(classRepository.findById(2L)).thenReturn(Optional.of(createClass(CategoryName.KOMBUCHA_TEA_LOUNGE)));

        assertThatThrownBy(() -> reservationService.validateReservation(2L, createRequest("   ")))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(exception -> {
                ResponseStatusException error = (ResponseStatusException) exception;
                assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(error.getReason()).isEqualTo(ReservationService.MISSING_NOTES_MESSAGE);
            });
    }

    @Test
    void validateReservation_rejectsKombuchaTeaSessionWithNullNotes() {
        when(classRepository.findById(2L)).thenReturn(Optional.of(createClass(CategoryName.KOMBUCHA_TEA_LOUNGE)));

        assertThatThrownBy(() -> reservationService.validateReservation(2L, createRequest(null)))
            .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void validateReservation_acceptsKombuchaTeaSessionWithNotes() {
        when(classRepository.findById(2L)).thenReturn(Optional.of(createClass(CategoryName.KOMBUCHA_TEA_LOUNGE)));

        ReservationDTOResponse response =
            reservationService.validateReservation(2L, createRequest("  Hibiscus Rose, no sugar  "));

        assertThat(response.sessionTitle()).isEqualTo("Session");
        assertThat(response.notes()).isEqualTo("Hibiscus Rose, no sugar");
    }

    @Test
    void validateReservation_acceptsOtherCategoriesWithoutNotes() {
        when(classRepository.findById(1L)).thenReturn(Optional.of(createClass(CategoryName.YOGA)));

        ReservationDTOResponse response = reservationService.validateReservation(1L, createRequest(null));

        assertThat(response.notes()).isEmpty();
    }

    @Test
    void validateReservation_throwsNotFoundWhenClassDoesNotExist() {
        when(classRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.validateReservation(99L, createRequest("Tea")))
            .isInstanceOf(ResponseStatusException.class)
            .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }

    private ClassEntity createClass(CategoryName categoryName) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryName);
        ClassEntity classEntity = new ClassEntity();
        classEntity.setTitle("Session");
        classEntity.setCategory(category);
        return classEntity;
    }

    private ReservationDTORequest createRequest(String notes) {
        return new ReservationDTORequest("Elena Vance", "elena@example.com", "+34 612 345 678", notes);
    }
}
