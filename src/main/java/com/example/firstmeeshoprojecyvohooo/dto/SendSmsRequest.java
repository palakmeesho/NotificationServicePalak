package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSmsRequest {
    @NonNull()
    @NotEmpty(message = "phone number cannot be empty")
    @JsonProperty(value = "phoneNumber")
    Long phoneNumber;
    @JsonProperty(value = "message")
    String message;
}
