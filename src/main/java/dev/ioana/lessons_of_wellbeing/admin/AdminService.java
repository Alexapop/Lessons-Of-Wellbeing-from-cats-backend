package dev.ioana.lessons_of_wellbeing.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminBookingDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.AdminDashboardDTOResponse;
import dev.ioana.lessons_of_wellbeing.admin.dtos.CreateSessionDTORequest;
import dev.ioana.lessons_of_wellbeing.admin.dtos.SessionOptionsDTOResponse;
import dev.ioana.lessons_of_wellbeing.booking.BookingEntity;
import dev.ioana.lessons_of_wellbeing.booking.BookingRepository;
import dev.ioana.lessons_of_wellbeing.category.CategoryEntity;
import dev.ioana.lessons_of_wellbeing.category.CategoryRepository;
import dev.ioana.lessons_of_wellbeing.classes.ClassEntity;
import dev.ioana.lessons_of_wellbeing.classes.ClassRepository;
import dev.ioana.lessons_of_wellbeing.classes.dtos.ClassDTOResponse;
import dev.ioana.lessons_of_wellbeing.classes.mappers.ClassMapper;
import dev.ioana.lessons_of_wellbeing.contracts.IAdminService;
import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import dev.ioana.lessons_of_wellbeing.enums.ReservationStatus;
import dev.ioana.lessons_of_wellbeing.enums.Role;
import dev.ioana.lessons_of_wellbeing.guide.GuideEntity;
import dev.ioana.lessons_of_wellbeing.guide.GuideRepository;
import dev.ioana.lessons_of_wellbeing.host.HostEntity;
import dev.ioana.lessons_of_wellbeing.host.HostRepository;
import dev.ioana.lessons_of_wellbeing.user.UserEntity;
import dev.ioana.lessons_of_wellbeing.user.UserRepository;

@Service
public class AdminService implements IAdminService {
    public static final String INVALID_GUIDE_MESSAGE = "Please choose a valid guide.";
    public static final String INVALID_HOSTS_MESSAGE = "Please choose valid hosts.";
    public static final String INVALID_CATEGORY_MESSAGE = "Please choose a valid category.";

    private final ClassRepository classRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ClassMapper classMapper;
    private final CategoryRepository categoryRepository;
    private final GuideRepository guideRepository;
    private final HostRepository hostRepository;

    public AdminService(
        ClassRepository classRepository,
        BookingRepository bookingRepository,
        UserRepository userRepository,
        ClassMapper classMapper,
        CategoryRepository categoryRepository,
        GuideRepository guideRepository,
        HostRepository hostRepository
    ) {
        this.classRepository = classRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.classMapper = classMapper;
        this.categoryRepository = categoryRepository;
        this.guideRepository = guideRepository;
        this.hostRepository = hostRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardDTOResponse getDashboard() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Madrid"));

        return new AdminDashboardDTOResponse(
            classRepository.count(),
            classRepository.countByStartDateTimeAfter(now),
            bookingRepository.countByStatus(ReservationStatus.CONFIRMED),
            bookingRepository.countByStatus(ReservationStatus.CANCELLED),
            userRepository.countByRole(Role.USER)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminBookingDTOResponse> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAllByOrderByClassEntityStartDateTimeDesc();
        List<AdminBookingDTOResponse> responses = new ArrayList<>();

        for (BookingEntity booking : bookings) {
            UserEntity user = booking.getUser();

            // Only the contact data the admin needs, never the password hash.
            AdminBookingDTOResponse.BookingUserResponse userResponse =
                new AdminBookingDTOResponse.BookingUserResponse(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhoneNumber()
                );

            responses.add(new AdminBookingDTOResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getNotes(),
                booking.getCreatedAt(),
                userResponse,
                classMapper.toResponse(booking.getClassEntity())
            ));
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public SessionOptionsDTOResponse getSessionOptions() {
        List<SessionOptionsDTOResponse.OptionResponse> guides = new ArrayList<>();
        for (GuideEntity guide : guideRepository.findAll(Sort.by("name"))) {
            guides.add(new SessionOptionsDTOResponse.OptionResponse(guide.getId(), guide.getName()));
        }

        List<SessionOptionsDTOResponse.OptionResponse> hosts = new ArrayList<>();
        for (HostEntity host : hostRepository.findAll(Sort.by("id"))) {
            hosts.add(new SessionOptionsDTOResponse.OptionResponse(host.getId(), host.getName()));
        }

        return new SessionOptionsDTOResponse(
            Arrays.asList(CategoryName.values()),
            Arrays.asList(ClassLevel.values()),
            guides,
            hosts
        );
    }

    @Override
    @Transactional
    public ClassDTOResponse createSession(CreateSessionDTORequest request) {
        CategoryEntity category = categoryRepository.findByName(request.category())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_CATEGORY_MESSAGE));

        GuideEntity guide = guideRepository.findById(request.guideId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_GUIDE_MESSAGE));

        Set<Long> hostIds = new HashSet<>(request.hostIds());
        List<HostEntity> hosts = hostRepository.findAllById(hostIds);
        if (hosts.size() != hostIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_HOSTS_MESSAGE);
        }

        ClassEntity classEntity = new ClassEntity();
        classEntity.setTitle(request.title().trim());
        classEntity.setDescription(request.description().trim());
        classEntity.setCategory(category);
        classEntity.setLevel(request.level());
        classEntity.setLocation(request.location().trim());
        classEntity.setStartDateTime(request.startDateTime().withSecond(0).withNano(0));
        classEntity.setDurationMinutes(request.durationMinutes());
        classEntity.setGuide(guide);
        classEntity.setHosts(new HashSet<>(hosts));

        return classMapper.toResponse(classRepository.save(classEntity));
    }
}
