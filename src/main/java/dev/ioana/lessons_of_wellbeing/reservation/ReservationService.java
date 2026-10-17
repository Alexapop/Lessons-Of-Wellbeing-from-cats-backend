package dev.ioana.lessons_of_wellbeing.reservation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.contracts.IReservationService;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTORequest;
import dev.ioana.lessons_of_wellbeing.reservation.dtos.ReservationDTOResponse;

@Service
public class ReservationService implements IReservationService {
    public static final String MISSING_NOTES_MESSAGE =
        "Please tell us your tea or kombucha preferences.";

    private final ClassRepository classRepository;

    public ReservationService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDTOResponse validateReservation(Long classId, ReservationDTORequest request) {
        ClassEntity classEntity = classRepository.findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String notes = request.notes() == null ? "" : request.notes().trim();
        boolean isKombuchaTea = classEntity.getCategory().getName() == CategoryName.KOMBUCHA_TEA_LOUNGE;

        if (isKombuchaTea && notes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MISSING_NOTES_MESSAGE);
        }

        return new ReservationDTOResponse(
            classEntity.getId(),
            classEntity.getTitle(),
            request.name().trim(),
            request.email().trim(),
            request.phone().trim(),
            notes
        );
    }
}
