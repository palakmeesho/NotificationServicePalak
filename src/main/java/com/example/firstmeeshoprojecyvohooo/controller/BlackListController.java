package com.example.firstmeeshoprojecyvohooo.controller;


import com.example.firstmeeshoprojecyvohooo.dto.BlackListRequestDto;
import com.example.firstmeeshoprojecyvohooo.service.BlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class BlackListController {

    @Autowired
    BlackListService blackListService;

    @PostMapping("/v1/blacklist")
    public ResponseEntity<Object> blacklistPhoneNumbers(@RequestBody BlackListRequestDto requestDto)
    {
        log.info("request to blaclist phone numbers : {}", requestDto)
        return blackListService.blacklistPhoneNumbers(requestDto);
    }
    @PutMapping("/v1/blacklist")
    public ResponseEntity<Object> whitelistPhoneNumbers(@RequestBody BlackListRequestDto requestDto)
    {
        return blackListService.whitelistPhoneNumbers(requestDto);
    }
    @GetMapping("/v1/blacklist")
    public ResponseEntity<Object> getBlackListedPhoneNumbers()
    {
        return blackListService.getBlackListedPhoneNumbers();
    }
}
