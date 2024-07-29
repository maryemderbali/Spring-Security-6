package com.ulysseprod.Entity;

import com.ulysseprod.DTO.RoleName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Integer id;

    @Enumerated(EnumType.STRING)
    @NaturalId

    @Column(length = 60)
    private RoleName name;


}
