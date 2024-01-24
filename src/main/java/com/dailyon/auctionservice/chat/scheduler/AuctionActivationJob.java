package com.dailyon.auctionservice.chat.scheduler;

import com.dailyon.auctionservice.facade.BidFacade;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuctionActivationJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    // JobDataMap에서 auctionId 가져오기
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    String auctionId = jobDataMap.getString("auctionId");
    // auctionId를 사용하여 Job 실행
    ApplicationContext applicationContext = null;
    try {
      applicationContext =
          (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    BidFacade bidFacade = applicationContext.getBean(BidFacade.class);
    bidFacade.start(auctionId).subscribe();
  }
}
