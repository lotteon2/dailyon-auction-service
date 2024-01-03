package com.dailyon.auctionservice.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.ContainerBaseTestSupport;
import com.dailyon.auctionservice.document.Auction;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

class AuctionRepositoryTests extends ContainerBaseTestSupport {
    @Autowired private AmazonDynamoDBAsync dynamoDB;
    @Autowired private DynamoDBMapper dynamoDBMapper;
    @Autowired private AuctionRepository auctionRepository;

    @BeforeEach
    void beforeEach() {
        CreateTableRequest createTableRequest = dynamoDBMapper
                .generateCreateTableRequest(Auction.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
    }

    @AfterEach
    void afterEach() {
        TableUtils.deleteTableIfExists(
                dynamoDB,
                dynamoDBMapper.generateDeleteTableRequest(Auction.class)
        );
    }

    @Test
    @DisplayName("경매 정보 생성 테스트")
    void createAuctionTest() {
        Auction toCreate = Auction.builder()
                .auctionProductId(1L)
                .auctionName("TEST")
                .startBidPrice(1000)
                .maximumWinner(5)
                .startAt(LocalDateTime.now())
                .build();

        Auction created = auctionRepository.save(toCreate);

        assertEquals(1L, created.getAuctionProductId());
        assertEquals("TEST", created.getAuctionName());
        assertEquals(1000, created.getStartBidPrice());
        assertEquals(5, created.getMaximumWinner());
        assertFalse(created.isEnded());
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    @DisplayName("경매 전체 목록 조회")
    void readAuctionPageTest() {
        for(int i=0; i<5; i++) {
            auctionRepository.save(Auction.builder()
                    .auctionProductId((long) i)
                    .auctionName("TEST_"+i)
                    .startBidPrice(1000)
                    .maximumWinner(5)
                    .startAt(LocalDateTime.now())
                    .build()
            );
        }

        List<Auction> auctions = (List<Auction>) auctionRepository.findAll();

        assertEquals(5, auctions.size());
    }
}
