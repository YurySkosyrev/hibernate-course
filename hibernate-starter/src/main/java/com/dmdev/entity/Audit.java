package com.dmdev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Serializable entityId;

    private String entityName;

    //Здесь возможно использование JSON, чтобы легче передавать в BigQuery
    private String entityContent;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    public enum Operation {
        SAVE, DELETE, UPDATE, INSERT
    }
}
