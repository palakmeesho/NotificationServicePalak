package com.example.firstmeeshoprojecyvohooo.service;

import com.example.firstmeeshoprojecyvohooo.dao.BlackListRepository;
import com.example.firstmeeshoprojecyvohooo.dao.RedisRepository;
import com.example.firstmeeshoprojecyvohooo.dto.BlackListRequestDto;
import com.example.firstmeeshoprojecyvohooo.dto.BlackListResponseDto;
import com.example.firstmeeshoprojecyvohooo.dto.GetBlackListResponseDto;
import com.example.firstmeeshoprojecyvohooo.model.BlackList;
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

    public ResponseEntity<BlackListResponseDto> blacklistPhoneNumbers(BlackListRequestDto requestDto) {
       List<Long> alreadyExisting = new ArrayList<>();
       List<Long> wrongNumbers = new ArrayList<>();
        try{
            for(Long phoneNum: requestDto.getPhoneNumbers()) {
                if(phoneNum.toString().length() == 10) {
                    RedisBlacklist redisBlacklist = redisRepository.findByPhoneNumber(phoneNum);
                    if (redisBlacklist == null) {
                        blacklistPhoneNumbersInDb(phoneNum, "add");
                        blacklistPhoneNumbersInRedis(phoneNum);
                    } else if (!redisBlacklist.getStatus()) {
                        blacklistPhoneNumbersInDb(phoneNum, "update");
                        blacklistPhoneNumbersInRedis(phoneNum);
                    } else {
                        alreadyExisting.add(redisBlacklist.getPhoneNumber());
                    }
                }
                else
                {
                    wrongNumbers.add(phoneNum);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception is"+ e.getMessage()+" "+ Arrays.toString(e.getStackTrace()));
            BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().error("Failed Blacklisting").build();
            return new ResponseEntity<>(blackListResponseDto, HttpStatus.BAD_REQUEST);
        }
        String message = "Successfully blacklisted";
        if(!wrongNumbers.isEmpty())
        {
            message = message + wrongNumbers+" Invalid phone numbers ";
        }
        if(!alreadyExisting.isEmpty())
        {
            message = message + alreadyExisting+" Already existed";
        }
        BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().data(message).build();
        return new ResponseEntity<>(blackListResponseDto, HttpStatus.CREATED);
    }
    //save phone numbers to db
    public void blacklistPhoneNumbersInDb(Long phoneNumber,String task)
    {
            BlackList blackList;
            if (Objects.equals(task, "add")) {
                blackList = BlackList.builder().phoneNumber(phoneNumber).statusBlackList(true).build();
            }
            else
            {
                List<BlackList> entries = blackListRepository.findAll().stream().filter(blackList1 -> blackList1.getPhoneNumber().equals(phoneNumber)).collect(Collectors.toList());
                blackList = BlackList.builder().id(entries.get(0).getId()).statusBlackList(true).phoneNumber(entries.get(0).getPhoneNumber()).build();
            }
            blackListRepository.save(blackList);
            log.info("Data saved successfully to database"+phoneNumber);
    }
    //save phone numbers to redis
    public void blacklistPhoneNumbersInRedis(Long phoneNumber)
    {
            redisRepository.save(RedisBlacklist.builder().phoneNumber(phoneNumber).status(true).build());
            log.info("Data saved successfully to redis"+phoneNumber);
    }

    public ResponseEntity<BlackListResponseDto> whitelistPhoneNumbers(BlackListRequestDto requestDto) {
        List<Long> notExistingNumber = new ArrayList<>();
        try {
            for(Long phoneNum: requestDto.getPhoneNumbers()) {
                RedisBlacklist redisBlacklist = redisRepository.findByPhoneNumber(phoneNum);
                if (redisBlacklist == null) {
                   notExistingNumber.add(phoneNum);
                } else if (!redisBlacklist.getStatus()) {
                   whitelistPhoneNumbersInDb(phoneNum);
                   whitelistPhoneNumbersInRedis(phoneNum);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception is"+ e.getMessage()+" "+ Arrays.toString(e.getStackTrace()));
            BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().error("Failed whitelisting").build();
            return new ResponseEntity<>(blackListResponseDto, HttpStatus.BAD_REQUEST);
        }
        if(!notExistingNumber.isEmpty())
        {
            BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().data(notExistingNumber+"not presnt").build();
            return new ResponseEntity<>(blackListResponseDto, HttpStatus.CREATED);
        }
        BlackListResponseDto blackListResponseDto = BlackListResponseDto.builder().data("Successfully whitelisted").build();
        return new ResponseEntity<>(blackListResponseDto, HttpStatus.CREATED);
    }
    //delete phone numbers from db
    public void whitelistPhoneNumbersInDb(Long phoneNumber)
    {
        List<BlackList> listOfBlackList = blackListRepository.findAll().stream().filter(blackList -> Objects.equals(blackList.getPhoneNumber(), phoneNumber)).collect(Collectors.toList());
        BlackList blackList = BlackList.builder().id(listOfBlackList.get(0).getId()).phoneNumber(listOfBlackList.get(0).getPhoneNumber()).statusBlackList(false).build();
        blackListRepository.save(blackList);
        log.info("Data  successfully whitelisted to database"+phoneNumber);
    }
    //delete phone numbers from redis
    public void whitelistPhoneNumbersInRedis(Long phoneNumber)
    {
        //save to redis
        redisRepository.save(RedisBlacklist.builder().status(false).phoneNumber(phoneNumber).build());
        log.info("Data  successfully whitelisted to redis"+phoneNumber);
    }
    //get phone numbers from database
    public ResponseEntity<GetBlackListResponseDto> getBlackListedPhoneNumbers() {
        try {
            List<RedisBlacklist> listOfBlackList = redisRepository.findAll();
            GetBlackListResponseDto getBlackListResponseDto = GetBlackListResponseDto.builder().data(
                    listOfBlackList.stream().map(RedisBlacklist::getPhoneNumber).collect(Collectors.toList())
            ).build();
            log.info("Data successfully returned");
            return new ResponseEntity<>(getBlackListResponseDto, HttpStatus.OK);
        }
        catch(Exception e)
        {
            log.error("Exception is"+ e.getMessage()+" "+ Arrays.toString(e.getStackTrace()));
            GetBlackListResponseDto getBlackListResponseDto = GetBlackListResponseDto.builder().error("Invalid Request").build();
            return new ResponseEntity<>(getBlackListResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
}
