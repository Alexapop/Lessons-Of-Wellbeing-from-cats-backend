package dev.ioana.lessons_of_wellbeing.guide;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name ="guides")
@Getter 
@Setter 

public class GuideEntity {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column (name ="id_guide")
    private Long id;

    @Column (length = 50)
    private String name;
}
