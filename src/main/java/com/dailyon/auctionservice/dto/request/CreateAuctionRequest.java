package com.dailyon.auctionservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuctionRequest {
    private String auctionName;
    private LocalDateTime startAt;
    private Integer startBidPrice;
    private Integer maximumWinner;

    private CreateProductRequest productRequest;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotNull(message = "브랜드를 입력해주세요")
        private Long brandId;

        @NotNull(message = "카테고리를 입력해주세요")
        private Long categoryId;

        @Min(value = 1, message = "가격은 1원 이상이어야 합니다")
        @Max(value = Integer.MAX_VALUE, message = "가격 범위를 초과했습니다")
        private Integer price;

        @NotBlank(message = "상품 이름을 입력해주세요")
        private String name;

        @NotBlank(message = "상품 코드를 입력해주세요")
        private String code;

        @NotBlank(message = "상품 유형을 입력해주세요")
        private String type;

        @NotBlank(message = "성별을 입력해주세요")
        private String gender;

        @NotBlank(message = "상품 이미지를 입력해주세요")
        private String image;

        @Size(max = 5, message = "상품 설명 이미지는 최대 5개까지 등록 가능합니다")
        private List<String> describeImages;

        @Valid
        @Size(min = 1, max = 1, message = "상품 개수를 등록해주세요")
        private List<ProductStockRequest> productStocks;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProductStockRequest {
            @NotNull(message = "치수값을 입력해주세요")
            private Long productSizeId;

            @Min(value = 1, message = "1개 이상이어야 합니다")
            @Max(value = Long.MAX_VALUE, message = "개수 범위를 초과했습니다")
            private Long quantity;
        }
    }
}
