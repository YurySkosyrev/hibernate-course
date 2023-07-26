package com.dmdev.listener;

import com.dmdev.entity.AuditableEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Instant;

public class AuditListener {

    @PrePersist
    public void prePersist(AuditableEntity<?> object) {
        object.setCreatedAt(Instant.now());
//        setCreatedBy(SecurityContext.getUser());
    }

    @PreUpdate
    public void preUpdate(AuditableEntity<?> object) {
        object.setUpdatedAt(Instant.now());
//        setUpdatedBy(SecurityContext.getUser());
    }

}
