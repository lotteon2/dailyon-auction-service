package com.dailyon.auctionservice.infra.sqs.dto;

import com.dailyon.auctionservice.infra.sqs.dto.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawNotificationData {
    private String message;
    private Map<String, String> parameters;
    private NotificationType notificationType; // 알림 유형


    public static RawNotificationData forAuctionEnd(String auctionId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("auctionId", auctionId);

        return new RawNotificationData(
                null,
                parameters,
                NotificationType.AUCTION_END
        );
    }
}
