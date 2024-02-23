package com.example.firstmeeshoprojecyvohooo.controller;


import com.example.firstmeeshoprojecyvohooo.dto.BlackListRequestDto;
import com.example.firstmeeshoprojecyvohooo.dto.BlackListResponseDto;
import com.example.firstmeeshoprojecyvohooo.dto.GetBlackListResponseDto;
import com.example.firstmeeshoprojecyvohooo.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/blacklist")
public class BlackListController {

    @Autowired
    BlackListService blackListService;

    @PostMapping
    public ResponseEntity<BlackListResponseDto> blacklistPhoneNumbers (@RequestBody BlackListRequestDto requestDto)
    {
        return blackListService.blacklistPhoneNumbers(requestDto);
    }
    @PutMapping
    public ResponseEntity<BlackListResponseDto> whitelistPhoneNumbers(@RequestBody BlackListRequestDto requestDto)
    {
        return blackListService.whitelistPhoneNumbers(requestDto);
    }
    @GetMapping
    public ResponseEntity<GetBlackListResponseDto> getBlackListedPhoneNumbers()
    {
        return blackListService.getBlackListedPhoneNumbers();
    }
}
