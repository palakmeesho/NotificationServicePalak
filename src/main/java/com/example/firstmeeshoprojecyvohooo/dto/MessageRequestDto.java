package com.example.firstmeeshoprojecyvohooo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDto {
    @JsonProperty(value = "deliverychannel")
    String deliveryChannel;
    @JsonProperty(value = "channels")
    ChannelDto channelDto;
    @JsonProperty(value = "destination")
    List<DestinationDto> destinationDtoList;
}

