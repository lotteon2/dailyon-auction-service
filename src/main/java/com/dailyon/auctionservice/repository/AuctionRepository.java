package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.Auction;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface AuctionRepository extends CrudRepository<Auction, String> {

}
