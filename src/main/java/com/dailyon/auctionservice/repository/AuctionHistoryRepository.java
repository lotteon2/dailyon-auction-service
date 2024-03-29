package com.dailyon.auctionservice.repository;

import com.dailyon.auctionservice.document.AuctionHistory;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@EnableScan
@EnableScanCount
public interface AuctionHistoryRepository extends CrudRepository<AuctionHistory, String> {
  List<AuctionHistory> findByMemberId(String memberId);

  Optional<AuctionHistory> findByAuctionIdAndMemberId(String AuctionId, String memberId);
}
