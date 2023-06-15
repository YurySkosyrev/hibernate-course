package com.dmdev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity <T extends Serializable> implements BaseEntity<T>{

    private Instant created_at;

    private String created_by;
}
