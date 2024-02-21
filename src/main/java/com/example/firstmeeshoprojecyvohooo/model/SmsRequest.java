package com.example.firstmeeshoprojecyvohooo.model;

import com.example.firstmeeshoprojecyvohooo.util.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class SmsRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "phone_number")
    private Long phoneNumber;
    @Column(name = "message")
    private String message;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Column(name = "failure_code")
    private String failureCode;
    @Column(name = "failure_comment")
    private String failureComments;



}
