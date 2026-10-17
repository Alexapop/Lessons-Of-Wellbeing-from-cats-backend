package dev.ioana.lessons_of_wellbeing.classes;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.contracts.IClassService;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import dev.ioana.lessons_of_wellbeing.security.SecurityConfiguration;

@WebMvcTest(ClassController.class)
@Import(SecurityConfiguration.class)
class ClassControllerTest {
    @MockitoBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IClassService classService;

    @Test
    void getUpcomingClasses_returnsClassesWithMetadataWithoutLogin() throws Exception {
        ClassDTOResponse response = new ClassDTOResponse(
            7L,
            "Weekend Thor Playful Partner Yoga & Agility",
            "Partner and solo yoga poses.",
            ClassLevel.BEGINNERS,
            "Sunbeam Studio",
            LocalDateTime.of(2026, 10, 13, 7, 30),
            90,
            new ClassDTOResponse.CategoryResponse(1L, CategoryName.YOGA),
            new ClassDTOResponse.GuideResponse(3L, "Leo"),
            List.of(new ClassDTOResponse.HostResponse(2L, "Thor")),
            List.of("Thor's Spark Ginger Citrus Kombucha")
        );
        when(classService.getUpcomingClasses()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/sessions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id").value(7))
            .andExpect(jsonPath("$[0].title").value("Weekend Thor Playful Partner Yoga & Agility"))
            .andExpect(jsonPath("$[0].description").value("Partner and solo yoga poses."))
            .andExpect(jsonPath("$[0].level").value("BEGINNERS"))
            .andExpect(jsonPath("$[0].location").value("Sunbeam Studio"))
            .andExpect(jsonPath("$[0].startDateTime").value("2026-10-13T07:30:00"))
            .andExpect(jsonPath("$[0].durationMinutes").value(90))
            .andExpect(jsonPath("$[0].category.name").value("YOGA"))
            .andExpect(jsonPath("$[0].guide.name").value("Leo"))
            .andExpect(jsonPath("$[0].hosts[0].name").value("Thor"))
            .andExpect(jsonPath("$[0].drinks[0]").value("Thor's Spark Ginger Citrus Kombucha"));
    }

    @Test
    void getUpcomingClasses_returnsEmptyListWhenThereAreNoClasses() throws Exception {
        when(classService.getUpcomingClasses()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/sessions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createSession_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/v1/sessions"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getClassById_returnsTheClassForSignedInUsers() throws Exception {
        ClassDTOResponse response = new ClassDTOResponse(
            7L,
            "Weekend Thor Playful Partner Yoga & Agility",
            "Partner and solo yoga poses.",
            ClassLevel.BEGINNERS,
            "Sunbeam Studio",
            LocalDateTime.of(2026, 10, 13, 7, 30),
            90,
            new ClassDTOResponse.CategoryResponse(1L, CategoryName.YOGA),
            new ClassDTOResponse.GuideResponse(3L, "Leo"),
            List.of(new ClassDTOResponse.HostResponse(2L, "Thor")),
            List.of()
        );
        when(classService.getClassById(7L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/sessions/7").with(jwt()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.title").value("Weekend Thor Playful Partner Yoga & Agility"))
            .andExpect(jsonPath("$.startDateTime").value("2026-10-13T07:30:00"))
            .andExpect(jsonPath("$.guide.name").value("Leo"));
    }

    @Test
    void getClassById_isUnauthorizedWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/v1/sessions/7"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getClassById_returnsNotFoundForUnknownClass() throws Exception {
        when(classService.getClassById(99L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/v1/sessions/99").with(jwt()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
