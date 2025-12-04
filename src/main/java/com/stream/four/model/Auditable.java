package com.stream.four.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(force = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable
{
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Instant createdDate;

    @LastModifiedBy
    private String changedBy;

    @CreatedBy
    private String createdBy;
}