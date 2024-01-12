package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.BidHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BidHistoryRepository extends CrudRepository<BidHistory, BidHistory.PrimaryKey> {

  List<BidHistory> findAllByAuctionIdAndRound(String auctionId, String round);
}
