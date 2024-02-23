package com.example.firstmeeshoprojecyvohooo.service;

import com.example.firstmeeshoprojecyvohooo.dao.NotificationRepository;
import com.example.firstmeeshoprojecyvohooo.dao.RedisRepository;
import com.example.firstmeeshoprojecyvohooo.dto.*;
import com.example.firstmeeshoprojecyvohooo.model.RedisBlacklist;
import com.example.firstmeeshoprojecyvohooo.model.SmsRequest;
import com.example.firstmeeshoprojecyvohooo.util.CommonUtilities;
import com.example.firstmeeshoprojecyvohooo.util.ExternalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RedisRepository redisRepository;

    public ResponseEntity<SendSmsResponseDto> sendSms(SendSmsRequest sendSmsRequest)  {
        String generatedRequestId = new CommonUtilities().generateUniqueRequestId();
        SmsRequest smsRequest = SmsRequest.builder().phoneNumber(sendSmsRequest.getPhoneNumber()).message(sendSmsRequest.getMessage()).createdAt(new java.sql.Timestamp(System.currentTimeMillis())).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).requestId(generatedRequestId).build();
        notificationRepository.save(smsRequest);
        List<SmsRequest> listOfSmsRequest = notificationRepository.findAll().stream().filter(sms -> Objects.equals(sms.getRequestId(), generatedRequestId)).collect(Collectors.toList());
        if(!listOfSmsRequest.isEmpty())
        {
            sendToKafka(listOfSmsRequest.get(0).getRequestId());
            SendSmsDataDto sendSmsDataDto = SendSmsDataDto.builder().requestId(listOfSmsRequest.get(0).getRequestId()).comments("Request received").build();
            SendSmsResponseDto sendSmsResponseDto = SendSmsResponseDto.builder().sendSmsDataDto(sendSmsDataDto).build();
            return new ResponseEntity<>(sendSmsResponseDto, HttpStatus.CREATED);
        }
        else
        {
            SendSmsResponseDto sendSmsResponseDto = SendSmsResponseDto.builder().error("Resource Not Found").build();
            return new ResponseEntity<>(sendSmsResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    void sendToKafka(String requestId)
    {
        Optional<SmsRequest> request = notificationRepository.findAll().stream().filter(smsRequest -> Objects.equals(smsRequest.getRequestId(), requestId)).findFirst();
        if(request.isPresent())
        {
            Long phoneNumber = request.get().getPhoneNumber();
            if(phoneNumber == null)
            {
                SmsRequest smsRequest = SmsRequest.builder().id(request.get().getId()).failureCode("500").failureComments("Number does not exist").status("failure").createdAt(request.get().getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).requestId(requestId).build();
                notificationRepository.save(smsRequest);
            }
            else {
                //check phone number is blacklisted or not
                RedisBlacklist redisBlacklist = redisRepository.findByPhoneNumber(phoneNumber);
                if (redisBlacklist!=null && redisBlacklist.getStatus()) {
                    SmsRequest smsRequest = SmsRequest.builder().id(request.get().getId()).failureCode("").failureComments("Number is blacklisted").status("failure").phoneNumber(phoneNumber).message(request.get().getMessage()).createdAt(request.get().getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).requestId(requestId).build();
                    notificationRepository.save(smsRequest);
                } else {
                    //send data to external api
                    sendDataToExternalApi(GetSmsDetailsDto.builder().id(request.get().getId()).phoneNumber(request.get().getPhoneNumber()).message(request.get().getMessage()).createdAt(request.get().getCreatedAt()).requestId(request.get().getRequestId()).build());
                }
            }
        }
        else
        {
            SmsRequest smsRequest = SmsRequest.builder().requestId(requestId).failureCode("500").failureComments("No such request id present").status("failure").createdAt(new java.sql.Timestamp(System.currentTimeMillis())).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
            notificationRepository.save(smsRequest);
        }
    }

    public ResponseEntity<GetSmsDetailsResponseDto> getSmsDetails(String requestId)  {
        Optional<SmsRequest> request = notificationRepository.findAll().stream().filter(smsRequest -> Objects.equals(smsRequest.getRequestId(), requestId)).findFirst();
        if(request.isPresent())
        {
            GetSmsDetailsDto getSmsDetailsDto = GetSmsDetailsDto.builder().phoneNumber(request.get().getPhoneNumber()).id(request.get().getId()).message(request.get().getMessage()).createdAt(request.get().getCreatedAt()).updatedAt(request.get().getUpdatedAt()).status(request.get().getStatus()).failureCode(request.get().getFailureCode()).failureComments(request.get().getFailureComments()).requestId(requestId).build();
            GetSmsDetailsResponseDto getSmsDetailsResponseDto = GetSmsDetailsResponseDto.builder().getSmsDetailsDto(getSmsDetailsDto).build();
            return new ResponseEntity<>(getSmsDetailsResponseDto,HttpStatus.OK);
        }
        GetSmsDetailsResponseDto getSmsDetailsResponseDto = GetSmsDetailsResponseDto.builder().error("Resource not found").build();
        return new ResponseEntity<>(getSmsDetailsResponseDto,HttpStatus.BAD_REQUEST);
    }
    public void sendDataToExternalApi(GetSmsDetailsDto getSmsDetailsDto)
    {
        ExternalService externalService = new ExternalService();
        ResponseEntity<ExternalApiResponseDto> response =  externalService.sendSms(getSmsDetailsDto);
        if(response!=null && response.hasBody()) {
            if (Objects.equals(Objects.requireNonNull(response.getBody()).getListOfExternalApiResponse().get(0).getCode(), "1001")) {
                SmsRequest smsRequest = SmsRequest.builder().id(getSmsDetailsDto.getId()).status("success").phoneNumber(getSmsDetailsDto.getPhoneNumber()).message(getSmsDetailsDto.getMessage()).createdAt(getSmsDetailsDto.getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).requestId(getSmsDetailsDto.getRequestId()).build();
                notificationRepository.save(smsRequest);
            } else {
                if(!response.getBody().getListOfExternalApiResponse().isEmpty()) {
                    ExternalApiResponse externalApiResponse = response.getBody().getListOfExternalApiResponse().get(0);
                    SmsRequest smsRequest = SmsRequest.builder().id(getSmsDetailsDto.getId()).failureCode(externalApiResponse.getCode()).failureComments(externalApiResponse.getDescription()).status("failure").phoneNumber(getSmsDetailsDto.getPhoneNumber()).message(getSmsDetailsDto.getMessage()).createdAt(getSmsDetailsDto.getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).requestId(getSmsDetailsDto.getRequestId()).build();
                    notificationRepository.save(smsRequest);
                }
            }
        }
        else
        {
            SmsRequest smsRequest = SmsRequest.builder().id(getSmsDetailsDto.getId()).failureCode("500").failureComments("Recieved null response").status("failure").phoneNumber(getSmsDetailsDto.getPhoneNumber()).message(getSmsDetailsDto.getMessage()).createdAt(getSmsDetailsDto.getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).requestId(getSmsDetailsDto.getRequestId()).build();
            notificationRepository.save(smsRequest);
        }
    }
}
