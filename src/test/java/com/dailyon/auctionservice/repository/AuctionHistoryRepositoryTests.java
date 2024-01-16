package com.dailyon.auctionservice.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.ContainerBaseTestSupport;
import com.dailyon.auctionservice.document.AuctionHistory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AuctionHistoryRepositoryTests extends ContainerBaseTestSupport {
    @Autowired
    private AmazonDynamoDBAsync dynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AuctionHistoryRepository auctionHistoryRepository;

    @BeforeEach
    void beforeEach() {
        CreateTableRequest createTableRequest = dynamoDBMapper
                .generateCreateTableRequest(AuctionHistory.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest
                .getGlobalSecondaryIndexes()
                .forEach(idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
                );

        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
    }

    @AfterEach
    void afterEach() {
        TableUtils.deleteTableIfExists(
                dynamoDB,
                dynamoDBMapper.generateDeleteTableRequest(AuctionHistory.class)
        );
    }

    @Test
    @DisplayName("경매 기록 생성 테스트")
    void auctionHistoryCreateTest() {
        AuctionHistory history = AuctionHistory.create(
                "1",
                "TEST",
                1L,
                "img.png",
                "name",
                true,
                10000L,
                1000L,
                1000L
        );

        AuctionHistory savedHistory = auctionHistoryRepository.save(history);
    }

    @Test
    @DisplayName("경매 내역 리스트 조회")
    void findAuctionHistoryListTests() {
        List<AuctionHistory> histories = List.of(
                AuctionHistory.create(
                        "1", "TEST", 1L, "img.png", "name",
                        true, 10000L, 1000L, 1000L
                ),
                AuctionHistory.create(
                        "1", "TEST2", 2L, "img.png", "name",
                        false, 0L, 100L, 10000L
                ),
                AuctionHistory.create(
                        "1", "TEST3", 3L, "img.png", "name",
                        false, 0L, 1000L, 10000L
                ),
                AuctionHistory.create(
                        "2", "TEST", 1L, "img.png", "name",
                        true, 10000L, 1000L, 1000L
                ),
                AuctionHistory.create(
                        "2", "TEST2", 2L, "img.png", "name",
                        false, 0L, 100L, 10000L
                )
        );
        auctionHistoryRepository.saveAll(histories);

        List<AuctionHistory> byMemberId = auctionHistoryRepository.findByMemberId(String.valueOf(1L));

        Assertions.assertEquals(3, byMemberId.size());
    }
}
