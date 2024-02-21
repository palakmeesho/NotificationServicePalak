package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSmsDetailsDto{
    @JsonProperty(value = "id")
    Integer id;
    @JsonProperty(value = "phoneNumber")
    Long phoneNumber;
    @JsonProperty(value = "status")
    String status;
    @JsonProperty(value = "message")
    String message;
    @JsonProperty(value = "created_at")
    Timestamp createdAt;
    @JsonProperty(value = "updated_at")
    Timestamp updatedAt;
    @JsonProperty(value = "failure_code")
    String failureCode;
    @JsonProperty(value = "failure_comments")
    String failureComments;
    @JsonProperty(value = "request_id")
    String requestId;


}
