package com.dailyon.auctionservice.infra.kafka;

import com.dailyon.auctionservice.infra.kafka.dto.BiddingDTO;
import com.dailyon.auctionservice.service.AuctionHistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dailyon.domain.order.kafka.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventListener {

  private final ObjectMapper objectMapper;
  private final AuctionHistoryService auctionHistoryService;

  @KafkaListener(topics = "cancel-bidding")
  public void cancel(String message, Acknowledgment ack) {
    BiddingDTO biddingDTO = null;
    try {
      biddingDTO = objectMapper.readValue(message, BiddingDTO.class);
      auctionHistoryService.delete(biddingDTO);
      ack.acknowledge();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @KafkaListener(topics = "approve-payment")
  public void updateStatus(String message, Acknowledgment ack) {
    OrderDTO orderDTO = null;
    try {
      orderDTO = objectMapper.readValue(message, OrderDTO.class);
      auctionHistoryService.update(orderDTO.getAuctionId(), String.valueOf(orderDTO.getMemberId()));
      ack.acknowledge();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
