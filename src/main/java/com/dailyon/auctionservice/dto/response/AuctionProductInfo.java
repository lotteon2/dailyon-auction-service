package com.dailyon.auctionservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionProductInfo {
  private String productName;
  private Integer stock;
  private Integer price;
  private String gender;
  private String imgUrl;
  private Long sizeId;
  private String sizeName;
  private boolean isWinner;
  private Long orderPrice;
}
