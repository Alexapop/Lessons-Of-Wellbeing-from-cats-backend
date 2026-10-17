package dev.ioana.lessons_of_wellbeing.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
    "jwt.secret=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY=",
    "demo.user.password=",
    "spring.datasource.url=jdbc:h2:mem:booking-tests"
})
@AutoConfigureMockMvc
@Transactional
class BookingIntegrationTest {
    // Seeded in data.sql: 1 = Yoga class, 2 = Kombucha Tea Tasting.
    private static final long YOGA_CLASS_ID = 1L;
    private static final long KOMBUCHA_CLASS_ID = 2L;

    @Autowired private MockMvc mvc;
    @Autowired private UserRepository users;
    @Autowired private BookingRepository bookings;
    @Autowired private PasswordEncoder encoder;
    @Autowired private ObjectMapper mapper;
    private UserEntity user;
    private String token;

    @BeforeEach
    void setUp() throws Exception {
        user = new UserEntity();
        user.setFirstName("Elena");
        user.setLastName("Vance");
        user.setEmail("booking-user@example.com");
        user.setPhoneNumber("+34 612 345 678");
        user.setPassword(encoder.encode("TestPassword123!"));
        user = users.save(user);

        String body = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"booking-user@example.com\",\"password\":\"TestPassword123!\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        token = mapper.readTree(body).get("accessToken").asString();
    }

    private String booking(long sessionId, String notes) {
        return "{\"sessionId\":" + sessionId + ",\"name\":\"Elena Vance\",\"email\":\"booking-user@example.com\","
            + "\"phone\":\"+34 612 345 678\",\"notes\":\"" + notes + "\"}";
    }

    @Test
    void createBooking_savesBookingAssociatedWithSignedInUser() throws Exception {
        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(KOMBUCHA_CLASS_ID, "Hibiscus Rose")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.sessionId").value(KOMBUCHA_CLASS_ID))
            .andExpect(jsonPath("$.status").value("CONFIRMED"))
            .andExpect(jsonPath("$.notes").value("Hibiscus Rose"));

        List<BookingEntity> saved = bookings.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.get(0).getClassEntity().getId()).isEqualTo(KOMBUCHA_CLASS_ID);
        assertThat(saved.get(0).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void createBooking_rejectsBookingTheSameClassTwice() throws Exception {
        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(YOGA_CLASS_ID, "")))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(YOGA_CLASS_ID, "")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(BookingService.ALREADY_BOOKED_MESSAGE));

        assertThat(bookings.findAll()).hasSize(1);
    }

    @Test
    void getMyBookings_returnsOnlyTheBookingsOfTheSignedInUser() throws Exception {
        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(KOMBUCHA_CLASS_ID, "ginger")))
            .andExpect(status().isCreated());

        UserEntity otherUser = new UserEntity();
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setEmail("other-user@example.com");
        otherUser.setPassword(encoder.encode("TestPassword123!"));
        otherUser = users.save(otherUser);
        BookingEntity otherBooking = new BookingEntity();
        otherBooking.setUser(otherUser);
        otherBooking.setClassEntity(bookings.findAll().get(0).getClassEntity());
        otherBooking.setCreatedAt(LocalDateTime.now());
        bookings.save(otherBooking);

        mvc.perform(get("/api/v1/bookings/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
            .andExpect(jsonPath("$[0].notes").value("ginger"))
            .andExpect(jsonPath("$[0].session.id").value(KOMBUCHA_CLASS_ID))
            .andExpect(jsonPath("$[0].session.category.name").value("KOMBUCHA_TEA_LOUNGE"));
    }

    @Test
    void createBooking_allowsEmptyNotesForNonKombuchaSessions() throws Exception {
        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(YOGA_CLASS_ID, "")))
            .andExpect(status().isCreated());

        assertThat(bookings.findAll()).hasSize(1);
    }

    @Test
    void createBooking_doesNotSaveKombuchaBookingWithoutNotes() throws Exception {
        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(KOMBUCHA_CLASS_ID, " ")))
            .andExpect(status().isBadRequest());

        assertThat(bookings.findAll()).isEmpty();
    }

    @Test
    void createBooking_returnsNotFoundForUnknownSession() throws Exception {
        mvc.perform(post("/api/v1/bookings").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(booking(999L, "Tea")))
            .andExpect(status().isNotFound());

        assertThat(bookings.findAll()).isEmpty();
    }
}
