package com.shailu.deposito_dental_pos.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@MappedSuperclass
public class OnlyDatesBaseEntity {

    @CreationTimestamp
    @Column( name = "created_date", updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    @Column( name = "updated_date")
    private Timestamp updatedDate;

    @Column( name = "")
    @JsonIgnore
    private Timestamp deleted_date;


}
