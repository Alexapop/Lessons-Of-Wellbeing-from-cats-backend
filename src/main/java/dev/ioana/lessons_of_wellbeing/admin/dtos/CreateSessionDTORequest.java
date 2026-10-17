package dev.ioana.lessons_of_wellbeing.admin.dtos;

import java.time.LocalDateTime;
import java.util.List;

import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateSessionDTORequest(
    @NotBlank @Size(max = 255) String title,
    @NotBlank @Size(max = 500) String description,
    @NotNull CategoryName category,
    @NotNull ClassLevel level,
    @NotBlank @Size(max = 100) String location,
    // Only upcoming sessions can be created, so they appear in the calendar.
    @NotNull @Future LocalDateTime startDateTime,
    @Min(15) @Max(480) int durationMinutes,
    @NotNull @Positive Long guideId,
    @NotEmpty List<@NotNull @Positive Long> hostIds
) {}
