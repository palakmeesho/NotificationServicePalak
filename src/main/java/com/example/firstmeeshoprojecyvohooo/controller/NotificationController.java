package com.example.firstmeeshoprojecyvohooo.controller;

import com.example.firstmeeshoprojecyvohooo.dto.GetSmsDetailsResponseDto;
import com.example.firstmeeshoprojecyvohooo.dto.SendSmsRequest;
import com.example.firstmeeshoprojecyvohooo.dto.SendSmsResponseDto;
import com.example.firstmeeshoprojecyvohooo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@Validated
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/v1/sms/send")
    public ResponseEntity<SendSmsResponseDto> sendSms(@Valid @RequestBody SendSmsRequest sendSmsRequest)
    {
       return notificationService.sendSms(sendSmsRequest);
    }
    @GetMapping("/v1/sms/{requestId}")
    public ResponseEntity<GetSmsDetailsResponseDto> getSmsDetails(@Valid @NotEmpty(message = "Cannot be empty") @PathVariable String requestId)
    {
        return notificationService.getSmsDetails(requestId);
    }


}
