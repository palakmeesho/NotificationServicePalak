package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSmsRequest {
    @NotNull(message = "phone number cannot be empty")
    @JsonProperty(value = "phoneNumber")
    Long phoneNumber;
    @JsonProperty(value = "message")
    String message;
}
