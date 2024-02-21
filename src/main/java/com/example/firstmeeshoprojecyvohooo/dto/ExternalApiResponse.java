package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalApiResponse{
    @JsonProperty(value = "code")
    String code;
    @JsonProperty(value = "transid")
    String transid;
    @JsonProperty(value = "description")
    String description;

}
