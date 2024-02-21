package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendSmsRequest {
    @JsonProperty(value = "phoneNumber")
    Long phoneNumber;
    @JsonProperty(value = "message")
    String message;
}
