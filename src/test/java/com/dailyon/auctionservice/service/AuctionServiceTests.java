package com.dailyon.auctionservice.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.dailyon.auctionservice.ContainerBaseTestSupport;
import com.dailyon.auctionservice.document.Auction;
import com.dailyon.auctionservice.repository.AuctionRepository;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

public class AuctionServiceTests extends ContainerBaseTestSupport {
    @Autowired private AmazonDynamoDBAsync dynamoDB;
    @Autowired private DynamoDBMapper dynamoDBMapper;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private AuctionService auctionService;

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
    @DisplayName("경매 목록 생성 내림차순 기준 정렬 페이지네이션 조회")
    void paginationTest() {
        for(int i=0; i<10; i++) {
            auctionRepository.save(Auction.builder()
                    .auctionProductId((long) i)
                    .auctionName("TEST_"+i)
                    .startBidPrice(1000)
                    .maximumWinner(5)
                    .startAt(LocalDateTime.now())
                    .build()
            );
        }

        Page<Auction> auctions = auctionService.readAuctionsForAdmin(PageRequest.of(0, 5));
        assertEquals(10, auctions.getTotalElements());
        assertEquals(2, auctions.getTotalPages());
        assertEquals(5, auctions.getContent().size());
        IntStream.range(1, 5).forEach(i -> {
            Auction prev = auctions.getContent().get(i-1);
            Auction next = auctions.getContent().get(i);

            BDDAssertions
                    .then(prev.getCreatedAt().isAfter(next.getCreatedAt()))
                    .isTrue();
        });
    }
}
