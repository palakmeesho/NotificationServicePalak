package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DestinationDto {
    @JsonProperty(value = "msisdn")
    List<Long> msisdn;
    @JsonProperty(value = "correlationId")
    Integer id;
}
