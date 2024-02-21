package com.example.firstmeeshoprojecyvohooo.controller;

import com.example.firstmeeshoprojecyvohooo.dto.SendSmsRequest;
import com.example.firstmeeshoprojecyvohooo.exception.ResourceNotFoundException;
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
    public ResponseEntity<Object> sendSms(@Valid @RequestBody SendSmsRequest sendSmsRequest) throws ResourceNotFoundException
    {
       return notificationService.sendSms(sendSmsRequest);
    }
    @GetMapping("/v1/sms/{requestId}")
    public ResponseEntity<Object> getSmsDetails(@Valid @NotEmpty(message = "Cannot be empty") @PathVariable String requestId) throws ResourceNotFoundException
    {
        return notificationService.getSmsDetails(requestId);
    }


}
