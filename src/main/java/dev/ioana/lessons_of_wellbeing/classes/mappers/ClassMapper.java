package dev.ioana.lessons_of_wellbeing.classes.mappers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import dev.ioana.lessons_of_wellbeing.category.CategoryEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.Drink;
import dev.ioana.lessons_of_wellbeing.guide.GuideEntity;
import dev.ioana.lessons_of_wellbeing.host.HostEntity;

@Component
public class ClassMapper {
    public ClassDTOResponse toResponse(ClassEntity classEntity) {
        CategoryEntity category = classEntity.getCategory();

        ClassDTOResponse.CategoryResponse categoryResponse =
            new ClassDTOResponse.CategoryResponse(
                category.getId(),
                category.getName()
            );

        GuideEntity guide = classEntity.getGuide();

        ClassDTOResponse.GuideResponse guideResponse =
            new ClassDTOResponse.GuideResponse(
                guide.getId(),
                guide.getName()
            );

        List<ClassDTOResponse.HostResponse> hostResponses = new ArrayList<>();

        for (HostEntity host : classEntity.getHosts()) {
            ClassDTOResponse.HostResponse hostResponse =
                new ClassDTOResponse.HostResponse(
                    host.getId(),
                    host.getName()
                );

            hostResponses.add(hostResponse);
        }

        hostResponses.sort((firstHost, secondHost) -> firstHost.id().compareTo(secondHost.id()));

        List<String> drinks = new ArrayList<>();

        if (category.getName() == CategoryName.KOMBUCHA_TEA_LOUNGE) {
            for (Drink drink : Drink.values()) {
                drinks.add(drink.getDisplayName());
            }
        }

        return new ClassDTOResponse(
            classEntity.getId(),
            classEntity.getTitle(),
            classEntity.getDescription(),
            classEntity.getLevel(),
            classEntity.getLocation(),
            classEntity.getStartDateTime(),
            classEntity.getDurationMinutes(),
            categoryResponse,
            guideResponse,
            hostResponses,
            drinks
        );
    }
}
