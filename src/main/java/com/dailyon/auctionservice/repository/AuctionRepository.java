package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.Auction;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

import java.util.List;
import java.util.Optional;

/* Caused by: com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException: Auction[created_at]; no HASH key for GSI auction_sort_idx
 https://stackoverflow.com/questions/68067091/sorting-not-supported-for-scan-expressions-and-no-hash-key-for-gsi-for-dynamodbp
 */
@EnableScan
@EnableScanCount
public interface AuctionRepository extends DynamoDBCrudRepository<Auction, String> {
    List<Auction> findAll();
    List<Auction> findAuctionsByStartedAndEnded(boolean started, boolean ended);
}
