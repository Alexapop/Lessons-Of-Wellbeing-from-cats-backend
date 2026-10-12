package dev.ioana.lessons_of_wellbeing.contracts;

import java.util.List;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;

public interface IClassService {
    List<ClassDTOResponse> getUpcomingClasses();
}
