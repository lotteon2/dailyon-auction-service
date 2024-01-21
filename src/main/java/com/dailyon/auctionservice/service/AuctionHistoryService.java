package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.document.AuctionHistory;
import com.dailyon.auctionservice.infra.kafka.dto.BiddingDTO;
import com.dailyon.auctionservice.repository.AuctionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionHistoryService {
  private final AuctionHistoryRepository auctionHistoryRepository;

  public Page<AuctionHistory> readAuctionHistories(String memberId, Pageable pageable) {
    int currentPage = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    int startIdx = currentPage * pageSize;
    int endIdx = startIdx + pageSize;

    List<AuctionHistory> auctionHistories = auctionHistoryRepository.findByMemberId(memberId);
    if (auctionHistories.isEmpty()) {
      return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }
    int totalSize = auctionHistories.size();

    List<AuctionHistory> sorted =
        auctionHistories.stream()
            .sorted(AuctionHistory::compareTo)
            .collect(Collectors.toList())
            .subList(startIdx, Math.min(endIdx, totalSize));

    return new PageImpl<>(sorted, pageable, totalSize);
  }

  public AuctionHistory getAuctionHistory(String memberId, String auctionId) {
    AuctionHistory auctionHistory =
        auctionHistoryRepository
            .findByAuctionIdAndMemberId(auctionId, memberId)
            .orElseThrow(() -> new RuntimeException("해당 경매 내역 정보가 존재하지 않습니다."));
    return auctionHistory;
  }

  public void delete(BiddingDTO biddingDTO) {
    AuctionHistory auctionHistory =
        auctionHistoryRepository
            .findByAuctionIdAndMemberId(
                biddingDTO.getAuctionId(), String.valueOf(biddingDTO.getMemberId()))
            .get();
    auctionHistoryRepository.delete(auctionHistory);
  }

  public void update(String auctionId, String memberId) {
    AuctionHistory auctionHistory =
        auctionHistoryRepository.findByAuctionIdAndMemberId(auctionId, memberId).get();
    auctionHistory.setPaid(true);
    auctionHistoryRepository.save(auctionHistory);
  }
}
