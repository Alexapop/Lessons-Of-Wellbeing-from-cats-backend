package dev.ioana.lessons_of_wellbeing.admin.dtos;

import java.util.List;

import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;

// Values the admin can choose from when creating a session.
public record SessionOptionsDTOResponse(
    List<CategoryName> categories,
    List<ClassLevel> levels,
    List<OptionResponse> guides,
    List<OptionResponse> hosts
) {

    public record OptionResponse(
        Long id,
        String name
    ) {
    }
}
