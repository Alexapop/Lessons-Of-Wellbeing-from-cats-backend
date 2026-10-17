package dev.ioana.lessons_of_wellbeing.classes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.mappers.ClassMapper;
import dev.ioana.lessons_of_wellbeing.contracts.IClassService;

@Service
public class ClassService implements IClassService {
    private final ClassRepository classRepository;
    private final ClassMapper classMapper;

    public ClassService(
        ClassRepository classRepository,
        ClassMapper classMapper
    ) {
        this.classRepository = classRepository;
        this.classMapper = classMapper;
    }
    @Override
    @Transactional(readOnly = true)
    public List<ClassDTOResponse> getUpcomingClasses() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Madrid"));

        List<ClassEntity> classes = classRepository.findByStartDateTimeAfter(now, Sort.by("startDateTime"));
        List<ClassDTOResponse> responses = new ArrayList<>();

        for (ClassEntity classEntity : classes) {
            ClassDTOResponse response = classMapper.toResponse(classEntity);

            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public ClassDTOResponse getClassById(Long id) {
        ClassEntity classEntity = classRepository.findWithDetailsById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return classMapper.toResponse(classEntity);
    }
}
