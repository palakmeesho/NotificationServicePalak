package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetSmsDetailsResponseDto {
    @JsonProperty(value = "data")
    GetSmsDetailsDto getSmsDetailsDto;
    @JsonProperty(value = "error")
    String error;
}
