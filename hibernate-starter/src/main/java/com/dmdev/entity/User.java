package com.dmdev.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Users")
@TypeDef(name = "dmdev", typeClass = JsonBinaryType.class)
public class User {

    @Id
    private String username;

    @Embedded
    @AttributeOverride(name = "birthDate", column =  @Column(name = "birth_date"))
    private PersonalInfo personalInfo;

//    @Type(type = "com.vladmihalcea.hibernate.type.json.JsonBinaryType")
    @Type(type = "jsonb")
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;

}
