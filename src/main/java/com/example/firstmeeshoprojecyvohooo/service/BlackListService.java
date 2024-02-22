package com.example.firstmeeshoprojecyvohooo.service;

import com.example.firstmeeshoprojecyvohooo.dao.BlackListRepository;
import com.example.firstmeeshoprojecyvohooo.dao.RedisRepository;
import com.example.firstmeeshoprojecyvohooo.dto.BlackListResponseDto;
import com.example.firstmeeshoprojecyvohooo.dto.ErrorResponseDto;
import com.example.firstmeeshoprojecyvohooo.dto.GetBlackListResponseDto;
import com.example.firstmeeshoprojecyvohooo.model.BlackList;
import com.example.firstmeeshoprojecyvohooo.dto.BlackListRequestDto;
import com.example.firstmeeshoprojecyvohooo.model.RedisBlacklist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlackListService {
    @Autowired
    BlackListRepository blackListRepository;
    @Autowired
    RedisRepository redisRepository;

    public ResponseEntity<Object> blacklistPhoneNumbers(BlackListRequestDto requestDto) {
        try{
            blacklistPhoneNumbersInDb(requestDto);
            blacklistPhoneNumbersInRedis(requestDto);
        }
        catch (Exception e)
        {
            log.error("Exception is"+ e.getMessage()+" "+ Arrays.toString(e.getStackTrace()));
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message("Failed Blacklisting").build();
            return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
        }
        BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().data("Successfully blacklisted").build();
        return new ResponseEntity<>(blackListResponseDto, HttpStatus.CREATED);
    }
    //save phone numbers to db
    public void blacklistPhoneNumbersInDb(BlackListRequestDto requestDto)
    {
        for(Long phoneNum: requestDto.getPhoneNumbers()) {
            List<BlackList> entries = blackListRepository.findAll().stream().filter(blackList1 -> blackList1.getPhoneNumber().equals(phoneNum)).collect(Collectors.toList());
            BlackList blackList;
            if (entries.isEmpty()) {
                blackList = BlackList.builder().phoneNumber(phoneNum).statusBlackList(true).build();
            }
            else
            {
                blackList = BlackList.builder().id(entries.get(0).getId()).statusBlackList(true).phoneNumber(entries.get(0).getPhoneNumber()).build();
            }
            blackListRepository.save(blackList);
        }
        log.info("Data saved successfully to database");
    }
    //save phone numbers to redis
    public void blacklistPhoneNumbersInRedis(BlackListRequestDto requestDto)
    {
        for(Long phoneNum: requestDto.getPhoneNumbers()) {
            RedisBlacklist redisBlacklist = redisRepository.findByPhoneNumber(phoneNum);
            if(redisBlacklist == null)
            {
                redisRepository.save(RedisBlacklist.builder().status(true).phoneNumber(phoneNum).build());
            }
            else
            {
                redisRepository.save(RedisBlacklist.builder().phoneNumber(phoneNum).status(true).build());
            }
            log.info("redis blacklist "+redisBlacklist);
        }
        log.info("Data saved successfully to redis"+redisRepository.findAll());
    }

    public ResponseEntity<Object> whitelistPhoneNumbers(BlackListRequestDto requestDto) {
        try {
            Object object = whitelistPhoneNumbersInDb(requestDto);
            if (object != null) {
                return new ResponseEntity<>(object, HttpStatus.CREATED);
            }
            whitelistPhoneNumbersInRedis(requestDto);
        }
        catch (Exception e)
        {
            log.error("Exception is"+ e.getMessage()+" "+ Arrays.toString(e.getStackTrace()));
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message("Failed whitelisting").build();
            return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
        }
        BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().data("Successfully whitelisted").build();
        return new ResponseEntity<>(blackListResponseDto, HttpStatus.CREATED);
    }
    //delete phone numbers from db
    public Object whitelistPhoneNumbersInDb(BlackListRequestDto requestDto)
    {
        List<Long> phoneNumberNotPresent = new ArrayList<>();
        for(Long phoneNum: requestDto.getPhoneNumbers()) {
            List<BlackList> listOfBlackList = blackListRepository.findAll().stream().filter(blackList -> Objects.equals(blackList.getPhoneNumber(), phoneNum)).collect(Collectors.toList());
            if (listOfBlackList.isEmpty()) {
                phoneNumberNotPresent.add(phoneNum);
            }
            else
            {
                BlackList blackList = BlackList.builder().id(listOfBlackList.get(0).getId()).phoneNumber(listOfBlackList.get(0).getPhoneNumber()).statusBlackList(false).build();
                blackListRepository.save(blackList);
            }
        }
        if(!phoneNumberNotPresent.isEmpty())
        {
            return ErrorResponseDto.builder().message(phoneNumberNotPresent+" not present").build();
        }
        log.info("Data deleted successfully to database");
        return null;
    }
    //delete phone numbers from redis
    public void whitelistPhoneNumbersInRedis(BlackListRequestDto requestDto)
    {
        //save to redis
        for(Long phoneNum: requestDto.getPhoneNumbers()) {
            RedisBlacklist redisBlacklist = redisRepository.findByPhoneNumber(phoneNum);
            if(redisBlacklist != null)
            {
                redisRepository.save(RedisBlacklist.builder().status(false).phoneNumber(phoneNum).build());
            }
            log.info("redis blacklist "+redisBlacklist);
        }
        log.info("Data deleted whitelisted successfully from redis");
    }
    //get phone numbers from database
    public ResponseEntity<Object> getBlackListedPhoneNumbers() {
        try {
            List<BlackList> listOfBlackList = blackListRepository.findAll();
            GetBlackListResponseDto getBlackListResponseDto = GetBlackListResponseDto.builder().data(
                    listOfBlackList.stream().map(BlackList::getPhoneNumber).collect(Collectors.toList())
            ).build();
            log.info("Data successfully returned");
            return new ResponseEntity<>(getBlackListResponseDto, HttpStatus.OK);
        }
        catch(Exception e)
        {
            log.error("Exception is"+ e.getMessage()+" "+ Arrays.toString(e.getStackTrace()));
            ErrorResponseDto errorResponseDto = ErrorResponseDto.builder().message("Invalid Request").build();
            return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
}
