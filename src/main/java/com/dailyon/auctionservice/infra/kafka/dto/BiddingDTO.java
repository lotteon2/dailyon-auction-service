package com.dailyon.auctionservice.infra.kafka.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiddingDTO {
  private String auctionId;
  private Long memberId;
  private Long usePoints;
}
