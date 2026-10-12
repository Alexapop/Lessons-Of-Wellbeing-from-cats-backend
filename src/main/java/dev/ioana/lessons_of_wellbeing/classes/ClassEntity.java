package dev.ioana.lessons_of_wellbeing.classes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import dev.ioana.lessons_of_wellbeing.category.CategoryEntity;
import dev.ioana.lessons_of_wellbeing.enums.ClassLevel;
import dev.ioana.lessons_of_wellbeing.guide.GuideEntity;
import dev.ioana.lessons_of_wellbeing.host.HostEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "classes")
@Getter
@Setter
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "id_class")
    private Long id;

    @Column(length = 255)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private ClassLevel level;

    @Column(length = 100)
    private String location;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @ManyToOne (optional = false)
    @JoinColumn (name = "category_id",nullable = false)
    private CategoryEntity category;

    @ManyToOne (optional = false)
    @JoinColumn (name = "guide_id", nullable = false)
    private GuideEntity guide;

  @ManyToMany
  @JoinTable (
    name = "class_host",
    joinColumns = @JoinColumn (name = "class_id"),
    inverseJoinColumns = @JoinColumn (name = "host_id")
  )
  private Set <HostEntity> hosts = new HashSet<>();

}
