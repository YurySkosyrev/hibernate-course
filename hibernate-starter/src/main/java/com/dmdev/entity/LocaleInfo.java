package com.dmdev.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class LocaleInfo {

    private String lang;
    private String description;

}
