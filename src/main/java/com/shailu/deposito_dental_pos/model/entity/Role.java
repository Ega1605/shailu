package com.shailu.deposito_dental_pos.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role extends OnlyDatesBaseEntity{

    @Id
    @SequenceGenerator(name = "role_seq_gen", sequenceName = "role_seq_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq_gen")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "description_role")
    private String descriptionRole;

    @OneToMany(mappedBy = "role")
    private List<UserPermission> userPermissions;

}
