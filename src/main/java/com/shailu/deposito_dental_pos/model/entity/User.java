package com.shailu.deposito_dental_pos.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends OnlyDatesBaseEntity {
    @Id
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

}
