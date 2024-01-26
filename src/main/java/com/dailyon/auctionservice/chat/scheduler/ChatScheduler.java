package com.dailyon.auctionservice.chat.scheduler;

import com.dailyon.auctionservice.chat.response.ChatCommand;
import com.dailyon.auctionservice.chat.response.ChatPayload;
import com.dailyon.auctionservice.controller.ChatHandler;
import com.dailyon.auctionservice.service.AuctionService;
import com.dailyon.auctionservice.service.BidService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@EnableScheduling
public class ChatScheduler implements SchedulingConfigurer {

  private final ThreadPoolTaskScheduler taskScheduler;
  private final ChatHandler chatHandler;
  private final AuctionService auctionService;
  private final BidService bidService;
  private SchedulerFactoryBean schedulerFactoryBean;
  private long countdown = 1 * 30 * 1000;
  private Disposable jobDisposable;

  public ChatScheduler(
      ChatHandler chatHandler,
      AuctionService auctionService,
      BidService bidService,
      SchedulerFactoryBean schedulerFactoryBean) {
    taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.initialize();
    this.chatHandler = chatHandler;
    this.auctionService = auctionService;
    this.bidService = bidService;
    this.schedulerFactoryBean = schedulerFactoryBean;
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setTaskScheduler(taskScheduler);
  }

  public void scheduleJob(LocalDateTime startTime, String auctionId)
      throws SchedulingException, SchedulerException {
    Date startAt = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());

    // JobDataMap에 auctionId 저장
    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("auctionId", auctionId);

    JobDetail jobDetail =
        JobBuilder.newJob(AuctionActivationJob.class)
            .withIdentity("auctionActivationJob")
            .usingJobData(jobDataMap) // JobDataMap 사용
            .build();

    Trigger trigger =
        TriggerBuilder.newTrigger()
            .withIdentity("auctionActivationTrigger")
            .startAt(startAt)
            .build();

    Scheduler scheduler = schedulerFactoryBean.getScheduler();
    scheduler.scheduleJob(jobDetail, trigger);

    scheduler.start();
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
