package com.shailu.deposito_dental_pos.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_permissions")
public class UserPermission extends OnlyDatesBaseEntity {


    @Id
    @SequenceGenerator(name = "user_permissions_seq_gen", sequenceName = "user_permissions_seq_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_permissions_seq_gen")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
