package com.dailyon.auctionservice.chat.scheduler;

import com.dailyon.auctionservice.chat.response.ChatCommand;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.controller.ChatHandler;
import com.dailyon.auctionservice.service.AuctionService;
import com.dailyon.auctionservice.service.BidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Configuration
@EnableScheduling
public class ChatScheduler implements SchedulingConfigurer {

  private final ThreadPoolTaskScheduler taskScheduler;
  private final ChatHandler chatHandler;
  private final AuctionService auctionService;
  private final BidService bidService;
  private long countdown = 1 * 30 * 1000;
  private Disposable jobDisposable;

  public ChatScheduler(
      ChatHandler chatHandler, AuctionService auctionService, BidService bidService) {
    taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.initialize();
    this.chatHandler = chatHandler;
    this.auctionService = auctionService;
    this.bidService = bidService;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setTaskScheduler(taskScheduler);
  }

  public void startJob(String auctionId) {
    countdown = 3 * 60 * 1000;
    if (this.jobDisposable == null || this.jobDisposable.isDisposed()) {
      this.jobDisposable =
          Flux.interval(Duration.ofSeconds(1)).flatMap(it -> executeJob(auctionId)).subscribe();
    }
  }

  public void stopJob() {
    if (this.jobDisposable != null && !this.jobDisposable.isDisposed()) {
      this.jobDisposable.dispose();
    }
  }

  public Mono<Void> executeJob(String auctionId) {
    synchronized (this) { // 동기화 블록 시작
      updateCountdown();
      if (countdown <= 0) {
        countdown = 0;
        return sendCloseCommand(auctionId)
            .doFinally(signalType -> stopJob()); // sendCloseCommand()가 완전히 실행된 후에 stopJob()을 호출
      } else {
        return sendTimeSyncCommand();
      }
    } // 동기화 블록 끝
  }

  private void updateCountdown() {
    countdown -= 1000;
    if (countdown <= 0) {
      countdown = 0;
    }
  }

  private Mono<Void> sendCloseCommand(String auctionId) {
    ChatPayload<Object> payload = ChatPayload.of(ChatCommand.AUCTION_CLOSE, null);
    return auctionService
        .endAuction(auctionId)
        .flatMap(auction -> bidService.createAuctionHistories(auction))
        .then(chatHandler.broadCast(payload));
  }

  private Mono<Void> sendTimeSyncCommand() {
    ChatPayload<Object> payload = ChatPayload.of(ChatCommand.TIME_SYNC, countdown);
    return chatHandler.broadCast(payload).then();
  }
}
