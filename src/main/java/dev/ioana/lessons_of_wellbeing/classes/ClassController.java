package dev.ioana.lessons_of_wellbeing.classes;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.contracts.IClassService;

@RestController
@RequestMapping (path = "${api-endpoint}/sessions")
public class ClassController {
    private final IClassService classService;

        public ClassController (IClassService classService){
            this.classService = classService;
    }

    @GetMapping
    public List<ClassDTOResponse> getUpcomingClasses(){
        return classService.getUpcomingClasses();
    }
}
