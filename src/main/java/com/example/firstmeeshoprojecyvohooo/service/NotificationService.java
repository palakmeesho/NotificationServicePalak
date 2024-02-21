package com.example.firstmeeshoprojecyvohooo.service;

import com.example.firstmeeshoprojecyvohooo.dao.BlackListRepository;
import com.example.firstmeeshoprojecyvohooo.dao.NotificationRepository;
import com.example.firstmeeshoprojecyvohooo.dto.*;
import com.example.firstmeeshoprojecyvohooo.model.BlackList;
import com.example.firstmeeshoprojecyvohooo.model.SmsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BlackListRepository blackListRepository;

    public ResponseEntity<Object> sendSms(SendSmsRequest sendSmsRequest) {
        if(sendSmsRequest.getPhoneNumber()==null)
        {
            NotificationErrorDto notificationErrorDto = NotificationErrorDto.builder().code("Invalid Request").message("Phone number is mandatory").build();
            NotificationErrorResponseDto notificationErrorResponseDto = NotificationErrorResponseDto.builder().notificationErrorDto(notificationErrorDto).build();
            return new ResponseEntity<>(notificationErrorResponseDto, HttpStatus.BAD_REQUEST);
        }
        SmsRequest smsRequest = SmsRequest.builder().phoneNumber(sendSmsRequest.getPhoneNumber()).message(sendSmsRequest.getMessage()).createdAt(new java.sql.Timestamp(System.currentTimeMillis())).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
        notificationRepository.save(smsRequest);
        List<SmsRequest> listOfSmsRequest = notificationRepository.findAll().stream().filter(sms -> Objects.equals(sms.getPhoneNumber(), sendSmsRequest.getPhoneNumber()) && (Objects.equals(sms.getMessage(), smsRequest.getMessage()))).collect(Collectors.toList());
        if(!listOfSmsRequest.isEmpty())
        {
            sendToKafka(listOfSmsRequest.get(0).getId());
            SendSmsDataDto sendSmsDataDto = SendSmsDataDto.builder().requestId(listOfSmsRequest.get(0).getId()).comments("Successfully sent").build();
            SendSmsResponseDto sendSmsResponseDto = SendSmsResponseDto.builder().sendSmsDataDto(sendSmsDataDto).build();
            return new ResponseEntity<>(sendSmsResponseDto, HttpStatus.CREATED);
        }
        NotificationErrorDto notificationErrorDto = NotificationErrorDto.builder().code("Invalid Request").message("Something is wrong").build();
        NotificationErrorResponseDto notificationErrorResponseDto = NotificationErrorResponseDto.builder().notificationErrorDto(notificationErrorDto).build();
        return new ResponseEntity<>(notificationErrorResponseDto, HttpStatus.BAD_REQUEST);
    }
    void sendToKafka(Integer requestId)
    {
        Optional<SmsRequest> request = notificationRepository.findById(requestId);
        if(request.isPresent())
        {
            SmsRequest smsRequest;
            Long phoneNumber = request.get().getPhoneNumber();
            if(phoneNumber == null)
            {
                smsRequest = SmsRequest.builder().id(requestId).failureCode("Invalid Request").failureComments("Number does not exist").status("failure").createdAt(request.get().getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
                notificationRepository.save(smsRequest);
            }
            else {
                List<BlackList> listOfBlackList = blackListRepository.findAll().stream().filter(blackList -> blackList.getPhoneNumber().equals(phoneNumber)).collect(Collectors.toList());
                if (!listOfBlackList.isEmpty() && listOfBlackList.get(0).getStatusBlackList()) {
                    smsRequest = SmsRequest.builder().id(requestId).failureCode("Invalid Request").failureComments("Number is blacklisted").status("failure").phoneNumber(phoneNumber).message(request.get().getMessage()).createdAt(request.get().getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
                } else {
                    //send data to external api
                    sendDatToExternalApi(GetSmsDetailsDto.builder().id(request.get().getId()).phoneNumber(request.get().getPhoneNumber()).message(request.get().getMessage()).build());
                    smsRequest = SmsRequest.builder().id(requestId).status("success").phoneNumber(phoneNumber).message(request.get().getMessage()).createdAt(request.get().getCreatedAt()).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
                }
                notificationRepository.save(smsRequest);
            }
        }
        else
        {
            SmsRequest smsRequest = SmsRequest.builder().id(requestId).failureCode("Invalid Request").failureComments("No such request id present").status("failure").createdAt(new java.sql.Timestamp(System.currentTimeMillis())).updatedAt(new java.sql.Timestamp(System.currentTimeMillis())).build();
            notificationRepository.save(smsRequest);
        }
    }

    public ResponseEntity<Object> getSmsDetails(Integer requestId) {
        Optional<SmsRequest> request = notificationRepository.findById(requestId);
        if(request.isPresent())
        {
            GetSmsDetailsDto getSmsDetailsDto = GetSmsDetailsDto.builder().phoneNumber(request.get().getPhoneNumber()).id(requestId).message(request.get().getMessage()).createdAt(request.get().getCreatedAt()).updatedAt(request.get().getUpdatedAt()).status(request.get().getStatus()).failureCode(request.get().getFailureCode()).failureComments(request.get().getFailureComments()).build();
            GetSmsDetailsResponseDto getSmsDetailsResponseDto = GetSmsDetailsResponseDto.builder().getSmsDetailsDto(getSmsDetailsDto).build();
            return new ResponseEntity<>(getSmsDetailsResponseDto,HttpStatus.OK);
        }
        NotificationErrorDto notificationErrorDto = NotificationErrorDto.builder().code("Invalid Request").message("No such request id exist").build();
        NotificationErrorResponseDto notificationErrorResponseDto = NotificationErrorResponseDto.builder().notificationErrorDto(notificationErrorDto).build();
        return new ResponseEntity<>(notificationErrorResponseDto, HttpStatus.BAD_REQUEST);
    }
    public void sendDatToExternalApi(GetSmsDetailsDto getSmsDetailsDto)
    {
       RestTemplate restTemplate = new RestTemplate();
       HttpHeaders headers = new HttpHeaders();
       headers.add("key","93ceffda-5941-11ea-9da9-025282c394f2");
       headers.setContentType(MediaType.APPLICATION_JSON);
       List<DestinationDto> listOfDestinationDto = new ArrayList<>();
       List<Long> msisdn = new ArrayList<>();
       msisdn.add(getSmsDetailsDto.getPhoneNumber());
       listOfDestinationDto.add(DestinationDto.builder().msisdn(msisdn).id(getSmsDetailsDto.getId()).build());
       MessageRequestDto messageRequestDto = MessageRequestDto.builder().deliveryChannel("sms").channelDto(ChannelDto.builder().sms(Sms.builder().text(getSmsDetailsDto.getMessage()).build()).build()).destinationDtoList(listOfDestinationDto).build();
       List<MessageRequestDto> listOfMessageRequestDto = new ArrayList<>();
       listOfMessageRequestDto.add(messageRequestDto);
        HttpEntity<List<MessageRequestDto>> request =
                new HttpEntity<>(listOfMessageRequestDto, headers);
        log.info("request is "+request);
        try {
            ResponseEntity<String> responseEntityStr = restTemplate.
                    postForEntity("https://api.imiconnect.in/resources/v1/messaging", request, String.class);
            log.info("response of external api is  " + responseEntityStr);
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }
    }
}
