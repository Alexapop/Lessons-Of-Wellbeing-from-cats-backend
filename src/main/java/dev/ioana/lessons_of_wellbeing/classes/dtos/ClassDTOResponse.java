package dev.ioana.lessons_of_wellbeing.classes.dtos;

import java.time.LocalDateTime;
import java.util.List;

import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;

public record ClassDTOResponse(
    Long id,
    String title,
    String description,
    ClassLevel level,
    String location,
    LocalDateTime startDateTime,
    int durationMinutes,
    CategoryResponse category,
    GuideResponse guide,
    List <HostResponse> hosts,
    List <String> drinks
) {

    public record CategoryResponse(
        Long id,
        CategoryName name
    ) {
    }

    public record GuideResponse(
        Long id,
        String name
    ) {
    }

    public record HostResponse(
        Long id,
        String name
    ) {
    }
}
