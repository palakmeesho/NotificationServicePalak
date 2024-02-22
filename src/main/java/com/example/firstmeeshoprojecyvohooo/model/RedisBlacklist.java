package com.example.firstmeeshoprojecyvohooo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisBlacklist implements Serializable {
    private Long phoneNumber;
    private Boolean status;
}
