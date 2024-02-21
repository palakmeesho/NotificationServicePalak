package com.example.firstmeeshoprojecyvohooo.controller;


import com.example.firstmeeshoprojecyvohooo.dto.BlackListRequestDto;
import com.example.firstmeeshoprojecyvohooo.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/blacklist")
@Validated
public class BlackListController {

    @Autowired
    BlackListService blackListService;

    @PostMapping
    public ResponseEntity<Object> blacklistPhoneNumbers( @Valid @RequestBody BlackListRequestDto requestDto)
    {
        return blackListService.blacklistPhoneNumbers(requestDto);
    }
    @PutMapping
    public ResponseEntity<Object> whitelistPhoneNumbers(@Valid @RequestBody BlackListRequestDto requestDto)
    {
        return blackListService.whitelistPhoneNumbers(requestDto);
    }
    @GetMapping
    public ResponseEntity<Object> getBlackListedPhoneNumbers()
    {
        return blackListService.getBlackListedPhoneNumbers();
    }
}
