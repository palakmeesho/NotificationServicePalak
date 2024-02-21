package com.example.firstmeeshoprojecyvohooo.util;

import com.example.firstmeeshoprojecyvohooo.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExternalService {
    private final String value = "93ceffda-5941-11ea-9da9-025282c394f2";
    private final String url = "https://api.imiconnect.in/resources/v1/messaging";
    public ResponseEntity<ExternalApiResponseDto> sendSms(GetSmsDetailsDto getSmsDetailsDto)
    {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("key",value);
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
            ResponseEntity<ExternalApiResponseDto> responseEntityStr = restTemplate.
                    postForEntity(url, request, ExternalApiResponseDto.class);
            log.info("response of external api is  " + responseEntityStr);
            return responseEntityStr;
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }
        return  null;
    }
}
