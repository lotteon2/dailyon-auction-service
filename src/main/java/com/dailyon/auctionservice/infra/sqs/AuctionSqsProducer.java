package com.dailyon.auctionservice.infra.sqs;


import com.dailyon.auctionservice.infra.sqs.dto.SQSNotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionSqsProducer {

    private final QueueMessagingTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    public static final String AUCTION_END_NOTIFICATION_QUEUE = "auction-end-notification-queue";

    public void produce(String queueName, SQSNotificationDto sqsNotificationDto) {
        // 알림 생성 중 에러 때문에 전체 로직이 취소되는것을 막음.
        try {
            String jsonMessage = objectMapper.writeValueAsString(sqsNotificationDto);
            Message<String> message = MessageBuilder.withPayload(jsonMessage).build();
            sqsTemplate.send(queueName, message);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
