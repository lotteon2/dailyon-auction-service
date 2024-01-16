package com.dailyon.auctionservice.service;

import com.dailyon.auctionservice.document.AuctionHistory;
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
        if(auctionHistories.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        int totalSize = auctionHistories.size();

        List<AuctionHistory> sorted = auctionHistories.stream()
                .sorted(AuctionHistory::compareTo)
                .collect(Collectors.toList())
                .subList(startIdx, Math.min(endIdx, totalSize));

        return new PageImpl<>(sorted, pageable, totalSize);
    }
}
