package dev.ioana.lessons_of_wellbeing.category;

import dev.ioana.lessons_of_wellbeing.enums.CategoryName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence. GenerationType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name = "categories")
@Getter 
@Setter 


public class CategoryEntity {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Setter (AccessLevel.NONE)
    @Column  (name = "id_category")
    private Long id;
    
    @Enumerated (EnumType.STRING)
    @Column
    private CategoryName name;

    
}
