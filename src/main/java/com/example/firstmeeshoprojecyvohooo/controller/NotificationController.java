package com.example.firstmeeshoprojecyvohooo.controller;

import com.example.firstmeeshoprojecyvohooo.dto.SendSmsRequest;
import com.example.firstmeeshoprojecyvohooo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping("/v1/sms/send")
    public ResponseEntity<Object> sendSms(@RequestBody SendSmsRequest sendSmsRequest)
    {
       return notificationService.sendSms(sendSmsRequest);
    }
    @GetMapping("/v1/sms/{requestId}")
    public ResponseEntity<Object> getSmsDetails(@PathVariable Integer requestId)
    {
        return notificationService.getSmsDetails(requestId);
    }


}
