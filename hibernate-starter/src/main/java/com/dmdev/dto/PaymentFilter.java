package com.dmdev.dto;

import lombok.Builder;
import lombok.Value;

import javax.persistence.Basic;

@Value
@Builder
public class PaymentFilter {
    String firstName;
    String lastName;
}
