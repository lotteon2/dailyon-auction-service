package com.dailyon.auctionservice.infra.kafka;

import com.dailyon.auctionservice.infra.kafka.dto.BiddingDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void createAuctionHistory(BiddingDTO biddingDTO) {
    log.info("createAuctionHistory -> memberId {}", biddingDTO.getMemberId());
    try {
      kafkaTemplate.send("success-bidding", objectMapper.writeValueAsString(biddingDTO));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
