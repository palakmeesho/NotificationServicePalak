package com.example.firstmeeshoprojecyvohooo.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackList {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phone_number")
    @NonNull
    private Long phoneNumber;

    @Column(name = "status_blacklist")
    private Boolean statusBlackList;
}
